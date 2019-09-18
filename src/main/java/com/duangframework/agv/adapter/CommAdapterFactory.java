package com.duangframework.agv.adapter;

import com.duangframework.agv.kit.PropKit;
import com.duangframework.agv.kit.ToolsKit;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;
import org.opentcs.drivers.vehicle.VehicleCommAdapterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static java.util.Objects.requireNonNull;
import static org.opentcs.util.Assertions.checkInRange;

/**
 * 适配器工厂
 *
 * @author Laotang
 */
public class CommAdapterFactory implements VehicleCommAdapterFactory {

    private static final Logger logger = LoggerFactory.getLogger(CommAdapterFactory.class);

    private ComponentsFactory componentsFactory;

    private boolean initialized;

    @Inject
    public CommAdapterFactory(ComponentsFactory componentsFactory) {
        this.componentsFactory = requireNonNull(componentsFactory, "componentsFactory");
    }

    @Override
    public void initialize() {
        if(initialized) {
            logger.warn("适配器重复初始化");
            return;
        }
        initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void terminate() {
        if(!initialized) {
            logger.warn("适配器没有初始化");
            return;
        }
        initialized = false;
    }

    /**
     * 创智通讯适配器的名称
     * @return 名称
     */
    @Override
    @Deprecated
    public String getAdapterDescription() {
        return PropKit.get("adapter.name", "MyAdapter");
    }

    @Override
    public boolean providesAdapterFor(Vehicle vehicle) {
        java.util.Objects.requireNonNull(vehicle,"vehicle");
        if (ToolsKit.isEmpty(ToolsKit.getVehicleHostName())) {
            return false;
        }
        if (ToolsKit.isEmpty(ToolsKit.getVehiclePortName())) {
            return false;
        }
        try {
            // 端口设置范围
            int port = Integer.parseInt(vehicle.getProperty(ToolsKit.getVehiclePortName()));
            checkInRange(port,ToolsKit.getMinPort(),ToolsKit.getMaxPort(), "port value");
        } catch (IllegalArgumentException exc) {
            return false;
        }

        return true;
    }

    @Nullable
    @Override
    public VehicleCommAdapter getAdapterFor(@Nonnull Vehicle vehicle) {
        java.util.Objects.requireNonNull(vehicle,"vehicle");
        // 验证一下端口
        if(!providesAdapterFor(vehicle)){
            return null;
        }
        CommAdapter commAdapter = componentsFactory.createCommAdapter(vehicle);
        requireNonNull(commAdapter, "通讯适配器对象不能为空，请检查！");
        commAdapter.getProcessModel().setVehicleHost(vehicle.getProperty(ToolsKit.getVehicleHostName()));
        commAdapter.getProcessModel().setVehiclePort(Integer.parseInt(vehicle.getProperty(ToolsKit.getVehiclePortName())));
        return commAdapter;
    }

}
