package com.duangframework.agv.adapter;

import com.duangframework.agv.core.ITelegramSender;
import com.duangframework.agv.core.TelegramMatcher;
import org.opentcs.data.model.Vehicle;


/**
 * 通信适配器的各种实例的工厂
 *
 * @author Laotang
 */
public interface AdapterComponentsFactory {

    /**
     * 创建一个新的通讯适配器(CommAdapter)给车辆
     *
     * @param vehicle 车辆
     * @return CommAdapter
     */
    CommAdapter createCommAdapter(Vehicle vehicle);

    /**
     * 创建请求响应匹配器
     * @param telegramSender    发送电报对象
     * @return TelegramMatcher
     */
    TelegramMatcher createTelegramMatcher(ITelegramSender telegramSender);

}
