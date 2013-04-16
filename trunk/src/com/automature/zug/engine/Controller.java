/***
,. * Controller.java
 *    This is the Controller class which executes the Test Cases for the Automation 
 */
package com.automature.zug.engine;

import com.automature.zug.exceptions.MoleculeDefinitionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.extractor.ExcelExtractor;

import com.automature.zug.reporter.DavosReporter;
import com.automature.zug.reporter.Reporter;
import com.automature.zug.reporter.TestLinkReporter;
//Internal package imports
import com.automature.zug.util.Log;
import com.automature.zug.util.Utility;
import com.automature.zug.util.ZipUtility;
import com.automature.zug.businesslogics.TestCaseResult;
import com.automature.zug.util.ExtensionInterpreterSupport;
import com.automature.davos.engine.DavosClient;
import com.automature.davos.exceptions.DavosExecutionException;

//import DatabaseLayer.Testplan;
public class Controller extends Thread {
	// Variables for checking Program options
	private SysEnv sysenv=new SysEnv();
	private String helpMessage = StringUtils.EMPTY;
	private String versionMessage = StringUtils.EMPTY;
	static Excel readExcel;

	public static int initializationTime = 0;
	private int executionTime = 0;
	public static long harnessPIDValue = 0;
	public static  ProgramOptions opts =new ProgramOptions();
	public static boolean macroentry = false; // Flag to set a Command Line
	// Macro Value
	public static boolean isLogFileName=false;
	public static String logfilename="";

	private static String Version = "ZUG Premium 6.0" + ".201304016" + ".146";
	static Hashtable<String, String[]> fileExtensionSupport = new Hashtable<String, String[]>();

	public static HashMap<String, String> macrocommandlineinputs = new HashMap<String, String>();

	TopologySet[] TopologySet = null;


	@SuppressWarnings("unused")
	private String dBHostName = StringUtils.EMPTY;
	private String dBName = StringUtils.EMPTY;
	private String dbUserName = StringUtils.EMPTY;
	private String dbUserPassword = StringUtils.EMPTY;
	private String validTopoDetail = StringUtils.EMPTY;

	public static String[] productLogFiles = null;
	// variable to store context variable for archiving
	public static String TOPOSET = null;
	// private String TPID = null;

	public static String ZUG_LOGFILENAME = "";
	// Initiating AtomInvoker
	// public static AtomInvoker invokeAtoms=null;
	public static final String inprocess_jar_xml_tag_path = "//root//inprocesspackages//inprocesspackage";
	public static final String inprocess_jar_xml_tag_attribute_name = "name";
	public static final String native_inprocess_xml_tag_path = "//root//inprocesspackages//inprocesspackage";
	public static final String inprocess_xml_tag_attribute_language = "language";
	public static final String reportingXmlTagPath="//root//Reporting-dest";
	public static final String reportingXmlTagAttribute="name";
	public static HashMap<String, AtomInvoker> invokeAtoms = new HashMap<String, AtomInvoker>();
	public static HashMap<String, AtomInvoker> invoke_native_atoms = new HashMap<String, AtomInvoker>();
	private static volatile String builtin_atom_package_name = "";

	static Reporter reporter=null;

	// Hashtable to store the file name as key and its desired macro column as value;
	public static HashMap<String,String> macroColumnValue=new HashMap<String, String>();

	public TestSuite testsuite=new TestSuite();

