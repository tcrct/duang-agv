package com.duangframework.agv.core;

import com.duangframework.agv.enums.CommunicationType;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.opentcs.guing.RunPlantOverview;
import org.opentcs.kernel.RunKernel;
import org.opentcs.kernelcontrolcenter.RunKernelControlCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static Application application;
    private List<IVehicleClient> vehicleClientList = new ArrayList<>();
    private Configure configure;
    private CommunicationType communicationType;

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

    public Application type(CommunicationType typeEnum) {
        this.communicationType = typeEnum;
        return application;
    }

    public Application configure(Configure config) {
        this.configure = config;
        return application;
    }

    public void run() throws Exception {
        // 默认以串口的方式通讯
        if (null == communicationType) {
            communicationType = CommunicationType.SERIALPORT;
        }

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
        configure.init(communicationType);
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

//        ExecutorService executor = Executors.newFixedThreadPool(2);
//        executor.submit(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
//        executor.submit(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
    }

}
