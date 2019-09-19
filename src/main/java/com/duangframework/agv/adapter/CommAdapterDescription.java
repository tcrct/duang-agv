package com.duangframework.agv.adapter;

import com.duangframework.agv.kit.PropKit;
import org.opentcs.drivers.vehicle.VehicleCommAdapterDescription;

public class CommAdapterDescription extends VehicleCommAdapterDescription {
    @Override
    public String getDescription() {
        return PropKit.get("adapter.name","MyAdapter");
    }
}
