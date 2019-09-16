package com.duangframework.agv.guiceconfig;

import com.duangframework.agv.adapter.DuangAgvAdapterComponentsFactory;
import com.duangframework.agv.adapter.DuangAgvCommAdapterFactory;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DuangAgvKernelInjectionModule extends KernelInjectionModule {

    private static final Logger logger = LoggerFactory.getLogger(DuangAgvKernelInjectionModule.class);

    @Override
    protected void configure() {
        // 安装及绑定通讯工厂
        install(new FactoryModuleBuilder().build(DuangAgvAdapterComponentsFactory.class));
        vehicleCommAdaptersBinder().addBinding().to(DuangAgvCommAdapterFactory.class);
        logger.info("安装及绑定通讯工厂成功");
    }
}