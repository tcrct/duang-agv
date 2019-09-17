package com.duangframework.agv.core;

import io.netty.channel.ChannelHandler;
import org.opentcs.drivers.vehicle.MovementCommand;

import java.util.List;
import java.util.function.Supplier;

/**
 * 构建电报接口类
 * @param <T> 进程模型泛型
 */
public interface ITelegramMapper<T> {

    /**
     *  构建电报
     * @param processModel  进程模型
     * @param movementCommand   移动命令
     * @return
     */
    Telegram  builderTelegram(T processModel, MovementCommand movementCommand);

    /**
     *
     * @param responseString
     * @return
     */
    Telegram builderTelegram(String responseString);

    /**
     *
     */
    Supplier<List<ChannelHandler>> getChannelHandlers();
}
