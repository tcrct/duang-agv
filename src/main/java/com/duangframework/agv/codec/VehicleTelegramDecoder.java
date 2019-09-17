package com.duangframework.agv.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringDecoder;
import org.opentcs.contrib.tcp.netty.ConnectionEventListener;

import java.nio.charset.Charset;
import java.util.List;

/**
 * 车辆电报解码
 */
public class VehicleTelegramDecoder extends StringDecoder {

    private final ConnectionEventListener<String> responseHandler;

    public VehicleTelegramDecoder(ConnectionEventListener<String> responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        String telegramData = msg.toString(Charset.defaultCharset());
        java.util.Objects.requireNonNull(telegramData, "telegramData");
        responseHandler.onIncomingTelegram(telegramData);
    }
}
