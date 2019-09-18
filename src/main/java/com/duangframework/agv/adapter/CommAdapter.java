package com.duangframework.agv.adapter;

import com.duangframework.agv.core.*;
import com.duangframework.agv.enums.LoadAction;
import com.duangframework.agv.enums.LoadState;
import com.duangframework.agv.kit.ObjectKit;
import com.duangframework.agv.kit.PropKit;
import com.duangframework.agv.kit.ToolsKit;
import com.duangframework.agv.model.ProcessModel;
import com.google.inject.assistedinject.Assisted;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.contrib.tcp.netty.TcpClientChannelManager;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.drivers.vehicle.BasicVehicleCommAdapter;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.util.Comparators;
import org.opentcs.util.ExplainedBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * 通讯适配器
 *
 * @author Laotang
 */
public class CommAdapter extends BasicVehicleCommAdapter {

    private static final Logger logger = LoggerFactory.getLogger(CommAdapter.class);
    // 组件工厂
    private ComponentsFactory componentsFactory;
    // 车辆管理缓存池
    private TcpClientChannelManager<String, String> vehicleChannelManager;
     // 请求响应电报匹配器
    private TelegramMatcher telegramMatcher;
    // 模板
    private AgreementTemplate template;

    private final Map<MovementCommand, String> cmdIds = new ConcurrentHashMap<>();

    private final Map<String, String> lastFinishedPositionIds =new ConcurrentHashMap<>();

    private TCSObjectService objectService;


    /***
     * 构造函数
     * @param vehicle   车辆
     * @param componentsFactory 组件工厂
     */
    @Inject
    public CommAdapter(@Assisted Vehicle vehicle, TCSObjectService objectService, ComponentsFactory componentsFactory) {
        super(new ProcessModel(vehicle), 3, 2, LoadAction.CHARGE);
        String agreementTemplate = PropKit.get("agreement.template", "");
        requireNonNull(agreementTemplate,"请在duang.properties里设置agreement.template值");
        this.template = ObjectKit.newInstance(agreementTemplate);
        this.componentsFactory = requireNonNull(componentsFactory, "componentsFactory");
        this.objectService = objectService;
        template.setComponent(this);
    }

    /***
     *  初始化
     */
    @Override
    public void initialize() {
        super.initialize();
        this.telegramMatcher = componentsFactory.createTelegramMatcher(template);
    }

    /**
     * 终止
     */
    @Override
    public void terminate() {
        super.terminate();
    }

    /**
     * 内核控制中心列表enable列勾选复选框后触发
     */
    @Override
    public synchronized void enable() {
        // 如果启用了则直接退出
        if (isEnabled()) {
            return;
        }

        // 创建负责与车辆连接的渠道管理器,基于netty
        vehicleChannelManager = new TcpClientChannelManager<String, String>(template.getConnEventListener(),
                template.getChannelHandlers(),
                getProcessModel().getVehicleIdleTimeout(),
                getProcessModel().isLoggingEnabled());

        // 初始化车辆渠道管理器
        vehicleChannelManager.initialize();
        // 调用父类开启
        super.enable();
    }

    /**
     * 车辆进程参数模型
     * @return
     */
    @Override
    public final ProcessModel getProcessModel() {
        return (ProcessModel) super.getProcessModel();
    }

    public TcpClientChannelManager<String, String> getVehicleChannelManager() {
        return vehicleChannelManager;
    }

    public TelegramMatcher getTelegramMatcher() {
        return telegramMatcher;
    }

    public AgreementTemplate getTemplate() {
        return template;
    }

    /**
     * 控制中心完成运输订单设置后，在车辆每移动一个点时，会执行以下方法
     * @param cmd 车辆移动相关参数
     * @throws IllegalArgumentException
     */
    @Override
    public void sendCommand(MovementCommand cmd) throws IllegalArgumentException {
        requireNonNull(cmd, "cmd");
        logger.info("sendCommand {}", cmd);
        try {
            // 将移动的参数转换为请求参数，这里要根据协议规则生成对应的请求对象
            Telegram telegram =  template.builderTelegram(getProcessModel(), cmd);
            // 将移动命令放入缓存池
            cmdIds.put(cmd, telegram.getId());
            // 把请求加入队列。请求发送规则是FIFO。这确保我们总是等待响应，直到发送新请求。
            telegramMatcher.enqueueRequestTelegram(telegram);
            logger.info("{}: 将订单报文提交到消息队列完成", getName());
        } catch (Exception e) {
            logger.error("{}: 将订单报文提交到消息队列失败 {}", getName(), cmd, e);
        }
    }

    /**
     * 清理命令队列
     */
    @Override
    public synchronized void clearCommandQueue() {
        super.clearCommandQueue();
        cmdIds.clear();
    }