	/*
	 * Constructor that initializes the program options.
	 */
	public Controller() {
		StringBuilder helpMessagebuf = new StringBuilder();
		versionMessage = Version;

		helpMessagebuf.append("\n\nZUG - Version - . " + versionMessage
				+ ". \n");

		helpMessagebuf
		.append(" Usage :: runZUG.bat [<option>] <inputfilepath.xls>");

		helpMessagebuf
		.append("\n\n\t <inputfilepath.xls> : It is the compulsory ");
		helpMessagebuf
		.append("argument required by Automation. It should point to the Test Case XLS file");
		helpMessagebuf
		.append("\n\tFor eg. : \"D:\\Client Test\\Automation\\inputFiles\\input.xls\"");

		helpMessagebuf.append("\n\nProgram Options --");

		helpMessagebuf
		.append("\n\n\n\n\t -TestcaseID=Comma Separated list of  Automated Test Case ID's to execute. ");
		helpMessagebuf
		.append("\n\t\t ZUG will just execute these test cases and will ignore the other test cases. This is not a Mandatory field. ");
		helpMessagebuf
		.append("\n\t\t By Default(if this argument is not provided) the ZUG will execute all the test cases specified in \"TestCases\" sheet.");

		helpMessagebuf
		.append("\n\n\t -Repeat : -Repeat should always be paired with Count or Duration, It basically tells Zug to repeat the execution of testcases a number of times or for a specified duration.");
		helpMessagebuf
		.append("\n\n\n\n\t -Count= integer; Number of times the test cases mentioned in the test plan will be executed in iteration.");
		helpMessagebuf
		.append("\n\t\t This should be a number. This is to support LONGEVITY support in ZUG.");
		helpMessagebuf
		.append("\n\t\t Note: ZUG Debug Logs are turned OFF during LONGEVITY to ensure ZUG is not taking more disk space.");

		helpMessagebuf
		.append("\n\n\n\n\t -Duration= Time-Duration; How long the test cases mentioned in the test plan will be executed in iteration.");
		helpMessagebuf
		.append("\n\t\t This is to support LONGEVITY support in ZUG.");
		helpMessagebuf
		.append("\n\t\t The value should be postfixed by \"d\" to signify days and \"h\" to signify Hours. ");
		helpMessagebuf
		.append("\n\t\t The value should be postfixed by \"m\" to signify Minutes and \"s\" to signify Seconds. ");

		helpMessagebuf
		.append("\n\t\t Example: -Duration=3d signifies 3 days, and -Duration=3h signifies 3 hours. ");
		helpMessagebuf
		.append("\n\t\t -Duration=3m signifies 3 minutes, and -Duration=3s signifies 3 Seconds. ");

		helpMessagebuf
		.append("\n\t\t Note: -Duration and -Count are mutually exclusive. If both of them are  ");
		helpMessagebuf
		.append(" specified on the command prompt, then -Count takes precedence over -Duration.");
		helpMessagebuf
		.append("\n\t\t Note: ZUG Debug Logs are turned OFF during LONGEVITY to ensure ZUG is not taking more disk space.");
		helpMessagebuf
		.append("\n\n\t -NoRepeat (by default -NoRepeat is set) : This option ensures that all the test cases in the test suite are run only once.");

		helpMessagebuf
		.append("\n\n\n\n\t -Verbose : To display Debug messages on the Console.");
		helpMessagebuf
		.append("\n\n\n\n\t -NoVerbose : This will stop Zug from displaying Debug messages on the console. By default -NoVerbose is set");
		helpMessagebuf.append(" unless explicitly specified.");

		helpMessagebuf
		.append("\n\n\n\n\t -Autorecover : There will be test step cleanup during test plan/step timeout. By default Autorecover is set");
		helpMessagebuf
		.append(" unless explicitly mentioned. This will be used for nightly builds to do test step cleanups during timeouts.");
		helpMessagebuf
		.append("\n\n\n\n\t -NoAutorecover : The cleanup steps/molecules in the test suite will not be executed.");

		helpMessagebuf
		.append("\n\n\n\n\t -Execute : This mode will verify if the Test Design Excel Sheet");
		helpMessagebuf
		.append(" created by the user is correct or not and will execute the atoms. ");
		helpMessagebuf
		.append("\n\t\t By default -Execute will be set unless explicitly mentioned ");
		helpMessagebuf
		.append("\n\n\n\n\t -NoExecute : This mode will verify if the Test Design Excel Sheet");
		helpMessagebuf
		.append(" created by the user is correct or not and will not execute the atoms.");

		helpMessagebuf
		.append("\n\n\n\n\t -Debug : To run the Automation in -Debug Mode. In this case ");
		helpMessagebuf
		.append(" if any atom is not implemeted then the ZUG will prompt with a default Action Atom.");
		helpMessagebuf
		.append("\n\n\n\n\t -NoDebug : To run the Automation in -NoDebug Mode. In this case ");
		helpMessagebuf
		.append(" if any atom is not implemeted then the Zug will have report that action Failed");
		helpMessagebuf
		.append("\n\t\t  By default -NoDebug will be set unless explicitly mentioned ");

		helpMessagebuf
		.append("\n\n\n\n\t -Verify : To execute the testcases with verification Action.");
		helpMessagebuf
		.append("\n\t\t By default -Verify is set means ZUG will run verification for each ");
		helpMessagebuf.append("testcase unless explicitly mentioned.");
		helpMessagebuf
		.append("\n\n\n\n\t -NoVerify : To execute the testcases without verification Action.");

		helpMessagebuf
		.append("\n\n\n\n\t -AtomPath=Location where the atoms are located. This is the location from where .");
		helpMessagebuf
		.append("\n\t\t the ZUG will pick up atoms for Test Automation/Execution.");

		helpMessagebuf
		.append("\n\n\n\n\t -TestcycleId=[TestCycle ID] : If testcycle ID is not provided then the Automation ZUG will Generate a New ID.");

		helpMessagebuf
		.append("\n\n\n\n\t -TestplanId=[Test Plan Id]. Use Upload Tool to generate a new TestPlan ID.");

		helpMessagebuf
		.append("\n\n\n\n\t -TopologysetId=[Topology Set Id] : The Id of the TopologySet. It is used to register results in a testcycle for the specified Topology Set");

		helpMessagebuf
		.append("\n\n\n\n\t -TestplanId and -TopologysetId both have to be set to put the test case execution result to the Framework Database.");
		helpMessagebuf
		.append("\n\n\n\n\t -Testplan and -Topologyset both have to be set to put the test case execution result to the Framework Database.");
		helpMessagebuf
		.append("\n\n\n\n\t -TestPlan=[Fully qualified path name of testplan] : Fully qualified name is a \":\" (colon) delimited string, comprising of the name of the name of product, the release, the sprint, and the testplan - that uniquely identifies the plan, under which the testcycle is to be created. If the release, sprint, or testplan does not already exist in Zermatt, new ones will be automatically created. Note that if any of the object names has spaces, then the entire string must be enclosed in quotes. When using this option, do not use the TestplanId option, as these are mutually exclusive.\n\n\n\n\t Example: -TestPlan=\"ZUG:First Release:rc7 sprint:Smoke test plan\"");
		helpMessagebuf
		.append("\n\n\n\n\t -TestPlanID=[Test Plan Id] : a numeric identifier for an existing testplan in Zermatt. To find the testplan identifier, use the appropriate icons under the Testsuite listing page to generate the appropriate command line options. When using this option, do not use the Testplan option, as these are mutually exclusive.");
		helpMessagebuf
		.append("\n\n\n\n\t -Topologyset=[Topology Set Name] : A name that uniquely identifies an existing topology set in Zermatt. When using this option, do not use the TopologysetId option, as these are mutually exclusive.");
		helpMessagebuf
		.append("\n\n\n\n\t -TopologysetId=[Topology Set Id] : a numeric identifier for an existing topology set in Zermatt. To find the topologyset identifier, use the appropriate icons under the Testsuite listing page to generate the appropriate command line options. Note that, when using the topologyset identifier option, the topologyset must already be included for the testplan. When using this option, do not use the Topologyset option, as these are mutually exclusive.");

		helpMessagebuf
		.append("\n\n\n\n\t -Buildtag=[Build Tag] : The tag used to identify the build of the product being tested. If the build tag does not exist in Zermatt (for the applicable sprint), a new record will be created. Note that if tag has spaces, then the entire string must be enclosed in quotes.\n\n\n\n\tExample: -Buildtag=\"Zug V4.3 - rc2 build 4035\"");
		helpMessagebuf
		.append("\n\n\n\n\t -Include=[location] :This option specifies the location from where ZUG will pick up molecules and macros for Test Automation/Execution. The location can be a file name residing in the same directory of that of the test suite or fully qualified location of the file but it should not be a relative path.We can give multiple locations by comma separator.\n\n\t Example: -include=C:\\Tests\\Molecules"+
				"\n\n\t We can also provide name spaces to the test suite included from command line.The syntax for giving name space in the -include option is\n\n\t -include=namespace1#filename1,namespace2#filename2");
		helpMessagebuf
		.append("\n\n\n\n\t -MacroColumn=[file identifier:column value] :This option let us select a particular macro value column for a test suite.The syntax for -macrocolumn is \n\n\t -macrocolumn=file identifier:column number, file identifier:column number."+
				"\n\n\t The file identifier is the file name,default name space or the optional name space provided in the -include option.If we have provided any optional name space in the -include option  then it should be the file identifier in the -macrocolumn.If any cell of the selected column is empty then it takes the value of the default column cell(1st column i.e, the “value” column).");
		helpMessagebuf
		.append("\n\n\n\n\t -MacroFile=[file location] :This option is used to include a macro text file from command line.The text file contains macro in the format $macro=value.Each macro should begin in a new line.At run time ZUG treats these macros as it belongs to the main test suite.The syntax is -macrofile=filelocation."+
				"\n\n\t Example: - macrofile=D:\\Files\\macroFile.txt");
		helpMessagebuf
		.append("\n\n\n\n\t -LogFileName=[AlphaNumeric] :This is required by Zug to change the logfile names where zug logs the execution messages(error,debug). The logfile name will be created as <logfilename>-Atom.log , <logfilename>-Debug.log.");

		helpMessagebuf.append("\n\n\n\n\t For example ");
		helpMessagebuf
		.append("\n\t------------------------------------------------------------------");
		helpMessagebuf
		.append("\n\t For eg. runZUG.bat \"c:\\input.xls\" -TestcaseID=ID01,ID02 ");
		helpMessagebuf
		.append("\n\t------------------------------------------------------------------\n\n");
		helpMessagebuf.append("\n--NOTE--\n");

		helpMessagebuf
		.append(" Options/Switches/Inputs has to specified with no whitespace before or after the equals sign.");
		helpMessagebuf
		.append(" If the option's value contains embedded whitespace, it must be enclosed in double quotes:\n\n");
		helpMessagebuf.append(" Correct   : c:\\input.xls\n");
		helpMessagebuf.append(" Correct   : \"c:\\input.xls\"\n");
		helpMessagebuf
		.append(" Incorrect : D:\\CPAPI Test\\Automation\\inputFiles\\input.xls\n");
		helpMessagebuf
		.append(" Correct   : \"D:\\CPAPI Test\\Automation\\inputFiles\\input.xls\"");
		helpMessagebuf
		.append("Note. You Must use both the TestPlan and the Topologyset together,when reporting results to Zermatt ");
		helpMessagebuf.append("\n\n");
		helpMessage = helpMessagebuf.toString();

	}

