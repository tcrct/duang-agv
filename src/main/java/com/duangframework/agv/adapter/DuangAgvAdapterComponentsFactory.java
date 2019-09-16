package com.duangframework.agv.adapter;

import com.makerwit.opentcs.common.telegrams.RequestResponseMatcher;
import com.makerwit.opentcs.common.telegrams.TelegramSender;
import org.opentcs.data.model.Vehicle;

import java.awt.event.ActionListener;

/**
 * 通信适配器的各种实例的工厂
 * @author Laotang
 */
public interface DuangAgvAdapterComponentsFactory {

    /**
     * 创建一个新的通讯适配器(MakerwitCommAdapter )给车辆
     *
     * @param vehicle 车辆
     * @return DuangAgvCommAdapter
     */
    DuangAgvCommAdapter createMakerwitCommAdapterCommAdapter(Vehicle vehicle);

    /**
     * 创建请求响应匹配器
     * @param telegramSender    发送电报对象
     * @return
     */
    RequestResponseMatcher createRequestResponseMatcher(TelegramSender telegramSender);

}
