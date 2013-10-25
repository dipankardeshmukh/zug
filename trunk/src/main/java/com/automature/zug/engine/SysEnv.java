package com.automature.zug.engine;

import java.io.File;
import java.lang.management.ManagementFactory;

import com.automature.zug.gui.ZugGUI;
import com.automature.zug.util.Log;
import com.automature.zug.util.Utility;

import javax.swing.*;


//import com.automature.zug.util.Utility;

public class SysEnv {

    public static String OS_NAME = "", OS_VERSION = "", OS_ARCH = "";
    public static String USER_HOME = "", USER_LANG = "", USER_NAME = "";
    public static String SLASH = "";
    public static String SEPARATOR = "";
    public static String PATH_CHECK = "";
    public static String LOG_DIR = "", ZIP_DIR = "", LOGLOCATION = "";
    public static boolean OS_FLAG;
    public static String JavaVersion = "", JavaHome = "", JavaCompiler = "",
            JavaLibraryPath = "" ,ComputerName="";
    public String mvmconfiguration;


    public  SysEnv() {

        OS_NAME = System.getProperty("os.name");
        OS_ARCH = System.getProperty("os.arch");
        OS_VERSION = System.getProperty("os.version");

        PATH_CHECK = "PATH";
        SEPARATOR = File.pathSeparator;
        SLASH = File.separator;
        LOG_DIR = Utility.getAppdataDirectory();
        OS_FLAG = true;
        ZIP_DIR = "TEMP";

        JavaVersion = System.getProperty("java.version");
        JavaCompiler = System.getProperty("java.compiler");
        JavaHome = System.getProperty("java.home");
        JavaLibraryPath = System.getProperty("java.library.path");
        ComputerName=System.getenv("ComputerName");

        LOGLOCATION = LOG_DIR;

        if (LOGLOCATION == null) {
            LOGLOCATION = System.getProperty("user.dir") + File.separator + "log";
        }

    }
    public static String getEnvProps(){
        try{
            String str="";
            str+="[OS Name="+System.getProperty("os.name")+"],";
            str+="[Java version="+System.getProperty("java.specification.version")+"],";
            str+="[LOGON SERVER="+System.getenv("LOGONSERVER")+"],";
            str+="[COMPUTER NAME="+ComputerName+"],";
            str+="[USER NAME="+System.getenv("USERNAME")+"],";
            str+="[APPDATA="+System.getenv("APPDATA")+"],";
            str+="[USERDOMAIN="+System.getenv("USERDOMAIN")+"],";
            //str+="os Arch="+System.getProperty("os.arch")+",";
            //	str+="PROCESSOR IDENTIFIER="+System.getenv("PROCESSOR_IDENTIFIER")+",";
            str+="[PROCESSOR_ARCHITECTURE="+System.getenv("PROCESSOR_ARCHITECTURE")+"],";
            str+="[NUMBER_OF_PROCESSORS="+System.getenv("NUMBER_OF_PROCESSORS")+"],";
            com.sun.management.OperatingSystemMXBean mxbean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            str+="[RAM="+mxbean.getTotalPhysicalMemorySize()/(1024*1024) + " MB]";
            //for(String str1:str.split(",")){
            //System.out.println(str);
            //	}
            //System.out.println(str);
            return str;
        }
        catch(Exception e)
        {
            Log.Debug(e.getLocalizedMessage());
            return "";

        }
    }
}
