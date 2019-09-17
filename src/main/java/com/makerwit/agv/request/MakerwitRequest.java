package com.makerwit.agv.request;

import com.duangframework.agv.core.Telegram;
import com.duangframework.agv.kit.CrcKit;
import com.makerwit.agv.enums.DirectionEnum;
import com.makerwit.agv.enums.FunctionCommandEnum;

/**
 * 创智通讯协议对象
 * 最终以字符串的形式进行发送
 *
 * 将id字段值设置为crc
 *
 * @author Laotang
 */
public class MakerwitRequest extends Telegram<String> {

    /**分隔符*/
    public static final String SEPARATOR = ",,";
    /**帧头标识*/
    public static final String FRAME_HEAD =  "##";
    /**帧尾标识*/
    public static final String FRAME_END =  "ZZ";
    /**上行标识符*/
    public static final String UP_LINK = "S";
    /**下行标识符*/
    public static final String DOWN_LINK = "r";

    /**类型标识*/
    private String type;
    /**终端地址*/
    private String terminalAddress;
    /**设备ID*/
    private String deviceId;
    /**功能指令*/
    private FunctionCommandEnum functionCommand;
    /**参数*/
    private String params;
    /**方向,上下行*/
    private DirectionEnum direction;


    private MakerwitRequest() {
    }



    private MakerwitRequest(String type, String terminalAddress, String deviceId, DirectionEnum direction, FunctionCommandEnum functionCommand, String params) {
        this.type = type;
        this.terminalAddress = terminalAddress;
        this.deviceId = deviceId;
        this.direction = direction;
        this.functionCommand = functionCommand;
        this.params = params;
    }

    public static class Builder {
        private String type;
        private String terminalAddress;
        private String deviceId;
        private DirectionEnum direction;
        private FunctionCommandEnum functionCommand;
        private String params;

        public Builder type(String type) {
            this.type = type;
            return this;
        }
        public Builder terminalAddress(String terminalAddress) {
            this.terminalAddress = terminalAddress;
            return this;
        }
        public Builder deviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }
        public Builder direction(DirectionEnum direction) {
            this.direction = direction;
            return this;
        }
        public Builder functionCommand(FunctionCommandEnum functionCommand) {
            this.functionCommand = functionCommand;
            return this;
        }
        public Builder params(String params) {
            this.params = params;
            return this;
        }
        public MakerwitRequest build() {
            MakerwitRequest request = new MakerwitRequest(type, terminalAddress, deviceId, direction, functionCommand, params);
            request.setId(request.buildCrc());
            return request;
        }
    }

    public String buildCrc() {
        return String.valueOf(CrcKit.CrcVerify(getCrcString()));
    }

    private String getCrcString() {
        StringBuilder crcString = new StringBuilder();
        crcString
                .append(type)
                .append(terminalAddress)
                .append(FRAME_HEAD)
                .append(SEPARATOR)
                .append(deviceId)
                .append(SEPARATOR)
                .append(UP_LINK)
                .append(SEPARATOR)
                .append(functionCommand.getValue())
                .append(SEPARATOR)
                .append(params)
                .append(SEPARATOR);
        return crcString.toString();
    }

    /**
     * 规则如下：
     * TCP/IP:  类型标识+终端地址+帧头+分隔符+设备ID+分隔符+上下行标识符+分隔符+功能指令+分隔符+参数(上位机)+分隔符+CRC校验码+分隔符+帧尾
     * 串口：帧头+分隔符+设备ID+分隔符+上下行标识符+分隔符+功能指令+分隔符+参数(上位机)+分隔符+CRC校验码+分隔符+帧尾
     *
     * @return 符合规则的字符串
     */
    @Override
    public String toString() {
        StringBuilder makerwitAgreement = new StringBuilder(getCrcString());
        // 添加CRC校验码，校验码存在ID字段里
        makerwitAgreement.append(getId())
                .append(SEPARATOR)
                .append(FRAME_END);
        return makerwitAgreement.toString();
    }

    public String getParams() {
        return params;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
