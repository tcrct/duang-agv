package com.makerwit.agv.enums;

/**
 * 功能指令枚举
 *
 * @author Laotang
 */
public enum FunctionCommandEnum {
    GETAC("getac", "查询RFID卡"),
    RATAC("rptac", "上报RFID号"),
    ;

    private String value;
    private String desc;
    private FunctionCommandEnum(String value, String desc) {
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
