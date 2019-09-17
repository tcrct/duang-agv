package com.makerwit.agv.enums;

/**
 * 功能指令枚举
 *
 * @author Laotang
 */
public enum DirectionEnum {
    UP("S", "上行"),
    DOWN("r", "下行"),
    ;

    private String value;
    private String desc;
    private DirectionEnum(String value, String desc) {
        this.value = value;
        this.desc =desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
