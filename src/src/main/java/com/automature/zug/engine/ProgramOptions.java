/***
 * ProgramOpitions.java
 * 		This is the basic Utility Class. It contains the functionality to read the Arguments.
 * 		Helper class that supports the configuration of program options
 * 		from command line arguments or console input 
 */
package com.automature.zug.engine;

//import Excel;

import com.automature.zug.util.Log;
import jline.console.ConsoleReader;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

//Internal Import

public class ProgramOptions {


	public  boolean verbose = true;
	boolean debugMode = true;
	public  boolean dbReporting = true;
	boolean compileMode = false;
	boolean verificationSwitching = true;
	boolean doCleanupOnTimeout = false;
	boolean forceExecution = false;
	public  boolean nyonserver = false; 
	public boolean includeFlag = true; 
	public  boolean isLongevityOn = false;
	public boolean ignore=false;
	int repeatCount = 1;
	boolean repeatDurationSpecified = false;
	int repeatDuration = 0;
	double repeatDurationLong = 0;
	String scriptLocation = StringUtils.EMPTY;
	String pwd = StringUtils.EMPTY;
	String includeMolecules = StringUtils.EMPTY;
	String manualTestCaseID = StringUtils.EMPTY;
	String excludeTestCaseID = StringUtils.EMPTY;
	String topologySetId = StringUtils.EMPTY;
	String inputFile = StringUtils.EMPTY;
	String TestPlanId = StringUtils.EMPTY;
	String TestPlanPath = StringUtils.EMPTY;
	String testCycleId = StringUtils.EMPTY;
	String TopologySetName = StringUtils.EMPTY;
	String BuildTag = StringUtils.EMPTY;
	String BuildId=StringUtils.EMPTY;
	String BuildNo = StringUtils.EMPTY;

	String buildName=StringUtils.EMPTY;
	String testPlanName=StringUtils.EMPTY;
	static boolean showTime=false;
	public static String CONTEXTVARRETRYCOUNT="5",CONTEXTVARRETRYTIMEOUT="1";
	boolean debugger=false;

	public static String filelocation = null;
	private Hashtable<String, String> _opts=new Hashtable<String, String>();
	public ArrayList<String> testCaseIds;
	protected static String currentPath, workingDirectory;
	private static final ArrayList<String> commandLineSwitchList=new ArrayList<String>(Arrays.asList("-testcaseid","-repeat","-norepeat","-autorecover","-noautorecover","-verbose","-debug","-nodebug","-verify","-noverify","-atompath","-include","-execute","-noexecute","-$","-$$","-testcycleid","-testplan","-testplanid","-topologyset","-topologysetid","-buildtag","-buildid","-macrofile","-macrocolumn","-logfilename","-help","-pwd","-version","-v","-?","--version","--v","-h","--help","/?","-retrycount","-retrytimeout","-testplanname","-buildname","-ignore","-atomexectime","-excludetestcaseid","-debugger"));
	/*
	 * Creates a new instance.
	 * 
	 * @param opts: A hashtable containing name/value pairs or switches
	 */

	ProgramOptions(){
		testCaseIds=new ArrayList<String>();
	}
	/*	ProgramOptions(Hashtable<String, String> opts) {
		this._opts = opts;
	}*/
	
	
	
	public static void checkCommandLineArgs(String...args) throws Exception
	{
		for(String arg:args)
		{
			if(arg.toLowerCase().startsWith("-"))
			{
				String argarr[]=arg.split("=");
				if(!commandLineSwitchList.contains(argarr[0].toLowerCase()))
				{
					if(!argarr[0].contains("-$")&&!argarr[0].contains("-$$")){
						throw new Exception("ProgramOptions/checkCommandLineArgs:: command line switch "+arg+" is not a valid switch. Please refer to Zug User Manual.");
					}	
				}
			}
		}

	}
	
	
	public String getTestPlanPath() {
		if(TestPlanPath==null)
			return StringUtils.EMPTY;
		else
			return TestPlanPath;
	}



	public String getTopologySetName() {
		if(TopologySetName==null)
			return StringUtils.EMPTY;
		else
			return TopologySetName;
	}



	public String getTopologySetId() {
		if(topologySetId==null)
			return StringUtils.EMPTY;
		else
			return topologySetId;
	}



	public String getTestPlanId() {
		if(TestPlanId==null)
			return StringUtils.EMPTY;
		else
			return TestPlanId;
	}



	public String getTestCycleId() {
		if(testCycleId==null)
			return StringUtils.EMPTY;
		else
			return testCycleId;
	}



	public String getBuildTag() {
		if(BuildTag==null)
			return StringUtils.EMPTY;
		else
			return BuildTag;
	}



	public String getBuildId() {
		if(BuildId==null)
			return StringUtils.EMPTY;
		else
			return BuildId;
	}



	public String getBuildNo() {
		if(BuildNo==null)
			return StringUtils.EMPTY;
		else
			return BuildNo;
	}



	public String getBuildName() {
		if(buildName==null)
			return StringUtils.EMPTY;
		else
			return buildName;
	}



	public String getTestPlanName() {
		if(testPlanName==null)
			return StringUtils.EMPTY;
		else
			return testPlanName;
	}



