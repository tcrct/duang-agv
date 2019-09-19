package com.duangframework.agv.model;

public interface AdapterConstants {


    /**
     * The key of the vehicle property that specifies the vehicle's initial position.
     */
    String PROPKEY_INITIAL_POSITION = "loopback:initialPosition";
    /**
     * The key of the vehicle property that specifies the default operating time.
     */
    String PROPKEY_OPERATING_TIME = "loopback:operatingTime";
    /**
     * The key of the vehicle property that specifies which operation loads the load handling device.
     */
    String PROPKEY_LOAD_OPERATION = "loopback:loadOperation";
    /**
     * The default value of the load operation property.
     */
    String PROPVAL_LOAD_OPERATION_DEFAULT = "Load cargo";
    /**
     * The key of the vehicle property that specifies which operation unloads the load handling device.
     */
    String PROPKEY_UNLOAD_OPERATION = "loopback:unloadOperation";
    /**
     * The default value of the unload operation property.
     */
    String PROPVAL_UNLOAD_OPERATION_DEFAULT = "Unload cargo";
    /**
     * The key of the vehicle property that specifies the maximum acceleration of a vehicle.
     */
    String PROPKEY_ACCELERATION = "loopback:acceleration";
    /**
     * The key of the vehicle property that specifies the maximum decceleration of a vehicle.
     */
    String PROPKEY_DECELERATION = "loopback:deceleration";

}
