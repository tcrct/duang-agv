package com.duangframework.agv.guiceconfig;

import com.duangframework.agv.adapter.ComponentsFactory;
import com.duangframework.agv.adapter.CommAdapterFactory;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DuangKernelInjectionModule extends KernelInjectionModule {

    private static final Logger logger = LoggerFactory.getLogger(DuangKernelInjectionModule.class);

    @Override
    protected void configure() {
        // 安装及绑定通讯工厂
        install(new FactoryModuleBuilder().build(ComponentsFactory.class));
        vehicleCommAdaptersBinder().addBinding().to(CommAdapterFactory.class);
        logger.info("安装及绑定通讯工厂成功");
    }
}