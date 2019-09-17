package com.makerwit.agv.request;


import com.duangframework.agv.kit.ToolsKit;

/**
 * 规则：卡号::方向::速度::激光区域::报警代码
 * 示例：1::f::100::2::0040
 * Created by laotang on 2019/9/13.
 */
public class UplinkParam implements java.io.Serializable {

    /**分隔符*/
    private static final String SEPARATOR = "::";

    /**卡号*/
    private String card;
    /**方向*/
    private String direction;
    /**速度*/
    private String speed;
    /**激光区域*/
    private String laserarea;
    /**报警代码*/
    private String alarmCode;

    public UplinkParam() {

    }

    public UplinkParam(String card, String direction, String speed, String laserarea, String alarmCode) {
        this.card = card;
        this.direction = direction;
        this.speed = speed;
        this.laserarea = laserarea;
        this.alarmCode = alarmCode;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getLaserarea() {
        return laserarea;
    }

    public void setLaserarea(String laserarea) {
        this.laserarea = laserarea;
    }

    public String getAlarmCode() {
        return alarmCode;
    }

    public void setAlarmCode(String alarmCode) {
        this.alarmCode = alarmCode;
    }

    public static UplinkParam string2UplinkParam(String uplinkString) {
        java.util.Objects.requireNonNull(uplinkString, "上位机参数字符串");
        String[] paramsArray = uplinkString.split(SEPARATOR);
        if(ToolsKit.isEmpty(paramsArray)) {
            throw new NullPointerException("将上位机字符串分割成数据时出错，可能参数字符串为空");
        }
        if(paramsArray.length !=5) {
            throw new NullPointerException("将上位机字符串分割成数据时出错,数组长度必须等于5位");
        }
        return new UplinkParam(paramsArray[0], paramsArray[1], paramsArray[2], paramsArray[3],paramsArray[4]);
    }

    /**
     * 将对象转换为通讯协议规则的字符串
     * @return  符合规则 的字符串
     */
    public String toAgreementString() {
        StringBuilder paramString = new StringBuilder();
        paramString.append(card)
                .append(SEPARATOR)
                .append(direction)
                .append(SEPARATOR)
                .append(speed)
                .append(SEPARATOR)
                .append(laserarea)
                .append(SEPARATOR)
                .append(alarmCode);
        return paramString.toString();
    }
}

