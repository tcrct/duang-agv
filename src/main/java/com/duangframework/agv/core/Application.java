package com.duangframework.agv.core;

import org.opentcs.guing.RunPlantOverview;
import org.opentcs.kernel.RunKernel;
import org.opentcs.kernelcontrolcenter.RunKernelControlCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static Application application;
    private List<IVehicleClient> vehicleClientList = new ArrayList<>();
    private Configure configure;

    public static Application duang() {
        if(null == application) {
            application = new Application();
        }
        return application;
    }

    public Application vehicleClient(IVehicleClient vehicleClient) {
        java.util.Objects.requireNonNull(vehicleClient != null, "车辆Dto对象不能为null");
        vehicleClientList.add(vehicleClient);
        return application;
    }

    public Application configure(Configure config) {
        this.configure = config;
        return application;
    }

    public void run() throws Exception {
        if(null == configure) {
            configure = new Configure();
        }
        // 启动车辆
        if(!vehicleClientList.isEmpty()) {
            for (IVehicleClient vehicleClient: vehicleClientList) {
                vehicleClient.start();
            }
            logger.warn("车辆启动成功");
        }

        // 初始化配置
        configure.init();
        logger.warn("初始化参数完成");

        // 启动内核
        RunKernel.main(null);
        logger.warn("启动内核完成");

        // 启动内核心控制中心
        RunKernelControlCenter.main(null);
        logger.warn("启动内核心控制中心完成");

        // 启动工厂概述控制中心
        RunPlantOverview.main(null);
        logger.warn("启动工厂概述控制中心完成");
    }

}
