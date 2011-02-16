/***
 * Log.java
 * This class creates different logging file for the application, and logs the messages in those files.	 
 */
package logs;
import java.util.ArrayList;
import java.util.Enumeration;


import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.xml.DOMConfigurator;

import ZUG.Utility;


	public  class Log {
		
		public static ArrayList<String> HarnessLogFileList=null;
		String fullFilePath;
		public static boolean TurnOFFDebugLogs=false;		
		
		//Creates a static final instance of Log class
		private static final Log INSTANCE = new Log();
		
		//Creates a static final instance of Log class
		private static Logger log;	 
	
		/***
		 * Constructor for Log class 
		 */
		private  Log(){
			
			
			log = Logger.getLogger(Log.class);
			 HarnessLogFileList=new ArrayList<String>();
			//sets the path for log files
			//Append current date time in log file name.
			fullFilePath=System.getenv("APPDATA") + "\\ZUG Logs\\" + Utility.dateAsString();

			//gets current user directory's path
			String userDir=System.getProperty("user.dir");

			//gets log4j.properties file's path
			String logConfigPath= userDir+"/LogConfig/log4j.xml";
			
			//URL url = Loader.getResource(logConfigPath);
			DOMConfigurator.configure(logConfigPath);
			
			setFileName(fullFilePath);	   
	}
		 /***
		  * puts logging message in result log file 
		  */
		public static void Result(String message){
			log.info(message);
		}
	
		/***
		 * puts logging message in error and debug log file 
		 */
		public static void Error(String message){
			log.warn(message);
			System.out.println(message);
				 
		}
		public static void showError(String message){
			log.warn(message);
			System.out.println("\n############################################################################\n");
			System.out.println(message);
			System.out.println("\n############################################################################\n");
		}
		public static void ErrorInLog(String message){
			log.warn(message);
		}
			 
			 
		 /***
		  * puts logging message in debug log file 
		  */
		 public static void Debug(String message){
			 if(!TurnOFFDebugLogs)
				 log.debug(message);
		 }
			 
		 public static void Primitive(String message)
		 {
			 log.error(message);
		 }
	        /// Function to LOG the PrimitiveResults - for easy visibility to END USER.
		 public static void PrimitiveResults(String message)
		 {
			 log.fatal(message);
		 }

	        /// Function to LOG the PrimitiveErrors - for easy visibility to END USER.
        public static void PrimitiveErrors(String message)
        {
        	log.trace(message);
        }
	        
        public static void Cleanup()
        {
        	log.shutdown();
        }
		        
        /***
         * Sets the file name to the logger at run time.............. 
         */
		public static void setFileName(String logFileName){
			Enumeration<Appender> appenders = Logger.getRootLogger().getAllAppenders();
			RollingFileAppender fa = null;
			String harnessLogFile=null;
			while(appenders.hasMoreElements())
			{
				Appender currAppender = (Appender) appenders.nextElement();
					if(currAppender instanceof RollingFileAppender)
					{
							fa = (RollingFileAppender) currAppender;
					}
					
					if(fa != null)
					{
						harnessLogFile=null;
						if(fa.getName().equalsIgnoreCase("Debug")){
							harnessLogFile=logFileName+"-Debug.log";
							fa.setFile(logFileName+"-Debug.log");
						}
						else if(fa.getName().equalsIgnoreCase("Error")){
							harnessLogFile=logFileName+"-Error.log";
							fa.setFile(logFileName+"-Error.log");
							}
						else if(fa.getName().equalsIgnoreCase("Result")){
							harnessLogFile=logFileName+"-" +"Result.log";
							fa.setFile(logFileName+"-" +"Result.log");
							}
						else if(fa.getName().equalsIgnoreCase("PrimitiveResults")){
							harnessLogFile=logFileName+"-" +"PrimitiveResults.log";
							fa.setFile(logFileName+"-" +"PrimitiveResults.log");
						}
						else if(fa.getName().equalsIgnoreCase("Primitives")){
							harnessLogFile=logFileName+"-" +"Primitives.log";
							fa.setFile(logFileName+"-" +"Primitives.log");
						}
						if(harnessLogFile!=null)
							HarnessLogFileList.add(harnessLogFile);
						fa.activateOptions();
						}
					}
			}
}
