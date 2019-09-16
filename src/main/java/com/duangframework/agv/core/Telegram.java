package com.duangframework.agv.core;

public class Telegram implements java.io.Serializable {

    private static final long serialVersionUID = 3670079982654483073L;

    /**电报的默认ID值，由0开始*/
    public static final int ID_DEFAULT = 0;

    protected String id;

    public Telegram() {

    }

    public Telegram(Object id) {
        id = String.valueOf(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Telegram{" +
                "id='" + id + '\'' +
                '}';
    }
}

