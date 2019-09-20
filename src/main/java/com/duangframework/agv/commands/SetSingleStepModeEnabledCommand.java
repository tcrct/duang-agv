package com.duangframework.agv.commands;

import com.duangframework.agv.adapter.CommAdapter;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;
import org.opentcs.virtualvehicle.LoopbackCommunicationAdapter;

public class SetSingleStepModeEnabledCommand
        implements AdapterCommand {

    /**
     * Whether to enable/disable single step mode.
     */
    private final boolean enabled;

    /**
     * Creates a new instance.
     *
     * @param enabled Whether to enable/disable single step mode.
     */
    public SetSingleStepModeEnabledCommand(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void execute(VehicleCommAdapter adapter) {
        if (!(adapter instanceof CommAdapter)) {
            return;
        }

        CommAdapter loopbackAdapter = (CommAdapter) adapter;
        loopbackAdapter.getProcessModel().setSingleStepModeEnabled(enabled);
    }
}
