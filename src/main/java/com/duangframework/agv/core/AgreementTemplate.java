package com.duangframework.agv.core;


import com.duangframework.agv.adapter.CommAdapter;
import com.duangframework.agv.codec.VehicleTelegramDecoder;
import com.duangframework.agv.codec.VehicleTelegramEncoder;
import com.duangframework.agv.listener.ConnEventListener;
import com.duangframework.agv.model.ProcessModel;
import io.netty.channel.ChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public abstract class AgreementTemplate implements ITelegramMapper<ProcessModel>, ITelegramSender {


    private final static Logger logger = LoggerFactory.getLogger(AgreementTemplate.class);

    private CommAdapter commAdapter;
    private ConnEventListener connEventListener;

    public AgreementTemplate() {
    }

    public void setComponent(CommAdapter adapter) {
        this.commAdapter = adapter;
    }

    public ConnEventListener getConnEventListener() {
        if(null == connEventListener && null != commAdapter){
            connEventListener = new ConnEventListener(commAdapter);
        }
        return connEventListener;
    }

    /**
     * 返回负责从字节流中写入和读取的通道处理程序
     * @return The channel handlers responsible for writing and reading from the byte stream
     */
    public Supplier<List<ChannelHandler>> getChannelHandlers() {
        Supplier<List<ChannelHandler>> listSupplier = new Supplier<List<ChannelHandler>>() {
            @Override
            public List<ChannelHandler> get() {
                return Arrays.asList(
                        new VehicleTelegramDecoder(getConnEventListener()),
                        new VehicleTelegramEncoder());
            }
        };
        return listSupplier;
    }




    @Override
    public void sendTelegram(Telegram telegram) {
        requireNonNull(telegram, "telegram");
        if (!commAdapter.isVehicleConnected()) {
            logger.debug("{}: Not connected - not sending request '{}'",
                    commAdapter.getName(),
                    telegram);
            return;
        }

        logger.info("{}: Sending request '{}'", commAdapter.getName(), telegram.toString());
        commAdapter.getVehicleChannelManager().send(telegram.toString());
    }

}
