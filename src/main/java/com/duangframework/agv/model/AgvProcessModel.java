package com.duangframework.agv.model;

import com.duangframework.agv.kit.ToolsKit;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.VehicleProcessModel;

import javax.annotation.Nonnull;

import static com.duangframework.agv.enums.Attribute.*;
import static java.util.Objects.requireNonNull;
import static org.opentcs.util.Assertions.checkInRange;

/**
 * 车辆进程参数模型
 * @author Laotang
 */
public class AgvProcessModel extends VehicleProcessModel {

    /**车辆的地址*/
    private String vehicleHost;

    /**车辆的端口*/
    private int vehiclePort;

    /**车辆连接超时是否自动进行重新连接，默认为true*/
    private boolean reconnectingOnConnectionLoss = true;

    /**车辆空闲时，是否断开连接*/
    private boolean disconnectingOnVehicleIdle = false;

    /**重新连接前，延迟多少毫秒*/
    private int reconnectDelay = 10000;

    /**车辆timeout时间，超过这默认为5000毫秒 时间，则认为连接已断开 */
    private int vehicleIdleTimeout = 5000;

    /**是否应启用日志记录,默认为false不启用*/
    private boolean loggingEnabled = false;

    /**车辆是否空闲*/
    private boolean vehicleIdle = true;

//    /**车辆最后一次发送的订单请求*/
//    private OrderRequest lastOrderSent;

    /**是否开启定时发送任务*/
    private boolean periodicStateRequestEnabled = true;

//    /**
//     * 车辆当前状态
//     */
//    private MakerwitResponse currentState;
//    /**
//     * 车辆的上一个状态
//     */
//    private MakerwitResponse previousState;


    /**
     * 构造方法
     *
     * @param attachedVehicle 车辆属性
     */
    public AgvProcessModel(@Nonnull Vehicle attachedVehicle) {
        super(attachedVehicle);
    }

    public String getVehicleHost() {
        return vehicleHost;
    }

    public void setVehicleHost(String vehicleHost) {
        String oldValue = this.vehicleHost;
        this.vehicleHost = requireNonNull(vehicleHost, "vehicleHost");
        getPropertyChangeSupport().firePropertyChange(VEHICLE_HOST.name(),
                oldValue,
                vehicleHost);
    }

    public int getVehiclePort() {
        return vehiclePort;
    }

    public void setVehiclePort(int vehiclePort) {
        int oldValue = this.vehiclePort;
        this.vehiclePort = checkInRange(vehiclePort, ToolsKit.getMinPort(),ToolsKit.getMaxPort(), "vehiclePort");
        getPropertyChangeSupport().firePropertyChange(VEHICLE_PORT.name(),
                oldValue,
                vehiclePort);
    }

    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public void setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    public int getVehicleIdleTimeout() {
        return vehicleIdleTimeout;
    }

    public void setVehicleIdleTimeout(int vehicleIdleTimeout) {
        this.vehicleIdleTimeout = vehicleIdleTimeout;
    }

    public boolean isReconnectingOnConnectionLoss() {
        return reconnectingOnConnectionLoss;
    }

    public void setReconnectingOnConnectionLoss(boolean reconnectingOnConnectionLoss) {
        this.reconnectingOnConnectionLoss = reconnectingOnConnectionLoss;
    }

    public int getReconnectDelay() {
        return reconnectDelay;
    }

    public void setReconnectDelay(int reconnectDelay) {
        this.reconnectDelay = reconnectDelay;
    }

    public boolean isVehicleIdle() {
        return vehicleIdle;
    }

    public void setVehicleIdle(boolean idle) {
        boolean oldValue = this.vehicleIdle;
        this.vehicleIdle = idle;

        getPropertyChangeSupport().firePropertyChange(VEHICLE_IDLE.name(),
                oldValue,
                idle);
    }

    public boolean isDisconnectingOnVehicleIdle() {
        return disconnectingOnVehicleIdle;
    }

    public void setDisconnectingOnVehicleIdle(boolean disconnectingOnVehicleIdle) {
        boolean oldValue = this.disconnectingOnVehicleIdle;
        this.disconnectingOnVehicleIdle = disconnectingOnVehicleIdle;

        getPropertyChangeSupport().firePropertyChange(DISCONNECTING_ON_IDLE.name(),
                oldValue,
                disconnectingOnVehicleIdle);
    }

    /*
    @Nonnull
    public MakerwitResponse getCurrentState() {
        return currentState;
    }

    public void setCurrentState(@Nonnull MakerwitResponse currentState) {
        MakerwitResponse oldValue = this.currentState;
        this.currentState = requireNonNull(currentState, "currentState");

        getPropertyChangeSupport().firePropertyChange(CURRENT_STATE.name(),
                oldValue,
                currentState);
    }

    @Nonnull
    public MakerwitResponse getPreviousState() {
        return previousState;
    }

    public void setPreviousState(@Nonnull MakerwitResponse previousState) {
        MakerwitResponse oldValue = this.previousState;
        this.previousState = requireNonNull(previousState, "previousState");

        getPropertyChangeSupport().firePropertyChange(com.makerwit.opentcs.enums.Attribute.PREVIOUS_STATE.name(),
                oldValue,
                previousState);
    }

    public void setLastOrderSent(@Nonnull OrderRequest telegram) {
        OrderRequest oldValue = this.lastOrderSent;
        this.lastOrderSent = telegram;

        getPropertyChangeSupport().firePropertyChange(com.makerwit.opentcs.enums.Attribute.LAST_ORDER.name(),
                oldValue,
                lastOrderSent);
    }
    */

    public boolean isPeriodicStateRequestEnabled(){
        return periodicStateRequestEnabled;
    }

    public void setPeriodicStateRequestEnabled(boolean periodicStateRequestEnabled) {
        this.periodicStateRequestEnabled = periodicStateRequestEnabled;
    }


//    @Nonnull
//    public StateResponse getCurrentState() {
//        return currentState;
//    }
//
//    public void setCurrentState(@Nonnull StateResponse currentState) {
//        StateResponse oldValue = this.currentState;
//        this.currentState = requireNonNull(currentState, "currentState");
//
//        getPropertyChangeSupport().firePropertyChange(com.makerwit.opentcs.enums.Attribute.CURRENT_STATE.name(),
//                oldValue,
//                currentState);
//    }

}

