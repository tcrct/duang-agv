package com.duangframework.agv.adapter;

import com.duangframework.agv.core.ITelegramMapper;
import com.duangframework.agv.core.ITelegramSender;
import com.duangframework.agv.core.TelegramMatcher;
import com.duangframework.agv.enums.LoadAction;
import com.duangframework.agv.listener.ConnEventListener;
import com.duangframework.agv.model.AgvProcessModel;
import com.google.inject.assistedinject.Assisted;
import io.netty.channel.ChannelHandler;
import org.opentcs.contrib.tcp.netty.TcpClientChannelManager;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.drivers.vehicle.BasicVehicleCommAdapter;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapterPanel;
import org.opentcs.util.ExplainedBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.swing.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * 通讯适配器
 *
 * @author Laotang
 */
public class CommAdapter
        extends BasicVehicleCommAdapter
        implements ITelegramSender {

    private static final Logger logger = LoggerFactory.getLogger(CommAdapter.class);
    // 组件工厂
    private AdapterComponentsFactory componentsFactory;
    // 车辆管理缓存池
    private TcpClientChannelManager<String, String> vehicleChannelManager;
     // 请求响应电报匹配器
    private TelegramMatcher telegramMatcher;
    // 电报构造器
    private ITelegramMapper telegramMapper;

    private final Map<MovementCommand, String> orderIds = new ConcurrentHashMap<>();

    private final Map<String, String> lastFinishedPositionIds =new ConcurrentHashMap<>();


    /***
     * 构造函数
     * @param vehicle   车辆
     * @param telegramMapper 请求转换对象
     * @param componentsFactory 组件工厂
     */
    @Inject
    public CommAdapter(@Assisted Vehicle vehicle, ITelegramMapper telegramMapper, AdapterComponentsFactory componentsFactory) {
        super(new AgvProcessModel(vehicle), 3, 2, LoadAction.CHARGE);
        this.telegramMapper = requireNonNull(telegramMapper,"telegramMapper");
        this.componentsFactory = requireNonNull(componentsFactory, "componentsFactory");
    }

    /***
     *  初始化
     */
    @Override
    public void initialize() {
        super.initialize();
        this.telegramMatcher = componentsFactory.createTelegramMatcher(this);
//        this.stateRequesterTask = componentsFactory.createStateRequesterTask(e -> {
//            logger.info("Adding new state requests to the queue.");
//            telegramMatcher.enqueueRequest(new MakerwitRequest.Builder().build());
//        });
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
        vehicleChannelManager = new TcpClientChannelManager<>(new ConnEventListener(this),
                this::getChannelHandlers,
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
    public final AgvProcessModel getProcessModel() {
        return (AgvProcessModel) super.getProcessModel();
    }

    public TcpClientChannelManager<String, String> getVehicleChannelManager() {
        return vehicleChannelManager;
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
            MakerwitRequest telegram =  telegramMapper.builderTelegram(getProcessModel(), cmd);
            String card =UplinkParam.string2UplinkParam(telegram.getParams()).getCard();
            // 将移动命令放入缓存池
            orderIds.put(cmd, card);
            // 将车辆与路径最后一个点的位置绑定起来
            lastFinishedPositionIds.put(telegram.getDeviceId(), cmd.getFinalDestination().getName());
            // 把请求加入队列。请求发送规则是FIFO。这确保我们总是等待响应，直到发送新请求。
            telegramMatcher.enqueueRequest(telegram);
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
        logger.warn("######################clearCommandQueue");
        super.clearCommandQueue();
        orderIds.clear();
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

        final JComboBox<Point> pointComboBox = new JComboBox<>();
        for(Iterator<VehicleCommAdapterPanel> iterator = getAdapterPanels().iterator(); iterator.hasNext();) {
            VehicleCommAdapterPanel panel = iterator.next();

            System.out.println(panel.getName());


//            pointComboBox.addItem(command.getStep().getSourcePoint());
//            System.out.println(command.getStep().getSourcePoint().getName());
//            pointNameList.add(command.getStep().getSourcePoint().getName());
        }
//        pointComboBox.addItemListener((ItemEvent e) ->{
//            Point newPoint = (Point)e.getItem();
//            getProcessModel().setVehiclePosition(newPoint.getName());
//        });
        // TODO 可以改为下拉选择的方式 ，待完成，目前先将起点位置设置为Point-0001
        getProcessModel().setVehiclePosition("Point-0001");
        getProcessModel().setVehicleState(Vehicle.State.IDLE);
        getProcessModel().setVehicleIdle(true);

        logger.warn("连接车辆 {} 成功:  host:{} port:{}.", getName(), host, port);
    }

    /**
     * 判断车辆连接是否断开
     * @return  链接状态返回true
     */
    @Override
    protected boolean isVehicleConnected() {
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
     * 返回负责从字节流中写入和读取的通道处理程序
     * @return The channel handlers responsible for writing and reading from the byte stream
     */
    private List<ChannelHandler> getChannelHandlers() {
        return Arrays.asList(
                new VehicleTelegramDecoder(this),
                new VehicleTelegramEncoder());
    }

    /******************ConnectionEventListener 回调方法 ************************/
    /**
     *
     * @param response 回复结果
     */
    @Override
    public void onIncomingTelegram(String response) {
        requireNonNull(response, "response");
        // 将车辆设置为不空闲状态
        getProcessModel().setVehicleIdle(false);
        // 是否与请求队列中的第一个匹配
        MakerwitResponse makerwitResponse = new MakerwitResponse(response);
//        MakerwitRequest makerwitRequest = makerwitResponse.toRequestObject();
        if(!telegramMatcher.tryMatchWithCurrentRequestTelegram(makerwitResponse)) {
            // 如果不匹配，则忽略该响应或关闭连接
            return;
        }
        logger.warn("控制中心接收到{}的回复: {}", getName(), response);
        /**检查并更新车辆状态，位置点*/
        checkForVehiclePositionUpdate(makerwitResponse);
        /**在执行上面更新位置的方法后再检查是否有下一条请求需要发送*/
        telegramMatcher.checkForSendingNextRequest();
    }



    /**
     * 检查车辆位置并更新
     * @param makerwitResponse
     */
    private void checkForVehiclePositionUpdate(MakerwitResponse makerwitResponse) {

        // 将报告的位置ID映射到点名称
        String currentPosition = makerwitResponse.getPositionId();
        logger.info("{}: Vehicle is now at point {}", getName(), currentPosition);
        // 更新位置，但前提是它不能是空
        if (ToolsKit.isNotEmpty(currentPosition)) {
            getProcessModel().setVehiclePosition(currentPosition);
        }


        MovementCommand cmd = getSentQueue().poll();
        orderIds.remove(cmd);
        getProcessModel().commandExecuted(cmd);
        /*
        Iterator<MovementCommand> cmdIter = getSentQueue().iterator();
        boolean finishedAll = false;
        while (!finishedAll && cmdIter.hasNext()) {
            MovementCommand cmd = cmdIter.next();
            cmdIter.remove();
            String orderId = orderIds.remove(cmd);
//            String orderId1 =cmd.getStep().getSourcePoint().getName();
//            String orderId2 =cmd.getStep().getDestinationPoint().getName();
            String orderId3 =cmd.getFinalDestination().getName();
//            System.out.println(orderId1+"                         "+orderId2+"                         "+orderId3);
            String lastPoint = lastFinishedPositionIds.get(makerwitResponse.getDeviceId());
//            System.out.println(makerwitResponse.getDeviceId() + "#############lastPoint: " + lastPoint);
            if (orderId3.equals(lastPoint)) {
                finishedAll = true;
            }

//            logger.info("{}: Reporting command with order ID {} as executed: {}", getName(), orderId, cmd);

            getProcessModel().commandExecuted(cmd);

        }
         */
        /*
        MakerwitResponse previousState = getProcessModel().getPreviousState();
        MakerwitResponse currentState = getProcessModel().getCurrentState();

        if(ToolsKit.isEmpty(previousState) || ToolsKit.isEmpty(currentState)){
            return;
        }

        getProcessModel().setPreviousState(getProcessModel().getCurrentState());
        getProcessModel().setCurrentState(makerwitResponse);

        previousState = getProcessModel().getPreviousState();
        currentState = getProcessModel().getCurrentState();

        logger.warn(previousState.getPositionId()+"                           currentState.getPositionId(): "+currentState.getPositionId());
        //如果位置没发生变化则退出
        if(previousState.getPositionId().equals(currentState.getPositionId())) {
           return;
        }
        // 将报告的位置ID映射到点名称
        String currentPosition = String.valueOf(currentState.getPositionId());
        logger.info("{}: Vehicle is now at point {}", getName(), currentPosition);
        // 更新位置，但前提是它不能是空
        if (ToolsKit.isNotEmpty(currentState.getPositionId())) {
            getProcessModel().setVehiclePosition(currentPosition);
        }

        checkForVehicleStateUpdate(previousState, currentState);
//        checkOrderFinished(previousState, currentState);

         */
    }

    /**
     * 检查车辆状态并更新
     * @param previousState
     * @param currentState
     */
    private void checkForVehicleStateUpdate(MakerwitResponse previousState, MakerwitResponse currentState) {
        if (previousState.getOperatingState() == currentState.getOperatingState()) {
            return;
        }
        getProcessModel().setVehicleState(ToolsKit.translateVehicleState(currentState.getOperatingState()));
    }

    /*
    private void checkOrderFinished(MakerwitResponse previousState, MakerwitResponse currentState) {
        if (currentState.getLastFinishedOrderId() == 0) {
            return;
        }
        // 如果上次完成的订单ID没有更改，请不要费心
        if (previousState.getLastFinishedOrderId() == currentState.getLastFinishedOrderId()) {
            return;
        }
        // 检查新的完成订单id是否在已发送订单的队列中
        // 如果是，则将该订单之前的所有订单报告为已完成
        if (!orderIds.containsValue(currentState.getLastFinishedOrderId())) {
            logger.debug("{}: Ignored finished order ID {} (reported by vehicle, not found in sent queue).",
                    getName(),
                    currentState.getLastFinishedOrderId());
            return;
        }

        Iterator<MovementCommand> cmdIter = getSentQueue().iterator();
        boolean finishedAll = false;
        while (!finishedAll && cmdIter.hasNext()) {
            MovementCommand cmd = cmdIter.next();
            cmdIter.remove();
            int orderId = orderIds.remove(cmd);
            if (orderId == currentState.getLastFinishedOrderId()) {
                finishedAll = true;
            }

            logger.debug("{}: Reporting command with order ID {} as executed: {}", getName(), orderId, cmd);
            getProcessModel().commandExecuted(cmd);
        }
    }
     */



    @Override
    public void sendTelegram(Request telegram) {
        requireNonNull(telegram, "telegram");
        if (!isVehicleConnected()) {
            logger.debug("{}: Not connected - not sending request '{}'",
                    getName(),
                    telegram);
            return;
        }

        logger.info("{}: Sending request '{}'", getName(), telegram.toRequestObject());
        vehicleChannelManager.send((String)telegram.toRequestObject());

        // 如果电报是命令，记住它
//        if (telegram instanceof OrderRequest) {
//            getProcessModel().setLastOrderSent((OrderRequest) telegram);
//        }
//
//        if (getProcessModel().isPeriodicStateRequestEnabled()) {
//            stateRequesterTask.restart();
//        }
    }
}