	// / This function prints the Usage
	private void PrintUsage() {
		System.out.println(helpMessage);

	}

	// This function prints the Config

	private void printConfig() {
		Runtime jvm = Runtime.getRuntime();
		message("\n-------------Zug Configuration-------------");
		message("Zug Version: " + Version);
		message("Zug is running in Java Version: " + SysEnv.JavaVersion
				+ "\nJava Maximum Memory Size: "
				+ Utility.getMaxJVMMemorySize(jvm));
		message("Java Home: " +SysEnv. JavaHome + "\nJava Compiler: " +SysEnv. JavaCompiler
				+ "\nJava Library Path: " + SysEnv.JavaLibraryPath);
		message("Zug is running in Operating System: " + SysEnv.OS_NAME
				+ "\nOperating System Version: " + SysEnv.OS_VERSION
				+ "\nOperating System Architecture: " +SysEnv. OS_ARCH);
		message("Total Physical Memory(RAM): " + Utility.getPhysicalMemory()
				+ "\nTotal Free Physical Memory: "
				+ Utility.getFreePhysicalMemory()
				+ "\nTotal Cpu usage by the Process: " + Utility.getCpuUsage());
		message("----------------------------------------------");
	}

	// / This function prints the Version
	private void PrintVersionInformation() {
		System.out.println(versionMessage);

	}

	public static void message(String msg) {
		Log.Result(msg);

		if (opts.verbose) {
			//	Charset cs=new Charset("US-ASCII","cmdset");

			System.out.println(msg);

		}
	}


	/***
	 * Function to Show the Test Case Report at the End, even if the DB
	 * reporting is OFF.
	 */
	private void ShowTestCaseResultONConsole() {
		ExecutedTestCase[] executedTestCase = new ExecutedTestCase[testsuite.executedTestCaseData
		                                                           .size()];

		int count = 0;
		// Set<String> TestCaseKey = executedTestCaseData.keySet();
		Set<Map.Entry<String, ExecutedTestCase>> testCaseSet =testsuite. executedTestCaseData
				.entrySet();
		List<Map.Entry<String, ExecutedTestCase>> testCaseList = new ArrayList(
				testCaseSet);
		Collections.sort(testCaseList, new TestCaseDateComparator());
		Iterator it = testCaseList.iterator();
		while (it.hasNext()) {
			Entry<String, ExecutedTestCase> TestCaseEntry = (Entry<String, ExecutedTestCase>) it
					.next();
			String s = TestCaseEntry.getKey();
			executedTestCase[count++] = (ExecutedTestCase)testsuite. executedTestCaseData
					.get(s);
		}
		if (executedTestCase.length > 0) {
			message("\nFollowing are the Details of the TestCases Result Executed by ZUG Version -> "
					+ Version);

			message("\nTestCase ID \t Status \t Time Taken(In milli-seconds) \t Comments\n ");
			if (!opts.verbose) {
				System.out
				.println("\nFollowing are the Details of the TestCases Result Executed by ZUG Version -> "
						+ Version);
				System.out
				.println("***********************************************************************************************************");
				System.out
				.println("\nTestCase ID \t Status \t Time Taken(In milli-seconds) \t Comments\n ");

			}
		}

		for (int i = 0; i < executedTestCase.length; i++) {

			TestCaseResult testCaseResult = new TestCaseResult();
			testCaseResult.set_testCaseId(executedTestCase[i].testCaseID);
			testCaseResult
			.set_testExecution_Time(executedTestCase[i].timeToExecute);
			testCaseResult.set_status(executedTestCase[i].testCaseStatus);
			testCaseResult
			.set_comments(executedTestCase[i].testCaseExecutionComments);

			message("\n" + testCaseResult.get_testCaseId() + "\t"
					+ testCaseResult.get_status() + "\t\t "
					+ testCaseResult.get_testExecution_Time() + "\t\t "
					+ testCaseResult.get_comments());
			if (!opts.verbose) {
				System.out.println("\n" + testCaseResult.get_testCaseId()
						+ "\t" + testCaseResult.get_status() + "\t\t "
						+ testCaseResult.get_testExecution_Time() + "\t\t "
						+ testCaseResult.get_comments());
			}
		}
		if (StringUtils.isNotBlank(testsuite.testcasenotran)) {
			System.out.println("\nTest Case Failed to Execute: "
					+testsuite. testcasenotran);
		}

	}

	private class TestCaseDateComparator implements Comparator<Object> {

		public int compare(Object obj1, Object obj2) {
			Map.Entry<String, ExecutedTestCase> e1 = (Map.Entry<String, ExecutedTestCase>) obj1;
			Map.Entry<String, ExecutedTestCase> e2 = (Map.Entry<String, ExecutedTestCase>) obj2;

			// Sort based on completion time.
			Date CompletionTime1 = e1.getValue().testCaseCompletetionTime;
			Date CompletionTime2 = e2.getValue().testCaseCompletetionTime;

			return CompletionTime1.compareTo(CompletionTime2);
		}
	}



	/**
	 * Method to sort a ArrayList<String> according to length of the strings
	 * 
	 * @param ArrayList
	 *            <String> unsortedlist
	 * @retrun sortedlist ArrayList<String>
	 */
	private ArrayList<String> sortListByLength(ArrayList<String> unsortedList) {

		ArrayList<String> sortedlist = new ArrayList<String>();
		sortedlist = unsortedList;
		// This logic is failing if argument length are similar
		Comparator<String> reverseLengthCompare = new Comparator<String>() {
			@Override
			public int compare(String string1, String string2) {

				Integer s1len = string1.length();
				Integer s2len = string2.length();
				return s2len.compareTo(s1len);
			}

		};
		Collections.sort(sortedlist, reverseLengthCompare);

		return sortedlist;
	}



	/***
	 * Function to check if value is MVM
	 * 
	 * @param value
	 *            String to Check for MVM. testcase TestCase to get the Actions
	 *            and their arguments
	 * @return boolean true if MVM false for not
	 */






	/*
	 * Check the command line Testcase exists
	 * 
	 * @param testcase
	 * 
	 * @return testcase
	 */

	/***
	 * Function to return the Value part from a name Value pair.
	 * 
	 * @param nameValue
	 *            String of the Format Name = Value Return just the Name
	 */
	public static String ReturnValue(String nameValue) {
		Log.Debug("Controller/ReturnValue: Start of function with nameValue = "
				+ nameValue);

		String value = nameValue;
		String[] varVal = Excel.SplitOnFirstEquals(nameValue);

		if (varVal.length > 1) {
			Log.Debug("Controller/ReturnValue : Adding VarName = " + varVal[0]
					+ " and Value = : " + varVal[1]);
			value = varVal[1];
		}
		Log.Debug("Controller/ReturnValue: End of function with nameValue = "
				+ nameValue + " and Value = " + value);

		return value;
	}

