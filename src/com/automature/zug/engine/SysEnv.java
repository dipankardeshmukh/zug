package com.automature.zug.engine;


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
			JavaLibraryPath = "";
	 boolean isOSSupported=false;
	public String mvmconfiguration;
    SysEnv(){
    	if (OS_NAME.toLowerCase().contains("windows")) {
			PATH_CHECK = "Path";
			SEPARATOR = ";";
			SLASH = "\\";
			LOG_DIR = "APPDATA";
			OS_FLAG = true;
			ZIP_DIR = "TEMP";
			isOSSupported=true;

		} else if (OS_NAME.equalsIgnoreCase("linux")) {
			PATH_CHECK = "PATH";
			SEPARATOR = ":";
			SLASH = "/";
			LOG_DIR = "HOME";
			OS_FLAG = false;
			ZIP_DIR = "HOME";
			isOSSupported=true;

		} else {
			
		}
		OS_NAME = System.getProperty("os.name");
		OS_ARCH = System.getProperty("os.arch");
		OS_VERSION = System.getProperty("os.version");
		// Getting Java software used informations
		JavaVersion = System.getProperty("java.version");
		JavaCompiler = System.getProperty("java.compiler");
		JavaHome = System.getProperty("java.home");
		JavaLibraryPath = System.getProperty("java.library.path");
		// mvmconfiguration=Utility.getPhysicalMemory();
		LOGLOCATION = System.getenv(LOG_DIR);
		if (LOGLOCATION == null) {
			LOGLOCATION = System.getProperty("user.dir") + "/log";
			
		}   
		//mvmconfiguration = Utility.getMaxJVMMemorySize(Runtime.getRuntime())
		//		.split("\\.")[0];
    }

}