    /***
     * 链接车辆
     */
    @Override
    protected void connectVehicle() {
        if(ToolsKit.isEmpty(vehicleChannelManager)) {
            logger.warn("连接车辆 {} 时失败: vehicleChannelManager not present.", getName());
            return;
        }
        /**进行bind操作*/
        String host = getProcessModel().getVehicleHost();
        int port =getProcessModel().getVehiclePort();
        vehicleChannelManager.connect(host, port);
        List<String> pointNameList = new ArrayList<>();

        Set<Point> pointSet = objectService.fetchObjects(Point.class);
        List<Point> pointList = new ArrayList<>(pointSet);
        Collections.sort(pointList, Comparators.objectsByName());
        pointList.add(0, null);

        for(Point point : pointList) {
            System.out.println(point.getName());
        }



//        getProcessModel().setVehiclePosition(((Point) item).getName());
        // TODO 可以改为下拉选择的方式 ，待完成，目前先将起点位置设置为Point-0001
        getProcessModel().setVehiclePosition("Point-0001");
        getProcessModel().setVehicleState(Vehicle.State.IDLE);
        getProcessModel().setVehicleIdle(true);

        logger.warn("连接车辆 {} 成功:  host:{} port:{}.", getName(), host, port);
    }

    /**
     * 断开连接
     */
    @Override
    protected void disconnectVehicle() {
        if(ToolsKit.isEmpty(vehicleChannelManager)) {
            logger.warn("断开连接车辆 {} 时失败: vehicleChannelManager not present.", getName());
            return;
        }
        vehicleChannelManager.disconnect();
    }

    /**
     * 判断车辆连接是否断开
     * @return  链接状态返回true
     */
    @Override
    public boolean isVehicleConnected() {
        return ToolsKit.isNotEmpty(vehicleChannelManager)  &&  vehicleChannelManager.isConnected();
    }


    /**
     * 创建运输订单后执行该方法
     * @param operations    操作标识字符串  例如 NOP
     * @return
     */
    @Nonnull
    @Override
    public ExplainedBoolean canProcess(@Nonnull List<String> operations) {
        requireNonNull(operations, "operations");
        logger.info("canProcess: {}", operations);
        boolean canProcess = true;
        String reason = "";

        if(!isEnabled()) {
            canProcess = false;
            reason= "通讯适配器没有开启";
        }

        if(canProcess && !isVehicleConnected()) {
            canProcess = false;
            reason = "车辆可能没有连接";
        }

        if(canProcess &&
                LoadState.UNKNOWN.name().equalsIgnoreCase(getProcessModel().getVehicleState().name())) {
            canProcess = false;
            reason = "车辆负载状态未知";
        }

        boolean loaded = LoadState.FULL.name().equalsIgnoreCase(getProcessModel().getVehicleState().name());
        final Iterator<String> iterator = operations.iterator();
        while (canProcess && iterator.hasNext()) {
            final String nextOp = iterator.next();
            if(loaded) {
                if (LoadAction.LOAD.equalsIgnoreCase(nextOp)) {
                    canProcess = false;
                    reason = "不能重复装载";
                } else if (LoadAction.UNLOAD.equalsIgnoreCase(nextOp)) {
                    loaded = false;
                } else if (DriveOrder.Destination.OP_PARK.equalsIgnoreCase(nextOp)) {
                    canProcess = false;
                    reason = "车辆在装载状态下不应该停车";
                } else if (LoadAction.CHARGE.equalsIgnoreCase(nextOp)) {
                    canProcess = false;
                    reason = "车辆在装载状态下不应该充电";
                }
            } else if (LoadAction.LOAD.equalsIgnoreCase(nextOp)){
                    loaded = true;
            } else if(LoadAction.UNLOAD.equalsIgnoreCase(nextOp)){
                    canProcess = false;
                    reason = "未加载时无法卸载";
            }
        }
        return new ExplainedBoolean(canProcess, reason);
    }

    @Override
    public void processMessage(@Nullable Object o) {
        logger.info("processMessage: {}", o);
    }

    /**
     * 检查车辆位置并更新
     * @param telegram
     */
    public void checkForVehiclePositionUpdate(Telegram telegram) {

        // 将报告的位置ID映射到点名称
        String currentPosition = telegram.getPositionId();
        logger.info("{}: Vehicle is now at point {}", getName(), currentPosition);
        // 更新位置，但前提是它不能是空
        if (ToolsKit.isNotEmpty(currentPosition)) {
            getProcessModel().setVehiclePosition(currentPosition);
        }
        // Update GUI.
        synchronized (CommAdapter.this) {
            MovementCommand cmd = getSentQueue().poll();
            cmdIds.remove(cmd);
            getProcessModel().commandExecuted(cmd);
            // 唤醒处于等待状态的线程
            CommAdapter.this.notify();
        }
    }

}
