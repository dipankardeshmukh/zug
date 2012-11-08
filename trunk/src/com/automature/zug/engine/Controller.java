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
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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

	private String helpMessage = StringUtils.EMPTY;
	private String versionMessage = StringUtils.EMPTY;
	Excel readExcel;
	private static String baseTestCaseID = StringUtils.EMPTY;
	private int initializationTime = 0;
	private int executionTime = 0;
	public static long harnessPIDValue = 0;
	ProgramOptions opts = null;
	public static boolean verbose = true;
	private static boolean debugMode = true;
	public static boolean dbReporting = true;
	private static boolean compileMode = false;
	private static boolean verificationSwitching = true;
	private static boolean doCleanupOnTimeout = false;
	private static boolean forceExecution = false;
	public static boolean nyonserver = false; // Flag which checks whether its
	// needed to generate the XML
	// file or not.
	public static boolean macroentry = false; // Flag to set a Command Line
												// Macro Value
	public static boolean includeFlag = true; // A new Flag which checks whether
	// the inlcude switch is enabled
	// or not
	// private Hashtable<String, List<BusinessLayer.Variable>>
	// TestCaseVariableValue = new Hashtable<String,
	// List<BusinessLayer.Variable>>();
	// By default we will assume that the Longevity is OFF
	// and will ON this when there is a Longevity test going on..
	public static boolean isLongevityOn = false;

	// / By Default the Repeat Count is 1
	private static int repeatCount = 1;
	private static String testsToRepeat = StringUtils.EMPTY;
	// By default we will assume that the RepeatDuration is not specified.
	private static boolean repeatDurationSpecified = false;
	private static int repeatDuration = 0;
	private static double repeatDurationLong = 0;
	// Change this Number every time the Harness is Released.
	private static String Version = "ZUG Premium 5.5." + "20121107" + ".110";
	private static Hashtable<String, String> errorMessageDuringTestCaseExecution = new Hashtable<String, String>();
	private static Hashtable<String, String> errorMessageDuringMoleculeCaseExecution = new Hashtable<String, String>();
	private static Hashtable<String, String> threadIdForTestCases = new Hashtable<String, String>();
	private static Hashtable<String, String[]> fileExtensionSupport = new Hashtable<String, String[]>();
	// Assuming that the Test Plan Initialization will Work Fine.
	boolean initWorkedFine = true;
	private static String manualTestCaseID = StringUtils.EMPTY;
	private static String excludeTestCaseID = StringUtils.EMPTY;
	private static String testcasesNotPresent = StringUtils.EMPTY;
	private Hashtable<String, ExecutedTestCase> executedTestCaseData = new Hashtable<String, ExecutedTestCase>();
	// Hashtable to store the name key value pair of the Command line macro
	// entry
	public static HashMap<String, String> macrocommandlineinputs = new HashMap<String, String>();
	// Hashtable to store the Abstract TestCase Name as KEY and the TestCase
	// Object as Value.
	private Hashtable<String, TestCase> abstractTestCase = new Hashtable<String, TestCase>();
	TopologySet[] TopologySet = null;
	// Some Internal Variables of the Controller.
	// Path of the Input TestCase Excel sheet
	private String topologySetXMLFile = StringUtils.EMPTY;
	private String topologySetId = StringUtils.EMPTY;
	private String inputFile = StringUtils.EMPTY; // @"C:\Documents and
	// Settings\gurpreet_anand\Desktop\CPAPI_PrimaryTestCases
	// WithFilePath.xls"; //
	private String scriptLocation = StringUtils.EMPTY;
	public static String includeMolecules = StringUtils.EMPTY;
	public static String pwd = StringUtils.EMPTY;
	@SuppressWarnings("unused")
	private String verificationVar = StringUtils.EMPTY;
	private String dBHostName = StringUtils.EMPTY;
	private String dBName = StringUtils.EMPTY;
	private String dbUserName = StringUtils.EMPTY;
	private String dbUserPassword = StringUtils.EMPTY;
	private String testSuitName = StringUtils.EMPTY;
	private String testSuiteId = null;
	private String testSuitRole = StringUtils.EMPTY;
	private String validTopoDetail = StringUtils.EMPTY;
	private String TestPlanId = StringUtils.EMPTY;
	private String TestPlanPath = StringUtils.EMPTY;
	private String BuildTag = StringUtils.EMPTY;
	private String TopologySetName = StringUtils.EMPTY;
	private String BuildNo = StringUtils.EMPTY;
	private String testCycleId = StringUtils.EMPTY;
	private String[] productLogFiles = null;
	private Hashtable<String, Prototype> prototypeHashTable = null;
	private static Boolean _testPlanStopper = false;
	private static Hashtable _testStepStopper = new Hashtable();
	// variable to store context variable for archiving
	public static String TOPOSET = null;
	// private String TPID = null;
	private String TESTSUITEID = null;
	public static String OS_NAME = "", OS_VERSION = "", OS_ARCH = "";
	public static String USER_HOME = "", USER_LANG = "", USER_NAME = "";
	public static String SLASH = "";
	public static String SEPARATOR = "";
	public static String PATH_CHECK = "";
	public static String LOG_DIR = "", ZIP_DIR = "", LOGLOCATION = "";
	public static boolean OS_FLAG;
	public static String JavaVersion = "", JavaHome = "", JavaCompiler = "",
			JavaLibraryPath = "";
	public static String mvmconfiguration = "512MB";
	public static String ZUG_LOGFILENAME = "";
	// Initiating AtomInvoker
	// public static AtomInvoker invokeAtoms=null;
	public static final String inprocess_jar_xml_tag_path = "//root//inprocesspackages//inprocesspackage";
	public static final String inprocess_jar_xml_tag_attribute_name = "name";
	public static final String native_inprocess_xml_tag_path = "//root//inprocesspackages//inprocesspackage";
	public static final String inprocess_xml_tag_attribute_language = "language";
	public static HashMap<String, AtomInvoker> invokeAtoms = new HashMap<String, AtomInvoker>();
	public static HashMap<String, AtomInvoker> invoke_native_atoms = new HashMap<String, AtomInvoker>();
	private static volatile String builtin_atom_package_name = "";
	// Web Service Connection
	static DavosClient davosclient = null;
	private boolean testcasenotfoundflag = false;
	private static String testcasenotran;

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

		/*
		 * helpMessagebuf.append(
		 * "\n\n\n\n\t TestsToRepeat= Comma Separated list of  Automated Test Case ID's which one need to "
		 * ); helpMessagebuf.append(
		 * "\n\t\t run in iteration till the condition(RepeatCount or RepeatDuration)is met. "
		 * ); helpMessagebuf.append(
		 * "\n\t\t This is to support LONGEVITY support in ZUG.");
		 * helpMessagebuf.append(
		 * "\n\t\t This argument holds significance only when RepeatCount or RepeatDuration argument is specified. "
		 * ); helpMessagebuf.append(
		 * "\n\t\t By Default(if this argument is not provided) the ZUG will execute all the test cases specified in \"TestCases\" sheet in iteration."
		 * ); helpMessagebuf.append(
		 * "\n\t\t Note: ZUG Debug Logs are turned OFF during LONGEVITY to ensure ZUG is not taking more disk space."
		 * );
		 */
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
		/*
		 * dbreporting switch is taken out and is implicitly understoods by the
		 * testplanid and topologysetXML options which trivially follows that
		 */
		// helpMessagebuf.append("\n\n\n\n\t dbreporting=[=TRUE/FALSE] : To put the test case execution result to the Framework Database. By default will be ON");
		// helpMessagebuf.append("\n\t\t unless explicitly mentioned ");

		helpMessagebuf.append("\n\n\t For example ");
		helpMessagebuf
				.append("\n\t------------------------------------------------------------------");
		helpMessagebuf
				.append("\n\t For eg. runZUG.bat \"c:\\input.xls\" -TestcaseID=ID01,ID02 ");
		helpMessagebuf
				.append("\n\t------------------------------------------------------------------\n\n");
		helpMessagebuf.append("\n--NOTE--\n");

		// helpMessagebuf.append(" Options specified on the command line must be of the form 'name=value'\n");
		// helpMessagebuf.append(" with no whitespace before or after the equals sign.  If the option's value\n");
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
		message("Zug is running in Java Version: " + JavaVersion
				+ "\nJava Maximum Memory Size: "
				+ Utility.getMaxJVMMemorySize(jvm));
		message("Java Home: " + JavaHome + "\nJava Compiler: " + JavaCompiler
				+ "\nJava Library Path: " + JavaLibraryPath);
		message("Zug is running in Operating System: " + OS_NAME
				+ "\nOperating System Version: " + OS_VERSION
				+ "\nOperating System Architecture: " + OS_ARCH);
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

	public void message(String msg) {
		Log.Result(msg);

		if (verbose) {
			System.out.println(msg);

		}
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

		try {
			// check if debug mode is set to true. else default value is True
			if ((debugModeVal = opts.getString("debug", null)) == "true") {
				Log.Debug("Controller/GetOptions: Debug switch Mode is specified.");
				Log.Debug("Controller/GetOptions: DebugModeVal = true");
				debugMode = true;

			} else if ((debugModeVal = opts.getString("nodebug", null)) == "true") {
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
			if ((doCleanupOnTimeoutVal = opts.getString("noautorecover", null)) == "true") {
				Log.Debug("Controller/GetOptions: NoAutoRecover switch is specified.");
				Log.Debug("Controller/GetOptions: doCleanupOnTimeout = false");
				// Lets make it FALSE by default
				doCleanupOnTimeout = false;
			} else if ((doCleanupOnTimeoutVal = opts.getString("autorecover",
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
			if ((repeatDurationVal = opts.getString("Duration", null)) == null) {
				Log.Debug("Controller/GetOptions: -Duration is not specified. By Default its value is Empty.");

				repeatDurationSpecified = false;
			} else {
				if ((opts.getString("repeat", null) == null)) // ||(opts.getString("norepeat",
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

			if ((repeatCountVal = opts.getString("Count", null)) == null) {
				Log.Debug("Controller/GetOptions: -Count is not specified. By Default its value is 1.");

				// Lets make it true by default
				repeatCount = 1;

			} else {
				if ((opts.getString("repeat", null) == null)) // ||(opts.getString("norepeat",
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

			if ((verboseFlagVal = opts.getString("verbose", null)) == "true") {
				Log.Debug("Controller/GetOptions: Verbose Flag is = true");
				verbose = true;
			} else if ((verboseFlagVal = opts.getString("noverbose", null)) == "true") {
				verbose = false;
				Log.Debug("Controller/GetOptions: Verbose Flag is = false");
			} else {
				// By default Verbose option is OFF and set to FALSE, unless
				// explicitly switched ON

				Log.Debug("Controller/GetOptions: Verbose flag is = false by default");

				verbose = false;

			}

			if ((compileModeFlagVal = opts.getString("execute", null)) == "true") {
				Log.Debug("Controller/GetOptions: execute switch found. Compile Mode = false");
				compileMode = false;
			} else if ((compileModeFlagVal = opts.getString("noexecute", null)) == "true") {
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
			if ((dbReportingVal = opts.getString("dbreporting", null)) == null) {
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
			if ((verificationSwitchingVal = opts.getString("verify", null)) == "true") {
				// verificationSwitching is ON and set to TRUE, unless
				// explicitly switched OFF
				Log.Debug("Controller/GetOptions: verify Switch Flag is = ON.");
				verificationSwitching = true;
			} else if ((verificationSwitchingVal = opts.getString("noverify",
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

			if ((scriptLocation = opts.getString("atompath", null)) == null) {
				Log.Debug("Controller/GetOptions: Atom Scripts is not specified.");
				scriptLocation = StringUtils.EMPTY;
			} else {
				Log.Debug("Controller/GetOptions: Atom Scripts specified by user is = "
						+ scriptLocation);
			}

			if ((includeMolecules = opts.getString("include", null)) == null) {
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
			if ((pwd = opts.getString("pwd", null)) == null) {
				Log.Debug("Controller/Getoptions: Working Directory specified : "
						+ pwd);

				pwd = StringUtils.EMPTY;
			} else {

				Log.Debug("Controller/Getoptions: Present Working Directory : "
						+ pwd);
			}
			if ((manualTestCaseID = opts.getString("testcaseID", null)) == null) {
				Log.Debug("Controller/GetOptions: ManualTestCase ID is not specified.");
				manualTestCaseID = StringUtils.EMPTY;
				excludeTestCaseID = opts.getString("excludeTestCaseID", null);
				// message("No specific testcase is invoked\n All testcases will be executed.");
			} else {
				 //message("The manual Testcase "+manualTestCaseID);
				if(manualTestCaseID.contains(".")||manualTestCaseID.contains("/")||manualTestCaseID.contains(":")||manualTestCaseID.contains(";"))
				{
					//message("it contains . "+manualTestCaseID);
					Log.Error("[Error]Multiple testcaseid must separate by , (comma) wrong inputs: "+manualTestCaseID);
					System.exit(1);
				}
				Log.Debug("Controller/GetOptions: ManualTestCase ID specified by user is = "
						+ manualTestCaseID);
				if ((excludeTestCaseID = opts.getString("excludetestcaseID",
						null)) == null) {
					excludeTestCaseID = StringUtils.EMPTY;
				}
			}

			if ((inputFile = opts.getString("inputfile", null)) == null) {
				Log.Error("Controller/GetOptions: Error : missing input file.");
				System.out.println("\n\nMissing required value : Input File "
						+ "\n Use -help for Usage information\n\n");
				Log.Debug("Controller/GetOptions: Function returns FALSE. End of Function.");
				return false;
			} else {
				Log.Debug("Controller/GetOptions: input = " + inputFile);
			}

			if ((testCycleId = opts.getString("testcycleid", null)) == null) {
				Log.Debug("Controller/GetOptions: TestCYcle ID specified from Command Prompt is NULL/Empty.");
			} else {
				// Harness Specific ContextVariable to store TestCycle ID
				ContextVar.setContextVar("ZUG_TCYCID", testCycleId);

				Log.Debug("Controller/GetOptions: testcycleid = " + testCycleId);
			}

			/*
			 * dbreporting option argument has been removed and implicitly set
			 * by testplanid and topologysetXML values
			 */
			// if (dbReporting)
			// {

			dbReporting = false;
			if ((TestPlanId = opts.getString("testplanid", null)) == null) {
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

			if ((TestPlanPath = opts.getString("testplan", null)) == null) {
				testplanpathVal = false;
				Log.Debug("Controller/GetOptions: testplanpath not specified. dbreporting os OFF");
			} else {
				ContextVar.setContextVar("ZUG_TESTPLANPATH", TestPlanPath);
				Log.Debug("Controller/GetOptions: TestPlanId = " + TestPlanPath);
				// message("The testplanpath is "+TestPlanPath);
				// message("ContextVar set "+ContextVar.getContextVar("ZUG_TESTPLANPATH"));
				testplanpathVal = true;
			}
			if ((topologySetId = opts.getString("topologysetid", null)) == null) {
				boolean topologysetnameexists = false;
				if (StringUtils.isNotBlank(opts.getString("topologyset", null))) {
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
				if (testplanidVal || testplanpathVal) {// put or checking for
														// the new value
														// testplanpathvalue=true
					if (compileMode) {
						Log.Debug("Controller/GetOptions: dbReportingVal is OFF as CompileMode is ON - Check Syntax mode is true.");
						dbReporting = false;
					} else {
						if (topologysetname) {
							Log.Error("Controller/GetOptions: Error: Toplogy name is already used.");
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
				} else {
					Log.Error("Controller/GetOptions: Error: missing testplanid or testplan.");
					System.out
							.println("\n\nMissing required value: testplanid or testplanid"
									+ "\n Use -help/-h for Usage Information\n\n");
					Log.Debug("Controller/GetOptions: Function returns FALSE. End of Function.");
					return false;
				}
			}
			if ((TopologySetName = opts.getString("topologyset", null)) == null) {
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
				if (testplanidVal || testplanpathVal) {// put or checking for
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
					Log.Error("Controller/GetOptions: Error: missing testplan or testplanid.");
					System.out
							.println("\n\nMissing required value: testplan or testplanid "
									+ "\n Use -help/-h for Usage Information\n\n");
					Log.Debug("Controller/GetOptions: Function returns FALSE. End of Function.");
					return false;
				}
			}
			if ((topologySetId = opts.getString("topologysetid", null)) == null) {
				if (testplanidVal && !topologysetname) {
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
				if (testplanidVal || testplanpathVal) {// put or checking for
														// the new value
														// testplanpathvalue=true
					if (compileMode) {
						Log.Debug("Controller/GetOptions: dbReportingVal is OFF as CompileMode is ON - Check Syntax mode is true.");
						dbReporting = false;
					} else {
						if (topologysetname) {
							Log.Error("Controller/GetOptions: Error: Toplogy name is already used.");
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
				} else {
					Log.Error("Controller/GetOptions: Error: missing testplanid or testplan.");
					System.out
							.println("\n\nMissing required value: testplanid or testplanid"
									+ "\n Use -help/-h for Usage Information\n\n");
					Log.Debug("Controller/GetOptions: Function returns FALSE. End of Function.");
					return false;
				}
			}
			if ((BuildTag = opts.getString("buildtag", null)) == null) {

				Log.Debug("Controller/GetOptions: buildtag not specified. dbreporting os OFF");
			} else {
				ContextVar.setContextVar("ZUG_BUILDTAG", BuildTag);
				Log.Debug("Controller/GetOptions: BuildTag = " + BuildTag);
				// message("The testplanpath is "+TestPlanPath
				// message("ContextVar set "+ContextVar.getContextVar("ZUG_TESTPLANPATH"));

			}
			/*
			 * if ((topologySetXMLFile = opts.getString("topologysetXML", null))
			 * == null) { if(testplanidVal) { Log.Error(
			 * "Controller/GetOptions: Error : missing Topology Set XML File.");
			 * System
			 * .out.println("\n\nMissing required value : Topology Set XML File "
			 * + "\n Use -help/-h for Usage information\n\n"); Log.Debug(
			 * "Controller/GetOptions: Function returns FALSE. End of Function."
			 * ); return false; } } else {
			 * Log.Debug("Controller/GetOptions: topologySetXMLFile = " +
			 * topologySetXMLFile); if(testplanidVal) { if (compileMode) {
			 * Log.Debug(
			 * "Controller/GetOptions: dbReportingVal is OFF as CompileMode is ON - Check Syntax mode is true."
			 * ); dbReporting = false; } else { // dbReporting option is ON as
			 * both testplanID and topologysetXML is specified
			 * Log.Debug("Controller/GetOptions: dbReportingVal ON.");
			 * dbReporting = true; } } else {
			 * Log.Error("Controller/GetOptions: Error: missing testplanid.");
			 * System.out.println("\n\nMissing required value: testplanid " +
			 * "\n Use -help/-h for Usage Information\n\n"); Log.Debug(
			 * "Controller/GetOptions: Function returns FALSE. End of Function."
			 * ); return false; } }
			 */
			// } commented out for removing the if(dbreporting) block
		} catch (Exception e) {
			Log.Debug("Controller/GetOptions: Exception occured,"
					+ e.getMessage());
			e.printStackTrace();
		}

		return true;
	}

	/***
	 * Function to Show the Test Case Report at the End, even if the DB
	 * reporting is OFF.
	 */
	private void ShowTestCaseResultONConsole() {
		ExecutedTestCase[] executedTestCase = new ExecutedTestCase[executedTestCaseData
				.size()];

		int count = 0;
		// Set<String> TestCaseKey = executedTestCaseData.keySet();
		Set<Map.Entry<String, ExecutedTestCase>> testCaseSet = executedTestCaseData
				.entrySet();
		List<Map.Entry<String, ExecutedTestCase>> testCaseList = new ArrayList(
				testCaseSet);
		Collections.sort(testCaseList, new TestCaseDateComparator());
		Iterator it = testCaseList.iterator();
		while (it.hasNext()) {
			Entry<String, ExecutedTestCase> TestCaseEntry = (Entry<String, ExecutedTestCase>) it
					.next();
			String s = TestCaseEntry.getKey();
			executedTestCase[count++] = (ExecutedTestCase) executedTestCaseData
					.get(s);
		}
		if (executedTestCase.length > 0) {
			message("\nFollowing are the Details of the TestCases Result Executed by ZUG Version -> "
					+ Version);

			message("\nTestCase ID \t Status \t Time Taken(In milli-seconds) \t Comments\n ");
			if (!verbose) {
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
			if (!verbose) {
				System.out.println("\n" + testCaseResult.get_testCaseId()
						+ "\t" + testCaseResult.get_status() + "\t\t "
						+ testCaseResult.get_testExecution_Time() + "\t\t "
						+ testCaseResult.get_comments());
			}
		}
		if (StringUtils.isNotBlank(testcasenotran)) {
			System.out.println("\nTest Case Failed to Execute: "
					+ testcasenotran);
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

	/***
	 * Function to Save the Test Case Result.
	 */
	private void SaveTestCaseResult() throws Exception, DavosExecutionException {
		Log.Debug("Controller/SaveTestCaseResult : Start of the Function");

		// BusinessLayer.TestCycle testCycle = new BusinessLayer.TestCycle();
		String buildNumber = BuildNo;
		// message("SaveTestCaseResult::\t The result to Save in a TEST case result  ");
		// BusinessLayer.TestCycleData testCycleData = new
		// BusinessLayer.TestCycleData();

		// if (testCycleId == null || StringUtils.isEmpty(testCycleId)) {
		// //testCycleData.setTestCycleId(null);
		// } else {
		// // testCycleData.setTestCycleId(Integer.parseInt(testCycleId));
		// }
		// //testCycleData.setExecutionTime(executionTime);
		// //testCycleData.setInitializationTime(initializationTime);

		// testCycleData.setTestplan_id(Integer.parseInt(TestPlanId));
		Log.Debug("Controller/SaveTestCaseResult : Saving TestCycle ID "
				+ testCycleId + " and Test Plan ID " + TestPlanId
				+ " of Test Plan " + testSuitName);

		message("Saving Result for TestCycle ID " + testCycleId
				+ " and Test Plan ID " + TestPlanId + " of Test Plan "
				+ testSuitName);
		message("\n-----------------------------------------------------------------------------------------------------------------------");
		message("\n-----------------------------------------------------------------------------------------------------------------------");

		// BusinessLayer.TopologySetResultData topologyResultData = new
		// BusinessLayer.TopologySetResultData();
		// topologyResultData.s.TopologyId = string.Empty;

		// if (TopologySet.length > 0) {
		// message("\nFollowing are the Details of the Topology");
		// message("\nTopology ID \t Role \t Build Number\n");
		// }
		//
		// Log.Debug("Controller/SaveTestCaseResult : Number of Topology Set to add is "
		// + TopologySet.length); // TODO Discuss the way of saving the details
		// through DAVOS ...
		// for (int i = 0; i < TopologySet.length; i++) {
		// // BusinessLayer.TopologyDetail testCasesExecutedMachine = new
		// BusinessLayer.TopologyDetail();
		// // topologyResultData.TopologyId += "_" + TopologySet[i].topologyID;
		// //testCasesExecutedMachine.set_topologyId(Integer.parseInt(TopologySet[i].topologyID));
		// //
		// testCasesExecutedMachine.set_buildNumber(TopologySet[i].buildNumber);
		// if
		// (TopologySet[i].topologyRole.compareToIgnoreCase("Local Topology Name")
		// == 0) {// need
		// // to
		// // change
		// // for
		// // test
		// // plan
		// // name
		// // testCasesExecutedMachine.set_role(testSuitRole);
		// // buildNumber = testCasesExecutedMachine.get_buildNumber();
		// } else {// need to change for test plan name
		// // testCasesExecutedMachine.set_role(TopologySet[i].topologyRole);
		// }
		//
		// // need to change for test plan name
		Log.Debug("Controller/SaveTestCaseResult : TopologyName = "
		// + testCasesExecutedMachine.get_topologyId()
				+ " BuildNumber = "
				// + testCasesExecutedMachine.get_buildNumber()
				// + " and  Role= " + testCasesExecutedMachine.get_role()
				+ " will be saved.");
		// topologyResultData.get_topologyDetailList().add(
		// testCasesExecutedMachine);

		// message("\n" + testCasesExecutedMachine.get_topologyId() + "\t"
		// + testCasesExecutedMachine.get_role() + "\t"
		// + testCasesExecutedMachine.get_buildNumber());
		// // }
		if(executedTestCaseData.size()>0)
		{
		ExecutedTestCase[] executedTestCase = new ExecutedTestCase[executedTestCaseData
				.size()];

		int count = 0;
		Set<String> TestCaseKey = executedTestCaseData.keySet();
		for (String s : TestCaseKey) {
			executedTestCase[count++] = (ExecutedTestCase) executedTestCaseData
					.get(s);
		}

		Log.Debug("Controller/SaveTestCaseResult : Number of TestCase Status to add is "
				+ executedTestCase.length);

		if (executedTestCase.length > 0) {
			message("\nFollowing are the Details of the TestCases Result getting added to the "
					+ dBHostName + "/" + " through Davos Web Service.");// TODO
																		// Change
																		// for
																		// Davos
																		// latest
																		// updates
			message("\nTestCase ID \t Status \t Time Taken(In mili-seconds) \t Comments\n ");
		}

		for (int i = 0; i < executedTestCase.length; i++) {
			TestCaseResult testCaseResult = new TestCaseResult();
			testCaseResult.set_testCaseId(executedTestCase[i].testCaseID);
			// testCaseResult.set_testSuiteName(testSuitName);
			if (StringUtils.isNotBlank(testSuiteId)) {
				testCaseResult.set_testSuiteId(Integer.valueOf(testSuiteId)
						.intValue());
			}
			// else
			// testCaseResult.set_testSuiteId(2012);
			testCaseResult.set_testEngineerName("Automation");
			testCaseResult
					.set_executionDate(executedTestCase[i].testCaseCompletetionTime);
			testCaseResult
					.set_testExecution_Time(executedTestCase[i].timeToExecute);
			testCaseResult.set_status(executedTestCase[i].testCaseStatus);
			testCaseResult
					.set_comments(executedTestCase[i].testCaseExecutionComments);

			testCaseResult.set_buildNo(StringUtils.EMPTY);
			if (StringUtils.isNotBlank(buildNumber)) {
				testCaseResult.set_buildNo(buildNumber);
			}

			// Log.Debug("Controller/SaveTestCaseResult : TestCaseId = "+testCaseResult.get_testCaseId()+" TestSuiteId = "+testCaseResult.get_testSuiteName()+" Status = "+testCaseResult.get_status()+" and Comments = "+testCaseResult.get_comments()+" will be saved.");
			Log.Debug("Controller/SaveTestCaseResult : TestCaseIdentifier = "
					+ testCaseResult.get_testCaseId() + " TestSuiteId = "
					+ testCaseResult.get_testSuiteId() + " Status = "
					+ testCaseResult.get_status() + " and Comments = "
					+ testCaseResult.get_comments() + " will be saved.");
			// topologyResultData.get_testCaseResultList().add(testCaseResult);
			testCycleId = davosclient.testCycle_write(TestPlanId, ContextVar
					.getContextVar("ZUG_TCYCLENAME"), "", "", new Integer(
					initializationTime).toString(),
					new Integer(testCaseResult.get_testExecution_Time())
							.toString());
			message("\n" + testCaseResult.get_testCaseId() + "\t"
					+ testCaseResult.get_status() + "\t"
					+ testCaseResult.get_testExecution_Time() + "\t"
					+ testCaseResult.get_comments());
			if (testCycleId == null) {
				Log.Debug("Controller/SaveTestCaseResultEveryTime : TestCycle=Null");
				// // message("No testcycle provided  ");
			} else {

				Log.Debug("Controller/SaveTestCaseResultEveryTime : TestCycle="
						+ testCycleId);
				// davosclient.testExecutionDetails_write(testCaseResult.get_testCaseId(),
				// testCycleId,
				// testCaseResult.get_status(),testCaseResult.get_buildNo(), new
				// Integer(testCaseResult.get_testExecution_Time()).toString(),testCaseResult.get_performanceExecutionDetailTable().get_performanceExecutionDetailId()
				// , testCaseResult.get_executionDate().toString(),
				// testSuitName);
				// message("Test Execution is done  "+testCycleId);
			}
			// testCycleData.get_topologySetResultDataList().add(topologyResultData);
			//
			// Log.Debug("Controller/SaveTestCaseResult : Calling testCycle.Save method with TestCycle Data filled above.");
			//
			// if (testCycle == null) {
			// Log.Debug("Controller/SaveTestCaseResultEveryTime : TestCycle=Null");
			// } else {
			// try {
			// Log.Debug("Controller/SaveTestCaseResultEveryTime : TestCycle="
			// + testCycle.toString()
			// + ", TestCycleExist= "
			// + (StringUtils.isBlank(testCycleId) ? "Null"
			// :
			// testCycle.TestCycleExist(Integer.parseInt(testCycleId))).toString());
			//
			// // For all topologies
			// testCycle.Save(testCycleData);
			// } catch (Exception e) {
			// Log.Error("Controller/SaveTestCaseResultEveryTime : Exception occured while saving test cases"
			// + e.getMessage());
			// throw new Exception(
			// "Controller/SaveTestCaseResultEveryTime : Exception occured while saving test cases"
			// + e.getMessage());
			//
			// }
		}
		//
		// Log.Debug("Controller/SaveTestCaseResult : SUCCESSFULLY SAVED THE TestCycle Data filled above.");
		// message("\n\nSUCCESSFULLY SAVED Result for TestCycle "
		// + testCycle.get_testCycleMessage() + " and TestPlan ID  "
		// + testCycleData.getTestplan_id() + " of Test Suit "
		// + testSuitName);
		}
		else
		{
			String tcycle=ContextVar.getContextVar("ZUG_TCYCLENAME");
			if(tcycle!=null)
			{	message("\n No execution found to Report updating the TestCycle "+tcycle+" to "
					+ dBHostName + "/" + " through Davos Web Service.");
			
			testCycleId = davosclient.testCycle_write(TestPlanId, ContextVar
					.getContextVar("ZUG_TCYCLENAME"), "", "", new Integer(
					initializationTime).toString(),
					new Integer(executionTime).toString());
			}
		}
		message("\n-----------------------------------------------------------------------------------------------------------------------");
		message("\n-----------------------------------------------------------------------------------------------------------------------");

		Log.Debug("Controller/SaveTestCaseResult : End of the Function");
	}

	/**
	 * Function to check the consistent of the arguments.
	 * 
	 * @param arguments
	 *            arguments as ArrayList<String>
	 * @return boolean true or false
	 */
	private boolean isParameterPassingConsistent(ArrayList<String> arguments,
			TestCase temp) throws Exception {
		boolean equalsPresent = true, equalsNotPresent = true;
		// Looping to get every arguments from the arraylist
		for (String args : arguments) {
			// Checking every argument if it contains any = or not
			// message("args are "+args);
			if (args.contains("=")) {
				// message("Coming here?? = check");
				// Putting the proposition logic Boolean AND operation
				equalsPresent = true && equalsPresent;
				equalsNotPresent = false && equalsNotPresent;

			} else {
				// message("argument length "+arguments.size());
				// message("The definitions "+temp._testcasemoleculeArgDefn+"\nLength "+temp._testcasemoleculeArgDefn.size());
				if (arguments.size() != temp._testcasemoleculeArgDefn.size()) {
					throw new Exception(
							"Named Argument size mismatch between pass by value and the definition :\n\tNo of Arguments in Molecule Definition: "
									+ temp._testcasemoleculeArgDefn.size()
									+ "\n\tNo of Arguments Passed to the Molecule: "
									+ arguments.size());
				}
				equalsNotPresent = true && equalsNotPresent;
				equalsPresent = false && equalsPresent;
			}

		}
		// message("last answer "+equalsNotPresent+" and "+equalsPresent);
		// Boolean OR Operation
		return equalsPresent || equalsNotPresent;
	}

	/***
	 * Function to run a particular action of a testcase.
	 * 
	 * @param realValue
	 *            realValue as String
	 * @return tunedValue String return tuned and edited.
	 */
	@Deprecated
	private String getActualDefnValue(String realValue) throws Exception {
		final String HASH = "#", PERCENT = "%", BLANK = "";
		String tunedValue = "";
		if (realValue.startsWith(PERCENT) && realValue.endsWith(PERCENT)) {
			realValue = realValue.replaceAll(PERCENT, BLANK);
			tunedValue = ContextVar
					.getContextVar(getActualDefnValue(realValue));// Recurrsion

		} else {
			tunedValue = realValue;
		}

		return tunedValue;
	}

	/***
	 * Function to check if named argument
	 * 
	 * @param actionVal
	 *            String.
	 */
	private boolean checkIfNamedArgument(String actionValue) {
		boolean namedArgFlag = false;
		// message("the count " + StringUtils.countMatches(actionValue, "#") +
		// " for " + actionValue + " String char length " +
		// actionValue.toCharArray().length);
		if (actionValue.toLowerCase().startsWith("#")
				|| actionValue.toLowerCase().startsWith("%#")) {
			namedArgFlag = true;

		}
		if (actionValue.toLowerCase().contains("=#")
				|| actionValue.toLowerCase().contains("=##")
				|| actionValue.toLowerCase().contains("=%#")) {
			namedArgFlag = true;
		} else if (StringUtils.countMatches(actionValue, "#") >= 1
				&& !actionValue.endsWith("##")) {

			if (actionValue.toCharArray().length > 1) {
				namedArgFlag = true;
			} else {
				namedArgFlag = false;
			}
		}
		return namedArgFlag;
	}

	/**
	 * Method to Replace the namedargument from a String
	 * 
	 * @param String
	 *            ,String,String source,searchS,replaceS
	 */
	private String replaceStringOnly(String source, String searchS,
			String replaceS) {

		Pattern pattrn = Pattern.compile("(\\b" + searchS.toLowerCase()
				+ ")(?)");

		Matcher matchr = pattrn.matcher(source.toLowerCase());
		StringBuffer bufferString = new StringBuffer();
		while (matchr.find()) {
			matchr.appendReplacement(bufferString, replaceS);
		}
		matchr.appendTail(bufferString);
		// message("Source: "+source+" Search: "+searchS+" Replace: "+replaceS+" replaced: "+bufferString.toString());

		return bufferString.toString();
	}

	/**
	 * Method to sort a ArrayList<String> according to length of the strings
	 * 
	 * @param ArrayList
	 *            <String> unsortedlist
	 * @retrun sortedlist ArrayList<String>
	 */
	private ArrayList<String> sortListByLength(ArrayList<String> unsortedList) {
		ArrayList<Integer> lengthlist = new ArrayList<Integer>();
		for (String elemnt : unsortedList) {
			Integer length = elemnt.length();
			lengthlist.add(length);
		}
		Comparator compr = Collections.reverseOrder();
		Collections.sort(lengthlist, compr);
		ArrayList<String> sortedlist = new ArrayList<String>();
//		for (int i = 0; i < lengthlist.size(); i++) {
//			int count_string = lengthlist.get(i);
//			for (int j = 0; j < unsortedList.size(); j++) {
//				String tempstring = unsortedList.get(j);
//				if (tempstring.length() == count_string) {
//					sortedlist.add(tempstring);
//					break;
//				}
//			}
//
//		}
		String tempArray[]=new String[unsortedList.size()];
		unsortedList.toArray(tempArray);
		Arrays.sort(tempArray);
		for(int i=0;i<tempArray.length;i++)
		{
			//System.out.println("The values "+tempArray[i]);
			sortedlist.add(tempArray[i]);
		}
		return sortedlist;
	}

	/**
	 * Method to get the index of Positional argument
	 * 
	 * @param testmoleculedefn
	 *            ArrayList<String>
	 * @return indexno Integer
	 */
	// TODO this method is total broken discuss and change it
	private int getMoleculeDefinitionIndex(ArrayList<String> moleculearglist,
			String valuetomatch) throws Exception {
		int count_index = 0;
		boolean argnotfound = true;
		//message("The molecule arglist "+moleculearglist);
		// create a reverse order list checking with the String inputs lengths
		ArrayList<String> reverselist = sortListByLength(moleculearglist);
		//message("The reverse order "+reverselist);
		for (int i = 0; i < reverselist.size(); i++) {
			String molecule_arg = reverselist.get(i);
			 //message("moleculearg "+molecule_arg+" values to match "+valuetomatch);//\n
			// Count matching "+
			// StringUtils.countMatches(valuetomatch,molecule_arg));
			// if (StringUtils.countMatches(valuetomatch, molecule_arg)==1) {
			if (valuetomatch.contains("#" + molecule_arg)) {
				// if(molecule_arg.matches("("+valuetomatch+")")){

				// message("count matching "+molecule_arg+" index "+i);
				count_index = i;
				argnotfound = false;
				break;
			}

			else {
				argnotfound = true;

			}

		}
		if (argnotfound) {
			throw new Exception("Argument not present in Molecule Definition");
		}
		//message("Count Index from getMolIndex "+moleculearglist.indexOf(reverselist.get(count_index)));
		return moleculearglist.indexOf(reverselist.get(count_index));
	}

	/***
	 * Function to Execute a particular Abstract testcase.
	 * 
	 * @param test
	 *            DataClass of the TestCase.
	 */
	// TODO check for named argument positional
	private void RunAbstractTestCase(TestCase test,
			ArrayList<String> argumentValues, String parentTestCaseID,
			String callingtTestCaseSTACK) throws Exception {
		Log.Debug("Controller/RunAbstractTestCase: Start of function with a new TestCase. TestCase ID is "
				+ test.testCaseID
				+ " and parentTestCaseID = "
				+ parentTestCaseID);

		TestCase tempTestCase = new TestCase();
		tempTestCase.automated = test.automated;
		Log.Debug("Controller/RunAbstractTestCase: tempTestCase.automated = "
				+ tempTestCase.automated);

		tempTestCase.nameSpace = test.nameSpace;
		Log.Debug("Controller/RunAbstractTestCase: tempTestCase.nameSpace = "
				+ tempTestCase.nameSpace);

		tempTestCase.concurrentExecutionOnExpansion = test.concurrentExecutionOnExpansion;
		Log.Debug("Controller/RunAbstractTestCase: tempTestCase.concurrentExecutionOnExpansion = "
				+ tempTestCase.concurrentExecutionOnExpansion);

		tempTestCase.testCaseDescription = test.testCaseDescription;
		Log.Debug("Controller/RunAbstractTestCase: tempTestCase.testCaseDescription = "
				+ tempTestCase.testCaseDescription);
		tempTestCase.testCaseID = test.testCaseID;
		Log.Debug("Controller/RunAbstractTestCase: tempTestCase.testCaseID = "
				+ tempTestCase.testCaseID);

		tempTestCase.parentTestCaseID = parentTestCaseID;
		Log.Debug("Controller/RunAbstractTestCase: tempTestCase.parentTestCaseID = "
				+ tempTestCase.parentTestCaseID);

		// Get the thread ID of the parent
		tempTestCase.threadID = (String) threadIdForTestCases
				.get(callingtTestCaseSTACK);

		tempTestCase.stackTrace = callingtTestCaseSTACK + "_" + test.stackTrace;

		tempTestCase.user = test.user;
		tempTestCase.userObj = test.userObj;

		Action[] actions = new Action[test.actions.size()];
		test.actions.toArray(actions);
		Log.Debug("Controller/RunAbstractTestCase: Number of Actions are : "
				+ actions.length + " for testcase : " + test.testCaseID);

		for (int j = 0; j < actions.length; j++) {
			Action action = (Action) actions[j];
			Action tempAction = new Action();
			tempAction.actionName = action.actionName;
			tempAction.isComment = action.isComment;
			tempAction.testCaseID = tempTestCase.testCaseID;
			tempAction.parentTestCaseID = parentTestCaseID;
			Log.Debug("Controller/RunAbstractTestCase: tempAction.parentTestCaseID =  : "
					+ tempAction.parentTestCaseID);

			tempAction.stackTrace = tempTestCase.stackTrace;
			tempAction.nameSpace = action.nameSpace;

			tempAction.userObj = action.userObj;
			tempAction.step = action.step;
			tempAction.lineNumber = action.lineNumber;
			tempAction.sheetName = action.sheetName;
			tempAction.actionActualArguments = action.actionActualArguments;
			tempAction.isNegative = action.isNegative;
			tempAction.isActionNegative = action.isActionNegative;

			Log.Debug("Controller/RunAbstractTestCase: Working on Action "
					+ action.actionName + " with Step Number as  "
					+ action.step);
			Log.Debug("Controller/RunAbstractTestCase: Number of Action Arguments are : "
					+ action.actionArguments.size()
					+ " for action : "
					+ action.actionName);

			ArrayList<String> tempActionArguments = new ArrayList<String>();
			for (int i = 0; i < action.actionArguments.size(); ++i) {
				Log.Debug("Controller/RunAbstractTestCase: Working on Action Argument : "
						+ i + " for action : " + action.actionName);
				String actionVal = action.actionArguments.get(i).toString();
				// message("RUNABSTRACT: The MOlecule-Atom args lvl 0 "+actionVal);
				Log.Debug("Controller/RunAbstractTestCase: actionVal[" + i
						+ "] = " + actionVal + " for action : "
						+ action.actionName);
				if (actionVal.toLowerCase().contains("$input_arg")
						|| actionVal.toLowerCase().contains("$$input_arg")) {
					boolean isThisAContextVar = false;
					Log.Debug("Controller/RunAbstractTestCase: actionVal[" + i
							+ "] = " + actionVal
							+ " is an Input Argument for action : "
							+ action.actionName);

					if (actionVal.contains("=")) {
						Log.Debug("Controller/RunAbstractTestCase:  actionVal["
								+ i + "] = " + actionVal
								+ " contains an = sign ");
						String[] splitActionVal = Excel
								.SplitOnFirstEquals(actionVal);

						String tempActionVal = splitActionVal[1];

						try {
							if (tempActionVal.startsWith("%")
									&& tempActionVal.endsWith("%")) {
								tempActionVal = Utility.TrimStartEnd(
										tempActionVal, '%', 0);
								tempActionVal = Utility.TrimStartEnd(
										tempActionVal, '%', 1);
								isThisAContextVar = true;
							}

							if (actionVal.toLowerCase().contains("$$input_arg")) {
								tempActionVal = argumentValues.get(
										Integer.parseInt(tempActionVal
												.substring(11)) - 1).toString();
							} else {
								tempActionVal = argumentValues.get(
										Integer.parseInt(tempActionVal
												.substring(10)) - 1).toString();
							}
							Log.Debug("Controller/RunAbstractTestCase: actionVal["
									+ i
									+ "] = "
									+ tempActionVal
									+ " is an Input Argument for action : "
									+ action.actionName);

							if (tempActionVal.contains("=")) {
								tempActionVal = Excel
										.SplitOnFirstEquals(tempActionVal)[1];
							}

							Log.Debug("Controller/RunAbstractTestCase: actionVal["
									+ i
									+ "] = "
									+ tempActionVal
									+ " is an Input Argument for action : "
									+ action.actionName);
						} catch (Exception e) {
							tempActionVal = StringUtils.EMPTY;
							Log.Debug("Controller/RunAbstractTestCase: actionVal["
									+ i
									+ "] = "
									+ actionVal
									+ " is an Input Argument for action : "
									+ action.actionName);
						}

						if (!isThisAContextVar) {
							actionVal = splitActionVal[0] + "=" + tempActionVal;
						} else {
							actionVal = splitActionVal[0] + "=%"
									+ tempActionVal + "%";
						}
					} else {
						try {
							if (actionVal.startsWith("%")
									&& actionVal.endsWith("%")) {
								actionVal = Utility.TrimStartEnd(actionVal,
										'%', 0);
								actionVal = Utility.TrimStartEnd(actionVal,
										'%', 1);
								isThisAContextVar = true;
							}

							if (actionVal.toLowerCase().contains("$$input_arg")) {
								actionVal = argumentValues.get(
										Integer.parseInt(actionVal
												.substring(11)) - 1).toString();
							} else {
								actionVal = argumentValues.get(
										Integer.parseInt(actionVal
												.substring(10)) - 1).toString();
							}

							if (isThisAContextVar) {
								actionVal = "%" + actionVal + "%";
							}

							Log.Debug("Controller/RunAbstractTestCase: actionVal["
									+ i
									+ "] = "
									+ actionVal
									+ " is an Input Argument for action : "
									+ action.actionName);
						} catch (Exception e) {
							actionVal = StringUtils.EMPTY;
							Log.Debug("Controller/RunAbstractTestCase: actionVal["
									+ i
									+ "] = "
									+ actionVal
									+ " is an Input Argument for action : "
									+ action.actionName);
						}
					}
				}// Checking For any new defined molecule exists or not.
					// else if (actionVal.toLowerCase().startsWith("#") ||
					// actionVal.toLowerCase().startsWith("%#") ||
					// actionVal.toLowerCase().contains("=#") ||
					// actionVal.toLowerCase().contains("=##") ||
					// actionVal.toLowerCase().contains("=%#")) {
				else if (checkIfNamedArgument(actionVal)) {
					// check for firstoccurrance of # string

					String key = null, value = null;
					// TODO the work for INDEXD MACROOO checking fails the code.
					// Document the behavior to use named argument for = values
					boolean isThisAContextVar = false, foundFormalArg = false, isKeyEnabled = false, isThisContextVarTypeAtom = false;
					if (action.actionName
							.equalsIgnoreCase("appendtocontextvar")
							|| action.actionName
									.equalsIgnoreCase("setcontextvar")) {
						isThisContextVarTypeAtom = true;
						// message("Action Name "+action.actionName+" The boolean "+isThisAppenToContextVarAtom);
					}
					// message("The action Name "+action.actionName+" Runabstract:: Actionss--"+actionVal+" Arguments list "+argumentValues);
					// Checking for the arguments if they are consistent
					if (isParameterPassingConsistent(argumentValues, test)) {

						String token;
						if (actionVal.contains("=")) {
							key = Excel.SplitOnFirstEquals(actionVal)[0];
							value = Excel.SplitOnFirstEquals(actionVal)[1];
							token = value;
							isKeyEnabled = true;
						} else {
							token = actionVal;
							value = actionVal.toLowerCase();
						}
						//message("RUNABSTRACT::-1 Key value- " + key);
						if (value.startsWith("%") && value.endsWith("%")) {
							value = value.replaceAll("%", "");
							isThisAContextVar = true;
						}

						 //message("RUNABSTRACT::1a Key value- " + value +"\targumentss " + argumentValues);
						if (argumentValues.get(0).contains("=")) {
							value = value.replaceAll("#", "");
							for (String molecule_arg : argumentValues) {
								String temp_value_split[] = Excel
										.SplitOnFirstEquals(molecule_arg);
								//message("RUNABSTRACT::2 split value- " + temp_value_split[0] + "\tValue " + value);
								//message("RUNABSTRACT::2-d" + molecule_arg + " 1->" + temp_value_split.length);
								//
								if (StringUtils.countMatches(
										value.toLowerCase(),
										temp_value_split[0].toLowerCase()) >= 1) {
									if (temp_value_split[0]
											.equalsIgnoreCase(value)) {
										if (temp_value_split.length < 2) {
											throw new Exception(
													"Controller/RunAbstractTestCase: Formal argument value is Empty:: "
															+ temp_value_split[0]);
										}
										// message("RUNABSTRACT::3EqualMatchingDone "
										// + value + " keyen " + isKeyEnabled);
										if (isKeyEnabled) {
											if (isThisAContextVar) {

												actionVal = key + "=%"
														+ temp_value_split[1]
														+ "%";
											} else {
												actionVal = key + "="
														+ temp_value_split[1];
											}
										} else {
											if (isThisAContextVar) {
												actionVal = "%"
														+ temp_value_split[1]
														+ "%";
											} else {
												actionVal = temp_value_split[1];
											}

										}

										// message("RUNABSTRACT:: step forloop "
										// + actionVal);

										foundFormalArg = true;
										break;

									} else {
										// message("action value "+value+" tempv0 "+temp_value_split[0]+" tempv1 "+temp_value_split[1]+" token "+token);
										// message("RUNABSTRACT:: String Replacement "+replaceStringOnly(value, temp_value_split[0],temp_value_split[1]));
										if (token.startsWith("#")||token.startsWith("$$%#")) {
											token = token.replace("#", "");
										}
										actionVal = replaceStringOnly(token,
												temp_value_split[0],
												temp_value_split[1]);
										// if (isKeyEnabled) {
										// if (isThisAContextVar) {
										// actionVal = key + "=%" +
										// StringUtils.replace(value,
										// temp_value_split[0],
										// temp_value_split[1]) + "%";
										//
										// } else {
										// actionVal = key + "=" +
										// StringUtils.replace(value,
										// temp_value_split[0],
										// temp_value_split[1]);
										// }
										// } else {
										// if (isThisAContextVar) {
										// actionVal = "%" +
										// StringUtils.replace(value,
										// temp_value_split[0],
										// temp_value_split[1]) + "%";
										//
										// } else {
										// actionVal =
										// StringUtils.replace(value,
										// temp_value_split[0],
										// temp_value_split[1]);
										// }
										//
										//
										// //foundFormalArg = true;
										// //break;
										//
										// }
										 //message("RUNABSTRACT final result : "+actionVal);
									}

								}
								// else
								// {
								// throw new
								// Exception("Formal Argument key-name mismatch or Formal argument value is Empty "+temp_value_split[0]);
								// }
							}
							// if (!foundFormalArg) {
							// throw new
							// Exception("Formal Argument Not found for Action: "
							// + action.actionName + " In sheet: " +
							// action.sheetName + " where testcase: " +
							// action.testCaseID);
							// }
						} else {
							// value = value.replaceAll("#", "");
							// message("RUNABSTRACT::: The length argss: "+argumentValues.size()+" molecuel "+test._testcasemoleculeArgDefn.size());
							// message("RUNABSTRACT:: " + value +
							// " The index no " +
							// test._testcasemoleculeArgDefn);
							// int
							// indexNo=test._testcasemoleculeArgDefn.indexOf(value.trim().toLowerCase())+1;
							int indexNo = getMoleculeDefinitionIndex(
									test._testcasemoleculeArgDefn, value);
							// message("Indexno " + indexNo +" RUNABSTRACT:: " +
							// argumentValues.get(indexNo));
							if (indexNo < 0) {
								throw new Exception(
										String.format(
												"Controller/RunAbstractTestCase: %s is not present in molecule definition %s ",
												value,
												test._testcasemoleculeArgDefn));
							}
							actionVal = replaceStringOnly(token,
									test._testcasemoleculeArgDefn.get(indexNo),
									argumentValues.get(indexNo)).replace("#",
									"");
							// TODO enhance the code for replacing the code..
							// putting %% for contextvariable for also for name=
							// value pair
							// if (isThisAContextVar) {
							//
							// //actionVal = "%" + argumentValues.get(indexNo) +
							// "%";
							// actionVal="%"+replaceStringOnly(token,
							// test._testcasemoleculeArgDefn.get(indexNo),argumentValues.get(indexNo)).replace("#","")+"%";
							// } else {
							// //actionVal = argumentValues.get(indexNo);
							// actionVal=replaceStringOnly(token,
							// test._testcasemoleculeArgDefn.get(indexNo),argumentValues.get(indexNo)).replace("#","");
							// }
							if (isKeyEnabled) {
								actionVal = key + "=" + actionVal;
								// message("RUNABSTRACT:: The Action Valuee "+actionVal+"\nThe args is "+argumentValues.get(test._testcasemoleculeArgDefn.indexOf(value.toLowerCase())));
							}

							///message("ABS: The action value is "+actionVal);

						}

					} else {
						throw new Exception(
								"Argument Passing Not Consistent:Either use named argument passing or positional argument passing. ");
					}
					// message("RUNABSTRACT:: The Action Valuee "+actionVal+"\nThe index is "+test._testcasemoleculeArgDefn.indexOf(value.toLowerCase())+"\t The TestCase ArrayList "+test._testcasemoleculeArgDefn);
					// if(isThisContextVarTypeAtom)
					// {
					// actionVal=key+"="+actionVal;
					// //message("RUNABSTRACT:: The Action Valuee "+actionVal+" the key "+key);
					// }
					// else
					// /TODO Find a better solution
					// message("RUNABSTRACT:: The Action Valuee "+actionVal+" the key "+key);
				}
				//message("RUNABSTRACT: The MOlecule-Atom args lvl 2 "+actionVal);
				try {

					if (action.actionArguments.get(i).toString().toLowerCase()
							.contains("$$input_arg")) {
						actionVal = readExcel
								.FindInMacroAndEnvTableForSingleVector(
										actionVal, action.nameSpace);
					}
					// TODO have to put some check if the Value is comming $$#
					// for named argument.>>>
					Log.Debug("Controller/RunAbstractTestCase: actionVal[" + i
							+ "] = " + actionVal
							+ " is the Final Value for action : "
							+ action.actionName);

					tempAction.actionArguments.add(actionVal);
					 //message("After The Argument List for Molecule "+tempAction.actionArguments);
				} catch (Exception e) {
					// Log.Error("Controller/RunAbstractTestCase: "+e.getMessage());
					throw new Exception("Controller/RunAbstractTestCase: "
							+ e.getMessage());
				}
			}

			Verification[] verifications = new Verification[action.verification
					.size()];
			action.verification.toArray(verifications);
			Log.Debug("Controller/RunAbstractTestCase: Number of verifications are : "
					+ verifications.length
					+ " for Action : "
					+ action.actionName);

			for (int cnt = 0; cnt < verifications.length; cnt++) {
				Verification verification = new Verification();
				verification = verifications[cnt];
				Log.Debug("Controller/RunAbstractTestCase: Working on Verification {0}. "
						+ verification.verificationName);
				Verification tempVerification = new Verification();
				// tempVerification.expectedResult =
				// verification.expectedResult;
				tempVerification.verificationName = verification.verificationName;
				tempVerification.isComment = verification.isComment;
				tempVerification.testCaseID = tempTestCase.testCaseID;
				tempVerification.userObj = verification.userObj;
				tempVerification.parentTestCaseID = parentTestCaseID;
				Log.Debug("Controller/RunAbstractTestCase: tempVerification.parentTestCaseID =  : "
						+ tempVerification.parentTestCaseID);

				tempVerification.stackTrace = tempTestCase.stackTrace;
				tempVerification.nameSpace = verification.nameSpace;

				tempVerification.lineNumber = verification.lineNumber;
				tempVerification.sheetName = verification.sheetName;
				tempVerification.verificationActualArguments = verification.verificationActualArguments;
				tempVerification.isVerifyNegative = verification.isVerifyNegative;
				tempVerification.isRowNegative = verification.isRowNegative;
				// message("The Verificationn bool "+tempVerification.isNegative);
				// Log.Debug("Controller/RunAbstractTestCase: Expected Result = "+verification.verificationName+" fon Verification "+
				// verification.verificationName);

				Log.Debug("Controller/RunAbstractTestCase: Number of Verification Arguments = "
						+ verification.verificationArguments.size()
						+ " for Verification " + verification.verificationName);
				for (int i = 0; i < verification.verificationArguments.size(); ++i) {
					Log.Debug("Controller/RunAbstractTestCase: Working on Verification Argument : "
							+ i
							+ " for Verification : "
							+ verification.verificationName);
					String verificationVal = verification.verificationArguments
							.get(i).toString();
					Log.Debug("Controller/RunAbstractTestCase: verificationVal["
							+ i
							+ "] = "
							+ verificationVal
							+ " for Verification : "
							+ verification.verificationName);

					if (verificationVal.toLowerCase().contains("$input_arg")
							|| verificationVal.toLowerCase().contains(
									"$$input_arg")) {
						boolean isThisAContextVar = false;
						Log.Debug("Controller/RunAbstractTestCase: verificationVal["
								+ i
								+ "] = "
								+ verificationVal
								+ " is an Input Argument for Verification : "
								+ verification.verificationName);

						if (verificationVal.contains("=")) {
							Log.Debug("Controller/RunAbstractTestCase:  verificationVal["
									+ i
									+ "] = "
									+ verificationVal
									+ " contains an = sign ");
							String[] splitVerificationVal = Excel
									.SplitOnFirstEquals(verificationVal);

							String tempVerificationVal = splitVerificationVal[1];

							try {
								if (tempVerificationVal.startsWith("%")
										&& tempVerificationVal.endsWith("%")) {
									tempVerificationVal = Utility.TrimStartEnd(
											tempVerificationVal, '%', 0);
									tempVerificationVal = Utility.TrimStartEnd(
											tempVerificationVal, '%', 1);
									isThisAContextVar = true;
								}

								if (verificationVal.toLowerCase().contains(
										"$$input_arg")) {
									tempVerificationVal = argumentValues
											.get(Integer
													.parseInt(tempVerificationVal
															.substring(11)) - 1)
											.toString();
								} else {
									tempVerificationVal = argumentValues
											.get(Integer
													.parseInt(tempVerificationVal
															.substring(10)) - 1)
											.toString();
								}

								Log.Debug("Controller/RunAbstractTestCase: verificationVal["
										+ i
										+ "] = "
										+ tempVerificationVal
										+ " is an Input Argument for Verification : "
										+ verification.verificationName);

								if (tempVerificationVal.contains("=")) {
									tempVerificationVal = Excel
											.SplitOnFirstEquals(tempVerificationVal)[1];
								}

								Log.Debug("Controller/RunAbstractTestCase: verificationVal["
										+ i
										+ "] = "
										+ tempVerificationVal
										+ " is an Input Argument for Verification : "
										+ verification.verificationName);
							} catch (Exception e) {
								tempVerificationVal = StringUtils.EMPTY;
								Log.Debug("Controller/RunAbstractTestCase: verificationVal["
										+ i
										+ "] = "
										+ tempVerificationVal
										+ " is an Input Argument for Verification : "
										+ verification.verificationName);
							}

							if (!isThisAContextVar) {
								verificationVal = splitVerificationVal[0] + "="
										+ tempVerificationVal;
							} else {
								verificationVal = splitVerificationVal[0]
										+ "=%" + tempVerificationVal + "%";
							}

						} else {
							try {
								if (verificationVal.startsWith("%")
										&& verificationVal.endsWith("%")) {
									verificationVal = Utility.TrimStartEnd(
											verificationVal, '%', 0);
									verificationVal = Utility.TrimStartEnd(
											verificationVal, '%', 1);
									isThisAContextVar = true;
								}

								if (verificationVal.toLowerCase().contains(
										"$$input_arg")) {
									verificationVal = argumentValues.get(
											Integer.parseInt(verificationVal
													.substring(11)) - 1)
											.toString();
								} else {
									verificationVal = argumentValues.get(
											Integer.parseInt(verificationVal
													.substring(10)) - 1)
											.toString();
								}

								if (isThisAContextVar) {
									verificationVal = "%" + verificationVal
											+ "%";
								}
								Log.Debug("Controller/RunAbstractTestCase: verificationVal["
										+ i
										+ "] = "
										+ verificationVal
										+ " is an Input Argument for Verification : "
										+ verification.verificationName);

							} catch (Exception e) {
								verificationVal = StringUtils.EMPTY;
								Log.Debug("Controller/RunAbstractTestCase: verificationVal["
										+ i
										+ "] = "
										+ verificationVal
										+ " is an Input Argument for Verification : "
										+ verification.verificationName);

							}
						}
					} else if (verificationVal.toLowerCase().startsWith("#")
							|| verificationVal.toLowerCase().startsWith("%#")
							|| verificationVal.toLowerCase().contains("=#")
							|| verificationVal.toLowerCase().contains("=%#")) {
						String key = null, value = null;

						boolean isThisAContextVar = false, foundFormalArg = false, isKeyEnabled = false;
						// message("Runabstract:: Actionss--"+actionVal);
						// Checking for the arguments if they are consistent
						if (isParameterPassingConsistent(argumentValues, test)) {

							if (verificationVal.contains("=")) {
								key = Excel.SplitOnFirstEquals(verificationVal)[0];
								value = Excel
										.SplitOnFirstEquals(verificationVal)[1];
								isKeyEnabled = true;
							} else {
								value = verificationVal.toLowerCase();
							}
							// message("RUNABSTRACT::-1 Key value- "+key);
							if (value.startsWith("%") && value.endsWith("%")) {
								value = value.replaceAll("%", "");
								isThisAContextVar = true;
							}
							value = value.replaceAll("#", "");
							// message("RUNABSTRACT::1a Key value- "+value+"\targumentss "+argumentValues);
							if (argumentValues.get(0).contains("=")) {
								for (String molecule_arg : argumentValues) {
									String temp_value_split[] = Excel
											.SplitOnFirstEquals(molecule_arg);
									// message("RUNABSTRACT::2 Key value- "+value+"\targumentss "+argumentValues);
									if (temp_value_split[0]
											.equalsIgnoreCase(value)) {
										if (temp_value_split.length < 2) {
											throw new Exception(
													"Controller/RunAbstractTestCase: Formal argument value is Empty:: "
															+ temp_value_split[0]);
										}
										if (isKeyEnabled) {
											if (isThisAContextVar) {
												verificationVal = key + "=%"
														+ temp_value_split[1]
														+ "%";
											} else {
												verificationVal = key + "="
														+ temp_value_split[1];
											}
										} else {
											if (isThisAContextVar) {
												verificationVal = "%"
														+ temp_value_split[1]
														+ "%";
											} else {
												verificationVal = temp_value_split[1];
											}

										}

										// message("RUNABSTRACT:: step forloop "+actionVal);
										foundFormalArg = true;
										break;

									}
								}
								if (!foundFormalArg) {
									throw new Exception(
											"Formal Argument Not found");
								}
							} else {
								if (test._testcasemoleculeArgDefn.indexOf(value
										.trim().toLowerCase()) < 0) {
									throw new Exception(
											String.format(
													"Controller/RunAbstractTestCase: %s is not present in molecule definition %s ",
													value,
													test._testcasemoleculeArgDefn));
								}
								if (isThisAContextVar) {
									verificationVal = "%"
											+ argumentValues
													.get(test._testcasemoleculeArgDefn.indexOf(value
															.toLowerCase()))
											+ "%";
								} else {
									verificationVal = argumentValues
											.get(test._testcasemoleculeArgDefn
													.indexOf(value
															.toLowerCase()));
								}
								if (isKeyEnabled) {
									verificationVal = key + "="
											+ verificationVal;
								}
							}

						} else {
							throw new Exception(
									"Argument Passing Not Consistent:Either use named argument passing or positional argument passing");
						}
						// message("RUNABSTRACT:: The Action Valuee "+actionVal+"\nThe index is "+test._testcasemoleculeArgDefn.indexOf(value)+"\t The TestCase ArrayList "+test._testcasemoleculeArgDefn);
					}

					try {
						if (verification.verificationArguments.get(i)
								.toString().toLowerCase()
								.contains("$$input_arg")) {
							verificationVal = readExcel
									.FindInMacroAndEnvTableForSingleVector(
											verificationVal,
											verification.nameSpace);
						}
						// TODO have to put some check if the Value is comming
						// $$# for named argument.>>>
						Log.Debug("Controller/RunAbstractTestCase: verificationVal["
								+ i
								+ "] = "
								+ verificationVal
								+ " is he Final Value for Verification : "
								+ verification.verificationName);

						tempVerification.verificationArguments
								.add(verificationVal);
					} catch (Exception e) {
						// Log.Error("Controller/RunAbstractTestCase: "+e.getMessage());
						throw new Exception("Controller/RunAbstractTestCase: "
								+ e.getMessage());
					}
				}
				tempAction.verification.add(tempVerification);
			}
			tempTestCase.actions.add(tempAction);
		}

		try {
			message("\n******************** Running Molecule "
					+ tempTestCase.stackTrace
					+ " ***************************\n");
			// Now everything is ready. Call the RunTestCase Function.
			RunTestCaseForMolecule(tempTestCase);
		} catch (Exception ex) {
			Log.Error("Exception while running Abstract Test Case "
					+ tempTestCase.stackTrace + " is : " + ex.getMessage());
			// throw new Exception("Exception while running Abstract Test Case "
			// + tempTestCase.stackTrace + " is : " + ex.getMessage());
			// message("Thhe boolean value "+isMoleculeNegative);
			// if(isMoleculeNegative)
			// {
			// ContextVar.setContextVar("ZUG_EXCEPTION", ex.getMessage());
			// Log.Error("Note: Executing Negative Molecule ");
			//
			// }
			// else
			// {
			throw new Exception(ex.getMessage());
			// }
		} finally {
			message("\n******************** Molecule "
					+ tempTestCase.stackTrace
					+ " Execution Finished **************************\n\n");
		}
		Log.Debug("Controller/RunAbstractTestCase: End of function with a new TestCase. TestCase ID is "
				+ test.testCaseID);
	}

	// /***
	// * Function to check if value is MVM
	// *
	// * @param value
	// * String to Check for MVM.
	// * testcase
	// * TestCase to get the Actions and their arguments
	// * @return boolean true if MVM false for not
	// */
	// @SuppressWarnings("unchecked")
	// private boolean checkIfMVM(TestCase testcase, String value) throws
	// Exception {
	// message("Controller/checkIfMVM: Method called using parameters " +
	// testcase.testCaseID + "  " + value + "\n Size of Action " +
	// testcase.actions.size());
	// Action[] allActionInTestCase = new Action[testcase.actions.size()];
	// testcase.actions.toArray(allActionInTestCase);
	// boolean checkMVM = false, checkInVrifiMVM = false;
	//
	// for (int count = 0; count < allActionInTestCase.length; count++) {
	// Action single_action = allActionInTestCase[count];
	// message("Action name is : " + single_action.actionName);
	// if (single_action.actionActualArguments.size() ==
	// single_action.actionArguments.size()) {
	// for (int i = 0; i < single_action.actionActualArguments.size(); i++) {
	// String purevalues =
	// GetTheActualValue(single_action.actionArguments.get(i));
	// if (value.trim().equals(purevalues.trim())) {
	// message("The values are similar " + purevalues);
	// message("The value is MVM without check " +
	// single_action.actionActualArguments.get(i));
	// if (single_action.actionActualArguments.get(i).startsWith("$$")) {
	// checkMVM = true;
	// message("The value is MVM " +
	// single_action.actionActualArguments.get(i));
	// break;
	// }
	// //TODO check = for named arguments
	// if (single_action.actionActualArguments.get(i).contains("=")) {
	// String[] split_actual_arg =
	// Excel.SplitOnFirstEquals(single_action.actionActualArguments.get(i));
	// if (split_actual_arg[1].startsWith("$$")) {
	// checkMVM = true;
	// //message("The value is MVM = " +
	// single_action.actionActualArguments.get(i));
	// break;
	// }
	// }
	// // else if (single_action.actionActualArguments.get(i).startsWith("$")) {
	// // checkMVM = false;
	// // message("The value not MVM " +
	// single_action.actionActualArguments.get(i));
	// // }
	//
	// } else {
	// continue;
	// }
	// }
	//
	// }
	//
	// Verification[] allVerificationInTestCase = new
	// Verification[single_action.verification.size()];
	// single_action.verification.toArray(allVerificationInTestCase);
	// for (int cont = 0; cont < allVerificationInTestCase.length; ++cont) {
	// Verification single_verification = allVerificationInTestCase[cont];
	// if (single_verification.verificationActualArguments.size() ==
	// single_verification.verificationArguments.size()) {
	// for (int i = 0; i <
	// single_verification.verificationActualArguments.size(); ++i) {
	// String purevalues =
	// GetTheActualValue(single_verification.verificationArguments.get(i));
	// if (value.trim().equals(purevalues.trim())) {
	// // message("The values are verify similar " + purevalues);
	// if
	// (single_verification.verificationActualArguments.get(i).startsWith("$$"))
	// {
	// checkInVrifiMVM = true;
	// // message("The value is verify MVM " +
	// single_verification.verificationActualArguments.get(i));
	// break;
	// }
	// //TODO check = for named arguments
	// if (single_verification.verificationActualArguments.get(i).contains("="))
	// {
	// String[] split_actual_arg =
	// Excel.SplitOnFirstEquals(single_verification.verificationActualArguments.get(i));
	// if (split_actual_arg[1].startsWith("$$")) {
	// checkInVrifiMVM = true;
	// // message("The value is verify MVM = " +
	// single_verification.verificationActualArguments.get(i));
	// break;
	// }
	// }
	// // else if (single_action.actionActualArguments.get(i).startsWith("$")) {
	// // checkMVM = false;
	// // message("The value not MVM " +
	// single_action.actionActualArguments.get(i));
	// // }
	//
	// } else {
	// continue;
	// }
	// }
	// }
	// }
	// }
	// return checkMVM || checkInVrifiMVM;
	//
	// }
	/***
	 * Function to check if value is MVM
	 * 
	 * @param value
	 *            String to Check for MVM. testcase TestCase to get the Actions
	 *            and their arguments
	 * @return boolean true if MVM false for not
	 */
	@Deprecated
	private boolean checkIfMVM(HashMap<String, String> mvm_map, String value)
			throws Exception {
		boolean checkMVM = false;
		if (mvm_map.containsKey(value)) {
			message("new CheckMVM the map value " + mvm_map.get(value));
			checkMVM = true;
		}

		return checkMVM;
	}
	/**
	 * Method to remove the duplicate mvm variables
	 * @param Action action
	 * refines the Action object to remove the  
	 * @return Action 
	 */
private Action removeDuplicateMVMVariables(Action action)
{
	Action refinedAction=new Action();
	refinedAction.actionName=action.actionName;
	refinedAction.actionProperty=action.actionProperty;
	refinedAction.actionDescription=action.actionDescription;
	//TODO the checking for duplicacy using Hasmap and Set
	message("the elements "+action.actionActualArguments);
	int count=0;
	for(String tmparg:action.actionActualArguments)
	{
		if(tmparg.startsWith("$$"))
		{
			message(tmparg+" = "+new Integer(count).toString() );
			refinedAction._mvmmap.put(tmparg+" "+count, new Integer(count).toString());
		}
		count++;
	}
	message("The refined map is "+refinedAction._mvmmap);
	return refinedAction;
}
/**
 * method for adding extra action in testcase
 * @throws Exception 
 */
private TestCase addSetContextVarMVMAction(TestCase tes) throws Exception
{
	
	ArrayList<Action> tempActions=new ArrayList<Action>();
	Excel er= new Excel();
	for(Action act:tes.actions)
	{
		
		for(int i=0;i<act.actionActualArguments.size();i++)
		{
			String arg=act.actionActualArguments.get(i);
			String value=act.actionArguments.get(i);
			if(arg.startsWith("$$"))
			{
				Action tempaction=new Action();
				tempaction.nameSpace=act.nameSpace;
				tempaction.testCaseID=act.testCaseID;
				tempaction.stackTrace=act.testCaseID;
				tempaction.parentTestCaseID=act.parentTestCaseID;
				tempaction.actionName="SetContextVar";
				//String ctxvarname=tempaction.testCaseID+"_"+arg;
				tempaction.actionActualArguments.add(tempaction.testCaseID+"_"+arg+"="+value);
				tempaction.actionActualArguments.add(er.FindInMacroAndEnvTable(arg.replace("$","" ), act.nameSpace));
				tempActions.add(tempaction);
//				act.actionArguments.remove(i);
//				act.actionArguments.add(i, "%"+ctxvarname+"%");
				}
			
		}
				
		
	}
	System.out.println("TempAction generated "+tempActions.size());
	tes.actions.addAll(tempActions);
	return tes;
	
}
	/***
	 * Function to Expand the TestCases
	 * 
	 * @param test
	 *            TestCase to Expand.
	 * @return All the Expanded TestCases
	 */
	@SuppressWarnings("unchecked")
	private TestCase[] ExpandTestCase(TestCase test, boolean fromTestCaseSheet)
			throws Exception {
		Log.Debug("Controller/ExpandTestCase: Start of function with TestCase ID is "
				+ test.testCaseID);

		// HashMap<String, String> mvm_vector_map = new HashMap<String,
		// String>();
		// message("THE testcase coming1a " + test.testCaseID);
//write the subroutine here.
		//test=addSetContextVarMVMAction(test);
		ArrayList<ArrayList<String>> allActionVerificationArgs = new ArrayList<ArrayList<String>>();
		Action[] allActions = new Action[test.actions.size()];
		// message("THE testcase coming 1b" + test.testCaseID);
		test.actions.toArray(allActions);
		Log.Debug("Controller/ExpandTestCase: Number of Actions are : "
				+ allActions.length + " for testcase : " + test.testCaseID);
		// message("EXPANDTESTCASE THE testcase coming 1c " + test.testCaseID);
		int count1 = -1;
		Hashtable<Integer, String> multiValuedVariablePosition = new Hashtable<Integer, String>();

		for (int j = 0; j < allActions.length; j++) {
			// message("THE testcase coming 1d " + test.testCaseID);
			Action action = allActions[j];
			Log.Debug("Controller/ExpandTestCase: Working on Action  : "
					+ action.actionName);
			// //TODO put checking if testcase have no actio argument then at
			// least print any message or put the exception
			//message("Action argument size? "+action.actionArguments.size()+" Argument Valuess "+action.actionArguments);
			
			if (action.actionArguments.size() > 0) {
				//removeDuplicateMVMVariables(action);
				for (int i = 0; i < action.actionArguments.size(); ++i) {

					// /In case of Molecule the actual value is not coming ?
					// why?
					// message("THE testcase coming 1e " + test.testCaseID +
					// " Every Values " + action.actionArguments.get(i) +
					// " Action Actual Arguments " +
					// action.actionActualArguments);
					count1++;
					// message("ExpandTest:/cheks to Macro action args exp\t" +
					// action.actionArguments);
					String tempVal = GetTheActualValue((String) (action.actionArguments
							.get(i)));
					// message("EXPANDEDTESTCASE:: The Temp Val "+tempVal);
					// message("Expandtest:/lengths " +
					// action.actionArguments.size()+"\n\t"+action.actionActualArguments.size());
					if (action.actionActualArguments.size() == action.actionArguments
							.size()) {
						// put in a hashmap
						// key=tempval value=actlArg
						if (action.actionActualArguments.get(i)
								.startsWith("$$")
								|| action.actionActualArguments.get(i)
										.startsWith("##")) {

							tempVal = tempVal + "$~$";
						} else if (action.actionActualArguments.get(i)
								.contains("=")) {
							String[] split_actual_arg = Excel
									.SplitOnFirstEquals(action.actionActualArguments
											.get(i));
							// if (split_actual_arg[1].startsWith("$$") ||
							// split_actual_arg[1].startsWith("##")) {
							if (split_actual_arg.length > 1
									&& (split_actual_arg[1].startsWith("$$") || split_actual_arg[1]
											.startsWith("##"))) {
								tempVal = tempVal + "$~$";
							}
						}

					}
					// mvm_vector_map.put(
					// tempVal,action.actionActualArguments.get(i).trim());
					// message("THE vector map only action " + tempVal);
					// message("EXPANDED TESTCASE:: cheks to Macro exp\t" +
					// tempVal);

					if (tempVal == null) {
						Log.Error("Controller/ExpandedTestCase : Variable -> "
								+ action.actionArguments.get(i) + " Value -> "
								+ tempVal
								+ " NullValueException \n In TesrCaseId: "
								+ action.testCaseID);
						throw new Exception(
								"Controller/ExpandedTestCase : Variable -> "
										+ action.actionArguments.get(i)
										+ " Value -> "
										+ tempVal
										+ " NullValueException \n In TesrCaseId: "
										+ action.testCaseID);
					}
					String tempindexcheckin[]=Excel.SplitOnFirstEquals(tempVal);
					if(tempindexcheckin.length==2)
					{
						if(tempindexcheckin[1].startsWith("$~$")&&tempindexcheckin[1].endsWith("$~$"))
						{
							//message("indexmacro checking: from "+tempVal+" to "+tempindexcheckin[1]);
							tempVal=tempindexcheckin[1];
						}
					}
					if ((tempVal.startsWith("$~$"))
							&& (tempVal.endsWith("$~$"))) {
						tempVal = StringUtils.replace(tempVal, "$~$", "");
						// tempVal=tempVal.replaceAll("~","");
						String val = Utility.TrimStartEnd(tempVal, '#', 1);
						val = Utility.TrimStartEnd(val, '#', 0);
						val = Utility.TrimStartEnd(val, '#', 0);
						val = Utility.TrimStartEnd(val, '#', 1);
						Log.Debug("Controller/ExpandTestCase: Working on Argument ["
								+ i
								+ "] = "
								+ val
								+ " of Action: "
								+ action.actionName);
						allActionVerificationArgs.add(new ArrayList<String>(
								Arrays.asList(val.split(","))));

						// Test Case Variable table not getting updated for one
						// valued MVM - Bug Fix
						// if (val.Split(',').Length > 1)
						multiValuedVariablePosition.put(count1,
								StringUtils.EMPTY);
					} else {

						allActionVerificationArgs.add(new ArrayList<String>(
								Arrays.asList(new String[] { tempVal })));
					}
					// Checking for MVM calls
					// if (action.actionActualArguments.get(i).startsWith("$$"))
					// {
					// isMVM = true;
					// message("action value ?" +
					// action.actionArguments.get(i));
					// } else {
					// continue;
					// }
				}
			} else {
				// message("No Arguments : Molecule called then "+action.actionName);
				allActionVerificationArgs.add(new ArrayList<String>(Arrays
						.asList(new String[] { "Somevalue" })));

			}
			// message("Expands :: action arguments "+action.actionArguments);
			Verification[] verifications = new Verification[action.verification
					.size()];
			action.verification.toArray(verifications);
			Log.Debug("Controller/ExpandTestCase: Number of verifications are : "
					+ verifications.length
					+ " for Action : "
					+ action.actionName);
			for (int k = 0; k < verifications.length; k++) {
				Verification verification = verifications[k];

				if (verification.verificationName == null) {
					continue;
				}
				Log.Debug("Controller/ExpandTestCase: Working on verification : "
						+ verification.verificationName
						+ " for Action : "
						+ action.actionName);
				if (verification.verificationArguments.size() > 0) {
					for (int l = 0; l < verification.verificationArguments
							.size(); ++l) {
						count1++;

						String tempVal2 = GetTheActualValue((String) (verification.verificationArguments
								.get(l)));
						if (verification.verificationActualArguments.size() == verification.verificationArguments
								.size()) {
							// put in a hashmap
							// key=tempval value=actlArg
							if (verification.verificationActualArguments.get(l)
									.startsWith("$$")
									|| verification.verificationActualArguments
											.get(l).startsWith("##")) {
								tempVal2 = tempVal2 + "$~$";
							} else if (verification.verificationActualArguments
									.get(l).contains("=")) {
								String[] split_actual_arg = Excel
										.SplitOnFirstEquals(verification.verificationActualArguments
												.get(l));
								if (split_actual_arg.length > 1
										&& (split_actual_arg[1]
												.startsWith("$$") || split_actual_arg[1]
												.startsWith("##"))) {
									tempVal2 = tempVal2 + "$~$";
								}
							}

						}
						if (tempVal2 == null) {
							Log.Error("Controller/ExpandedTestCase : Variable -> "
									+ verification.verificationArguments.get(l)
									+ " Value -> "
									+ tempVal2
									+ " NullValueException \n In TesrCaseId: "
									+ verification.testCaseID);
							throw new Exception(
									"Controller/ExpandedTestCase : Variable -> "
											+ verification.verificationArguments
													.get(l)
											+ " Value -> "
											+ tempVal2
											+ " NullValueException \n In TesrCaseId: "
											+ verification.testCaseID);
						}
						if (tempVal2.startsWith("$~$#")
								&& tempVal2.endsWith("#$~$")) {
							tempVal2 = StringUtils.replace(tempVal2, "$~$", "");
							String val = Utility.TrimStartEnd(tempVal2, '#', 1);
							val = Utility.TrimStartEnd(val, '#', 0);
							val = Utility.TrimStartEnd(val, '#', 1);
							val = Utility.TrimStartEnd(val, '#', 0);
							Log.Debug("Controller/ExpandTestCase: Working on Argument ["
									+ j
									+ "] = "
									+ val
									+ " of verification  :"
									+ verification.verificationName);
							allActionVerificationArgs
									.add(new ArrayList<String>(Arrays
											.asList(val.split(","))));

							// Test Case Variable table not getting updated for
							// one
							// valued MVM - Bug Fix
							// if (val.Split(',').Length > 1)
							multiValuedVariablePosition.put(count1, "");
						} else {
							allActionVerificationArgs
									.add(new ArrayList<String>(Arrays
											.asList(new String[] { tempVal2 })));
						}
					}
				} else {
					allActionVerificationArgs.add(new ArrayList<String>(Arrays
							.asList(new String[] { "somevalue" })));
				}
			}

		}
		//message("EXZPANDSS:: the arguments are "+allActionVerificationArgs+" multivalued macro position "+multiValuedVariablePosition);

		List<Tuple<String>> resultAfterIndexed = CartesianProduct
				.indexedProduct(allActionVerificationArgs);

		// .get(0),
		// allActionVerificationArgs.get(1));
		List<Tuple<String>> result = new ArrayList<Tuple<String>>();

		for (Tuple<String> tempResult : resultAfterIndexed) {
			allActionVerificationArgs = new ArrayList<ArrayList<String>>();
			ArrayList<String> tempResultList = new ArrayList<String>();
			Object[] actualValue = tempResult.ToArray();

			for (int q = 0; q < actualValue.length; ++q) {
				tempResultList.add((String) actualValue[q]);
			}
//message("temp result list "+tempResultList);
			count1 = -1;
			try {
				for (String tempVal : tempResultList) {
					// We can comeup with a data structure which rembers the
					// value with the macro key. if no macro key containing "$$"
					// Then no expansion
					// new function for checking the $$ or $ to expand the
					// testcase
					count1++;

					// message("Comming to Cartesian Product ");
					// //if ((tempVal.startsWith("{")) &&
					// (tempVal.endsWith("}"))) {
					// boolean checkMVM=checkIfMVM(test, tempVal);
					// boolean checkMVM=checkIfMVM(mvm_vector_map, tempVal);
					// if (checkMVM) {
					if (tempVal.endsWith("$~$")) {
						tempVal = StringUtils.replace(tempVal, "$~$", "");
						String val = StringUtils.replace(tempVal, "{", "");
						val = StringUtils.replace(val, "}", "");

						Log.Debug("Controller/ExpandTestCase: Working on Argument with value = "
								+ val + " of Action: ");
						allActionVerificationArgs.add(new ArrayList<String>(
								Arrays.asList(val.split(","))));
						// this.message("MVM part\t"+allActionVerificationArgs);
						// Test Case Variable table not getting updated for one
						// valued MVM - Bug Fix
						// if (val.Split(',').Length > 1)
						multiValuedVariablePosition.put(count1,
								StringUtils.EMPTY);
					} else {
						if (!tempVal.equalsIgnoreCase("somevalue")) {
							allActionVerificationArgs
									.add(new ArrayList<String>(Arrays
											.asList(new String[] { tempVal })));
						}
					}

					// }
				}

				result.addAll(CartesianProduct
						.cartesianProduct(allActionVerificationArgs));

			} catch (OutOfMemoryError e) {
				testcasenotran = test.testCaseID + "\nError: " + e.getMessage()
						+ "(memory) exceeded";
				Log.Error("["
						+ test.testCaseID
						+ "] Combination Of MVM values Exceeded permisible limit :: Not Comapatibile \nError: "
						+ e.getMessage() + "(memory) exceeded");
				throw new Exception(
						"Combination Of MVM values Exceeded permisible limit :: Not Comapatibile \nError: ");
			}
		}

		ArrayList<TestCase> tempTestCases = new ArrayList<TestCase>();
		// message("Coming to this end. 7 ");
		if (result != null) {
			int size = result.size();
			Object[] tempResult = result.toArray();
			// Object [] tempResult = allActionVerificationArgs.toArray();
			for (int p = 0; p < size; ++p) {
				Tuple<String> subList = (Tuple<String>) tempResult[p];
				// ArrayList<BusinessLayer.Variable> variables = new
				// ArrayList<BusinessLayer.Variable>();
				// message("Coming to this end. 8 ");
				TestCase tempTestCase = new TestCase();

				tempTestCase.testCaseID = test.testCaseID;
				Log.Debug("Controller/ExpandTestCase: tempTestCase.testCaseID = "
						+ tempTestCase.testCaseID);

				tempTestCase.automated = test.automated;
				Log.Debug("Controller/ExpandTestCase: tempTestCase.automated = "
						+ tempTestCase.automated);

				tempTestCase.nameSpace = test.nameSpace;
				Log.Debug("Controller/ExpandTestCase: tempTestCase.nameSpace = "
						+ tempTestCase.nameSpace);

				tempTestCase.concurrentExecutionOnExpansion = test.concurrentExecutionOnExpansion;
				Log.Debug("Controller/ExpandTestCase: tempTestCase.concurrentExecutionOnExpansion = "
						+ tempTestCase.concurrentExecutionOnExpansion);

				tempTestCase.testCaseDescription = test.testCaseDescription;
				Log.Debug("Controller/ExpandTestCase: tempTestCase.testCaseDescription = "
						+ tempTestCase.testCaseDescription);

				tempTestCase.user = test.user;
				tempTestCase.userObj = test.userObj;

				ArrayList<String> tempCollection = new ArrayList<String>();
				Object[] actualValue = subList.ToArray();
				for (int q = 0; q < actualValue.length; ++q) {
					tempCollection.add((String) actualValue[q]);
				}

				/*
				 * while(subList.hasMoreElements()) { String
				 * val=subList.nextElement(); tempCollection.add(val); }
				 */

				String[] tempTestCaseVar = new String[tempCollection.size()];
				tempCollection.toArray(tempTestCaseVar);
				int count = 0;

				Action[] actions = new Action[test.actions.size()];
				test.actions.toArray(actions);
				Log.Debug("Controller/ExpandTestCase: Number of Actions are : "
						+ actions.length + " for testcase : "
						+ tempTestCase.testCaseID);

				for (int j = 0; j < actions.length; ++j) {
					Action action = actions[j];
					for (int i = 0; i < action.actionArguments.size(); ++i) {

						if (multiValuedVariablePosition.containsKey(count)) {
							String testcase_partial_id = tempTestCaseVar[count]
									.toLowerCase();
							
														
							 //message("Index "+testcase_partial_id+" "+action.actionActualArguments.get(i)+" "+action.actionArguments.get(i));

							if (action.actionName
									.equalsIgnoreCase("appendtocontextvar")
									|| action.actionName
											.equalsIgnoreCase("setcontextvar")) {
								// message("Coming to this end. 10 "+action.actionName);
								String real_value[] = Excel
										.SplitOnFirstEquals(testcase_partial_id);
								// message("Coming to this end. 10 a "+testcase_partial_id);
								String actual_value[] = Excel
										.SplitOnFirstEquals(action.actionActualArguments
												.get(i));
								// message("Coming to this end. 10 b "+actual_value[0]+" and real "+real_value[0]);
								if (real_value.length > 0) {
									if (real_value[0]
											.equalsIgnoreCase(actual_value[0])) {
										// message("Before Change "+tempTestCaseVar[count]+" ID "+testcase_partial_id);
										testcase_partial_id = StringUtils
												.replace(testcase_partial_id,
														actual_value[0] + "=",
														"");
										if(testcase_partial_id.contains("="))
										{
											testcase_partial_id=Excel.SplitOnFirstEquals(testcase_partial_id)[1];
										}
										// message("After change "+tempTestCaseVar[count]+" ID "+testcase_partial_id);

										// message("Coming to this end. 10 c "+testcase_partial_id);
									} else if (action.actionActualArguments
											.get(i).contains("=")) {
										String realVal[] = Excel
												.SplitOnFirstEquals(testcase_partial_id);
										if (realVal.length > 1) {
											testcase_partial_id = realVal[1];

										}
									}
								} else {

									testcase_partial_id = null;
								}

							}
							tempTestCase.testCaseID += "_"
									+ testcase_partial_id;
							// message("TempCase id "+tempTestCase.testCaseID);
							// message("EXPPANNDS vars "+tempTestCaseVar[count]);
							//
							// if (fromTestCaseSheet == true) {
							// BusinessLayer.Variable var = new
							// BusinessLayer.Variable();
							// var.set_name(ReturnValue(action.actionActualArguments.get(i).toString()));
							// var.set_value(ReturnValue(tempTestCaseVar[count]));
							//
							// variables.add(var);
							// }
						}
						// message("Coming to this end. 11 "+count);
						count++;
					}

					Verification[] verifications = new Verification[action.verification
							.size()];
					action.verification.toArray(verifications);
					Log.Debug("Controller/ExpandTestCase: Number of verifications are : "
							+ verifications.length
							+ " for Action : "
							+ action.actionName);
					for (int cnt = 0; cnt < verifications.length; ++cnt) {
						Verification verification = verifications[cnt];

						Log.Debug("Controller/ExpandTestCase: Working on Verification  "
								+ verification.verificationName);

						for (int i = 0; i < verification.verificationArguments
								.size(); ++i) {
							if (multiValuedVariablePosition.containsKey(count)) {
								String testcase_partial_id = tempTestCaseVar[count]
										.toLowerCase();
								if (action.actionName
										.equalsIgnoreCase("appendtocontextvar")) {
									String real_value[] = Excel
											.SplitOnFirstEquals(testcase_partial_id);
									String actual_value[] = Excel
											.SplitOnFirstEquals(action.actionActualArguments
													.get(i));
									if (real_value[0]
											.equalsIgnoreCase(actual_value[0])) {
										// message("Before Change "+tempTestCaseVar[count]+"  "+testcase_partial_id);
										testcase_partial_id = StringUtils
												.replace(testcase_partial_id,
														actual_value[0] + "=",
														"");
										// message("After change "+tempTestCaseVar[count]+"  "+testcase_partial_id);
									}

								} else if (verification.verificationActualArguments
										.get(i).contains("=")) {
									String real_value[] = Excel
											.SplitOnFirstEquals(testcase_partial_id);
									if (real_value.length > 1) {
										testcase_partial_id = real_value[1];
									}
								}
								tempTestCase.testCaseID += "_"
										+ testcase_partial_id;
								// if (fromTestCaseSheet == true) {
								//
								// BusinessLayer.Variable var = new
								// BusinessLayer.Variable();
								// var.set_name(ReturnValue(verification.verificationActualArguments.get(i).toString()));
								// var.set_value(ReturnValue(tempTestCaseVar[count]));
								//
								// variables.add(var);
								// }
							}

							count++;
						}
					}
				}
				// message("EXPANDTESTCASE THE testcase coming 1f " +
				// tempTestCase.testCaseID);

				tempTestCase.stackTrace = test.stackTrace;
				tempTestCase.threadID = test.threadID;

				if (fromTestCaseSheet) {
					// If this is from the Test Case Sheet, then add the Test
					// Case Variables.
					// Which is nothing but the Macro and its value

					// TestCaseVariableValue.put(tempTestCase.testCaseID,
					// variables);

					tempTestCase.parentTestCaseID = tempTestCase.testCaseID;
					Log.Debug("Controller/ExpandTestCase: fromTestCaseSheet= TRUE so tempTestCase.parentTestCaseID  = "
							+ tempTestCase.parentTestCaseID);

					tempTestCase.stackTrace = tempTestCase.testCaseID;
					Log.Debug("Controller/ExpandTestCase: fromTestCaseSheet= TRUE so tempTestCase.stackTrace  = "
							+ tempTestCase.stackTrace);
				} else {
					tempTestCase.parentTestCaseID = test.parentTestCaseID;
					Log.Debug("Controller/ExpandTestCase: fromTestCaseSheet= FALSE so tempTestCase.parentTestCaseID  = "
							+ tempTestCase.parentTestCaseID);

					tempTestCase.stackTrace = tempTestCase.stackTrace.replace(
							test.testCaseID, tempTestCase.testCaseID);
					Log.Debug("Controller/ExpandTestCase: fromTestCaseSheet= TRUE so tempTestCase.stackTrace  = "
							+ tempTestCase.stackTrace);

				}

				count = 0;
				// message("EXPNDD:: arguments .. "+actions[1].actionArguments);
				for (int cnt = 0; cnt < actions.length; ++cnt) {
					Action action = actions[cnt];
					Action tempAction = new Action();
					tempAction.actionName = action.actionName;
					tempAction.isComment = action.isComment;
					tempAction.testCaseID = tempTestCase.testCaseID;
					tempAction.parentTestCaseID = tempTestCase.parentTestCaseID;
					Log.Debug("Controller/ExpandTestCase: tempAction.parentTestCaseID  = "
							+ tempAction.parentTestCaseID);

					tempAction.stackTrace = tempTestCase.stackTrace;
					tempAction.nameSpace = action.nameSpace;
					tempAction.userObj = action.userObj;
					tempAction.step = action.step;
					tempAction.lineNumber = action.lineNumber;
					tempAction.sheetName = action.sheetName;
					tempAction.actionActualArguments = action.actionActualArguments;
					tempAction.actionProperty = action.actionProperty;
					tempAction.actionDescription = action.actionDescription;
					tempAction.isNegative = action.isNegative;
					tempAction.isActionNegative = action.isActionNegative;
					// message("EXXCC: argsss " + action.actionArguments);
					for (int i = 0; i < action.actionArguments.size(); ++i) {
						tempAction.actionArguments.add(GetActualCombination(
								(String) action.actionArguments.get(i),
								tempTestCaseVar[count++]));

					}
					// message("EXPANDSS args "+tempAction.actionArguments);
					Verification[] verifications = new Verification[action.verification
							.size()];
					action.verification.toArray(verifications);
					Log.Debug("Controller/ExpandTestCase: Number of verifications are : "
							+ verifications.length
							+ " for Action : "
							+ action.actionName);

					for (int verCount = 0; verCount < verifications.length; ++verCount) {
						Verification verification = verifications[verCount];

						Log.Debug("Controller/ExpandTestCase: Working on Verification "
								+ verification.verificationName);
						Verification tempVerification = new Verification();
						tempVerification.testCaseID = tempTestCase.testCaseID;
						// tempVerification.expectedResult =
						// verification.expectedResult;
						tempVerification.verificationName = verification.verificationName;
						tempVerification.parentTestCaseID = tempTestCase.parentTestCaseID;
						Log.Debug("Controller/ExpandTestCase: tempVerification.parentTestCaseID  = "
								+ tempVerification.parentTestCaseID);

						tempVerification.nameSpace = verification.nameSpace;
						tempVerification.stackTrace = tempTestCase.stackTrace;
						tempVerification.userObj = verification.userObj;
						tempVerification.isComment = verification.isComment;
						tempVerification.lineNumber = verification.lineNumber;
						tempVerification.sheetName = verification.sheetName;
						tempVerification.isVerifyNegative = verification.isVerifyNegative;
						tempVerification.isRowNegative = verification.isRowNegative;
						for (int i = 0; i < verification.verificationArguments
								.size(); ++i) {

							tempVerification.verificationArguments
									.add(GetActualCombination(
											(String) verification.verificationArguments
													.get(i),
											tempTestCaseVar[count++]));
						}
						tempAction.verification.add(tempVerification);
					}
					tempTestCase.actions.add(tempAction);
					// message("The testcase id is "+tempTestCase.testCaseID);
				}
				// message("Expndd:: The Action arguments "+tempTestCase.actions.get(1).actionArguments);
				// message("Testcase ID "+tempTestCase.testCaseID);

				if (manualTestCaseID.contains(",")) {
					if (checkWithCommandLineInput(tempTestCase).testCaseID
							.equalsIgnoreCase("Not Present")) {
						// message("Tempo Test Case is "+tempTestCase.testCaseID);
						// continue;
					} else {
						// message("Temp case added "+tempTestCase.testCaseID);
						tempTestCases
								.add(checkWithCommandLineInput(tempTestCase));

					}

				} else {
					// message("Not Comma Separted command Line");
					if (checkWithCommandLineInput(tempTestCase).testCaseID
							.equalsIgnoreCase("Not Present")) {
						// message("Tempo Test Case is "+tempTestCase.testCaseID);
						// continue;
					} else {
						// message("Temp case added "+tempTestCase.testCaseID);
						tempTestCases
								.add(checkWithCommandLineInput(tempTestCase));
					}
				}

			}
		}

		if (tempTestCases.size() == 0) {
			Log.Debug("Controller/ExpandTestCase: Returning only 1 testcase after Expansion. End of function with TestCase ID is "
					+ test.testCaseID);
			// message("ExpandTestCase:: The Manual isss22  " +
			// test.testCaseID);
			return new TestCase[] { test };
		}
//		ArrayList<TestCase> removemulticases=new ArrayList<TestCase>();
//		//Write a method which removes the similar mvm values.
//		for(TestCase tmp:tempTestCases)
//		{
//		
//			
//			//message("temn  "+tmp.testCaseID);
//			removemulticases=removeSimilarTestCases(tmp,removemulticases);
//		}
//		tempTestCases.removeAll(removemulticases);
//for(TestCase dd:removemulticases)
//{
//	message("The ids "+dd.testCaseID);
//}
		if (excludeTestCaseID != null) {
			ArrayList<TestCase> removeTestCase = new ArrayList<TestCase>();
			if (excludeTestCaseID.contains(",")) {

				String excludeTCID[] = excludeTestCaseID.split(",");

				for (int i = 0; i < excludeTCID.length; i++) {
					for (TestCase tt : tempTestCases) {
						if (tt.testCaseID.equalsIgnoreCase(excludeTCID[i])) {
							removeTestCase.add(tt);

						}
					}
				}

			} else {
				for (TestCase tt : tempTestCases) {
					if (tt.testCaseID.equalsIgnoreCase(excludeTestCaseID)) {
						removeTestCase.add(tt);
						break;
					}
				}
			}

			tempTestCases.removeAll(removeTestCase);
		}

		Log.Debug("Controller/ExpandTestCase: Returning "
				+ tempTestCases.size()
				+ " testcase after Expansion. End of function with TestCase ID is "
				+ test.testCaseID);

		TestCase[] tempT = new TestCase[tempTestCases.size()];
		return tempTestCases.toArray(tempT);
	}
	
	
/**
 * Method to remove Similar MVM combination testcases
 * 
 */
	@Deprecated
	private ArrayList<TestCase> removeSimilarTestCases(TestCase mvmtest,ArrayList<TestCase> toRemove)
	{
		ArrayList<TestCase> removingTestCase=toRemove;
		//message("Method is starting to execute ... with "+mvmtest.testCaseID);
		
		Action[] cc=new Action[mvmtest.actions.size()];
		//message("Action array created "+cc.length);
		mvmtest.actions.toArray(cc);
		for(int k=0;k<cc.length;++k)
		{
			
			Action aa=cc[k];
			//message("Every Action to be checked "+aa.actionName);
			for(int i=0;i<aa.actionActualArguments.size();i++)
			{
				//message("Every action argumemts "+aa.actionArguments.get(i));
				int nextElm=i+1;
				//message("Previous element "+i +" The element comes "+nextElm+" " +aa.actionActualArguments.size());
				if(nextElm!=aa.actionActualArguments.size()){
				if(aa.actionActualArguments.get(i).equals(aa.actionActualArguments.get(nextElm)))
			{
					//message("Matching args "+aa.actionActualArguments.get(i));
					String[] tempBreak=mvmtest.testCaseID.split("_");
					for(String ss:tempBreak)
					{
						//message("count matching... "+StringUtils.countMatches(mvmtest.testCaseID,ss));
						if(StringUtils.countMatches(mvmtest.testCaseID,ss)>1)
						{
							removingTestCase.add(mvmtest);
						}
					}
					
			}
			}
//				else
//				{
//					message("Out of index");
//				}
//				
			}
		}
		
		return removingTestCase;
	}
	/**
	 * Check the command line Testcase contains ,
	 * 
	 * @param testcase
	 * 
	 * @return testcase
	 */
	private TestCase checkWithCommandLineInput(TestCase tempcom) {

		// Implement the code
		TestCase command_line_testcase = new TestCase();
		command_line_testcase.testCaseID = "Not Present";
		// message("checkWithCommandLineIfComma:: The , manualtestcase " +
		// manualTestCaseID);
		String cmdTestCases[] = manualTestCaseID.split(",");
		for (String manual : cmdTestCases) {
			// message("checkWithCommandLineIfComma " + manual +
			// " tempt Testcase " + tempcom.testCaseID);
			if (tempcom.parentTestCaseID.equalsIgnoreCase(tempcom.testCaseID)) {
				if (manual.equalsIgnoreCase(tempcom.testCaseID)) {
					command_line_testcase = tempcom;
					// message("checkWithCommandLineIfComma 1a " + manual +
					// " testcase " + tempcom.testCaseID);
					break;
				} else if (tempcom.testCaseID.toLowerCase().trim()
						.contains(manual.toLowerCase())) {
					command_line_testcase = tempcom;
					// message("checkWithCommandLineIfComma 1b " + manual +
					// " testcase " + tempcom.testCaseID);
					break;
				} else if (tempcom.testCaseID.equalsIgnoreCase("init")
						|| tempcom.testCaseID.equalsIgnoreCase("cleanup")) {
					command_line_testcase = tempcom;
					break;
				} else if (tempcom.parentTestCaseID.equalsIgnoreCase("init")
						|| tempcom.parentTestCaseID.equalsIgnoreCase("cleanup")) {
					command_line_testcase = tempcom;
					break;
				}

			} else {
				if (manual.equalsIgnoreCase(tempcom.parentTestCaseID)) {
					command_line_testcase = tempcom;
					// message("checkWithCommandLineIfComma 1a " + manual +
					// " testcase " + tempcom.testCaseID);
					break;
				} else if (tempcom.parentTestCaseID.toLowerCase().trim()
						.contains(manual.toLowerCase())) {
					command_line_testcase = tempcom;
					// message("checkWithCommandLineIfComma 1b " + manual +
					// " testcase " + tempcom.testCaseID);
					break;
				} else if (tempcom.testCaseID.equalsIgnoreCase("init")
						|| tempcom.testCaseID.equalsIgnoreCase("cleanup")) {
					command_line_testcase = tempcom;
					break;
				} else if (tempcom.parentTestCaseID.equalsIgnoreCase("init")
						|| tempcom.parentTestCaseID.equalsIgnoreCase("cleanup")) {
					command_line_testcase = tempcom;
					break;
				}
			}
		}
		// message("the testcase id evrytime "+tempcom.testCaseID);
		return command_line_testcase;

	}

	/*
	 * Check the command line Testcase exists
	 * 
	 * @param testcase
	 * 
	 * @return testcase
	 */
	@Deprecated
	private TestCase checkWithCommandLine(TestCase tempt) {

		TestCase commadLine = new TestCase();
		commadLine.testCaseID = "Not Present";
		// message("checkWithCommandLine:: 1a ParentId "+tempt.parentTestCaseID+" child testcase "
		// + tempt.testCaseID + " Manual Test " + manualTestCaseID);
		// if(manualTestCaseID.indexOf(",")>0)
		// {
		// return tempt;
		// }
		if (tempt.parentTestCaseID.equalsIgnoreCase(tempt.testCaseID)) {
			if (manualTestCaseID.equalsIgnoreCase(tempt.testCaseID)) {
				// message("checkWithCommandLine:: 1b " + tempt.testCaseID +
				// " Manual Test " + manualTestCaseID);
				commadLine = tempt;
				// return tempt;
			}
			if (tempt.testCaseID.trim().toLowerCase()
					.contains(manualTestCaseID.toLowerCase())) {
				// message("checkWithCommandLine:: index  "+manualTestCaseID.indexOf(","));
				// message("checkWithCommandLine:: 1cc " + tempt.testCaseID +
				// " Manual Test " + manualTestCaseID);
				commadLine = tempt;
				// return tempt;
			}
			if (manualTestCaseID == null) {
				commadLine = tempt;
				// return tempt;
			}
		} else {
			if (manualTestCaseID.equalsIgnoreCase(tempt.parentTestCaseID)) {
				commadLine = tempt;
			}
			if (tempt.parentTestCaseID.trim().contains(manualTestCaseID)) {
				// message("checkWithCommandLine:: index  "+manualTestCaseID.indexOf(","));
				// message("checkWithCommandLine:: 1cc " + tempt.testCaseID +
				// " Manual Test " + manualTestCaseID);
				commadLine = tempt;
				// return tempt;
			}
			if (manualTestCaseID == null) {
				commadLine = tempt;

			}
		}

		// } else {
		// //message("checkWithCommandLine:: 1d " + tempt.testCaseID +
		// " Manual Test " + manualTestCaseID);
		// //testcasesNotPresent = manualTestCaseID;
		// tempt.testCaseID = "Not Present";
		// return tempt;
		// }

		return commadLine;
	}

	private String GetActualCombination(String entireValue,
			String valueToSubstitute) {

		String tempValue = StringUtils.EMPTY;
		// message("GetActualComb:: 1a "+entireValue+" valusubs "+valueToSubstitute);
		if (entireValue.contains("=")) {
			Log.Debug("Controller/GetActualCombination : entireValue contains an = sign ");
			String[] splitVariableToFind = Excel
					.SplitOnFirstEquals(entireValue);

			Log.Debug("Controller/GetActualCombination : Length of  splitVariableToFind = "
					+ splitVariableToFind.length);
			if (splitVariableToFind.length <= 1) {
				Log.Debug("Controller/GetActualCombination : End of function with variableToFind = "
						+ entireValue
						+ " and its value is -> "
						+ valueToSubstitute);
				// message("GetActualComb:: 1b "+entireValue+" valusubs "+valueToSubstitute);
				return valueToSubstitute;
			}
			if (valueToSubstitute.contains("=")) {
				// if (valueToSubstitute.contains("{")) {
				// valueToSubstitute = valueToSubstitute.replace("{", "");
				// } else if (valueToSubstitute.contains("}")) {
				// valueToSubstitute = valueToSubstitute.replace("}", "");
				// }
				// message("GetActualComb:: 3a "+valueToSubstitute.replace("{",""));
				tempValue = valueToSubstitute;
				// message("GetActualComb:: GGHH"+tempValue);
			} else {
				tempValue = splitVariableToFind[0] + "=" + valueToSubstitute;
			}
			// message("GetActualComb:: 1c "+tempValue);
		} // First Check in the Context Variable
		else {
			tempValue = valueToSubstitute;
			// message("GetActualComb:: 1d "+tempValue);
		}

		Log.Debug("Controller/GetActualCombination : End of function with variableToFind = "
				+ entireValue + " and its value is -> " + tempValue);
		// message("GetActualComb:: 1e "+tempValue);
		return tempValue;
	}

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

	private String GetTheActualValue(String entireValue) throws Exception {
		String tempValue = StringUtils.EMPTY;
		// message("enitre value "+entireValue);
		if (entireValue.contains("=")) {
			Log.Debug("Controller/GetTheActualValue : entireValue contains an = sign ");
			String[] splitVariableToFind = Excel
					.SplitOnFirstEquals(entireValue);
			// //TODO I need to Check here what is going on with the Values

			Log.Debug("Controller/GetTheActualValue : Length of  splitVariableToFind = "
					+ splitVariableToFind.length);
			if (splitVariableToFind.length <= 1) {
				Log.Debug("Controller/GetTheActualValue : End of function with variableToFind = "
						+ entireValue + " and its value is -> " + entireValue);

				return entireValue;
			}

			tempValue = splitVariableToFind[1];
			// message("as temp val have = "+tempValue);

			if (tempValue.startsWith("$$") && tempValue.endsWith("%")) {
				// message("EQUAL contextvariablemvm "+tempValue);
				tempValue = Utility.TrimStartEnd(tempValue, '$', 1);
				tempValue = tempValue.replaceAll("%", "");
				tempValue = ContextVar.getContextVar(tempValue);
				// message("EQUAL contextvariablemvm value"+tempValue);
			} else if (tempValue.startsWith("$$") & tempValue.contains("#")) {
				// message("GetValue:/This is Indexed = " + tempValue);
				tempValue = Utility.TrimStartEnd(tempValue, '$', 1);
				tempValue = Excel._indexedMacroTable.get(tempValue
						.toLowerCase());
				// message("GetValue:/This is Indexed The Value = " +
				// tempValue);
			} else {
				// message("The entire value unchanged "+entireValue);
				tempValue = entireValue;
			}
			Log.Debug("Controller/GetTheActualValue : variableToFind = "
					+ tempValue);
		} // First Check in the Indexed Variable
		else if (entireValue.startsWith("$$") & entireValue.contains("#")) {
			entireValue = Utility.TrimStartEnd(entireValue, '$', 1);
			// message("GetValue:/This is a entire Indexed\t" + entireValue);
			tempValue = Excel._indexedMacroTable.get(entireValue.toLowerCase());
			// /message("GetValue:/This is a Indexed\t" + tempValue);

		} else if (entireValue.startsWith("$$%") && entireValue.endsWith("%")) {
			entireValue = Utility.TrimStartEnd(entireValue, '$', 1);
			entireValue = entireValue.replaceAll("%", "");
			tempValue = ContextVar.getContextVar(entireValue);
		} else {
			// message("TempVal=EntireVal "+tempValue);
			tempValue = entireValue;
		}

		Log.Debug("Controller/GetTheActualValue : End of function with variableToFind = "
				+ entireValue + " and its value is -> " + tempValue);

		// message("The tempvalue "+tempValue);
		return tempValue;
	}

	/***
	 * Function to Execute a particular testcase.
	 * 
	 * @param test
	 *            DataClass of the TestCase.
	 */
	private void RunTestCaseForMolecule(TestCase test1) throws Exception {
		if (test1.automated == false) {
			Log.Debug("Controller/RunTestCaseForMolecule : TestCase ID {0} is a MANUAL TestCase. Automation Framework is ignoring this TestCase."
					+ test1.testCaseID);
			message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : {0}. This is a Manual TestCase."
					+ test1.testCaseID);
			return;
		}

		Log.Debug("Controller/RunTestCaseForMolecule: Start of function with a new TestCase. TestCase ID is "
				+ test1.testCaseID);

		Log.Debug("Controller/RunTestCaseForMolecule: Calling ExpandTestCase With TestCase ID is "
				+ test1.testCaseID);

		// message("The Molecule contains the actionActualArguments=="+test1.actions.get(0).actionActualArguments);
		TestCase[] expandedTestCases = ExpandTestCase(test1, false);
		Log.Debug("Controller/RunTestCaseForMolecule: After Expansion for TestCase ID is "
				+ test1.testCaseID
				+ " the number of expanded test case is : "
				+ expandedTestCases.length);

		ArrayList<Thread> ThreadPool = new ArrayList<Thread>();

		for (int i = 0; i < expandedTestCases.length; i++) {
			TestCase test = expandedTestCases[i];
			errorMessageDuringMoleculeCaseExecution.put(test.stackTrace,
					StringUtils.EMPTY);
		}

		// These are not kept in a try catch block. This is to make sure that
		// any time an Exception happens
		// in an Molecule, then don't proceed as they are considered as part of
		// one Abstract Test Case
		// and are not expanded like other TestCases in the TestCases sheet for
		// the MultiValued Macro.
		for (int i = 0; i < expandedTestCases.length; i++) {
			final TestCase test = expandedTestCases[i];
			// Start a new thread and run the expanded test case on it
			if (test1.concurrentExecutionOnExpansion == true) {
				Thread thread = new Thread(new Runnable() {

					public void run() {
						RunExpandedTestCaseForMolecule(test);
					}
				});

				thread.start();
				// set as background thread
				threadIdForTestCases.put(test.stackTrace, "" + thread.getId());

				ThreadPool.add(thread);
			} else {
				if (StringUtils.isBlank(test.threadID)) {
					threadIdForTestCases
							.put(test.stackTrace, StringUtils.EMPTY);
				} else // Assign the Parent Test Case Thread.
				{
					threadIdForTestCases.put(test.stackTrace, test.threadID);
				}

				RunExpandedTestCaseForMolecule(test);
			}

			if (StringUtils.isNotBlank(errorMessageDuringMoleculeCaseExecution
					.get(test.stackTrace))
					&& test1.concurrentExecutionOnExpansion == false) {
				String errorMsg = (((String) errorMessageDuringMoleculeCaseExecution
						.get(test.stackTrace)));
				errorMessageDuringMoleculeCaseExecution.put(test.stackTrace,
						StringUtils.EMPTY);
				throw new Exception(errorMsg);
			}
		}

		// Wait for all the Threads i.e. expanded test cases to finish.
		for (int t = 0; t < ThreadPool.size(); ++t) {
			((Thread) ThreadPool.get(t)).join();
		}

		String error = StringUtils.EMPTY;
		for (int i = 0; i < expandedTestCases.length; i++) {
			TestCase test = expandedTestCases[i];
			error += (((String) errorMessageDuringMoleculeCaseExecution
					.get(test.stackTrace)));
			errorMessageDuringMoleculeCaseExecution.put(test.stackTrace,
					StringUtils.EMPTY);
		}

		if (StringUtils.isNotBlank(error)) {
			throw new Exception(error);
		}

		Log.Debug("Controller/RunTestCaseForMolecule: End of function with TestCase ID is "
				+ test1.testCaseID);
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
	 * Function will verify if the TestPlan ID, Topology ID and other related
	 * stuff specified is correct and exists in the Result Database or not.
	 */
	public boolean ValidateDatabaseEntries() throws InterruptedException {
		Log.Debug("Controller/ValidateDatabaseEntries : Start of function");
		try {
			// message("The Seesion Id  "+sessionid);
			davosclient.heartBeat(sessionid);
		} catch (DavosExecutionException e) {
			e.printStackTrace();
		}
		// Check if the TestCycle Exists or not
		// BusinessLayer.TestCycle tID = new BusinessLayer.TestCycle();
		// if (testCycleId != null) {
		// try {
		// if (!tID.TestCycleExist(Integer.parseInt(testCycleId))) {
		// Log.Error("TestCycle ID " + testCycleId
		// + " doesnot exists in " + dBHostName + "\\"
		// + dBName + " Database.");
		// message("Use a Valid TestCycle ID or use Upload Tool to create a new TestCycle ID");
		// return false;
		// }
		// } catch (NumberFormatException e) {
		// Log.Error("Exception while parsing testCycleId to integer");
		// e.printStackTrace();
		// return false;
		// } catch (Exception e) {
		// e.printStackTrace();
		// return false;
		// }
		// }
		//
		// // Check if the TESTPLAN ID Exists or not..
		// BusinessLayer.TestPlan testplanObj = new BusinessLayer.TestPlan();
		// try {
		// if (!testplanObj.TestplanIDExist(Integer.parseInt(TestPlanId))) {
		// Log.Error("TESTPLAN ID " + TestPlanId + " does not exist in "
		// + dBHostName + "\\" + dBName + " Database.");
		// message("Use a Valid TESTPLAN ID or use ZERMATT to create a new TestPlan ID");
		// return false;
		// }
		// } catch (NumberFormatException e1) {
		// Log.Error("Exception while parsing TestPlanId to integer");
		// e1.printStackTrace();
		// return false;
		// } catch (Exception e1) {
		// e1.printStackTrace();
		// return false;
		// }
		// // Check if the TOPOLOGYSET ID Exists or not..
		// BusinessLayer.Topologyset topologysetObj = new
		// BusinessLayer.Topologyset();
		// try {
		// if
		// (!topologysetObj.TopologysetExist(Integer.parseInt(topologySetId))) {
		// Log.Error("TOPOLOGY ID " + topologySetId + "does not exist in "
		// + dBHostName + "\\" + dBName + " Database.");
		// message("Use a Valid TOPOLOGYSET ID or use ZERMATT to create a new Topologyset ID");
		// return false;
		// }
		// } catch (NumberFormatException e1) {
		// Log.Error("Exception while parsing Topologyset Id to integer");
		// e1.printStackTrace();
		// return false;
		// } catch (Exception e) {
		// e.printStackTrace();
		// return false;
		// }
		// // Read topology from TopologyTopologyXref for given TopologysetID
		// try {
		//
		// List<Integer> topologyIDs =
		// topologysetObj.ReadTopologySetXref(Integer.parseInt(topologySetId));
		// TopologySet = new TopologySet[topologyIDs.size()];
		// for (int i = 0; i < topologyIDs.size(); i++) {
		// TopologySet[i] = new TopologySet();
		// TopologySet[i].topologyID = topologyIDs.get(i).toString();
		// }
		//
		// } catch (Exception e) {
		// message("Exception raised for TopologySet ID : " + topologySetId);
		// e.printStackTrace();
		// return false;
		// }
		//
		// if (!(TopologySet.length > 0)) {
		// Log.Error("No Topology information specified in the TopologySet sheet.");
		// return false;
		// }
		//
		// for (int i = 0; i < TopologySet.length; i++) {
		// BusinessLayer.Topology topologyObj = new BusinessLayer.Topology();
		// try {
		// if
		// (!topologyObj.TopologyExist(Integer.parseInt(TopologySet[i].topologyID)))
		// {
		// Log.Error("Topology ID " + TopologySet[i].topologyID
		// + " doesnot exists in " + dBHostName + "\\"
		// + dBName + " Database.");
		// message("Use a Valid Topology ID or use Upload Tool to create a new Topology ID");
		// return false;
		// } else {
		// // read topology Role from topology table
		// TopologySet[i].topologyRole =
		// topologyObj.getTopologyRole(Integer.parseInt(TopologySet[i].topologyID));
		// }
		// } catch (NumberFormatException e) {
		// Log.Error("Exception while parsing topology ID to integer");
		// return false;
		// } catch (Exception e) {
		// e.printStackTrace();
		// return false;
		// }
		// }
		//
		// Log.Debug("Controller/ValidateDatabaseEntries : End of function");
		//
		return true;
	}

	/***
	 * Function to Initialize the variables required by the Controller to Do
	 * Setup/TearDown and run testcases. This can only be done after reading the
	 * Test Case Excel sheet.
	 * 
	 */
	private void InitializeVariables(Excel readExcel) throws Exception {
		Log.Debug("Controller/InitializeVariables :Start of Function");

		if (StringUtils.isEmpty(scriptLocation)) {
			scriptLocation = readExcel.ScriptLocation() + ";"
					+ opts.workingDirectory; // appends the current directory
			String[] spliArr = scriptLocation.split(";");
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

			scriptLocation = scriptLocation + ";" + newLocation;
			// message("Notun Location\t"+scriptLocation);
			Log.Debug("Controller::The Updated ScriptLocation\t"
					+ scriptLocation);
		}
		// from where the Zug is invoked

		if (StringUtils.isEmpty(includeMolecules)) {
			includeMolecules = readExcel.IncludeMolecules();
		}
		// Add the Include Sheet to NameSpace
		if (includeMolecules != null && includeMolecules != StringUtils.EMPTY) {
			includeFlag = false;
			// System.out.println("Its Coming Here\t"+includeFlag);
			readExcel.AddToExternalSheets("Include", includeMolecules); // if
			// the
			// include
			// switch
			// is
			// used
			// it
			// changes
			// the
			// inlcude
			// file
			// in
			// chur
			// sheet
			// with
			// the
			// given
		}

		// System.out.println("Script at - >"+scriptLocation);
		Log.Debug("Controller/InitializeVariables : ScriptLocation = "
				+ scriptLocation);
		productLogFiles = readExcel.ProductLogFiles();
		for (int i = 0; i < productLogFiles.length; i++) {
			Log.Debug("Controller/InitializeVariables : Product Log File- "
					+ productLogFiles[i]);
		}

		// Get Prototype sheet values
		prototypeHashTable = readExcel.Prototypes();

		// Set the TestPlan and TestCase timeout context Variable...
		// Test Plan Timeout Depends a lot on the Longevity Parameters
		if (repeatDurationSpecified) {
			CreateContextVariable("ZUG_TESTSUITE_TIMEOUT="
					+ ((repeatDurationLong / 1000) + readExcel
							.TESTPLAN_TIMEOUT()));

		} else if (repeatCount >= 1) {
			CreateContextVariable("ZUG_TESTSUITE_TIMEOUT=" + repeatCount
					* readExcel.TESTPLAN_TIMEOUT());
		}

		// No Affect to the Test Step Timeout.
		CreateContextVariable("ZUG_TESTSTEP_TIMEOUT="
				+ readExcel.TESTSTEP_TIMEOUT());

		validTopoDetail = readExcel.ValidTopoDetail();
		Log.Debug("Controller/InitializeVariables : validTopoDetail = "
				+ validTopoDetail);

		if (dbReporting) {
			dBHostName = readExcel.DBHostName();
			// message("The DBHOST NAME "+dBHostName);
			Log.Debug("Controller/InitializeVariables : DBHostName = "
					+ dBHostName);
			// dBName = readExcel.DBName();
			// Log.Debug("Controller/InitializeVariables : dBName = " + dBName);
			dbUserName = readExcel.DBUserName();
			Log.Debug("Controller/InitializeVariables : dbUserName = "
					+ dbUserName);
			dbUserPassword = readExcel.DBUserPassword();
			Log.Debug("Controller/InitializeVariables : dbUserPassword = "
					+ dbUserPassword);
			testSuitName = readExcel.TestSuitName();
			// ZUG Specific ContextVariable to store ZUG_TESTSUITEID
			ContextVar.setContextVar("ZUG_TESTSUITEID", testSuitName);
			Log.Debug("Controller/InitializeVariables : testPlanName = "
					+ testSuitName);
			testSuitRole = readExcel.TestSuitRole();
			Log.Debug("Controller/InitializeVariables : testPlanRole = "
					+ testSuitRole);
		}
		Log.Debug("Controller/InitializeVariables :End of Function");
	}

	/***
	 * Function to Set a Context Variable
	 * 
	 * @param variableAndValue
	 *            Variable along with its Value separated by = sign. For Example
	 *            : PATH="C:\test"
	 */
	private void CreateContextVariable(String variableAndValue)
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
	 * Function to save the Test Suite to the Result Database.
	 */
	// /TODO:this method is not used now.
	@Deprecated
	private void SaveTestSuite() throws Exception, DavosExecutionException {
		Log.Debug("Controller/SaveTestSuite : Start of Function ");
		roleid = davosclient.role_read(readExcel.TestSuitRole());
		productid = davosclient.testPlan_read(TestPlanId, "product_id");

		testSuiteId = davosclient.testSuite_write(productid, roleid,
				testSuitName, "Ready", "Automatic", "", "");
		message("SaveTestSuite::\tThe test suite date to be send "
				+ readExcel.TestSuitName() + "\t" + readExcel.TestSuitRole()
				+ "\t" + roleid + "This is a product\t" + productid);

		// // /Added to update Testsuite name and topologyset list
		// BusinessLayer.TestPlanUpdateData testplanUpdateData = new
		// BusinessLayer.TestPlanUpdateData();
		// BusinessLayer.TestPlanUpdate TestplanUpdate = new
		// BusinessLayer.TestPlanUpdate();
		//
		// // /Get the required field to update TestPlan Data table.
		// testplanUpdateData.TestSuiteData.setTestSuiteName(testSuitName);
		// // /////////////TODO : confusion in test suit role name OR ID
		// testplanUpdateData.TestSuiteData.setRoleName(testSuitRole);//
		// (1);//Integer.parseInt(testSuitRole));
		// testplanUpdateData.TestSuiteData.setStatus("active");
		// Log.Debug("Controller/SaveTestSuite : Saving Test Suite Name "
		// + testSuitName + " with Role as " + testSuitRole);
		//
		// // /get the TestPlan ID
		// testplanUpdateData.testPlanData.setTestPlanId(Integer.parseInt(TestPlanId));
		//
		// BusinessLayer.TestPlan testplanObj = new BusinessLayer.TestPlan();
		//
		// // if this works, then possibly remove all the manual setters above
		// testplanUpdateData.testPlanData =
		// testplanObj.getTestPlanData(Integer.parseInt(TestPlanId));
		//
		// BusinessLayer.TestPlanTopologySetData TopologySetData = new
		// BusinessLayer.TestPlanTopologySetData();
		//
		// String topologyIDS = StringUtils.EMPTY;
		// for (int i = 0; i < TopologySet.length; i++) {
		// BusinessLayer.TopologyDetail topologyDetail = new
		// BusinessLayer.TopologyDetail();
		// topologyDetail.set_topologyId(Integer.parseInt(TopologySet[i].topologyID));
		//
		// topologyIDS += topologyDetail.get_topologyId() + ",";
		// // /Add the topology name to the topology set
		// TopologySetData.get_topologyList().add(
		// topologyDetail.get_topologyId());
		// }
		//
		// topologyIDS = Utility.TrimStartEnd(topologyIDS, ',', 0);//TODO get
		// the product ID from DAVOS for the TestPlan
		// topologyIDS = Utility.TrimStartEnd(topologyIDS, ',', 1);//TODO get
		// the Role ID from DAVOS for testsuite rolename
		//
		// // Context Variable to Store the Comma Separated Topology ID's.
		// ContextVar.setContextVar("ZUG_TOPO", topologyIDS);
		// testplanUpdateData.TopologySets.add(TopologySetData);
		//
		// Log.Debug("Controller/SaveTestSuite : Saving Test Suite Name "
		// + testSuitName + " with Role as " + testSuitRole);
		// TestplanUpdate.Save(testplanUpdateData);
		// testSuiteId = testplanUpdateData.TestSuiteListData.getTestSuiteId();
		//
		// // Harness Specific ContextVariable to store TopologySet ID
		ContextVar.setContextVar("ZUG_TOPOSET", "" + topologySetId);
		this.TOPOSET = ContextVar.getContextVar("ZUG_TOPOSET");

		Log.Debug("Controller/SaveTestPlan : SUCCESSFULLY SAVED Test Plan Name "
				+ testSuitName + " with Role as " + testSuitRole);
		Log.Debug("Controller/SaveTestPlan : End of Function ");
	}

	/***
	 * Function to connect to the Database. This function will throw an
	 * exception if the Database or the user credentials provided is not a valid
	 * user.
	 */
	public static String roleid = null, productid = null, sessionid = null;

	public boolean ConnectToDavos() throws DavosExecutionException {
		Log.Debug("Controller/ConnectToDavos : Start of Function ");
		boolean connnect_flag = false;
		try {

			message("Connecting to DAVOS --> URL: " + readExcel.DBHostName()
					+ " User: " + readExcel.DBUserName());
			davosclient = new DavosClient(readExcel.DBHostName(),
					readExcel.DBUserName(), readExcel.DBUserPassword());
			sessionid = davosclient.getSessionId();
			Log.Debug("Davos Connection Successful. Session ID: " + sessionid);
			// TODO create DavosClient instance global variable.dbHOst dbname
			// Session id for Davos connection
			ContextVar.setContextVar("ZUG_SESSIONID", sessionid);
			connnect_flag = true;
		} catch (DavosExecutionException dd) {

			Log.Error("Controller/ConnectToDavos : Exception while Connecting to Davos "
					+ dBName
					+ " of host "
					+ dBHostName
					+ " with user "
					+ dbUserName + ". The Exception is " + dd.getMessage());
			dd.printStackTrace();
			connnect_flag = false;
			System.exit(1);

		} catch (Exception ex) {
			Log.Error("Controller/ConnectToDavos : Exception while connecting to Davos "
					+ dBName
					+ " of host "
					+ dBHostName
					+ " with user "
					+ dbUserName + ". The Exception is " + ex.getMessage());
			Log.Debug("Controller/ConnectToDavos : End of Function. Function returns FALSE.");
			connnect_flag = false;
		}

		Log.Debug("Controller/ConnectToDavos : End of Function. Function returns TRUE.");

		return connnect_flag;
	}

	private void RunTestCaseForMain(Object act) throws Exception,
			DavosExecutionException {
		Log.Debug("Controller/RunTestCaseForMain : Start of function");
		TestCase[] testcases = (TestCase[]) act;

		// System.out.println("\n*** Number of TestCase to Execute is "+
		// testcases.length + "***\n ");
		System.out.println("\n*** Number of TestCase in Chur Sheet "
				+ testcases.length + " ***\n ");
		System.out.println("\n*** Start Executing the testcases ***\n ");

		// Harness Specific ContextVariable to store AH_TPSTARTTIME = Timestamp
		// when Test Plan execution started
		// ZUG Specific ContextVariable to store ZUG_TPSTARTTIME = Timestamp
		// when Test Plan execution started
		ContextVar.setContextVar("ZUG_TPSTARTTIME", Utility.dateAsString());

		if (repeatDurationSpecified) {
			int count = 1;
			// Normally we will log the debug logs unless one want to turn that
			// OFF
			// Specifically for the case of Longevity
			isLongevityOn = true;

			Log.TurnOFFDebugLogs = true;

			HiPerfTimer testPlanStartTime = new HiPerfTimer();

			testPlanStartTime.Start();
			boolean testcasenotfound = false;
			for (TestCase test : testcases) {
				// If this is a cleanup Step, then dont run it now, that should
				// be handled at the end.
				// Or if a test case has no actions to execute, then dont
				// execute the test case.
				if (test.actions.size() <= 0
						|| (test.testCaseID.compareToIgnoreCase("cleanup") == 0)) {
					continue;
				}

				// If TestCaseId is specified in the command prompt, then make
				// sure, that
				// the current executing test case is also specified.

				if (test.testCaseID.compareToIgnoreCase("init") != 0) {
					if (StringUtils.isNotBlank(manualTestCaseID)) {

						if (!manualTestCaseID.toLowerCase().contains(
								test.testCaseID.toLowerCase().trim())) {
							// if
							// (!manualTestCaseID.equalsIgnoreCase(test.testCaseID.trim()))
							// {
							testcasenotfound = true;
							continue;
						} else {
							testcasenotfound = false;
						}
					}
				}

				if ((initWorkedFine == true)
						|| (initWorkedFine == false && (test.testCaseID
								.compareToIgnoreCase("cleanup") == 0))) {
					try {
						if (test.automated) {
							Log.Debug("Controller/Main : TestCase ID "
									+ test.testCaseID
									+ " is a Automated TestCase. Calling controller.RunTestCase...");
							// Function to run and Execute the TestCase

							if (test.testCaseID.compareToIgnoreCase("init") == 0) {

								TestCase tempTest = GenerateNewTestCaseID(test,
										count);
								// message("The generated Id 0a "+tempTest.testCaseID);
								RunTestCase(tempTest);
							} else {
								// message("The generated Id 0b "+test.testCaseID);
								RunTestCase(test);
							}
						} else {
							Log.Debug("Controller/RunTestCaseForMain : TestCase ID "
									+ test.testCaseID
									+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
							message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
									+ test.testCaseID
									+ ". This is a Manual TestCase.");
							message("********************************************************************************** ");
						}

					} catch (Exception e) {
						if (test.testCaseID.compareToIgnoreCase("init") == 0) {
							initWorkedFine = false;
							Log.Debug("Controller/RunTestCaseForMain : TestCase ID "
									+ test.testCaseID
									+ " is an Initialization and it failed.");

						}
					}
				}

				if (!debugMode) {
					if (_testPlanStopper) // Sleep for 500 ms, but wake up
					// immediately
					{
						Log.Error("Controller/RunTestCaseForMain : TestCase "
								+ test.testCaseID
								+ " took longer time to execute. Test Plan Time Specified = "
								+ ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
								+ " seconds  is over.");
						break;
					}
				}
			}
			// if(testcasenotfound)
			// message(manualTestCaseID+" The testcase is not Present in Chur Sheet");
			testPlanStartTime.Stop();

			repeatDurationLong -= testPlanStartTime.Duration();

			// Make sure we run all the test cases, till the repeatDurationLong
			// is more than ONE minute = 1000 * 60 - this is not a magic number
			// :-).
			// We should not run the initialization and cleanup step in this
			// case.
			while (repeatDurationLong > 1000 * 60) {

				testPlanStartTime.Start();
				count++;
				for (TestCase test : testcases) {
					if (!debugMode) {
						if (_testPlanStopper) // Sleep for 500 ms, but wake up
						// immediately
						{
							Log.Error("Controller/RunTestCaseForMain : TestCase "
									+ test.testCaseID
									+ " took longer time to execute. Test Plan Time Specified = "
									+ ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
									+ " seconds  is over.");
							break;
						}
					}

					// If the test case have no action or is a Initialization or
					// a cleanup Step then Ignore the Test Case.
					if (test.actions.size() <= 0
							|| (test.testCaseID.compareToIgnoreCase("cleanup") == 0)
							|| (test.testCaseID.compareToIgnoreCase("init") == 0)) {
						continue;
					}

					// If TestCaseId is specified in the prompt, then make sure,
					// that
					// the current executing test case is also specified.
					if (test.testCaseID.compareTo("init") != 0) {

						if (StringUtils.isNotBlank(manualTestCaseID)) {
							if (!manualTestCaseID.toLowerCase().contains(
									test.testCaseID.toLowerCase().trim())) {
								continue;
							}
						}
					}

					// Make sure we run only those test cases, who are specified
					// for repeatation.
					// Other test cases are not repeated :-).
					// If nothing is specified, then all the test cases are
					// repeated.
					if (StringUtils.isNotBlank(testsToRepeat)) {
						if (!testsToRepeat.contains(test.testCaseID.trim())) {
							continue;
						}
					}

					if ((initWorkedFine == true)) {
						try {
							if (test.automated) {
								Log.Debug(String
										.format("Controller/Main : TestCase ID {%s} is a Automated TestCase. Calling controller.RunTestCase... ",
												test.testCaseID));
								// Function to run and Execute the TestCase
								if (!(test.testCaseID
										.compareToIgnoreCase("init") == 0)) {
									TestCase tempTest = GenerateNewTestCaseID(
											test, count);
									message("The generated Id 0a "
											+ tempTest.testCaseID);
									RunTestCase(tempTest);
								} else {
									message("The generated Id 0b "
											+ test.testCaseID);
									RunTestCase(test);
								}

							} else {
								Log.Debug("Controller/RunTestCaseForMain : TestCase ID "
										+ test.testCaseID
										+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
								message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
										+ test.testCaseID
										+ ". This is a Manual TestCase.");
								message("********************************************************************************** ");
							}
						} catch (Exception e) {
							if (test.testCaseID.compareToIgnoreCase("init") == 0) {
								initWorkedFine = false;
								Log.Debug("Controller/RunTestCaseForMain : TestCase ID "
										+ test.testCaseID
										+ " is an Initialization and it failed.");
							}
						}
					}

				}
				testPlanStartTime.Stop();
				repeatDurationLong -= testPlanStartTime.Duration();
			}

			// At the end Run the Cleanup Step
			for (TestCase test : testcases) {
				if (!debugMode) {
					if (_testPlanStopper) // Sleep for 500 ms, but wake up
					// immediately
					{
						Log.Error("Controller/RunTestCaseForMain : TestCase "
								+ test.testCaseID
								+ " took longer time to execute. Test Plan Time Specified ="
								+ ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
								+ " seconds  is over.");
						break;
					}
				}

				// If the test case have no action or is not a cleanup Step,
				// then Ignore it.
				if (test.actions.size() <= 0
						|| (test.testCaseID.compareToIgnoreCase("cleanup") != 0)) {
					continue;
				}

				try {
					if (test.automated) {
						Log.Debug("Controller/RunTestCaseForMain : TestCase ID "
								+ test.testCaseID
								+ " is a Automated TestCase. Calling controller.RunTestCase... ");
						// Function to run and Execute the TestCase

						RunTestCase(test);

					} else {
						Log.Debug("Controller/RunTestCaseForMain : TestCase ID "
								+ test.testCaseID
								+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
						message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
								+ test.testCaseID
								+ ". This is a Manual TestCase.");
						message("********************************************************************************** ");
					}

				} catch (Exception e) {
				}
			}
		} // In the normal Scenario - when the RepeatDuration is not specified,
			// or
			// the
			// RepeatCount is specified, then use the following code snippet -
			// the
			// code
			// or the control will follow the below
		else {
			int count = 1;
			if (repeatCount > 1) {
				// Normally we will log the debug logs unless one want to turn
				// that OFF
				// Specifically for the case of Longevity
				isLongevityOn = true;
				Log.TurnOFFDebugLogs = true;
				message("Repeat Count is on   " + isLongevityOn);
			}
			boolean testcasenotpresent = false;
			for (TestCase test : testcases) {
				// If this is a cleanup Step, then dont run it now, that should
				// be handled at the end.
				// Or if a test case has no actions to execute, then dont
				// execute the test case.
				if (test.actions.size() <= 0
						|| (test.testCaseID.compareToIgnoreCase("cleanup") == 0)) {
					continue;
				}

				// If TestCaseId is specified in the command prompt, then make
				// sure, that
				// the current executing test case is also specified.

				if (test.testCaseID.compareToIgnoreCase("init") != 0) {
					if (StringUtils.isNotBlank(manualTestCaseID)) {
						// message("The manual commandline 1b " +
						// manualTestCaseID + "\n The Testcase ids " +
						// test.testCaseID);
						if (!manualTestCaseID.toLowerCase().contains(
								test.testCaseID.toLowerCase().trim())) {
							// if(!manualTestCaseID.equalsIgnoreCase(test.testCaseID.trim())){

							testcasenotpresent = true;

							continue;
						} else {
							testcasenotpresent = false;
						}

					}
				}
				// message("The testcase step comming alpha " +
				// test.testCaseID);

				if ((initWorkedFine == true)
						|| (initWorkedFine == false && (test.testCaseID
								.compareToIgnoreCase("cleanup") == 0))) {
					try {
						if (test.automated) {
							Log.Debug("Controller/Main : TestCase ID "
									+ test.testCaseID
									+ " is a Automated TestCase. Calling controller.RunTestCase... ");
							// Function to run and Execute the TestCase
							if (!(test.testCaseID.compareToIgnoreCase("init") == 0)
									&& isLongevityOn) {
								TestCase tempTest = GenerateNewTestCaseID(test,
										count);
								// message("The Testcases to match1a " +
								// tempTest.testCaseID);
								RunTestCase(tempTest);
							} else {
								test.threadID = (String.valueOf(Thread
										.currentThread().getId()));
							}
							// message("The generated Id 2a " +
							// test.testCaseID);
							RunTestCase(test);
						} else {
							Log.Debug("Controller/RunTestCaseForMain : TestCase ID "
									+ test.testCaseID
									+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
							message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
									+ test.testCaseID
									+ ". This is a Manual TestCase.");
							message("********************************************************************************** ");
						}

					} catch (Exception e) {
						if (test.testCaseID.compareToIgnoreCase("init") == 0) {
							initWorkedFine = false;
							Log.Debug("Controller/RunTestCaseForMain : TestCase ID "
									+ test.testCaseID
									+ " is an Initialization and it failed.");

						}
					}
				}

				if (!debugMode) {
					if (_testPlanStopper) // Sleep for 500 ms, but wake up
					// immediately
					{
						Log.Error("Controller/RunTestCaseForMain : TestCase "
								+ test.testCaseID
								+ " took longer time to execute. Test Plan Time Specified = "
								+ ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
								+ " seconds  is over.");
						break;
					}
				}
			}
			// if (testcasenotpresent) {
			// //message(manualTestCaseID +
			// " The testcase is not Present in Chur Sheet");
			// }
			repeatCount--;

			// Make sure we run all the test cases, till the RepeatCount is
			// equal to 0.
			// We should not run the initialization and cleanup step in this
			// case.
			while (repeatCount > 0) {
				count++;
				for (TestCase test : testcases) {
					if (!debugMode) {
						if (_testPlanStopper) // Sleep for 500 ms, but wake up
						// immediately
						{
							Log.Error("Controller/RunTestCaseForMain : TestCase "
									+ test.testCaseID
									+ " took longer time to execute. Test Plan Time Specified = "
									+ ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
									+ " seconds  is over.");
							break;
						}
					}

					// If the test case have no action or is a Initialization or
					// a cleanup Step then Ignore the Test Case.
					if (test.actions.size() <= 0
							|| (test.testCaseID.compareToIgnoreCase("cleanup") == 0)
							|| (test.testCaseID.compareToIgnoreCase("init") == 0)) {
						continue;
					}

					// If TestCaseId is specified in the command prompt, then
					// make sure, that
					// the current executing test case is also specified.
					if (test.testCaseID.compareToIgnoreCase("init") != 0) {
						// message("The manual commandline 1c" +
						// manualTestCaseID + "\n The Testcase ids " +
						// test.testCaseID);
						if (StringUtils.isNotBlank(manualTestCaseID)) {
							if (!manualTestCaseID.toLowerCase().contains(
									test.testCaseID.toLowerCase().trim())) {
								continue;
							}
						}
					}

					// Make sure we run only those test cases, who are specified
					// for repeatation.
					// Other test cases are not repeated :-).
					// If nothing is specified, then all the test cases are
					// repeated.
					if (StringUtils.isNotBlank(testsToRepeat)) {
						if (!testsToRepeat.contains(test.testCaseID.trim())) {
							continue;
						}
					}

					if ((initWorkedFine == true)) {
						try {
							if (test.automated) {
								Log.Debug("Controller/Main : TestCase ID "
										+ test.testCaseID
										+ " is a Automated TestCase. Calling controller.RunTestCase... ");
								// Function to run and Execute the TestCase

								if (!(test.testCaseID
										.compareToIgnoreCase("init") == 0)
										&& isLongevityOn) {
									TestCase tempTest = GenerateNewTestCaseID(
											test, count);
									// message("The Testcases to match1b " +
									// tempTest.testCaseID);
									RunTestCase(tempTest);
								} else {
									RunTestCase(test);
								}
							} else {
								Log.Debug("Controller/RunTestCaseForMain : TestCase ID "
										+ test.testCaseID
										+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
								message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
										+ test.testCaseID
										+ ". This is a Manual TestCase.");
								message("********************************************************************************** ");
							}

						} catch (Exception e) {
							if (test.testCaseID.compareToIgnoreCase("init") == 0) {
								initWorkedFine = false;
								Log.Debug("Controller/RunTestCaseForMain : TestCase ID "
										+ test.testCaseID
										+ " is an Initialization and it failed.");
							}
						}
					}

				}
				repeatCount--;
			}

			// At the end Run the Cleanup Step
			for (TestCase test : testcases) {
				if (!debugMode) {
					if (_testPlanStopper) // Sleep for 500 ms, but wake up
					// immediately
					{
						Log.Error("Controller/RunTestCaseForMain : TestCase "
								+ test.testCaseID
								+ " took longer time to execute. Test Plan Time Specified = "
								+ ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
								+ " seconds  is over.");
						break;
					}
				}

				// If the test case have no action or is not a cleanup Step,
				// then Ignore it.
				if (test.actions.size() <= 0
						|| (test.testCaseID.compareToIgnoreCase("cleanup") != 0)) {
					continue;
				}

				try {
					if (test.automated) {
						Log.Debug("Controller/Main : TestCase ID "
								+ test.testCaseID
								+ " is a Automated TestCase. Calling controller.RunTestCase... ");
						// Function to run and Execute the TestCase

						RunTestCase(test);
					} else {
						Log.Debug("Controller/RunTestCaseForMain : TestCase ID "
								+ test.testCaseID
								+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
						message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
								+ test.testCaseID
								+ ". This is a Manual TestCase.");
						message("********************************************************************************** ");
					}

				} catch (Exception e) {
					// Log.Error("Controller/RunTestCaseForMain : Error occured, Excetion message is : "
					// + e.getMessage());
					throw new Exception(
							"Controller/RunTestCaseForMain : Error occured, Excetion message is : "
									+ e.getMessage());
				}
			}
		}

		Log.Debug("Controller/RunTestCaseForMain : End of function");
	}

	/***
	 * Function to Execute a particular testcase.
	 * 
	 * @param test1
	 *            DataClass of the TestCase.
	 */
	private void RunTestCase(TestCase test1) throws Exception,
			DavosExecutionException {
		if (test1.automated == false) {
			Log.Debug("Controller/RunTestCase : TestCase ID "
					+ test1.testCaseID
					+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
			message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
					+ test1.testCaseID + ". This is a Manual TestCase.");
			return;
		}

		Log.Debug("Controller/RunTestCase: Start of function with a new TestCase. TestCase ID is "
				+ test1.testCaseID);

		// Harness Specific ContextVariable to store BaseTestCase ID
		ContextVar.setContextVar("ZUG_BASETCID", test1.testCaseID);
		baseTestCaseID = test1.testCaseID;

		// Added Context Variable basically to get the User Information
		if (test1.userObj != null) {
			// Harness Specific ContextVariable to store UserName
			ContextVar.setContextVar("UserName", test1.userObj.userName);
			// Harness Specific ContextVariable to store UserPassword
			ContextVar
					.setContextVar("UserPassword", test1.userObj.userPassword);
			// Harness Specific ContextVariable to store userDomain
			ContextVar.setContextVar("UserDomain", test1.userObj.domain);

			Log.Debug("Controller/RunTestCase: Set Context Variables for User with Value as UserName = "
					+ test1.userObj.userName
					+ " userPassword = "
					+ test1.userObj.userPassword
					+ " domain = "
					+ test1.userObj.domain);
		} else {
			// Harness Specific ContextVariable to store UserName
			ContextVar.setContextVar("UserName", "");
			// Harness Specific ContextVariable to store UserPassword
			ContextVar.setContextVar("UserPassword", "");
			// Harness Specific ContextVariable to store userDomain
			ContextVar.setContextVar("UserDomain", "");
			Log.Debug("Controller/RunTestCase: UserObj is null, so setting the context Variables to String.Empty.");
		}

		Log.Debug("Controller/RunTestCase: Calling ExpandTestCase With TestCase ID is "
				+ test1.testCaseID);
		// message("RUNTESTCASE "+test1.testCaseID);
		TestCase[] expandedTestCases = ExpandTestCase(test1, true);
		Log.Debug("Controller/RunTestCase: After Expansion for TestCase ID is "
				+ test1.testCaseID + " the number of expanded test case is : "
				+ expandedTestCases.length);

		ArrayList<Thread> ThreadPool = new ArrayList<Thread>();
		for (TestCase test : expandedTestCases) {
			final TestCase test2 = test;
			// message("\n The test case id " + test.testCaseID +
			// " check with command line " +
			// checkWithCommandLine(test).testCaseID);
			// // if
			// (checkWithCommandLine(test).testCaseID.equalsIgnoreCase("Not Present"))
			// {
			// // message("The commandline error " +
			// checkWithCommandLine(test).testCaseID);
			// // continue;
			// // } else
			// // {
			// if (test.testCaseID.equalsIgnoreCase("init") ||
			// test.testCaseID.equalsIgnoreCase("cleanup")) {
			// message("INITI_CLEANUP");
			// test2 = test;
			//
			// } else {
			// message("COMMANDLINE " + test.testCaseID);
			// test2 = checkWithCommandLine(test);
			// }
			// // }
			// RunExpandedTestCase(test);
			// Start a new thread and run the expanded test case on it
			if (test1.concurrentExecutionOnExpansion == true) {
				Thread thread = new Thread(new Runnable() {

					public void run() {

						try {
							try {
								RunExpandedTestCase(test2);
							} catch (DavosExecutionException ex) {
								Log.Error("Controller/RunTestCase: Exception when calling RunExpandedTestCase with exception message as : "
										+ ex.getMessage());
							}
						} catch (Exception ex) {
							Log.Error("Controller/RunTestCase: Exception when calling RunExpandedTestCase with exception message as : "
									+ ex.getMessage());
						}
					}
				});
				thread.start();

				// ////////////TODO : didn't get idea to convert a java thread
				// as a background thread
				// thread.IsBackground = true;
				threadIdForTestCases.put(test.stackTrace, "" + thread.getId());
				ThreadPool.add(thread);
			} else {
				if (StringUtils.isNotBlank(test.threadID)) {
					threadIdForTestCases.put(test.stackTrace, test.threadID);
				} else // Assigning the Parent Test Case Thread.
				{
					threadIdForTestCases
							.put(test.stackTrace, StringUtils.EMPTY);
				}

				RunExpandedTestCase(test);
			}
		}
		// Wait for all the Threads i.e. expanded test cases to finish.
		for (int t = 0; t < ThreadPool.size(); ++t) {
			((Thread) ThreadPool.get(t)).join();
		}

		Log.Debug("Controller/RunTestCase: End of function with TestCase ID is "
				+ test1.testCaseID);
	}

	/***
	 * Function to Run all the Verifications of an Action..
	 * 
	 * @param action
	 *            Action for which one wants to run all the Verifications
	 */
	private void RunVerification(Action action, String threadID)
			throws Exception {
		Log.Debug("Controller/RunVerification : Start of function with Action = "
				+ action.actionName);

		String testCaseID = action.testCaseID;
		UserData user = action.userObj;

		Verification[] verifications = new Verification[action.verification
				.size()];
		action.verification.toArray(verifications);
		Log.Debug(String
				.format("Controller/RunVerification : Number of Verifications for %s Action is %s",
						action.actionName, verifications.length));
		for (int j = 0; j < verifications.length; j++) {
			Verification verification = verifications[j];

			if (StringUtils.isBlank(verification.verificationName)) {
				continue;
			}

			Log.Debug(String.format(
					"Controller/RunVerification : Working on Verification %s",
					verification.verificationName));

			if (verification.verificationName.startsWith("@")) {
				ExecuteVerificationCommand(verification, user, threadID);
			} else if (verification.verificationName.startsWith("&")) {
				// Run an Abstract Test Case
				String abstractTestCaseName = Utility.TrimStartEnd(
						verification.verificationName, '&', 1);
				Log.Debug(String
						.format("Controller/RunVerification: Verifying if the abstract TestCase Exists in the sheet : %s ",
								abstractTestCaseName));

				// Check if the Abstract TestCase ID Exists
				if (abstractTestCase.get(Excel.AppendNamespace(
						abstractTestCaseName, verification.nameSpace)) != null) {
					Log.Debug(String
							.format("Controller/RunVerification: Calling  RunAbstractTestCase for Abstract TestCase ID as : %s & action.parentTestCaseID = %s .",
									abstractTestCaseName,
									action.parentTestCaseID));

					ArrayList<String> tempList = new ArrayList<String>();
					for (int i = 0; i < verification.verificationArguments
							.size(); ++i) {
						Log.Debug("Controller/RunVerification: Working on Verification  Argument : "
								+ i + " for action : " + action.actionName);
						String verificationVal = verification.verificationArguments
								.get(i).toString();
						Log.Debug("Controller/RunVerification: Working on Verification  Argument : "
								+ i
								+ " With  verificationVal : "
								+ verificationVal
								+ " && NormalizeVariable = "
								+ NormalizeVariable(verificationVal, threadID));
						tempList.add(NormalizeVariable(verificationVal,
								threadID));
					}
					boolean isMoleculeVerificationNegative;
					if (verification.isVerifyNegative
							|| verification.isRowNegative) {
						isMoleculeVerificationNegative = true;
					} else {
						isMoleculeVerificationNegative = false;
					}
					// RunAbstractTestCase((TestCase)
					// abstractTestCase.get(Excel.AppendNamespace(abstractTestCaseName,
					// verification.nameSpace)), tempList,
					// action.parentTestCaseID,
					// action.stackTrace,isMoleculeVerificationNegative);
					RunAbstractTestCase((TestCase) abstractTestCase.get(Excel
							.AppendNamespace(abstractTestCaseName,
									verification.nameSpace)), tempList,
							action.parentTestCaseID, action.stackTrace);
					Log.Debug(String
							.format("Controller/RunVerification: Successfully executed  RunAbstractTestCase for Abstract TestCase ID as : %s ",
									abstractTestCaseName));
				} else {
					// Log.Error("Controller/RunVerification : Unrecognized Molecule ["
					// + abstractTestCaseName +
					// "] Verification specified for TestCase ID # " +
					// testCaseID + " for Action " + action.actionName +
					// " which is located at Line " + verification.lineNumber +
					// 1 + " of Sheet " + verification.sheetName + ".");
					throw new Exception(
							"Controller/RunVerification : Unrecognized Molecule ["
									+ abstractTestCaseName
									+ "] Verification specified for TestCase ID # "
									+ testCaseID + " for Action "
									+ action.actionName
									+ " which is located at Line "
									+ verification.lineNumber + 1
									+ " of Sheet " + verification.sheetName
									+ ".");
				}
			} else if (verification.verificationName.trim()
					.compareToIgnoreCase("setcontextvar") == 0) {
				if (verification.verificationArguments.size() >= 1) {
					try {
						String arg = NormalizeVariable(
								(String) verification.verificationArguments
										.get(0),
								threadID);

						message(String
								.format("\n[%s] Verification %s Execution STARTED With Arguments %s",
										verification.stackTrace.toUpperCase(),
										verification.verificationName
												.toUpperCase(), arg));
						CreateContextVariable(arg);
						message(String.format(
								"\n[%s] Verification %s SUCCESSFULLY Executed",
								verification.stackTrace.toUpperCase(),
								verification.verificationName.toUpperCase()));
					} catch (Exception ex) {
						throw new Exception(
								String.format(
										"Exception Happened while executing Verification %s which is located at Line %s of Sheet %s. Exception Message is %s",
										verification.verificationName,
										verification.lineNumber + 1,
										verification.sheetName, ex.getMessage()));
					}
				} else {
					message(String
							.format("\n[%s] Verification %s Executed With NO Arguments ",
									verification.stackTrace.toUpperCase(),
									verification.verificationName.toUpperCase()));
				}

			} else if (verification.verificationName.trim()
					.compareToIgnoreCase("unsetcontextvar") == 0) {
				if (verification.verificationArguments.size() >= 1) {
					try {
						String arg = NormalizeVariable(
								(String) verification.verificationArguments
										.get(0),
								threadID);

						message(String
								.format("\n[%s] Verification %s Execution STARTED With Arguments %s",
										verification.stackTrace.toUpperCase(),
										verification.verificationName
												.toUpperCase(), arg));
						DestroyContextVariable(arg);
						message(String.format(
								"\n[%s] Verification %s SUCCESSFULLY Executed",
								verification.stackTrace.toUpperCase(),
								verification.verificationName.toUpperCase()));
					} catch (Exception ex) {
						throw new Exception(
								String.format(
										"Exception Happened while executing Verification %s which is located at Line %s of Sheet %s. Exception Message is %s",
										verification.verificationName,
										verification.lineNumber + 1,
										verification.sheetName, ex.getMessage()));
					}
				} else {
					message(String
							.format("\n[%s] Verification %s Executed With NO Arguments ",
									verification.stackTrace.toUpperCase(),
									verification.verificationName.toUpperCase()));
				}
			} else if (verification.verificationName.trim().equalsIgnoreCase(
					"print")) {
				if (verification.verificationArguments.size() > 0) {
					for (int i = 0; i < verification.verificationArguments
							.size(); i++) {
						if (!verification.verificationArguments.get(i)
								.isEmpty()) {

							if (verification.verificationArguments.get(i)
									.startsWith("$$%")
									&& verification.verificationArguments
											.get(i).endsWith("%")) {
								// message("The values are "+action.actionArguments.get(i));
								String action_args = StringUtils.removeStart(
										verification.verificationArguments
												.get(i), "$$");
								verification.verificationArguments
										.set(i,
												NormalizeVariable(action_args,
														threadID));
							} else {
								verification.verificationArguments
										.set(i,
												NormalizeVariable(
														verification.verificationArguments
																.get(i),
														threadID));
							}

						}
					}
					message(String.format(
							"[%s] Executed Action %s with values %s ",
							verification.stackTrace.toUpperCase(),
							verification.verificationName,
							verification.verificationArguments));
					RunVerification(action, threadID);
				} else {
					message(String.format(
							"[%s] Executed Action %s with No Values %s ",
							verification.stackTrace.toUpperCase(),
							verification.verificationName,
							verification.verificationArguments));
				}
			} else if (verification.verificationName.trim().equalsIgnoreCase(
					"GetValueAtIndex")) {
				if (verification.verificationArguments.size() == 3) {
					try {
						String arg1 = NormalizeVariable(
								(String) verification.verificationArguments
										.get(0),
								threadID);
						String arg2 = NormalizeVariable(
								(String) verification.verificationArguments
										.get(1),
								threadID);
						String arg3 = NormalizeVariable(
								(String) verification.verificationArguments
										.get(2),
								threadID);
						message(String
								.format("\n[%s] Verification %s Execution STARTED With Arguments %s",
										verification.stackTrace.toUpperCase(),
										verification.verificationName
												.toUpperCase(),
										verification.verificationArguments));

						String args_list[] = arg1.split(",");

						if (args_list.length >= Integer.valueOf(arg2)) {
							// message(args_list[Integer.valueOf(arg2)].replace("{","").replace("}",""));
							CreateContextVariable(arg3
									+ "="
									+ args_list[Integer.valueOf(arg2) - 1]
											.replace("{", "").replace("}", ""));
							message(String
									.format("\n[%s] Verification  %s SUCCESSFULLY Executed",
											verification.stackTrace
													.toUpperCase(),
											verification.verificationName
													.toUpperCase()));
						} else {
							throw new Exception(
									"\n\tIndex is greater than MVM argument length ");
						}

					} catch (Exception ex) {
						throw new Exception(
								String.format(
										"\n\nException Happened while executing Verification %s which is located at Line %s of Sheet %s. Exception Message is %s",
										verification.verificationName,
										verification.lineNumber,
										verification.sheetName, ex.getMessage()));
					}
				} else {
					throw new Exception(
							String.format(
									"\n\t %s Number of argument mismatch. Excpected argument length 3",
									verification.verificationName));
				}

			} else if (verification.verificationName.trim().equalsIgnoreCase(
					"GetCurrentIndex")) {
				if (verification.verificationArguments.size() == 3) {
					try {
						String arg1 = NormalizeVariable(
								(String) verification.verificationArguments
										.get(0),
								threadID);
						String arg2 = NormalizeVariable(
								(String) verification.verificationArguments
										.get(1),
								threadID);
						String arg3 = NormalizeVariable(
								(String) verification.verificationArguments
										.get(2),
								threadID);
						message(String
								.format("\n[%s] Verification %s Execution STARTED With Arguments %s",
										verification.stackTrace.toUpperCase(),
										verification.verificationName
												.toUpperCase(),
										verification.verificationArguments));

						String args_list[] = arg1.split(",");
						boolean element_present = false;
						int count_index = 1;
						for (String arg : args_list) {
							arg = arg.replace("{", "").replace("}", "");
							if (arg.equalsIgnoreCase(arg2)) {
								// message("The count Index "+count_index);
								element_present = true;
								break;
							} else {
								element_present = false;
								count_index++;
							}

						}
						if (!element_present) {
							throw new Exception("\n\t" + arg2
									+ " Element is not present is given list "
									+ arg1);
						}
						// message(args_list[Integer.valueOf(arg2)].replace("{","").replace("}",""));
						CreateContextVariable(arg3 + "="
								+ new Integer(count_index).toString());
						message(String.format(
								"\n[%s] Verification %s SUCCESSFULLY Executed",
								verification.stackTrace.toUpperCase(),
								verification.verificationName.toUpperCase()));
					} catch (Exception ex) {
						throw new Exception(
								String.format(
										"\n\nException Happened while executing Verification %s which is located at Line %s of Sheet %s. Exception Message is %s",
										verification.verificationName,
										verification.lineNumber,
										verification.sheetName, ex.getMessage()));
					}
				} else {
					throw new Exception(
							String.format(
									"\n\t %s Number of argument mismatch. Excpected argument length 3",
									verification.verificationName));
				}

			} else if (verification.verificationName.trim().equalsIgnoreCase(
					"GetValueAt")) {
				if (verification.verificationArguments.size() == 4) {
					try {
						String arg1 = NormalizeVariable(
								(String) verification.verificationArguments
										.get(0),
								threadID);
						String arg2 = NormalizeVariable(
								(String) verification.verificationArguments
										.get(1),
								threadID);
						String arg3 = NormalizeVariable(
								(String) verification.verificationArguments
										.get(2),
								threadID);
						String arg4 = NormalizeVariable(
								(String) verification.verificationArguments
										.get(3),
								threadID);
						message(String
								.format("\n[%s] Verification %s Execution STARTED With Arguments %s",
										verification.stackTrace.toUpperCase(),
										verification.verificationName
												.toUpperCase(),
										verification.verificationArguments));

						String args1_list[] = arg1.split(",");
						String args2_list[] = arg2.split(",");
						if (args1_list.length == args2_list.length) {
							int count_source_index = 0;
							boolean element_found = false;
							for (String arg : args2_list) {
								arg = arg.replace("{", "").replace("}", "");
								if (arg.equalsIgnoreCase(arg3)) {
									element_found = true;
									break;
								} else {
									element_found = false;
									count_source_index++;
								}
							}
							if (!element_found) {
								throw new Exception(
										"\n\t"
												+ arg3
												+ " Element is not present is given Source list "
												+ arg2);
							}
							CreateContextVariable(arg4
									+ "="
									+ args1_list[count_source_index].replace(
											"{", "").replace("}", ""));
							message(String
									.format("\n[%s] Verification %s SUCCESSFULLY Executed",
											verification.stackTrace
													.toUpperCase(),
											verification.verificationName
													.toUpperCase()));
						} else {
							throw new Exception(
									"\n\tTarget list and Source list are not similar.");
						}

					} catch (Exception ex) {
						throw new Exception(
								String.format(
										"\n\nException Happened while executing Action %s which is located at Line %s of Sheet %s. Exception Message is %s",
										verification.verificationName,
										verification.lineNumber,
										verification.sheetName, ex.getMessage()));
					}
				} else {
					throw new Exception(
							String.format(
									"\n\t %s Number of argument mismatch. Excpected argument length 4",
									verification.verificationName));
				}

			} else if (verification.verificationName.trim()
					.compareToIgnoreCase("getattribute") == 0) {
				try {
					Hashtable<String, String> ht = new Hashtable<String, String>();
					for (int i = 0; i < verification.verificationArguments
							.size(); ++i) {
						String opt = NormalizeVariable(
								(String) verification.verificationArguments
										.get(i),
								threadID);
						int idx = opt.indexOf('=');
						if (idx == -1) {
							ht.put(opt, "true");
						} else {
							ht.put(opt.substring(0, idx).toLowerCase(),
									opt.substring(idx + 1));
						}
					}

					String xmlFilePath = StringUtils.isBlank(((String) ht
							.get("xmlfilepath"))) ? StringUtils.EMPTY
							: ((String) ht.get("xmlfilepath"));
					String keyTagName = StringUtils.isBlank(((String) ht
							.get("keytagname"))) ? StringUtils.EMPTY
							: ((String) ht.get("keytagname"));
					String keyIdentity = StringUtils.isBlank(((String) ht
							.get("keyidentity"))) ? StringUtils.EMPTY
							: ((String) ht.get("keyidentity"));
					String keyValue = StringUtils.isBlank(((String) ht
							.get("keyvalue"))) ? StringUtils.EMPTY
							: ((String) ht.get("keyvalue"));
					String attributeName = StringUtils.isBlank(((String) ht
							.get("attributename"))) ? StringUtils.EMPTY
							: ((String) ht.get("attributename"));
					String returnContextVar = StringUtils.isBlank(((String) ht
							.get("returncontextvar"))) ? StringUtils.EMPTY
							: ((String) ht.get("returncontextvar"));

					message(String
							.format("\n[%s] Verification %s Execution STARTED With Arguments xmlfilepath = %s keytagname = %s keyidentity = %s keyvalue = %s attributename = %s returncontextvar = %s",
									verification.stackTrace.toUpperCase(),
									verification.verificationName.toUpperCase(),
									xmlFilePath, keyTagName, keyIdentity,
									keyValue, attributeName, returnContextVar));
					ContextVariable nameValue = XMLPrimitive.GetAttribute(
							xmlFilePath, keyTagName, keyIdentity, keyValue,
							attributeName, returnContextVar);
					ContextVar.setContextVar(nameValue.getName(),
							nameValue.getValue());
					message(String.format(
							"\n[%s] Verification %s SUCCESSFULLY Executed",
							verification.stackTrace.toUpperCase(),
							verification.verificationName.toUpperCase()));
				} catch (Exception ex) {
					throw new Exception(
							String.format(
									"Exception Happened while executing Verification %s which is located at Line %s of Sheet %s. Exception Message is %s",
									verification.verificationName,
									verification.lineNumber + 1,
									verification.sheetName, ex.getMessage()));
				}
			} else if (verification.verificationName.trim()
					.compareToIgnoreCase("gettagvalue") == 0) {
				try {
					Hashtable<String, String> ht = new Hashtable<String, String>();
					for (int i = 0; i < verification.verificationArguments
							.size(); ++i) {
						String opt = NormalizeVariable(
								(String) verification.verificationArguments
										.get(i),
								threadID);
						int idx = opt.indexOf('=');
						if (idx == -1) {
							ht.put(opt, "true");
						} else {
							ht.put(opt.substring(0, idx).toLowerCase(),
									opt.substring(idx + 1));
						}
					}

					String xmlFilePath = StringUtils.isBlank(((String) ht
							.get("xmlfilepath"))) ? StringUtils.EMPTY
							: ((String) ht.get("xmlfilepath"));
					String keyTagName = StringUtils.isBlank(((String) ht
							.get("keytagname"))) ? StringUtils.EMPTY
							: ((String) ht.get("keytagname"));
					String keyIdentity = StringUtils.isBlank(((String) ht
							.get("keyidentity"))) ? StringUtils.EMPTY
							: ((String) ht.get("keyidentity"));
					String keyValue = StringUtils.isBlank(((String) ht
							.get("keyvalue"))) ? StringUtils.EMPTY
							: ((String) ht.get("keyvalue"));
					String tagName = StringUtils.isBlank(((String) ht
							.get("tagname"))) ? StringUtils.EMPTY
							: ((String) ht.get("tagname"));
					String returnContextVar = StringUtils.isBlank(((String) ht
							.get("returncontextvar"))) ? StringUtils.EMPTY
							: ((String) ht.get("returncontextvar"));

					message(String
							.format("\n[%s] Verification %s Execution STARTED With Arguments xmlfilepath = %s keytagname = %s keyidentity = %s keyvalue = %s tagname = %s returncontextvar = %s",
									verification.stackTrace.toUpperCase(),
									verification.verificationName.toUpperCase(),
									xmlFilePath, keyTagName, keyIdentity,
									keyValue, tagName, returnContextVar));
					ContextVariable nameValue = XMLPrimitive.GetTagValue(
							xmlFilePath, keyTagName, keyIdentity, keyValue,
							tagName, returnContextVar);
					ContextVar.setContextVar(nameValue.getName(),
							nameValue.getValue());
					message(String.format(
							"\n[%s] Verification %s SUCCESSFULLY Executed",
							verification.stackTrace.toUpperCase(),
							verification.verificationName.toUpperCase()));
				} catch (Exception ex) {
					throw new Exception(
							String.format(
									"Exception Happened while executing Verification %s which is located at Line %s of Sheet %s. Exception Message is %s",
									verification.verificationName,
									verification.lineNumber + 1,
									verification.sheetName, ex.getMessage()));
				}
			} else if (verification.verificationName.trim()
					.compareToIgnoreCase("getattributeortaglist") == 0) {
				try {
					Hashtable<String, String> ht = new Hashtable<String, String>();
					for (int i = 0; i < verification.verificationArguments
							.size(); ++i) {
						String opt = NormalizeVariable(
								(String) verification.verificationArguments
										.get(i),
								threadID);
						int idx = opt.indexOf('=');
						if (idx == -1) {
							ht.put(opt, "true");
						} else {
							ht.put(opt.substring(0, idx).toLowerCase(),
									opt.substring(idx + 1));
						}
					}

					String xmlFilePath = StringUtils.isBlank(((String) ht
							.get("xmlfilepath"))) ? StringUtils.EMPTY
							: ((String) ht.get("xmlfilepath"));
					String keyTagName = StringUtils.isBlank(((String) ht
							.get("keytagname"))) ? StringUtils.EMPTY
							: ((String) ht.get("keytagname"));
					String keyIdentity = StringUtils.isBlank(((String) ht
							.get("keyidentity"))) ? StringUtils.EMPTY
							: ((String) ht.get("keyidentity"));
					String keyValue = StringUtils.isBlank(((String) ht
							.get("keyvalue"))) ? StringUtils.EMPTY
							: ((String) ht.get("keyvalue"));
					String tagName = StringUtils.isBlank(((String) ht
							.get("tagname"))) ? StringUtils.EMPTY
							: ((String) ht.get("tagname"));
					String attributeNames = StringUtils.isBlank(((String) ht
							.get("attributenames"))) ? StringUtils.EMPTY
							: ((String) ht.get("attributenames"));
					String returnAttrContextVars = StringUtils
							.isBlank(((String) ht.get("returnattrcontextvars"))) ? StringUtils.EMPTY
							: ((String) ht.get("returnattrcontextvars"));
					String returnTagContextVars = StringUtils
							.isBlank(((String) ht.get("returntagcontextvars"))) ? StringUtils.EMPTY
							: ((String) ht.get("returntagcontextvars"));

					message(String
							.format("\n[%s] Verification %s Execution STARTED With Arguments xmlfilepath = %s keytagname = %s keyidentity = %s keyvalue = %s attributenames = %s tagnames = %s returnattrcontextvars = %s returntagcontextvars = %s",
									verification.stackTrace.toUpperCase(),
									verification.verificationName.toUpperCase(),
									xmlFilePath, keyTagName, keyIdentity,
									keyValue, attributeNames, tagName,
									returnAttrContextVars, returnTagContextVars));
					List<ContextVariable> nameValuePair = XMLPrimitive
							.GetAttributeorTagList(xmlFilePath, keyTagName,
									keyIdentity, keyValue, attributeNames,
									tagName, returnAttrContextVars,
									returnTagContextVars);

					for (ContextVariable nameValue : nameValuePair) {
						ContextVar.setContextVar(nameValue.getName(),
								nameValue.getValue());
					}

					message(String.format(
							"\n[%s] Verification %s SUCCESSFULLY Executed",
							verification.stackTrace.toUpperCase(),
							verification.verificationName.toUpperCase()));
				} catch (Exception ex) {
					throw new Exception(
							String.format(
									"Exception Happened while executing Verification %s which is located at Line %s of Sheet %s. Exception Message is %s",
									verification.verificationName,
									verification.lineNumber + 1,
									verification.sheetName, ex.getMessage()));
				}
			} else if (verification.verificationName.trim()
					.compareToIgnoreCase("appendtocontextvar") == 0) {
				if (verification.verificationArguments.size() >= 2) {

					try {
						String contextVarName = StringUtils.EMPTY;
						StringBuilder appendValueBuilder = new StringBuilder();
						if (verification.verificationActualArguments.get(0)
								.toLowerCase().startsWith("contextvar=")) {
							for (int i = 0; i < verification.verificationArguments
									.size(); ++i) {

								String real_value[] = Excel
										.SplitOnFirstEquals(verification.verificationArguments
												.get(i));
								String actual_value[] = Excel
										.SplitOnFirstEquals(verification.verificationActualArguments
												.get(i));
								String valuesToWorkWith = verification.verificationArguments
										.get(i);
								if (!real_value[0]
										.equalsIgnoreCase(actual_value[0])) {
									valuesToWorkWith = actual_value[0] + "="
											+ valuesToWorkWith;
								}
								String opt = NormalizeVariable(
										(String) valuesToWorkWith, threadID);

								int idx = opt.indexOf('=');
								if (idx == -1) {
									continue;
								} else {
									if (opt.substring(0, idx).toLowerCase()
											.compareToIgnoreCase("contextvar") == 0) {
										contextVarName = opt.substring(idx + 1);
									} else {
										appendValueBuilder.append(opt
												.substring(idx + 1));
									}
								}
							}
						} else {
							contextVarName = NormalizeVariable(
									verification.verificationArguments.get(0),
									threadID);
							for (int i = 1; i < verification.verificationArguments
									.size(); ++i) {
								String verf_value = NormalizeVariable(
										verification.verificationArguments
												.get(i),
										threadID);
								// if (verf_value.startsWith("%") &&
								// verf_value.endsWith("%")) {
								// verf_value =
								// ContextVar.getContextVar(verf_value.replaceAll("%",
								// ""));
								// }
								appendValueBuilder.append(verf_value);
							}
						}
						message(String.format(
								"\n[%s]Verification %s Execution STARTED With Arguments contextvar = %s"
										+ " valueToAppend = {%s",
								verification.stackTrace.toUpperCase(),
								verification.verificationName.toUpperCase(),
								contextVarName, appendValueBuilder.toString()));

						String value = ContextVar.getContextVar(contextVarName);
						ContextVar.alterContextVar(contextVarName, value
								+ appendValueBuilder.toString());

						message(String.format(
								"\n[%s] Verification %s SUCCESSFULLY Executed",
								verification.stackTrace.toUpperCase(),
								verification.verificationName.toUpperCase()));
					} catch (Exception ex) {
						throw new Exception(
								String.format(
										"Exception Happened while executing Verification %s which is located at Line %s of Sheet %s."
												+ " Exception is %s",
										verification.verificationName,
										verification.lineNumber + 1,
										verification.sheetName, ex.getMessage()));
					}
				} else {
					message(String
							.format("\n[%s] Verification %s Executed With less than two Arguments ",
									verification.stackTrace.toUpperCase(),
									verification.verificationName.toUpperCase()));
				}
			} else if (verification.verificationName.trim().contains(".")
					&& !verification.verificationName.trim().startsWith("&")
					&& !verification.verificationName.trim().startsWith("@")) {

				try {
					boolean PkgstructureFound = false;
					String package_struct[] = verification.verificationName
							.trim().split("\\.");
					for (int i = 0; i < verification.verificationArguments
							.size(); i++) {
						// if
						// (verification.verificationArguments.get(i).startsWith("%")
						// &&
						// verification.verificationArguments.get(i).endsWith("%"))
						// {
						//
						// String actionargs =
						// verification.verificationArguments.get(i).replaceAll("%",
						// "");
						//
						// String context_value =
						// ContextVar.getContextVar(actionargs);
						// verification.verificationArguments.set(i,
						// context_value);
						//
						//
						// }
						if (!verification.verificationArguments.get(i)
								.isEmpty()) {
							if (verification.verificationArguments.get(i)
									.startsWith("$$%")
									&& verification.verificationArguments
											.get(i).endsWith("%")) {
								String verification_arg = StringUtils
										.removeStart(
												verification.verificationArguments
														.get(i), "$$");
								verification.verificationArguments.set(
										i,
										NormalizeVariable(verification_arg,
												threadID));
							} else {
								verification.verificationArguments
										.set(i,
												NormalizeVariable(
														verification.verificationArguments
																.get(i),
														threadID));
							}
						}

					}
					message(String
							.format("[%s] Execution Started Verification %s with values %s ",
									verification.stackTrace.toUpperCase(),
									verification.verificationName,
									verification.verificationArguments));
					boolean exception_occured = true;
					for (String pkg_name : new ExtensionInterpreterSupport()
							.reteriveXmlTagAttributeValue(
									inprocess_jar_xml_tag_path,
									inprocess_jar_xml_tag_attribute_name)) {
						if (pkg_name.equalsIgnoreCase(package_struct[0])) {
							//if (!builtin_atom_package_name.equalsIgnoreCase(pkg_name)) {
								//builtin_atom_package_name = pkg_name;
								//invokeAtoms.get(builtin_atom_package_name).loadInstance(builtin_atom_package_name);
							//}

							try {
								// message("The Values "+verification.isNegative);
								invokeAtoms
										.get(pkg_name)
										.invokeMethod(
												package_struct[1].trim(),
												verification.verificationArguments);
								// message("what is condition " +
								// verification.isRowNegative);
								exception_occured = true;

							} catch (Exception e) {

								exception_occured = false;
								if (verification.isVerifyNegative
										|| verification.isRowNegative) {
									ContextVar
											.setContextVar(
													"ZUG_VERIFY_EXCEPTION",
													NormalizeVariable(
															String.format(
																	"\n\nException in Verification %s (%s:%s).\n\tMessage: %s",
																	verification.verificationName,
																	verification.sheetName,
																	verification.lineNumber,
																	e.getMessage()),
															threadID));

								} else {
									throw new Exception(
											String.format(
													"\n\nException in Verification %s (%s:%s).\n\tMessage: %s",
													verification.verificationName,
													verification.sheetName,
													verification.lineNumber,
													e.getMessage()), e);
								}
							}
							PkgstructureFound = true;
							break;

						}

					}
					if (PkgstructureFound == false) {
						throw new Exception(
								String.format(
										"\n\nException in Verification %s (%s:%s).\n\t %s Package architecture is not matching with ZugINI.xml definition",
										verification.verificationName,
										verification.sheetName,
										verification.lineNumber,
										package_struct[0]));
					}
					message(String.format(
							"\n[%s] Verification %s SUCCESSFULLY Executed",
							verification.stackTrace.toUpperCase(),
							verification.verificationName.toUpperCase()));
					if (verification.isRowNegative && exception_occured) {

						throw new Exception(
								String.format(
										"\n\nException:: Verify Step passed while Test step: %s (%s:%s) property set to negative.",
										verification.verificationName,
										verification.sheetName,
										verification.lineNumber));
					} else if (verification.isVerifyNegative
							&& exception_occured) {
						throw new Exception(
								String.format(
										"\n\nException:: Verify Step passed while Test step: %s (%s:%s) property set to negative-verify or neg-verify.",
										verification.verificationName,
										verification.sheetName,
										verification.lineNumber));
					}
					Log.Debug(String
							.format("Controller/RunAction :Successfully Action called %s..",
									verification.verificationName));
				} catch (Exception e) {
					// throw new
					// Exception(String.format("\n\nException in Verification %s (%s:%s).\n\t Message: %s",verification.verificationName,
					// verification.lineNumber,verification.sheetName,
					// e.toString()));
					throw e;
				}

			} else {
				if (debugMode == true) {
					StringBuilder arguments = new StringBuilder();
					Log.Debug("Controller/RunVerification : Number of Arguments are : "
							+ verification.verificationArguments.size());
					for (int i = 0; i < verification.verificationArguments
							.size(); ++i) {
						Log.Debug(String
								.format("Controller/RunVerification : Working on verification.verificationArguments[%s] = %s",
										i, verification.verificationArguments
												.get(i).toString()));
						arguments
								.append("\""
										+ NormalizeVariable(
												verification.verificationArguments
														.get(i).toString(),
												threadID) + "\"");
						arguments.append(" ");
					}
					Log.Debug(String
							.format("Controller/RunVerification : Calling ExecuteDefaultCommand with Command as %s. ",
									verification.verificationName));

					if (StringUtils.isBlank(arguments.toString())) {
						message(String
								.format("\n[%s] Default Verification %s Execution STARTED With NO Arguments  ",
										verification.stackTrace.toUpperCase(),
										verification.verificationName
												.toUpperCase()));
					} else {
						message(String
								.format("\n[%s] Default Verification %s Execution STARTED With Arguments %s ",
										verification.stackTrace.toUpperCase(),
										verification.verificationName
												.toUpperCase(), arguments
												.toString()));
					}
					ExecuteDefaultCommand(verification.verificationName,
							verification.parentTestCaseID);
					message(String
							.format("\n[%s] Default Verification %s SUCCESSFULLY Executed",
									verification.stackTrace.toUpperCase(),
									verification.verificationName.toUpperCase()));
				} else {
					// Log.Error("Controller/RunVerification : Unrecognized verification Action :["
					// + verification.verificationName +
					// "] specified for Action " + action.actionName);
					throw new Exception(
							"Controller/RunVerification : Unrecognized verification Action :["
									+ verification.verificationName
									+ "] specified for Action "
									+ action.actionName);
				}
			}
		}
		Log.Debug("Controller/RunVerification : End of function with Action = "
				+ action.actionName);
	}

	/***
	 * Function to execute an Verification of an Action of a Test Case
	 * 
	 * @param verification
	 *            Verification Object to Execute
	 * @param user
	 *            User to perform/execute this verification of action
	 */
	public void ExecuteVerificationCommand(Verification verification,
			UserData user, String threadID) throws Exception {
		try {
			Log.Debug(String
					.format("Controller/ExecuteVerificationCommand : Start of function with command = %s ",
							verification.verificationName));

			Log.Debug(String
					.format("Controller/ExecuteVerificationCommand : Before trimming command = {0} with @ ",
							verification.verificationName));
			String command = Utility.TrimStartEnd(
					verification.verificationName, '@', 1);
			Log.Debug(String
					.format("Controller/ExecuteVerificationCommand : After trimming command = %s with @ ",
							command));

			StringBuilder arguments = new StringBuilder();

			Log.Debug("Controller/ExecuteVerificationCommand : Number of Arguments are : "
					+ verification.verificationArguments.size());
			for (int i = 0; i < verification.verificationArguments.size(); ++i) {
				Log.Debug(String
						.format("Controller/ExecuteVerificationCommand : Working on verification.verificationArguments[%s] = %s",
								i, verification.verificationArguments.get(i)
										.toString()));
				arguments.append("\""
						+ NormalizeVariable(verification.verificationArguments
								.get(i).toString(), threadID) + "\"");
				arguments.append(" ");
			}

			Log.Debug(String
					.format("Controller/ExecuteVerificationCommand : Calling ExecuteCommand with Command as %s ,  arguments =%s and verification.parentTestCaseID = %s ",
							command, arguments.toString(),
							verification.parentTestCaseID));

			if (StringUtils.isEmpty(arguments.toString())) {
				message(String
						.format("\n[%s] Verification %s Execution STARTED With NO Arguments",
								verification.stackTrace.toUpperCase(),
								verification.verificationName.toUpperCase()));
			} else {
				message(String
						.format("\n[%s] Verification %s Execution STARTED With Arguments %s ",
								verification.stackTrace.toUpperCase(),
								verification.verificationName.toUpperCase(),
								arguments.toString()));
			}

			String prototypeName = Excel.AppendNamespace(command,
					verification.nameSpace);
			if (prototypeHashTable.get(prototypeName) != null) {
				// check Verfication is In-Process primitive.
				Prototype prototype = (Prototype) prototypeHashTable
						.get(prototypeName);
				if (StringUtils.isNotBlank(prototype.InProcessDllName)) {
					// Verification is In-Process primitive.
					// ExecuteInProcessPrimitive(prototype.InProcessDllName,
					// command, arguments.toString());
				} else {
					// Verification is out-of-process
					ExecuteCommand(command, arguments.toString(),
							scriptLocation, user,
							verification.parentTestCaseID, StringUtils.EMPTY);
				}
			} else {
				// To support old input sheet where Prototype sheet is not
				// define. So all Verification primitive are
				// out-of process.
				ExecuteCommand(command, arguments.toString(), scriptLocation,
						user, verification.parentTestCaseID, StringUtils.EMPTY);
			}
			message(String.format(
					"\n[%s] Verification %s SUCCESSFULLY Executed",
					verification.stackTrace.toUpperCase(),
					verification.verificationName.toUpperCase()));

			Log.Debug(String
					.format("Controller/ExecuteVerificationCommand : Successfully Executed ExecuteCommand with Command as %s and  arguments =%s ",
							command, arguments.toString()));

			Log.Debug(String
					.format("Controller/ExecuteVerificationCommand : End of function with command = %s ",
							verification.verificationName));
			if (verification.isVerifyNegative) {
				throw new Exception(
						String.format(
								"\n\nException:: Action %s (%s:%s) passed while test step property set to neg-action or negative-action ",
								verification.verificationName,
								verification.sheetName, verification.lineNumber));
			} else if (verification.isRowNegative) {
				throw new Exception(
						String.format(
								"\n\nException:: Action %s (%s:%s) passed while test step property set to negative ",
								verification.verificationName,
								verification.sheetName, verification.lineNumber));
			}

		} catch (Exception ex) {
			if (verification.isVerifyNegative || verification.isRowNegative) {
				ContextVar.setContextVar("ZUG_VERIFY_EXCEPTION", String.format(
						"Exception in Verification %s (%s:%s).\n\tMessage: %s",
						verification.verificationName,
						verification.lineNumber + 1, verification.sheetName,
						ex.getMessage()));
				Log.Error(String
						.format("\tException in Verification %s (%s:%s).\n\tMessage: %s\nNote: Executing Negative Vrify Test Step.",
								verification.verificationName,
								verification.sheetName,
								verification.lineNumber, ex.getMessage()));

			} else {
				throw new Exception(String.format(
						"Exception in Verification %s (%s:%s).\n\tMessage: %s",
						verification.verificationName, verification.sheetName,
						verification.lineNumber, ex.getMessage()));
			}
		}
	}

	/***
	 * Function to run a particular action of a testcase.
	 * 
	 * @param testCaseID
	 *            TestCase ID to which the action belongs.
	 * @param action
	 *            Action to be executed.
	 */
	private void RunAction(Object act) {
		// System.out.println(Utility.dateAsString());
		Action action = (Action) act;
		String testCaseID = action.testCaseID;
		// message("The Action Argument is "+action.actionArguments);
		String[] st;
		try {
			UserData user = action.userObj;
			String threadID = (String) threadIdForTestCases
					.get(action.stackTrace);
			Log.Debug("Controller/RunAction : Start of function with TestCaseID as : "
					+ testCaseID);

			Log.Debug("Controller/RunAction : Name of the Action is : "
					+ action.actionName + " for TestCaseID  : " + testCaseID);

			if (action.actionName.startsWith("@")) {

				Log.Debug("Controller/RunAction : Running Command "
						+ action.actionName + " for TestCase ID as : "
						+ testCaseID);

				Log.Debug(String
						.format("Controller/RunAction : Calling ExecuteActionCommand for action %s....",
								action.actionName));

				ExecuteActionCommand(action, user, threadID);
				Log.Debug(String
						.format("Controller/RunAction : SUCCESSFULLY Executed ExecuteActionCommand for action %s",
								action.actionName));

				Log.Debug(String
						.format("Controller/RunAction : Calling RunVerification for action %s....",
								action.actionName));
				RunVerification(action, threadID);
				Log.Debug(String
						.format("Controller/RunAction : SUCCESSFULLY Executed  RunVerification for action %s....",
								action.actionName));

			} else if (action.actionName.startsWith("&")) {
				// Run an Abstract Test Case
				String abstractTestCaseName = Utility.TrimStartEnd(
						action.actionName, '&', 1);
				Log.Debug(String
						.format("Controller/RunAction: Verifying if the abstract TestCase Exists in the sheet : %s ",
								abstractTestCaseName));

				// Check if the Abstract TestCase ID Exists
				if (abstractTestCase.get(Excel.AppendNamespace(
						abstractTestCaseName, action.nameSpace)) != null) {
					ArrayList<String> tempList = new ArrayList<String>();
					for (int i = 0; i < action.actionArguments.size(); ++i) {
						Log.Debug("Controller/RunAction: Working on Action  Argument : "
								+ i);
						String actionVal = action.actionArguments.get(i)
								.toString();
						Log.Debug("Controller/RunAction: Working on Action  Argument : "
								+ i
								+ " With  actionVal : "
								+ actionVal
								+ " && NormalizeVariable = "
								+ NormalizeVariable(actionVal, threadID));
						tempList.add(NormalizeVariable(actionVal, threadID));
						// if(action.actionArguments.get(i).contains("="))
						// {
						// String
						// molecule_args[]=action.actionArguments.get(i).split("=");
						// ContextVar.setContextVar(abstractTestCaseName+"&"+molecule_args[0],molecule_args[1]);
						//
						// }
					}

					Log.Debug(String
							.format("Controller/RunAction: Calling  RunAbstractTestCase for Abstract TestCase ID as : %s and action.parentTestCaseID = %s .",
									abstractTestCaseName,
									action.parentTestCaseID));
					boolean isMoleculeActionNegative;
					if (action.isActionNegative) {
						isMoleculeActionNegative = true;
					} else if (action.isNegative) {
						isMoleculeActionNegative = true;
					} else {
						isMoleculeActionNegative = false;
					}
					if (isMoleculeActionNegative) {
						Log.Error("----------Negative property not yet supported for Molecules---------");
						throw new Exception(
								"Negative property not yet supported for Molecules for "
										+ action.actionName);

					}
					// RunAbstractTestCase((TestCase)
					// abstractTestCase.get(Excel.AppendNamespace(abstractTestCaseName,action.nameSpace)),
					// tempList, action.parentTestCaseID,
					// action.stackTrace,isMoleculeActionNegative);
					RunAbstractTestCase((TestCase) abstractTestCase.get(Excel
							.AppendNamespace(abstractTestCaseName,
									action.nameSpace)), tempList,
							action.parentTestCaseID, action.stackTrace);
					Log.Debug(String
							.format("Controller/RunAction: Successfully executed  RunAbstractTestCase for Abstract TestCase ID as : %s ",
									abstractTestCaseName));

					Log.Debug(String
							.format("Controller/RunAction : Calling RunVerification for action %s....",
									action.actionName));
					RunVerification(action, threadID);
					Log.Debug(String
							.format("Controller/RunAction : SUCCESSFULLY Executed  RunVerification for action %s....",
									action.actionName));

				} else {
					// Log.Error("Controller/RunAction : Unrecognized Molecule ["
					// + abstractTestCaseName +
					// "] Action specified for TestCase ID # " + testCaseID +
					// " which is located at Line " + action.lineNumber +1 +
					// " of Sheet " + action.sheetName + ".");
					throw new Exception(
							"Controller/RunAction : Unrecognized Molecule ["
									+ abstractTestCaseName
									+ "] Action specified for TestCase ID # "
									+ testCaseID + " which is located at Line "
									+ action.lineNumber + 1 + " of Sheet "
									+ action.sheetName + ".");
				}
			} else if (action.actionName.trim().compareToIgnoreCase(
					"setcontextvar") == 0) {
				if (action.actionArguments.size() >= 1) {
					try {
						String arg = NormalizeVariable(
								(String) action.actionArguments.get(0),
								threadID);

						message(String
								.format("\n[%s] Action %s Execution STARTED With Arguments %s",
										action.stackTrace.toUpperCase(),
										action.actionName.toUpperCase(), arg));
						CreateContextVariable(arg);

						message(String.format(
								"\n[%s] Action %s SUCCESSFULLY Executed",
								action.stackTrace.toUpperCase(),
								action.actionName.toUpperCase()));

						Log.Debug(String
								.format("Controller/RunAction : Calling RunVerification for action %s....",
										action.actionName));
						RunVerification(action, threadID);
						Log.Debug(String
								.format("Controller/RunAction : SUCCESSFULLY Executed  RunVerification for action %s....",
										action.actionName));

					} catch (Exception ex) {
						throw new Exception(
								String.format(
										"\n\nException Happened while executing Action %s which is located at Line %s of Sheet %s. Exception Message is %s",
										action.actionName,
										action.lineNumber + 1,
										action.sheetName, ex.getMessage()));
					}
				} else {
					message(String.format(
							"\n[%s] Action %s Executed With NO Arguments",
							action.stackTrace.toUpperCase(),
							action.actionName.toUpperCase()));
				}

			} else if (action.actionName.trim().compareToIgnoreCase(
					"unsetcontextvar") == 0) {
				if (action.actionArguments.size() >= 1) {
					try {
						String arg = NormalizeVariable(
								(String) action.actionArguments.get(0),
								threadID);
						message(String
								.format("\n[%s] Action %s Execution STARTED With Arguments %s",
										action.stackTrace.toUpperCase(),
										action.actionName.toUpperCase(), arg));
						DestroyContextVariable(arg);
						message(String.format(
								"\n[%s] Action %s SUCCESSFULLY Executed",
								action.stackTrace.toUpperCase(),
								action.actionName.toUpperCase()));

						Log.Debug(String
								.format("Controller/RunAction : Calling RunVerification for action %s....",
										action.actionName));
						RunVerification(action, threadID);
						Log.Debug(String
								.format("Controller/RunAction : SUCCESSFULLY Executed  RunVerification for action %s....",
										action.actionName));

					} catch (Exception ex) {
						throw new Exception(
								String.format(
										"\n\nException Happened while executing Action %s which is located at Line %s of Sheet %s. Exception Message is %s",
										action.actionName,
										action.lineNumber + 1,
										action.sheetName, ex.getMessage()));
					}
				} else {
					message(String.format(
							"\n[%s] Action %s Executed With NO Arguments",
							action.stackTrace.toUpperCase(),
							action.actionName.toUpperCase()));
				}
			} else if (action.actionName.trim().equalsIgnoreCase(
					"GetValueAtIndex")) {
				if (action.actionArguments.size() == 3) {
					try {
						String arg1 = NormalizeVariable(
								(String) action.actionArguments.get(0),
								threadID);
						String arg2 = NormalizeVariable(
								(String) action.actionArguments.get(1),
								threadID);
						String arg3 = NormalizeVariable(
								(String) action.actionArguments.get(2),
								threadID);
						message(String
								.format("\n[%s] Action %s Execution STARTED With Arguments %s",
										action.stackTrace.toUpperCase(),
										action.actionName.toUpperCase(),
										action.actionArguments));

						String args_list[] = arg1.split(",");

						if (args_list.length >= Integer.valueOf(arg2)) {
							// message(args_list[Integer.valueOf(arg2)].replace("{","").replace("}",""));
							CreateContextVariable(arg3
									+ "="
									+ args_list[Integer.valueOf(arg2) - 1]
											.replace("{", "").replace("}", ""));
							message(String.format(
									"\n[%s] Action %s SUCCESSFULLY Executed",
									action.stackTrace.toUpperCase(),
									action.actionName.toUpperCase()));
						} else {
							throw new Exception(
									"\n\tIndex is greater than MVM argument length ");
						}
						RunVerification(action, threadID);
					} catch (Exception ex) {
						throw new Exception(
								String.format(
										"\n\nException Happened while executing Action %s which is located at Line %s of Sheet %s. Exception Message is %s",
										action.actionName,
										action.lineNumber + 1,
										action.sheetName, ex.getMessage()));
					}
				} else {
					throw new Exception(
							String.format(
									"\n\t %s Number of argument mismatch. Excpected argument length 3",
									action.actionName));
				}

			} else if (action.actionName.trim().equalsIgnoreCase(
					"GetCurrentIndex")) {
				if (action.actionArguments.size() == 3) {
					try {
						String arg1 = NormalizeVariable(
								(String) action.actionArguments.get(0),
								threadID);
						String arg2 = NormalizeVariable(
								(String) action.actionArguments.get(1),
								threadID);
						String arg3 = NormalizeVariable(
								(String) action.actionArguments.get(2),
								threadID);
						message(String
								.format("\n[%s] Action %s Execution STARTED With Arguments %s",
										action.stackTrace.toUpperCase(),
										action.actionName.toUpperCase(),
										action.actionArguments));

						String args_list[] = arg1.split(",");
						boolean element_present = false;
						int count_index = 1;
						for (String arg : args_list) {
							arg = arg.replace("{", "").replace("}", "");
							if (arg.equalsIgnoreCase(arg2)) {
								// message("The count Index "+count_index);
								element_present = true;
								break;
							} else {
								element_present = false;
								count_index++;
							}

						}
						if (!element_present) {
							throw new Exception("\n\t" + arg2
									+ " Element is not present is given list "
									+ arg1);
						}
						// message(args_list[Integer.valueOf(arg2)].replace("{","").replace("}",""));
						CreateContextVariable(arg3 + "="
								+ new Integer(count_index).toString());
						message(String.format(
								"\n[%s] Action %s SUCCESSFULLY Executed",
								action.stackTrace.toUpperCase(),
								action.actionName.toUpperCase()));
						RunVerification(action, threadID);
					} catch (Exception ex) {
						throw new Exception(
								String.format(
										"\n\nException Happened while executing Action %s which is located at Line %s of Sheet %s. Exception Message is %s",
										action.actionName,
										action.lineNumber + 1,
										action.sheetName, ex.getMessage()));
					}
				} else {
					throw new Exception(
							String.format(
									"\n\t %s Number of argument mismatch. Excpected argument length 3",
									action.actionName));
				}

			} else if (action.actionName.trim().equalsIgnoreCase("GetValueAt")) {
				if (action.actionArguments.size() == 4) {
					try {
						String arg1 = NormalizeVariable(
								(String) action.actionArguments.get(0),
								threadID);
						String arg2 = NormalizeVariable(
								(String) action.actionArguments.get(1),
								threadID);
						String arg3 = NormalizeVariable(
								(String) action.actionArguments.get(2),
								threadID);
						String arg4 = NormalizeVariable(
								(String) action.actionArguments.get(3),
								threadID);
						message(String
								.format("\n[%s] Action %s Execution STARTED With Arguments %s",
										action.stackTrace.toUpperCase(),
										action.actionName.toUpperCase(),
										action.actionArguments));

						String args1_list[] = arg1.split(",");
						String args2_list[] = arg2.split(",");
						if (args1_list.length == args2_list.length) {
							int count_source_index = 0;
							boolean element_found = false;
							for (String arg : args2_list) {
								arg = arg.replace("{", "").replace("}", "");
								if (arg.equalsIgnoreCase(arg3)) {
									element_found = true;
									break;
								} else {
									element_found = false;
									count_source_index++;
								}
							}
							if (!element_found) {
								throw new Exception(
										"\n\t"
												+ arg3
												+ " Element is not present is given Source list "
												+ arg2);
							}
							CreateContextVariable(arg4
									+ "="
									+ args1_list[count_source_index].replace(
											"{", "").replace("}", ""));
							message(String.format(
									"\n[%s] Action %s SUCCESSFULLY Executed",
									action.stackTrace.toUpperCase(),
									action.actionName.toUpperCase()));
							RunVerification(action, threadID);
						} else {
							throw new Exception(
									"\n\tTarget list and Source list are not similar.");
						}

					} catch (Exception ex) {
						throw new Exception(
								String.format(
										"\n\nException Happened while executing Action %s which is located at Line %s of Sheet %s. Exception Message is %s",
										action.actionName,
										action.lineNumber + 1,
										action.sheetName, ex.getMessage()));
					}
				} else {
					throw new Exception(
							String.format(
									"\n\t %s Number of argument mismatch. Excpected argument length 4",
									action.actionName));
				}

			} else if (action.actionName.trim().equalsIgnoreCase("wait")) {
				int arg_size = action.actionArguments.size();

				if (arg_size == 1) {
					message(String.format(
							"\n[%s] Action %s Execution STARTED With Arguments "
									+ "%s", action.stackTrace.toUpperCase(),
							action.actionName.toUpperCase(),
							action.actionArguments));
					try {
						long timetowait = Long.valueOf(action.actionArguments
								.get(0)) * 1000;
						Thread.sleep(timetowait);
						// message("Sleeping done");
						// action.wait(timetowait);
						message(String.format(
								"\n[%s] Action %s SUCCESSFULLY Executed",
								action.stackTrace.toUpperCase(),
								action.actionName.toUpperCase()));
					} catch (Exception e) {
						throw new Exception(
								String.format(
										"\n\nException Happened while executing Action %s which is located "
												+ "at Line %s of Sheet %s. Exception is %s",
										action.actionName, action.lineNumber,
										action.sheetName, e.toString()));
					}
				} else {
					throw new Exception("Illegal number of arguments(Size): "
							+ arg_size + "\n Use only 1 argument");
				}

			} else if (action.actionName.trim().compareToIgnoreCase(
					"appendtocontextvar") == 0) {
				// message("appendtocontextvar Argument list " +
				// action.actionArguments + " \n actual " +
				// action.actionActualArguments);

				if (action.actionArguments.size() >= 2) {
					try {

						// TODO NIIISH Check context var exists or not
						// message("Appened to contervar value "+action.actionArguments);
						String contextVarName = StringUtils.EMPTY;
						StringBuilder appendValueBuilder = new StringBuilder();
						if (action.actionActualArguments.get(0).toLowerCase()
								.startsWith("contextvar=")) {
							for (int i = 0; i < action.actionArguments.size(); ++i) {
								String real_value[] = Excel
										.SplitOnFirstEquals(action.actionArguments
												.get(i));
								String actua_value[] = Excel
										.SplitOnFirstEquals(action.actionActualArguments
												.get(i));
								String valueToWorkWith = action.actionArguments
										.get(i);
								if (!real_value[0]
										.equalsIgnoreCase(actua_value[0])) {
									valueToWorkWith = actua_value[0] + "="
											+ valueToWorkWith;
								}
								// message("The value edited "+valueToWorkWith);
								String opt = NormalizeVariable(
										(String) valueToWorkWith, threadID);
								// if (opt.startsWith("%") && opt.endsWith("%"))
								// {
								// opt =
								// ContextVar.getContextVar(opt.replaceAll("%",
								// ""));
								// }
								int idx = opt.indexOf('=');
								// message("the index is "+idx);
								if (idx == -1) {
									continue;
								} else {
									// message("the opt string "+opt.substring(0,idx));
									if (opt.substring(0, idx).toLowerCase()
											.compareToIgnoreCase("contextvar") == 0) {
										contextVarName = opt.substring(idx + 1);
									} else {
										appendValueBuilder.append(opt
												.substring(idx + 1));

									}
								}
							}
						} else {
							contextVarName = NormalizeVariable(
									action.actionArguments.get(0), threadID);
							for (int i = 1; i < action.actionArguments.size(); ++i) {
								String arg_value = NormalizeVariable(
										action.actionArguments.get(i), threadID);
								// if (arg_value.startsWith("%") &&
								// arg_value.endsWith("%")) {
								// arg_value =
								// ContextVar.getContextVar(arg_value.replaceAll("%",
								// ""));
								// }
								appendValueBuilder.append(arg_value);
							}
						}
						message(String.format(
								"\n[%s] Action %s Execution STARTED With Arguments "
										+ "contextvar = %s valueToAppend = %s",
								action.stackTrace.toUpperCase(),
								action.actionName.toUpperCase(),
								contextVarName, appendValueBuilder.toString()));
						// message("The contextvar "+contextVarName+" value "+ReadContextVariable(contextVarName));
						String value = ContextVar.getContextVar(contextVarName
								.trim());
						if (value == null) {
							Log.Error("AppendtoContextVar: ContextVariable is not defined");
							throw new Exception("Context Variable not defined");
						}
						ContextVar.alterContextVar(contextVarName, value
								+ appendValueBuilder.toString());

						message(String.format(
								"\n[%s] Action %s SUCCESSFULLY Executed",
								action.stackTrace.toUpperCase(),
								action.actionName.toUpperCase()));

						Log.Debug(String
								.format("Controller/RunAction : Calling RunVerification for action %s....",
										action.actionName));
						RunVerification(action, threadID);
						Log.Debug(String
								.format("Controller/RunAction : SUCCESSFULLY Executed  RunVerification for action %s....",
										action.actionName));
					} catch (Exception e) {
						throw new Exception(
								String.format(
										"\n\nException Happened while executing Action %s which is located "
												+ "at Line %s of Sheet %s. Exception is %s",
										action.actionName, action.lineNumber,
										action.sheetName, e.toString()));
					}
				} else {
					message(String
							.format("\n[%s] Action %s Executed With less than two Arguments",
									action.stackTrace.toUpperCase(),
									action.actionName.toUpperCase()));
				}
			} else if (action.actionName.trim().toLowerCase().contains("print")) {
				if (action.actionArguments.size() > 0) {
					// message("THe Args "+action.actionArguments);
					for (int i = 0; i < action.actionArguments.size(); i++) {

						if (!action.actionArguments.get(i).isEmpty()) {

							if (action.actionArguments.get(i).startsWith("$$%")
									&& action.actionArguments.get(i).endsWith(
											"%")) {
								// message("The values are "+action.actionArguments.get(i));
								String action_args = StringUtils.removeStart(
										action.actionArguments.get(i), "$$");
								action.actionArguments
										.set(i,
												NormalizeVariable(action_args,
														threadID));
							} else {
								action.actionArguments.set(
										i,
										NormalizeVariable(
												action.actionArguments.get(i),
												threadID));
							}

						}
					}
					message(String.format(
							"[%s] Executed Action %s with values %s ",
							action.stackTrace.toUpperCase(), action.actionName,
							action.actionArguments));
					RunVerification(action, threadID);
				} else {
					message(String.format(
							"[%s] Executed Action %s with No Values %s ",
							action.stackTrace.toUpperCase(), action.actionName,
							action.actionArguments));
				}
			} else if (action.actionName.trim().startsWith("#define")) {
			} else if (action.actionName.trim().contains("\\.")
					|| !action.actionName.startsWith("&")) {
				boolean isActionPassing = false;
				try {
					boolean PkgstructureFound = false;
					String package_struct[] = action.actionName.trim().split(
							"\\.");
					for (int i = 0; i < action.actionArguments.size(); i++) {
						// if (action.actionArguments.get(i).startsWith("%") &&
						// action.actionArguments.get(i).endsWith("%")) {
						//
						// String actionargs =
						// action.actionArguments.get(i).replaceAll("%", "");
						//
						// String context_value =
						// ContextVar.getContextVar(actionargs);
						// action.actionArguments.set(i, context_value);
						//
						//
						// }
						if (!action.actionArguments.get(i).isEmpty()) {

							if (action.actionArguments.get(i).startsWith("$$%")
									&& action.actionArguments.get(i).endsWith(
											"%")) {
								// message("The values are "+action.actionArguments.get(i));
								String action_args = StringUtils.removeStart(
										action.actionArguments.get(i), "$$");
								action.actionArguments
										.set(i,
												NormalizeVariable(action_args,
														threadID));
							} else {
								action.actionArguments.set(
										i,
										NormalizeVariable(
												action.actionArguments.get(i),
												threadID));
							}

						}

					}
					
					//TODO need to make the builtin_atom_package_name as threadsafe....
					message(String.format(
							"[%s] Execution Started Action %s with values %s ",
							action.stackTrace.toUpperCase(), action.actionName,
							action.actionArguments));
					for (String pkg_name : new ExtensionInterpreterSupport()
							.reteriveXmlTagAttributeValue(
									inprocess_jar_xml_tag_path,
									inprocess_jar_xml_tag_attribute_name)) {
						if (pkg_name.equalsIgnoreCase(package_struct[0])) {
//							if (!builtin_atom_package_name
//									.equalsIgnoreCase(pkg_name)) {
//								builtin_atom_package_name = pkg_name;
//								if (invokeAtoms.get(pkg_name).native_flag) {
//									// message("This is native Dll Calling");
//								} else if (invokeAtoms.get(pkg_name).com_flag) {
//									// message("Com Jacob Calling");
//								} else {
//									invokeAtoms.get(builtin_atom_package_name)
//											.loadInstance(
//													builtin_atom_package_name);
//								}
//							}
							//Need Work on this
							invokeAtoms.get(pkg_name)
									.setInprocessAction(action);
							// try {
							
							isActionPassing = true;
							//try{
							invokeAtoms.get(pkg_name)
									.invokeMethod(package_struct[1].trim(),
											action.actionArguments);
							
							if (action.isNegative) {
								isActionPassing = false;
								throw new Exception(
										String.format(
												"\n\nException: The test step %s (%s:%s) passed while property is set to negative.",
												action.actionName,
												action.sheetName,
												action.lineNumber));

							}
							if (action.isActionNegative) {
								isActionPassing = false;
								throw new Exception(
										String.format(
												"\n\nException: The test step %s (%s:%s) passed while property is set to negative-action or neg-action.",
												action.actionName,
												action.sheetName,
												action.lineNumber));
							}
							// } catch (Exception e) {
							// message(action.isActionNegative+" v "+action.isNegative);
							// if (action.isActionNegative) {
							// //message("The Exception is action neg " +
							// e.toString());
							// ContextVar.setContextVar("ZUG_ACTION_EXCEPTION",
							// NormalizeVariable(String.format("\n\nException in Action %s (%s:%s).\n\t Message: %s",
							// action.actionName, action.sheetName,
							// action.lineNumber, e.getMessage()), threadID));
							//
							// RunVerification(action, threadID);
							// } else if (action.isNegative) {
							// //message("Total Row negative ");
							// ContextVar.setContextVar("ZUG_EXCEPTION",
							// NormalizeVariable(String.format("\n\nException in Action %s (%s:%s).\n\t Message: %s",
							// action.actionName, action.sheetName,
							// action.lineNumber, e.getMessage()), threadID));
							// RunVerification(action, threadID);
							// } else {
							// throw new Exception(e.getMessage());
							// }
							// }

							PkgstructureFound = true;
							break;

						}

					}
					if (PkgstructureFound == false) {
						throw new Exception(
								String.format(
										"\n\nException in Action %s (%s:%s).\n %s Package architecture is not matching with ZugINI.xml definition ",
										action.actionName, action.lineNumber,
										action.sheetName, package_struct[0]));
					}
					message(String.format(
							"\n[%s] Action %s SUCCESSFULLY Executed",
							action.stackTrace.toUpperCase(),
							action.actionName.toUpperCase()));

					RunVerification(action, threadID);
					Log.Debug(String
							.format("Controller/RunAction :Successfully called action %s ",
									action.actionName));
				} catch (Exception e) {
					if (action.isActionNegative && isActionPassing) {
						// message("The Exception is action neg " +
						// e.toString());
						ContextVar
								.setContextVar(
										"ZUG_ACTION_EXCEPTION",
										NormalizeVariable(
												String.format(
														"\n\nException in Action %s (%s:%s).\n\t Message: %s",
														action.actionName,
														action.sheetName,
														action.lineNumber,
														e.getMessage()),
												threadID));

						RunVerification(action, threadID);
					} else if (action.isNegative && isActionPassing) {
						// message("Total Row negative ");
						ContextVar
								.setContextVar(
										"ZUG_EXCEPTION",
										NormalizeVariable(
												String.format(
														"\n\nException in Action %s (%s:%s).\n\t Message: %s",
														action.actionName,
														action.sheetName,
														action.lineNumber,
														e.getMessage()),
												threadID));
						RunVerification(action, threadID);
					} else {

						throw new Exception(
								String.format(
										"\n\nException in Action %s (%s:%s).\n\t Message: %s",
										action.actionName, action.sheetName,
										action.lineNumber, e.getMessage()));
					}

				}

			} else {

				if (debugMode == true) {
					StringBuilder arguments = new StringBuilder();
					Log.Debug("Controller/RunAction : Number of Arguments are : "
							+ action.actionArguments.size());
					for (int i = 0; i < action.actionArguments.size(); ++i) {
						Log.Debug(String
								.format("Controller/RunAction : Working on action.actionArguments[%s] = %s",
										i, action.actionArguments.get(i)
												.toString()));
						arguments.append("\""
								+ NormalizeVariable(
										action.actionArguments.get(i)
												.toString(), threadID) + "\"");
						arguments.append(" ");
					}
					Log.Debug(String
							.format("Controller/RunAction : Calling ExecuteDefaultCommand with Command as %s. ",
									action.actionName));

					if (arguments.toString() == StringUtils.EMPTY) {
						message(String
								.format("\n[%s] Default Action %s Execution STARTED With NO Arguments  ",
										action.stackTrace.toUpperCase(),
										action.actionName.toUpperCase()));
					} else {
						message(String
								.format("\n[%s] Default Action %s Execution STARTED With Arguments %s ",
										action.stackTrace.toUpperCase(),
										action.actionName.toUpperCase(),
										arguments.toString()));
					}

					ExecuteDefaultCommand(action.actionName,
							action.parentTestCaseID);
					message(String.format(
							"\n[%s] Default Action %s SUCCESSFULLY Executed",
							action.parentTestCaseID.toUpperCase(),
							action.actionName.toUpperCase()));

					Log.Debug(String
							.format("Controller/RunAction : Calling RunVerification for action %s....",
									action.actionName));
					RunVerification(action, threadID);
					Log.Debug(String
							.format("Controller/RunAction : SUCCESSFULLY Executed  RunVerification for action %s....",
									action.actionName));

				} else {
					// Log.Error("Controller/RunAction : Unrecognized action:["
					// + action.actionName + "] for TestCase ID # " +
					// testCaseID);
					throw new Exception(
							"Controller/RunAction : Unrecognized action:["
									+ action.actionName
									+ "] for TestCase ID # " + testCaseID);
				}
			}

			Log.Debug("Controller/RunAction : End of function with TestCaseID as : "
					+ testCaseID);
		} catch (Exception ex) {

			if (StringUtils.isBlank(errorMessageDuringTestCaseExecution
					.get(action.parentTestCaseID))) {
				errorMessageDuringTestCaseExecution.put(
						action.parentTestCaseID,
						((String) errorMessageDuringTestCaseExecution
								.get(action.parentTestCaseID))
								+ "\n\t"
								+ ex.getMessage());
			}

			if (StringUtils.isBlank(errorMessageDuringMoleculeCaseExecution
					.get(action.stackTrace))) {
				errorMessageDuringMoleculeCaseExecution.put(
						action.stackTrace,
						((String) errorMessageDuringMoleculeCaseExecution
								.get(action.stackTrace))
								+ "\n\t"
								+ ex.getMessage());
			}

			// throw new Exception(ex.getMessage());
		}

	}

	/***
	 * Function to execute an Action of a Test Case
	 * 
	 * @param action
	 *            Action Object to Execute
	 * @param user
	 *            User to perform/execute this action.
	 */
	public void ExecuteActionCommand(Action action, UserData user,
			String threadID) throws Exception {
		try {
			Log.Debug(String
					.format("Controller/ExecuteActionCommand : Start of function with command = %s ",
							action.actionName));

			Log.Debug(String
					.format("Controller/ExecuteActionCommand : Before trimming command = %s with @ ",
							action.actionName));
			String command = Utility.TrimStartEnd(action.actionName, '@', 1);
			Log.Debug(String
					.format("Controller/ExecuteActionCommand : After trimming command = %s with @ ",
							command));

			StringBuilder arguments = new StringBuilder();

			Log.Debug("Controller/ExecuteActionCommand : Number of Arguments are : "
					+ action.actionArguments.size());
			for (int i = 0; i < action.actionArguments.size(); ++i) {
				Log.Debug(String
						.format("Controller/ExecuteActionCommand : Working on action.actionArguments[%d] = %s",
								i, action.actionArguments.get(i).toString()));
				arguments.append("\""
						+ NormalizeVariable(action.actionArguments.get(i)
								.toString(), threadID) + "\"");
				arguments.append(" ");
			}
			Log.Debug(String
					.format("Controller/ExecuteActionCommand : Calling ExecuteCommand with Command as %s ,  arguments =%s and action.parentTestCaseID = %s",
							command, arguments.toString().trim(),
							action.parentTestCaseID));

			if (StringUtils.isBlank(arguments.toString())) {
				message(String
						.format("\n[%s] Action %s Execution STARTED With NO Arguments  ",
								action.stackTrace.toUpperCase(),
								action.actionName.toUpperCase()));
			} else {
				message(String
						.format("\n[%s] Action %s Execution STARTED With Arguments %s ",
								action.stackTrace.toUpperCase(),
								action.actionName.toUpperCase(),
								arguments.toString()));
			}

			String protoTypeName = Excel.AppendNamespace(command,
					action.nameSpace);

			if (prototypeHashTable.get(protoTypeName) != null) {
				// check Action is In-Process primitive.
				Prototype prototype = (Prototype) prototypeHashTable
						.get(protoTypeName);

				if (StringUtils.isNotBlank(prototype.InProcessDllName)) {
					// Action is In-Process primitive.
					// ExecuteInProcessPrimitive(prototype.InProcessDllName,
					// command, arguments.toString());
				} else {
					// Action is out-of-process.
					ExecuteCommand(command, arguments.toString(),
							scriptLocation, user, action.parentTestCaseID,
							action.step);

				}
			} else {
				// To support old input sheet where Prototype sheet is not
				// define. So all primitive are out-of process.
				ExecuteCommand(command, arguments.toString(), scriptLocation,
						user, action.parentTestCaseID, action.step);
			}
			message(String.format("\n[%s] Action %s SUCCESSFULLY Executed",
					action.stackTrace.toUpperCase(),
					action.actionName.toUpperCase()));
			Log.Debug(String
					.format("Controller/ExecuteActionCommand : Successfully Executed ExecuteCommand with Command as %s and  arguments =%s ",
							command, arguments.toString()));

			Log.Debug(String
					.format("Controller/ExecuteActionCommand : End of function with command = %s ",
							action.actionName));
			if (action.isActionNegative) {
				throw new Exception(
						String.format(
								"\n\nException:: Action %s (%s:%s) passed while test step property set to neg-action or negative-action ",
								action.actionName, action.sheetName,
								action.lineNumber + 1));
			} else if (action.isNegative) {
				throw new Exception(
						String.format(
								"\n\nException:: Action %s (%s:%s) passed while test step property set to negative ",
								action.actionName, action.sheetName,
								action.lineNumber + 1));
			}

		} catch (Exception ex) {
			// message("The values of property "+action.isActionNegative+" The value of Neg "+action.isNegative);
			if (action.isNegative) {
				ContextVar.setContextVar("ZUG_EXCEPTION", String.format(
						"\tException in Action %s (%s:%s).\n\t%s",
						action.actionName, action.sheetName,
						action.lineNumber + 1, ex.getMessage()));
				if (verbose)
					Log.Error(String.format(
							"\tException in Action %s (%s:%s).\n\tMessage: %s",
							action.actionName, action.sheetName,
							action.lineNumber + 1, ex.getMessage()));
				// message("Comming to if Clause ");
			}
			if (action.isActionNegative) {
				ContextVar.setContextVar("ZUG_ACTION_EXCEPTION", String.format(
						"\tException in Action %s (%s:%s).\n\t%s",
						action.actionName, action.sheetName,
						action.lineNumber + 1, ex.getMessage()));
				if (verbose)
					Log.Error(String.format(
							"\tException in Action %s (%s:%s).\n\tMessage: %s",
							action.actionName, action.sheetName,
							action.lineNumber + 1, ex.getMessage()));
			} else {
				// message("Comming to else Clause ");

				throw new Exception(String.format(
						"\tException in Action %s (%s:%s).\n\tMessage: %s",
						action.actionName, action.sheetName,
						action.lineNumber + 1, ex.getMessage()));
			}
			// throw new
			// Exception(String.format("\tException in Action %s (%s:%s).\n\t%s",action.actionName,
			// action.sheetName, action.lineNumber + 1,ex.getMessage()));
		}
	}

	/***
	 * Function to Delete a Context () Variable.
	 * 
	 * @param variableName
	 *            Name of the VAriable to remove from the List of Environment
	 *            Variable.
	 */
	private void DestroyContextVariable(String variableName) throws Exception {
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

	/***
	 * Function to execute the Default Action/Verification
	 * 
	 * @param command
	 *            Name of the Default Action to run manually/debug mode.
	 */
	private void ExecuteDefaultCommand(String command, String parentTestCaseID)
			throws Exception {
		Log.Debug(String
				.format("Controller/ExecuteDefaultCommand : Start of function with Action = %s and parentTestCaseID = %s ",
						command, parentTestCaseID));
		File pathOfDefaultexe;
		if (OS_FLAG) {
			pathOfDefaultexe = new File("./DefaultAction.exe");
		} else {
			pathOfDefaultexe = new File("./DefaultAction");
		}
		ProcessBuilder pr = new ProcessBuilder();
		pr.directory(pathOfDefaultexe.getParentFile());

		ArrayList<String> CommandParam = new ArrayList<String>();

		Process process = null;
		try {
			CommandParam.add("ExeWrapper.exe");
			CommandParam.add(command);
			CommandParam.add(parentTestCaseID);

			pr.command(CommandParam);
			process = pr.start();

			Log.Debug(String
					.format("Controller/ExecuteDefaultCommand  :Arguments for the Command DefaultAction.exe is %s. ",
							command));

			Log.Debug(String
					.format("Controller/ExecuteDefaultCommand :Started the Process for the Command DefaultAction.exe with Arguments as %s . ",
							command));

			Log.Debug(String
					.format("Controller/ExecuteDefaultCommand  :Waiting for the Process to Exit for the Command DefaultAction.exe with Arguments as %s . ",
							command));

			// System.out.println("Waiting for process Finish");
			process.waitFor();

			// System.out.println(process.exitValue());
			Log.Debug(String
					.format("Controller/ExecuteDefaultCommand :Process Exited Gracefully for the Command DefaultAction.exe with Arguments as %s . ",
							command));

			if (!(process.exitValue() == 0)) {
				// Log.Error(String.format("Controller/ExecuteDefaultCommand : Exit Status for DefaultAction.exe Command is %s",
				// process.exitValue()));
				throw new Exception(
						String.format(
								"Controller/ExecuteDefaultCommand : Exit Status for DefaultAction.exe Command is %s ",
								process.exitValue()));
			}
		} catch (Exception ex) {
			String error = String
					.format("Controller/ExecuteDefaultCommand : Exception raised while running Command DefaultAction.exe. Exception Message is : %s ",
							ex.getMessage() + ex.getCause()
									+ ex.getStackTrace() + ex.toString());
			// Log.Error(error);
			throw new Exception(error);
		} finally {
			// Close the process created.
			if (process != null) {
				try {
					// No exception means that the process has exited -
					// otherwise kill it
					process.exitValue();
				} catch (Exception e) {
					Log.Debug("Controller/ExecuteDefaultCommand  : Closing and Disposing the Process.");
					process.destroy();
					Log.Debug("Controller/ExecuteDefaultCommand : Successfully Closed and Disposed the Process.");
				}
			}
		}
		Log.Debug(String
				.format("Controller/ExecuteDefaultCommand : End of function with command = DefaultAction.exe, Arguments = %s and parentTestCaseID = %s ",
						command, parentTestCaseID));

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
			/*
			 * try { // final BufferedWriter bw = new BufferedWriter( new
			 * OutputStreamWriter( os ), 50 /* keep small for tests
			 */// );
				// System.out.
			/*
			 * for ( int i = 99; i >= 0; i-- ) { bw.write( "There are " + i +
			 * " bottles of beer on the wall, " + i + " bottles of beer." );
			 * bw.write( lineSeparator ); }
			 */
			/*
			 * bw.close(); } catch ( IOException e ) { throw new
			 * IllegalArgumentException(
			 * "IOException sending data to child process." ); }
			 */
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
	 * Function to find the directory which contains the command file
	 * 
	 * @param command Command to look for
	 * 
	 * @param workingDirectoryList Working Directory List which has to searched
	 * in written by Nitish on 01-Apr-2011
	 */
	private String FindWorkingDirectory(String command,
			String workingDirectoryList) {
		Log.Debug("Controller/FindWorkingDirectory: Function started with arguments command = "
				+ command + " workingDirectoryList = " + workingDirectoryList);
		if (StringUtils.isBlank(workingDirectoryList)) {
			return workingDirectoryList;
		}
		String[] workingDirectoryArray = workingDirectoryList.split(";");
		for (String workingDirectory : workingDirectoryArray) {

			if (StringUtils.isBlank(workingDirectory)) {

				File f = new File(command);
				if (f.exists()) {
					Log.Debug(String
							.format("Controller/FindWorkingDirectory: Found with argument as %s when Working Directory value is %s",
									command, workingDirectory));
					return workingDirectory;
				}
			} else {
				/*
				 * StringBuffer sb = new StringBuffer( workingDirectory );
				 * String myDirectory = new String(); myDirectory =
				 * sb.toString(); System.out.println("value " +
				 * String.valueOf("\"")); //String myDirectory =
				 * "\"C:\\ZUG\\Input Files\"";
				 * //System.out.println("wdirectory = " + myDirectory);
				 * System.out.println("Original: " + myDirectory); int pos;
				 * while ( (pos = myDirectory.indexOf("\"")) > -1 ) {
				 * sb.replace(pos, pos + 1, "&quot;"); myDirectory =
				 * sb.toString(); }
				 * 
				 * StringBuffer qualifiedWorkingDirectory = new StringBuffer();
				 * /*char[] charsToTrim = workingDirectory.toCharArray();
				 * StringBuffer stringTrimmed = new StringBuffer();
				 * 
				 * for(int i=0;i<charsToTrim.length;i++) {
				 * if(charsToTrim[i]!='\"') charsTrimmed[charsTrimmed.length] =
				 * charsToTrim[i]; } workingDirectory = charsTrimmed.toString();
				 * workingDirectory = Utility.TrimStartEnd(workingDirectory,
				 * '\"', 0); workingDirectory =
				 * Utility.TrimStartEnd(workingDirectory, '\"', 1);
				 */
				// System.out.println("FindWorkingDir-?\t"+workingDirectory+"\nCommand-?\t"+command);
				File f = new File(workingDirectory + SLASH + command);// Changed
																		// from
																		// "\\"
																		// to
																		// "/"
				if (f.exists()) {
					Log.Debug(String
							.format("Controller/FindWorkingDirectory: Found with argument as %s when Working Directory value is %s",
									command, workingDirectory));

					return workingDirectory;
				}
			}
		}
		return StringUtils.EMPTY;

	}

	/***
	 * Function to Execute the command on the Command Prompt.
	 * 
	 * @param command
	 *            Command to Execute
	 * @param arguments
	 *            Argument for the Command to Execute.
	 * @param workingDirectory
	 *            Working Directory from where one would like to execute the
	 *            command. workingDirectory can be list of directory
	 */
	private void ExecuteCommand(String command, String arguments,
			String workingDirectoryList, UserData data,
			String parentTestCaseId, String step) throws Exception {
		Log.Debug(String
				.format("Controller/ExecuteCommand : Start of function with command = %s, Arguments = %s and Working Directory = %s & parentTestCaseID = %s ",
						command, arguments, workingDirectoryList,
						parentTestCaseId));

		String actualCommand = command;

		Log.Debug("Controller/ExecuteCommand: Calling Controller/FindWorkingDirectory()");
		String workingDirectory = FindWorkingDirectory(command,
				workingDirectoryList);

		if (debugMode == true) {

			if (StringUtils.isBlank(workingDirectory)) {
				File f = new File(command);
				if (!f.exists()) {
					Log.Debug(String
							.format("Controller/ExecuteCommand : Calling ExecuteDefaultCommand with argument as %s when Working Directory is Empty",
									command));
					ExecuteDefaultCommand(command, parentTestCaseId);
					Log.Debug(String
							.format("Controller/ExecuteCommand : End of function with command = %s, Arguments = %s and Working Directory = %s ",
									command, arguments, workingDirectory));
					return;
				}
			} else {
				File f = new File(workingDirectory + SLASH + command);// Changed
																		// from
																		// "\\"
																		// to
																		// "/"
				if (!f.exists()) {

					Log.Debug(String
							.format("Controller/ExecuteCommand : Calling ExecuteDefaultCommand with argument as %s when Working Directory value is %s",
									command, workingDirectory));
					ExecuteDefaultCommand(command, parentTestCaseId);
					Log.Debug(String
							.format("Controller/ExecuteCommand : End of function with command = %s, Arguments = %s and Working Directory = %s ",
									command, arguments, workingDirectory));
					return;
				}

			}
		}
		if (OS_FLAG) {
			workingDirectory = Utility.TrimStartEnd(workingDirectory, '/', 1);
			workingDirectory = Utility.TrimStartEnd(workingDirectory, '\\', 1);
			actualCommand = new File(workingDirectory, command)
					.getCanonicalPath();// Removed the method getCanonical Path
		} else {
			actualCommand = new File(workingDirectory, command).getPath();
		}
		// System.out.println("The Working Directory-?\t"+workingDirectory+"\n"+actualCommand);

		// Part changed by Suddha and Modified by Sankho
		// In this part it is coming with .class
		String FileName = "";
		boolean Flags = false;
		if (actualCommand.endsWith("class") || actualCommand.endsWith("CLASS")) {
			// Here .class is removed.

			Flags = true;
			String Path[] = actualCommand.split("\\\\");
			for (String name : Path) {

				if (name.endsWith(".class") || name.endsWith(".CLASS")) {
					FileName = name.replaceAll(".class", "").trim();
				} else {
					// FileName="C:\\Program Files\\Automature\\ZUG\\DefaultAction.exe";
					continue;

				}
			}

		} else {
			FileName = actualCommand;
		}

		String Arguments = arguments;
		ProcessBuilder pr = new ProcessBuilder();
		String commandValue[];

		if (arguments == null || StringUtils.isEmpty(arguments)) {
			commandValue = new String[0];
		} else {
			commandValue = arguments.split("\" \""); // splits the Argument
		} // string on
			// " "(<quote><space><quote>)
		Process process = null;
		ArrayList<String> commandparam = new ArrayList<String>(); // ArrayList
		// to hold
		// the
		// command
		// and
		// command
		// parameters.
		int exitValue;
		// Add command(file) to be execute into Arraylist.

		if (command.contains(".")) {
			Log.Debug("Controller/ExecuteCommand :: Looking for commands in interpreter Lists");
			String[] commandNameExtension = command.split("\\.");
			if (fileExtensionSupport.containsKey(commandNameExtension[1]
					.toLowerCase())) {
				String[] commandList = fileExtensionSupport
						.get(commandNameExtension[1]);
				for (String cmd : commandList) {
					Log.Debug("controller/ExecuteCommand: Adding Command parameters - "
							+ cmd);
					if (!cmd.isEmpty())// Chekcing for Null Inclution
					{
						commandparam.add(cmd.trim());
					} else {
						// System.out.println("Empty Value");
					}

				}
				/*
				 * if(Flags) { commandparam.add("-classpath C:\\MyTests "); }
				 */
			}
		}

		/*
		 * String Path = new String(System.getenv("PATH")); String[] PathList =
		 * Path.split(";"); boolean foundPath = false;
		 * 
		 * if(command.endsWith(".rb")) { // commandparam.add("cmd.exe"); String
		 * RubyPath = null; for (String PathVar : PathList) {
		 * if(PathVar.contains("Ruby")) { Log.Debug(
		 * "Controller/ExecuteCommand : Path Listing found with Ruby string");
		 * foundPath=true; if (PathVar.endsWith("\\")) RubyPath = new
		 * String(PathVar + "Ruby.exe"); else RubyPath = new String(PathVar +
		 * "\\Ruby.exe"); break; } } File RubyInterpreter = new File(RubyPath);
		 * if(RubyInterpreter.exists()) { Log.Debug(
		 * "Controller/ExecuteCommand : Ruby Path adding to Command parameters "
		 * ); commandparam.add(RubyPath); } else { Log.Debug(
		 * "Controller/ExecuteCommand : Ruby Interpreter does not exist so calling Default command..."
		 * ); Log.Debug(String.format(
		 * "Controller/ExecuteCommand : Calling ExecuteDefaultCommand with argument as %s when Working Directory value is %s"
		 * , command, workingDirectory)); ExecuteDefaultCommand(command,
		 * parentTestCaseId); Log.Debug(String.format(
		 * "Controller/ExecuteCommand : End of function with command = %s, Arguments = %s and Working Directory = %s "
		 * , command, arguments, workingDirectory)); return; } } else
		 * if(command.endsWith(".pl")) { // commandparam.add("cmd.exe"); String
		 * PerlPath = null; for (String PathVar : PathList) {
		 * if(PathVar.contains("Perl")) { Log.Debug(
		 * "Controller/ExecuteCommand : Path Listing found with Perl string");
		 * foundPath=true; if (PathVar.endsWith("\\")) PerlPath = new
		 * String(PathVar + "Perl.exe"); else PerlPath = new String(PathVar +
		 * "\\Perl.exe"); break; } } File PerlInterpreter = new File(PerlPath);
		 * if(PerlInterpreter.exists()) { Log.Debug(
		 * "Controller/ExecuteCommand : Perl Path adding to Command parameters "
		 * ); commandparam.add(PerlPath); } else { Log.Debug(
		 * "Controller/ExecuteCommand : Perl Interpreter does not exist so calling Default command..."
		 * ); Log.Debug(String.format(
		 * "Controller/ExecuteCommand : Calling ExecuteDefaultCommand with argument as %s when Working Directory value is %s"
		 * , command, workingDirectory)); ExecuteDefaultCommand(command,
		 * parentTestCaseId); Log.Debug(String.format(
		 * "Controller/ExecuteCommand : End of function with command = %s, Arguments = %s and Working Directory = %s "
		 * , command, arguments, workingDirectory)); return; } }
		 */
		commandparam.add(FileName.trim());

		// Add the Command param to arrayList

		for (int i = 0; i < commandValue.length; i++) {

			if (i == 0) {

				commandparam.add(commandValue[i].substring(1));
			} else if (i == commandValue.length - 1) {
				commandparam.add(commandValue[i].substring(0,
						commandValue[i].length() - 2).trim());
			} else {
				commandparam.add(commandValue[i].trim());
			}
		}

		try {
			pr.command(commandparam);
			// System.out.println("Command Param is-?\t"+commandparam+"File-?\n"+FileName);
			// message("The List"+commandparam);
			if (StringUtils.isNotBlank(workingDirectory)) {
				pr.directory(new File(workingDirectory));
				Log.Debug(String
						.format("Controller/ExecuteCommand  :workingDirectory for the Command %s is %s . ",
								command, workingDirectory));
			}

			Log.Debug(String
					.format("Controller/ExecuteCommand  :Arguments for the Command %s is %s . ",
							command, arguments));

			Log.Debug(String
					.format("Controller/ExecuteCommand :Started the Process for the Command %s with Arguments as %s . ",
							command, arguments));

			/*
			 * process= Runtime.getRuntime().exec(new String[] { "cmd", "/c",
			 * "start", "C:\\DefaultAction.BAT", Arguments });
			 * //Runtime.getRuntime().exec(FileName, null, new
			 * File("E:\\MyWork\\LM")); //pr.start();
			 */
			// System.out.println("Process dir ="+pr.directory().getCanonicalPath());

			process = pr.start();

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					process.getErrorStream()));

			// read the output from the command
			String primitiveStreams = null;
			String errorPrimitiveMessage = StringUtils.EMPTY;

			// The standard output of the command
			while ((primitiveStreams = stdInput.readLine()) != null) {
				Log.Debug("Controller/ExecuteCommand : [AtomLog/" + FileName
						+ "] - " + primitiveStreams);
				// System.out.println("OUTPUT->\t"+primitiveStreams);
			}

			// read any errors from the attempted command

			// The standard error of the command

			while ((primitiveStreams = stdError.readLine()) != null) {
				Log.Debug("Controller/ExecuteCommand : [AtomLog/" + FileName
						+ "] - " + primitiveStreams);
				Log.Error("[PrimitiveLog/" + FileName + "] - "
						+ primitiveStreams);
				errorPrimitiveMessage += primitiveStreams;
			}
			Log.Debug(String
					.format("Controller/ExecuteCommand  :Waiting for the Process to Exit for the Command %s with Arguments as %s . ",
							command, arguments));

			if (debugMode || (doCleanupOnTimeout && step.endsWith("c"))) {
				exitValue = process.waitFor();
			} else {
				while (true) {
					if (!debugMode) {
						if (_testPlanStopper
								|| ((Boolean) _testStepStopper
										.get(parentTestCaseId))) {
							// kill the process....
							try {
								process.destroy();
								process = null;
							} catch (Exception e) {
								Log.Error(String
										.format("Controller/ExecuteCommand : Exception occured while killing the process"));
							}

							if (_testPlanStopper) {
								// Log.Error(String.format("Controller/ExecuteCommand : Command %s took longer time to execute. Test Plan Time Specified = %s seconds  is over.",
								// command,
								// ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")));
								throw new Exception(
										String.format(
												"Controller/ExecuteCommand : Command %s took longer time to execute. Test Plan Time Specified = %s seconds  is over.",
												command,
												ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")));
							}

							if (((Boolean) _testStepStopper
									.get(parentTestCaseId))) {
								// Log.Error(String.format("Controller/ExecuteCommand : Command %s took longer time to execute. Test Step Time Specified = %s seconds  is over.",
								// command,
								// ReadContextVariable("ZUG_TESTSTEP_TIMEOUT")));
								throw new Exception(
										String.format(
												"Controller/ExecuteCommand : Command %s took longer time to execute. Test Step Time Specified = %s seconds  is over.",
												command,
												ReadContextVariable("ZUG_TESTSTEP_TIMEOUT")));
							}
						}
					}
					// Check process has exited
					try {
						exitValue = process.exitValue();
						break;
					} catch (IllegalThreadStateException ex) {
						Log.Debug(String
								.format("Controller/ExecuteCommand : Process with name : %s \n is still RUNNING.  IllegalThreadState Exception. Message is %s ",
										FileName.toString(), ex.getMessage()));
						Log.Error(String
								.format("Controller/ExecuteCommand : Process with name : %s \n is still RUNNING.  IllegalThreadState Exception. Message is %s ",
										FileName.toString(), ex.getMessage()));
					} catch (Exception e) {
						Log.Debug(String
								.format("Controller/ExecuteCommand : Process with name : %s \n is still RUNNING.  Unknown Exception. Message is %s ",
										FileName.toString(), e.getMessage()));
						Log.Error(String
								.format("Controller/ExecuteCommand : Process with name : %s \n is still RUNNING.  Unknown Exception. Message is %s ",
										FileName.toString(), e.getMessage()));
					}
					// Wait for some time interval..
					Thread.sleep(20);
				}
			}

			String errorMessage = ""; // TODO
			// ContextVar.getContextVar("ErrorMessage_"
			// + process
			// ProcessMonitorThread.currentThread().getId());
			ContextVar.Delete("ErrorMessage_" + Thread.currentThread().getId());

			if (StringUtils.isNotBlank(errorMessage)) {
				// Log.Error(String.format("Controller/ExecuteCommand : Errors for %s Command is %s",
				// command, errorMessage));
				throw new Exception(
						String.format(
								"Controller/ExecuteCommand : Errors for %s Command is %s",
								command, errorMessage));
			}

			Log.Debug(String
					.format("Controller/ExecuteCommand :Process Exited Gracefully for the Command %s with Arguments as %s . ",
							command, arguments));

			if (!(process.exitValue() == 0)) {
				// Log.Error(String.format("Controller/ExecuteCommand : Exit Status for %s Command is %s",
				// command, process.exitValue()));
				throw new Exception(String.format(
						"Exit Status for %s Command is %s\n%s", command,
						process.exitValue(), errorPrimitiveMessage));
			}
		} catch (Exception ex) {
			String error = String
					.format("Exception in Command %s.\n\tException Message is :\n\tFoot Print:\n %s",
							command, ex.getMessage());
			// Log.Error(error);

			throw new Exception(error);
		} finally {
			// Close the process created.
			if (process != null) {
				try {
					// This will throw an exception when the process has not
					// exited.
					process.exitValue();

				} catch (Exception e) {

					Log.Debug("Controller/ExecuteCommand  : Closing and Disposing the Process.");
					process.destroy();
					process = null;
					Log.Debug("Controller/ExecuteCommand : Successfully Closed and Disposed the Process.");

				}
			}
		}
		Log.Debug(String
				.format("Controller/ExecuteCommand : End of function with command = %s, Arguments = %s and Working Directory = %s ",
						command, arguments, workingDirectory));
	}

	/***
	 * Function to get the correct argument at Runtime to pass to an Action or
	 * Verification... The function will check if this is a Context Variable or
	 * not and pass the Value to the Actions accordingly.
	 * 
	 * @param argument
	 *            Name of the Argument
	 */
	private String NormalizeVariable(String argument, String threadID)
			throws Exception {
		if (StringUtils.isBlank(argument)) {
			Log.Debug("Controller/NormalizeVariable : Start of function with variableToFind = EMPTY and its value is -> EMPTY. ");
			return StringUtils.EMPTY;
		}
		Log.Debug(String
				.format("Controller/NormalizeVariable : Start of function with argument = %s .",
						argument));
		String tempValue = argument;
		if (argument.contains("=")) {
			Log.Debug("Controller/NormalizeVariable : The Variable to Find contains an = sign ");
			String[] splitVariableToFind = Excel.SplitOnFirstEquals(argument);

			Log.Debug("Controller/NormalizeVariable : Length of  splitVariableToFind = "
					+ splitVariableToFind.length);
			if (splitVariableToFind.length <= 1) {
				String tempVariable = argument;
				if (tempVariable.endsWith("##")) {
					tempVariable = Utility.TrimStartEnd(tempVariable, '#', 0);
					tempVariable = Utility.TrimStartEnd(tempVariable, '#', 1);
					tempVariable += threadID;
				}
				Log.Debug("Controller/NormalizeVariable : End of function with variableToFind = "
						+ argument + " and its value is -> " + tempVariable);
				return tempVariable;
			}

			tempValue = splitVariableToFind[1];
			Log.Debug("Controller/NormalizeVariable : variableToFind = "
					+ tempValue);

			String tempVariableOutside = splitVariableToFind[0];
			if (tempVariableOutside.endsWith("##")) {
				tempVariableOutside = Utility.TrimStartEnd(tempVariableOutside,
						'#', 0);
				tempVariableOutside = Utility.TrimStartEnd(tempVariableOutside,
						'#', 1);
				tempVariableOutside += threadID;
			}

			tempValue = tempVariableOutside + "="
					+ DoSomeFineTuning(tempValue, threadID);
		} // First Check in the Context Variable
		else {
			tempValue = DoSomeFineTuning(tempValue, threadID);
		}

		Log.Debug("Controller/NormalizeVariable : End of function with variableToFind = "
				+ argument + " and its value is -> " + tempValue);

		return tempValue;
	}

	/***
	 * This is just a temporary Utility. Could not think of a good name for this
	 * Function. This function actually checks a variable and looks for if it
	 * contains any Context Variable. If it contains a Context Variable then,
	 * the function just substitutes the Value of the Context Variable.
	 * 
	 * @param variable
	 *            = Name of the Variable for which we need to do FINE
	 *            Tuning</param> <returns>A Fine tuned Variable.</returns>
	 */
	private String DoSomeFineTuning(String variable, String threadID)
			throws Exception {
		boolean isContextVar = false;
		Log.Debug("Controller/DoSomeFineTuning : Start of Function with Variable Name as : "
				+ variable);
		if (StringUtils.isBlank(variable)) {
			Log.Debug("Controller/DoSomeFineTuning : Variable is Empty, so returning an Empty String.");
			return StringUtils.EMPTY;
		}
		int firstOccuranceOfPercentage = -1;
		int secondOccuranceOfPercentage = -1;

		int indexer = -1;
		// Log.Debug("Controller/DoSomeFineTuning : Checking for the Occurance of % and their Indexes in Variable :"
		// + variable);
		for (char varChar : variable.toCharArray()) {
			indexer++;
			// Log.Debug(String.format("Controller/DoSomeFineTuning : Working at Indexes[%d]=%s in Variable %s.",
			// indexer, varChar, variable));
			if (varChar == '%') {
				// Log.Debug(String.format("Controller/DoSomeFineTuning : Indexes=%d in Variable %s.",
				// indexer, variable));
				if (firstOccuranceOfPercentage >= 0) {
					secondOccuranceOfPercentage = indexer;
					// Log.Debug(String.format("Controller/DoSomeFineTuning : secondOccuranceOfPercentage=%d in Variable %s.",
					// indexer, variable));
					break;
				} else {
					firstOccuranceOfPercentage = indexer;
					// Log.Debug(String.format("Controller/DoSomeFineTuning : firstOccuranceOfPercentage=%d in Variable %s.",
					// indexer, variable));
				}
			}
		}
		// message("THe first Occurrance is "+firstOccuranceOfPercentage+"\n The second Ouccurance "+secondOccuranceOfPercentage);

		Log.Debug(String
				.format("Controller/DoSomeFineTuning : firstOccuranceOfPercentage=%s && secondOccuranceOfPercentage=%s.",
						firstOccuranceOfPercentage, secondOccuranceOfPercentage));

		if (firstOccuranceOfPercentage >= 0 && secondOccuranceOfPercentage >= 0) {
			// String tempVariable =
			// variable.substring(firstOccuranceOfPercentage,
			// secondOccuranceOfPercentage - firstOccuranceOfPercentage + 1);
			String tempVariable = variable
					.substring(firstOccuranceOfPercentage,
							secondOccuranceOfPercentage + 1);
			// message("The String subs.. "+tempVariable);
			Log.Debug(String.format(
					"Controller/DoSomeFineTuning : Context Variable = ",
					tempVariable));

			// First Check in the Macro Sheet
			if (tempVariable.startsWith("%") && tempVariable.endsWith("%")) {

				tempVariable = Utility.TrimStartEnd(tempVariable, '%', 0);
				tempVariable = Utility.TrimStartEnd(tempVariable, '%', 1);

				if (tempVariable.endsWith("##")) {
					tempVariable = Utility.TrimStartEnd(tempVariable, '#', 0);
					tempVariable = Utility.TrimStartEnd(tempVariable, '#', 1);
					tempVariable += threadID;
				}

				// if (StringUtils.isNotBlank(ContextVar
				// .getContextVar(tempVariable))) {
				isContextVar = true;
				// }
				tempVariable = ReadContextVariable(tempVariable);

				Log.Debug(String
						.format("Controller/DoSomeFineTuning : After Context Variable Parsing, variableToFind = %s ",
								tempVariable));
			}

			// TODO - Although Gurpreet has done this, still I need to re-verify
			// the same
			// message("The Variable "+variable);
			// StringBuffer actualValue = new StringBuffer(variable.substring(0,
			// firstOccuranceOfPercentage)+
			// variable.substring(secondOccuranceOfPercentage -
			// firstOccuranceOfPercentage + 1,variable.length()));
			StringBuffer actualValue = new StringBuffer(variable.substring(0,
					firstOccuranceOfPercentage)
					+ variable.substring(firstOccuranceOfPercentage,
							variable.length()));
			Log.Debug(String.format(
					"Controller/DoSomeFineTuning : actualValue = %s",
					actualValue.toString()));
			// message("The actual value "+actualValue+"\t The temp "
			// +tempVariable);

			if (isContextVar) {
				actualValue.replace(firstOccuranceOfPercentage,
						secondOccuranceOfPercentage + 1, tempVariable);
			} else {
				actualValue = actualValue.insert(firstOccuranceOfPercentage,
						tempVariable);
			}

			// message("The actual value after insert "+actualValue);
			Log.Debug(String
					.format("Controller/DoSomeFineTuning : End of Function. Function returning %s for Variable %s ",
							actualValue, variable));

			return new String(actualValue);
		} else {
			String tempVariable = variable;
			if (tempVariable.endsWith("##")) {
				tempVariable = Utility.TrimStartEnd(tempVariable, '#', 0);
				tempVariable = Utility.TrimStartEnd(tempVariable, '#', 1);
				tempVariable += threadID;
			}
			Log.Debug(String
					.format("Controller/DoSomeFineTuning : End of Function. Function returning %s for Variable %s ",
							tempVariable, variable));

			return tempVariable;
		}
	}

	private void RunExpandedTestCaseForMolecule(Object testcaseObj) {
		Log.Debug("Controller/RunExpandedTestCaseForMolecule : Start of Function.");

		TestCase test = (TestCase) testcaseObj;
		// String[] variableCombinations = test.testCaseID.split("_");

		// String jointVarComb = Utility.join(" ", variableCombinations,
		// 1,variableCombinations.length - 1);
		String jointVarComb = test.testCaseID.replace("_", " ");
		if (StringUtils.isNotBlank(jointVarComb)) {
			message("******************************************************************************** ");
			message("\nWorking on Test Case Variable Combination "
					+ jointVarComb);
		}
		// Now run each of the Actions mentioned here...and try running it.
		Action[] actions = new Action[test.actions.size()];
		test.actions.toArray(actions);
		Log.Debug("Controller/RunExpandedTestCaseForMolecule:  Number of Actions to run is : "
				+ actions.length + " for TestCase ID : " + test.testCaseID);

		Hashtable<String, String> stepsKeys = new Hashtable<String, String>();
		// After getting the Actions Store the Steps somewhere...
		for (int i = 0; i < actions.length; i++) {
			Action action = actions[i];
			Log.Debug("Controller/RunExpandedTestCaseForMolecule: Storing the Steps in a HashTable. Step Number = "
					+ action.step);
			stepsKeys.put(action.step, StringUtils.EMPTY);
			Log.Debug("Controller/RunExpandedTestCaseForMolecule: Successfully stored Step Number = "
					+ action.step + " to the HashTable");
		}

		String errorDuringTestCaseExecution = StringUtils.EMPTY;
		// First Clear the Stack.
		Stack<String> actionsForCleanup = new Stack<String>();

		int count = 0;
		ArrayList<Thread> ThreadPool = new ArrayList<Thread>();
		String stepNumber = StringUtils.EMPTY;

		if (actions.length > 0) {
			stepNumber = actions[0].step;
		}
		try {
			for (int i = 0; i < actions.length; i++) {
				final Action action = actions[i];
				count++;
				if (StringUtils.isBlank(action.actionName)) {
					continue;
				}

				Log.Debug("Controller/RunExpandedTestCaseForMolecule:  Action : "
						+ action.actionName
						+ " has Step Number as : "
						+ action.step);

				// If the action Steps contain an "i" and there is a cleanup
				// step for it then add that to the stack.
				if (action.step.endsWith("i")
						&& (stepsKeys.containsKey(action.step.substring(0,
								action.step.length() - 1) + "c") || stepsKeys
									.containsKey(action.step.substring(0,
											action.step.length() - 1) + "C"))) {
					Log.Debug("Controller/RunExpandedTestCaseForMolecule:  Action : "
							+ action.actionName
							+ " with Step : "
							+ action.step
							+ " is an Initialization Action. A cleanup exist for this action ..so pusing this to the STACK");
					actionsForCleanup.push(action.step.substring(0,
							action.step.length() - 1));
				}

				// If this is a Cleanup action then break and run the cleanup at
				// the end.
				if (action.step.endsWith("c")
						&& actionsForCleanup.contains(action.step.substring(0,
								action.step.length() - 1))) {
					Log.Debug("Controller/RunExpandedTestCaseForMolecule:  Action : "
							+ action.actionName
							+ " with Step : "
							+ action.step
							+ " is an Cleanup Action/STEP. so breaking and moving to cleanup");
					count--;
					break;
				}

				Log.Debug("Controller/RunExpandedTestCaseForMolecule:  Calling RunAction for Action : "
						+ action.actionName
						+ " for TestCase ID : "
						+ test.testCaseID);

				if (stepNumber.equals(action.step)
						&& StringUtils.isNotBlank(action.step)) {
					Thread thread = new Thread(new Runnable() {

						public void run() {
							RunAction(action);
						}
					});
					// thread.IsBackground = true;
					thread.start();
					ThreadPool.add(thread);
				} else {
					// Wait for all the Threads to finish.
					for (int t = 0; t < ThreadPool.size(); ++t) {
						((Thread) ThreadPool.get(t)).join();
					}

					ThreadPool.clear();

					// In case we get an Exception then Dont run any more
					// processes
					if ((StringUtils
							.isNotBlank(errorMessageDuringMoleculeCaseExecution
									.get(test.stackTrace)))) {
						count--;
						// Remove this element as this is not executed.
						if (action.step.endsWith("i")
								&& (stepsKeys.containsKey(action.step
										.substring(0, action.step.length() - 1)
										+ "c") || stepsKeys
											.containsKey(action.step.substring(
													0, action.step.length() - 1)
													+ "C"))) {
							actionsForCleanup.pop();
						}
						break;
					}

					// The new step number is the current one
					stepNumber = action.step;
					Thread thread = new Thread(this.getActionImplementer(this,
							action));
					// thread.IsBackground = true;
					thread.start();
					ThreadPool.add(thread);
				}

				Log.Debug("Controller/RunExpandedTestCaseForMolecule:  RunAction executed successfully for Action : "
						+ action.actionName
						+ " for TestCase ID : "
						+ test.testCaseID);
			}

			for (int t = 0; t < ThreadPool.size(); ++t) {
				((Thread) ThreadPool.get(t)).join();
			}
		} catch (Exception ex) {
			Log.ErrorInLog("Controller/RunExpandedTestCaseForMolecule: Exception while running test case "
					+ test.testCaseID + "." + ex.getMessage());
			errorDuringTestCaseExecution += "Controller/RunExpandedTestCaseForMolecule: Exception while running test case "
					+ test.testCaseID + "." + ex.getMessage();
		} finally {
			boolean cleanupActionStarted = false;
			Log.Debug("Controller/RunExpandedTestCaseForMolecule:  Number of testcases steps successfully executed are "
					+ count + " for TestCase ID : " + test.testCaseID);
			// In the finally iterate over all the cleanup steps that one would
			// like to perform
			// before the test case is finally said to be done..
			//
			if ((actionsForCleanup.size() > 0 && !_testPlanStopper && !((Boolean) _testStepStopper
					.get(test.parentTestCaseID)))
					|| (actionsForCleanup.size() > 0 && doCleanupOnTimeout)) {
				message("\n******************** Cleanup Action Started for Test Case : "
						+ test.stackTrace + ". ***************************");
				for (int i = count; i < actions.length; ++i) {
					if ((_testPlanStopper || ((Boolean) _testStepStopper
							.get(test.parentTestCaseID)))
							&& !doCleanupOnTimeout) {
						break;
					}

					Action action = actions[i];

					Log.Debug("Controller/RunExpandedTestCaseForMolecule - Finally/Cleanup:  Working on Action  : "
							+ action.actionName
							+ " for TestCase ID : "
							+ test.testCaseID);
					// We agreed that even if there are Init steps after a
					// cleanup, then that should work as it is.
					// This was required by Urvish initially for
					// Apply-Change-Remove test plan
					// And was accepted by Gilbert Hayes.
					// Gurpreet Anand
					if (!action.step.endsWith("c") && cleanupActionStarted) {
						try {
							Log.Debug("Controller/RunExpandedTestCaseForMolecule - Finally/Cleanup:  Calling RunAction for Action : "
									+ action.actionName
									+ " for TestCase ID : "
									+ test.testCaseID);

							// If the action Steps contain an "i" and there is a
							// cleanup step for it then add that to the stack.
							if (action.step.endsWith("i")
									&& (stepsKeys.containsKey(action.step
											.substring(0,
													action.step.length() - 1)
											+ "c") || stepsKeys
												.containsKey(action.step
														.substring(
																0,
																action.step
																		.length() - 1)
														+ "C"))) {
								Log.Debug("Controller/RunExpandedTestCaseForMolecule:  Action : "
										+ action.actionName
										+ " with Step : "
										+ action.step
										+ " is an Initialization Action. A cleanup exist for this action ..so pusing this to the STACK");
								actionsForCleanup.push(action.step.substring(0,
										action.step.length() - 1));
							}

							if (stepNumber.equals(action.step)
									&& StringUtils.isNotBlank(action.step)) {
								Thread thread = new Thread(
										this.getActionImplementer(this, action));
								// thread.IsBackground = true;
								thread.start();
								ThreadPool.add(thread);
							} else {
								// Wait for all the Threads to finish.
								for (int t = 0; t < ThreadPool.size(); ++t) {
									((Thread) ThreadPool.get(t)).join();
								}

								ThreadPool.clear();

								// The new step number is the current one
								stepNumber = action.step;
								Thread thread = new Thread(
										this.getActionImplementer(this, action));
								// thread.IsBackground = true;
								thread.start();
								ThreadPool.add(thread);
							}

							Log.Debug("Controller/RunExpandedTestCaseForMolecule - Finally/Cleanup:  RunAction executed successfully for Action : "
									+ action.actionName
									+ " for TestCase ID : "
									+ test.testCaseID);
						} catch (Exception ex) {
							Log.ErrorInLog("Controller/RunExpandedTestCaseForMolecule - Finally/Cleanup: Exception while running test case "
									+ test.testCaseID + "." + ex.getMessage());
							errorDuringTestCaseExecution += "Controller/RunExpandedTestCaseForMolecule - Finally/Cleanup: Exception while running test case "
									+ test.testCaseID + "." + ex.getMessage();
						}
					} else if (action.step.endsWith("c")
							&& !actionsForCleanup.contains(action.step
									.substring(0, action.step.length() - 1))) {
						Log.Debug("Controller/RunExpandedTestCaseForMolecule - Finally/Cleanup: For Action  : "
								+ action.actionName
								+ " Initialization is not Done. So Skipping it for TestCase ID : "
								+ test.testCaseID);
						continue;
					} else if ((action.step.endsWith("c")
							&& actionsForCleanup.contains(action.step
									.substring(0, action.step.length() - 1))
							&& !_testPlanStopper && !((Boolean) _testStepStopper
								.get(test.parentTestCaseID)))
							|| (action.step.endsWith("c")
									&& actionsForCleanup.contains(action.step
											.substring(0,
													action.step.length() - 1)) && doCleanupOnTimeout)) {
						// This marks the start for one Cleanup Action.
						cleanupActionStarted = true;
						try {
							Log.Debug("Controller/RunExpandedTestCaseForMolecule - Finally/Cleanup:  Calling RunAction for Action : "
									+ action.actionName
									+ " for TestCase ID : "
									+ test.testCaseID);

							if (stepNumber.equals(action.step)
									&& StringUtils.isNotBlank(action.step)) {
								Thread thread = new Thread(
										this.getActionImplementer(this, action));
								// thread.IsBackground = true;
								thread.start();
								ThreadPool.add(thread);
							} else {
								// Wait for all the Threads to finish.
								for (int t = 0; t < ThreadPool.size(); ++t) {
									((Thread) ThreadPool.get(t)).join();
								}

								ThreadPool.clear();

								// The new step number is the current one
								stepNumber = action.step;
								Thread thread = new Thread(
										this.getActionImplementer(this, action));
								// thread.IsBackground = true;
								thread.start();
								ThreadPool.add(thread);
							}
							Log.Debug("Controller/RunExpandedTestCaseForMolecule - Finally/Cleanup:  RunAction executed successfully for Action : "
									+ action.actionName
									+ " for TestCase ID : "
									+ test.testCaseID);
						} catch (Exception ex) {
							Log.ErrorInLog(String
									.format("Controller/RunExpandedTestCaseForMolecule - Finally/Cleanup: Exception while running test case %s.%s",
											test.testCaseID, ex.getMessage()));
							errorDuringTestCaseExecution += String
									.format("Controller/RunExpandedTestCaseForMolecule - Finally/Cleanup: Exception while running test case %s.%s",
											test.testCaseID, ex.getMessage());
						}
					}
				}

				for (int t = 0; t < ThreadPool.size(); ++t) {
					try {
						((Thread) ThreadPool.get(t)).join();
					} catch (InterruptedException e) {
						Log.Error("controller/RunExpandedTestCases : Interrupted Exception in calling join() on thread pool");
						e.printStackTrace();
					}
				}
				message("\n******************** Cleanup Action Finished for Test Case : "
						+ test.stackTrace + ". ***************************");
			}
		}

		if (StringUtils.isNotBlank(errorDuringTestCaseExecution)) {
			Log.Debug("Controller/RunExpandedTestCaseForMolecule: There were exception raised while running TestCase = "
					+ test.testCaseID
					+ " with Exception message as : "
					+ errorDuringTestCaseExecution);
			errorMessageDuringMoleculeCaseExecution.put(
					test.stackTrace,
					((String) errorMessageDuringMoleculeCaseExecution
							.get(test.stackTrace))
							+ errorDuringTestCaseExecution);
		}
	}

	// private void davosReporting(String hostServer,String message)
	// {
	//
	// sendReport=new RestClient();
	// boolean helpflag=false;
	// if(hostServer.contains(":"))
	// {
	// String tempSplit[]=null;
	// tempSplit=hostServer.split(":");
	// if(tempSplit.length==3)
	// {
	// hostServer=hostServer;
	// }
	// else if(tempSplit.length==2)
	// {
	// hostServer=hostServer+":4567";
	// }
	// else
	// {
	// hostServer="http://www.automature.com:4567/help";
	// helpflag=true;
	// Log.Error("Controller/DavosReporting::Reporting Server url is Incorrect");
	// }
	// }
	// message("Connecting To->"+hostServer);
	// if(helpflag)
	// message(""+sendReport.sendRequest(hostServer, "GET","kkk"));
	// else
	// message(sendReport.sendRequest(hostServer,"POST",message));
	// }
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

	@Deprecated
	private void SaveTestCase(String testCaseID, String testCaseDesc)
			throws Exception, DavosExecutionException {
		Log.Debug("Controller/SaveTestCase : Start of Function with TestCase ID as : "
				+ testCaseID + " and Description as : " + testCaseDesc);
		Log.Debug(String
				.format("Controller/SaveTestCase : Saving Test Case Identifier '%s' with testSuiteName as '%s'. and Description '%s'",
						testCaseID, testSuitName, testCaseDesc));
		testCaseID = davosclient.testCase_write(testSuiteId, testCaseID,
				testCaseDesc, "", "", "", "");
		// message("SaveTestCase::\ttestCaseDesc "+testCaseDesc+"\tTestCaseId "+testCaseID+"\ttestsuitename "+testSuitName+"\ttestsuiteid "+testSuiteId+"\ttestplanid "+TestPlanId+"\t topologysetid "+topologySetId);
		Log.Debug(String
				.format("Controller/SaveTestCase : SUCCESSFULLY SAVED Test Case Identifier '%s' with testSuiteName as '%s'. and Description '%s'",
						testCaseID, testSuitName, testCaseDesc));

		Log.Debug("Controller/SaveTestCase : End of Function with TestCase Identifier as : '"
				+ testCaseID + "' and Description as : '" + testCaseDesc + "'");
	}

	/***
	 * Function to Save the Test Case Result at intermediate steps.
	 */
	String testexecutiondetailid = null, testcycletopologysetid = null,
			variables = null;

	private void SaveTestCaseResultEveryTime(ExecutedTestCase tData)
			throws Exception, DavosExecutionException {
		Log.Debug("Controller/SaveTestCaseResultEveryTime : Start of the Function");
		// message("SaveTestCaseResultEveryTime::\t"+tData.testCaseID+"\t"+tData.testCaseStatus);
		// BusinessLayer.TestCycle testCycle = new BusinessLayer.TestCycle();
		String buildNumber = BuildNo;// Get the build number from Davos ...
		// ("The Build Tag is "+BuildTag+" The build no "+BuildNo);
		TestCaseResult testCaseResult = new TestCaseResult();
		testCaseResult.set_testCaseId(tData.testCaseID);
		// testCaseResult.set_testSuiteName(testSuitName);
		if (StringUtils.isNotBlank(testSuiteId)) {
			testCaseResult.set_testSuiteId(Integer.valueOf(testSuiteId));
		}
		testCaseResult.set_testEngineerName("Automation");
		testCaseResult.set_executionDate(tData.testCaseCompletetionTime);
		testCaseResult.set_testExecution_Time(tData.timeToExecute);
		testCaseResult.set_status(tData.testCaseStatus);
		testCaseResult.set_comments(tData.testCaseExecutionComments);

		testCaseResult.set_buildNo(StringUtils.EMPTY);
		if (StringUtils.isNotBlank(buildNumber)) {
			testCaseResult.set_buildNo(buildNumber);
		}

		// message("The build number " + buildNumber);
		// message("Ininitime " + initializationTime);
		// message("exec time " + testCaseResult.get_testExecution_Time());
		// message("Testcase description " + tData.testcasedescription);
		if (StringUtils.isNotBlank(testCycleId)) {
			Log.Debug("Controller/SaveTestCaseEveryTime:: Davos TestExecutionDetail_write call using TestCaseId="
					+ testCaseResult.get_testCaseId()
					+ " TestCycleId="
					+ testCycleId + "---TIMESTAMP:: " + Utility.dateNow());
			// message("testcycle found " + testCycleId);
			// message("testexecution call \n" + testCaseResult.get_testCaseId()
			// + "--" + tData.testcasedescription + "--" + testCycleId +
			// testCaseResult.get_status() + buildNumber + new
			// Integer(testCaseResult.get_testExecution_Time()).toString() + ""
			// + "" + testSuitName + topologySetId + readExcel.TestSuitRole() +
			// testCaseResult.get_comments());
			if (StringUtils.isBlank(tData.testcasedescription)) {
				tData.testcasedescription = "";
			}
			ContextVar.setContextVar("ZUG_TCYCLENAME", "");
			testexecutiondetailid = davosclient.testExecutionDetails_write(
					testCaseResult.get_testCaseId(), tData.testcasedescription,
					testCycleId, testCaseResult.get_status(), buildNumber,
					new Integer(testCaseResult.get_testExecution_Time())
							.toString(), "", "", testSuitName, topologySetId,
					readExcel.TestSuitRole(), testCaseResult.get_comments());
			// variables=davosclient.variables_write(testCaseResult.get_testCaseId(),""
			// ,"" );

		} else {

			Log.Debug("Controller/SaveTestCaseEveryTime:: Davos TestCycle_Write call using TestPlanID="
					+ TestPlanId
					+ " TestCycleDescription=TC_"
					+ Utility.dateAsString()
					+ "---TIMESTAMP:: "
					+ Utility.dateNow());
			// Harness specific TestCycle name
			ContextVar.setContextVar("ZUG_TCYCLENAME",
					"TC_" + Utility.dateAsString());
			testCycleId = davosclient.testCycle_write(TestPlanId, ContextVar
					.getContextVar("ZUG_TCYCLENAME"), "", "", new Integer(
					initializationTime).toString(),
					new Integer(testCaseResult.get_testExecution_Time())
							.toString());
			Log.Debug("Controller/SaveTestCaseEveryTime:: Davos TestExecutionDetail_write call using TestCaseId="
					+ testCaseResult.get_testCaseId()
					+ " TestCycleId="
					+ testCycleId + "---TIMESTAMP:: " + Utility.dateNow());
			// message("testexecution call \n" + testCaseResult.get_testCaseId()
			// + "--" + tData.testcasedescription + "--" + testCycleId +
			// testCaseResult.get_status() + buildNumber + new
			// Integer(testCaseResult.get_testExecution_Time()).toString() + ""
			// + "" + testSuitName + topologySetId + readExcel.TestSuitRole() +
			// testCaseResult.get_comments());
			testexecutiondetailid = davosclient.testExecutionDetails_write(
					testCaseResult.get_testCaseId(), tData.testcasedescription,
					testCycleId, testCaseResult.get_status(), buildNumber,
					new Integer(testCaseResult.get_testExecution_Time())
							.toString(), "", "", testSuitName, topologySetId,
					readExcel.TestSuitRole(), testCaseResult.get_comments());
			// testexecutiondetailid =
			// davosclient.testExecutionDetails_write(testCaseResult.get_testCaseId(),
			// testCycleId, testCaseResult.get_status(),
			// testCaseResult.get_buildNo(), new
			// Integer(testCaseResult.get_testExecution_Time()).toString(),
			// testCaseResult.get_performanceExecutionDetailTable().get_performanceExecutionDetailId(),
			// testCaseResult.get_executionDate().toString(),
			// testSuitName,topologySetId);//no performance detail id
		}
		Log.Debug(String
				.format("Controller/SaveTestCaseResultEveryTime : TestCaseId = %s Status = %s and Comments = %s is saved. The TestExecutionID=%s",
						testCaseResult.get_testCaseId(),
						testCaseResult.get_status(),
						testCaseResult.get_comments(), testexecutiondetailid));
		// // Harness Specific ContextVariable to store TestCycle ID
		ContextVar.setContextVar("ZUG_TCYCID", testCycleId);
		// // Harness Specific ContextVariable to store TestExecutionDetail ID
		ContextVar.setContextVar("ZUG_TSEXDID", testexecutiondetailid);
		// Harness Specific Topologysetid
		ContextVar.setContextVar("ZUG_TOPOSET", "" + topologySetId);
		this.TOPOSET = ContextVar.getContextVar("ZUG_TOPOSET");
		Log.Debug("Controller/SaveTestCaseResultEveryTime : End of the Function");
	}

	/***
	 * Function to generate new test case ID
	 */
	private TestCase GenerateNewTestCaseID(TestCase test, int count) {
		Log.Debug("Controller/GenerateNewTestCaseID: Start of function with a new TestCase. TestCase ID is "
				+ test.testCaseID + " and count = " + count);

		TestCase tempTestCase = new TestCase();
		tempTestCase.automated = test.automated;
		Log.Debug("Controller/GenerateNewTestCaseID: tempTestCase.automated = "
				+ tempTestCase.automated);

		tempTestCase.nameSpace = test.nameSpace;
		Log.Debug("Controller/GenerateNewTestCaseID: tempTestCase.nameSpace = "
				+ tempTestCase.nameSpace);

		tempTestCase.concurrentExecutionOnExpansion = test.concurrentExecutionOnExpansion;
		Log.Debug("Controller/GenerateNewTestCaseID: tempTestCase.concurrentExecutionOnExpansion = "
				+ tempTestCase.concurrentExecutionOnExpansion);

		tempTestCase.testCaseDescription = test.testCaseDescription;
		Log.Debug("Controller/GenerateNewTestCaseID: tempTestCase.testCaseDescription = "
				+ tempTestCase.testCaseDescription);
		tempTestCase.testCaseID = test.testCaseID + "_" + count;
		Log.Debug("Controller/GenerateNewTestCaseID: tempTestCase.testCaseID = "
				+ tempTestCase.testCaseID);

		tempTestCase.parentTestCaseID = tempTestCase.testCaseID;
		Log.Debug("Controller/GenerateNewTestCaseID: tempTestCase.parentTestCaseID = "
				+ tempTestCase.parentTestCaseID);

		tempTestCase.stackTrace = tempTestCase.testCaseID;

		tempTestCase.user = test.user;
		tempTestCase.userObj = test.userObj;

		Action[] actions = new Action[test.actions.size()];
		test.actions.toArray(actions);
		Log.Debug("Controller/GenerateNewTestCaseID: Number of Actions are : "
				+ actions.length + " for testcase : " + tempTestCase.testCaseID);

		for (Action action : actions) {
			Action tempAction = new Action();
			tempAction.actionName = action.actionName;
			tempAction.isComment = action.isComment;
			tempAction.testCaseID = tempTestCase.testCaseID;
			tempAction.parentTestCaseID = tempTestCase.testCaseID;
			Log.Debug("Controller/GenerateNewTestCaseID: tempAction.parentTestCaseID =  : "
					+ tempAction.parentTestCaseID);

			tempAction.stackTrace = tempTestCase.stackTrace;
			tempAction.nameSpace = action.nameSpace;

			tempAction.userObj = action.userObj;
			tempAction.step = action.step;
			tempAction.lineNumber = action.lineNumber;
			tempAction.sheetName = action.sheetName;

			Log.Debug("Controller/GenerateNewTestCaseID: Working on Action "
					+ action.actionName + " with Step Number as " + action.step);
			Log.Debug("Controller/GenerateNewTestCaseID: Number of Action Arguments are : "
					+ action.actionArguments.size()
					+ " for action : "
					+ action.actionName);
			@SuppressWarnings("unused")
			ArrayList<String> tempActionArguments = new ArrayList<String>();

			tempAction.actionActualArguments = new ArrayList<String>(
					action.actionActualArguments);
			tempAction.actionArguments = new ArrayList<String>(
					action.actionArguments);

			Verification[] verifications = new Verification[action.verification
					.size()];
			action.verification.toArray(verifications);
			Log.Debug("Controller/GenerateNewTestCaseID: Number of verifications are : "
					+ verifications.length
					+ " for Action : "
					+ action.actionName);
			for (int i = 0; i < verifications.length; i++) {
				Verification verification = verifications[i];
				Log.Debug("Controller/GenerateNewTestCaseID: Working on Verification "
						+ verification.verificationName);
				Verification tempVerification = new Verification();
				// tempVerification.expectedResult =
				// verification.expectedResult;
				tempVerification.verificationName = verification.verificationName;
				tempVerification.isComment = verification.isComment;
				tempVerification.testCaseID = tempTestCase.testCaseID;
				tempVerification.userObj = verification.userObj;
				tempVerification.parentTestCaseID = tempTestCase.testCaseID;
				Log.Debug("Controller/GenerateNewTestCaseID: tempVerification.parentTestCaseID =  : "
						+ tempVerification.parentTestCaseID);

				tempVerification.stackTrace = tempTestCase.stackTrace;
				tempVerification.nameSpace = verification.nameSpace;

				tempVerification.lineNumber = verification.lineNumber;
				tempVerification.sheetName = verification.verificationName;

				// Log.Debug("Controller/GenerateNewTestCaseID: Expected Result = "+verification.verificationName+" for Verification "+
				// verification.verificationName);

				Log.Debug("Controller/GenerateNewTestCaseID: Number of Verification Arguments = "
						+ verification.verificationArguments.size()
						+ " fon Verification " + verification.verificationName);
				tempVerification.verificationArguments = new ArrayList<String>(
						verification.verificationArguments);
				tempVerification.verificationActualArguments = new ArrayList<String>(
						verification.verificationActualArguments);

				tempAction.verification.add(tempVerification);
			}
			tempTestCase.actions.add(tempAction);
		}

		Log.Debug("Controller/GenerateNewTestCaseID: End of function with a new TestCase. TestCase ID is "
				+ test.testCaseID);
		return tempTestCase;
	}

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
			sock = new ServerSocket(iPORT);
			if (sock != null) {
				Log.Debug("Socket created successfully!!!!!!! ->  " + sock);
			}
			// Setting it Reusable for keep on running if the thread waits
			// for 2mls
			sock.setReuseAddress(true);

			Log.Debug("Controller/ListenToPrimitive : Server Socket Created on port "
					+ Integer.toString(iPORT));
//			System.out
//					.println("Controller/ListenToPrimitive : Server Socket Created on port "
//							+ Integer.toString(iPORT));
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
						// else
						// Log.Error("Controller/ListenToPrimitive :ParamFromClient length is zero");
						// message("This is the Log Param->"+ParamFromClient);

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
//				System.out
//						.println("Controller/ListenToPrimitive : Server Socket Created on port "
//								+ Integer.toString(iPORT));
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
							// else
							// Log.Error("Controller/ListenToPrimitive :ParamFromClient length is zero");
							// message("This is the Log Param->"+ParamFromClient);

						}
					} catch (StringIndexOutOfBoundsException se) {
						continue;

					}

				}

			} catch (Exception e) {
//				Log.Error("Controller/ListenToPrimitive: Exception Occurred in Primitive Atom->"
//						+ e.getMessage());
				iPORT++;
				if(iPORT==8256)
				{
					Log.Error("Controller/ListenToPrimitive: Only ten zug instances can run simultaneously "+iPORT);
					System.exit(1);
				}
				ListenToPrimitives(iPORT);
				//System.exit(1);
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
	private String ReadContextVariable(String variableName) throws Exception {
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
		this.TESTSUITEID = ContextVar.getContextVar("ZUG_TESTSUITEID");
		ContextVar.DeleteAll((int) harnessPIDValue);
		ArchiveLog();

		// if (StringUtils.isNotBlank(testCycleId) &&
		// StringUtils.isNotBlank(testSuitName))
		if (dbReporting) {
			message("Cleanup starting... Closing Log");
			Log.Cleanup();
			for (int i = 0; i < Log.HarnessLogFileList.size(); i++) {
				String filePath = Log.HarnessLogFileList.get(i);
				File file = new File(filePath);
				if (file.exists()) {
					// delete the log files after coping
					/*
					 * if(!file.delete())
					 * System.out.println("Could not delete file : " +
					 * file.getAbsolutePath());
					 */
				}
			}
		}

		// Log.Debug(" DoHarnessCleanup/Main : USING System.Environment.Exit(0) to EXIT ");
		System.out.println("\nExiting ZUG");

		System.exit(0);
	}

	/***
	 * Archive Harness and Product log files.
	 */
	private void ArchiveLog() throws DavosExecutionException {

		String archiveLogLocation = StringUtils.EMPTY;
		try {
			// Log.Debug("Controller/ArchiveLog() : Function Started.");
			if (!dbReporting) {
				// dbreporting is off so log will not be archived.
				return;
			}

			// //////////////////this function is not implemented yet in
			// DBLayer. TODO
			/*
			 * if (!BusinessLayer.Initialize.IsDbConnected()) {
			 * Log.Error("Controller/ArchiveLog : Database host machine " +
			 * BusinessLayer.Initialize.getHostName() +
			 * " is not accessible so log is not archived."); return; }
			 */
			// BusinessLayer.FrameworkMetaData frameworkMetaData = new
			// BusinessLayer.FrameworkMetaData();
			//
			archiveLogLocation = davosclient
					.zermattConfig_read("Archivelocation");

			File ArchivePath = new File(archiveLogLocation);

			if (!ArchivePath.isAbsolute()) {
				Log.Error("Controller/ArchiveLog : Log is not archived. Archive Location: "
						+ archiveLogLocation + " is not valid path.");

				return;
			}

			// archiveLogLocation
			// =String.format("%s\\%s\\%s\\%s\\%s",archiveLogLocation,
			// TestPlanId, testCycleId,
			// ContextVar.getContextVar("ZUG_TESTSUITEID"),ContextVar.getContextVar("ZUG_TOPOSET"));
			archiveLogLocation = String.format("%s\\%s\\%s\\%s\\%s",
					archiveLogLocation, TestPlanId, testCycleId, TESTSUITEID,
					TOPOSET);

			String exactDateTime = Utility.dateAsString();
			// Create date time directory.
			archiveLogLocation = String.format("%s\\%s", archiveLogLocation,
					exactDateTime);

			String zugArchive = String.format("%s\\Zug", archiveLogLocation);
			String productArchive = String.format("%s\\Application",
					archiveLogLocation);

			File zugArchivePath = new File(zugArchive);
			File productArchivePath = new File(productArchive);
			if (!zugArchivePath.exists()) {
				// Harness Archive Directory does not exist.
				zugArchivePath.mkdirs();
			}

			if (!productArchivePath.exists()) {
				// Product Archive Directory does not exist.
				productArchivePath.mkdirs();
			}

			// String zugLogLocation=System.getenv("APPDATA") + "\\ZUG Logs";
			// String zipLogLocation=System.getenv("TEMP") + "\\" +
			// exactDateTime + ".zip";

			// new
			// ZipUtility().zip("C:\\Documents and Settings\\dipu\\Application Data\\ZUG Logs",
			// new
			// File("C:\\Documents and Settings\\dipu\\Application Data\\ZUG Logs").getParent()+
			// exactDateTime + ".zip");

			// copy the zug log files to zug archive directory.
			Log.Debug(" Copying and zipping log files in : " + zugArchive);
			for (int i = 0; i < Log.HarnessLogFileList.size(); i++) {
				String filePath = Log.HarnessLogFileList.get(i);
				File file = new File(filePath);
				if (file.exists()) {
					String destinationPath = String.format("%s\\%s",
							zugArchive, file.getName());
					Utility.copyFile(filePath, destinationPath);
					// delete the log files after coping
					/*
					 * if(!file.delete())
					 * System.out.println("Could not delete file : " +
					 * file.getAbsolutePath());
					 */
				}
			}

			// Copy the product log files to product archive directory.
			for (int i = 0; i < productLogFiles.length; i++) {
				File file = new File(productLogFiles[i]);

				// TODO - need to check on this

				if (file.exists()) {
					String fileName = file.getName().substring(0,
							file.getName().indexOf("."));
					String destinationPath = productArchive + SLASH + fileName
							+ ".txt";
					Utility.copyFile(productLogFiles[i], destinationPath);
				}
			}

			// Zip the copied log files in archive location

			// new ZipUtility().zip(archiveLogLocation, new
			// File(archiveLogLocation).getParent()+ exactDateTime + ".zip");

			String zipTmpLocation = System.getenv(ZIP_DIR) + SLASH
					+ exactDateTime + ".zip";
			new ZipUtility().zip(zugArchive, zipTmpLocation);

			// Utility.deleteDirectory(new File(archiveLogLocation));
			if (Utility.deleteDirectory(new File(zugArchive))) {
				zugArchivePath.mkdirs();
			}
			Utility.copyFile(zipTmpLocation, zugArchive + SLASH + exactDateTime
					+ ".zip");
			System.out.println("Finished creating zip file in : " + zugArchive);
			Utility.deleteDirectory(new File(zipTmpLocation));

			if (productArchivePath.list().length != 0) {
				String zipProductLocation = System.getenv(ZIP_DIR) + SLASH
						+ "Product_" + exactDateTime + ".zip";
				new ZipUtility().zip(productArchive, zipProductLocation);
				if (Utility.deleteDirectory(new File(productArchive))) {
					productArchivePath.mkdirs();
				}
				Utility.copyFile(zipProductLocation, productArchive + SLASH
						+ "Product_" + exactDateTime + ".zip");
				Utility.deleteDirectory(new File(zipProductLocation));
			}

		} catch (Exception e) {
			System.out.println("Exception occured while archiving log on "
					+ archiveLogLocation + ": " + e.getMessage());
		}
	}

	/*
	 * Find the Variables and Values of TestCase running
	 * 
	 * @param test as TestCase object return HashMap
	 */

	private HashMap<String, String> findVariablesValueForTestCase(TestCase test) {
		HashMap<String, String> variablevalueMap = new HashMap<String, String>();
		// message("The finding variables started "+test.testCaseID);
		Action test_action_arr[] = new Action[test.actions.size()];
		test.actions.toArray(test_action_arr);
		for (int i = 0; i < test_action_arr.length; i++) {
			Action testcase_actions = test_action_arr[i];
			// message("the varabless are "+testcase_actions.actionArguments+"\n Action Actual Arguments "+testcase_actions.actionActualArguments);

			if (testcase_actions.actionArguments.size() == testcase_actions.actionActualArguments
					.size()) {
				for (int j = 0; j < testcase_actions.actionArguments.size(); j++) {
					String variable_name = testcase_actions.actionActualArguments
							.get(j);
					// message("the varabless are "+variable_name);
					if (variable_name.startsWith("$$")) {
						variablevalueMap.put(variable_name,
								testcase_actions.actionArguments.get(j));
					} else if (variable_name.contains("=")) {
						// variable_name =
						// Excel.SplitOnFirstEquals(variable_name)[1];
						// message("The value variable "+Excel.SplitOnFirstEquals(variable_name).length);
						variable_name = Excel.SplitOnFirstEquals(variable_name).length > 1 ? Excel
								.SplitOnFirstEquals(variable_name)[1]
								: variable_name;
						if (variable_name.startsWith("$$")) {
							variablevalueMap.put(variable_name,
									testcase_actions.actionArguments.get(j));
						}
					}
				}
			}

		}

		Log.Debug("Controller/findVariablesValueForTestCase: The Variable Map "
				+ variablevalueMap);
		return variablevalueMap;
	}

	/*
	 * Saves the Variables and Values of TestCase running
	 * 
	 * @param test as TestCase object return HashMap
	 */

	private void saveTestCaseVariables(HashMap<String, String> variablemap,
			String testcase_id, String testsuite_name)
			throws DavosExecutionException, InterruptedException {
		Set<String> variable_key_set = variablemap.keySet();
		Iterator<String> var_key_iterate = variable_key_set.iterator();
		// message("error trail coming to here?? ");
		while (var_key_iterate.hasNext()) {
			String variable_key = var_key_iterate.next();
			Log.Debug("Controller/saveTestCaseVariables: The variable key saved "
					+ variable_key
					+ " the value "
					+ variablemap.get(variable_key));
			davosclient.variables_write(testsuite_name, testcase_id,
					variable_key, variablemap.get(variable_key));
		}
	}

	/**
	 * gets the test plan id from Testplan path
	 * 
	 * @param String
	 *            testplanpath : separated as path return String the testplan id
	 */
	private String getTestPlanId(String testplanpath)
			throws DavosExecutionException, InterruptedException {
		String result = null;
		try {
			if (testplanpath.contains(":")) {
				result = davosclient.testplanpath_write(testplanpath);
				return result;
			} else {
				throw new DavosExecutionException(
						"Test plan name Must contain ':'");
			}
		} catch (DavosExecutionException ex) {
			Log.Error("Controller/getTestPlanId:DavosExecutionException "
					+ ex.getMessage());

			result = ex.getLocalizedMessage();
			System.exit(1);
			return result;
		}

	}

	/**
	 * gets the topologyset id from topologysetname
	 * 
	 * @param String
	 *            topologysetname return String the topologysetid
	 */
	private String getTopologySetId(String topologysetname)
			throws DavosExecutionException, InterruptedException {
		String result = null;
		try {
			if (topologysetname.length() > 0) {
				result = davosclient.topologyset_read(topologysetname);
				return result;
			} else {
				throw new DavosExecutionException("Topology Set Name is null");
			}
		} catch (DavosExecutionException x) {
			Log.Error("Controller/getTopologySetId:DavosExecutionException "
					+ x.getMessage());
			result = x.getMessage();
			System.exit(1);
			return result;

		}
	}

	/*
	 * Saves the build tag
	 * 
	 * @param Testplan id and the build tag name
	 */

	private String saveBuildTag(String testplanid, String buildtag)
			throws DavosExecutionException, InterruptedException {
		Log.Debug("Controller/saveBuildTag: TestPlanid " + testplanid
				+ " Build tag " + buildtag);
		try {
			return davosclient.buildtag_write(testplanid, buildtag);
		} catch (DavosExecutionException ee) {
			Log.Error("Controller/saveBuildTag: Exception: " + ee.getMessage());
			System.exit(1);
			return ee.getMessage();
		}
	}

	/***
	 * This will be executed on a separate thread..This will run the expanded
	 * test case.
	 * 
	 * @param testcaseObj
	 */
	private void RunExpandedTestCase(Object testcaseObj) throws Exception,
			DavosExecutionException {
		Log.Debug("Controller/RunExpandedTestCase : Start of Function.");

		TestCase test = (TestCase) testcaseObj;

		Log.Debug("Controller/RunExpandedTestCase : Setting ZUG_TCID as ->"
				+ test.testCaseID);
		// Harness Specific ContextVariable to store Generated TestCase ID
		ContextVar.setContextVar("ZUG_TCID", test.testCaseID);
		Log.Debug("Controller/RunExpandedTestCase : Successfully SET ZUG_TCID as ->"
				+ test.testCaseID);

		// Make sure that the Errors are removed for all the Test Cases at the
		// start.
		errorMessageDuringTestCaseExecution.put(test.parentTestCaseID,
				StringUtils.EMPTY);
		HiPerfTimer tm = new HiPerfTimer();
		try {
			Log.Debug("Controller/RunExpandedTestCase : Running TestCase ID "
					+ test.testCaseID);
			message("******************************************************************************** ");
			message("\nRunning TestCase ID " + test.testCaseID
					+ " On(Current Date): " + Utility.getCurrentDateAsString());

			// Harness Specific ContextVariable to store AH_TCSTARTTIME
			// ZUG Specific ContextVariable to store ZUG_TCSTARTTIME = Timestamp
			// when Test Case execution started
			ContextVar.setContextVar("ZUG_TCSTARTTIME", Utility.dateAsString());
			// Method Return Variable names and Values.

			// If the testCase is not an Init or Cleanup Step then only Save the
			// TestCase Result to the Framework Database.
			if (!(baseTestCaseID.compareToIgnoreCase("cleanup") == 0 || baseTestCaseID
					.compareToIgnoreCase("init") == 0)) {
				ExecutedTestCase tData = new ExecutedTestCase();
				tData.testCaseID = test.testCaseID;
				tData.timeToExecute = 0;
				tData.testCaseExecutionComments = StringUtils.EMPTY;
				tData.testCaseStatus = "running";
				if (StringUtils.isNotBlank(test.testCaseDescription)) {
					tData.testcasedescription = test.testCaseDescription;
				} else {
					tData.testcasedescription = "";
				}
				executedTestCaseData.put(tData.testCaseID, tData);
				// message("RunExpandTest: Description  "+tData.testcasedescription);
				// findVariablesValueForTestCase(test);
				// saveVariables(findVariablesValueForTestCase(test),test.testCaseID);
				// At any cost save the TestCase ID to the result Database.

				// If the testCase is not an Init or Cleanup Step then only
				// Save the TestCase to the Result Database.
				//
				// if (!(baseTestCaseID.compareToIgnoreCase("cleanup") == 0 ||
				// baseTestCaseID.compareToIgnoreCase("init") == 0)) {
				// Log.Debug(String.format("Controller/RunExpandedTestCase : Saving Expanded Testcase ID %s with Description %s to Result Database.",
				// test.testCaseID,
				// test.testCaseDescription));
				//
				// SaveTestCase(test.testCaseID, test.testCaseDescription);
				// Log.Debug(String.format("Controller/RunExpandedTestCase : SUCCESSFULLY SAVED Expanded Testcase ID %s with Description %s to Result Database.",
				// test.testCaseID,
				// test.testCaseDescription));
				//
				// }

				if (dbReporting == true) {
					// If the testCase is not an Init or Cleanup Step then only
					// Save the TestCase to the Result Database.
					if (!(baseTestCaseID.compareToIgnoreCase("cleanup") == 0 || baseTestCaseID
							.compareToIgnoreCase("init") == 0)) {
						Log.Debug(String
								.format("Controller/RunExpandedTestCase : Saving Expanded Testcase ID %s with Description %s to Result Davos.",
										test.testCaseID,
										test.testCaseDescription));
						// SaveTestCase(test.testCaseID,
						// test.testCaseDescription);

						Log.Debug(String
								.format("Controller/RunExpandedTestCase : SUCCESSFULLY SAVED Expanded Testcase ID %s with Description %s to Result Davos.",
										test.testCaseID,
										test.testCaseDescription));
						try {

							// if(!davosclient.validateData())
							// {
							// Log.Error("Davos Reporting Data are not Valid. ");
							// System.exit(1);
							// }

							SaveTestCaseResultEveryTime(tData);

							saveTestCaseVariables(
									findVariablesValueForTestCase(test),
									test.testCaseID, testSuitName);
						} catch (DavosExecutionException de) {
							Log.Error("Failure:: " + de.getMessage());
							System.exit(1);
						}
					} else {
						Log.Debug(String
								.format("Controller/RunExpandedTestCase : Testcase ID %s is of type Initialization/Cleanup",
										test.testCaseID));
					}
				}
			} else {
				Log.Debug(String
						.format("Controller/RunExpandedTestCase : Testcase ID %s is of type Initialization/Cleanup",
								test.testCaseID));
			}

			// Now run each of the Actions mentioned here...and try running it.
			Action[] actions = new Action[test.actions.size()];
			test.actions.toArray(actions);
			Log.Debug("Controller/RunExpandedTestCase:  Number of Actions to run is : "
					+ actions.length + " for TestCase ID : " + test.testCaseID);

			Hashtable<String, String> stepsKeys = new Hashtable<String, String>();
			// After getting the Actions Store the Steps somewhere...
			for (int i = 0; i < actions.length; i++) {
				Action action = actions[i];
				Log.Debug(String
						.format("Controller/RunExpandedTestCase: Storing the Steps in a HashTable. Step Number = "
								+ action.step));
				stepsKeys.put(action.step, StringUtils.EMPTY);
				Log.Debug(String
						.format("Controller/RunExpandedTestCase: Successfully stored Step Number = %s to the HashTable",
								action.step));
			}

			String errorDuringTestCaseExecution = StringUtils.EMPTY;
			// First Clear the Stack.
			Stack<String> actionsForCleanup = new Stack<String>();

			int count = 0;
			ArrayList<Thread> ThreadPool = new ArrayList<Thread>();
			String stepNumber = StringUtils.EMPTY;
			if (actions.length > 0) {
				stepNumber = actions[0].step;
			}

			try {
				for (int i = 0; i < actions.length; i++) {
					Action action = actions[i];
					count++;
					if (StringUtils.isBlank(action.actionName)) {
						continue;
					}

					Log.Debug("Controller/RunExpandedTestCase:  Action : "
							+ action.actionName + " has Step Number as : "
							+ action.step);

					// If the action Steps contain an "i" and there is a cleanup
					// step for it then add that to the stack.
					if (action.step.endsWith("i")
							&& (stepsKeys.containsKey(action.step.substring(0,
									action.step.length() - 1) + "c") || stepsKeys
										.containsKey(action.step.substring(0,
												action.step.length() - 1) + "C"))) {
						Log.Debug("Controller/RunExpandedTestCase:  Action : "
								+ action.actionName
								+ " with Step : "
								+ action.step
								+ " is an Initialization Action. A cleanup exist for this action ..so pusing this to the STACK");
						actionsForCleanup.push(action.step.substring(0,
								action.step.length() - 1));

					}

					// If this is a Cleanup action then break and run the
					// cleanup at the end.
					if (action.step.endsWith("c")
							&& actionsForCleanup.contains(action.step
									.substring(0, action.step.length() - 1))) {
						Log.Debug("Controller/RunExpandedTestCase:  Action : "
								+ action.actionName
								+ " with Step : "
								+ action.step
								+ " is an Cleanup Action/STEP. so breaking and moving to cleanup");
						count--;

						break;
					}

					Log.Debug("Controller/RunExpandedTestCase:  Calling RunAction for Action : "
							+ action.actionName
							+ " for TestCase ID : "
							+ test.testCaseID);

					// System.out.println("stepNumber = " + stepNumber +
					// " action.step = " + action.step );
					// If this step is equivalent to the previous step then
					// Create a Thread and Run the same
					if (stepNumber.equals(action.step)
							&& StringUtils.isNotBlank(action.step)) {
						final Action tempAction = action;
						Thread thread = new Thread(new Runnable() {

							public void run() {
								RunAction(tempAction);
							}
						});
						// thread.IsBackground = true;
						// During the Launching of a new Action....always reset
						// this Value...
						_testStepStopper.put(test.parentTestCaseID, false);

						// Context Variable to store Timestamp when Test Step
						// execution started
						ContextVar.setContextVar("ZUG_TSSTARTTIME",
								Utility.dateAsString());
						thread.start();

						ThreadPool.add(thread);

					} // otherwise Wait for the Previous Threads to Complete and
						// then Start with the new process...
					else {
						// Wait for all the Threads to finish.
						for (int t = 0; t < ThreadPool.size(); ++t) {
							if (debugMode) {
								((Thread) ThreadPool.get(t)).join();
							} else {
								((Thread) ThreadPool.get(t))
										.join(Integer
												.parseInt(ReadContextVariable("ZUG_TESTSTEP_TIMEOUT")) * 1000);
								if (((Thread) ThreadPool.get(t)).isAlive()) {
									_testStepStopper.put(test.parentTestCaseID,
											true);
								}
							}
						}
						// Lastly wait for the Threads running to get Over
						for (int t = 0; t < ThreadPool.size(); ++t) {
							((Thread) ThreadPool.get(t)).join();
						}

						ThreadPool.clear();

						// In case we get an Exception then Dont run any more
						// processes
						if (StringUtils
								.isNotBlank((((String) errorMessageDuringTestCaseExecution
										.get(test.parentTestCaseID))))) {
							// If the action Steps contain an "i" and there is a
							// cleanup step for it then remove that from the
							// stack.

							if (action.step.endsWith("i")
									&& (stepsKeys.containsKey(action.step
											.substring(0,
													action.step.length() - 1)
											+ "c") || stepsKeys
												.containsKey(action.step
														.substring(
																0,
																action.step
																		.length() - 1)
														+ "C"))) {
								actionsForCleanup.pop();
							}

							break;
						}

						// The new step number is the current one
						stepNumber = action.step;
						Thread thread = new Thread(this.getActionImplementer(
								this, action));
						// thread.IsBackground = true;
						// During the Launching of a new Action....always reset
						// this Value...
						_testStepStopper.put(test.parentTestCaseID, false);

						// Context Variable to store Timestamp when Test Step
						// execution started
						ContextVar.setContextVar("ZUG_TSSTARTTIME",
								Utility.dateAsString());

						thread.start();
						ThreadPool.add(thread);

					}

					Log.Debug("Controller/RunExpandedTestCase:  RunAction executed successfully for Action : "
							+ action.actionName
							+ " for TestCase ID : "
							+ test.testCaseID);

				}

				// Wait until oThread finishes. Join also has overloads
				// that take a millisecond interval or a TimeSpan object.
				for (int t = 0; t < ThreadPool.size(); ++t) {
					if (debugMode) {
						((Thread) ThreadPool.get(t)).join();
					} else {
						((Thread) ThreadPool.get(t))
								.join(Integer
										.parseInt(ReadContextVariable("ZUG_TESTSTEP_TIMEOUT")) * 1000);
						if (((Thread) ThreadPool.get(t)).isAlive()) {
							_testStepStopper.put(test.parentTestCaseID, true);
						}

					}

				}

				// Lastly wait for the Threads running to get Over
				for (int t = 0; t < ThreadPool.size(); ++t) {
					((Thread) ThreadPool.get(t)).join();
				}

				if (StringUtils
						.isNotBlank(((String) errorMessageDuringTestCaseExecution
								.get(test.parentTestCaseID)))) {
					Log.ErrorInLog(String
							.format("Controller/RunExpandedTestCase: Exception while running test case %s.%s",
									test.testCaseID,
									(String) errorMessageDuringTestCaseExecution
											.get(test.parentTestCaseID)));
					errorDuringTestCaseExecution = String.format(
							"Exception while running test case %s.%s",
							test.testCaseID,
							(String) errorMessageDuringTestCaseExecution
									.get(test.parentTestCaseID));
				}

			} catch (Exception ex) {
				Log.ErrorInLog(String
						.format("Controller/RunExpandedTestCase: Exception while running test case %s.%s",
								test.testCaseID, ex.toString()));
				errorDuringTestCaseExecution += String.format(
						"Exception while running test case %s.%s",
						test.testCaseID, ex.getMessage());
			} finally {
				boolean cleanupActionStarted = false;
				_testStepStopper.put(test.parentTestCaseID, false);

				Log.Debug("Controller/RunExpandedTestCase:  Number of testcases steps successfully executed are "
						+ count + " for TestCase ID : " + test.testCaseID);
				// In the finally iterate over all the cleanup steps that one
				// would like to perform
				// before the test case is finally said to be done..
				// if ((actionsForCleanup.Count > 0 &&
				// !_testPlanStopper.WaitOne(1, false)) ||
				// (actionsForCleanup.Count > 0 && doCleanupOnTimeout))
				if ((actionsForCleanup.size() > 0 && !_testPlanStopper)
						|| (actionsForCleanup.size() > 0 && doCleanupOnTimeout)) {
					message("\n******************** Cleanup Action For TestCase : "
							+ test.stackTrace
							+ " Started. ***************************");

					for (int i = count; i < actions.length; ++i) {
						// If the Test Plan Timeout and Do CleanuUp on Timeout
						// is not there..
						// then ignore it
						// if (_testPlanStopper.WaitOne(1, false) &&
						// !doCleanupOnTimeout)
						// message("The counts for Cleanup\t"+i);
						// if (!_testPlanStopper && doCleanupOnTimeout) {
						// break;
						// }
						// message("The Actions are\t"+actions[i].actionName);
						Action action = actions[i];

						Log.Debug("Controller/RunExpandedTestCase - Finally/Cleanup:  Working on Action  : "
								+ action.actionName
								+ " for TestCase ID : "
								+ test.testCaseID);

						if (!action.step.endsWith("c") && cleanupActionStarted) {// changes
																					// done
																					// from
																					// cleanupActionStarted
																					// from
																					// !cleanupActionStarted
							try {
								Log.Debug("Controller/RunExpandedTestCase - Finally/Cleanup:  Calling RunAction for Action : "
										+ action.actionName
										+ " for TestCase ID : "
										+ test.testCaseID);

								// If the action Steps contain an "i" and there
								// is a cleanup step for it then add that to the
								// stack.
								if (action.step.endsWith("i")
										&& (stepsKeys
												.containsKey(action.step
														.substring(
																0,
																action.step
																		.length() - 1)
														+ "c") || stepsKeys
												.containsKey(action.step
														.substring(
																0,
																action.step
																		.length() - 1)
														+ "C"))) {
									Log.Debug("Controller/RunExpandedTestCase:  Action : "
											+ action.actionName
											+ " with Step : "
											+ action.step
											+ " is an Initialization Action. A cleanup exist for this action ..so pushing this to the STACK");
									actionsForCleanup.push(action.step
											.substring(0,
													action.step.length() - 1));
								}
								if (stepNumber.equals(action.step)
										&& StringUtils.isNotBlank(action.step)) {
									Thread thread = new Thread(
											this.getActionImplementer(this,
													action));
									// thread.IsBackground = true;

									// During the Launching of a new
									// Action....always reset this Value...
									_testStepStopper.put(test.parentTestCaseID,
											false);

									// Context Variable to store Timestamp when
									// Test Step execution started
									ContextVar.setContextVar("ZUG_TSSTARTTIME",
											Utility.dateAsString());

									thread.start();
									ThreadPool.add(thread);

								} // otherwise Wait for the Previous Threads to
									// Complete and then Start with the new
									// process...
								else {
									// Wait for all the Threads to finish.
									for (int t = 0; t < ThreadPool.size(); ++t) {
										if (debugMode) {
											((Thread) ThreadPool.get(t)).join();
										} else {
											((Thread) ThreadPool.get(t))
													.join(Integer
															.parseInt(ReadContextVariable("ZUG_TESTSTEP_TIMEOUT")) * 1000);

											if (((Thread) ThreadPool.get(t))
													.isAlive()) {
												_testStepStopper.put(
														test.parentTestCaseID,
														true);
											}
										}

									}

									// Lastly wait for the Threads running to
									// get Over
									for (int t = 0; t < ThreadPool.size(); ++t) {
										((Thread) ThreadPool.get(t)).join();
									}

									ThreadPool.clear();

									// The new step number is the current one
									stepNumber = action.step;
									Thread thread = new Thread(
											this.getActionImplementer(this,
													action));
									// thread.IsBackground = true;
									// During the Launching of a new
									// Action....always reset this Value...
									_testStepStopper.put(test.parentTestCaseID,
											false);

									// Context Variable to store Timestamp when
									// Test Step execution started
									ContextVar.setContextVar("ZUG_TSSTARTTIME",
											Utility.dateAsString());
									thread.start();
									ThreadPool.add(thread);

								}

								Log.Debug("Controller/RunExpandedTestCase - Finally/Cleanup:  RunAction executed successfully for Action : "
										+ action.actionName
										+ " for TestCase ID : "
										+ test.testCaseID);
							} catch (Exception e) {
								Log.ErrorInLog(String
										.format("Controller/RunExpandedTestCase - Finally/Cleanup: Exception while running test case %s.%s",
												test.testCaseID, e.getMessage()));
								errorDuringTestCaseExecution += String
										.format("Controller/RunExpandedTestCase - Finally/Cleanup: Exception while running test case %s.%s",
												test.testCaseID, e.getMessage());
							}
						} else if (action.step.endsWith("c")
								&& !actionsForCleanup
										.contains(action.step.substring(0,
												action.step.length() - 1))) {
							Log.Debug("Controller/RunExpandedTestCase - Finally/Cleanup: For Action  : "
									+ action.actionName
									+ " Initialization is not Done. So Skipping it for TestCase ID : "
									+ test.testCaseID);

							continue;
						} else if ((action.step.endsWith("c")
								&& actionsForCleanup
										.contains(action.step.substring(0,
												action.step.length() - 1)) && !_testPlanStopper)
								|| (action.step.endsWith("c")
										&& actionsForCleanup
												.contains(action.step
														.substring(
																0,
																action.step
																		.length() - 1)) && doCleanupOnTimeout)) {
							// This marks the start for one Cleanup Action.
							cleanupActionStarted = true;

							try {
								Log.Debug("Controller/RunExpandedTestCase - Finally/Cleanup:  Calling RunAction for Action : "
										+ action.actionName
										+ " for TestCase ID : "
										+ test.testCaseID);

								if (stepNumber.equals(action.step)
										&& StringUtils.isNotBlank(action.step)) {
									Thread thread = new Thread(
											this.getActionImplementer(this,
													action));
									// thread.IsBackground = true;

									// During the Launching of a new
									// Action....always reset this Value...
									_testStepStopper.put(test.parentTestCaseID,
											false);

									// Context Variable to store Timestamp when
									// Test Step execution started
									ContextVar.setContextVar("ZUG_TSSTARTTIME",
											Utility.dateAsString());

									thread.start();
									ThreadPool.add(thread);

								} // otherwise Wait for the Previous Threads to
									// Complete and then Start with the new
									// process...
								else {
									// Wait for all the Threads to finish.
									for (int t = 0; t < ThreadPool.size(); ++t) {
										if (debugMode) {
											((Thread) ThreadPool.get(t)).join();
										} else {
											((Thread) ThreadPool.get(t))
													.join(Integer
															.parseInt(ReadContextVariable("ZUG_TESTSTEP_TIMEOUT")) * 1000);
											if (((Thread) ThreadPool.get(t))
													.isAlive()) {
												_testStepStopper.put(
														test.parentTestCaseID,
														true);
											}
										}

									}

									// Lastly wait for the Threads running to
									// get Over
									for (int t = 0; t < ThreadPool.size(); ++t) {
										((Thread) ThreadPool.get(t)).join();
									}

									ThreadPool.clear();

									// The new step number is the current one
									stepNumber = action.step;
									Thread thread = new Thread(
											this.getActionImplementer(this,
													action));
									// thread.IsBackground = true;
									// During the Launching of a new
									// Action....always reset this Value...
									_testStepStopper.put(test.parentTestCaseID,
											false);

									// Context Variable to store Timestamp when
									// Test Step execution started
									ContextVar.setContextVar("ZUG_TSSTARTTIME",
											Utility.dateAsString());
									thread.start();
									ThreadPool.add(thread);

								}

								// RunAction(action);
								Log.Debug("Controller/RunExpandedTestCase - Finally/Cleanup:  RunAction executed successfully for Action : "
										+ action.actionName
										+ " for TestCase ID : "
										+ test.testCaseID);
							} catch (Exception exp) {
								Log.ErrorInLog(String
										.format("Controller/RunExpandedTestCase - Finally/Cleanup: Exception while running test case %s.%s",
												test.testCaseID,
												exp.getMessage()));
								errorDuringTestCaseExecution += String
										.format("Controller/RunExpandedTestCase - Finally/Cleanup: Exception while running test case %s.%s",
												test.testCaseID,
												exp.getMessage());
							}
						}
					}

					for (int t = 0; t < ThreadPool.size(); ++t) {
						if (debugMode) {
							((Thread) ThreadPool.get(t)).join();
						} else {
							((Thread) ThreadPool.get(t))
									.join(Integer
											.parseInt(ReadContextVariable("ZUG_TESTSTEP_TIMEOUT")) * 1000);

							if (((Thread) ThreadPool.get(t)).isAlive()) {
								_testStepStopper.put(test.parentTestCaseID,
										true);
							}
						}
					}

					// Lastly wait for the Threads running to get Over
					for (int t = 0; t < ThreadPool.size(); ++t) {
						((Thread) ThreadPool.get(t)).join();
					}
					// message("The total teststepperput\t"+_testStepStopper);
					message("\n******************** Cleanup Action Finished For TestCase : "
							+ test.stackTrace + ". ***************************");
				}
			}
			tm.Stop();
			if (StringUtils.isNotBlank(errorDuringTestCaseExecution)) {
				Log.Debug("Controller/RunExpandedTestCase: There were exception raised while running TestCase = "
						+ test.testCaseID
						+ " with Exception message as : "
						+ errorDuringTestCaseExecution);
				throw new Exception(errorDuringTestCaseExecution);
			}

			// If everything went well then LOG THAT the test case has PASSED.
			Log.Debug(String.format(
					"Controller/RunExpandedTestCase : TestCase ID %s PASSED ",
					test.testCaseID));
			message(String.format(
					"\n\nSTATUS : PASS FOR  TestCase ID %s ",
					test.testCaseID + " On(Current Date): "
							+ Utility.getCurrentDateAsString()));

			// If the testCase is not an Init or Cleanup Step then only Save the
			// TestCase Result to the Framework Database.
			if (!(baseTestCaseID.compareToIgnoreCase("cleanup") == 0 || baseTestCaseID
					.compareToIgnoreCase("init") == 0)) {
				ExecutedTestCase tData = new ExecutedTestCase();
				tData.testCaseCompletetionTime = Utility.dateNow();
				tData.testCaseID = test.testCaseID;
				tData.timeToExecute = (int) tm.Duration();
				tData.testCaseExecutionComments = StringUtils.EMPTY;
				tData.testCaseStatus = "pass";
				// if(!verbose)
				// {
				// showTestCaseResultEveryTime(tData);
				// }
				// And then Add the same at the last.
				executedTestCaseData.put(tData.testCaseID, tData);

				if (dbReporting) {
					// message("Data is going to be saved through davos");
					SaveTestCaseResultEveryTime(tData);
					// message("Data is saved through davos");
				}
			}
		} catch (Exception ex) {
			// String failureReason =
			// String.format("Status FAILED FOR TestCase ID %s. Exception MESSAGE IS : %s .",
			// test.testCaseID,(String)errorMessageDuringTestCaseExecution.get(test.parentTestCaseID)
			// + ex.getMessage());
			String failureReason = String
					.format("Status FAILED FOR Worksheet %s TestCase ID (%s:%s). Exception MESSAGE IS : \n%s .\nCause:%s",
							test.nameSpace, test.parentTestCaseID,
							test.testCaseID, ex.getMessage(), ex.getClass());
			// String failureReason =
			// String.format("Status FAILED FOR TestCase ID %s. Exception MESSAGE IS : %s .",
			// test.testCaseID,(String)errorMessageDuringTestCaseExecution.get(test.parentTestCaseID));
			message("\n******************** Error Messages For Test Case "
					+ test.parentTestCaseID + " ***************************");
			if (verbose)
				Log.Error(failureReason);
			message("\n******************* Error Messages End For Test Case "
					+ test.parentTestCaseID + " **************************");
			message(String.format(
					"\n\nSTATUS : FAIL FOR TestCase ID %s ",
					test.testCaseID + " On(Current Date): "
							+ Utility.getCurrentDateAsString()));

			// If the testCase is not an Init or Cleanup Step then only Save the
			// TestCase Result to the Framework Database.
			if (!(baseTestCaseID.compareToIgnoreCase("cleanup") == 0 || baseTestCaseID
					.compareToIgnoreCase("init") == 0)) {
				ExecutedTestCase tData = new ExecutedTestCase();
				tData.testCaseCompletetionTime = Utility.dateNow();
				tData.testCaseID = test.testCaseID;
				tData.timeToExecute = (int) tm.Duration();
				// tData.testCaseExecutionComments =
				// (String)errorMessageDuringTestCaseExecution.get(test.parentTestCaseID)
				// + ex.getMessage();
				tData.testCaseExecutionComments = ex.getMessage();
				tData.testCaseStatus = "fail";
				// if(!verbose)
				// {
				// showTestCaseResultEveryTime(tData);
				// }
				// And then Add the same at the last.

				executedTestCaseData.put(tData.testCaseID, tData);

				if (dbReporting) {
					SaveTestCaseResultEveryTime(tData);
				}
			} else {
				initWorkedFine = false;
			}

		} finally {
			message("********************************************************************************** ");

		}
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
	
	/**
	 * main method for Harness. Entry point to the Controller
	 * 
	 * @param args
	 *            -Command line parameters for harness.
	 */
	public static void main(String[] args) throws InterruptedException,
			Exception, DavosExecutionException, MoleculeDefinitionException {

		// Getting Operating System Information
		OS_NAME = System.getProperty("os.name");
		OS_ARCH = System.getProperty("os.arch");
		OS_VERSION = System.getProperty("os.version");
		// Getting Java software used informations
		JavaVersion = System.getProperty("java.version");
		JavaCompiler = System.getProperty("java.compiler");
		JavaHome = System.getProperty("java.home");
		JavaLibraryPath = System.getProperty("java.library.path");
		// mvmconfiguration=Utility.getPhysicalMemory();
		mvmconfiguration = Utility.getMaxJVMMemorySize(Runtime.getRuntime())
				.split("\\.")[0];
		// System.out.println("The config "+mvmconfiguration);

		if (OS_NAME.toLowerCase().contains("windows")) {
			PATH_CHECK = "Path";
			SEPARATOR = ";";
			SLASH = "\\";
			LOG_DIR = "APPDATA";
			OS_FLAG = true;
			ZIP_DIR = "TEMP";

		} else if (OS_NAME.equalsIgnoreCase("linux")) {
			PATH_CHECK = "PATH";
			SEPARATOR = ":";
			SLASH = "/";
			LOG_DIR = "HOME";
			OS_FLAG = false;
			ZIP_DIR = "HOME";

		} else {
			Log.Error("Not Supporting Operating System\t" + OS_NAME);
			System.exit(1);
		}
		LOGLOCATION = System.getenv(LOG_DIR);
		if (LOGLOCATION == null) {
			LOGLOCATION = System.getProperty("user.dir") + "/log";
		}
		final Controller controller = new Controller();
		StringBuilder cmdinputsargs = new StringBuilder();
		for (String cmdinputs : args) {
			cmdinputsargs.append(cmdinputs);
		}
		Log.Debug("Controller/Main:: Command Line Input: "
				+ cmdinputsargs.toString());

		// controller.LoggedInUser();
		// geting the process id of the program
		// System.out.println(System.getProperty("java.library.path"));

		// Checking for jar file entry in ZugINI.xml
		if (new ExtensionInterpreterSupport().reteriveXmlTagAttributeValue(
				inprocess_jar_xml_tag_path,
				inprocess_jar_xml_tag_attribute_name).length > 0) {
			int c = 0;
			for (String package_names : new ExtensionInterpreterSupport()
					.reteriveXmlTagAttributeValue(inprocess_jar_xml_tag_path,
							inprocess_jar_xml_tag_attribute_name)) {
				if(StringUtils.isNotBlank(package_names)||StringUtils.isNotEmpty(package_names))
				{
					AtomInvoker ai = new AtomInvoker(package_names);
				
				if (ai.native_flag) {
				 //controller.message("This is native Dll Calling");
				} else if (ai.com_flag) {
					// controller.message("Com Jacob Calling");
			} else {
				//controller.message("invoking jar instance "+package_names);
					ai.loadInstance(package_names);
				}
				invokeAtoms.put(package_names, ai);
			}else
			{
				controller.message("[Warning] ZugINI.xml contains blank inprocess package definition. Please refer to the readme.txt or Zug User Manual");
			}
			}
			
			// invokeAtoms.loadJarFile(new
			// ExtensionInterpreterSupport().readExternalJarFilePath().get(0));
		} else {
			controller
					.message("No Inprocess Jar definition found in ZugINI.xml with proper Attribute definition for Tag");
		}

		Controller.harnessPIDValue = Integer
				.parseInt((java.lang.management.ManagementFactory
						.getRuntimeMXBean().getName().split("@"))[0]); // ProcessMonitorThread.currentThread().getId();

		// First Validate the Command Line Arguments
		if (args.length > 1) {
			for (String arg : args) {
				// controller.message("The argumentss "+arg);
				if (arg.equalsIgnoreCase("-nyon")) {
					nyonserver = true;
					controller.message("Nyon-Server executions\t" + nyonserver);
				} else if (arg.toLowerCase().contains("-mvmconfiguration=")) {
					String mvmconfig_arr[] = arg.split("=");
					mvmconfiguration = mvmconfig_arr[1].toLowerCase();
					controller.message("MVM Configuration is set to "
							+ mvmconfiguration);
				}

			}
		}

		try {
			// controller.message("\n\nValidating Command Line Arguments");
			// Jacob Test
			// ContextVar.setContextVar("testing","testingvalue");
			// ActiveXComponent test = new
			// ActiveXComponent("Automature.ZugAPI");
			// int
			// response=Dispatch.call(test,"AlterContextVar","testing","newvalue").toInt();
			// controller.message("Response "+response);
			// controller.message("Ctx value "+ContextVar.getContextVar("testing"));

			Log.Debug("Controller/Main : Calling ProgramOptions.parse() to Parse program argument");
			controller.opts = ProgramOptions.parse(args);

			if (controller.opts.isHelpRequest()) {
				controller.PrintUsage();
				return;
			}

			if (controller.opts.isConfigRequest()) {
				controller.printConfig();
				return;
			}

			if (controller.opts.isVersionRequest()) {
				// if(OS_FLAG) {
				try {
					com.automature.zug.license.LicenseValidator licenseValid = new com.automature.zug.license.LicenseValidator();
					if (licenseValid.matchMac() == false) {
						controller
								.message("Please get a valid license for your machine");
						System.exit(1);
					}
					if (licenseValid.isDateValid() == false) {
						controller
								.message("The License of ZUG has expired. Please renew. \n\tVisit www.automature.com");
						System.exit(1);
					}
					controller.message("Zug is Valid "
							+ licenseValid.userInfo.companyName);

				} catch (Exception e) {
					controller.message("Failed to validate your License copy");
					controller.message("Message : " + e.getMessage() + "\n");
					System.exit(1);
				}

				controller.PrintVersionInformation();
				return;
			}

			Log.Debug("Controller/Main : Getting and Validating Command Line Arguments.");

			
			if (!controller.GetOptions()) {

				return;
			}
			controller.message("\n\nCommand Line Arguments Validated \n");
		} catch (Exception e) {
			controller
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
				controller
						.message("Please get a valid license for your machine");
				System.exit(1);
			}
			if (licenseValid.isDateValid() == false) {
				controller
						.message("The License of ZUG has expired. Please renew. \n\tVisit www.automature.com");
				System.exit(1);
			}
			controller.message("Zug is Valid "
					+ licenseValid.userInfo.companyName);
			if (!verbose) {
				System.out.println("Zug is Valid "
						+ licenseValid.userInfo.companyName);
			}

		} catch (Exception e) {
			controller.message("Failed to validate your License copy");
			controller.message("Message : " + e.getMessage() + "\n");
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
			Log.Debug("Reading the Excel Sheet = " + controller.inputFile);
			controller.message("Reading the TestCases Input Sheet  "
					+ controller.inputFile + ".\n");
			// Now reading the Excel object.
			controller.readExcel = new Excel();
			controller.readExcel.setXlsFilePath(controller.inputFile);
			
controller.CreateContextVariable("ZUG_BWD="+new File(controller.readExcel.getXlsFilePath()).getParent());
//System.out.println("The base working directory "+ContextVar.getContextVar("ZUG_PWD"));
			controller.readExcel.ReadExcel(controller.inputFile,
					verificationSwitching, compileMode);
			controller.message("SUCCESSFULLY Read the TestCases Input Sheet "
					+ controller.inputFile);
			if (!verbose) {
				System.out
						.println("SUCCESSFULLY Read the TestCases Input Sheet "
								+ controller.inputFile);
			}

			if (Controller.compileMode) {
				if (controller.readExcel.CompileTimeErrorMessage() != null) {
					controller
							.message("\n******************** START: Compile Time Errors Present in the Test Design Sheet. ***************************");
					controller.message(controller.readExcel
							.CompileTimeErrorMessage());
					controller
							.message("\n******************** END: Compile Time Errors Present in the Test Design Sheet. ***************************");

				} else {
					controller
							.message("\n******************** There are no Syntax errors in the Test Design Sheet. ***************************");
				}
				controller.DoHarnessCleanup();
				return;
			}

			// Initializing some of these Variables so that the controller can
			// do useful work
			Log.Debug("Controller/Main : Initializing the Controller Variables after reading the Excel sheet. Calling controller.InitializeVariables");
			controller.InitializeVariables(controller.readExcel);
			Log.Debug("Controller/Main : Initialized the Controller Variables after reading the Excel sheet.");
			// reporting_server_add = controller.readExcel.DBHostName();

			if (Controller.dbReporting == true) {
				// XMLUtility utility = new XMLUtility();
				Log.Debug("Controller/InitializeVariables : Getting the TopologySet");
				// controller.TopologySet =
				// utility.ReadTopologySetInformation(controller.topologySetXMLFile);
				Log.Debug("Controller/InitializeVariables : Successfully got the TopologySet from the Excel Sheet");

				// controller.message("Connecting to the Davos : " +
				// controller.dBName + " of Host "+ controller.dBHostName +
				// " with User "+ controller.dbUserName);

				if (!controller.ConnectToDavos()) {
					controller
							.message("\nError Connecting to Davos. Controller Exiting ");
					controller.DoHarnessCleanup();
					return;
				}

				// If the Validation is successful. ... then its the turn of the
				// Test Plan and Test Cases to be Inserted to the Database.

				if (!controller.ValidateDatabaseEntries()) {
					controller
							.message("\nInvalid Entries provided. Controller Exiting Gracefully..... ");
					controller.DoHarnessCleanup();
					return;
				}
				controller.message("Connection to Davos is successful.\n ");
				if (!verbose) {
					System.out.println("Connection to Davos is successful.\n ");
				}
				// First task is to insert the test suite to the Database.
				// controller.SaveTestSuite();
				if (StringUtils.isNotEmpty(controller.TestPlanPath)
						&& StringUtils.isBlank(controller.TestPlanId)) {
					// controller.message("THe testplan path is "+controller.TestPlanPath);
					controller.TestPlanId = controller
							.getTestPlanId(controller.TestPlanPath);

				}
				if (StringUtils.isNotEmpty(controller.TopologySetName)
						&& StringUtils.isBlank(controller.topologySetId)) {
					controller.topologySetId = controller
							.getTopologySetId(controller.TopologySetName);

				}
				// controller.message("The build tag "+controller.BuildTag );
				if (StringUtils.isNotEmpty(controller.BuildTag)) {

					controller.BuildNo = controller.saveBuildTag(
							controller.TestPlanId, controller.BuildTag);
				}
				try{
					davosclient.validate_Testplan(controller.TestPlanId);
					davosclient.validate_Topologyset(controller.topologySetId);
				}catch(DavosExecutionException e)
				{
					Log.Error("Error in Validating Data: "+e.getMessage());
					System.exit(1);
				}
				
				
				
				if(StringUtils.isNotBlank(controller.testCycleId)||StringUtils.isNotEmpty(controller.testCycleId))
				{
					if(davosclient.getTestplanFromTestCycleID(controller.testCycleId)!=controller.TestPlanId)
					{
						Log.Error(String.format("Testcycle: %s is not mapped to Testplan: %s",controller.testCycleId,controller.TestPlanId));
						System.exit(1);
						
					}
				}
			}

			// Now run the test-case one by one -
			final TestCase[] testcases = controller.readExcel.TestCases();

			// Read the Abstract Test Cases from the Sheet
			controller.abstractTestCase = controller.readExcel
					.AbstractTestCases();

			initializationTime.Stop();
			// controller.initializationTime =
			// (int)(initializationTime.Duration()/ (double)1000);
			controller.initializationTime = (int) (initializationTime
					.Duration());

			controller
					.message("\n******************************************************************************** ");
			controller
					.message("\nTotal time taken to initialize the Harness is -> "
							+ controller.initializationTime + " milli Seconds.");
			controller
					.message("\n******************************************************************************** ");
			if (!verbose) {
				System.out
						.println("\n******************************************************************************** ");
				System.out
						.println("\nTotal time taken to initialize the Harness is -> "
								+ controller.initializationTime
								+ " milli Seconds.");
				System.out
						.println("\n******************************************************************************** ");
			}
			Thread thread = new Thread(new Runnable() {

				public void run() {
					try {
						try {
							// TODO update the current threadid
							controller.RunTestCaseForMain(testcases);
							if (controller.dbReporting == true) {
								davosclient.heartBeat(sessionid);
							}
						} catch (DavosExecutionException ex) {
							String error = "Davos Exception occured during running test cases for main, exception is\n"
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

			if (!debugMode) {
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
			if (Controller.dbReporting == true) {

				if (controller.executedTestCaseData.size() > 0) {
					// Now everything is done except storing the TestCase Data
					// to Result Data..so doing that now...
					controller.message("\n\nStoring the TestCase Result to "
							+ controller.dBHostName + "\\" + controller.dBName
							+ " Davos.....");

					try {
						controller.SaveTestCaseResult();
					} catch (DavosExecutionException de) {
						Log.Error(de.getMessage());
						System.exit(1);

					}

					
					controller
							.message("\n\nSUCCESSFULLY Stored the TestCase Result to "
									+ controller.dBHostName
									+ "\\"
									+ controller.dBName + " Davos.....");
					
				}
				else
				{
					controller.message("\n\n Updating testcycle data with no test execution data");
					try{
						controller.SaveTestCaseResult();
						controller.message("\n\nSUCCESSFULLY Stored the TestCycle Result to "
								+ controller.dBHostName
								+ "\\"
								+ controller.dBName + " Davos.....");
			
					}catch(DavosExecutionException de)
					{
						Log.Error(de.getMessage());
						System.exit(1);
					}
					
					
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

		controller
				.message("\n******************************************************************************** ");
		controller
				.message("\nTotal time taken to execute all the test cases (End to End) is -> "
						+ controller.executionTime + " milli Seconds.");
		controller
				.message("\n******************************************************************************** ");
		if (!verbose) {
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

	public ActionImplementer getActionImplementer(Controller t, Action action) {
		return new ActionImplementer(t, action);
	}

	class ActionImplementer extends Thread {

		Action action;
		Controller t;

		ActionImplementer(Controller t, Action action) {
			this.t = t;
			this.action = action;
		}

		public void run() {
			RunAction(action);
		}
	}
}
