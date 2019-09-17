package com.makerwit.agv.client;

import com.duangframework.agv.core.IVehicleClient;

/**
 * 车辆Dto对象
 *
 * @author Laotang
 */
public class VehicleClientDto implements java.io.Serializable {

    private String host;
    private Integer port;
    private String name;

    public VehicleClientDto() {
    }

    public VehicleClientDto(Integer port, String name) {
        this.port = port;
        this.name = name;
    }

    public VehicleClientDto(String host, Integer port, String name) {
        this.host = host;
        this.port = port;
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "VehicleClientDto{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", name='" + name + '\'' +
                '}';
    }

}
