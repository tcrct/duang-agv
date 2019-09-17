package com.duangframework.agv;


import com.duangframework.agv.core.Application;
import com.makerwit.agv.client.VehicleClient;

/**
 * 启动程序
 *
 * @author Laotang
 */
public class Duang {
    public static void main( String[] args ) throws Exception {
        Application.duang()
                .vehicleClient(new VehicleClient(9000, "Vehicle-01"))
                .run();
    }
}