	public static void populateMacroColumnValue(String str){
		String fileArr[] = str.split(",");
		for(String temp:fileArr){
			String value[]=temp.split(":");
			if(value.length!=2){
				Log.Error("\n"+temp
						+ "->  Contains More Than One ':' .This file's default macro values will be used");
				continue;
			}
			String filename=value[0];
			String columnValue=value[1];
			String nameSpace=null;
			if (filename.endsWith(".xls")) {
				filename = filename.replaceAll(".xls", "");
			}
			nameSpace= filename.toLowerCase();
			Controller.macroColumnValue.put(nameSpace, columnValue);
		}    	
	}

	/**
	 * Parses args into a table of switches or name/value pairs.
	 * Each String in args must be of the form 'name=value' or
	 * 'name'.  If value contains embedded whitespace, it must be
	 * enclosed in single or double quotes.  Values containing an
	 * embedded equals sign are not supported.
	 * 
	 * @param args array of switches and/or name/value pairs
	 * @return ProgramOptions Object
	 * @throws Exception On occurrence of any error.
	 */

	public void parse(String[] args) throws Exception,Throwable {

		Hashtable<String, String> ht = new Hashtable<String, String>();
		 for (String opt : args) {
			 int indexOfDash = opt.indexOf("-");
			 String[] nv = new String[2];
			 if (indexOfDash == 0) {
				 int indexOfEqual = opt.indexOf("=");
				 if (indexOfEqual == -1) {
					 // this block is for switch options without equal sign like -Repeat
					 nv[0] = opt.substring(1).toLowerCase();
					 nv[1] = "true";
				 } else {
					 // this block is for name=value options with equal sign like -Count=12
					 nv[0] = opt.substring(1, indexOfEqual).toLowerCase();
					 nv[1] = opt.substring(indexOfEqual + 1);

					 if (opt.contains("pwd")) {
						 workingDirectory = opt.substring(indexOfEqual + 1);
						 //System.out.println("Working D\t"+workingDirectory);
					 }


				 }
			 } else if (indexOfDash == -1 || indexOfDash != 0) {
				 // this block is for option without Dash '-' . for inputfile

				 if (ht.containsKey("inputfile")) {
					 Log.Error("ProgramOptions/parse: Error : Repeated input file.");
					 System.out.println("\n\nRedundant value : Input File "
							 + "\n Use -help for Usage information\n\n");
					 Log.Debug("ProgramOptions/parse: Error : Repeated input file. Program exiting");
					 throw new Throwable();

				 }
				 nv[0] = "inputfile";
				 if (opt.contains(":") || opt.startsWith("/")) //Checks for the Absolute path of the input file
				 {


					 filelocation = "";
					 nv[1] = opt;
					 String tempStrings[] = opt.split("\\\\");
					 for (String divs : tempStrings) {
						 if (!divs.contains(".xls")) {
							 filelocation += divs + "\\";
						 }

					 }

				 } else {
					 for (String str : args) //Chekching the whole input arguments again in a for loop
					 {
						 int indexofEq = str.indexOf("=");
						 if (str.contains("pwd")) //Checking if it contains any -pwd= switch or not
						 {
							 currentPath = str.substring(indexofEq + 1);
							 //System.out.println("This the Pats\t"+currentPath);
							 nv[1] = currentPath.replaceAll("\\\\", "/") + "/" + opt; //Replacing the Current working Directory path provided by the batch file
						 }
					 }
					 //System.out.println("The Path \t"+nv[1]);
					 Log.Debug("Command Line Path Showing the Current Directory:\t" + nv[1]);
				 }
			 }
			
			 if(opt.toLowerCase().contains("-macrocolumn")){
				 String temp[]=opt.split("=");
				 if(temp.length==2){
					 populateMacroColumnValue(temp[1]);
				 }
				 else{
					 Log.Error("\n"+opt
							 + "-> The Value  Contains More Than One '=' .Program will skip the macro value column switch feature");
					 //	System.out.println("The Value  Contains More Than One '='.Program will skip the macro value column switch feature");
				 }
				 ht.put(nv[0], nv[1].trim().replaceAll("\"", "").replaceAll("'", "").trim());
			 }
			 if (opt.toLowerCase().contains("-macrofile")) {
				 Controller.macroentry = true;
				 String file[] = opt.split("=", 2);
				 String macro, namespaces = "";

				 if(!file[1].substring(file[1].lastIndexOf(".")+1).trim().equalsIgnoreCase("txt")){
					 System.out.println(file[1]+" is not a text file");
					 continue;
				 }
				 Log.Debug("\nExternal macro file : "+file[1]);
				 System.out.println("\nExternal macro file:"+file[1]);
				 try {
					 File macrofile = new File(file[1]);
					 if(macrofile==null ||!macrofile.exists()){
						 Log.Error("Could not found the macro file specified");
					 }
					 BufferedReader br = new BufferedReader(new FileReader(
							 macrofile));
					 //String line;
					 String fileArr[] = null;
					 String tempPath = ht.get("inputfile").replaceAll(
							 "\\\\", "/");

					 fileArr = tempPath.split("/");
					 for (String filename : fileArr) {

						 if (filename.endsWith(".xls")) {

							 filename = filename.replaceAll(".xls", "");
							 namespaces = filename.toLowerCase();
							 // System.out.println("Namespace "+namespaces);
							 Log.Debug(String.format(
									 "The Namespace is Created  %s",
									 namespaces));
							 // macrokey+=filename.toLowerCase();
						 }

					 }
					 while ((macro = br.readLine()) != null) {
						 macro = macro.trim();
						 if(!macro.startsWith("$")){
							 Log.Error(macro+" is not a macro.Program will skip this macro's substitution");
							 continue;
						 }
						 String temp[] = macro.split("=");
						 if (temp.length == 2) {
							 String macroKey = temp[0];
							 String macroValue = temp[1];
						//	 Excel ee = new Excel();
							 macroKey = Controller.readExcel.AppendNamespace(macroKey, namespaces);
							 if ((macroValue.contains("$$") || macroValue
									 .contains("$"))
									 && (macroValue.startsWith("{") && macroValue
											 .endsWith("}"))) {
								 macroValue = macroValue;
							 } else {
								 macroValue = Controller.readExcel.ExpandMacrosValue(macroValue,
										 macroKey, namespaces);
							 }
							 // ht.put(macrokey, macrovalue);
							 // Creating the command line Macro hashmap
							 Controller.macrocommandlineinputs.put(macroKey,
									 macroValue);
							 // System.out.println("The Command line changing "+Controller.macrocommandlineinputs);

						 } else {
							 // controller.message(macro+"-->The Value Assigned contains more than one =");
							 Log.Error(macro
									 + "->The Value Assigned Contains More Than One '=' ");
						 }
					 }

				 } catch (Exception e) {
					 Log.Error("Error in macro file parsing from command line argument");
				 }
				 ht.put(nv[0], nv[1].trim().replaceAll("\"", "").replaceAll("'", "").trim());
			 }
			 if (opt.contains("-$") || opt.contains("-$$")) {
				 Controller.macroentry = true;
				 //controller.message("Macro Command Line Arguments\t"+macroentry);
				 //System.out.println("Macro Command Line Arguments\t "+Controller.macroentry+"\n\t"+opt);
				 String macro, macrokey = "$", macrovalue, namespaces = "";
				 macro = opt.replace("-$", "");
				 //macro=macro.replace("$", "");
				 String fileArr[] = null;
				
				 String tempPath = ht.get("inputfile").replaceAll("\\\\", "/");


				 fileArr = tempPath.split("/");
				 for (String filename : fileArr) {

					 if (filename.endsWith(".xls")) {

						 filename = filename.replaceAll(".xls", "");
						 namespaces = filename.toLowerCase();
						 //System.out.println("Namespace "+namespaces);
						 Log.Debug(String.format("The Namespace is Created  %s", namespaces));
						 //macrokey+=filename.toLowerCase();
					 }

				 }

				 //controller.message("Macro Value is\t"+macro);
				 String temp[] = null;
				 temp = macro.split("=");
				 if (temp.length == 2) {
					 macrokey += temp[0];
					 macrovalue = temp[1];
					 //System.out.println("The MacroKey is "+macrokey+" The MacroValue is "+macrovalue);
					 //macrocommandlineinputs.put(macrokey, macrovalue);

					// Excel ee = new Excel();
					 macrokey = Controller.readExcel.AppendNamespace(macrokey, namespaces);
					 if ((macrovalue.contains("$$") || macrovalue.contains("$")) && (macrovalue.startsWith("{") && macrovalue.endsWith("}"))) {
						 macrovalue = macrovalue;
					 } else {
						 macrovalue = Controller.readExcel.ExpandMacrosValue(macrovalue, macrokey, namespaces);
					 }
					 //ht.put(macrokey, macrovalue);
					 //Creating the command line Macro hashmap 
					 Controller.macrocommandlineinputs.put(macrokey, macrovalue);
					 //System.out.println("The Command line changing "+Controller.macrocommandlineinputs);

				 } else {
					 //controller.message(macro+"-->The Value Assigned contains more than one =");
					 Log.Error(macro + "->The Value Assigned Contains More Than One '=' ");
				 }
			 }
			 ht.put(nv[0], nv[1].trim().replaceAll("\"", "").replaceAll("'", "").trim());
		 }
		 //System.out.println("The File Path\t"+ht.get("inputfile")+"\n"+"The Total Hast Table\n"+ht);
		 if (StringUtils.isNotBlank(workingDirectory)) {
			 workingDirectory=workingDirectory.replaceAll("\"","").trim() ;
			 ContextVar.setContextVar("ZUG_PWD", workingDirectory);
			 //ContextVar.setContextVar("ZUG_LOGFILENAME",Controller.ZUG_LOGFILENAME);
			 //System.out.println("The working dir "+ContextVar.getContextVar("ZUG_PWD"));
		 } 
		 _opts.putAll(ht);
		// System.out.println(_opts.toString());
	}

