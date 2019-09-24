package com.duangframework.agv.core;


import com.duangframework.agv.contrib.serialport.SerialPortManager;
import com.duangframework.agv.enums.CommunicationType;
import com.duangframework.agv.kit.PathKit;
import com.duangframework.agv.kit.PropKit;
import com.duangframework.agv.kit.ToolsKit;
import gnu.io.SerialPort;

import java.io.File;
import java.util.List;

public class Configure {

    private static CommunicationType communicationType;
    private static SerialPort mSerialport;

    public Configure() {

    }

    protected void init(CommunicationType communicationType) {
        String classPath = PathKit.getPath(Configure.class);
        String packagePath= File.separator + Configure.class.getPackage().getName().replace(".", File.separator);
        String configPath = classPath.replace(packagePath, "");
        System.setProperty("java.util.logging.config.file", configPath+ "/config/logging.config");
        System.setProperty("java.security.policy", configPath + "/config/java.policy");
        System.setProperty("opentcs.base", configPath);
        System.setProperty("opentcs.home", ".");
        System.setProperty("splash", configPath + "/bin/splash-image.gif");
        System.setProperty("file.encoding", "UTF-8");
        this.communicationType = communicationType;
        initCommunicationType();
    }

    public static CommunicationType getCommunicationType() {
        return communicationType;
    }

    public static SerialPort getSerialport() {
        return mSerialport;
    }

    private void initCommunicationType() {
        if(CommunicationType.SERIALPORT.equals(communicationType)) {
            List<String> mCommList = SerialPortManager.findPorts();
            if(ToolsKit.isEmpty(mCommList)) {
                throw new NullPointerException("没有找到可用的串串口！");
            }

            String serialPortName = PropKit.get("serialport.name", "COM6");
            if(!mCommList.contains(serialPortName)) {
                throw new IllegalArgumentException("指定的串口名称["+serialPortName+"]与系统允许使用的不符");
            }

            // 获取波特率，默认为9600
            int baudrate = PropKit.getInt("serialport.baudrate", 9600);
            try {
                mSerialport = SerialPortManager.openPort(serialPortName, baudrate);
            } catch (Exception e) {
                throw new RuntimeException("打开串口时失败，名称["+serialPortName+"]， 波特率["+baudrate+"]");
            }

            System.out.println("串口启动成功");

        }
    }

}
