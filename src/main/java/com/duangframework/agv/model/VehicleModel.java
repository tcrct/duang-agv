package com.duangframework.agv.model;

import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.VehicleProcessModel;
import org.opentcs.virtualvehicle.VelocityListener;

import javax.annotation.Nonnull;

public class VehicleModel extends VehicleProcessModel implements VelocityListener {
    /**
     * Creates a new instance.
     *
     * @param attachedVehicle The vehicle attached to the new instance.
     */
    public VehicleModel(@Nonnull Vehicle attachedVehicle) {
        super(attachedVehicle);
    }

    @Override
    public void addVelocityValue(int velocityValue) {

    }
}