	/***
	 * This will log the primitive log that is sent to the Harness
	 */
	private void LogPrimitiveMessage(String levelAndMessage) {

		if (levelAndMessage.length() != 0) {
			String level = levelAndMessage.substring(0,
					levelAndMessage.indexOf(","));
			String message = levelAndMessage.substring(levelAndMessage
					.indexOf(",") + 1);

			if (level.equalsIgnoreCase("error")
					|| level.equalsIgnoreCase("fatal")) {
				Log.Error(message);
				Log.Debug(message);
				Log.PrimitiveErrors(message);
			} else if (level.equalsIgnoreCase("debug")) {
				Log.Debug(message);
				Log.Primitive(message);
			} // This is basically to Log the Results of Primitives
			else if (level.equalsIgnoreCase("info")) {
				Log.Debug(message);
				Log.Primitive(message);
				Log.PrimitiveResults(message);
			} else {
				Log.Error("Invalid Log Level Received from the Primitive ->  LogLevel as "
						+ level + " and Log Message as : " + message);
			}
		} else {
			Log.Error("Invalid Log Message-->No such Message for-- \""
					+ levelAndMessage + "\"--In Primitive");
			message("Try Fixing Atom Log message Definitions");
			message("----------------------------Ending Automation---------------------------");
			// //System.exit(1);
		}

	}


	/***
	 * Function to Initialize the variables required by the Controller to Do
	 * Setup/TearDown and run testcases. This can only be done after reading the
	 * Test Case Excel sheet.
	 * 
	 */
	private void InitializeVariables(Excel readExcel) throws Exception {
		
		Log.Debug("Controller/InitializeVariables :Start of Function");

		if (StringUtils.isEmpty(opts.scriptLocation)) {
			opts.scriptLocation = readExcel.ScriptLocation() + ";"
					+ opts.workingDirectory; // appends the current directory
			String[] spliArr = opts.scriptLocation.split(";");
			String newLocation = "";
			for (String spits : spliArr) {
				String[] temp0 = spits.split("\\\\");
				if (!temp0[0].contains(":")) {
					if (opts.filelocation == null) {
						temp0[0] = opts.workingDirectory;
						newLocation = temp0[0] + "\\" + spits;
					} else {
						temp0[0] = opts.filelocation;
						newLocation = temp0[0] + spits;
					}

				}

			}

			opts.scriptLocation = opts.scriptLocation + ";" + newLocation;
			// message("Notun Location\t"+scriptLocation);
			Log.Debug("Controller::The Updated ScriptLocation\t"
					+ opts.scriptLocation);
		}
		// from where the Zug is invoked

		if (StringUtils.isEmpty(opts.includeMolecules)) {
			opts.includeMolecules = readExcel.IncludeMolecules();
		}
		// Add the Include Sheet to NameSpace
		if (opts.includeMolecules != null && opts.includeMolecules != StringUtils.EMPTY) {
			opts.includeFlag = false;
		}

		// System.out.println("Script at - >"+scriptLocation);
		Log.Debug("Controller/InitializeVariables : ScriptLocation = "
				+ opts.scriptLocation);
		productLogFiles = readExcel.ProductLogFiles();
		for (int i = 0; i < productLogFiles.length; i++) {
			Log.Debug("Controller/InitializeVariables : Product Log File- "
					+ productLogFiles[i]);
		}

		// Get Prototype sheet values
		testsuite.prototypeHashTable = readExcel.Prototypes();

		// Set the TestPlan and TestCase timeout context Variable...
		// Test Plan Timeout Depends a lot on the Longevity Parameters
		if (opts.repeatDurationSpecified) {
			CreateContextVariable("ZUG_TESTSUITE_TIMEOUT="
					+ ((opts.repeatDurationLong / 1000) + readExcel
							.TESTPLAN_TIMEOUT()));

		} else if (opts.repeatCount >= 1) {
			CreateContextVariable("ZUG_TESTSUITE_TIMEOUT=" + opts.repeatCount
					* readExcel.TESTPLAN_TIMEOUT());
		}

		// No Affect to the Test Step Timeout.
		CreateContextVariable("ZUG_TESTSTEP_TIMEOUT="
				+ readExcel.TESTSTEP_TIMEOUT());

		validTopoDetail = readExcel.ValidTopoDetail();
		Log.Debug("Controller/InitializeVariables : validTopoDetail = "
				+ validTopoDetail);

		if (opts.dbReporting) {
			dBHostName = readExcel.DBHostName();
			ContextVar.setContextVar("ZUG_DBHOSTNAME",dBHostName);
			// message("The DBHOST NAME "+dBHostName);
			Log.Debug("Controller/InitializeVariables : DBHostName = "
					+ dBHostName);
			 dBName = readExcel.DBName();
			 Log.Debug("Controller/InitializeVariables : dBName = " + dBName);
			dbUserName = readExcel.DBUserName();
			ContextVar.setContextVar("ZUG_DBUSERNAME",dbUserName);
			Log.Debug("Controller/InitializeVariables : dbUserName = "
					+ dbUserName);
			dbUserPassword = readExcel.DBUserPassword();
			ContextVar.setContextVar("ZUG_DBPASSWORD",dbUserPassword);
			Log.Debug("Controller/InitializeVariables : dbUserPassword = "
					+ dbUserPassword);
			testsuite.testSuitName = readExcel.TestSuitName();
			ContextVar.setContextVar("ZUG_TESTSUITENAME",testsuite.testSuitName);
			// ZUG Specific ContextVariable to store ZUG_TESTSUITEID
			ContextVar.setContextVar("ZUG_TESTSUITEID", testsuite.testSuitName);
			Log.Debug("Controller/InitializeVariables : testPlanName = "
					+ testsuite.testSuitName);
			testsuite.testSuitRole = readExcel.TestSuitRole();
			ContextVar.setContextVar("ZUG_TESTSUITEROLE",testsuite.testSuitRole);
			Log.Debug("Controller/InitializeVariables : testPlanRole = "
					+ testsuite.testSuitRole);
		}
		ContextVar.setContextVar("ZUG_SCRIPTLOCATION", readExcel.ScriptLocation());
		Log.Debug("Controller/InitializeVariables :End of Function");
	}

	/***
	 * Function to Set a Context Variable
	 * 
	 * @param variableAndValue
	 *            Variable along with its Value separated by = sign. For Example
	 *            : PATH="C:\test"
	 */
	static void CreateContextVariable(String variableAndValue)
			throws Exception {

		Log.Debug("Controller/CreateContextVariable : Start of function with variableAndValue = ."
				+ variableAndValue);

		if (StringUtils.isBlank(variableAndValue)) {
			Log.Debug("Controller/CreateContextVariable : End of function with Empty variableAndValue .");
			return;
		}

		if (variableAndValue.contains("=")) {
			Log.Debug("Controller/CreateContextVariable : variableAndValue = "
					+ variableAndValue + " Contains an = sign.");
			String[] tempVarValue = Excel.SplitOnFirstEquals(variableAndValue);
			Log.Debug("Controller/CreateContextVariable : After split of variableAndValue = "
					+ variableAndValue
					+ " the Length is "
					+ tempVarValue.length);

			if (tempVarValue.length <= 1) {
				Log.Debug("Controller/CreateContextVariable : Setting {0} Context Variable with Empty Value."
						+ tempVarValue[0]);
				ContextVar.setContextVar(tempVarValue[0].trim(),
						StringUtils.EMPTY);
			} else {
				Log.Debug("Controller/CreateContextVariable : Setting "
						+ tempVarValue[0] + " Context Variable with Value = "
						+ tempVarValue[1]);
				ContextVar.setContextVar(tempVarValue[0].trim(),
						tempVarValue[1]);
			}
		} else {
			// This else will just set the Environement Variable with an Empty
			// String.
			Log.Debug("Controller/CreateContextVariable : Setting "
					+ variableAndValue + " Context Variable with Empty Value.");
			ContextVar
			.setContextVar(variableAndValue.trim(), StringUtils.EMPTY);
		}
		Log.Debug("Controller/CreateContextVariable : End of function with variableAndValue = "
				+ variableAndValue);
	}


