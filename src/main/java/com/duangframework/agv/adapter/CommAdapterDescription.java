package com.duangframework.agv.adapter;

import org.opentcs.drivers.vehicle.VehicleCommAdapterDescription;

public class CommAdapterDescription extends VehicleCommAdapterDescription {
    @Override
    public String getDescription() {
        return "communication adapter";
    }
}