	/**
	 * Prompts the user for sensitive information and prevents typed
	 * characters from being displayed on the screen
	 * Pressing the Enter key will terminate input and return 
	 * all characters typed up to the point where the Enter key was pressed.
	 * Pressing the Escape key will discard all input.
	 * 
	 * @param prompt: Descriptive text to be displayed on the console prior to accepting keyboard input.
	 */
	public static String promptForPassword(String prompt) throws Exception {

		ConsoleReader reader = new ConsoleReader();
		String pwd = reader.readLine(prompt + " : ", '*');

		if (StringUtils.isNotBlank(pwd)) {
			return pwd;
		}
		return null;
	}

	/**
	 * Check if Hash table contains the specified key.
	 *@return true if the specified option is set else false.
	 */
	public boolean isSet(String opt) {
		return _opts.containsKey(opt.toLowerCase());
	}

	/**
	 * Check if string is null or empty.
	 * @return true if the Value is Null or Empty, else False.
	 */
	public static boolean isNullOrEmpty(String value) {
		if (value != null && value.trim().length() != 0) {
			return false;
		}

		return true;
	}

	/**
	 * @return Gets the current value for the specified option 
	 * 	the specified default if the option is not set
	 */
	public String getString(String opt, String dflt) throws Exception {
		String val;
		if (isSet(opt.toLowerCase())) {
			val = (String) _opts.get(opt.toLowerCase());
		//	System.out.println("opt:"+opt+"\toptval"+val);
			if (!isNullOrEmpty(val)) {
				return val;
			}
		}

		return dflt;
	}

