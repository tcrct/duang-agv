package com.makerwit.agv.client;

import com.duangframework.agv.core.IVehicleClient;
import io.netty.channel.ChannelHandler;
import org.opentcs.contrib.tcp.netty.ClientEntry;
import org.opentcs.contrib.tcp.netty.ConnectionEventListener;
import org.opentcs.contrib.tcp.netty.TcpServerChannelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  模拟客户端---车辆
 *
 * @author Laotang
 */
public class VehicleClient implements IVehicleClient, ConnectionEventListener<String> {

    private static final Logger logger = LoggerFactory.getLogger(VehicleClient.class);

    private TcpServerChannelManager<String,String> vehicleServer;
    public static final Object CLIENT_OBJECT = new Object();
    private final Map<Object, ClientEntry<String>> client = new HashMap<>();
    private int port = 9000;
    private static String clientName;

    /**
     * 车辆构造方法
     * @param port  端口
     */
    public VehicleClient(int port) {
       this(port, String.valueOf(CLIENT_OBJECT.hashCode()));
    }

    /**
     * 车辆构造方法
     * @param port  端口
     * @param clinetName 车辆名称，须唯一
     */
    public VehicleClient(int port, String clinetName) {
        this.port = port;
        this.clientName = clinetName;
        vehicleServer =new TcpServerChannelManager<>(port, client, this::getChannelHandlers, 5000, true);
    }

    @Override
    public void start(){
        initialize();
    }

    private void initialize() {
        if (vehicleServer.isInitialized()) {
            return;
        }
        vehicleServer.initialize();
        vehicleServer.register(clientName, this, true);
        logger.info("车辆{}启动成功,端口:{}", clientName, port);
    }

    private void terminate() {
        if (!vehicleServer.isInitialized()) {
            return;
        }
        vehicleServer.terminate();
    }

    @Override
    public void onIncomingTelegram(String telegram) {
        logger.info("车辆接收到的请求: {}", telegram);

        // 接收到什么回复什么，为了演示，强制暂停1秒，以查看效果
        try {
            Thread.sleep(1000L);
        } catch (Exception e) {

        }
        vehicleServer.send(clientName, telegram);
    }

    @Override
    public void onConnect() {
        logger.warn("通讯适配器链成功连接到车辆");
    }

    @Override
    public void onFailedConnectionAttempt() {
        logger.warn("通讯适配器链连接车辆失败");
    }

    @Override
    public void onDisconnect() {
        logger.info("车辆从通讯适配器断开连接");
        terminate();
        initialize();
    }

    @Override
    public void onIdle() {
//        logger.info("通讯适配器空闲");
    }

    private List<ChannelHandler> getChannelHandlers() {
        return Arrays.asList(
//                new LengthFieldBasedFrameDecoder(getMaxTelegramLength(), 1, 1, 2, 0),
                new TelegramDecoder(),
                new TelegramEncoder(),
                new ConnectionAssociator(client));
    }

    public static Object getClientName() {
        return clientName;
    }
}
