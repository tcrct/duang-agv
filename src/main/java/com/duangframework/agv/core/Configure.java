package com.duangframework.agv.core;


import com.duangframework.agv.kit.PathKit;

import java.io.File;

public class Configure {

    public Configure() {

    }

    protected void init() {
        String configPath = PathKit.getWebRootPath()+ File.separator  + "src"+File.separator+"main"+File.separator+"resources";
        System.setProperty("java.util.logging.config.file", configPath+ "/config/logging.config");
        System.setProperty("java.security.policy", configPath + "/config/java.policy");
        System.setProperty("opentcs.base", configPath);
        System.setProperty("opentcs.home", ".");
        System.setProperty("splash", configPath + "/bin/splash-image.gif");
    }

}
