package com.makerwit.agv;

import com.duangframework.agv.core.AgreementTemplate;
import com.duangframework.agv.core.Telegram;
import com.duangframework.agv.model.EmptyTelegram;
import com.duangframework.agv.model.ProcessModel;
import com.makerwit.agv.enums.FunctionCommandEnum;
import com.makerwit.agv.request.MakerwitRequest;
import com.makerwit.agv.request.UplinkParam;
import org.opentcs.drivers.vehicle.MovementCommand;

/**
 *
 */
public class MakerwitAgreementTemplate extends AgreementTemplate {

    /**
     * 发送请求到车辆
     * @param processModel  进程模型
     * @param movementCommand   移动命令
     * @return
     */
    @Override
    public Telegram builderTelegram(ProcessModel processModel, MovementCommand movementCommand) {
        String positionId = movementCommand.getStep().getDestinationPoint().getName();
        // TODO 模拟测试数据
        UplinkParam uplinkParam = new UplinkParam(positionId,
                "f",
                "100",
                "2",
                "0040"
        );
        MakerwitRequest request = new MakerwitRequest.Builder()
                .type("A001")
                .deviceId(processModel.getName())
                .terminalAddress(processModel.getVehicleHost()+":"+processModel.getVehiclePort())
                .functionCommand(FunctionCommandEnum.GETAC)
                .params(uplinkParam.toAgreementString())
                .build();
        request.setPositionId(positionId);
        return request;
    }

    /**
     * 接收到 返回信息后，创建Telegram对象
     *
     * @param responseString
     * @return
     */
    @Override
    public Telegram builderTelegram(String responseString) {
        java.util.Objects.requireNonNull(responseString, "车辆返回的协议字符串为空");
        String[] respArray = responseString.split(MakerwitRequest.SEPARATOR);
        String params = respArray[4];
        String crc = respArray[5];
        UplinkParam uplinkParam= UplinkParam.string2UplinkParam(params);
        String deviceId = respArray[1];
        String id = crc;
        String positionId = uplinkParam.getCard();
//         TODO 临时填充数据
//        loadState = LoadState.FULL;
//        operatingState = OperatingState.MOVING;
        EmptyTelegram telegram = new EmptyTelegram();
        telegram.setId(id);
        telegram.setPositionId(positionId);
        return telegram;
    }
}
