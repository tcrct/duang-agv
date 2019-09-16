package com.duangframework.agv.listener;

import com.duangframework.agv.adapter.CommAdapter;
import com.duangframework.agv.kit.ToolsKit;
import org.opentcs.contrib.tcp.netty.ConnectionEventListener;
import org.opentcs.data.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Laotang
 */
public class ConnEventListener implements ConnectionEventListener<String> {

    private final static Logger logger = LoggerFactory.getLogger(ConnEventListener.class);

    private CommAdapter agvCommAdapter;

    public ConnEventListener(CommAdapter adapter) {
        this.agvCommAdapter = adapter;
    }

    @Override
    public void onIncomingTelegram(String telegram) {

    }

    /**
     *连接成功时调用
     */
    @Override
    public void onConnect() {
        if(!agvCommAdapter.isEnabled()){
            return;
        }
        logger.warn("{} 连接成功", agvCommAdapter.getName());
        // 告知通信适配器当前已连接
        agvCommAdapter.getProcessModel().setCommAdapterConnected(true);
    }

    /**
     * 连接失败时调用
     */
    @Override
    public void onFailedConnectionAttempt() {
        if(!agvCommAdapter.isEnabled()) {
            return;
        }
        // 告知通讯适配器当前连接失败
        agvCommAdapter.getProcessModel().setCommAdapterConnected(false);
        // 如果是开启了且设置了自动重连接
        reconnecting();
    }

    /**
     * 断开连接时调用
     */
    @Override
    public void onDisconnect() {
        logger.warn("{} 断开连接", agvCommAdapter.getName());
        // 告知通讯适配器当前连接断开
        agvCommAdapter.getProcessModel().setCommAdapterConnected(false);
        // 将车辆设置为空闲
        agvCommAdapter.getProcessModel().setVehicleIdle(true);
        // 再将车辆的状态设置为未知
        agvCommAdapter.getProcessModel().setVehicleState(Vehicle.State.UNKNOWN);
        // 如果是开启了且设置了自动重连接
        reconnecting();
    }

    /**自动重连*/
    private void reconnecting(){
        if(agvCommAdapter.isEnabled() &&
                agvCommAdapter.getProcessModel().isReconnectingOnConnectionLoss()) {
            agvCommAdapter.getVehicleChannelManager().scheduleConnect(agvCommAdapter.getProcessModel().getVehicleHost(),
                    agvCommAdapter.getProcessModel().getVehiclePort(),
                    agvCommAdapter.getProcessModel().getReconnectDelay());
        }
    }

    /**
     * 车辆空闲
     */
    @Override
    public void onIdle() {
//        logger.info("{} 空闲", getName());
        agvCommAdapter.getProcessModel().setVehicleIdle(true);
        if(agvCommAdapter.isEnabled() && agvCommAdapter.getProcessModel().isDisconnectingOnVehicleIdle()){
            logger.info("{} 车辆空闲，断开连接", agvCommAdapter.getName());
            disconnectVehicle();
        }
    }

    /**
     * 断开车辆连接
     */
    private void disconnectVehicle() {
        if(ToolsKit.isEmpty(agvCommAdapter.getVehicleChannelManager())) {
            logger.warn("断开连接车辆 {} 时失败: vehicleChannelManager not present.", agvCommAdapter.getName());
            return;
        }
        agvCommAdapter.getVehicleChannelManager().disconnect();
    }
}
