package com.duangframework.agv.kit;

import com.duangframework.agv.enums.OperatingState;
import org.opentcs.data.model.Vehicle;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class ToolsKit {

    // 车辆的host地址
    private static String VEHICLE_HOST_FIELD;
    // 车辆的端口
    private static String VEHICLE_PORT_FIELD;

    /***
     * 判断传入的对象是否为空
     *
     * @param obj
     *            待检查的对象
     * @return 返回的布尔值,为空或等于0时返回true
     */
    public static boolean isEmpty(Object obj) {
        return checkObjectIsEmpty(obj, true);
    }

    /***
     * 判断传入的对象是否不为空
     *
     * @param obj
     *            待检查的对象
     * @return 返回的布尔值,不为空或不等于0时返回true
     */
    public static boolean isNotEmpty(Object obj) {
        return checkObjectIsEmpty(obj, false);
    }

    @SuppressWarnings("rawtypes")
    private static boolean checkObjectIsEmpty(Object obj, boolean bool) {
        if (null == obj) {
            return bool;
        }
        else if (obj == "" || "".equals(obj)) {
            return bool;
        }
        else if (obj instanceof Integer || obj instanceof Long || obj instanceof Double) {
            try {
                Double.parseDouble(obj + "");
            } catch (Exception e) {
                return bool;
            }
        } else if (obj instanceof String) {
            if (((String) obj).length() <= 0) {
                return bool;
            }
            if ("null".equalsIgnoreCase(obj+"")) {
                return bool;
            }
        } else if (obj instanceof Map) {
            if (((Map) obj).size() == 0) {
                return bool;
            }
        } else if (obj instanceof Collection) {
            if (((Collection) obj).size() == 0) {
                return bool;
            }
        } else if (obj instanceof Object[]) {
            if (((Object[]) obj).length == 0) {
                return bool;
            }
        }
        return !bool;
    }

    /**
     * 取车辆host名称
     * @return
     */
    public static String getVehicleHostName() {
        if(ToolsKit.isEmpty(VEHICLE_HOST_FIELD)) {
            VEHICLE_HOST_FIELD = PropKit.get("vehicle.host.name", "host");
        }
        return VEHICLE_HOST_FIELD;
    }

    /**
     * 取车辆port名称
     * @return
     */
    public static String getVehiclePortName() {
        if(ToolsKit.isEmpty(VEHICLE_PORT_FIELD)) {
            VEHICLE_PORT_FIELD = PropKit.get("vehicle.port.name", "port");
        }
        return VEHICLE_PORT_FIELD;
    }

    /**
     * 端口最小值
     * @return
     */
    public static int getMinPort(){
        return PropKit.getInt("vehicle.min.port",5050);
    }

    /**
     * 端口最大值
     * @return
     */
    public static int getMaxPort(){
        return PropKit.getInt("vehicle.max.port", 6060);
    }

    /**
     * 检查端口范围
     * @param value
     * @param minimum
     * @param maximum
     * @param valueName
     * @return
     */
    public static  int checkInRange(int value, int minimum, int maximum, String valueName) {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(String.format("%s is not in [%d..%d]: %d",
                    String.valueOf(valueName),
                    minimum,
                    maximum,
                    value));
        }
        return value;
    }

    /**
     * Ensures the given expression is {@code true}.
     *
     * @param expression The expression to be checked.
     * @param messageTemplate A formatting template for the error message.
     * @param messageArgs The arguments to be formatted into the message template.
     * @throws IllegalArgumentException If the given expression is not true.
     */
    public static void checkArgument(boolean expression, String messageTemplate, @Nullable Object... messageArgs) throws IllegalArgumentException {
        if (!expression) {
            throw new IllegalArgumentException(String.format(String.valueOf(messageTemplate),
                    messageArgs));
        }
    }

    /**
     将车辆的运行状态映射到内核的车辆状态
     *
     * @param operationState  车辆当前的操作状态
     */
    public static Vehicle.State translateVehicleState(OperatingState operationState) {
        switch (operationState) {
            case IDLE:
                return Vehicle.State.IDLE;
            case MOVING:
            case ACTING:
                return Vehicle.State.EXECUTING;
            case CHARGING:
                return Vehicle.State.CHARGING;
            case ERROR:
                return Vehicle.State.ERROR;
            default:
                return Vehicle.State.UNKNOWN;
        }
    }

    /**
     *  将字符串日期根据format格式化字段转换成日期类型
     * @param stringDate    字符串日期
     * @param format           格式化日期
     * @return
     */
    public static Date parseDate(String stringDate, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