	/***
	 * Function to connect to the Database. This function will throw an
	 * exception if the Database or the user credentials provided is not a valid
	 * user.
	 */
	public static String  productid = null, sessionid = null;




	/***
	 * Function to Delete a Context () Variable.
	 * 
	 * @param variableName
	 *            Name of the VAriable to remove from the List of Environment
	 *            Variable.
	 */
	static void DestroyContextVariable(String variableName) throws Exception {
		Log.Debug(String
				.format("Controller/DestroyContextVariable : Start of function with variableName = %s.",
						variableName));
		Log.Debug(String
				.format("Controller/DestroyContextVariable : Setting %s{0} Context Variable with NULL Value i.e. Deleting the Variable.",
						variableName));
		ContextVar.Delete(variableName.trim());
		Log.Debug(String
				.format("Controller/DestroyContextVariable : End of function with variableName = %s.",
						variableName));
	}

	/**
	 * thread to send output to the child.
	 */
	final class Sender implements Runnable {
		// ------------------------------ CONSTANTS
		// ------------------------------

		/**
		 * e.g. \n \r\n or \r, whatever system uses to separate lines in a text
		 * file. Only used inside multiline fields. The file itself should use
		 * Windows format \r \n, though \n by itself will alsolineSeparator
		 * work.
		 */
		private final String lineSeparator = System
				.getProperty("line.separator");
		// ------------------------------ FIELDS ------------------------------
		/**
		 * stream to send output to child on
		 */
		private final OutputStream os;

		// -------------------------- PUBLIC INSTANCE METHODS
		// --------------------------
		/**
		 * method invoked when Sender thread started. Feeds dummy data to child.
		 */
		public void run() {
		}

		// --------------------------- CONSTRUCTORS ---------------------------
		/**
		 * constructor
		 * 
		 * @param os
		 *            stream to use to send data to child.
		 */
		Sender(OutputStream os) {
			this.os = os;
		}
	}

	/**
	 * thread to read output from child
	 */
	class Receiver implements Runnable {
		// ------------------------------ FIELDS ------------------------------

		/**
		 * stream to receive data from child
		 */
		private final InputStream is;

		// -------------------------- PUBLIC INSTANCE METHODS
		// --------------------------
		/**
		 * method invoked when Receiver thread started. Reads data from child
		 * and displays in on System.out.
		 */
		public void run() {
			try {
				final BufferedReader br = new BufferedReader(
						new InputStreamReader(is), 50 /* keep small for testing */);
				String line;
				while ((line = br.readLine()) != null) {
					System.out.println(line);
				}
				br.close();
			} catch (IOException e) {
				throw new IllegalArgumentException(
						"IOException receiving data from child process.");
			}
		}

		// --------------------------- CONSTRUCTORS ---------------------------
		/**
		 * contructor
		 * 
		 * @param is
		 *            stream to receive data from child
		 */
		Receiver(InputStream is) {
			this.is = is;
		}
	}



	/*
	 * Function to save the Test Case to the Result Database in the Test Case
	 * table.
	 * 
	 * @param testCaseID TestCase ID to save to the Result Database
	 * 
	 * @param testCaseDesc Description of the TestCase - to be stored to the
	 * Result Database
	 */
	String testcaseid = null;



	/***
	 * Function to Save the Test Case Result at intermediate steps.
	 */
	String testexecutiondetailid = null, testcycletopologysetid = null,
			variables = null;


	/***
	 * Function to generate new test case ID
	 */


	/**
	 * Need to work on this function This function opens the Pipe(Server Pipe)
	 * with the different primitives, so that there can be one common location
	 * for logging.
	 */
	private void ListenToPrimitives(int iPORT) {
		String ParamFromClient = StringUtils.EMPTY;
		Socket conn = null;
		ServerSocket sock = null;

		InputStream inStream = null;
		// DataInputStream inDataStream = null;
		BufferedReader inDataStream = null;

		try {

			// Server Socket initialization with iPORT=8245
			boolean sockCreated=false;
			while(!sockCreated){
				try{
					sock = new ServerSocket(iPORT);
					sockCreated=true;
				}catch(Exception e){
					iPORT++;
					if (iPORT == 65535) {
						Log.Error("Controller/ListenToPrimitive: Only ten zug instances can run simultaneously "
								+ iPORT);
						System.exit(1);
					}
				}
			}

			if (sock != null) {
				Log.Debug("Socket created successfully!!!!!!! ->  " + sock);
			}
			// Setting it Reusable for keep on running if the thread waits
			// for 2mls
			sock.setReuseAddress(true);

			Log.Debug("Controller/ListenToPrimitive : Server Socket Created on port "
					+ Integer.toString(iPORT));
			try{
				ContextVar.setContextVar("ZUG_PORTNO", ""+iPORT);
			}catch(Exception e){
				Log.Error("Controller/ListenToPrimitive : Exception "+e.getMessage());
			}
			while (true) {

				conn = sock.accept();
				Log.Debug("Controller/ListenToPrimitive : Client Connected....");

				Log.Debug("Controller/ListenToPrimitive : Creating input stream object in order to read from socket.");

				inStream = conn.getInputStream();

				// inDataStream = new DataInputStream ( inStream );

				inDataStream = new BufferedReader(new InputStreamReader(
						inStream));

				Log.Debug("Controller/ListenToPrimitive : Creating input stream object in order to read from socket...Successful");
				Log.Debug("Controller/ListenToPrimitive : Reading Data from Socket. Sent by Client.");

				try {
					while ((ParamFromClient = inDataStream.readLine()) != null) {

						Log.Debug("Controller/ListenToPrimitive : Data from client is - "
								+ ParamFromClient);
						// message("The Length of The param is\t"+ParamFromClient.length());

						if (ParamFromClient.length() != 0) // Checking for
							// if the
							// Content is
							// null or not.
						{
							LogPrimitiveMessage(ParamFromClient);
						}
					}
				} catch (StringIndexOutOfBoundsException se) {
					continue;

				}

			}

		} catch (Exception e) {
			Log.Error("Controller/ListenToPrimitive: Exception Occurred in Primitive Atom->"
					+ e.getMessage());
			System.exit(1);
		}
	}

