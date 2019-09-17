package com.duangframework.agv.core;

public abstract class Telegram<T> implements java.io.Serializable {

    private static final long serialVersionUID = 3670079982654483073L;

    /**电报ID*/
    protected String id;
    /**当前车辆所在的位置*/
    protected  String positionId;

    public Telegram() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public abstract String toString();

}

