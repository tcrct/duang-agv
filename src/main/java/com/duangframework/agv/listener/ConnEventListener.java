package com.duangframework.agv.listener;

import com.duangframework.agv.adapter.CommAdapter;
import com.duangframework.agv.core.Telegram;
import com.duangframework.agv.kit.ToolsKit;
import com.duangframework.agv.model.EmptyTelegram;
import org.opentcs.contrib.tcp.netty.ConnectionEventListener;
import org.opentcs.data.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

/**
 *
 * @author Laotang
 */
public class ConnEventListener implements ConnectionEventListener<String> {

    private final static Logger logger = LoggerFactory.getLogger(ConnEventListener.class);

    private CommAdapter commAdapter;

    public ConnEventListener(CommAdapter adapter) {
        this.commAdapter = adapter;
    }

    @Override
    public void onIncomingTelegram(String telegram) {
        requireNonNull(telegram, "telegram");
        // 将车辆设置为不空闲状态
        commAdapter.getProcessModel().setVehicleIdle(false);
        // 是否与请求队列中的第一个匹配

        Telegram responseTelegram =commAdapter.getTemplate().builderTelegram(telegram);
        if(!commAdapter.getTelegramMatcher().tryMatchWithCurrentRequestTelegram(responseTelegram)) {
            // 如果不匹配，则忽略该响应或关闭连接
            return;
        }
        logger.warn("控制中心接收到{}的回复: {}",commAdapter.getName(), telegram);
        /**检查并更新车辆状态，位置点*/
        commAdapter.checkForVehiclePositionUpdate(responseTelegram);
        /**在执行上面更新位置的方法后再检查是否有下一条请求需要发送*/
        commAdapter.getTelegramMatcher().checkForSendingNextRequest();
    }

    /**
     *连接成功时调用
     */
    @Override
    public void onConnect() {
        if(!commAdapter.isEnabled()){
            return;
        }
        logger.warn("{} 连接成功", commAdapter.getName());
        // 告知通信适配器当前已连接
        commAdapter.getProcessModel().setCommAdapterConnected(true);
    }

    /**
     * 连接失败时调用
     */
    @Override
    public void onFailedConnectionAttempt() {
        if(!commAdapter.isEnabled()) {
            return;
        }
        // 告知通讯适配器当前连接失败
        commAdapter.getProcessModel().setCommAdapterConnected(false);
        // 如果是开启了且设置了自动重连接
        reconnecting();
    }

    /**
     * 断开连接时调用
     */
    @Override
    public void onDisconnect() {
        logger.warn("{} 断开连接", commAdapter.getName());
        // 告知通讯适配器当前连接断开
        commAdapter.getProcessModel().setCommAdapterConnected(false);
        // 将车辆设置为空闲
        commAdapter.getProcessModel().setVehicleIdle(true);
        // 再将车辆的状态设置为未知
        commAdapter.getProcessModel().setVehicleState(Vehicle.State.UNKNOWN);
        // 如果是开启了且设置了自动重连接
        reconnecting();
    }

    /**自动重连*/
    private void reconnecting(){
        if(commAdapter.isEnabled() &&
                commAdapter.getProcessModel().isReconnectingOnConnectionLoss()) {
            commAdapter.getVehicleChannelManager().scheduleConnect(commAdapter.getProcessModel().getVehicleHost(),
                    commAdapter.getProcessModel().getVehiclePort(),
                    commAdapter.getProcessModel().getReconnectDelay());
        }
    }

    /**
     * 车辆空闲
     */
    @Override
    public void onIdle() {
//        logger.info("{} 空闲", getName());
        commAdapter.getProcessModel().setVehicleIdle(true);
        if(commAdapter.isEnabled() && commAdapter.getProcessModel().isDisconnectingOnVehicleIdle()){
            logger.info("{} 车辆空闲，断开连接", commAdapter.getName());
            disconnectVehicle();
        }
    }

    /**
     * 断开车辆连接
     */
    private void disconnectVehicle() {
        if(ToolsKit.isEmpty(commAdapter.getVehicleChannelManager())) {
            logger.warn("断开连接车辆 {} 时失败: vehicleChannelManager not present.", commAdapter.getName());
            return;
        }
        commAdapter.getVehicleChannelManager().disconnect();
    }
}
