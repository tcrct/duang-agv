package com.duangframework.agv.core;

import org.opentcs.drivers.vehicle.MovementCommand;

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

    Telegram builderTelegram(String responseString);
}
