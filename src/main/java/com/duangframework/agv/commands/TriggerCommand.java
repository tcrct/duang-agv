package com.duangframework.agv.commands;

import com.duangframework.agv.adapter.CommAdapter;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;
import org.opentcs.virtualvehicle.LoopbackCommunicationAdapter;

public class TriggerCommand
        implements AdapterCommand {

    @Override
    public void execute(VehicleCommAdapter adapter) {
        if (!(adapter instanceof CommAdapter)) {
            return;
        }

        CommAdapter loopbackAdapter = (CommAdapter) adapter;
        loopbackAdapter.trigger();
    }
}