	private void ListenToPrimitives() {
		int iPORT = 8245;

		String ParamFromClient = StringUtils.EMPTY;
		Socket conn = null;
		ServerSocket sock = null;

		try {

			InputStream inStream = null;
			// DataInputStream inDataStream = null;
			BufferedReader inDataStream = null;

			try {

				// Server Socket initialization with iPORT=8245
				sock = new ServerSocket(iPORT);
				if (sock != null) {
					Log.Debug("Socket created successfully!!!!!!! ->  " + sock);
				}
				// Setting it Reusable for keep on running if the thread waits
				// for 2mls
				sock.setReuseAddress(true);

				Log.Debug("Controller/ListenToPrimitive : Server Socket Created on port "
						+ Integer.toString(iPORT));
				try{
					ContextVar.setContextVar("ZUG_PORTNO", ""+iPORT);
				}catch(Exception e){
					Log.Error("Controller/ListenToPrimitive : Exception "+e.getMessage());
				}
				while (true) {

					conn = sock.accept();
					Log.Debug("Controller/ListenToPrimitive : Client Connected....");

					Log.Debug("Controller/ListenToPrimitive : Creating input stream object in order to read from socket.");

					inStream = conn.getInputStream();

					// inDataStream = new DataInputStream ( inStream );

					inDataStream = new BufferedReader(new InputStreamReader(
							inStream));

					Log.Debug("Controller/ListenToPrimitive : Creating input stream object in order to read from socket...Successful");
					Log.Debug("Controller/ListenToPrimitive : Reading Data from Socket. Sent by Client.");

					try {
						while ((ParamFromClient = inDataStream.readLine()) != null) {

							Log.Debug("Controller/ListenToPrimitive : Data from client is - "
									+ ParamFromClient);
							// message("The Length of The param is\t"+ParamFromClient.length());

							if (ParamFromClient.length() != 0) // Checking for
								// if the
								// Content is
								// null or not.
							{
								LogPrimitiveMessage(ParamFromClient);
							}

						}
					} catch (StringIndexOutOfBoundsException se) {
						continue;

					}

				}

			} catch (Exception e) {
				// Log.Error("Controller/ListenToPrimitive: Exception Occurred in Primitive Atom->"
				// + e.getMessage());
				iPORT++;
				if (iPORT == 65535) {
					Log.Error("Controller/ListenToPrimitive: Only ten zug instances can run simultaneously "
							+ iPORT);
					System.exit(1);
				}
				ListenToPrimitives(iPORT);
				// System.exit(1);
			} finally {
				try {
					conn.close();
					sock.close();
				} catch (IOException e) {
					Log.Error("Controller/ListenToPrimitive : Exception while closing the connection"
							+ e.getMessage());
					e.printStackTrace();
				}

			}
		} finally {
			try {
				conn.close();
				sock.close();
			} catch (IOException e) {
				Log.Error("Controller/ListenToPrimitive : Exception while closing the connection"
						+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/***
	 * Function to Read a Context Variable Variable...
	 * 
	 * @param variableName
	 *            = Name of the VAriable to Read from the List of Context
	 *            Variable.
	 */
	static String ReadContextVariable(String variableName) throws Exception {
		Log.Debug(String
				.format("Controller/ReadContextVariable : Start of function with variableName = %s.",
						variableName));

		String keyValue = StringUtils.EMPTY;
		keyValue = ContextVar.getContextVar(variableName);

		if (StringUtils.isBlank(keyValue)) {
			keyValue = StringUtils.EMPTY;
		}

		Log.Debug(String
				.format("Controller/ReadContextVariable : End of function with variableName = %s & its Values as %s .",
						variableName, keyValue));

		return keyValue;
	}

	private void DoHarnessCleanup() throws Exception, DavosExecutionException {
		// Remove all the ContextVariable

		this.TOPOSET = ContextVar.getContextVar("ZUG_TOPOSET");
		testsuite.testSuiteId = ContextVar.getContextVar("ZUG_TESTSUITEID");
		//ContextVar.DeleteAll((int) harnessPIDValue);

		if (opts.dbReporting) {
			reporter.archiveLog();
			message("Cleanup starting... Closing Log");
			Log.Cleanup();
			for (int i = 0; i < Log.HarnessLogFileList.size(); i++) {
				String filePath = Log.HarnessLogFileList.get(i);
				File file = new File(filePath);
				if (file.exists()) {
				}
			}
		}

		// Log.Debug(" DoHarnessCleanup/Main : USING System.Environment.Exit(0) to EXIT ");
		System.out.println("\nExiting ZUG");

		System.exit(0);
	}

	public void showTestCaseResultEveryTime(ExecutedTestCase testCaseResult) {
		System.out
		.println("\nTestCase ID \t Status \t Time Taken(In milli-seconds) \t Comments\n ");
		System.out.println(testCaseResult.testCaseID + "\t"
				+ testCaseResult.testCaseStatus + "\t\t "
				+ testCaseResult.timeToExecute + "\t\t "
				+ testCaseResult.testCaseExecutionComments);

	}

	public void LoggedInUser() {
		try {
			String loginAppName = "GetLoginNameUnix";
			// If the application is run on NT rather than Unix, use this name
			loginAppName = "GetLoginNameNT";
			// Create login context
			LoginContext lc = new LoginContext(loginAppName,
					new com.sun.security.auth.callback.TextCallbackHandler());
			// Retrieve the information on the logged-in user
			lc.login();
			// Get the authenticated subject
			Subject subject = lc.getSubject(); // Get the subject principals
			Principal principals[] = (Principal[]) subject.getPrincipals()
					.toArray(new Principal[0]);
			for (int i = 0; i < principals.length; i++) {
				if (principals[i] instanceof com.sun.security.auth.NTUserPrincipal
						|| principals[i] instanceof com.sun.security.auth.UnixPrincipal) {
					String loggedInUserName = principals[i].getName();
					message(loggedInUserName);
				}
			}
		} catch (LoginException e) { // Login failed } }
		}
	}

	public Hashtable getConnectionParams(){
		Hashtable<String,String> ht=new Hashtable<String,String>();
		ht.put("dbUserName".toLowerCase(), dbUserName);
		ht.put("dBHostName".toLowerCase(),dBHostName );
		ht.put("dBName".toLowerCase(),dBName );
		ht.put("dbUserPassword".toLowerCase(), dbUserPassword);
		ht.put("TESTSUITEID".toLowerCase(), testsuite.testSuiteId);
		ht.put("testsuitename", testsuite.testSuitName);
		ht.put("testsuiterole".toLowerCase(), testsuite.testSuitRole);	
		ht.put("TestPlanPath".toLowerCase(),opts.getTestPlanPath());
		ht.put("TopologySetName".toLowerCase(), opts.getTopologySetName());
		ht.put("topologySetId".toLowerCase(), opts.getTopologySetId());
		ht.put("TestPlanId".toLowerCase(), opts.getTestPlanId());
		ht.put("testplanname",opts.getTestPlanName());
		ht.put("BuildTag".toLowerCase(),opts.getBuildTag());
		ht.put("BuildNo".toLowerCase(), opts.getBuildNo());
		ht.put("BuildId".toLowerCase(), opts.getBuildId());
		ht.put("buildname", opts.buildName);
		ht.put("testCycleId".toLowerCase(), opts.getTestCycleId());
		return ht;
	}
/*
	public Hashtable getReportingparams(){
		Hashtable<String,String> ht=new Hashtable<String,String>();
		ht.put("TESTSUITEID".toLowerCase(), testsuite.testSuiteId);
		ht.put("testsuitename", testsuite.testSuitName);
		ht.put("roleid".toLowerCase(), testsuite.testSuitRole);	
		ht.put("TestPlanPath".toLowerCase(),opts.TestPlanPath);
		ht.put("TopologySetName".toLowerCase(), opts.TopologySetName);
		ht.put("topologySetId".toLowerCase(), opts.topologySetId);
		ht.put("TestPlanId".toLowerCase(), opts.TestPlanId);
		ht.put("BuildTag".toLowerCase(),opts.BuildTag);
		ht.put("BuildNo".toLowerCase(), opts.BuildNo);
		ht.put("BuildId".toLowerCase(), opts.BuildId);
		ht.put("testCycleId".toLowerCase(), opts.testCycleId);
		return ht;
	}
*/
	/**
	 * main method for Harness. Entry point to the Controller
	 * 
	 * @param args
	 *            -Command line parameters for harness.
	 */
	public static void main(String[] args) throws InterruptedException,
	Exception, DavosExecutionException, MoleculeDefinitionException {
		SysEnv sysenv=new SysEnv();
		ProgramOptions.checkCommandLineArgs(args);
		if (args.length > 1) {
			for (String arg : args) {
				// controller.message("The argumentss "+arg);
				if(arg.toLowerCase().startsWith("-logfilename="))
				{

					String logfile[]=arg.split("=");
					if(logfile.length==2)
					{
						isLogFileName=true;
						logfilename=logfile[1];
						break;
					}
					else
					{
						System.out.println("[Error] Incorrect usage of -logfilename switch.");
						System.exit(1);
					}

				}
			}
		}
    		Controller.harnessPIDValue = Integer
				.parseInt((java.lang.management.ManagementFactory
						.getRuntimeMXBean().getName().split("@"))[0]); 
		final Controller controller = new Controller();
		String frameWork="";
		// controller.CreateContextVariable("ZUG_LOGFILENAME="+ZUG_LOGFILENAME);
		//System.out.println(Controller.ZUG_LOGFILENAME+ " 1st level contextvar of LOG "+ContextVar.getContextVar("ZUG_LOGFILENAME"));
     controller.CreateContextVariable("ZUG_LOGFILENAME="+ZUG_LOGFILENAME);
		ContextVar.setContextVar("ZUG_LOGFILENAME",
				Controller.ZUG_LOGFILENAME);
		
		StringBuilder cmdinputsargs = new StringBuilder();
		for (String cmdinputs : args) {
			cmdinputsargs.append(cmdinputs);
		}
		Log.Debug("Controller/Main:: Command Line Input: "
				+ cmdinputsargs.toString());
		// Checking for jar file entry in ZugINI.xml
		ExtensionInterpreterSupport testINI = new ExtensionInterpreterSupport();
		Set<Set> errorSet = new HashSet<Set>();

		if (testINI.reteriveXmlTagAttributeValue(inprocess_jar_xml_tag_path,
				inprocess_jar_xml_tag_attribute_name).length > 0) {
			int c = 0;

			for (String package_names : testINI.reteriveXmlTagAttributeValue(
					inprocess_jar_xml_tag_path,
					inprocess_jar_xml_tag_attribute_name)) {
				if (StringUtils.isNotBlank(package_names)
						|| StringUtils.isNotEmpty(package_names)) {
					AtomInvoker ai = new AtomInvoker(package_names);

					if (ai.native_flag) {
						// controller.message("This is native Dll Calling");
					} else if (ai.com_flag) {
						// controller.message("Com Jacob Calling");
					} else {
						// controller.message("invoking jar instance "+package_names);
						ai.loadInstance(package_names);

						if (ai.interpreter.inprocesspackageError.size() > 0) {
							errorSet.add(ai.interpreter.inprocesspackageError);

						}
					}
					invokeAtoms.put(package_names, ai);
				} else {
					Controller
					.message("[Warning] ZugINI.xml contains blank inprocess package definition. Please refer to the readme.txt or Zug User Manual");
				}
			}

			if (errorSet.size() > 0) {

				Log.Error(String
						.format("[Warning] %s has duplicate and incorrect for inprocess packages xml definition: 'language' attribute is not defined in ZugINI.xml",
								errorSet));

			}

		} else {
			Controller
			.message("No Inprocess Jar definition found in ZugINI.xml with proper Attribute definition for Tag");
		}
		//String reporterName[]=testINI.reteriveXmlTagAttributeValue(reportingXmlTagPath, reportingXmlTagAttribute);
// ProcessMonitorThread.currentThread().getId();

		// First Validate the Command Line Arguments



		//System.out.println(Controller.ZUG_LOGFILENAME+" 3rd level contextvar of LOG "+ContextVar.getContextVar("ZUG_LOGFILENAME"));
		try {
			Log.Debug("Controller/Main : Calling ProgramOptions.parse() to Parse program argument");
			opts.parse(args);


			if (controller.opts.isHelpRequest()) {
				controller.PrintUsage();
				return;
			}

			if (Controller.opts.isConfigRequest()) {
				controller.printConfig();
				return;
			}
			//	System.out.println(opts.toString());
			if (Controller.opts.isVersionRequest()) {
				// if(OS_FLAG) {
				try {
					com.automature.zug.license.LicenseValidator licenseValid = new com.automature.zug.license.LicenseValidator();
					if (licenseValid.matchMac() == false) {
						Controller
						.message("Please get a valid license for your machine");
						System.exit(1);
					}
					if (licenseValid.isDateValid() == false) {
						Controller
						.message("The License of ZUG has expired. Please renew. \n\tVisit www.automature.com");
						System.exit(1);
					}
					Controller.message("Zug is Valid "
							+ licenseValid.userInfo.companyName);

				} catch (Exception e) {
					Controller.message("Failed to validate your License copy");
					Controller.message("Message : " + e.getMessage() + "\n");
					System.exit(1);
				}

				controller.PrintVersionInformation();
				return;
			}

			Log.Debug("Controller/Main : Getting and Validating Command Line Arguments.");
			//System.out.println("db reporting "+opts.dbReporting);
			if (!opts.GetOptions()) {

				return;
			}
		//	System.out.println("db reporting "+opts.dbReporting);

			Controller.message("\n\nCommand Line Arguments Validated \n");
		} catch (Exception e) {
			Controller
			.message("Failed to Validate Command Line Arguments, exiting "
					+ "Message : " + e.getMessage() + "\n");
			Log.Error("Failed to Validate Command Line Arguments "
					+ "Message : " + e.getMessage() + "\n");
			return;
		}

		// if(OS_FLAG) {

		try {
			com.automature.zug.license.LicenseValidator licenseValid = new com.automature.zug.license.LicenseValidator();

			if (licenseValid.matchMac() == false) {
				Controller
				.message("Please get a valid license for your machine");
				System.exit(1);
			}
			if (licenseValid.isDateValid() == false) {
				Controller
				.message("The License of ZUG has expired. Please renew. \n\tVisit www.automature.com");
				System.exit(1);
			}
			Controller.message("Zug is Valid "
					+ licenseValid.userInfo.companyName);
			if (!opts.verbose) {
				System.out.println("Zug is Valid "
						+ licenseValid.userInfo.companyName);
			}

		} catch (Exception e) {
			Controller.message("Failed to validate your License copy");
			Controller.message("Message : " + e.getMessage() + "\n");
			System.exit(1);
		}

		fileExtensionSupport = ExtensionInterpreterSupport
				.ReadFileExtensionXML();

		HiPerfTimer tm = new HiPerfTimer();
		HiPerfTimer initializationTime = new HiPerfTimer();
		initializationTime.Start();
		tm.Start();

		Thread threadToOpenServerPipe = new Thread(new Runnable() {

			public void run() {
				controller.ListenToPrimitives();
			}
		});
		threadToOpenServerPipe.setDaemon(false);
		threadToOpenServerPipe.start();
		// Check for non verbose result show

		try {
			Log.Debug("Reading the Excel Sheet = " + opts.inputFile);
			Controller.message("Reading the TestCases Input Sheet  "
					+ opts.inputFile + ".\n");
			// Now reading the Excel object.
			Controller.readExcel = new Excel();
			Controller.readExcel.setXlsFilePath(opts.inputFile);

			Controller.CreateContextVariable("ZUG_BWD="
					+ new File(Controller.readExcel.getXlsFilePath())
					.getParent());
			// System.out.println("The base working directory "+ContextVar.getContextVar("ZUG_PWD"));
			Controller.readExcel.ReadExcel(opts.inputFile,
					opts.verificationSwitching, opts.compileMode);
			Controller.message("SUCCESSFULLY Read the TestCases Input Sheet "
					+ opts.inputFile);
			if (!opts.verbose) {
				System.out
				.println("SUCCESSFULLY Read the TestCases Input Sheet "
						+ opts.inputFile);
			}

			if (opts.compileMode) {
				if (Controller.readExcel.CompileTimeErrorMessage() != null) {
					Controller
					.message("\n******************** START: Compile Time Errors Present in the Test Design Sheet. ***************************");
					Controller.message(Controller.readExcel
							.CompileTimeErrorMessage());
					Controller
					.message("\n******************** END: Compile Time Errors Present in the Test Design Sheet. ***************************");

				} else {
					Controller
					.message("\n******************** There are no Syntax errors in the Test Design Sheet. ***************************");
				}
				controller.DoHarnessCleanup();
				return;
			}
			// System.out.println("4th level contextvar of LOG "+ContextVar.getContextVar("ZUG_LOGFILENAME"));
			// Initializing some of these Variables so that the controller can
			// do useful work
			Log.Debug("Controller/Main : Initializing the Controller Variables after reading the Excel sheet. Calling controller.InitializeVariables");
			//System.out.println("db reporting "+opts.dbReporting);
			controller.InitializeVariables(controller.readExcel);
			Log.Debug("Controller/Main : Initialized the Controller Variables after reading the Excel sheet.");
			// reporting_server_add = controller.readExcel.DBHostName();
			
			if (opts.dbReporting == true) {
				// XMLUtility utility = new XMLUtility();
				Log.Debug("Controller/InitializeVariables : Getting the TopologySet");
				// controller.TopologySet =
				// utility.ReadTopologySetInformation(controller.topologySetXMLFile);
				Log.Debug("Controller/InitializeVariables : Successfully got the TopologySet from the Excel Sheet");

				// If the Validation is successful. ... then its the turn of the
				// Test Plan and Test Cases to be Inserted to the Database.
				Hashtable connectionParam=controller.getConnectionParams();
				//System.out.println("DB name ="+controller.dBName);
				
				if(controller.dBName.equalsIgnoreCase("testlink")){
					reporter=new TestLinkReporter(connectionParam);
				//	System.out.println("Testlink obejct created");
					frameWork=controller.dBName;
				}else{
					reporter=new DavosReporter(connectionParam);
					frameWork="Davos";
				}
				if (!controller.reporter.connect()) {
					Controller
					.message("\nError Connecting to "+frameWork+". Controller Exiting ");
					controller.DoHarnessCleanup();
					return;
				}
				Controller.message("Connection to "+frameWork+" is successful.\n ");
				if (!opts.verbose) {
					System.out.println("Connection to "+frameWork+" is successful.\n ");
				}
			//	Hashtable reportingParams=controller.getReportingparams();
				if (!controller.reporter.ValidateDatabaseEntries()) {
					Controller
					.message("\nInvalid Entries provided. Controller Exiting Gracefully..... ");
					controller.DoHarnessCleanup();
					return;
				}
				// First task is to insert the test suite to the Database.
				// controller.SaveTestSuite();
			}

			// Now run the test-case one by one -
			controller.testsuite.testcases = controller.readExcel.TestCases();

			// Read the Abstract Test Cases from the Sheet
			controller.testsuite.abstractTestCase = controller.readExcel
					.AbstractTestCases();

			initializationTime.Stop();
			// controller.initializationTime =
			// (int)(initializationTime.Duration()/ (double)1000);
			controller.initializationTime = (int) (initializationTime
					.Duration());

			Controller
			.message("\n******************************************************************************** ");
			Controller
			.message("\nTotal time taken to initialize the Harness is -> "
					+ Controller.initializationTime + " milli Seconds.");
			Controller
			.message("\n******************************************************************************** ");
			if (!opts.verbose) {
				System.out
				.println("\n******************************************************************************** ");
				System.out
				.println("\nTotal time taken to initialize the Harness is -> "
						+ controller.initializationTime
						+ " milli Seconds.");
				System.out
				.println("\n******************************************************************************** ");
			}
			final String fw=frameWork;
			Thread thread = new Thread(new Runnable() {

				public void run() {
					try {
						try {
							// TODO update the current threadid
							controller.testsuite.run();
							if (opts.dbReporting == true) {
								reporter.heartBeat(sessionid);
								Log.Debug("Controller/"+fw+"heartbeat method Invoked with session Id: "+sessionid);		
							}
						} catch (DavosExecutionException ex) {
							String error = fw+" Exception occured during running test cases for main, exception is\n"
									+ ex.getMessage();
							Log.Error(error);
							ex.printStackTrace();
						}
					} catch (Exception e) {
						String error = "Exception occured during running test cases for main, exception is\n"
								+ e.getMessage();
						Log.Error(error);
						e.printStackTrace();

					}
				}
			});

			thread.start();

			if (!opts.debugMode) {
				// wait for thread for specific time
				String[] tep = controller.ReadContextVariable(
						"ZUG_TESTSUITE_TIMEOUT").split("\\.");
				thread.join(Integer.parseInt(tep[0]) * 1000);

			} else {
				thread.join();
			}

			// Last time again wait for this Thread to get Over....
			thread.join();

			tm.Stop();
			// controller.executionTime = (int)(tm.Duration() / ((double)1000));
			controller.executionTime = (int) (tm.Duration());
			// controller.message("the output\t" + controller.executionTime);
			// In any case, do the reporting
			if (opts.dbReporting == true) {

				if (controller.testsuite.executedTestCaseData.size() > 0) {
					// Now everything is done except storing the TestCase Data
					// to Result Data..so doing that now...
					controller.message("\n\nStoring the TestCase Result to "
							+ controller.dBHostName + "\\" + controller.dBName
							+ frameWork+".....");

					try {
						reporter.saveTestCaseResults(controller.testsuite.executedTestCaseData);
					} catch (Exception de) {
						Log.Error(de.getMessage());
						System.exit(1);

					}

					Controller
					.message("\n\nSUCCESSFULLY Stored the TestCase Result to "
							+ controller.dBHostName
							+ "\\"
							+ controller.dBName + frameWork+".....");

				}
			} else // / Even if the DB reporting is FALSE, still we should
				// actually
				// show the statistics.
			{
				controller.ShowTestCaseResultONConsole();

			}
		} catch (Exception ex) {
			Log.Error("\nController/Main : Exception Raised while executing the Test Cases in Controller. Exception is "
					+ ex.getMessage() + " and Stack Trace is : \n");
			ex.printStackTrace();

		}

		Controller
		.message("\n******************************************************************************** ");
		Controller
		.message("\nTotal time taken to execute all the test cases (End to End) is -> "
				+ controller.executionTime + " milli Seconds.");
		Controller
		.message("\n******************************************************************************** ");
		if (!opts.verbose) {
			System.out
			.println("\n******************************************************************************** ");
			System.out
			.println("\nTotal time taken to execute all the test cases (End to End) is -> "
					+ controller.executionTime + " milli Seconds.");
			System.out
			.println("\n******************************************************************************** ");
		}
		controller.DoHarnessCleanup();

	}

}
