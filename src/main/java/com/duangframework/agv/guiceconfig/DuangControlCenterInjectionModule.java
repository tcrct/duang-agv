package com.duangframework.agv.guiceconfig;

import com.duangframework.agv.adapter.CommAdapterPanelFactory;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.opentcs.customizations.controlcenter.ControlCenterInjectionModule;
import org.opentcs.virtualvehicle.AdapterPanelComponentsFactory;

/**
 *
 */
public class DuangControlCenterInjectionModule extends ControlCenterInjectionModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().build(AdapterPanelComponentsFactory.class));
        commAdapterPanelFactoryBinder().addBinding().to(CommAdapterPanelFactory.class);
    }

}