	/**
	 * Returns true if Help option is specified in the argument; False otherwise.
	 */
	public boolean isHelpRequest() {
		return isSet("help")
				|| isSet("-help")
				|| isSet("--help")
				|| isSet("-h")
				|| isSet("h")
				|| isSet("--h")
				|| isSet("?")
				|| isSet("-?")
				|| isSet("/?");
	}

	/**
	 * Check if the user is asking for version of tool.
	 * @return true - If the option contains string such as version,-version,--version,-v,--v
	 */
	public boolean isVersionRequest() {
		return isSet("version")
				|| isSet("-version")
				|| isSet("--version")
				|| isSet("-v")
				|| isSet("v")
				|| isSet("--v");
	}

	/**
	 * Check if the user is asking for Configuration of tool
	 * @return true - if the option contains String such as -showconfig,--showconfig,-config,--config,showconfig,config
	 *
	 */
	public boolean isConfigRequest() {
		return isSet("showconfig") || isSet("config") || isSet("-showconfig") || isSet("--showconfig") || isSet("-config") || isSet("--config");
	}

	public boolean GetOptions() {
		Log.Debug("Controller/GetOptions: Start of function Controller/GetOptions");

		String debugModeVal = StringUtils.EMPTY;
		String doCleanupOnTimeoutVal = StringUtils.EMPTY;
		String repeatDurationVal = StringUtils.EMPTY;
		String compileModeFlagVal = null;
		String verboseFlagVal = null;

		boolean testplanidVal = false;
		boolean testplanpathVal = false;
		boolean topologysetXMLVal = false;
		boolean topologysetid = false;
		boolean topologysetname = false;
		boolean testcycleidflag=false;
		try {
			if((CONTEXTVARRETRYCOUNT=this.getString("retrycount", null))==null)
			{
				Log.Debug("Controller/GetOptions: -retrycount is not specified. By Default its value is 5.");
			}
			else
			{
				if((CONTEXTVARRETRYTIMEOUT=this.getString("retrytimeout", null))==null)
				{
					Log.Error("Controller/GetOptions: Invalid Options Specified.... retrycount must have retrytimeout");
					Log.Error("Controller/GetOptions: Use -help for usage option.");
					return false;
				}
				
			}
			if((CONTEXTVARRETRYTIMEOUT=this.getString("retrytimeout", null))==null)
			{
				Log.Debug("Controller/GetOptions: -retrytimeout is not specified. By Default its value is 1 sec.");
			}
			else
			{
				if((CONTEXTVARRETRYCOUNT=this.getString("retrycount", null))==null)
				{
					Log.Error("Controller/GetOptions: Invalid Options Specified.... retrytimeout must have retrycount ");
					Log.Error("Controller/GetOptions: Use -help for usage option.");
					return false;
				}
				
			}
			// check if debug mode is set to true. else default value is True
			if ((debugModeVal = this.getString("debug", null)) == "true") {
				Log.Debug("Controller/GetOptions: Debug switch Mode is specified.");
				Log.Debug("Controller/GetOptions: DebugModeVal = true");
				debugMode = true;

			} else if ((debugModeVal = this.getString("nodebug", null)) == "true") {
				debugMode = false;
				Log.Debug("Controller/GetOptions: debugModeVal = " + "false");
			} else {
				Log.Debug("Controller/GetOptions: Debug switch Mode is not specified.");
				Log.Debug("Controller/GetOptions: Debug Mode is false by default");
				// Lets make it true by default
				debugMode = false;

			}

			// check if doCleanupOnTimeout is set to true. else default value is
			// False
			if ((doCleanupOnTimeoutVal = this.getString("noautorecover", null)) == "true") {
				Log.Debug("Controller/GetOptions: NoAutoRecover switch is specified.");
				Log.Debug("Controller/GetOptions: doCleanupOnTimeout = false");
				// Lets make it FALSE by default
				doCleanupOnTimeout = false;
			} else if ((doCleanupOnTimeoutVal = this.getString("autorecover",
					null)) == "true") {
				Log.Debug("Controller/GetOptions: AutoRecover swithch is specified.");
				Log.Debug("Controller/GetOptions: doCleanupOnTimeout = true");
				doCleanupOnTimeout = true;
			} else {
				Log.Debug("Controller/GetOptions: AutoRecover switch is not specified. ");
				Log.Debug("Controller/GetOptions: doCleanupOnTimeout = true (by default)");
				doCleanupOnTimeout = true;
			}

			// check if repeat duration is specified.
			if ((repeatDurationVal = this.getString("Duration", null)) == null) {
				Log.Debug("Controller/GetOptions: -Duration is not specified. By Default its value is Empty.");

				repeatDurationSpecified = false;
			} else {
				if ((this.getString("repeat", null) == null)) // ||(opts.getString("norepeat",
					// null) ==
					// null))
				{
					Log.Error("Controller/GetOptions: Invalid Options Specified....");
					Log.Error("Controller/GetOptions: Use -help for usage option.");
					return false;
				}
				if (repeatDurationVal.length() < 2) {
					repeatDurationSpecified = false;
					Log.Error("Controller/GetOptions: Invalid Value Specified for -Duration. Check Help.");
					return false;
				}

				// Get the last Character -
				char lastChar = repeatDurationVal.charAt(repeatDurationVal
						.length() - 1);

				if (lastChar == 'd' || lastChar == 'D') {
					// Then convert days to time.
					try {
						repeatDuration = Integer.parseInt(repeatDurationVal
								.substring(0, repeatDurationVal.length() - 1));
					} catch (Exception e) {
						repeatDurationSpecified = false;
						Log.Error("Controller/GetOptions: Invalid Value Specified for -Duration. Check Help.");
						return false;
					}

					// Convert the Days to MilliSeconds.
					repeatDurationLong = repeatDuration * 24 * 60 * 60 * 1000;
				} else if (lastChar == 'h' || lastChar == 'H') {
					// Then convert days to time.
					try {
						repeatDuration = Integer.parseInt(repeatDurationVal
								.substring(0, repeatDurationVal.length() - 1));
					} catch (Exception e) {
						repeatDurationSpecified = false;
						Log.Error("Controller/GetOptions: Invalid Value Specified for -Duration. Check Help.");
						return false;
					}

					// Convert the Hours to MilliSeconds.
					repeatDurationLong = repeatDuration * 60 * 60 * 1000;
				} else if (lastChar == 'm' || lastChar == 'M') {
					// Then convert Minutes to time.
					try {
						repeatDuration = Integer.parseInt(repeatDurationVal
								.substring(0, repeatDurationVal.length() - 1));
						repeatDurationLong = repeatDuration * 60 * 1000;

					} catch (Exception e) {
						repeatDurationSpecified = false;
						Log.Error("Controller/GetOptions: Invalid Value Specified for -Duration. Check Help.");
						return false;
					}

					// Convert the Minutes to MilliSeconds.

				} else if (lastChar == 's' || lastChar == 'S') {
					// Then convert Seconds to time.
					try {
						repeatDuration = Integer.parseInt(repeatDurationVal
								.substring(0, repeatDurationVal.length() - 1));
					} catch (Exception e) {
						repeatDurationSpecified = false;
						Log.Error("Controller/GetOptions: Invalid Value Specified for -Duration. Check Help.");
						return false;
					}

					// Convert the Seconds to MilliSeconds.
					repeatDurationLong = repeatDuration * 1000;
				} else {
					// This format is not understood by the ZUG, then throw an
					// Exception..
					repeatDurationSpecified = false;
					Log.Error("Controller/GetOptions: Invalid Value Specified for -Duration. Check Help.");
					return false;
				}

				repeatDurationSpecified = true;
				Log.Debug("Controller/GetOptions: -Duration(In Milli-Seconds) = "
						+ repeatDurationLong);
			}

			String repeatCountVal = StringUtils.EMPTY;

			if ((repeatCountVal = this.getString("Count", null)) == null) {
				Log.Debug("Controller/GetOptions: -Count is not specified. By Default its value is 1.");

				// Lets make it true by default
				repeatCount = 1;

			} else {
				if ((this.getString("repeat", null) == null)) // ||(opts.getString("norepeat",
					// null) ==
					// null)
				{
					Log.Error("Controller/GetOptions: Invalid Options Specified....");
					Log.Error("Controller/GetOptions: Use -help for usage option.");
					return false;
				}
				repeatCount = Integer.parseInt(repeatCountVal);

				// RepeatCount will take precedence over repeatDuration. So if
				// both are specified, then we need
				// to ignore repeatDuration and only take repeatCount.
				repeatDurationSpecified = false;
				// message("Repeat Count value" + repeatCount);
				Log.Debug("Controller/GetOptions: repeatCount = " + repeatCount);
			}

			/*
			 * if ((testsToRepeat = opts.getString("TestsToRepeat", null)) ==
			 * null) { Log.Debug(
			 * "Controller/GetOptions: reptestsToRepeateatCount is not specified. By Default its value is Empty."
			 * );
			 * 
			 * // Lets make it true by default testsToRepeat =
			 * StringUtils.EMPTY; } else {
			 * Log.Debug("Controller/GetOptions: testsToRepeat = " +
			 * testsToRepeat); }
			 */

			if ((verboseFlagVal = this.getString("verbose", null)) == "true") {
				Log.Debug("Controller/GetOptions: Verbose Flag is = true");
				verbose = true;
			} else if ((verboseFlagVal = this.getString("noverbose", null)) == "true") {
				verbose = false;
				Log.Debug("Controller/GetOptions: Verbose Flag is = false");
			} else {
				// By default Verbose option is OFF and set to FALSE, unless
				// explicitly switched ON

				Log.Debug("Controller/GetOptions: Verbose flag is = false by default");

				verbose = false;

			}

			if ((compileModeFlagVal = this.getString("execute", null)) == "true") {
				Log.Debug("Controller/GetOptions: execute switch found. Compile Mode = false");
				compileMode = false;
			} else if ((compileModeFlagVal = this.getString("noexecute", null)) == "true") {
				compileMode = Boolean.parseBoolean(compileModeFlagVal);
				Log.Debug("Controller/GetOptions: checkSyntax Flag is = "
						+ compileModeFlagVal);
			} else {
				// By default checkSyntax option is OFF and set to FALSE, unless
				// explicitly switched OFF
				Log.Debug("Controller/Getoptions: Execution flag is = true by default");
				compileMode = false;
			}

			String dbReportingVal = null;
			if ((dbReportingVal = this.getString("dbreporting", null)) == null) {
				if (compileMode) {
					Log.Debug("Controller/GetOptions: dbReportingVal is OFF as CompileMode is ON - Check Syntax mode is true.");
					dbReporting = false;

				} else {
					// By default DBReporting option is ON and set to TRUE,
					// unless explicitly switched OFF
					Log.Debug("Controller/GetOptions: dbReportingVal ON.");
					dbReporting = true;
				}
			} else {
				if (compileMode) {
					Log.Debug("Controller/GetOptions: dbReportingVal is OFF as CompileMode is ON - Check Syntax mode is true.");
					dbReporting = false;
				} else {
					dbReporting = Boolean.parseBoolean(dbReportingVal);

					Log.Debug("Controller/GetOptions: dbReportingVal Flag is = "
							+ dbReportingVal);
				}
			}

			String verificationSwitchingVal = StringUtils.EMPTY;
			if ((verificationSwitchingVal = this.getString("verify", null)) == "true") {
				// verificationSwitching is ON and set to TRUE, unless
				// explicitly switched OFF
				Log.Debug("Controller/GetOptions: verify Switch Flag is = ON.");
				verificationSwitching = true;
			} else if ((verificationSwitchingVal = this.getString("noverify",
					null)) == "true") {
				verificationSwitching = false;
				Log.Debug("Controller/GetOptions: verificationSwitching Flag is = "
						+ verificationSwitchingVal);
			} else {
				// By Default verificationSwitching is ON and set to TRUE,
				// unless explicitly switched OFF
				Log.Debug("Controller/GetOptions: verify Switch Flag is = ON.");
				verificationSwitching = true;
			}

			if ((scriptLocation = this.getString("atompath", null)) == null) {
				Log.Debug("Controller/GetOptions: Atom Scripts is not specified.");
				scriptLocation = StringUtils.EMPTY;
			} else {
				Log.Debug("Controller/GetOptions: Atom Scripts specified by user is = "
						+ scriptLocation);
			}

			if ((includeMolecules = this.getString("include", null)) == null) {
				// System.out.println("This is the Options\t"+includeMolecules);
				Log.Debug("Controller/GetOptions: Include Molecule file specified = "
						+ includeMolecules);
				includeMolecules = StringUtils.EMPTY;
			} else {
				includeFlag = false;
				// System.out.println("This is the Options\t"+includeMolecules);
				Log.Debug("Controller/Getoptions: Include Molecule specified : "
						+ includeMolecules);
			}
			if ((pwd = this.getString("pwd", null)) == null) {
				Log.Debug("Controller/Getoptions: Working Directory specified : "
						+ pwd);

				pwd = StringUtils.EMPTY;
			} else {

				Log.Debug("Controller/Getoptions: Present Working Directory : "
						+ pwd);
			}
			if ((manualTestCaseID = this.getString("testcaseID", null)) == null) {
				Log.Debug("Controller/GetOptions: ManualTestCase ID is not specified.");
				manualTestCaseID = StringUtils.EMPTY;
				excludeTestCaseID = this.getString("excludeTestCaseID", null);
				// message("No specific testcase is invoked\n All testcases will be executed.");
			} else {
				// message("The manual Testcase "+manualTestCaseID);
				if (manualTestCaseID.contains(".")
						|| manualTestCaseID.contains("/")
						|| manualTestCaseID.contains(":")
						|| manualTestCaseID.contains(";")) {
					// message("it contains . "+manualTestCaseID);
					Log.Error("[Error]Multiple testcaseid must separate by , (comma) wrong inputs: "
							+ manualTestCaseID);
					return false;
				}
				String []testCaseIDs=manualTestCaseID.split(",");
				for(String tcID:testCaseIDs){
					testCaseIds.add(tcID.trim().toLowerCase());
				}
				Log.Debug("Controller/GetOptions: ManualTestCase ID specified by user is = "
						+ manualTestCaseID);
				if ((excludeTestCaseID = this.getString("excludetestcaseID",
						null)) == null) {
					excludeTestCaseID = StringUtils.EMPTY;
				}
			}

			if ((inputFile = this.getString("inputfile", null)) == null) {
				Log.Error("Controller/GetOptions: Error : missing input file.");
				System.out.println("\n\nMissing required value : Input File "
						+ "\n Use -help for Usage information\n\n");
				Log.Debug("Controller/GetOptions: Function returns FALSE. End of Function.");
				return false;
			} else {
				Log.Debug("Controller/GetOptions: input = " + inputFile);
			}

			if ((testCycleId = this.getString("testcycleid", null)) == null) {
				Log.Debug("Controller/GetOptions: TestCYcle ID specified from Command Prompt is NULL/Empty.");
			} else {
				// Harness Specific ContextVariable to store TestCycle ID
				ContextVar.setContextVar("ZUG_TCYCID", testCycleId);
				testcycleidflag=true;
				if(this.getString("topologysetid", null)==null)
				{
					Log.Error("Controller/GetOptions: Error : missing Topology Set Id.");
					System.out
					.println("\n\nMissing required value : Topology Set Id. With testcycleid topologysetid is mandatory."
							+ "\n Use -help/-h for Usage information\n\n");
					Log.Debug("Controller/GetOptions: Function returns FALSE. End of Function.");
					return false;
				}
				else{
					dbReporting=true;
					Log.Debug("Controller/GetOptions: testcycleid = " + testCycleId);
				}

			}
			
		


			/*
			 * dbreporting option argument has been removed and implicitly set
			 * by testplanid and topologysetXML values
			 */
			// if (dbReporting)
			// {

			dbReporting = false;

			if ((TestPlanId = this.getString("testplanid", null)) == null) {
				// TODO here put checking for testplanid=product:sprint:
				/*
				 * Log.Error(
				 * "Controller/GetOptions: Error : missing TestPlan ID(Release regression Test Plan ID)."
				 * );
				 * System.out.println("\n\nMissing required value : Test Plan ID "
				 * + "\n Use -help/-h for Usage information\n\n"); Log.Debug(
				 * "Controller/GetOptions: Function returns FALSE. End of Function."
				 * ); return false;
				 */
				testplanidVal = false;
				Log.Debug("Controller/GetOptions: testplanid not specified. dbreporting os OFF");
			} else {
				// Harness Specific ContextVariable to store TESTPLAN
				ContextVar.setContextVar("ZUG_TESTPLANID", TestPlanId);
				Log.Debug("Controller/GetOptions: TestPlanId = " + TestPlanId);
				testplanidVal = true;
			}

			if ((TestPlanPath = this.getString("testplan", null)) == null) {
				testplanpathVal = false;
				Log.Debug("Controller/GetOptions: testplanpath not specified. dbreporting os OFF");
			} else {
				ContextVar.setContextVar("ZUG_TESTPLANPATH", TestPlanPath);
				Log.Debug("Controller/GetOptions: TestPlanId = " + TestPlanPath);
				// message("The testplanpath is "+TestPlanPath);
				// message("ContextVar set "+ContextVar.getContextVar("ZUG_TESTPLANPATH"));
				testplanpathVal = true;
			}
			if ((topologySetId = this.getString("topologysetid", null)) == null) {
				boolean topologysetnameexists = false;
				if (StringUtils.isNotBlank(this.getString("topologyset", null))) {
					topologysetnameexists = true;
				}
				if (testplanidVal && !topologysetnameexists) {
					Log.Error("Controller/GetOptions: Error : missing Topology Set Id.");
					System.out
					.println("\n\nMissing required value : Topology Set Id "
							+ "\n Use -help/-h for Usage information\n\n");
					Log.Debug("Controller/GetOptions: Function returns FALSE. End of Function.");
					return false;
				}
			} else {

				Log.Debug("Controller/GetOptions: topologySetId = "
						+ topologySetId);
				//if (testplanidVal || testplanpathVal) {// put or checking for
				if(testplanidVal){
					// the new value
					// testplanpathvalue=true
					if (compileMode) {
						Log.Debug("Controller/GetOptions: dbReportingVal is OFF as CompileMode is ON - Check Syntax mode is true.");
						dbReporting = false;
					} else {
						if (topologysetname) {
							Log.Error("Controller/GetOptions: Error: Topology name is already used.");
							System.out
							.println("\n\nToplogy name is already used. "
									+ "\n Use -help/-h for Usage Information\n\n");
							Log.Debug("Controller/GetOptions: Function returns FALSE. End of Function.");
							return false;
						}
						// dbReporting option is ON as both testplanID and
						// topologysetXML is specified
						Log.Debug("Controller/GetOptions: dbReportingVal ON.");
						topologysetid = true;
						dbReporting = true;
					}
				} else if (testcycleidflag){
					if(compileMode)
					{
						Log.Debug("Controller/GetOptions: dbReportingVal is OFF as CompileMode is ON - Check Syntax mode is true.");
						dbReporting = false;
					}
					else
					{
						if(topologysetname)
						{
							Log.Error("Controller/GetOptions: Error: Toplogy name is already used.");
							System.out
							.println("\n\nToplogy name is already used. "
									+ "\n Use -help/-h for Usage Information\n\n");
							Log.Debug("Controller/GetOptions: Function returns FALSE. End of Function.");
							return false;
						}
						Log.Debug("Controller/GetOptions: dbReportingVal ON.");
						topologysetid = true;
						dbReporting = true;
					}

				}
				else {
					Log.Error("Controller/GetOptions: Error: missing testplanid or testcycleid.");
					System.out
					.println("\n\nMissing required value: testplanid or testcycleid. With topologysetid only testplanid or testcycleid can be used."
							+ "\n Use -help/-h for Usage Information\n\n");
					Log.Debug("Controller/GetOptions: Function returns FALSE. End of Function.");
					return false;
				}
			}
			if ((TopologySetName = this.getString("topologyset", null)) == null) {
				// message("The topologyset is null "+TopologySetName);
				// message("Toplogy id? "+topologysetid);
				if (testplanpathVal && !topologysetid) {
					Log.Error("Controller/GetOptions: Error : missing Topology Set Name.");
					System.out
					.println("\n\nMissing required value : topologyset"
							+ "\n Use -help/-h for Usage information\n\n");
					Log.Debug("Controller/GetOptions: Function returns FALSE. End of Function.");
					return false;
				}

			} else {
				Log.Debug("Controller/GetOptions: topologySet = "
						+ TopologySetName);
				// message("The topologyset is "+TopologySetName);
				//if (testplanidVal || testplanpathVal) {// put or checking for
				if(testplanpathVal){
					// the new value
					// testplanpathvalue=true
					if (compileMode) {
						Log.Debug("Controller/GetOptions: dbReportingVal is OFF as CompileMode is ON - Check Syntax mode is true.");
						dbReporting = false;
					} else {
						if (topologysetid) {
							Log.Error("Controller/GetOptions: Error: Toplogy ID is already used.");
							System.out
							.println("\n\nToplogy ID is already used. "
									+ "\n Use -help/-h for Usage Information\n\n");
							Log.Debug("Controller/GetOptions: Function returns FALSE. End of Function.");
							return false;
						}
						// dbReporting option is ON as both testplanID and
						// topologysetXML is specified
						Log.Debug("Controller/GetOptions: dbReportingVal ON.");
						topologysetname = true;
						dbReporting = true;
					}
				} else {
					Log.Error("Controller/GetOptions: Error: missing testplan.");
					System.out
					.println("\n\nMissing required value: testplan. With topologyset only testplan can be used."
							+ "\n Use -help/-h for Usage Information\n\n");
					Log.Debug("Controller/GetOptions: Function returns FALSE. End of Function.");
					return false;
				}
			}

			if ((BuildTag = this.getString("buildtag", null)) == null) {

				Log.Debug("Controller/GetOptions: buildtag not specified. dbreporting os OFF");
			} else {
				if(testplanpathVal && topologysetname)
				{ContextVar.setContextVar("ZUG_BUILDTAG", BuildTag);
				Log.Debug("Controller/GetOptions: BuildTag = " + BuildTag);}
				else{
					Log.Error("\nController/GetOptions: Error: missing testplan and topologyset.");
					System.out
					.println("\n\nMissing required value: testplan and topologyset. With buildtag both testplan and topologyset need to be used."
							+ "\n Use -help/-h for Usage Information\n\n");
					Log.Debug("Controller/GetOptions: Function returns FALSE. End of Function.");
					return false;
				}
				// message("The testplanpath is "+TestPlanPath
				// message("ContextVar set "+ContextVar.getContextVar("ZUG_TESTPLANPATH"));

			}
			if((BuildId=this.getString("buildid",null))==null)
			{
				Log.Debug("Controller/GetOptions: buildid not specified. dbreporting os OFF");

			}
			else{
				if(testplanidVal&&topologysetid)
				{ContextVar.setContextVar("ZUG_BUILDID", BuildId);
				Log.Debug("Controller/GetOptions: BuildId = " + BuildId);
				}
				else{
					Log.Error("\nController/GetOptions: Error: missing testplanid and topologysetid.");
					System.out
					.println("\n\nMissing required value: testplanid and topologysetid. With buildid both testplanid and topologysetid need to be used."
							+ "\n Use -help/-h for Usage Information\n\n");
					Log.Debug("Controller/GetOptions: Function returns FALSE. End of Function.");
					return false;
				}
			}	
			if((testPlanName=this.getString("testplanname", null))!=null ){//|| testPlanName!=null) {
				Log.Debug("Controller/GetOptions: test plan name = "+testPlanName);
				dbReporting=true;
			}
			
			if((this.buildName=this.getString("buildname", null))==null){
				buildName=StringUtils.EMPTY;
			}
			if((this.getString("ignore", null))!=null){
				this.ignore=true;
			}
			if((this.getString("atomexectime", null))!=null){
				this.showTime=true;
			}
			if(this.getString("debugger", null)!=null){
				this.debugger=true;
			}

		} catch (Exception e) {
			Log.Debug("Controller/GetOptions: Exception occured,"
					+ e.getMessage());
			e.printStackTrace();
		}

		return true;
	}

}
