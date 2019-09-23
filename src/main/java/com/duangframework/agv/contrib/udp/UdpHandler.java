package com.duangframework.agv.contrib.udp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;


public class UdpHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
        try {
            // 因为Netty对UDP进行了封装，所以接收到的是DatagramPacket对象。
            String str = datagramPacket.content().toString(CharsetUtil.UTF_8);
            System.out.println(str+"           "+ datagramPacket.getClass());
            ctx.channel().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("udp: "+ str, CharsetUtil.UTF_8), datagramPacket.sender()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause)throws Exception{
        ctx.close();
        cause.printStackTrace();
    }
}
