package com.makerwit.agv.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.opentcs.contrib.tcp.netty.ClientEntry;
import org.opentcs.contrib.tcp.netty.ConnectionAssociatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ConnectionAssociator extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ChannelInboundHandlerAdapter.class);

    /**连接到TcpServerChannelManager的客户端连接池*/
    private Map<Object, ClientEntry<String>> clientEntryMap;

    /**客户端*/
    private ClientEntry<String> client;

    public ConnectionAssociator(Map<Object, ClientEntry<String>> clientEntrys) {
        this.clientEntryMap = java.util.Objects.requireNonNull(clientEntrys ,"clientEntitys");
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) {
        logger.info("接收到的消息: {}", msg);
        // 如果不是字符串则忽略，退出
        if (!(msg instanceof String)) {
            logger.info("接收到的消息不是字符串类型，忽略并退出");
            return;
        }

        if (null == client) {
            logger.info("客户端接收一个数据");

            client = clientEntryMap.get(VehicleClient.getClientName());

            //如果还是为null， 则关闭连接
            if(null == client) {
                logger.info("客户端为null，关闭连接且忽略未知键{}的消息，注册keyw集合{}", VehicleClient.getClientName(), clientEntryMap.keySet());
                context.close();
                return;
            }
            // 设置渠道
            client.setChannel(context.channel());
            // 隐式地通知任何侦听器该通道已与此昵称关联
            // 通知通信适配器已建立连接。
            context.fireUserEventTriggered(new ConnectionAssociatedEvent(VehicleClient.getClientName()));
        }

        client.getConnectionEventListener().onIncomingTelegram((String)msg);

    }



}
