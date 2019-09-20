package com.duangframework.agv.core;


import com.duangframework.agv.kit.PathKit;

import java.io.File;

public class Configure {

    public Configure() {

    }

    protected void init() {
        String classPath = PathKit.getPath(Configure.class);
        String packagePath= File.separator + Configure.class.getPackage().getName().replace(".", File.separator);
        String configPath = classPath.replace(packagePath, "");
        System.setProperty("java.util.logging.config.file", configPath+ "/config/logging.config");
        System.setProperty("java.security.policy", configPath + "/config/java.policy");
        System.setProperty("opentcs.base", configPath);
        System.setProperty("opentcs.home", ".");
        System.setProperty("splash", configPath + "/bin/splash-image.gif");
        System.setProperty("file.encoding", "UTF-8");
    }

}
