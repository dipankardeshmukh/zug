/***
 * Excel.java
 *	This is the Excel class which reads the input TestCases from the Excel sheet.
 * 
 */
package com.automature.zug.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jline.ConsoleReader;
//Internal Imports
import com.automature.zug.util.Log;
import com.automature.zug.util.XMLWriter;
import com.automature.zug.engine.Controller;
import com.automature.zug.exceptions.MoleculeDefinitionException;
import com.automature.zug.util.ExtensionInterpreterSupport;
import com.automature.zug.util.Utility;

import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class Excel {

	// Objects for reading the input EXCEL file
	private HSSFWorkbook _workBook = null;
	static String mainNameSpace = StringUtils.EMPTY;
	private static Boolean verificationSwitching = true;
	private static Boolean compileModeFlag = false;
	private static String errorInTheSheet = StringUtils.EMPTY;
	private static String ConfigSheetName = "Config";
	private static String MacroSheetName = "Macros";
	// private static String UserSheetName = "Users";
	private static String TestCaseSheetName = "TestCases";
	private static String AbstractSheetName = "Molecules";
	private static String PrototypeSheetName = "Prototypes";
	public static final String NEGATIVE = "negative";
	private static int counter;
	static XMLWriter xmlfile;
	private String xlsfilepath = StringUtils.EMPTY;
	// DS to hold file names and their optional namespace
	private HashMap<String, String> namespaceMap = new HashMap<String, String>();
	// Variable to store total number of Action and Verification Arguments.
	private int TotalActionArgs = 0;
	private int TotalVerificationArgs = 0;
	private Double default_cardinality = new Double(10);
	// An Array of string to hold the config sheet's keys
	private String[] _configSheetKeys = new String[] { "ScriptLocation",
			"DBHostName", "DBName", "DBUserName", "DBUserPassword",
			"Test Suite Name", "Test Suite Role", "TestPlan Id",
			"Test Cycle Id", "ProductLogLocations", "Include", "ValidTopos","TestFramework"};

	// A hash table to store the Values(Name/Value pair) from the Configuration
	// Sheet
	private Hashtable<String, String> _configSheetHashTable = new Hashtable<String, String>();

	private List<String> _externalSheets = new ArrayList<String>();
	private static HashMap<String, String> externalSheets = new HashMap<String, String>();
	// A hashtable to store the Values(Name/Value pair) from the Macro Sheet
	 static Hashtable<String, String> _macroSheetHashTable = new Hashtable<String, String>();
	public static Hashtable<String, String> _indexedMacroTable = new Hashtable<String, String>();
	// A hashtable to store the UserName and its Object contain its credentials
	// from the User Sheet
	// private Hashtable<String,UserData> _userSheetHashTable = new
	// Hashtable<String,UserData>();
	// ArrayList to Store the TestCases Object that the Controller needs to
	// Execute.
	private ArrayList<TestCase> testCases = new ArrayList<TestCase>();
	// Hashtable to store the "testcase name/id" and "The testcase Object"
	private Hashtable<String, Molecule> abstractTestCase = new Hashtable<String, Molecule>();
	// / Hashtable to store the "prototype name" and "The Prototype Object"
	private Hashtable<String, Prototype> protoTypesHT = new Hashtable<String, Prototype>();
	List<String> _productLogFiles = new ArrayList<String>();
	//Tags for reading from INI
	private static String dbHostName="//root//configurations//dbhostname";
	private static String dbName="//root//configurations//dbname";
	private static String dbUserName="//root//configurations//dbusername";
	private static String dbUserPassword="//root//configurations//dbuserpassword";
	private static String scriptLocationsTag="//root//configurations//scriptlocation";

	public Excel(){
		
	}
	
	/**
	 * 
	 * @return array of configuration keys
	 */
	public String[] ConfigSheetKeys() {
		return _configSheetKeys;
	}

	// /returns array of test case's objects
	public TestCase[] TestCases() {
		if (testCases.size() == 0) {
			return new TestCase[0];
		} else {
			TestCase[] tempTestcase = new TestCase[testCases.size()];
			testCases.toArray(tempTestcase);
			return tempTestcase;
		}
	}

	/**
	 * @return compile time error message
	 */
	public String CompileTimeErrorMessage() {
		return errorInTheSheet;
	}

	/**
	 * @return the Abstract TestCases.
	 */
	public Hashtable<String, Molecule> AbstractTestCases() {
		return abstractTestCase;
	}

	/**
	 * @return the Prototypes hashtable<String, Prototype>
	 */
	public Hashtable<String, Prototype> Prototypes() {
		return protoTypesHT;
	}

	/**
	 * @return testplan timeout
	 */
	public int TESTPLAN_TIMEOUT() {
		try {
			int timeout;
			if ((timeout = Integer.parseInt((String) _macroSheetHashTable
					.get(AppendNamespace("TESTPLAN_TIMEOUT", mainNameSpace)))) == 0) // Timeout
																						// in
																						// milliSecond.
			{
				return timeout;
			} else {
				return 4 * 60 * 60;
			}
		} finally {
			return 4 * 60 * 60;
		}
	}

	/**
	 * @return teststep timeout
	 */
	public int TESTSTEP_TIMEOUT() {

		try {
			int timeout;
			if ((timeout = Integer.parseInt((String) _macroSheetHashTable
					.get(AppendNamespace("TESTSTEP_TIMEOUT", mainNameSpace)))) == 0) // Timeout
																						// in
																						// milliSecond.
			{
				return timeout;
			} else {
				return 60 / 2 * 60;
			}
		} finally {
			return 60 / 2 * 60;
		}
	}

	/**
	 * @return the xlsfilepath
	 */
	public String getXlsFilePath() {
		return xlsfilepath;
	}

	/**
	 * @param xlsfilepath
	 *            the xlsfilepath to set
	 */
	public void setXlsFilePath(String xlsfilepath) {
		this.xlsfilepath = xlsfilepath;
	}

	/**
	 * @return ScriptLocation
	 */
	public String ScriptLocation() {
		String scriptLocation = _configSheetHashTable.get(_configSheetKeys[0]) != null ? (String) _configSheetHashTable
				.get(_configSheetKeys[0]) : null;
		String iniSL=null;
		try{
			iniSL=ExtensionInterpreterSupport.getNode(scriptLocationsTag);//.getNodesValues(scriptLocationsTag);
		}catch(Exception e){
			
		}
		if(scriptLocation==null)
			scriptLocation=iniSL;
		else if(iniSL!=null && !iniSL.isEmpty())
			scriptLocation+=";"+iniSL+";";
		return scriptLocation;		
	}

	public String IncludeMolecules() {

		return _configSheetHashTable.get(_configSheetKeys[10]) != null ? (String) _configSheetHashTable
				.get(_configSheetKeys[10]) : null;

	}

	/**
	 * @return the value of Database Host Name
	 */
	public String DBHostName() {
		String dbHostName = null;
		try {
			if (_configSheetHashTable.get(_configSheetKeys[1]) != null) {
				dbHostName = (String) _configSheetHashTable
						.get(_configSheetKeys[1]);
			}

			if (StringUtils.isBlank(dbHostName) && Controller.opts.dbReporting) {
				dbHostName=ExtensionInterpreterSupport.getNode(Excel.dbHostName);
				if(dbHostName==null||dbHostName.equals("")||dbHostName.isEmpty() ){
					Log.Error("Exception:Excel/DBHostName-dbhostname not provided in test suite and also in INI file.Exiting ZUG");
					System.exit(0);
				}
			
			}
		} catch (IOException e) {
			
		}catch(Exception e){
			Log.Error("Excel/DBHostName:"+e.getMessage());
		}
		//System.out.println("dbHostName"+dbHostName);
		return dbHostName;
	}

	public String DBName() {
		String dbName = null;
		try {
			
			if (_configSheetHashTable.get(_configSheetKeys[2]) != null) {
				dbName = (String) _configSheetHashTable
						.get(_configSheetKeys[2]);
			}else if(_configSheetHashTable.get(_configSheetKeys[_configSheetKeys.length-1]) != null) {
				dbName = (String) _configSheetHashTable
						.get(_configSheetKeys[_configSheetKeys.length-1]);
			}
			if (StringUtils.isBlank(dbName) && Controller.opts.dbReporting) {
			
				dbName=ExtensionInterpreterSupport.getNode(Excel.dbName);
				if(dbName==null||dbName.equals("")){
					Log.Error("Excel/DBName:Exception:-dbname not provided in test suite and also in INI file.Exiting ZUG");
					System.exit(0);
				}
						}
		} catch (IOException e) {
			
		}catch(Exception e){
			Log.Error("Excel/DBName:"+e.getMessage());
		}
		return dbName;
	}
	/**
	 * @return the value of Database Name
	 */
	

	/**
	 * @return the value of Database User Name
	 */
	public String DBUserName() {
		String dbUserName = null;
		try {
			if (_configSheetHashTable.get(_configSheetKeys[3]) != null) {
				dbUserName = (String) _configSheetHashTable
						.get(_configSheetKeys[3]);
			}
			if (StringUtils.isBlank(dbUserName) && Controller.opts.dbReporting) {
				dbUserName=ExtensionInterpreterSupport.getNode(Excel.dbUserName);
				if(dbUserName==null||dbUserName.equals("")){
					Log.Error("Excel/DBUserName:Exception:-dbUsername not provided in test suite and also in INI file.Exiting ZUG");
					System.exit(0);
				}
				
			}
		} catch (IOException e) {
		}catch(Exception e){
			Log.Error("Excel/DBUserName:"+e.getMessage());
		}
		
		return dbUserName;
	}

	/**
	 * @return the value of Database User Password to connect to the Database.
	 */
	public String DBUserPassword() {
		String dbUserPassword = null;
		try {
			if (_configSheetHashTable.get(_configSheetKeys[4]) != null) {
				dbUserPassword = (String) _configSheetHashTable
						.get(_configSheetKeys[4]);
			}

			if (StringUtils.isBlank(dbUserPassword) && Controller.opts.dbReporting) {
				dbUserPassword=ExtensionInterpreterSupport.getNode(Excel.dbUserPassword);
				if(dbUserPassword==null||dbUserPassword.equals("")){
					Log.Error("Excel/DBUserPassword:Exception:-dbUserPassword not provided in test suite and also in INI file.Exiting ZUG");
					System.exit(0);
				}
			
			}
		} catch (Exception e) {
			Log.Error("Excel/DBUserPassword:"+e.getMessage());
		}

		return dbUserPassword;
	}

	/**
	 * @return the Test Plan Name.
	 */
	public String TestSuitName() {
		String testSuitName = null;
		try {
			ConsoleReader reader = new ConsoleReader();
			if (_configSheetHashTable.get(_configSheetKeys[5]) != null) {
				testSuitName = (String) _configSheetHashTable
						.get(_configSheetKeys[5]);
			}

			up: while (true) {
				if (StringUtils.isBlank(testSuitName)) {
					testSuitName = reader
							.readLine("\nEnter the Test Suite Name : ");
					continue up;
				} else {
					_configSheetHashTable.put(_configSheetKeys[5],
							testSuitName.trim());
					break up;
				}
			}
		} catch (Exception e) {
		}
		return testSuitName;
	}

	/**
	 * @return the Test Plan Role.
	 * 
	 */
	public String TestSuitRole() {
		String testSuitRole = null;
		try {
			ConsoleReader reader = new ConsoleReader();
			if (_configSheetHashTable.get(_configSheetKeys[6]) != null) {
				testSuitRole = (String) _configSheetHashTable
						.get(_configSheetKeys[6]);
			}

			up: while (true) {
				if (StringUtils.isBlank(testSuitRole)) {
					testSuitRole = reader
							.readLine("\nEnter valid Test Suite Role (like client, server, etc). ");
					continue up;
				} else {
					_configSheetHashTable.put(_configSheetKeys[6],
							testSuitRole.trim());
					break up;
				}
			}
		} catch (IOException e) {
		}
		return testSuitRole;
	}

	/**
	 * @return the testplan Id.
	 * 
	 */
	public String TestPlanID() {
		String testplanID = null;
		try {
			ConsoleReader reader = new ConsoleReader();
			if (_configSheetHashTable.get(_configSheetKeys[7]) != null) {
				testplanID = (String) _configSheetHashTable
						.get(_configSheetKeys[7]);
			}

			up: while (true) {
				if (StringUtils.isBlank(testplanID)) {
					testplanID = reader
							.readLine("\nEnter an existing Testplan ID from the TestPlan Table.\n \nYou can use the Upload Tool to Create an TestPlan, if you want to");
					continue up;
				} else {
					_configSheetHashTable.put(_configSheetKeys[7],
							testplanID.trim());
					break up;
				}
			}
		} catch (IOException e) {
		}
		return testplanID;
	}

	/**
	 * 
	 * @return Return the Test Cycle Id.
	 */
	public String TestCycleId() {
		String testCycleId = null;
		try {
			ConsoleReader reader = new ConsoleReader();
			if (_configSheetHashTable.get(_configSheetKeys[8]) != null) {
				testCycleId = (String) _configSheetHashTable
						.get(_configSheetKeys[8]);
			}

			up: while (true) {
				if (StringUtils.isBlank(testCycleId)) {
					testCycleId = reader
							.readLine("\nEnter the Test Cycle to Use (Can be a new one or an already existing one)");
					continue up;
				} else {
					_configSheetHashTable.put(_configSheetKeys[8],
							testCycleId.trim());
					break up;
				}
			}
		} catch (IOException e) {
		}
		return testCycleId;
	}

	/**
	 * 
	 * @return Get product log files path.
	 */
	public String[] ProductLogFiles() {
		String applicationLogLocation = null;
		if (_configSheetHashTable.get(_configSheetKeys[9]) == null) {
			// return array of zero elements.
			return new String[0];
		}
		applicationLogLocation = (String) _configSheetHashTable
				.get(_configSheetKeys[9]);
		String[] qualifiedPaths = applicationLogLocation.split(",");

		for (int i = 0; i < qualifiedPaths.length; i++) {
			File file = new File(qualifiedPaths[i]);
			if (file.isAbsolute() && file.isFile()) {
				// path represent product log files path.
				_productLogFiles.add(qualifiedPaths[i]);
			}
		}
		String[] productFiles = new String[_productLogFiles.size()];
		_productLogFiles.toArray(productFiles);
		return productFiles;
	}

	public String[] ExternalSheets() {
		String[] externalSheetArray = null;
		_externalSheets.toArray(externalSheetArray);
		return externalSheetArray;
	}

	public static String[] getExternalSheets() {
		String[] externalSheetArray = new String[externalSheets.size()];
		int i = 0;
		for (String key : externalSheets.keySet()) {
			String ele = externalSheets.get(key);
			// System.out.println(ele);
			externalSheetArray[i++] = ele;
		}
		return externalSheetArray;
	}

	// / Returns Valid Topo detail.
	public String ValidTopoDetail() {
		if (_configSheetHashTable.get(_configSheetKeys[11]) == null) {
			return null;
		} else {
			return (String) _configSheetHashTable.get(_configSheetKeys[11]);
		}
	}

	/*
	 * function to find the ClosetValue in a list
	 * 
	 * @param value,range_list
	 * 
	 * double,List<Double>
	 * 
	 * return closetValue
	 * 
	 * double
	 */

	private double closestValue(double value, List<Double> range_list) {
		double min = Double.MAX_VALUE;
		double closest = value;
		Collections.sort(range_list);

		for (double v : range_list) {
			final double diff = v - value;

			if (Math.abs(diff) < min && diff <= 0) {
				min = diff;
				closest = v;
			}
		}

		return closest;
	}

	/***
	 * Function to check the optimality of ZUG to execute expanded Testcases
	 * 
	 * @param memorysize
	 *            memorysize as a String
	 * 
	 * @param cardinalnumber
	 *            cardinalnumber as String
	 * 
	 * @return boolean
	 */
	private boolean checkOptimalityForCartesianProduct(String memorysize,
			String cardinalnumber) throws Exception {
		boolean checkOptimality = false;
		double ratio, memory, cardinality, xml_memory, xml_cardinality;
		memorysize = memorysize.split("\\.")[0];
		memory = new Double(memorysize);

		cardinality = new Double(cardinalnumber);
		// ratio = memory / cardinality;
		// String xml_memory_size =new
		// ExtensionInterpreterSupport().readConfigurationForCartestisanProduct(Controller.mvmconfiguration.toLowerCase()).get(Controller.mvmconfiguration.toLowerCase()).get(0);
		// String xml_mvm_cardinality =new
		// ExtensionInterpreterSupport().readConfigurationForCartestisanProduct(Controller.mvmconfiguration.toLowerCase()).get(Controller.mvmconfiguration.toLowerCase()).get(1);

		// System.out.println("THE MEMORY of MACHINE " + memorysize +
		// " The NUMBER of MVM in CHUR " + cardinalnumber);
		// System.out.println("THE MEMORY of XML " + xml_memory_size +
		// " The NUMBER of MVM in XML " +
		// xml_mvm_cardinality+" config "+Controller.mvmconfiguration);
		HashMap<String, String> temp_map = new ExtensionInterpreterSupport()
				.readConfigurationForCartestisanProduct();
		Set<String> temp_map_key = temp_map.keySet();
		Iterator<String> set_key_iterator = temp_map_key.iterator();
		while (set_key_iterator.hasNext()) {

			String xml_memory_size = set_key_iterator.next();
			xml_memory = new Double(xml_memory_size);
			// System.out.println("The Value:: "+closestValue(memory,ExtensionInterpreterSupport.jvm_max_memory_list));
			if (xml_memory_size.equalsIgnoreCase(memorysize)
					|| xml_memory == closestValue(memory,
							ExtensionInterpreterSupport.jvm_max_memory_list)) {

				xml_cardinality = new Double(temp_map.get(xml_memory_size));

				if (memory < xml_memory && memory > 1) {
					xml_cardinality = default_cardinality;// java max memory
															// lowest than the
															// definitions.....
					if (cardinality <= xml_cardinality) {
						checkOptimality = true;
					} else
						checkOptimality = false;

				} else if (memory >= xml_memory
						&& cardinality <= xml_cardinality) {
					checkOptimality = true;
				} else {
					checkOptimality = false;
					Log.Error("Excel/checkOptimalityForCartesianProduct: ZugINI.xml memory configuration is invalid: Not enough memory ");
				}
				break;
			}

		}

		// if (ratio > 10) {
		// checkOptimality = true;
		// } else {
		// checkOptimality = false;
		// }

		// if (memory >= xml_memory && cardinality <= xml_cardinality) {
		// checkOptimality = true;
		// } else {
		// checkOptimality = false;
		// }

		return checkOptimality;
	}

	/***
	 * Function to find the cardinality of the macro values
	 * 
	 * @param macroList
	 *            macroList as a Hashtable<?,?>
	 * 
	 * @return int
	 */
	private int getCardinalityForMVM(Hashtable<?, ?> macroList) {
		int cardinalNumber = 1, count;
		Set<?> tableKey = macroList.keySet();
		for (Object macros : tableKey) {
			if (macros.toString().startsWith("$$")) {
				String macro_arr[] = macroList.get(macros).toString()
						.split(",");
				// System.out.println("The array value "+macro_arr[0]+"----"+macro_arr[1]);
				if (macro_arr[0].startsWith("{$$")
						|| macro_arr[0].startsWith("#")) {
					continue;
				} else {
					count = macro_arr.length;
				}
				cardinalNumber *= count;
				// System.out.println(macro_arr[1] +
				// " This is array length for every multi-valued macro- " +
				// count);
			}

		}
		return cardinalNumber;
	}

	/**
	 * Function to find a variable in Macro sheet and Environment Variable Sheet
	 * 
	 * @param variableToFind
	 *            Name of the Variable to Search in Macro and Env Variable
	 *            Sheet.
	 * @return Returns the value of the Variable from Macro/Environment Variable
	 *         table.
	 */

	// /TODO check this for vector macro. This is the only place where its
	// taking only Single macro
	public String FindInMacroAndEnvTableForSingleVector(String variableToFind,
			String nameSpace) throws Exception {
		Log.Debug("Excel/FindInMacroAndEnvTableForSingleVector : Start of function with variableToFind = "
				+ variableToFind);

		String tempValue = variableToFind;

		try {
			if (variableToFind.contains("=")) {
				Log.Debug("Excel/FindInMacroAndEnvTableForSingleVector : The Variable to Find contains an = sign ");

				String[] splitVariableToFind = Excel
						.SplitOnFirstEquals(variableToFind); // .Split('=');

				Log.Debug(String
						.format("Excel/FindInMacroAndEnvTableForSingleVector : Length of  splitVariableToFind = %s",
								splitVariableToFind.length));

				if (splitVariableToFind.length <= 1) {
					Log.Debug(String
							.format("Excel/FindInMacroAndEnvTableForSingleVector : End of function with variableToFind = %s and its value is ->  %s",
									variableToFind, variableToFind));
					return variableToFind;
				}

				tempValue = splitVariableToFind[1];

				Log.Debug(String
						.format("Excel/FindInMacroAndEnvTableForSingleVector : variableToFind = %s",
								tempValue));

				// First Check in the Macro Sheet
				if (_macroSheetHashTable.get("$"
						+ AppendNamespace(tempValue, nameSpace)) != null) {
					tempValue = (String) _macroSheetHashTable.get("$"
							+ AppendNamespace(tempValue, nameSpace));
					Log.Debug(String
							.format("Excel/FindInMacroAndEnvTableForSingleVector : After Macro Sheet parsing , variableToFind = %s",
									tempValue));
				}

				tempValue = splitVariableToFind[0] + "=" + tempValue;
			} // First Check in the Macro Sheet
			else if (_macroSheetHashTable.get("$"
					+ AppendNamespace(tempValue, nameSpace)) != null) {
				tempValue = (String) _macroSheetHashTable.get("$"
						+ AppendNamespace(tempValue, nameSpace));
				Log.Debug(String
						.format("Excel/FindInMacroAndEnvTableForSingleVector : After Macro Sheet parsing , variableToFind =  %S",
								tempValue));
			}

		} catch (Exception e) {
			Log.Debug(String
					.format("Excel/FindInMacroAndEnvTableForSingleVector : Exception occured,message is : %s",
							e.getMessage()));
			throw new Exception(
					"Excel/FindInMacroAndEnvTableForSingleVector : Exception occured,message is : "
							+ e.getMessage());
		}
		

		Log.Debug("Excel/FindInMacroAndEnvTableForSingleVector : End of function with variableToFind = "
				+ variableToFind + " and its value is -> " + tempValue);
		
		return tempValue;
	}// FindInMacroAndEnvTableForSingleVector


	/**
	 * Function to read the Excel file. This function will read the different
	 * sheets present inside this sheet..usually Config sheet, Macros Sheet,
	 * Users sheet and the TestCase sheet. If there are any other sheet then
	 * those will be ignored.
	 * 
	 * @param inputFileName
	 *            Name of the input Excel file.
	 * @param verificationSwitchingFlag
	 * @param compileMode
	 *            states if compile mode or execution mode.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void ReadExcel(String inputFileName,
			boolean verificationSwitchingFlag, boolean compileMode)
			throws Exception, MoleculeDefinitionException {
		FileInputStream inputFile = null;

		try {
			// / Creating Input Stream
			File filehandle = new File(inputFileName);

			inputFile = new FileInputStream(filehandle);
			Log.Debug(String
					.format("Excel/ReadExcel : Start of the function with Excel sheet name as : %s",
							inputFileName));

			if (!filehandle.exists()) {
				String error = String
						.format("Excel/ReadExcel :  Excel file specified %s does not exits. Enter a valid file path",
								inputFileName);
				Log.Error(error);
				throw new Exception(error);
			}

			String fileName = filehandle.getName();

			int i;
			for (i = fileName.length() - 1; i > 0; i--) {
				if (fileName.charAt(i) == '.') {
					break;
				}
			}
			String str = fileName.substring(0, i);
			String nameSpace = mainNameSpace = str.toLowerCase();

			// / Assign verificationSwitching flag.
			verificationSwitching = verificationSwitchingFlag;

			// / Assign Compilation flag
			compileModeFlag = compileMode;

			// / Create a POIFSFileSystem object
			POIFSFileSystem inputFileSystem = new POIFSFileSystem(inputFile);
			Log.Debug("Excel/openExcelFile : Creating new workbook object ");

			// / Create a workbook using the File System
			_workBook = new HSSFWorkbook(inputFileSystem);
			Log.Debug(String.format(
					"Excel/ReadExcel : The excel File %s opened successfully",
					inputFileName));

			Log.Debug(String.format(
					"Excel/ReadExcel : Reading the Config Sheet %s",
					ConfigSheetName));
			// system.out.println(String.format("READING Config SHEET OF %s" ,
			// inputFileName));

			ReadConfigSheet(_workBook);
			readHashTable(_configSheetHashTable);

			Log.Debug(String
					.format("Excel/ReadExcel : The config Sheet - %s of Excel Sheet %s read successfully.",
							ConfigSheetName, inputFileName));
		} catch (Exception e) {

			Log.Error(String.format(
					"Excel/ReadExcel : Exception  message is : %s",
					e.getMessage()));
			// system.out.println(String.format("Excel/ReadExcel : Exception  message is : %s",e.getMessage()));

			try {

				Log.Debug(String.format(
						"Excel/ReadExcel : closing Excel file %s.",
						inputFileName));
				inputFile.close();
				Log.Debug(String.format(
						"Excel/ReadExcel : Excel file %s closed successfully.",
						inputFileName));

			} catch (IOException e1) {

				Log.Error(String
						.format("Excel/ReadExcel : Exception occured while closing Excel file, message is : %s",
								e1.getMessage()));
				e1.printStackTrace();
				throw new Exception(
						String.format(
								"Excel/ReadExcel : Exception occured while closing Excel file, message is : %s",
								e1.getMessage()));
			}

			throw new Exception(String.format(
					"Excel/ReadExcel : Exception  message is : %s.",
					e.getMessage()), e);
		}
		try {
			for (String externalSheet : Excel.getExternalSheets()) {
				System.out.println("\nExternal sheets : " + externalSheet);
				ReadExternalMacros(externalSheet);
			}
			for (String externalSheet : Excel.getExternalSheets()) {
				Log.Debug(String
						.format("Excel/ReadExcel : Reading the External INCLUDE Sheet %s",
								externalSheet));
				// System.out.println(String.format("Reading the External INCLUDE  FILE : %s",
				// externalSheet));

				ReadExternalSheets(externalSheet);

				Log.Debug(String
						.format("Excel/ReadExcel : Successfully Read the External INCLUDE Sheet %s",
								externalSheet));
				Log.Result(String
						.format("Excel/ReadExcel : Successfully Read the External INCLUDE Sheet %s",
								externalSheet));
			}

			// / Resume Back with the MAIN Sheet after reading all the External
			// Molecule sheets.
			Log.Debug(String
					.format("Excel/ReadExcel : Resume Back with the MAIN Sheet after reading all the External Molecule sheets of %s. ",
							inputFileName));

			Log.Debug(String.format(
					"Excel/ReadExcel : Reading the Macro Sheet %s",
					MacroSheetName));
			// system.out.println(String.format("READING Macro SHEET OF %s",
			// inputFileName));

			ReadMacroSheet(_workBook, mainNameSpace);
			// System.out.println("Before Merge\t"+_macroSheetHashTable);
			// Merging the command line macros with the Excel Macro

			Set<String> cmdkey = Controller.macrocommandlineinputs.keySet();
			Iterator<String> cmdkey_iterate = cmdkey.iterator();
			while (cmdkey_iterate.hasNext()) {
				String cmd_macro_key = cmdkey_iterate.next();
				String cmd_macro_value = Controller.macrocommandlineinputs
						.get(cmd_macro_key);
				if ((cmd_macro_value.contains("$$") || cmd_macro_value
						.contains("$"))
						&& (cmd_macro_value.startsWith("{") && cmd_macro_value
								.endsWith("}"))) {
					// System.out.println("The macro key "+cmd_macro_key+" The namespace "+mainNameSpace);
					String cmd_macro_namespace = Utility.TrimStartEnd(
							cmd_macro_key.split("\\.")[0], '$', 1);
					// System.out.println("The macro key "+cmd_macro_key+" The namespace "+cmd_macro_namespace);
					cmd_macro_value = ExpandMacrosValue(cmd_macro_value,
							cmd_macro_key, cmd_macro_namespace);
				}
			}
			_macroSheetHashTable.putAll(Controller.macrocommandlineinputs);

			readHashTable(_macroSheetHashTable);
			Log.Debug(String
					.format("Excel/ReadExcel : The Macro Sheet - %s of Excel Sheet %s read successfully.",
							MacroSheetName, inputFileName));
			Log.Debug(String.format(
					"Excel/ReadExcel: The New CommandLine - %s",
					_macroSheetHashTable));
			Log.Debug(String.format(
					"Excel/ReadExcel : Reading the Prototypes Sheet %s",
					PrototypeSheetName));

			ReadPrototypesSheet(_workBook, mainNameSpace);

			Log.Debug(String
					.format("Excel/ReadExcel : The Prototypes Sheet - %s of Excel Sheet %s read successfully.",
							PrototypeSheetName, inputFileName));

			Log.Debug(String.format(
					"Excel/ReadExcel : Reading the Abstract TestCase Sheet %s",
					AbstractSheetName));

			ReadAbstractTestCaseSheet(_workBook, mainNameSpace);

			Log.Debug(String
					.format("Excel/ReadExcel : The Abstract TestCase Sheet - %s of Excel Sheet %s read successfully.",
							AbstractSheetName, inputFileName));

			Log.Debug(String.format(
					"Excel/ReadExcel : Reading the TestCase Sheet %s",
					TestCaseSheetName));

			ReadTestCaseSheet(_workBook, mainNameSpace);

			Log.Debug(String
					.format("Excel/ReadExcel : The TestCase Sheet - %s of Excel Sheet %s read successfully.",
							TestCaseSheetName, inputFileName));
			Log.Result(String.format(
					"Excel/ReadExcel: Excel file : %s read successfully",
					inputFileName));
		} catch (Exception ex) {
			Log.Error(String
					.format("Excel/ReadExcel : Error in Reading the Excel file  %s\nMessage : %s",
							inputFileName, ex.getMessage()));
			// system.out.println(String.format("Excel/ReadExcel : Exception  message is : %s",ex.getMessage()));

			try {
				Log.Debug(String.format(
						"Excel/ReadExcel : closing Excel file %s.",
						inputFileName));
				inputFile.close();
				Log.Debug(String.format(
						"Excel/ReadExcel : Excel file %s closed successfully.",
						inputFileName));

			} catch (IOException e1) {
				Log.Error(String
						.format("Excel/ReadExcel : Exception in closing Excel file, message is : %s",
								e1.getMessage()));
				e1.printStackTrace();
				throw new Exception(
						String.format(
								"Excel/ReadExcel : Exception in closing Excel file, message is : %s",
								e1.getMessage()));
			}
			throw new Exception(String.format(
					"Excel/ReadExcel : Exception  message is : %s.",
					ex.getMessage()));
		} finally {
			try {
				Log.Debug(String.format(
						"Excel/ReadExcel : closing Excel file %s.",
						inputFileName));
				inputFile.close();
				Log.Debug(String.format(
						"Excel/ReadExcel : Excel file %s closed successfully.",
						inputFileName));

			} catch (IOException e1) {
				Log.Error(String
						.format("Excel/ReadExcel : Exception in closing Excel file, message is : %s",
								e1.getMessage()));
				e1.printStackTrace();
				throw new Exception(
						String.format(
								"Excel/ReadExcel : Exception in closing Excel file, message is : %s",
								e1.getMessage()));
			}

		}
	}

	/**
	 * Function to read the Configuration sheet
	 * 
	 * @param workBook
	 * @throws Exception
	 */
	void ReadConfigSheet(HSSFWorkbook workBook) throws Exception {

		Log.Debug(String
				.format("Excel/ReadConfigSheet : Start of the Function with ConfigSheetName as %s",
						ConfigSheetName));

		String configSheetName = ConfigSheetName;

		HSSFSheet workSheet = null;
		xmlfile = new XMLWriter();
		Log.Debug("Excel/ReadConfigSheet : The worksheet object created");

		Log.Debug(String.format(
				"Excel/ReadConfigSheet : Reading the WorkSheet %s",
				configSheetName));

		try {
			// / Get the config sheet from workbook
			workSheet = workBook.getSheet(configSheetName);

			if (workSheet == null) {
				throw new Exception(
						String.format(
								"Excel/ReadConfigSheet : Could not find the Config Sheet : %s",
								configSheetName));
			}

			Log.Debug(String
					.format("Excel/ReadConfigSheet : Config Worksheet : %s exists and read successfully",
							configSheetName));

		} catch (Exception e) {

			Log.Error(String
					.format("Excel/ReadConfigSheet : Could not find the Config Sheet : %s Exception Message raised is : %s",
							configSheetName, e.getMessage()));
			throw new Exception(
					String.format(
							"Excel/ReadConfigSheet : Could not find the Config Sheet : %s Exception Message raised is : %s",
							configSheetName, e.getMessage()));
		}
		try {

			Log.Debug("Excel/ReadConfigSheet : Getting the values from the Config Sheet. Calling GetKeyValuePair....");

			GetKeyValuePair(workSheet, _configSheetHashTable, "config", null);
			Log.Debug("Excel/ReadConfigSheet : The values from the Config sheet is read successfully.");
			String _nested_include_list = "", _nested_scriptlocation_list = "";

			for (String nested : Excel.getExternalSheets()) {
				// System.out.println("Excel/ReadConfigSheet : the nested includes "
				// + readNestedIncludes(nested));
				_nested_include_list += readNestedIncludes(nested);

			}
			// forDebug("Excel/ReadConfigSheet : the nested includes "+_nested_include_list);
			Log.Debug("Excel/ReadConfigSheet : The values from the Config sheet is read successfully.");
			AddToExternalSheets("Include", _nested_include_list);
			for (String nested : Excel.getExternalSheets()) {
				// System.out.println("Excel/ReadConfigSheet : the nested includes "
				// + readNestedIncludes(nested));
				_nested_scriptlocation_list += readNestedScriptLocations(nested);

			}
			if (!_configSheetHashTable.isEmpty()) {
				// forDebug("Excel/ReadConfigSheet::  The Previous List "+_configSheetHashTable.get("ScriptLocation"));
				String _tableList = _configSheetHashTable.get("ScriptLocation");
				_configSheetHashTable.remove("ScriptLocation");
				_tableList = _tableList + ";" + _nested_scriptlocation_list;
				_configSheetHashTable.put("ScriptLocation", _tableList);
				// System.out.println("Excel/ReadConfigSheet::  The added List "+_configSheetHashTable.get("ScriptLocation"));
			}
			// //System.out.println("Excel/ReadConfigSheet : the nested includes "
			// +_externalSheets);
			if (Controller.opts.nyonserver) {
				System.out.println(xmlfile.createXMLFile()); // creates a XML
			} // file is
				// Generated for
				// the includes
		} catch (Exception ex) {

			Log.Error(String
					.format("Excel/ReadConfigSheet : Error occured while getting the values from the Config sheeet %s. Message : %s",
							configSheetName, ex.getMessage()));
			throw new Exception(
					String.format(
							"Excel/ReadConfigSheet : Error occured while getting the values from the Config sheeet %s. Message : %s",
							configSheetName, ex.getMessage()));
		}
		Log.Debug(String
				.format("Excel/ReadConfigSheet : End of the Function with ConfigSheetName as %s",
						configSheetName));

	}// ReadConfigSheet

	/***
	 * Function to always return String Equivalent of a given cell.
	 * 
	 * @param cell
	 * @return String value for the Cell
	 */
	String GetCellValueAsString(HSSFCell cell) {
		if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
			return Boolean.toString(cell.getBooleanCellValue());
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			return Double.toString(cell.getNumericCellValue());
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			
			return cell.getStringCellValue();
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK
				|| cell.getCellType() == HSSFCell.CELL_TYPE_ERROR) {
			// System.out.println(" get cell value "+cell.getCellType());
			return StringUtils.EMPTY;
		}

		return StringUtils.EMPTY;
	}

	// This function will read include files and return the list of nested
	// script locations.

	private String readNestedScriptLocations(String nestedexternalfile) {

		File inputFile = null;
		FileInputStream extFile = null;
		String ScriptLocationPathList = "";
		try {
			inputFile = new File(nestedexternalfile);
			if (!inputFile.exists()) {
				String error = "Excel/readNestedScriptLocations: External Excel file(include) specified "
						+ nestedexternalfile
						+ " does not exits. Enter a valid path";
				Log.Error(error);
				throw new Exception(error);
			}
			extFile = new FileInputStream(inputFile);
			POIFSFileSystem inputFileSystem = new POIFSFileSystem(extFile);
			HSSFWorkbook _workbook_nested_extern = new HSSFWorkbook(
					inputFileSystem);
			HSSFSheet externalSheet = _workbook_nested_extern
					.getSheet(ConfigSheetName);

			int key = 0, value = 1;
			String extrKey = null, extrValue = null;
			Iterator rowIter = externalSheet.rowIterator();

			do {

				HSSFRow myRow = (HSSFRow) rowIter.next();
				Iterator cellIter = myRow.cellIterator();

				while (cellIter.hasNext()) {

					HSSFCell myCell = (HSSFCell) cellIter.next();

					if (myCell.getCellNum() == key) {

						extrKey = GetCellValueAsString(myCell);
					}

					if (myCell.getCellNum() == value) {
						extrValue = GetCellValueAsString(myCell);
					}

					if ((_configSheetKeys[0].compareTo(extrKey) == 0)
							&& StringUtils.isNotBlank(extrValue)) {
						String[] ScriptValues = extrValue.split(";");

						for (String Path : ScriptValues) {
							if (Path.contains(":") || Path.startsWith("/")) {
								// forDebug("Excel/readScriptLocations:: The ScriptLocation "
								// + Path+"   "+extrKey);
								ScriptLocationPathList += Path + ";";

							} else {
								if (Path.isEmpty()) {
									continue;
								} else {
									String actualPath = "";
									actualPath += ProgramOptions.workingDirectory
											+ SysEnv.SLASH + Path + ";";
									ScriptLocationPathList += actualPath;
								}
							}

							// forDebug("readNestMolecle:: The nested molecule list "+includePathList);
						}
					}
				}
			} while (rowIter.hasNext());

		} catch (Exception ex) {
			Log.Error(String
					.format("Excel/readNestedScriptLocations : Error occured while getting values from the %s Sheet. \n Message %s: ",
							nestedexternalfile, ex.getClass()));
			throw new Exception(
					String.format(
							"Excel/readNestedScriptLocations : Error occured while getting values from the %s Sheet. \n Message %s: ",
							nestedexternalfile, ex.getMessage()));
		} finally {
			// forDebug("Excel/readNestedScriptLocations:: "+ScriptLocationPathList);
			return ScriptLocationPathList;
		}
	}

	// This function will read include files and return the list of nested
	// include files

	@SuppressWarnings("finally")
	private String readNestedIncludes(String nestedexternalfile) {

		File inputFile = null;
		FileInputStream extFile = null;
		String includePathList = "";
		try {
			inputFile = new File(nestedexternalfile);
			if (!inputFile.exists()) {
				String error = "Excel/readNestedInlcudes: External Excel file(include) specified "
						+ nestedexternalfile
						+ " does not exits. Enter a valid path";
				Log.Error(error);
				throw new Exception(error);
			}
			extFile = new FileInputStream(inputFile);
			POIFSFileSystem inputFileSystem = new POIFSFileSystem(extFile);
			HSSFWorkbook _workbook_nested_extern = new HSSFWorkbook(
					inputFileSystem);
			HSSFSheet externalSheet = _workbook_nested_extern
					.getSheet(ConfigSheetName);

			int key = 0, value = 1;
			String extrKey = null, extrValue = null;
			Iterator rowIter = externalSheet.rowIterator();

			do {

				HSSFRow myRow = (HSSFRow) rowIter.next();
				Iterator cellIter = myRow.cellIterator();

				while (cellIter.hasNext()) {

					HSSFCell myCell = (HSSFCell) cellIter.next();

					if (myCell.getCellNum() == key) {

						extrKey = GetCellValueAsString(myCell);
					}

					if (myCell.getCellNum() == value) {
						extrValue = GetCellValueAsString(myCell);
					}
				}
				if (_configSheetKeys[10].compareTo(extrKey) == 0) {
					// System.out.println("The paths are "+extrValue);
					String[] includeValues = extrValue.split(",");

					for (String Path : includeValues) {

						if (Path.contains(":") || Path.startsWith("/")) {
							// System.out.println("Excel/readNestedInclude:: The include files "
							// + Path);
							includePathList += Path + ",";

						} else {
							if (Path.isEmpty()) {
								continue;
							} else {
								// System.out.println("The file name "+this.getXlsFilePath());
								String actualPath = "";
								// Implement the Inlcule file path issue.
								File inputxlsfile = new File(
										this.getXlsFilePath());

								// actualPath +=
								// ProgramOptions.workingDirectory+
								// Controller.SLASH + Path + ",";
								actualPath += inputxlsfile.getParent()
										+ SysEnv.SLASH + Path + ",";
								// System.out.println("This is the file location of include "+actualPath);
								includePathList += actualPath;
							}
						}

						// forDebug("readNestMolecle:: The nested molecule list "+includePathList);
					}

				}
			} while (rowIter.hasNext());

		} catch (Exception ex) {
			Log.Error(String
					.format("Excel/readNestedIncludeMolecules : Error occured while getting values from the %s Sheet. \n Message %s: ",
							nestedexternalfile, ex.getMessage()));
			throw new Exception(
					String.format(
							"Excel/readNestedIncludeMolecules : Error occured while getting values from the %s Sheet. \n Message %s: ",
							nestedexternalfile, ex.getMessage()));

		} finally {

			// System.out.println("nested include file "+includePathList+" external include file list "+_externalSheets);
			return includePathList;
		}
	}

	// /This function will put the values of different sheets in their
	// corresponding HashTables

	void GetKeyValuePair(HSSFSheet worksheet,
			Hashtable<String, String> hashTable, String sheetname,
			String nameSpace) throws Exception {
		int key = 0, value = 1, defValue = 1;
		String strKey = null, strValue = null, defStrValue = null;
		// System.out.println("Name space="+nameSpace+"sheet Name="+sheetname);
		boolean isMVC = false;

		if (sheetname.equals("macros")
				&& nameSpace != null
				&& (isMVC = Controller.macroColumnValue.containsKey(nameSpace)) != false) {
			value = Integer
					.parseInt(Controller.macroColumnValue.get(nameSpace));
			// System.out.println("Macro field value of file "+nameSpace+"is "+value);
		}
		try {
			Log.Debug("Excel/GetKeyValuePair : Start of the FUnction.");
			Log.Debug(String
					.format("Excel/GetKeyValuePair : Getting the key/Value pair from the sheet. Number of rows to read is -> %s",
							worksheet.getPhysicalNumberOfRows()));
			// / We now need something to iterate through the cells.
			Iterator rowIter = worksheet.rowIterator();
			//System.out.println("row iterator initialized");
			//do {
			int line=0;
			 while (rowIter.hasNext()){
				// System.out.println("at line "+line);
				boolean keyflag = false, valueflag = false;
				HSSFRow myRow = (HSSFRow) rowIter.next();
				Iterator cellIter = myRow.cellIterator();
				int col=0;
				while (cellIter.hasNext()) {
			//		System.out.println("col no"+col);
					HSSFCell myCell = (HSSFCell) cellIter.next();

					if (myCell.getCellNum() == key) {
						
						strKey = GetCellValueAsString(myCell);
						//if(StringUtils.isNotEmpty(strKey)||StringUtils.isNotBlank(strKey))
							keyflag = true;
						
					}
					if (myCell.getColumnIndex() == defValue
							&& defValue != value) {
						defStrValue = GetCellValueAsString(myCell);
					}
					if (myCell.getCellNum() == value) {
						valueflag = true;
						// System.out.println("The value of cell "+GetCellValueAsString(myCell));
						strValue = GetCellValueAsString(myCell);
						/*
						 * System.out.println("Str key:'" + strKey +
						 * "'\tStr value:'" + strValue + "'");
						 */
						// If specified column is empty then using the default
						// column value
						try {
							if (isMVC && sheetname.equals("macros")
									&& !strKey.isEmpty() && strValue.isEmpty()) {
								strValue = defStrValue;
							}
						} catch (Exception e) {

						}
					}
				}
			//	System.out.println("str key"+strKey+"\t str value"+strValue);
				// / If this is a Macros sheet then Expand the Value before
				// adding it to the Hashtable..
				if (sheetname.equals("macros")) {
					// / If this is a Macros Sheet, then, append the name space
					// for the Macro
					// System.out.println("the flags are "+keyflag+"  "+valueflag);
					if (keyflag) {
						if (!valueflag) {
							ContextVar.setContextVar(strKey+Thread.currentThread().getId(), StringUtils.EMPTY);
							strValue = "%" + strKey + "%";

						}
					}
					if (StringUtils.isBlank(strValue)
							|| StringUtils.isEmpty(strValue) && keyflag) {
						// System.out.println("This is blank value but contains a key "+strValue+" "+strKey);
						ContextVar.setContextVar(strKey+Thread.currentThread().getId(), StringUtils.EMPTY);
						strValue = "%" + strKey + "%";
					}
					//System.out.println("strkey coming "+strKey+" strkey value "+strValue);
					if(!strKey.isEmpty())
					{
				//		System.out.println("The key is not empty "+strKey);
						strKey = AppendNamespace(strKey, nameSpace);
					strValue = ExpandMacrosValue(strValue.trim(),
							strKey.trim(), nameSpace); // modify
					}
				
				}

				if (sheetname.equals("config")
						&& ((_configSheetKeys[10].compareTo(strKey) == 0))) {
					// /Don't insert in hash table if sheet is config and key is
					// INCLUDE
					
					if (!Controller.opts.includeFlag) {
						Log.Debug("Command Line Executions switch Given\t"
								+ Controller.opts.includeFlag);
						String[] include_commandlist = Controller.opts.includeMolecules
								.split(",");
						String commmandline_includelist = "";
						for (String commandline_include : include_commandlist) {// System.out.println("This the include files\t"+Controller.includeMolecules);
							String nSMkey = "";
							if (commandline_include.contains("#") == true) {
								String temp[] = commandline_include.split("#");
								if (temp.length != 2) {
									Log.Error("Excel/GetKeyValuePair :"
											+ commandline_include
											+ " contains "
											+ temp.length
											+ " numbers of #.It should contains 2 #.Its default macro column will be selected");
								} else {
									nSMkey = temp[0];
									commandline_include = temp[1];
								//	System.out.println("Name space"+nSMkey);
								//	System.out.println("File:"+commandline_include);
								}
							}
							if (commandline_include.contains(":")
									|| commandline_include.startsWith("/")) {
								commmandline_includelist += commandline_include;// Changes
																				// the
																				// include
																				// molecule
								if (!nSMkey.isEmpty())
									namespaceMap.put(commandline_include,
											nSMkey);
								commmandline_includelist += ",";
								// System.out.println(commandline_include+":"+nSMkey);
							} else {
								String actualPath = "";
								{
									File inputxlsfile = new File(
											this.getXlsFilePath());
									// actualPath +=
									// ProgramOptions.workingDirectory+
									// Controller.SLASH + commandline_include +
									// ",";
									
									if(commandline_include.contains("..")){
										int lastIndex=commandline_include.lastIndexOf("..")+1;
										String temp[]=commandline_include.split("\\..");
										int upperH=temp.length;	
										String p=inputxlsfile.getParent();
										for(int i=1;i<upperH-1;i++){
											p=p.substring(0, p.lastIndexOf(SysEnv.SLASH));		//		System.out.println("p="+p);
										}
										commandline_include=p+commandline_include.substring(lastIndex+1,commandline_include.length());
										actualPath +=commandline_include;
									//	System.out.println("IF/Command Line Include:"+commandline_include);
									}
									else{
											actualPath += inputxlsfile.getParent()
											+ SysEnv.SLASH
											+ commandline_include;
								//			System.out.println("ELse/Command Line Include:"+commandline_include);
									}
									if (!nSMkey.isEmpty())
										namespaceMap.put(actualPath, nSMkey);
									actualPath += ",";
								//	System.out.println("Command Line Include:"+commandline_include);
								}
								// System.out.println(actualPath+":"+nSMkey);
								// namespaceMap.put(actualPath,nSMkey);
								commmandline_includelist += actualPath;
							}
						}
						AddToExternalSheets(strKey, commmandline_includelist);
						// path
						// specified
						// from
						// the
						// command
						// line
						// argument
					}
					if (Controller.opts.includeFlag || !Controller.opts.includeFlag) {
						Log.Debug("No Command Line Executions switch Given\t"
								+ Controller.opts.includeFlag);
						if (strValue==null||strValue.isEmpty()) {

							break;
						} else {
							String[] includeValues = strValue.split(",");
							String inlcudelist = "";
							for (String Path : includeValues) {

								if (Path.contains(":") || Path.startsWith("/")) {
									inlcudelist += Path + ",";
									// AddToExternalSheets(strKey, strValue);
								} else {

									if (Path.isEmpty()) {
										continue;
									} else {
										String actualPath = "";

										File inputxlsfile = new File(
												this.getXlsFilePath());
										// actualPath +=
										// ProgramOptions.workingDirectory+
										// Controller.SLASH + Path + ",";
										if(Path.contains("..")){
											int lastIndex=Path.lastIndexOf("..")+1;
											String temp[]=Path.split("\\..");
											int upperH=temp.length;	
											String p=inputxlsfile.getParent();
											for(int i=1;i<upperH-1;i++){
												p=p.substring(0, p.lastIndexOf(SysEnv.SLASH));		//		System.out.println("p="+p);
											}
											Path=p+Path.substring(lastIndex+1,Path.length());
											actualPath +=Path;
										//	System.out.println("IF/Command Line Include:"+commandline_include);
										}else{
										actualPath += inputxlsfile.getParent()
												+ SysEnv.SLASH + Path + ",";
										}// System.out.println(Path+" This the actual Path\t"+actualPath);
								//		System.out.println("Files:"+actualPath);
										inlcudelist += actualPath+",";
									} // AddToExternalSheets(strKey,
										// actualPath);
								}
							}
							// forDebug("GetKeyValuePair:: The full path ---> "
							// + inlcudelist);
							AddToExternalSheets(strKey, inlcudelist);

						}
					}
					// System.out.println("GetKeyPairValue:: key iserted->"+strKey+"\tvalue given--->"+strValue);
					// System.out.println("key iserted->"+strKey+"\tvalue given--->"+ProgramOptions.workingDirectory+"\\"+strValue);

					if (Controller.opts.nyonserver) {
						// System.out.println("Molecule to be parsed "+_externalSheets);
						for (String moleculefile : Excel.getExternalSheets()) {
							// System.out.println("Molecule files "+moleculefile);
							System.out.println(xmlfile
									.genarateXML(moleculefile)
									+ " Molecule file: " + moleculefile);
						}
					}
				} else if(strValue!=null){
					if (sheetname.equals("config")
							&& _configSheetKeys[1].equalsIgnoreCase(strKey)) {
						// System.out.println("The Key of Config sheet "+strKey+"vlue "+strValue);
						// if(strValue.startsWith("/")||strValue.indexOf("\\\\")>1)
						if (new File(strValue).isDirectory()
								|| strValue.contains(";"))
							hashTable.put(strKey, "");
						else
							hashTable.put(strKey, strValue);
					} else if (sheetname.equals("config")
							&& _configSheetKeys[3].equalsIgnoreCase(strKey)) {
						if (new File(strValue).isDirectory()
								|| strValue.contains(";") || strValue.contains(":"))
							hashTable.put(strKey, "");
						else
							hashTable.put(strKey, strValue);
					} else {
						hashTable.put(strKey, strValue);
					}
				}else{
					strValue=strValue==null?"":strValue;
					hashTable.put(strKey, strValue);
				}

			}// while (rowIter.hasNext());

		} catch (Exception e) {
			e.printStackTrace();
			Log.Error(String
					.format("Excel/GetKeyValuePair : Error occured while getting values from the %s Sheet. \n Message %s: ",
							sheetname, e.getMessage()));
			throw new Exception(
					String.format(
							"Excel/GetKeyValuePair : Error occured while getting values from the %s Sheet. \n Message %s: ",
							sheetname, e.getMessage()));
		}

	}// /GetKeyValuePair

	// /Function to Expand a Macro for the looping construct, also modified for
	// indexed MVM
	public String ExpandMacrosValue(String macroValue, String macroKey,
			String nameSpace) throws Exception {
		try {
		//	System.out.println("Macro value :"+macroValue+"\tmacroKey:"+macroKey+"\tnameSpace:"+nameSpace);
			String macroValExpander = "..";
			Log.Debug(String
					.format("Excel/ExpandMacrosValue : Start of the Function with macroValue = %s",
							macroValue));
			macroValue = macroValue.trim();
			if ((macroValue.startsWith("{")) && (macroValue.endsWith("}"))) {
				Log.Debug(String
						.format("Excel/ExpandMacrosValue : Macro value %s contains a \"{\" and \"}\"",
								macroValue));

				String macroValueTrimmed = Utility.TrimStartEnd(macroValue,
						'}', 0);
				macroValueTrimmed = Utility.TrimStartEnd(macroValueTrimmed,
						'{', 1);
				String[] allStringsToExpand = macroValueTrimmed.split(",");

				Log.Debug(String
						.format("Excel/ExpandMacrosValue : Macro value %s contains a \"{\" and \"}\". Before FOR Loop.",
								macroValue));
				int size = allStringsToExpand.length;
				int[] countOfValues = new int[size]; // variable to store count
				// of Values in Index
				// Macros
				int count = 0; // variable as counter for Macrros
				for (String tempStringToExpand : allStringsToExpand) {
					Log.Debug(String
							.format("Excel/ExpandMacrosValue : Macro value =%s. Working on %s",
									macroValue, tempStringToExpand));
					// System.out.println("The Temp Value "+tempStringToExpand+" \n Macro Key "+macroKey+"\n Macro Value "+macroValue);
					// if (tempStringToExpand.startsWith("$$")) {
					if (tempStringToExpand.startsWith("$$")) {
						tempStringToExpand = Utility.TrimStartEnd(
								tempStringToExpand, '$', 1);
						tempStringToExpand = "$" + tempStringToExpand;
						// System.out.println("Temp Values after triming "+tempStringToExpand);
						Log.Debug("Excel/ExpandMacroValue : Macro value is replaced by single $ if there exists $$\n The Replaced Value is "
								+ tempStringToExpand);
						// TODO statements for indexed MVM
						// add new strkey name and put value as #value1,value2#
						String newMacroKey = new String(macroKey + "#"
								+ tempStringToExpand.substring(1));
						// String newMacroKey = new String(macroKey + "#"+
						// tempStringToExpand.substring(2));
						// System.out.println("New Macro Key Created "+newMacroKey);
						Log.Debug("Excel/ExpandMacrosValue : searching for macroKey "
								+ tempStringToExpand.substring(1)
								+ "in hashtable");
						String tempMacroToExpand = AppendNamespace(
								tempStringToExpand, nameSpace);
						// System.out.println("The macro hash\n"+_macroSheetHashTable);
						if (_macroSheetHashTable.containsKey(tempMacroToExpand)) {
							Log.Debug("Excel/ExpandMacrosValue : Index Macro key found");
							String newMacroValue = _macroSheetHashTable
									.get(tempMacroToExpand);

							// newMacroValue = newMacroValue.replace("{", "~#");
							// newMacroValue = newMacroValue.replace("}", "#~");
							// Change from
							// newMacroValue = newMacroValue.replace('{', '~');
							// newMacroValue = newMacroValue.replace('}', '~');
							newMacroValue = StringUtils.replace(newMacroValue,
									"{", "$~$");
							newMacroValue = StringUtils.replace(newMacroValue,
									"}", "$~$");
							// newMacroValue = newMacroValue.replace('{', '#');
							// newMacroValue = newMacroValue.replace('}', '#');
							// newMacroValue="~"+newMacroValue+"~";
							// System.out.println("The new Macro Value after putting indexs "+newMacroValue);
							countOfValues[count] = newMacroValue.split(",").length;

							Log.Debug("Excel/ExpandMacrosValue : Comparing indexes of Macros");
							if (count > 0
									&& countOfValues[count] != countOfValues[count - 1]) {
								Log.Error("Excel/ExpandMacrosValue : Unequal indexes in Indexed MVM. Check MVM - "
										+ macroKey);
								throw new Exception(
										"Excel/ExpandMacrosValue : Unequal indexes in Indexed MVM. Check MVM - "
												+ macroKey);
							}
							Log.Debug("Excel/ExpandMacrosValue : Adding Indexed MVM- Key:"
									+ newMacroKey
									+ " Value:'"
									+ newMacroValue
									+ "'");
							_macroSheetHashTable.put(newMacroKey.toLowerCase(),
									newMacroValue);
					//		System.out.println("value in macro table"+newMacroKey.toLowerCase()+"-"+newMacroValue);
							String indexMacroKey[] = newMacroKey.split("\\.");
							if(indexMacroKey[0].substring(1).equalsIgnoreCase(mainNameSpace)){
                                _indexedMacroTable.put(
                                        indexMacroKey[1].toLowerCase(),
                                        newMacroValue.trim());
                               // System.out.println("\nExcel/putting the value in inedexed table"+indexMacroKey[1].toLowerCase().toLowerCase()+","+newMacroValue.trim());
                            }else{
                                _indexedMacroTable.put(newMacroKey.toLowerCase(),newMacroValue.trim());
                              //  System.out.println(newMacroKey+":"+newMacroValue);
                            //    System.out.println("\nExcel/putting the value in inedexed table"+newMacroKey.toLowerCase()+","+newMacroValue.trim());
                            }
						/*	_indexedMacroTable.put(
									indexMacroKey[1].toLowerCase(),
									newMacroValue.trim());*/
							count++;

						} else {
							Log.Error("Excel/ExpandMacrosValue : Indexed Macro could not be found in Macros Table");
							throw new Exception(
									"Indexed Macros could not be found in Macro Table.");
						}

					} else if (tempStringToExpand.startsWith("$")
							&& !tempStringToExpand.startsWith("$$")) {
						
						String tempMacroToExpand = AppendNamespace(
								tempStringToExpand, nameSpace);
						String newMacroKey = macroKey + "#"
								+ tempStringToExpand.substring(1);
					//	System.out.println("tempMacroToExpand "+tempMacroToExpand+"\tnewMacroKey"+newMacroKey);
						if (_macroSheetHashTable.containsKey(tempMacroToExpand)) {
							Log.Debug("Excel/ExpandMacrosValue : Index Macro key found");
							String newMacroValue = _macroSheetHashTable
									.get(tempMacroToExpand);
					//		System.out.println("New macro value:"+newMacroValue);
							Log.Debug("Excel/ExpandMacrosValue : Comparing indexes of Macros");
							_macroSheetHashTable.put(newMacroKey.toLowerCase(),
									newMacroValue);

						} else {
							Log.Error("Excel/ExpandMacrosValue : Indexed Macro could not be found in Macros Table");
							throw new Exception(
									"Indexed Macros could not be found in Macro Table.");
						}
					}
				}
				// System.out.println("Checking Indexed Macro\t"+_indexedMacroTable+"\nChecking Normal macro table "+_macroSheetHashTable);

				if (macroValue.contains(macroValExpander)) {
					Log.Debug(String
							.format("Excel/ExpandMacrosValue : Macro value %s contains a \"{\" and \"}\" and it DOES require EXPANSION",
									macroValue));

					macroValueTrimmed = Utility
							.TrimStartEnd(macroValue, '}', 1);
					macroValueTrimmed = Utility.TrimStartEnd(macroValueTrimmed,
							'{', 0);
					allStringsToExpand = macroValueTrimmed.split(",");
					StringBuilder expandedMacroString = new StringBuilder("{");

					Log.Debug(String
							.format("Excel/ExpandMacrosValue : Macro value %s contains a \"{\" and \"}\". Before FOR Loop.",
									macroValue));

					for (String tempStringToExpand : allStringsToExpand) {
						Log.Debug(String
								.format("Excel/ExpandMacrosValue : Macro value =%s. Working on %s",
										macroValue, tempStringToExpand));

						if (tempStringToExpand.contains(macroValExpander)) {
							Log.Debug(String
									.format("Excel/ExpandMacrosValue : Calling ExpandValue .... Working on %s",
											tempStringToExpand));
							// /////////////////TODO : Implement ExpandValue
							// method...........
							// Eliminating the { } from the macro values
							tempStringToExpand = tempStringToExpand.replace(
									"{", "");
							tempStringToExpand = tempStringToExpand.replace(
									"}", "");

							StringBuilder exec = expandedMacroString
									.append(ExpandValue(tempStringToExpand));
							Log.Debug(String
									.format("Excel/ExpandValue : Executing Expanded Macro %s",
											exec));
						} else {
							expandedMacroString.append(macroValExpander);

						}
						Log.Debug(String
								.format("Excel/ExpandMacrosValue : Expanded Macro so far is -> %s",
										expandedMacroString.toString()));
						expandedMacroString.append(",");
					}

					String expandedMacro = Utility.TrimStartEnd(
							expandedMacroString.toString(), ',', 1) + "}";
					Log.Debug(String
							.format("Excel/ExpandMacrosValue : End of the Function  with macroValue = %s and return Value as = %s",
									macroValue, expandedMacro));

					return expandedMacro;
				} else {
					Log.Debug(String
							.format("Excel/ExpandMacrosValue : Macro value %s contains a \"{\" and \"}\" but DOES NOT require EXPANSION",
									macroValue));
					Log.Debug(String
							.format("Excel/ExpandMacrosValue : End of the Function  with macroValue = %s and return Value as = %s",
									macroValue, macroValue));

					return macroValue;
				}

			} else {
				// / If this doesnot start with a "{" or a "} , then return the
				// same value.
				Log.Debug(String
						.format("Excel/ExpandMacrosValue : End of the Function  with macroValue = %s and return Value as = %s",
								macroValue, macroValue));
				return macroValue;
			}
		} catch (Exception ex) {
			Log.Error(String.format(
					"Excel/ExpandMacrosValue: Exception raised is ..: %s",
					ex.getMessage()));
			throw new Exception(String.format(
					"Excel/ExpandMacrosValue: Exception raised is ..: %s",
					ex.getMessage()));
		}
	}// /ExpandMacrosValue

	// /////////////////TODO : Implement ExpandValue method...........
	// /////////////////Not fully implemented
	// /<param name="valueToExpand"></param>
	private String ExpandValue(String valueToExpand) {
		Log.Debug(String
				.format("Excel/ExpandValue : Start of the Function with valueToExpand = %s",
						valueToExpand));

		// / Splitting the Value on the basis of ".." i.e. MacroValExpander
		String[] stringsToCompare = valueToExpand.split("\\..");

		Log.Debug(String.format(
				"Excel/ExpandValue : Length of stringsToCompare = %d",
				stringsToCompare.length));

		if (stringsToCompare.length < 2) {
			Log.Debug(String
					.format("Excel/ExpandValue : [stringsToCompare.Length < 2] End of the Function  with valueToExpand = %s and return Value as = %s",
							valueToExpand, valueToExpand));
			return valueToExpand;
		}

		String[] stringToReturnTemp;
		String firstVal = stringsToCompare[0];

		Log.Debug(String
				.format("Excel/ExpandValue : firstVal = %s. ", firstVal));
		String secondVal = stringsToCompare[1];

		Log.Debug(String.format("Excel/ExpandValue : secondVal = %s. ",
				secondVal));

		int firstIntVal = 0, secondIntVal = 0;
		firstIntVal = Integer.parseInt(firstVal);
		secondIntVal = Integer.parseInt(secondVal);

		if (firstIntVal > 0 && secondIntVal > 0) {
			Log.Debug("Excel/ExpandValue : firstIntVal && secondIntVal are integers.");
			if (firstIntVal > secondIntVal) {
				Log.Debug(String
						.format("Excel/ExpandValue : firstIntVal = %s  > secondIntVal =%s ",
								firstIntVal, secondIntVal));
				stringToReturnTemp = new String[firstIntVal - secondIntVal + 1];
				for (int i = firstIntVal; i >= secondIntVal; --i) {
					Log.Debug(String.format(
							"Excel/ExpandValue : Working on Index = %d", i));
					stringToReturnTemp[firstIntVal - i] = "" + i;
					Log.Debug(String
							.format("Excel/ExpandValue : stringToReturn[firstIntVal -i]  = %s",
									stringToReturnTemp[firstIntVal - i]));
				}
			} else {
				Log.Debug(String.format("Excel/ExpandValue : firstIntVal ="
						+ firstIntVal + "  <= secondIntVal = " + secondIntVal));
				stringToReturnTemp = new String[secondIntVal - firstIntVal + 1];
				for (int i = firstIntVal; i <= secondIntVal; ++i) {
					Log.Debug("\nExcel/ExpandValue : Working on Index = " + i
							+ "\n");
					stringToReturnTemp[i - firstIntVal] = "" + i;
					Log.Debug("Excel/ExpandValue : stringToReturn[i - firstIntVal]  = "
							+ stringToReturnTemp[i - firstIntVal]);
				}
			}

			return Utility.join(",", stringToReturnTemp, 0,
					stringToReturnTemp.length - 1);
		}
		// Assume that there are 2 string after the split...we will be finding
		// the diff between the two strings..
		// //////////////////////////////////////////////////////////////////////////////////////
		/*
		 * Log.Debug("\nExcel/ExpandValue : Calling DiffCharCodes for " +
		 * stringsToCompare[0]); int[] a_codes =
		 * DiffCharCodes(stringsToCompare[0], true);
		 * 
		 * Log.Debug("\nExcel/ExpandValue : Calling DiffCharCodes for " +
		 * stringsToCompare[1]); int[] b_codes =
		 * DiffCharCodes(stringsToCompare[1], true);
		 * 
		 * Log.Debug("\nExcel/ExpandValue : Calling DiffInt for a_codes & b_codes"
		 * ); Diff.Item[] diffs = Diff.DiffInt(a_codes, b_codes);
		 * 
		 * Log.Debug("Excel/ExpandValue : diffs.Length = " + diffs.Length); if
		 * (diffs.Length != 1) { Log.Debug(
		 * "\nExcel/ExpandValue : [diffs.Length != 1] End of the Function  with valueToExpand = "
		 * + valueToExpand + " and return Value as = " + valueToExpand); return
		 * valueToExpand; }
		 * 
		 * if (diffs[0].deletedA != diffs[0].insertedB) { Log.Debug(
		 * "\nExcel/ExpandValue : [diffs[0].deletedA != diffs[0].insertedB] End of the Function  with valueToExpand = "
		 * + valueToExpand + " and return Value as = " + valueToExpand); return
		 * valueToExpand; }
		 * 
		 * Diff.Item it = diffs[0];
		 * 
		 * ArrayList firstCharSet = new ArrayList();
		 * 
		 * Log.Debug("\nExcel/ExpandValue : Finding difference in first word. ");
		 * // Find difference in the first Word if (it.deletedA > 0) { for (int
		 * m = 0; m < it.deletedA; m++) {
		 * firstCharSet.add(stringsToCompare[0][it.StartA + m]); } } ArrayList
		 * secondCharSet = new ArrayList(); // Find difference in the second
		 * Word
		 * 
		 * Log.Debug("\nExcel/ExpandValue : Finding difference in Second Word. ")
		 * ; if (it.insertedB > 0) { for (int m = 0; m < it.insertedB; m++) {
		 * secondCharSet.add(stringsToCompare[1][it.StartB + m]); } }
		 * 
		 * 
		 * String[] stringToReturn;
		 * 
		 * firstVal = new string((char[])(firstCharSet.ToArray(typeof(char))));
		 * Log.Debug("\nExcel/ExpandValue : firstVal = . " + firstVal);
		 * secondVal = new
		 * string((char[])(secondCharSet.ToArray(typeof(char))));
		 * Log.Debug("\nExcel/ExpandValue : secondVal = . " + secondVal);
		 * 
		 * firstIntVal = 0; secondIntVal = 0;
		 * firstIntVal=Integer.parseInt(firstVal);
		 * secondIntVal=Integer.parseInt(secondVal); if (firstIntVal>0 &&
		 * secondIntVal>0) { Log.Debug(
		 * "\nExcel/ExpandValue : firstIntVal && secondIntVal are integers. ");
		 * if (firstIntVal > secondIntVal) { _log.info(string.Format(
		 * "Excel/ExpandValue : firstIntVal = {0}  > secondIntVal = {1}",
		 * firstIntVal, secondIntVal)); stringToReturn = new string[firstIntVal
		 * - secondIntVal + 1]; for (int i = firstIntVal; i >= secondIntVal;
		 * --i) {
		 * _log.info(string.Format("Excel/ExpandValue : Working on Index = {0}",
		 * i)); string tempVal = stringsToCompare[0];
		 * _log.info(string.Format("Excel/ExpandValue : tempVal  = {0} ",
		 * tempVal)); stringToReturn[firstIntVal - i] =
		 * tempVal.Remove(it.StartA, it.deletedA); _log.info(string.Format(
		 * "Excel/ExpandValue : stringToReturn[firstIntVal -i]  = {0} ",
		 * stringToReturn[firstIntVal - i])); stringToReturn[firstIntVal - i] =
		 * stringToReturn[firstIntVal - i].Insert(it.StartA, i.ToString());
		 * 
		 * _log.info(string.Format(
		 * "Excel/ExpandValue : stringToReturn[firstIntVal -i]  = {0} ",
		 * stringToReturn[firstIntVal - i])); } } else {
		 * Log.Debug(string.Format(
		 * "Excel/ExpandValue : firstIntVal = {0}  <= secondIntVal = {1}",
		 * firstIntVal, secondIntVal)); stringToReturn = new string[secondIntVal
		 * - firstIntVal + 1]; for (int i = firstIntVal; i <= secondIntVal; ++i)
		 * {
		 * _log.info(string.Format("Excel/ExpandValue : Working on Index = {0}",
		 * i)); string tempVal = stringsToCompare[0];
		 * _log.info(string.Format("Excel/ExpandValue : tempVal  = {0} ",
		 * tempVal)); stringToReturn[i - firstIntVal] =
		 * tempVal.Remove(it.StartA, it.deletedA); _log.info(string.Format(
		 * "Excel/ExpandValue : stringToReturn[i - firstIntVal]  = {0} ",
		 * stringToReturn[i - firstIntVal])); stringToReturn[i - firstIntVal] =
		 * stringToReturn[i - firstIntVal].Insert(it.StartA, i.ToString());
		 * _log.info(string.Format(
		 * "Excel/ExpandValue : stringToReturn[i - firstIntVal]  = {0} ",
		 * stringToReturn[i - firstIntVal])); } } } else { Log.Debug(
		 * "\nExcel/ExpandValue : firstIntVal || secondIntVal is a String....Calling GenerateCartesianProduc "
		 * ); // Get all the possible Combinations
		 * IEnumerable<IEnumerable<char>> result =
		 * GenerateCartesianProduct(firstCharSet, secondCharSet);
		 * 
		 * ArrayList allCombination = new ArrayList(); foreach
		 * (IEnumerable<char> subList in result) { string tempValue =
		 * string.Empty; for (char val : subList) { tempValue += val; }
		 * Log.Debug
		 * ("Excel/ExpandValue : Inside allCombination loop...tempValue = " +
		 * tempValue); allCombination.Add(tempValue); }
		 * 
		 * // Now we have list of all the combinations...the only thing is to
		 * append this in the already existing string..
		 * 
		 * Log.Debug(
		 * "Excel/ExpandValue : Expanding the Value NOW in a Loop  with allCombination.Count = "
		 * + allCombination.Count); stringToReturn = new
		 * string[allCombination.Count]; for (int i = 0; i <
		 * allCombination.Count; ++i) {
		 * Log.Debug("\nExcel/ExpandValue : Inside the loop..working on index = "
		 * + i); string tempVal = stringsToCompare[0];
		 * Log.Debug("\nExcel/ExpandValue : tempVal = " + tempVal);
		 * stringToReturn[i] = tempVal.Remove(it.StartA, it.deletedA);
		 * Log.Debug("\nExcel/ExpandValue : stringToReturn[i] = " +
		 * stringToReturn[i]); stringToReturn[i] =
		 * stringToReturn[i].Insert(it.StartA, (string)allCombination[i]);
		 * Log.Debug("\nExcel/ExpandValue : Finally....stringToReturn[i] = " +
		 * stringToReturn[i]); } }
		 * 
		 * Log.Debug(
		 * "Excel/ExpandValue : End of the Function  with valueToExpand = " +
		 * valueToExpand + " and return Value as = " + string.Join(",",
		 * stringToReturn));
		 * 
		 * return join(",", stringToReturn,0,stringToReturn.length-1);
		 */
		// //////////////////////////////////////////////////////////////////////////////////////////////////////////
		return Utility.join(",", stringsToCompare, 0,
				stringsToCompare.length - 1);

	}// ExpandValue

	/**
	 * 
	 * @param name
	 * @return result as boolean
	 */
	private static boolean checkExternalIndexMacro(String name) {
		boolean result = false;
		// System.out.println("the name:- "+
		// name+" contains # "+name.contains("#")+" contains '.' "+name.contains("."));
		if (name.contains("#") && name.contains("."))
			result = true;
		return result;
	}

	// Function to append a namespace for the Macro/Molecule
	/**
	 * 
	 * @param name
	 * @param nameSpace
	 * @return macro key
	 * @throws Exception
	 */

	public static String AppendNamespace(String name, String nameSpace)
			throws Exception {

		try {
			Log.Debug("Excel/AppendNamespace : Start of the Function with name = "
					+ name + " and namespace = " + nameSpace);

			String returnValue = name;
			if (name == null) {
				// throw new Exception("Name is null");
				returnValue = "";
				return returnValue;
			}
			// Dont append namespace, if this already contains a "." or a
			// namespace.
			if (name.indexOf(".") >= 0 && !checkExternalIndexMacro(name)) {
				Log.Debug(String
						.format("Excel/AppendNamespace : End of the Function with name = %s and namespace = %s returnValue = %s",
								name, nameSpace, returnValue));

				return returnValue.toLowerCase();
			}

			if (name.startsWith("$$")) {
				returnValue = "$$" + nameSpace.toLowerCase() + "."
						+ name.substring(2).toLowerCase();
			} else if (name.startsWith("$")) {
				returnValue = "$" + nameSpace.toLowerCase() + "."
						+ name.substring(1).toLowerCase();
			} else {
				returnValue = nameSpace.toLowerCase() + "."
						+ name.toLowerCase();
			}
			Log.Debug(String
					.format("Excel/AppendNamespace : End of the Function with name = %s and namespace = %s returnValue = %s",
							name, nameSpace, returnValue));

			return Utility.TrimStartEnd(returnValue, '.', 1);
		} catch (Exception e) {
			Log.Error("Excel/AppendNamespace : Exception in function message is : "
					+ e.getMessage());
			return null;

		}
	}

	/**
	 * 
	 * @param inputFileName
	 * @throws Exception
	 */
	private void ReadExternalMacros(String inputFileName) throws Exception {
		File inputFile = null;
		FileInputStream inFile = null;
		try {

			inputFile = new File(inputFileName);
			if (!inputFile.exists()) {
				String error = "Excel/ReadExternalMacros :  External Excel file (INCLUDE) specified  "
						+ inputFileName
						+ " does not exits. Enter a valid file path";
				Log.Error(error);
				throw new Exception(error);
			}

			String fileName = inputFile.getName();
			int i;
			for (i = fileName.length() - 1; i > 0; i--) {
				if (fileName.charAt(i) == '.') {
					break;
				}
			}
			String nameSpace = namespaceMap.get(inputFileName);
			if (nameSpace == null)
				nameSpace = fileName.substring(0, i).toLowerCase();
			else
				nameSpace = nameSpace.toLowerCase();

			/** Create a POIFSFileSystem object **/
			inFile = new FileInputStream(inputFile);
			POIFSFileSystem inputFileSystem = new POIFSFileSystem(inFile);

			Log.Debug("Excel/ReadExternalMacros : Opening the excel File "
					+ inputFileName);
			Log.Debug("Excel/ReadExternalMacros : Creating new workbook object ");

			// / Create a workbook using the File System
			HSSFWorkbook _workBook_extern = new HSSFWorkbook(inputFileSystem);

			Log.Debug("Excel/ReadExternalMacros : The excel File "
					+ inputFileName + " opened successfully");

			ReadMacroSheet(_workBook_extern, nameSpace);

			Log.Debug(String
					.format("Excel/ReadExternalMacros : The Macro Sheet - %s of Excel Sheet %s read successfully.",
							MacroSheetName, inputFileName));

		} catch (Exception ex) {
			Log.Error(String
					.format("Excel/ReadExternalMacros : Error occured while Reading the External (INCLUDE) Excel file %s \nMessage : %s ",
							inputFileName, ex.getMessage()));
			inFile.close();
			throw new Exception(
					String.format(
							"Excel/ReadExternalMacros : Error occured while Reading the External (INCLUDE) Excel file %s \nMessage : %s ",
							inputFileName, ex.getMessage()));
		}
	}

	// AppendNamespace

	// / Function to read external Included Sheets. This will read the following
	// sheets -
	// / 1. Macros Sheet.
	// / 2. Molecule Sheet
	// / 3. Prototypes Sheet
	// / Provided they are present.
	private void ReadExternalSheets(String inputFileName) throws Exception,
			MoleculeDefinitionException {

		File inputFile = null;
		FileInputStream inFile = null;
		try {

			Log.Debug("Excel/ReadExternalSheets : Start of the function with Excel sheet name as : "
					+ inputFileName);
			inputFile = new File(inputFileName);
			if (!inputFile.exists()) {
				String error = "Excel/ReadExternalSheets :  External Excel file (INCLUDE) specified  "
						+ inputFileName
						+ " does not exits. Enter a valid file path";
				Log.Error(error);
				throw new Exception(error);
			}

			String fileName = inputFile.getName();
			int i;
			for (i = fileName.length() - 1; i > 0; i--) {
				if (fileName.charAt(i) == '.') {
					break;
				}
			}
			String nameSpace = namespaceMap.get(inputFileName);
			if (nameSpace == null)
				nameSpace = fileName.substring(0, i).toLowerCase();
			else
				nameSpace = nameSpace.toLowerCase();

			/** Create a POIFSFileSystem object **/
			inFile = new FileInputStream(inputFile);
			POIFSFileSystem inputFileSystem = new POIFSFileSystem(inFile);

			Log.Debug("Excel/ReadExternalSheets : Opening the excel File "
					+ inputFileName);
			Log.Debug("Excel/ReadExternalSheets : Creating new workbook object ");

			// / Create a workbook using the File System
			HSSFWorkbook _workBook_extern = new HSSFWorkbook(inputFileSystem);

			Log.Debug("Excel/ReadExternalSheets : The excel File "
					+ inputFileName + " opened successfully");

			Log.Debug("Excel/ReadExternalSheets : Reading the Macro Sheet "
					+ MacroSheetName);
			// system.out.println("READING Macro SHEET OF " +
			// inputFileName+"\n");

			// ReadMacroSheet(_workBook_extern, nameSpace);
			// System.out.println("Macro Values of: "+nameSpace+" - "+readHashTable(_macroSheetHashTable));

			Log.Debug(String
					.format("Excel/ReadExternalSheets : The Macro Sheet - %s of Excel Sheet %s read successfully.",
							MacroSheetName, inputFileName));

			Log.Debug("Excel/ReadExternalSheets : Reading the Prototypes Sheet "
					+ PrototypeSheetName);

			// system.out.println("READING prototype SHEET OF " + inputFileName+
			// "\n");
			ReadPrototypesSheet(_workBook_extern, nameSpace);
			readHashTable(protoTypesHT);

			Log.Debug(String
					.format("Excel/ReadExternalSheets : The Prototypes Sheet - %s of Excel Sheet %s read successfully.",
							PrototypeSheetName, inputFileName));

			Log.Debug("Excel/ReadExternalSheets : Reading the Abstract TestCase Sheet "
					+ AbstractSheetName);
			ReadAbstractTestCaseSheet(_workBook_extern, nameSpace);
			Log.Debug(String
					.format("Excel/ReadExternalSheets : The Abstract TestCase Sheet - %s of Excel Sheet %s read successfully.",
							AbstractSheetName, inputFileName));
		} catch (Exception ex) {
			Log.Error(String
					.format("Excel/ReadExternalSheets : Error occured while Reading the External (INCLUDE) Excel file %s \nMessage : %s ",
							inputFileName, ex.getMessage()));
			inFile.close();
			throw new Exception(
					String.format(
							"Excel/ReadExternalSheets : Error occured while Reading the External (INCLUDE) Excel file %s \nMessage : %s ",
							inputFileName, ex.getMessage()));
		} finally {
			Log.Debug("Excel/ReadExternalSheets: Closing the External Excel File "
					+ inputFileName);
			Log.Debug("Excel/ReadExternalSheets : The External Excel File "
					+ inputFileName + " closed successfully");
			// system.out.println("READING EXTERNAL SHEET " + inputFileName
			// +"WAS SUCCESSFULLY DONE!!!");

			// System.Threading.Thread.CurrentThread.CurrentCulture = CurrentCI;
		}
	}

	// / Function to read the Macros sheet
	private void ReadMacroSheet(HSSFWorkbook workBook, String nameSpace)
			throws Exception {

		HSSFSheet workSheet = null;
		String macroSheetName = MacroSheetName;

		Log.Debug("Excel/ReadMacroSheet : The worksheet object created");
		Log.Debug("Excel/ReadMacroSheet : Reading the WorkSheet "
				+ macroSheetName);

		try {
			// / Get the config sheet from workbook
			workSheet = workBook.getSheet(MacroSheetName);

			if (workSheet == null) {
				throw new Exception();
			}
			Log.Debug("Excel/ReadMacroSheet : Config Worksheet : "
					+ MacroSheetName + " exists and read successfully");
		} catch (Exception e) {
			Log.Error(String
					.format("Excel/ReadMacroSheet : Could not find the Macro Sheet : %s Exception Message raised is : %s",
							MacroSheetName, e.getMessage()));
			throw new Exception(
					String.format(
							"Excel/ReadMacroSheet : Could not find the Macro Sheet : %s Exception Message raised is : %s",
							MacroSheetName, e.getMessage()));
		}

		try {
			Log.Debug("Excel/ReadMacroSheet : Getting the values from the Macro Sheet. Calling GetKeyValuePair ......");
			GetKeyValuePair(workSheet, _macroSheetHashTable, "macros",
					nameSpace);

			Log.Debug("Excel/ReadMacroSheet : The values from the Macro sheet is read successfully."
					+ "\n");
		} catch (Exception ex) {
			String error = "Excel/ReadMacroSheet : Error occured while getting the values from Macro Sheet "
					+ macroSheetName
					+ "\nException Message is "
					+ ex.getMessage();
			Log.Error(error);
			throw new Exception(error);
		}
		Log.Debug("Excel/ReadMacroSheet : End of the Function with MacroSheetName as "
				+ macroSheetName);
	}// ReadMacroSheet

	// This function will add name of external sheets to an list of string
	protected void AddToExternalSheets(String key, String value) {
		if (_configSheetKeys[10].compareTo(key) == 0) {
			Log.Debug("\nExternal key : " + key);
			Log.Debug("\nExternal value : " + value);
			// System.out.println("The Value of Include file is... \t" + value);
			// If key is INCLUDE
			String[] qualifiedPaths = value.split(",");
			Log.Debug("\nPath length : " + qualifiedPaths.length);
			// /////////TODO : add ";" as a separator as well
			for (int i = 0; i < qualifiedPaths.length; i++) {
				if (qualifiedPaths[i].isEmpty()) {
					// System.out.println("Empty file");
					continue;
				}
				File f = new File(qualifiedPaths[i]);
				if (f.exists()) {
					String fileKey = f.getName();
					if (Excel.externalSheets.containsKey(fileKey)) {
						Log.Error(fileKey + " is already included \t");
						String filepath = externalSheets.get(fileKey);
						if (!filepath.equals(qualifiedPaths[i]))
							Log.Error(qualifiedPaths[i]
									+ " will not be included");
					} else {
						Excel.externalSheets.put(fileKey, qualifiedPaths[i]);
						Log.Debug("\nExternal sheets : " + qualifiedPaths[i]);
					}
					// System.out.println("\nExternal sheets : " +
					// qualifiedPaths[i]);
					// _externalSheets.add(qualifiedPaths[i]);
				} else {
					Log.Error(qualifiedPaths[i] + " file doesn't exists");
				}

			}
		}

	}// AddToExternalSheets

	// / Function to read the User sheet
	/*
	 * private void ReadUserSheet(HSSFWorkbook workBook) throws Exception{
	 * 
	 * String userSheetName = UserSheetName; Log.Debug(
	 * "Excel/ReadUserSheet : Start of the Function with UserSheetName as " +
	 * userSheetName);
	 * 
	 * HSSFSheet workSheet = null;
	 * Log.Debug("Excel/ReadUserSheet : The worksheet object created");
	 * Log.Debug("Excel/ReadUserSheet : Reading the WorkSheet " +
	 * userSheetName);
	 * 
	 * try { Log.Debug("Excel/ReadUserSheet : Getting the Users Sheet " +
	 * userSheetName + " from the WorkBook");
	 * 
	 * // get the first and only worksheet from the collection of worksheets
	 * workSheet=workBook.getSheet(userSheetName);
	 * 
	 * if(workSheet==null) throw new Exception
	 * ("Excel/ReadUserSheet : Could not find the User sheet " + userSheetName);
	 * 
	 * Log.Debug("Excel/ReadUserSheet : Worksheet " + userSheetName +
	 * " exists and read successfully"); } catch (Exception e) {
	 * Log.Error("Excel/ReadUserSheet : Could not find the User sheet " +
	 * userSheetName + " Exception message is : " + e.getMessage() );
	 * 
	 * throw new Exception
	 * ("Excel/ReadUserSheet : Could not find the User sheet " + userSheetName +
	 * " Exception message is : " + e.getMessage() ); }
	 * 
	 * try { Log.Debug(
	 * "Excel/ReadUserSheet : Getting the values from the User Sheet. Calling GetUserFromUserSheet ......"
	 * ); GetUserFromUserSheet(workSheet, _userSheetHashTable); Log.Debug(
	 * "Excel/ReadUserSheet : The values from the User sheet is read successfully."
	 * );
	 * 
	 * } catch (Exception ex) { String error =
	 * "Excel/ReadUserSheet : Error occured while getting the values from User Sheet "
	 * +userSheetName+". Exception Message is "+ ex.getMessage();
	 * Log.Error(error); throw new Exception (error); }
	 * Log.Debug("Excel/ReadUserSheet : End of the Function with UserSheetName as "
	 * + userSheetName);
	 * 
	 * }//ReadUserSheet
	 */
	// / Function will retrieve the user information from the sheet. Here we
	// have assumed that the 1st Column is the
	// / name of the User and the Second Column is the
	// / Password associated with the User. The third column is the Domain of
	// the User.
	/*
	 * private void GetUserFromUserSheet(HSSFSheet worksheet,
	 * Hashtable<String,UserData> hashTable)throws Exception { try {
	 * Log.Debug("Excel/GetUserFromUserSheet : Start of the Function.");
	 * Log.Debug(
	 * "Excel/GetUserFromUserSheet : Getting the user information from the sheet. Number of rows to read is -> "
	 * + worksheet.getLastRowNum());
	 * 
	 * Iterator rowIter = worksheet.rowIterator();
	 * 
	 * String strUserName=null,strUserPassword=null,strUserDomain=null; int
	 * userKey=-1, pswdKey=-1,domainKey=-1;
	 * 
	 * while(rowIter.hasNext()){
	 * 
	 * HSSFRow myRow=null;
	 * 
	 * try{
	 * 
	 * strUserName=null; strUserPassword=null; strUserDomain=null;
	 * 
	 * myRow = (HSSFRow) rowIter.next(); Iterator cellIter =
	 * myRow.cellIterator();
	 * 
	 * while(cellIter.hasNext()){
	 * 
	 * HSSFCell myCell = (HSSFCell) cellIter.next();
	 * 
	 * //Assign cell no. for different fields [Username, password,domain]
	 * if(userKey==-1 || pswdKey==-1 || domainKey==-1) {
	 * if(GetCellValueAsString(myCell).equalsIgnoreCase("username"))
	 * 
	 * userKey= myCell.getCellNum();
	 * 
	 * else if(GetCellValueAsString(myCell).equalsIgnoreCase("password"))
	 * 
	 * pswdKey= myCell.getCellNum();
	 * 
	 * else if(GetCellValueAsString(myCell).equalsIgnoreCase("domain"))
	 * 
	 * domainKey= myCell.getCellNum(); }
	 * 
	 * else if(userKey!=-1 || pswdKey!=-1 || domainKey!=-1) {
	 * if(myCell.getCellNum()==userKey)
	 * 
	 * strUserName=GetCellValueAsString(myCell);
	 * 
	 * else if(myCell.getCellNum()==pswdKey)
	 * 
	 * strUserPassword=GetCellValueAsString(myCell);
	 * 
	 * else if(myCell.getCellNum()==domainKey)
	 * 
	 * strUserDomain=GetCellValueAsString(myCell); } }//CellIterator loop
	 * 
	 * if (StringUtils.isNotBlank(strUserName) &&
	 * StringUtils.isNotBlank(strUserPassword) &&
	 * StringUtils.isNotBlank(strUserDomain)) { Log.Debug(String.format(
	 * "Excel/GetUserFromUserSheet : Working on Row[%d] of the sheet. The userName is %s and Domain is %s"
	 * ,myRow.getRowNum(),strUserName,strUserDomain)); UserData tempUser = new
	 * UserData();
	 * 
	 * tempUser.userName = strUserName; tempUser.userPassword = strUserPassword;
	 * tempUser.domain = strUserDomain;
	 * 
	 * //system.out.println(String.format("User : %s pswd : %s domain : %s",
	 * strUserName,strUserPassword,strUserDomain));
	 * 
	 * Log.Debug(String.format(
	 * "Excel/GetUserFromUserSheet : Working on Row[%d] of the sheet. userName %s along with the User Object is getting inserted into the hashtable."
	 * ,myRow.getRowNum(),strUserName));
	 * //system.out.println("UserKey : "+strUserName+
	 * " tempUserValue : "+tempUser.toString());
	 * 
	 * hashTable.put(strUserName, tempUser);
	 * Log.Debug("Excel/GetUserFromUserSheet : Working on Row["
	 * +myRow.getRowNum()+"] of the sheet. userName "+strUserName+
	 * " along with the User Object is inserted into the hastbale."); }
	 * 
	 * }catch (Exception e) { Log.Error(
	 * "Excel/GetUserFromUserSheet : Non-string and Non-boolean value read from "
	 * + "User.file at Index : , " +myRow.getRowNum() + "\nIgnoring the line");
	 * 
	 * throw new Exception(
	 * "Excel/GetUserFromUserSheet : Non-string and Non-boolean value read from "
	 * + "User.file at Index : , " +myRow.getRowNum() + "\nIgnoring the line");
	 * }
	 * 
	 * } } catch (Exception e) { Log.Error(
	 * "Excel/GetUserFromUserSheet : Error occured while getting values from the "
	 * + " User Sheet. \n Message : " + e.getMessage());
	 * 
	 * throw new Exception(
	 * "Excel/GetUserFromUserSheet : Error occured while getting values from the "
	 * + " User Sheet. \n Exception Message is : " + e.getMessage());
	 * 
	 * } }///GetUserFromUserSheet
	 */
	// / Function to read the Prototypes sheet
	public void ReadPrototypesSheet(HSSFWorkbook workBook, String nameSpace)
			throws Exception {
		String prototypeSheetName = PrototypeSheetName;
		Log.Debug("Excel/ReadPrototypesSheet : Start of the Function with prototypeSheetName as "
				+ prototypeSheetName);

		HSSFSheet workSheet = null;
		Log.Debug("Excel/ReadPrototypesSheet : The worksheet object created");
		Log.Debug("Excel/ReadPrototypesSheet : Reading the prototypeSheetName "
				+ prototypeSheetName);

		try {
			workSheet = workBook.getSheet(prototypeSheetName);
			if (workSheet == null) // throw new
			// Exception("Excel/ReadPrototypesSheet : Could not find the prototypeSheetName "
			// + prototypeSheetName );
			{
				return;
			}
			Log.Debug("Excel/ReadPrototypesSheet : prototypeSheetName : "
					+ prototypeSheetName + " exists and read successfully");

		} catch (Exception e) {
			Log.Error("Excel/ReadPrototypesSheet : Could not find the prototypeSheetName : "
					+ prototypeSheetName);

			throw new Exception(
					"Excel/ReadPrototypesSheet : Could not find the prototypeSheetName "
							+ prototypeSheetName + " Message : "
							+ e.getMessage());
		}
		try {
			Log.Debug("Excel/ReadPrototypesSheet : Getting the values from the PrototypeSheetName. ");
			GetPrototypesSheetValues(workSheet, nameSpace);
			Log.Debug("Excel/ReadPrototypesSheet : The values from the PrototypeSheetName is read successfully.");

		} catch (Exception ex) {
			Log.Error("Excel/ReadPrototypesSheet : Error occured while getting the values from the prototypeSheetName "
					+ prototypeSheetName + ". Message : " + ex.getMessage());

			throw new Exception(
					" Excel/ReadPrototypesSheet : Error occured while getting the values from the prototypeSheetName "
							+ prototypeSheetName
							+ ". Message : "
							+ ex.getMessage());
		}

		Log.Debug("Excel/ReadPrototypesSheet : End of the Function with prototypeSheetName as "
				+ prototypeSheetName);
	}// ReadPrototypesSheet

	// / Function to read the Prototypes Sheet and Fill in the Appropriate Data
	// Structures.
	// / This will find the prototype for a Molecule/ Primitives with its
	// arguments
	// / and other details.
	private void GetPrototypesSheetValues(HSSFSheet workSheet, String nameSpace)
			throws Exception {
		int index = 1;

		try {
			Log.Debug("Excel/GetPrototypesSheetValues : Start of Function to read Prototypes sheet");
			// / Read the Labels from the Prototypes Sheet and find their index
			Hashtable<Short, String> labelIndex = new Hashtable<Short, String>();

			Log.Debug("Excel/GetPrototypesSheetValues : Calling getLabelIndex for Prototypes sheet");
			GetLabelIndex(workSheet, labelIndex);

			short argumentIndex = 0;
			// /Initially we assume that the prototype and its Arguments to be
			// Null.
			Prototype prototypeObj = null;
			Arguments argumentsObj = null;

			short nameIndex = 0;
			Set<Short> it = labelIndex.keySet();
			for (Short key : it) {
				if (labelIndex.get(key).toString()
						.equalsIgnoreCase("arguments")) {
					argumentIndex = key;
					Log.Debug("Excel/GetPrototypesSheetValues : argumentIndex = "
							+ argumentIndex);
				}

				if (labelIndex.get(key).toString().equalsIgnoreCase("name")) {
					nameIndex = key;
				}
				Log.Debug("Excel/GetPrototypesSheetValues : nameIndex = "
						+ nameIndex);
			}

			Log.Debug("Excel/GetPrototypesSheetValues : Last Prototypes Row index to read is -> "
					+ workSheet.getLastRowNum());
			Log.Debug("Excel/GetPrototypesSheetValues : Started reading the Prototypes Excel sheet at Index -> "
					+ index);

			String description = null;
			for (; index <= workSheet.getLastRowNum(); index++) {
				Log.Debug("Excel/GetPrototypesSheetValues : Reading the Abstract TestCase Excel sheet at Index -> "
						+ index);

				String valueInNameColumn = null;

				HSSFCell tempcol = workSheet.getRow(index).getCell(nameIndex);
				if (tempcol != null) {
					valueInNameColumn = GetCellValueAsString(tempcol);
				}
				// / If this is a comment then Ignore
				if (valueInNameColumn != null
						&& valueInNameColumn.toLowerCase().trim()
								.equalsIgnoreCase("comment")) {
					HSSFCell col = workSheet.getRow(index).getCell(
							(short) (nameIndex + 1));

					if (col == null) {
						throw new Exception(
								String.format(
										"Excel/GetPrototypesSheetValues :Unable to read the cell at Row/Col(%d/%d) in prototypesheet",
										index, nameIndex + 1));
					}

					description = GetCellValueAsString(workSheet.getRow(index)
							.getCell((short) (nameIndex + 1)));
					Log.Debug("Excel/GetPrototypesSheetValues : This is a Comment Section/Row..Ignoring this as this is of no use to Automation. ");
					continue;
				}

				// / Else if this is a new Prototype
				if (valueInNameColumn != null
						&& StringUtils.isNotEmpty(valueInNameColumn)) {
					if (prototypeObj != null) {
						if (argumentsObj != null) {
							prototypeObj.arguments.add(argumentsObj);
							argumentsObj = null;
						}

						protoTypesHT.put(
								AppendNamespace(prototypeObj.prototypeName,
										nameSpace), prototypeObj);
					}

					// / Read the TestCase Section of the Row
					Log.Debug("Excel/GetPrototypesSheetValues : Calling ReadPrototypes with row index as : "
							+ index);

					prototypeObj = ReadPrototypes(workSheet, labelIndex, index,
							nameIndex, description, nameSpace);
				}

				// / First checking for the Arguments column
				HSSFCell col = workSheet.getRow(index).getCell(
						(short) (argumentIndex));

				// HSSFRow.MissingCellPolicy();
				if (col == null) {
					Log.Debug((String
							.format("Excel/GetPrototypesSheetValues :Unable to read the cell at Row/Col(%d/%d) in prototypesheet",
									index, nameIndex)));
				} else {
					String valueInArgumentsColumn = GetCellValueAsString(workSheet
							.getRow(index).getCell((short) (argumentIndex)));
					if (StringUtils.isNotBlank(valueInArgumentsColumn)) {
						if (argumentsObj != null && prototypeObj != null) {
							prototypeObj.arguments.add(argumentsObj);
						}

						// / Read the Action Section of the Row
						Log.Debug("Excel/GetPrototypesSheetValues : Calling ReadArgumentsSection with row index as : "
								+ index);
						// system.out.println(prototypeObj.prototypeName);
						argumentsObj = ReadArgumentsSection(workSheet,
								labelIndex, index, argumentIndex);
					}
				}
				// / Checking the last Row and saving the
				// Prototypes/Arguments/in their proper Datastructures
				if (index == workSheet.getLastRowNum()) {
					Log.Debug("Excel/GetPrototypesSheetValues : Read the last Row of the Prototypes sheet : "
							+ index);
					if (argumentsObj != null && prototypeObj != null) {
						prototypeObj.arguments.add(argumentsObj);
					}
					if (prototypeObj != null) {
						protoTypesHT.put(
								AppendNamespace(prototypeObj.prototypeName,
										nameSpace), prototypeObj);
					}
					break;
				}
			}
			Log.Debug("Excel/GetPrototypesSheetValues : End of Function to read Prototypes sheet");
		} catch (Exception e) {
			Log.Error("Excel/GetPrototypesSheetValues : Exception occured, exception message is : "
					+ e.getMessage());
			throw new Exception("Exception occured, exception message is : "
					+ e.getMessage());
		}

	}// GetPrototypesSheetValues

	// / Method to read the first row in the testcase worksheet.
	public void GetLabelIndex(HSSFSheet sheet,
			Hashtable<Short, String> indexHashtable) throws Exception {
		try {

			Log.Debug("Excel/getLabelIndex : Start of Function to read the First Row in the TestCase Sheet");

			Iterator rowIter = sheet.rowIterator();

			if (rowIter.hasNext()) {

				HSSFRow myRow = (HSSFRow) rowIter.next();
				Iterator cellIter = myRow.cellIterator();

				Log.Debug("Excel/getLabelIndex : Started reading the Cells of the First Row");
				while (cellIter.hasNext()) {

					HSSFCell myCell = (HSSFCell) cellIter.next();
					Log.Debug("Excel/getLabelIndex : Working on Cell["
							+ myCell.getCellNum() + "] of the First Row");

					if (StringUtils.isNotBlank(GetCellValueAsString(myCell))) {

						Log.Debug("Excel/getLabelIndex :Inserting the Value in the Hashtable for Cell["
								+ myCell.getCellNum()
								+ "] = "
								+ GetCellValueAsString(myCell));
						indexHashtable.put(myCell.getCellNum(),
								GetCellValueAsString(myCell).toLowerCase()
										.trim());
					}
				}
			}
		} catch (Exception e) {

			Log.Error("Excel/getLabelIndex  : Exception occured, exception message is : "
					+ e.getMessage());
			throw new Exception(
					"Excel/getLabelIndex  : Exception occured, exception message is : "
							+ e.getMessage());

		}
		Log.Debug("Excel/getLabelIndex : End of Function to read the First Row in the TestCase Sheet");

	}// /GetLabelIndex

	// / Function to Read the Prototypes Section.
	// / <param name="worksheet"></param>
	// / <param name="labelIndex"></param>
	// / <param name="index"></param>
	// / <param name="nameIndex"></param>
	// / <param name="description"></param>
	private Prototype ReadPrototypes(HSSFSheet worksheet,
			Hashtable<Short, String> labelIndex, int index, short nameIndex,
			String description, String nameSpace) throws Exception {
		Prototype prototypes = new Prototype();
		{

			Log.Debug("Excel/ReadPrototypes : Start of the Function with Row Index = "
					+ index
					+ " and  Index of nameIndex column as : "
					+ nameIndex);
			HSSFCell col = worksheet.getRow(index).getCell((nameIndex));

			if (col == null) {
				throw new Exception(
						String.format(
								"Excel/ReadPrototypes :Unable to read the cell at Row/Col(%d/%d) in prototypesheet",
								index, nameIndex));
			}

			prototypes.prototypeName = GetCellValueAsString(worksheet.getRow(
					index).getCell((nameIndex)));

			Log.Debug("Excel/ReadPrototypes : Before adding Namespace Row["
					+ index + "] prototypeName = " + prototypes.prototypeName);

			prototypes.prototypeName = AppendNamespace(
					prototypes.prototypeName, nameSpace);
			Log.Debug("Excel/ReadPrototypes : After adding Namespace Row["
					+ index + "] prototypeName = " + prototypes.prototypeName);

			prototypes.prototypeDescription = description;
			Log.Debug("Excel/ReadPrototypes : Row[" + index
					+ "] prototypes.prototypeDescription = "
					+ prototypes.prototypeDescription);

			col = worksheet.getRow(index).getCell(
					getKey(labelIndex, "property"));

			if (col == null) {
				throw new Exception(
						String.format(
								"Excel/ReadPrototypes :Unable to read the cell at Row/Col(%d/%d) in prototypesheet",
								index, getKey(labelIndex, "property")));
			}

			String property = GetCellValueAsString(
					worksheet.getRow(index).getCell(
							getKey(labelIndex, "property"))).toLowerCase()
					.trim();
			String[] setProperties = property.split(",");

			for (int i = 0; i < setProperties.length; i++) {
				if (setProperties[i].compareToIgnoreCase("atom") == 0) {
					prototypes.isMolecule = false;
					Log.Debug("Excel/ReadPrototypes : Row[" + index
							+ "] prototypes.isMolecule = "
							+ prototypes.isMolecule);
				} else if (setProperties[i].contains("=")) {
					String[] keyValuePair = setProperties[i].split("=");// ,
					// StringSplitOptions.RemoveEmptyEntries);
					if (keyValuePair[0].compareTo("DLL") == 0) {
						// /Get In-Process dll name.
						prototypes.InProcessDllName = setProperties[1]
								.substring(setProperties[1].indexOf('=') + 1)
								.trim();
						Log.Debug("Excel/ReadPrototypes : Row[" + index
								+ "] prototypes.InProcessDllName =  "
								+ prototypes.InProcessDllName);
					}
				}
			}
			col = worksheet.getRow(index).getCell(
					getKey(labelIndex, "arg_count"));
			int argCount = 0;
			if (col == null) {
				throw new Exception(
						String.format(
								"Excel/ReadPrototypes :Unable to read the cell for argument count at Row/Col(%d/%d) in prototypesheet",
								index, getKey(labelIndex, "property")));
			}
			// system.out.println("Cell's value is : " + col.getCellType()
			// +" Numeric is : "+HSSFCell.CELL_TYPE_NUMERIC + "String is : "+
			// HSSFCell.CELL_TYPE_STRING + "Formula " +
			// HSSFCell.CELL_TYPE_FORMULA );
			if (col.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
				argCount = (int) col.getNumericCellValue();
			} else if (col.getCellType() == HSSFCell.CELL_TYPE_STRING) {
				argCount = Integer.parseInt(GetCellValueAsString(col));
			}

			prototypes.argumentCount = argCount;

			Log.Debug("Excel/ReadPrototypes : Row[" + index
					+ "] prototypes.argumentCount = "
					+ prototypes.argumentCount + " argCount = " + argCount);
			Log.Debug("Excel/ReadPrototypes : End of the Function with Row Index = "
					+ index
					+ " and  Index of nameIndex column as : "
					+ nameIndex);
		}
		return prototypes;
	}// /ReadPrototypes

	// / Function to read the Arguments Section of a Prototype
	// / <param name="worksheet"></param>
	// / <param name="labelIndex"></param>
	// / <param name="index"></param>
	// / <param name="argumentsIndex"></param>
	private Arguments ReadArgumentsSection(HSSFSheet workSheet,
			Hashtable<Short, String> labelIndex, int index, int argumentsIndex)
			throws Exception {
		Arguments arguments = new Arguments();

		try {
			Log.Debug("Excel/ReadArgumentsSection : Start of the Function with Row Index = "
					+ index
					+ " and  Index of argumentsIndex column as : "
					+ argumentsIndex);

			HSSFCell col = workSheet.getRow(index).getCell(
					(short) (argumentsIndex));

			if (col == null) {
				throw new Exception(
						String.format(
								"Excel/ReadArgumentsSection :Unable to read the cell for argument name at Row/Col(%d/%d) ",
								index, argumentsIndex));
			}

			arguments.argumentName = (GetCellValueAsString(workSheet.getRow(
					index).getCell((short) (argumentsIndex))));
			Log.Debug("Excel/ReadArgumentsSection : Row[" + index
					+ "]  arguments.argumentName =  " + arguments.argumentName);

			col = workSheet.getRow(index).getCell(
					(getKey(labelIndex, "argument properties")));

			if (col == null) {
				throw new Exception(
						String.format(
								"Excel/ReadArgumentsSection :Unable to read the cell for argument properties at Row/Col(%d/%d) ",
								index,
								getKey(labelIndex, "argument properties")));
			}

			String argumentProperties = GetCellValueAsString(workSheet.getRow(
					index).getCell((getKey(labelIndex, "argument properties"))));

			if (argumentProperties.contains("ref")) {
				arguments.isRef = true;
				arguments.isValue = false;
			}

			Log.Debug("Excel/ReadArgumentsSection : Row[" + index
					+ "] arguments.isRef = " + arguments.isRef
					+ ", arguments.isValue = " + arguments.isValue);

			if (argumentProperties.contains("vector")) {
				arguments.isVector = true;
				arguments.isScalar = false;
			}

			Log.Debug("Excel/ReadArgumentsSection : Row[" + index
					+ "] arguments.isVector = " + arguments.isVector
					+ ", arguments.isScalar = " + arguments.isScalar);

			if (argumentProperties.contains("default")) {
				arguments.isMandatory = false;
				arguments.isDefaultValue = true;

			}
			Log.Debug("Excel/ReadArgumentsSection : Row[" + index
					+ "]  arguments.isMandatory = " + arguments.isMandatory);
			// system.out.println("Argument : " +arguments.argumentName +
			// " ref: " +arguments.isRef +" vector: " + arguments.isVector +
			// " default: " + arguments.isMandatory);
			Log.Debug("Excel/ReadArgumentsSection : End of the Function with Row Index = "
					+ index
					+ " and  Index of argumentsIndex column as : "
					+ argumentsIndex);

		} catch (Exception e) {
			Log.Error("Excel/ReadArgumentsSection : Exception occured while reading argument section with Row Index = "
					+ index + " .Exception message is : " + e.getMessage());
			throw new Exception(
					"Excel/ReadArgumentsSection : Exception occured while reading argument section with Row Index = "
							+ index
							+ " .Exception message is : "
							+ e.getMessage());
		}
		return arguments;
	}// /ReadArgumentsSection

	// /function returns key of a HashTable corresponding to the value supplied
	public short getKey(Hashtable<Short, String> hTable, String value) {
		short key = -1;
		Set<Short> it = hTable.keySet();
		for (short s : it) {
			if (hTable.get(s).equalsIgnoreCase(value)) {
				key = s;
				break;
			}
		}
		return key;
	}// /getKey

	// / Function to read the Abstract TestCase Sheet
	public void ReadAbstractTestCaseSheet(HSSFWorkbook workBook,
			String nameSpace) throws Exception, MoleculeDefinitionException {
		String testCaseSheetName = AbstractSheetName;
		Log.Debug("Excel/ReadAbstractTestCaseSheet : Start of the Function with Abstract TestCaseSheetName as "
				+ testCaseSheetName);

		HSSFSheet workSheet = null;
		Log.Debug("Excel/ReadAbstractTestCaseSheet : The worksheet object created");

		Log.Debug("Excel/ReadAbstractTestCaseSheet : Reading the WorkSheet "
				+ testCaseSheetName);

		try {
			workSheet = workBook.getSheet(testCaseSheetName);

			if (workSheet == null) {
				throw new Exception(
						"Excel/ReadAbstractTestCaseSheet : Could not find the Absstract TestCase sheet "
								+ testCaseSheetName);
			}

			Log.Debug("Excel/ReadAbstractTestCaseSheet : Worksheet "
					+ testCaseSheetName + " exists and read successfully");
		} catch (Exception e) {
			Log.Error("Excel/ReadAbstractTestCaseSheet : Could not find the Abstract TestCase sheet "
					+ testCaseSheetName);

			throw new Exception(
					"Excel/ReadAbstractTestCaseSheet : Could not find the Absstract TestCase sheet "
							+ testCaseSheetName
							+ " Exception Message : "
							+ e.getMessage() + "\n");
		}
		try {
			Log.Debug("Excel/ReadAbstractTestCaseSheet : Getting the values from the Abstract TestCase Sheet. Calling GetAbstractTestCaseSheetValues ......");
			GetAbstractTestCaseSheetValues(workSheet, nameSpace);
			Log.Debug("Excel/ReadAbstractTestCaseSheet : The values from the Abstract TestCase sheet is read successfully.");

		} catch (Exception ex) {
			String error = "Excel/ReadAbstractTestCaseSheet : Error occured while getting the values from Abstract TestCase Sheet "
					+ testCaseSheetName
					+ ". Exception Message is \n"
					+ ex.getMessage();
			Log.Error(error);
			throw new Exception(error);
		}

		Log.Debug("Excel/ReadAbstractTestCaseSheet : End of the Function with Abstract TestCase SheetName as "
				+ testCaseSheetName);
	}// /ReadAbstractTestCaseSheet

	// / Function to read the Data from the Abstract TestCase sheet. This
	// function will read the Abstract TestCase sheet and will
	// / populate the values in the DataStructures appropriately.
	boolean molecule_error = false;

	private void GetAbstractTestCaseSheetValues(HSSFSheet worksheet,
			String nameSpace) throws Exception, MoleculeDefinitionException {
		Hashtable<Short, String> _mapHashTable = new Hashtable<Short, String>();

		try {
			int index = 1;
			Log.Debug("Excel/GetAbstractTestCaseSheetValues : Start of Function to read Abstract Test Case sheet");

			// / Read the Labels from the TestCase Sheet and find their index in
			// the TestSheet
			Hashtable<Short, String> labelIndex = new Hashtable<Short, String>();

			Log.Debug("Excel/GetAbstractTestCaseSheetValues : Calling getLabelIndex for Abstract Test Case sheet");
			GetLabelIndex(worksheet, labelIndex);

			// / Find the number of Action and Verification Arguments.
			Log.Debug("Excel/GetAbstractTestCaseSheetValues : Calling FindTotalActionVerificationArgs for Abstract Test Case sheet");

			FindTotalActionVerificationArgs(labelIndex);

			Log.Debug("Excel/GetAbstractTestCaseSheetValues : Successfully Called FindTotalActionVerificationArgs for Test Case sheet");
			Log.Debug("Excel/GetAbstractTestCaseSheetValues : Total number of Action Arguments are "
					+ TotalActionArgs);
			Log.Debug("Excel/GetAbstractTestCaseSheetValues : Total number of Verification Arguments are "
					+ TotalVerificationArgs);

			Log.Debug("Excel/GetAbstractTestCaseSheetValues : Calling GetActionVerificationMap to read the Header from the Excel sheet.");
			index = GetActionVerificationMap(worksheet, labelIndex,
					_mapHashTable);

			Log.Debug("Excel/GetAbstractTestCaseSheetValues : Read the Header from the TestCase Excel sheet. Index returned is -> "
					+ index);

			// / Initially we assume that the test cases their actions and their
			// verification are not read.
			TestCase testCase = null;
			Action actionObj = null;
			// Verification verifyObj = new Verification();
			Verification verifyObj = null;

			int testCaseIndex = 1;
			try {
				testCaseIndex = (int) (getKey(labelIndex, "testcase id"));

				if (testCaseIndex == -1) {
					testCaseIndex = (int) (getKey(labelIndex, "molecule id"));
				}
			} catch (Exception e) {
				Log.Error("Excel/GetAbstractTestCaseSheetValues : Exception occured, exception message is : "
						+ e.getMessage());
			}
			Log.Debug("Excel/GetAbstractTestCaseSheetValues : testCaseIndex = "
					+ testCaseIndex);

			short actionIndex = getKey(labelIndex, "action");
			Log.Debug("Excel/GetAbstractTestCaseSheetValues : actionIndex = "
					+ actionIndex);

			short verifyIndex = getKey(labelIndex, "verify");
			Log.Debug("Excel/GetAbstractTestCaseSheetValues : verifyIndex = "
					+ verifyIndex);

			Log.Debug("Excel/GetAbstractTestCaseSheetValues : Last Abstract TestCase Row index to read is -> "
					+ worksheet.getLastRowNum());
			Log.Debug("Excel/GetAbstractTestCaseSheetValues : Started reading the Abstract TestCase Excel sheet at Index -> "
					+ index);

			String description = null;

			// System.out.println("The RowCount "+worksheet.getLastRowNum());
			for (; index <= worksheet.getLastRowNum(); index++) {
				Log.Debug("Excel/GetAbstractTestCaseSheetValues : Reading the Abstract TestCase Excel sheet at Index -> "
						+ index);
				HSSFCell col=null;
				try{
					col = worksheet.getRow(index).getCell(
							((short) (testCaseIndex)));
				}catch(Exception e){
//					e.printStackTrace();
					Log.Error("Warn: Exception in molecule sheet of "+nameSpace+" due to empty line or cell in line number "+(index+1)+". "+e.getMessage());
					continue;
				}


				String valueInTestCaseColumn = null;

				if (col != null) {
					valueInTestCaseColumn = GetCellValueAsString(col);
				}
				// System.out.println("The values in Testcase "+valueInTestCaseColumn);
				// / If this is a comment then Ignore
				if (StringUtils.isNotBlank(valueInTestCaseColumn)
						&& valueInTestCaseColumn.toLowerCase()
								.equals("comment")) {
					// System.out.println("if comment valid ");
					col = worksheet.getRow(index).getCell(
							((short) (testCaseIndex + 1)));

					if (col != null) {

						description = GetCellValueAsString(col);
						// System.out.println("Its not Nulll "+description);
					}

					// System.out.println("\nTest Case Description : "+
					// description);
					if (StringUtils.isNotBlank(description)
							&& index == worksheet.getLastRowNum()) {
						throw new Exception(
								"Comment can't be provided at last row of "
										+ nameSpace
										+ ".xls Sheet: molecule Row: "
										+ worksheet.getLastRowNum());

					}
					Log.Debug("Excel/GetAbstractTestCaseSheetValues : This is a Comment Section/Row..Ignoring this as this is of no use to Automation. ");
					continue;
				}
				// / First checking for the Verification Methods

				String valueInVerificationColumn = null;
				col = worksheet.getRow(index).getCell(((short) (verifyIndex)));

				if (col != null) {
					valueInVerificationColumn = GetCellValueAsString(col);
				}

				if (StringUtils.isNotBlank(valueInVerificationColumn)) {
					if (verifyObj != null && actionObj != null
							&& testCase != null) {
						actionObj.verification.add(verifyObj);
					}

					// / Read the Verification Section of the Row
					Log.Debug("Excel/GetAbstractTestCaseSheetValues : Calling ReadVerificationSection with row index as : "
							+ index);

					if (StringUtils.isNotBlank(valueInTestCaseColumn)){
						verifyObj = ReadVerificationSection(worksheet,
								labelIndex, _mapHashTable, index, verifyIndex,
								AbstractSheetName, null, testCaseIndex,
								nameSpace);
					} else {
						verifyObj = ReadVerificationSection(worksheet,
								labelIndex, _mapHashTable, index, verifyIndex,
								AbstractSheetName, testCase, testCaseIndex,
								nameSpace);
					}
					// System.out.println("The Verify bools "+verifyObj.isNegative);
				} else {
					if (verifyObj != null && actionObj != null
							&& testCase != null) {
						actionObj.verification.add(verifyObj);
						verifyObj = null;
					}
				}
				//
				// / The read the Action
				String valueInActionColumn = null;
				col = worksheet.getRow(index).getCell(((short) (actionIndex)));
				if (col != null) {
					valueInActionColumn = GetCellValueAsString(col);
				}
				if (StringUtils.isNotBlank(valueInActionColumn)) {
					if (actionObj != null && testCase != null) {
						testCase.actions.add(actionObj);
						//

						// System.out.println("The action arguments  "+actionObj.actionArguments);

					}

					// / Read the Action Section of the Row
					Log.Debug("Excel/GetAbstractTestCaseSheetValues : Calling ReadActionSection with row index as : "
							+ index);

					if (StringUtils.isNotBlank(valueInTestCaseColumn)) {
						actionObj = ReadActionSection(worksheet, labelIndex,
								_mapHashTable, index, actionIndex,
								AbstractSheetName, null, testCaseIndex,
								nameSpace);

					} else {
						actionObj = ReadActionSection(worksheet, labelIndex,
								_mapHashTable, index, actionIndex,
								AbstractSheetName, testCase, testCaseIndex,
								nameSpace);
					}

				}

				// / Else if this is a new test case
				if (StringUtils.isNotBlank(valueInTestCaseColumn)) {
					if (testCase != null) {

						// System.out.println("Test case object coming upto  "+_forTestcaseMolecule.get(actionObj.testCaseID));
						abstractTestCase
								.put(AppendNamespace(testCase.testCaseID,
										nameSpace),(Molecule) testCase);

					}

					// / Read the TestCase Section of the Row
					Log.Debug("Excel/GetAbstractTestCaseSheetValues : Calling ReadTestCase with row index as : "
							+ index);
					// System.out.println("The description 1d "+description+" ---- "+worksheet+"++++++"+labelIndex+"_______"+index+"::::::::"+nameSpace);
					testCase = ReadTestCase(worksheet, labelIndex, index,
							testCaseIndex, description, nameSpace,false);

					// System.out.println("The control coming to 2d "+actionObj);
					try {
						String acnval = actionObj.testCaseID;
					} catch (Exception e) {
						// System.out.println("Problems "+e.getMessage());
						continue;

					}
					// System.out.println("mol defn "+actionObj._actionmoleculeArgDefn);
					testCase._testcasemoleculeArgDefn
							.addAll(actionObj._actionmoleculeArgDefn);
					description = null;
					// System.out.println("The control coming to 3d");
				}
				// System.out.println("The index of row "+index+" the last row "+worksheet.getLastRowNum());
				// / Checking the last Row and saving the Abstract
				// TestCases/Actions/Verification in their proper Datastructures
				if (index == worksheet.getLastRowNum()) {
					// check the index number... or go to readaction method to
					// check

					Log.Debug("Excel/GetAbstractTestCaseSheetValues : Read the last Row of the Test Case sheet : "
							+ index);
					Log.Debug("Excel/GetAbstractTestCaseSheetValues : Saving the Action/ Verification and the TestCase Datastructures: "
							+ index);

					if (verifyObj != null && actionObj != null
							&& testCase != null) {
						actionObj.verification.add(verifyObj);
					}

					if (actionObj != null && testCase != null) {
						// System.out.println("The action object "+actionObj.isComment+"\nAction definition "+actionObj._actionmoleculeArgDefn);
						testCase.actions.add(actionObj);
						// Implement a method to get the exact object.

					}

					if (testCase != null) {
						abstractTestCase
								.put(AppendNamespace(testCase.testCaseID,
										nameSpace),(Molecule) testCase);

					}
				}
			}

		} catch (MoleculeDefinitionException md) {
			throw new Exception(
					"Excel/GetAbstractTestCaseSheetValues :Exception occured, exception message is : "
							+ "\n" + md.getMessage());
		} catch (Exception e) {
			Log.Error("Excel/GetAbstractTestCaseSheetValues : Exception occured, exception message is : "
					+ "\n" + e.getMessage());
			throw new Exception(
					"Excel/GetAbstractTestCaseSheetValues :Exception occured, exception message is : "
							+ "\n" + e.getMessage());
		}

		Log.Debug("Excel/GetAbstractTestCaseSheetValues : End of Function to read Test Case sheet");
	}// GetAbstractTestCaseSheetValues

	// / Function reads the Verification section of the TestCase sheet.
	// / <param name="Worksheet">Object of the Worksheet</param>
	// / <param name="index">Row of the TestCase sheet that one is
	// reading</param>
	// / <param name="verifyIndex">Index of the Verify Column</param>
	private Verification ReadVerificationSection(HSSFSheet worksheet,
			Hashtable<Short, String> labelIndex,
			Hashtable<Short, String> _mapHashTable, int index, int verifyIndex,
			String sheetName, TestCase testCase, int testcaseIndex,
			String nameSpace) throws Exception {
		Log.Debug("Excel/ReadVerificationSection : Start of the Function with Row Index = "
				+ index
				+ " and  Index of Verification column as : "
				+ verifyIndex + ". The name of the Sheet is - > " + sheetName);
		Verification verifyObj = new Verification();

		if (!verificationSwitching) {
			// / verification swithcing OFF.
			Log.Debug("Excel/ReadVerificationSection : Verifcation swithch flag is off.");
			Log.Debug("Excel/ReadVerificationSection : End of the Function.");
			return null;
		}

		String property = null;

		HSSFCell col = worksheet.getRow(index).getCell(
				getKey(labelIndex, "property"));

		if (col != null) {
			property = GetCellValueAsString(col);
		}

		try {
			if (StringUtils.isNotBlank(property)
					&& (property.contains("comment") || property
							.contains("rem"))) {
				// /verification is a Comment.
				Log.Debug("Excel/ReadVerificationSection : Verifcation is a Comment");
				Log.Debug("Excel/ReadVerificationSection : End of the Function.");
				return null;
			}
			if (property != null
					&& property.toLowerCase().equalsIgnoreCase(NEGATIVE)) {
				Log.Debug("Excel/ReadActionSection: Verification(row) is Negative ");

				verifyObj.isNegative = true;

			}
			if (property != null
					&& (property.toLowerCase().equalsIgnoreCase("neg-verify") || property
							.toLowerCase().equalsIgnoreCase("negative-verify"))) {
				Log.Debug("Excel/ReadActionSection: Verification(only) is Negative ");

				verifyObj.isVerifyNegative = true;

			}
			else if (property != null
					&& property.toLowerCase().equalsIgnoreCase("Ignore")) {
				Log.Debug("Excel/ReadActionSection: Action(row) is Ignore ");
				verifyObj.isIgnore = true;

			}
			
			
			verifyObj.name = GetCellValueAsString(worksheet.getRow(
					index).getCell((short) (verifyIndex)));

			if (verifyObj.name == null) {
				return null;
			}
			if (StringUtils.isNotBlank(verifyObj.name)) // system.out.print("\nverification Name "
			// +verifyObj.verificationName);
			{
				//check if it is a action?
				Log.Debug("Excel/ReadVerificationSection : Row[" + index
						+ "] ...Verification = " + verifyObj.name);
			}

			String testCaseID = null;
			UserData userObj = new UserData();
			if (testCase != null) {
				testCaseID = testCase.testCaseID;
				userObj = testCase.userObj;
			} else {
				String user = null;

				col = worksheet.getRow(index).getCell(
						getKey(labelIndex, "user"));
				if (col == null) {
					user = StringUtils.EMPTY;
				} else {
					user = GetCellValueAsString(col);
				}
				Log.Debug("Excel/ReadVerificationSection : Row[" + index
						+ "] ...user = " + user);

				if (_macroSheetHashTable.get(AppendNamespace(user, nameSpace)) != null) {
					user = (String) _macroSheetHashTable.get(AppendNamespace(
							user, nameSpace));
					Log.Debug("Excel/ReadVerificationSection : Row[" + index
							+ "] Actual Value of  user from Macro sheet is = "
							+ user);
				}

				/*
				 * if (_userSheetHashTable.get(user) != null) {
				 * Log.Debug("Excel/ReadVerificationSection : Row["
				 * +index+"] User Credentials and Information for User "
				 * +user+" exists in the User Sheet"); userObj =
				 * (UserData)_userSheetHashTable.get(user); }
				 */col = worksheet.getRow(index).getCell((short) testcaseIndex);
				if (col != null) {
					testCaseID = GetCellValueAsString(col);
				}

			}

			verifyObj.testCaseID = testCaseID;
			Log.Debug("Excel/ReadVerificationSection : Row[" + index
					+ "] ...verifyObj.testCaseID = " + verifyObj.testCaseID);

			verifyObj.nameSpace = nameSpace;
			verifyObj.parentTestCaseID = testCaseID;
			verifyObj.stackTrace = testCaseID;
			Log.Debug("Excel/ReadVerificationSection : Row[" + index
					+ "] ...verifyObj.parentTestCaseID = "
					+ verifyObj.parentTestCaseID);

			verifyObj.userObj = userObj;

			if (StringUtils.isNotBlank(property)
					&& (property.contains("comment") || property
							.contains("rem"))) {
				verifyObj.isComment = true;
			}
			Log.Debug("Excel/ReadVerificationSection : Row[" + index
					+ "] test case ID is isComment = " + verifyObj.isComment);
			if (property != null
					&& property.toLowerCase().equalsIgnoreCase(NEGATIVE)) {
				Log.Debug("Excel/ReadActionSection: Verification(row) is Negative ");

				verifyObj.isNegative = true;

			}
			if (property != null
					&& (property.toLowerCase().equalsIgnoreCase("neg-verify") || property
							.toLowerCase().equalsIgnoreCase("negative-verify"))) {
				Log.Debug("Excel/ReadActionSection: Verification(only) is Negative ");

				verifyObj.isVerifyNegative = true;

			}
			// Get the Value of the Expected Result here.
			/*
			 * col =
			 * worksheet.getRow(index).getCell(getKey(labelIndex,"expected result"
			 * )); if(col!=null) { verifyObj.expectedResult =
			 * GetCellValueAsString(col);
			 * Log.Debug("Excel/ReadVerificationSection : Row["
			 * +index+"] ...Expected Result = "+ verifyObj.expectedResult); }
			 * else Log.Debug("Excel/ReadVerificationSection : Row["+index+
			 * "] ...Expected Result = String.Empty.");
			 */

			verifyObj.lineNumber = index;
			Log.Debug("Excel/ReadVerificationSection : Row[" + index
					+ "] ...lineNumber =  " + verifyObj.lineNumber);

			verifyObj.sheetName = sheetName;
			Log.Debug("Excel/ReadVerificationSection : Row[" + index
					+ "] ...sheetName = " + verifyObj.sheetName);

			/*
			 * if
			 * (_macroSheetHashTable.get(AppendNamespace(verifyObj.expectedResult
			 * , nameSpace)) != null) { verifyObj.expectedResult =
			 * (String)_macroSheetHashTable
			 * .get(AppendNamespace(verifyObj.expectedResult, nameSpace)) ;
			 * Log.Debug("Excel/ReadVerificationSection : Row["+index+
			 * "] ...Expected Result = "+verifyObj.expectedResult); }
			 */
			if (verifyObj.name != null
					&& verifyObj.name != " "
					&& verifyObj.name != "") {
				for (int i = 1; i <= TotalVerificationArgs; ++i) {
					Log.Debug("Excel/ReadVerificationSection : Finding value for verificationArgument "
							+ i
							+ " for Verification "
							+ verifyObj.name + " of Index " + index);

					String argumentValue = null;
					col = worksheet.getRow(index).getCell(
							(short) ((getKey(labelIndex, "verifyarg_" + i))));

					if (col == null) {
						Log.Debug("\n"
								+ String.format(
										"Excel/ReadVerificationSection : Row[%s] ...verificationActualArguments %s = NULL/EMPTY. So not inserting the value to the verifyObj.verificationActualArguments Arraylist. ",
										index, i));
					} //
					else {
						argumentValue = GetCellValueAsString(col);
						Log.Debug("\n"
								+ String.format(
										"Excel/ReadVerificationSection : Row[%s] ...verificationArgument %s = %s ",
										index, i, argumentValue));
					}
					if (StringUtils.isNotBlank(argumentValue)) {
						verifyObj.actualArguments
								.add(argumentValue);
						// system.out.print("\nverification argumentValue "
						// +argumentValue);

						argumentValue = FindInMacroAndEnvTable(argumentValue,
								nameSpace);
						Log.Debug("Excel/ReadVerificationSection : AFTER CALLING FindInMacroAndEnvTable -> Row["
								+ index
								+ "] ...verificationArgument "
								+ i
								+ " = " + argumentValue);

					} else {
						Log.Debug("\n"
								+ String.format(
										"Excel/ReadVerificationSection : Row[%s] ...verificationActualArguments %s = NULL/EMPTY. So not inserting the value to the verifyObj.verificationActualArguments Arraylist. ",
										index, i));
					}

					if (StringUtils.isNotBlank(argumentValue)) {
						verifyObj.arguments.add(argumentValue);
					} else {
						Log.Debug("Excel/ReadVerificationSection : Row["
								+ index
								+ "] ...verificationArgument "
								+ i
								+ " = NULL/EMPTY. So not inserting the value to the verifyObj.verificationArguments Arraylist. ");
					}

				}
			}

			// If Compilation is ON THEN Check in more detail...
			if (compileModeFlag) {
				CheckIFSheetIsFine(verifyObj.name,
						verifyObj.arguments, nameSpace,
						verifyObj.lineNumber, verifyObj.sheetName,
						verifyObj.testCaseID);
			}

			Log.Debug("Excel/ReadVerificationSection : End of the Function with Row Index = "
					+ index
					+ " and  Index of Verification column as : "
					+ verifyIndex);
		} catch (Exception e) {
			Log.Error("Excel/ReadVerificationSection : Exception occured while reading verification Section with Row Index = "
					+ index + " .Exception message is : " + e.getMessage());
			throw new Exception(
					"Excel/ReadVerificationSection : Exception occured while reading verification Section with Row Index = "
							+ index
							+ " .Exception message is : "
							+ e.getMessage());
		}
		return verifyObj;
	}// ReadVerificationSection

	// / This function checks if the Primitive or Molecule prototype is correct
	// or not.
	// / <param name="name">Name of the Action or Verification.</param>
	// / <param name="arguments">Arguments ..... of the Action or
	// Verification</param>
	private void CheckIFSheetIsFine(String name, ArrayList<String> arguments,
			String nameSpace, int lineNumber, String sheetName,
			String testCaseID) throws Exception {
		Log.Debug("Excel/CheckIFSheetIsFine : Start of the Function with name = "
				+ name + " and  arguments.Count as : " + arguments.size());
		if (name == null || name == "") {
			return;
		}
		try {
			if (name.startsWith("@")) {
				String tempName = Utility.TrimStartEnd(name, '@', 1);
				if (protoTypesHT.get(AppendNamespace(tempName, nameSpace)) != null) {
					Prototype tempPrototype = (Prototype) protoTypesHT
							.get(AppendNamespace(tempName, nameSpace));
					if (arguments.size() > tempPrototype.arguments.size()) {
						errorInTheSheet += String
								.format(" \n Number of Arguments Specified For Primitive : %s is : %d whereas the prototype has lesser number of Arguments specified.  Exception at Line Number :  %d  of Sheet : %s  while working on TestCase : %s",
										name, arguments.size(), lineNumber + 1,
										sheetName, testCaseID);
					}
					ComparePrototypeAndActualArguments(tempPrototype.arguments,
							arguments, name, lineNumber, sheetName, testCaseID);
				} else {
					errorInTheSheet += String
							.format(" \n No Prototype Present For Primitive : %s Exception at Line Number : %d of Sheet : %s while working on TestCase : %s",
									name, lineNumber + 1, sheetName, testCaseID);
				}

			} else if (name.startsWith("&")) {
				String tempName = Utility.TrimStartEnd(name, '&', 1);
				if (protoTypesHT.get(AppendNamespace(tempName, nameSpace)) != null) {
					Prototype tempPrototype = (Prototype) protoTypesHT
							.get(AppendNamespace(tempName, nameSpace));
					if (arguments.size() > tempPrototype.arguments.size()) {
						errorInTheSheet += String
								.format(" \n Number of Arguments Specified For Molecule : %s is : %d whereas the prototype has lesser number of Arguments specified.  Exception at Line Number : %d of Sheet : %s while working on TestCase : %s",
										name, arguments.size(), lineNumber + 1,
										sheetName, testCaseID);
					}

					ComparePrototypeAndActualArguments(tempPrototype.arguments,
							arguments, name, lineNumber, sheetName, testCaseID);
				} else {
					errorInTheSheet += String
							.format(" \n No Prototype Present For Molecule : %s Exception at Line Number : %d of Sheet : %s while working on TestCase : %s",
									name, lineNumber + 1, sheetName, testCaseID);

				}
			} else if (name.trim().compareTo("setcontextvar") == 0) {
				if (arguments.size() != 1) {
					errorInTheSheet += String
							.format(" \n Number of Arguments Specified For Built-In Primitive : %s is : %d whereas it should be 1.  Exception at Line Number : %d of Sheet : %s while working on TestCase : %s",
									name, arguments.size(), lineNumber + 1,
									sheetName, testCaseID);
				}

			} else if (name.trim().compareTo("unsetcontextvar") == 0) {
				if (arguments.size() != 1) {
					errorInTheSheet += String
							.format(" \n Number of Arguments Specified For Built-In Primitive : %s is : %d whereas it should be 1.  Exception at Line Number : %d of Sheet : 5s while working on TestCase : %s",
									name, arguments.size(), lineNumber + 1,
									sheetName, testCaseID);
				}
			} else if (name.trim().compareTo("appendtocontextvar") == 0) {
				if (arguments.size() >= 2) {
					String firstArgument = (String) arguments.get(0);
					int idx = firstArgument.indexOf('=');
					if (firstArgument.substring(0, idx).toLowerCase()
							.compareTo("contextvar") != 0) {
						errorInTheSheet += String
								.format(" \n Invalid first argument specified For Built-In Primitive : %s is : %s should be contextvar=%s Exception at Line Number : %d of Sheet : %s while working on TestCase : %s",
										name, firstArgument,
										firstArgument.substring(idx + 1),
										lineNumber + 1, sheetName, testCaseID);
					}
				} else {
					errorInTheSheet += String
							.format(" \n Number of Arguments Specified For Built-In Primitive : %s is : %d whereas it should be more than equal to 2.  Exception at Line Number : %d of Sheet : %s while working on TestCase : %s",
									name, arguments.size(), lineNumber + 1,
									sheetName, testCaseID);
				}
			} else if (name.trim().compareTo("getdbcolumnsbykey") == 0) {
				if (arguments.size() != 3) {
					errorInTheSheet += String
							.format(" \n Number of Arguments Specified For Built-In Primitive : %s is : %d whereas it should be equal to 3.  Exception at Line Number : %d of Sheet : %s while working on TestCase : %s",
									name, arguments.size(), lineNumber + 1,
									sheetName, testCaseID);
				} else {
					boolean isValidatedTableName = false;
					boolean isValidatedKeyName = false;
					boolean isValidatedKeyValue = false;

					for (int i = 0; i < arguments.size(); i++) {
						String arg = (String) arguments.get(i);
						int idx = arg.indexOf("=");
						String keyName = arg.substring(0, idx);

						if (keyName.toLowerCase().equals("tablename")) {
							isValidatedTableName = true;
						} else if (keyName.toLowerCase().equals("keyname")) {
							isValidatedKeyName = true;
						} else if (keyName.toLowerCase().equals("keyvalue")) {
							isValidatedKeyValue = true;
						}
					}
					if (!(isValidatedTableName && isValidatedKeyName && isValidatedKeyValue)) {
						errorInTheSheet += String
								.format(" \n Arguments Specified For Built-In Primitive : %s should be "
										+ "tablename, keyname and keyvalue. Exception at Line Number : %d of Sheet : %s while working on TestCase : %s",
										name, lineNumber + 1, sheetName,
										testCaseID);
					}
				}
			}
		} catch (Exception e) {
			Log.Error("Excel/CheckIFSheetIsFine : Exception occured, exception message is : "
					+ e.getMessage());
			e.printStackTrace();
			throw new Exception(
					"Excel/CheckIFSheetIsFine : Exception occured, exception message is : "
							+ e.getMessage());
		}
		Log.Debug("Excel/CheckIFSheetIsFine : End of the Function with name = "
				+ name + " and  arguments.Count as : " + arguments.size());
	}// /CheckSheetIsFine

	// / This function will compare arguments given in prototype sheet with
	// actual arguments of the test cases
	private void ComparePrototypeAndActualArguments(
			ArrayList<Arguments> prototypeArguments,
			ArrayList<String> actualArguments, String name, int lineNumber,
			String sheetName, String testCaseID) {
		for (int i = 0; i < prototypeArguments.size(); i++) {
			Arguments prototypeArg = (Arguments) prototypeArguments.get(i);
			String actualArgumentValue = StringUtils.EMPTY;
			if (i < actualArguments.size()) {
				actualArgumentValue = (String) actualArguments.get(i);
			}
			// Don't put in an error if this is a Default Argument - Okay
			if (!prototypeArg.isDefaultValue) {
				if (!actualArgumentValue.startsWith(prototypeArg.argumentName)) {
					errorInTheSheet += String
							.format(" \n Argument  : %s specified as  argument %d in the Prototype is not present for : %s Exception at Line Number : %d of Sheet : %s while working on TestCase : %s",
									prototypeArg.argumentName, (i + 1), name,
									(lineNumber + 1), sheetName, testCaseID);
				}
			}
		}
	}// ComparePrototypeAndActualArguments

	// / Function to find a variable in Macro sheet and Environment Variable
	// Sheet
	// / <param name="variableToFind">Name of the Variable to Search in Macro
	// and Env Variable Sheet</param>
	// / <returns>Returns the value of the Variable from Macro/Environment
	// Variable table.</returns>
	public String FindInMacroAndEnvTable(String variableToFind, String nameSpace)
			throws Exception {
		String tempValue = variableToFind;
		Log.Debug("Excel/FindInMacroAndEnvTable : Start of function with variableToFind = "
				+ variableToFind);
		// System.out.println("The variable to find "+variableToFind+" Macro Table coming "+_macroSheetHashTable+" namespace "+nameSpace);
		try {
			// Getting keys of macro hash table
			Set<String> _macroKeys = _macroSheetHashTable.keySet();
			Iterator<String> _keyIterate = _macroKeys.iterator();
			// System.out.println("FindMacro 1a->"+variableToFind);
			if (variableToFind.toLowerCase().contains("$input_arg")
					|| variableToFind.toLowerCase().contains("$$input_arg")) {
				Log.Debug("Excel/FindInMacroAndEnvTable : End of function with variableToFind = "
						+ variableToFind
						+ " and its value is -> "
						+ variableToFind);
				return variableToFind;
			}
			if (variableToFind.contains("=")) {
				// System.out.println("The Variable "+variableToFind);
				Log.Debug("Excel/FindInMacroAndEnvTable : The Variable to Find contains an = sign ");
				String[] splitVariableToFind = Excel
						.SplitOnFirstEquals(variableToFind); // .Split('=');

				Log.Debug("Excel/FindInMacroAndEnvTable : Length of  splitVariableToFind = "
						+ splitVariableToFind.length);
				if (splitVariableToFind.length <= 1) {
					Log.Debug("Excel/FindInMacroAndEnvTable : End of function with variableToFind = "
							+ variableToFind
							+ " and its value is -> "
							+ variableToFind);
					return variableToFind;
				}

				String tempValPrefix = splitVariableToFind[0].trim();
				// / First Check in the Macro Sheet
				if (StringUtils.isNotBlank(tempValPrefix)
						|| StringUtils.isNotEmpty(tempValPrefix)) {

					if (_macroSheetHashTable.get(AppendNamespace(tempValPrefix,
							nameSpace)) != null) {
						tempValPrefix = (String) _macroSheetHashTable
								.get(AppendNamespace(tempValPrefix, nameSpace));
						Log.Debug("\n"
								+ String.format(
										"Excel/FindInMacroAndEnvTable : After Macro Sheet parsing , tempValPrefix = %s ",
										tempValPrefix));
					}
				}
				tempValue = splitVariableToFind[1];
				Log.Debug("Excel/FindInMacroAndEnvTable : variableToFind = "
						+ tempValue);

				// / First Check in the Macro Sheet
				if (_macroSheetHashTable.get(AppendNamespace(tempValue,
						nameSpace)) != null) {
					tempValue = (String) _macroSheetHashTable
							.get(AppendNamespace(tempValue, nameSpace));
					Log.Debug("\n"
							+ String.format(
									"Excel/FindInMacroAndEnvTable : After Macro Sheet parsing , variableToFind = %s ",
									tempValue));

				}
				// Checking for vector macro
				else if (tempValue.startsWith("$$") && !tempValue.contains("%")) {
					// System.out.println("TempValue starts with -Prefix "+tempValPrefix+" Value: "+tempValue);
					boolean macroFound = false;
					tempValue = Utility.TrimStartEnd(tempValue, '$', 1);
					tempValue = AppendNamespace(tempValue, nameSpace);
					tempValue = "$" + tempValue;

					tempValue = _macroSheetHashTable.get(tempValue);
					 if(tempValue==null){
						 tempValue=splitVariableToFind[1].substring(1).toLowerCase();
					 }

				}/*
				 * else if(tempValue.startsWith("$") && !tempValue.contains("%")
				 * && tempValue.contains("#")){ if(tempValue.contains(".")){
				 * System.out.println("IF:tempValue:"+tempValue); tempValue=
				 * _macroSheetHashTable.get(tempValue);
				 * System.out.println("tempValue after getting"+tempValue);
				 * }else{ tempValue=AppendNamespace(tempValue, nameSpace);
				 * System.out.println("ELSE:tempValue:"+tempValue); tempValue=
				 * _macroSheetHashTable.get(tempValue);
				 * System.out.println("tempValue after getting"+tempValue); } }
				 */
				/*
				 * if(tempValue==null){ //
				 * if(splitVariableToFind[1].startsWith("$$")){
				 * tempValue=splitVariableToFind[1];//.substring(1); //
				 * System.out.println("Excel/tempValue="+tempValue); // } }
				 */
				tempValue = tempValPrefix + "=" + tempValue;
			} // / First Check in the Macro Sheet
			// System.out.println("The temp value "+tempValue+" namespace "+nameSpace);
			if (_macroSheetHashTable.get(AppendNamespace(tempValue, nameSpace)) != null) {
				// System.out.println("The value comming action "+tempValue);
				tempValue = (String) _macroSheetHashTable.get(AppendNamespace(
						tempValue, nameSpace));
				// System.out.println(tempValue+" THE macro hashtable "+_macroSheetHashTable);
				Log.Debug("\n"
						+ String.format(
								"Excel/FindInMacroAndEnvTable : After Macro Sheet parsing , variableToFind = %s ",
								tempValue));
			}
			// checking for any vector macro
			else if (variableToFind.startsWith("$$")
					&& !variableToFind.endsWith("%")) {
				boolean macroFoundFlag = false;
				tempValue = Utility.TrimStartEnd(tempValue, '$', 1);
				// System.out.println("FindMacro 2a->"+tempValue+" the namespace "+nameSpace);
				tempValue = AppendNamespace(tempValue, nameSpace);
				tempValue = "$" + tempValue;
				// System.out.println("2b The ValueToFind -> "+variableToFind+" The Value is -> "+tempValue);
				while (_keyIterate.hasNext()) {
					String _keyToSearch = _keyIterate.next();
					// System.out.println("Iteterator -> "+_keyToSearch+" TempValue "+tempValue);
					if (tempValue.equalsIgnoreCase(_keyToSearch)) {
						macroFoundFlag = true;
						break;
					} else {
						macroFoundFlag = false;
						// continue;
					}

				}
				if (!macroFoundFlag) {
					Log.Error(String.format(
							"[Warning]The Macro: %s is not defined ",
							variableToFind));
				}

				tempValue = _macroSheetHashTable.get(tempValue);
				/*
				 * if(tempValue==null){ //if(variableToFind.startsWith("$$")){
				 * tempValue=variableToFind;//.substring(1);
				 * 
				 * // System.out.println("Excel/tempValue="+tempValue); //} }
				 */

			}/*
			 * else if(tempValue.startsWith("$") && !tempValue.contains("%") &&
			 * tempValue.contains("#")){ if(tempValue.contains(".")){
			 * System.out.println("IF:tempValue:"+tempValue); tempValue=
			 * _macroSheetHashTable.get(tempValue);
			 * System.out.println("tempValue after getting"+tempValue); }else{
			 * tempValue=AppendNamespace(tempValue, nameSpace);
			 * System.out.println("ELSE:tempValue:"+tempValue); tempValue=
			 * _macroSheetHashTable.get(tempValue);
			 * System.out.println("tempValue after getting"+tempValue); } }
			 */
			Log.Debug("Excel/FindInMacroAndEnvTable : End of function with variableToFind = "
					+ variableToFind + " and its value is -> " + tempValue);
		} catch (Exception e) {
			Log.Error("Excel/FindInMacroAndEnvTable : Exception occured, exception mesasge is  : "
					+ e.getMessage());
			throw new Exception(
					"\nExcel/FindInMacroAndEnvTable : Exception occured, exception mesasge is  : "
							+ e.getMessage());
		} finally {
			/*if (tempValue == null
					|| ((tempValue.contains("$$") || tempValue.contains("$"))
							&& tempValue.contains("#") && !tempValue
							.endsWith("%"))) {
				System.out.println("calling check indexed macro method"
						+ tempValue);
				tempValue = checkForIndexedMacro(variableToFind, nameSpace);
			}*/
			if(tempValue==null){
				tempValue=variableToFind;
			}

		}
		return tempValue;
	}// FindInMacroAndEnvTable

	public String checkForIndexedMacro(String value, String nameSpace) {
		String Ovalue = value;
		String str[] = null;
		if (value.contains("=")) {
			str = value.split("=", 2);
			value = str[1];
		}
		if ((value.startsWith("$$") || value.startsWith("$"))
				&& value.contains("#") && !value.endsWith("%")) {
			if (!value.contains(".")) {
				try {
					value = AppendNamespace(value, nameSpace);
					// System.out.println("value after appending NS"+value);
				} catch (Exception e) {
					// System.out.println("Exception in appending name space");
				}
			}
			value = _macroSheetHashTable.get(value.toLowerCase());
		}

		if (value == null) {
			value = Ovalue;
		} else if (Ovalue.contains("=")) {
			value = str[0] + "=" + value;
		}
		// System.out.println("Ovalue :"+Ovalue+"\ttempvalue :"+value);
		return value;
	}

	public static String[] SplitOnFirstEquals(String nameOfVariable) {
		if (StringUtils.isBlank(nameOfVariable)) {
			return new String[0];
		}

		String[] stringToReturn = nameOfVariable.split("=");
		if (stringToReturn.length <= 2) {
			return stringToReturn;
		} else {
			String[] tempStringToReturn = new String[2];
			tempStringToReturn[0] = stringToReturn[0];
			String[] tempStringToJoin = new String[stringToReturn.length - 1];// Separately
			
			for (int i = 1; i < stringToReturn.length; i++) {
				tempStringToJoin[i - 1] = stringToReturn[i];// getting the
			} // computed string
				// for joining
			tempStringToReturn[1] = Utility.join("=", tempStringToJoin, 0,
					tempStringToJoin.length);// string is joined with the
			// delimeters needed also the
			// join method is changed

			return tempStringToReturn;
		}
	}
	private TestCase ReadTestCase(HSSFSheet worksheet,
			Hashtable<Short, String> labelIndex, int index, int testCaseIndex,
			String description, String nameSpace,boolean isTestCase) throws Exception {
		Log.Debug("Excel/ReadTestCase : Start of the Function with Row Index = "
				+ index
				+ " and  Index of testCaseIndex column as : "
				+ testCaseIndex);
		HSSFCell col;
		// Same description comming for other testcases.....
		// System.out.println("Testcaseid "+index
		// +" TestcaseDescription->"+description+" Namespace "+nameSpace);
		TestCase testCase ;
		if(isTestCase){
			testCase= new TestCase();
		}
		else{
			testCase= new Molecule();
		}

		try {
			col = worksheet.getRow(index).getCell((short) (testCaseIndex));

			if (col == null) {
				throw new Exception(
						String.format(
								"Excel/ReadTestCase :Not able to read the cell at Row/Col(%d/%d)",
								index, testCaseIndex));
			}

			testCase.testCaseID = GetCellValueAsString(col);
			Log.Debug("\n"
					+ String.format(
							"Excel/ReadTestCase : Row[%d] test case ID = %s ",
							index, testCase.testCaseID));
			// System.out.print("\nTest Case ID "+ testCase.testCaseID);
			if (StringUtils.isNotBlank(testCase.testCaseID)) // system.out.print("\nTest Case ID "+
																// testCase.testCaseID);
			{
				testCase.parentTestCaseID = testCase.testCaseID;
			}
			testCase.stackTrace = testCase.testCaseID;
			testCase.nameSpace = nameSpace;

			Log.Debug("\n"
					+ String.format(
							"Excel/ReadTestCase : Row[%d] parentTestCaseID = %s ",
							index, testCase.parentTestCaseID));

			testCase.testCaseDescription = description; // (string)((Range)worksheet.Cells[index,
			// (int)labelIndex["description"]]).Text.ToString();

			Log.Debug("\n"
					+ String.format(
							"Excel/ReadTestCase : Row[%d] test case ID = %s ",
							index, testCase.testCaseDescription));

			String isAutomated = null;
			col = worksheet.getRow(index).getCell(
					(getKey(labelIndex, "property")));

			if (col == null) {
				isAutomated = "auto"; // assume AUTO is default value
			} else {
				isAutomated = GetCellValueAsString(col).toLowerCase();
			}

			if (isAutomated != null && isAutomated.contains("auto")) {
				testCase.automated = true;
			}

			if (isAutomated != null && isAutomated.contains("manual")) {
				testCase.automated = false;
			}

			Log.Debug("\n"
					+ String.format(
							"Excel/ReadTestCase : Row[%d] test case ID is Automated = %s ",
							index, testCase.automated));

			if (isAutomated != null && isAutomated.contains("gce")) {
				testCase.concurrentExecutionOnExpansion = true;
			}

			Log.Debug("\n"
					+ String.format(
							"Excel/ReadTestCase : Row[%d] test case ID is concurrentExecutionOnExpansion = %s ",
							index, testCase.concurrentExecutionOnExpansion));

			col = worksheet.getRow(index).getCell(getKey(labelIndex, "user"));
			if (col == null) {
				testCase.user = "";
			} else {
				testCase.user = GetCellValueAsString(col);
			}

			Log.Debug("\n"
					+ String.format("Excel/ReadTestCase : Row[%d] user = %s ",
							index, testCase.user));

			if (_macroSheetHashTable.get(AppendNamespace(testCase.user,
					nameSpace)) != null) {
				testCase.user = (String) _macroSheetHashTable
						.get(AppendNamespace(testCase.user, nameSpace));
				Log.Debug("\n"
						+ String.format(
								"Excel/ReadTestCase : Row[%d] Actual Value of  user from Macro sheet is = %s ",
								index, testCase.user));
			}

			/*
			 * if (_userSheetHashTable.get(testCase.user) != null) {
			 * Log.Debug("\n"+String.format(
			 * "Excel/ReadTestCase : Row[%d] User Credentials and Information for User %s exists in the User Sheet"
			 * , index, testCase.user)); testCase.userObj =
			 * (UserData)_userSheetHashTable.get(testCase.user) ; }
			 */Log.Debug("Excel/ReadTestCase : End of the Function with Row Index = "
					+ index
					+ " and  Index of testCaseIndex column as : "
					+ testCaseIndex);
		} catch (Exception e) {
			Log.Error("Excel/ReadTestCase : Exception occured while reading test caseswith Row Index = "
					+ index + " .Exception message is : " + e.getMessage());
			throw new Exception(
					"Excel/ReadTestCase : Exception occured while reading test caseswith Row Index = "
							+ index
							+ " .Exception message is : "
							+ e.getMessage());
		}
		// System.out.println("testcase returning "+testCase.testCaseID);
		return testCase;
	}// ReadTestCase

	// / Function reads the Action section of the TestCase sheet.
	// / <param name="worksheet">Object of the Worksheet</param>
	// / <param name="index">Row of the TestCase sheet that one is
	// reading</param>
	// / <param name="actionIndex">Index of the Verify Column</param>
	private Action ReadActionSection(HSSFSheet worksheet,
			Hashtable<Short, String> labelIndex,
			Hashtable<Short, String> _mapHashTable, int index, int actionIndex,
			String sheetName, TestCase testCase, int testcaseIndex,
			String nameSpace) throws Exception, MoleculeDefinitionException {
		Log.Debug("Excel/ReadActionSection : Start of the Function with Row Index = "
				+ index
				+ " and  Index of Action column as : "
				+ actionIndex
				+ ". The name of the Sheet is - > " + sheetName);

		Action actionObj = new Action();
		try {
			String property = null;

			HSSFCell col = worksheet.getRow(index).getCell(
					(getKey(labelIndex, "property")));

			if (col != null) {
				property = GetCellValueAsString(col);
			}

			if (property != null
					&& (property.toLowerCase().contains("comment") || property
							.toLowerCase().contains("rem"))) {
				// /Action is a Comment.
				Log.Debug("Excel/ReadActionSection : Action is a Comment");
				Log.Debug("Excel/ReadActionSection : End of the Function.");
				actionObj.isComment = true;
				HSSFCell actionDefine = worksheet.getRow(index).getCell(
						(short) (actionIndex));
				// System.out.println("The name "+GetCellValueAsString(actionDefine));
				if (GetCellValueAsString(actionDefine).toLowerCase().trim()
						.contains("#define_arg")) {
					Log.Error("Excel/ReadActionSection : Molecule definition cannot be set to 'comment' or 'rem'");
					throw new MoleculeDefinitionException(
							"Excel/ReadActionSection : Molecule definition cannot be set to 'comment' or 'rem'");
				}

				return null;
			}
			
			if(property==null){
				actionObj.property=" ";
			}else{
				actionObj.property=property;
			}
			if (property != null
					&& property.toLowerCase().equalsIgnoreCase(NEGATIVE)) {
				Log.Debug("Excel/ReadActionSection: Action(row) is Negative ");
				actionObj.actionProperty = property.toLowerCase();
				actionObj.isNegative = true;

			}
			else if (property != null
					&& property.toLowerCase().equalsIgnoreCase("Ignore")) {
				Log.Debug("Excel/ReadActionSection: Action(row) is Ignore ");
				actionObj.actionProperty = property.toLowerCase();
				actionObj.isIgnore = true;

			}


			if (property != null
					&& (property.toLowerCase().contains("neg-action") || property
							.toLowerCase().equalsIgnoreCase("negative-action"))) {
				Log.Debug("Excel/ReadActionSection: Action(only) is Negative ");

				actionObj.isActionNegative = true;

			}
			col = worksheet.getRow(index).getCell(
					(getKey(labelIndex, "description")));
			if (col != null) {
				actionObj.actionDescription = GetCellValueAsString(col);
				Log.Debug("Excel/ReadActionSection : Action Description "
						+ actionObj.actionDescription);
			}
			col = worksheet.getRow(index).getCell((short) (actionIndex));
			if (col == null) {
				Log.Debug(String
						.format("Excel/ReadActionSection :Not able to read the value for action name from cell  at Row/Col(%d/%d)",
								index, actionIndex));

				return null;
			}

			actionObj.name = GetCellValueAsString(col);

			if (actionObj.name != null && actionObj.name != " "
					&& actionObj.name != "") // system.out.print("\nAction Name "
													// + actionObj.actionName);
			{
				Log.Debug("\n"
						+ String.format(
								"Excel/ReadActionSection : Row[%d] ...actionName = %s ",
								index, actionObj.name));
			}

			String testCaseID = null;
			UserData userObj = new UserData();

			if (testCase != null) {
				testCaseID = testCase.testCaseID;
				userObj = testCase.userObj;
			} else {
				col = worksheet.getRow(index).getCell((short) (testcaseIndex));

				if (col == null) {
					throw new Exception(
							String.format(
									"Excel/ReadActionSection :Not able to read the value for test case ID from cell  at Row/Col(%d/%d)",
									index, testcaseIndex));
				}

				testCaseID = GetCellValueAsString(col);
			}
			actionObj.testCaseID = testCaseID;
			Log.Debug("\n"
					+ String.format(
							"Excel/ReadActionSection : Row[%d] ...actionObj.testCaseID  = %s ",
							index, actionObj.testCaseID));

			actionObj.nameSpace = nameSpace;
			actionObj.parentTestCaseID = testCaseID;
			actionObj.stackTrace = testCaseID;
			Log.Debug("\n"
					+ String.format(
							"Excel/ReadActionSection : Row[%d] ...actionObj.parentTestCaseID  = %s ",
							index, actionObj.parentTestCaseID));

			actionObj.userObj = userObj;
			if (property != null
					&& (property.toLowerCase().contains("comment") || property
							.toLowerCase().contains("rem"))) {
				actionObj.isComment = true;
			}
			if (property != null
					&& property.toLowerCase().equalsIgnoreCase(NEGATIVE)) {
				Log.Debug("Excel/ReadActionSection: Action(row) is Negative ");

				actionObj.isNegative = true;

			}
			if (property != null
					&& (property.toLowerCase().equalsIgnoreCase("neg-action") || property
							.toLowerCase().equalsIgnoreCase("negative-action"))) {
				Log.Debug("Excel/ReadActionSection: Action(only) is Negative ");

				actionObj.isActionNegative = true;

			}else if (property != null
					&& property.toLowerCase().equalsIgnoreCase("Ignore")) {
				Log.Debug("Excel/ReadActionSection: Action(row) is Ignore ");
				actionObj.actionProperty = property.toLowerCase();
				actionObj.isIgnore = true;

			}

			Log.Debug("\n"
					+ String.format(
							"Excel/ReadActionSection : Row[%d] test case ID is isComment = %s ",
							index, actionObj.isComment));

			col = worksheet.getRow(index).getCell(getKey(labelIndex, "step"));
			if (col == null) {
				Log.Debug(String
						.format("Excel/ReadActionSection :Not able to read the value for step from cell  at Row/Col(%d/%d)",
								index, getKey(labelIndex, "step")));
				actionObj.step = StringUtils.EMPTY;
			} else if (col.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
				actionObj.step = Integer.toString((int) col
						.getNumericCellValue());
				
			} else {
				actionObj.step = GetCellValueAsString(col);
			}
			Log.Debug("\n"
					+ String.format(
							"Excel/ReadActionSection : Row[%d] ...step = %s ",
							index, actionObj.step));

			actionObj.lineNumber = index;
			Log.Debug("\n"
					+ String.format(
							"Excel/ReadActionSection : Row[%d] ...lineNumber = %s ",
							index, actionObj.lineNumber));

			actionObj.sheetName = sheetName;
			Log.Debug("\n"
					+ String.format(
							"Excel/ReadActionSection : Row[%d] ...sheetName = %s ",
							index, actionObj.sheetName));

			if (StringUtils.isNotBlank(actionObj.name)) {
				for (int i = 1; i <= TotalActionArgs; i++) {
					Log.Debug("\n"
							+ String.format(
									"Excel/ReadActionSection : Finding value for actionArgument %d for Action %s of Index %s ",
									i, actionObj.name, index));

					String argumentValue = null;
					col = worksheet.getRow(index).getCell(
							getKey(labelIndex, "actionarg_" + i));

					if (col != null) {
						argumentValue = GetCellValueAsString(col);

						Log.Debug("\n"
								+ String.format(
										"Excel/ReadActionSection : Row[%d] ...actionArgument %d = %s ",
										index, i, argumentValue));
					}
					if (StringUtils.isNotBlank(argumentValue)) {
						actionObj.actualArguments.add(argumentValue);
						// system.out.print("\nAction ArgValue " +
						// argumentValue);
						 //System.out.println("THE Argument sent "+argumentValue+" namt "+nameSpace);

						argumentValue = FindInMacroAndEnvTable(argumentValue,
								nameSpace);
						

						Log.Debug("Excel/ReadActionSection : AFTER CALLING FindInMacroAndEnvTable -> Row["
								+ index
								+ "] ...actionArgument "
								+ argumentValue);
					} else {
						Log.Debug("Excel/ReadActionSection : Row["
								+ index
								+ "] ...actionArgument "
								+ i
								+ " = NULL/EMPTY. So not inserting the value to the actionObj.actionActualArguments Arraylist. ");
					}

					if (StringUtils.isNotBlank(argumentValue)) {
						actionObj.arguments.add(argumentValue);
					} else {
						Log.Debug("Excel/ReadActionSection : Row["
								+ index
								+ "] ...actionArgument "
								+ i
								+ " = NULL/EMPTY. So not inserting the value to the actionObj.actionArguments Arraylist. ");
					}
				}
				 //System.out.println("The action argument "+actionObj.actionArguments);
			}

			// / If Compilation is ON THEN Check in more detail...
			if (compileModeFlag) {
				CheckIFSheetIsFine(actionObj.name,
						actionObj.arguments, nameSpace,
						actionObj.lineNumber, actionObj.sheetName,
						actionObj.testCaseID);
			}
			Log.Debug("Excel/ReadActionSection : End of the Function with Row Index = "
					+ index
					+ " and  Index of Action column as : "
					+ actionIndex);

			// System.out.println("sheetName "+sheetName+"\ntestcase "+testCaseID+"\n Args "+actionObj.actionArguments);
			// Checking fot Molecule sheet and create the definition
			if (sheetName.equalsIgnoreCase(AbstractSheetName)
					|| actionObj.sheetName.equalsIgnoreCase(AbstractSheetName)) {
				// System.out.println("Testcase id=" + testCase.testCaseID +
				// "\n" + actionObj.actionArguments);
				// System.out.println("the Action Name=="+actionObj.actionName+" action ID--"+actionObj.testCaseID+" negative "+actionObj.isNegative);
				if (actionObj.name.startsWith("#define")) {
					for (String var_key : actionObj.arguments) {
						// System.out.println("mol args "+var_key.toLowerCase());
						if (var_key.startsWith("#") || var_key.startsWith("$")) {
							throw new MoleculeDefinitionException(
									"Variable name incosistent. Can't be started with # or $. Molecule: "
											+ actionObj.testCaseID);
						}
						actionObj._actionmoleculeArgDefn.add(var_key
								.toLowerCase());

					}

					Log.Debug("Excel/ReadActionSection : Molecule Definition reading done:: "
							+ actionObj._actionmoleculeArgDefn);

				}

			}

		} catch (Exception e) {
			Log.Error("Excel/ReadActionSection : Exception occured while reading Action Section with Row Index = "
					+ index + " .Exception message is : " + e.getMessage());
			throw new Exception(
					"Excel/ReadActionSection : Exception occured while reading Action Sectionwith Row Index = "
							+ index
							+ " .Exception message is : "
							+ e.getMessage());
		}

		return actionObj;
	}// /ReadActionSection

	// / Function to find the total number of Action and Verification Arguments.
	// / <param name="labelIndex">Hashtable which store the labels that is part
	// of the TestCase.</param>
	private void FindTotalActionVerificationArgs(
			Hashtable<Short, String> labelIndex) {
		Log.Debug("Excel/FindTotalActionVerificationArgs : Start of Function.");
		{

			// / Re-initializing these values to null.
			// / so that the same function can be used for both TestCase and
			// Abstract Test Case Sheet
			TotalVerificationArgs = 0;
			TotalActionArgs = 0;

			// / Loop over the list, writing out the value
			Set<Short> it = labelIndex.keySet();

			List<String> actionVer = new ArrayList<String>();
			for (short key : it) {
				String value = labelIndex.get(key).toString();
				Log.Debug("Excel/FindTotalActionVerificationArgs : Value of the Key is -> "
						+ value);

				if (value.startsWith("verifyarg_")) {
					if (actionVer.contains(value.toLowerCase())) {
						Log.Error("Header "
								+ value
								+ " is specified more than once. Correct the CHUR Excel, and re-run the test.");
						Log.Error("Exitting...");
						System.exit(0);
					}

					actionVer.add(value.toLowerCase());

					TotalVerificationArgs++;
					Log.Debug("Excel/FindTotalActionVerificationArgs : Key "
							+ value
							+ " is a Verification argument and the number of Verification arg so far is "
							+ TotalVerificationArgs);

				}

				if (value.startsWith("actionarg_")) {
					if (actionVer.contains(value.toLowerCase())) {
						Log.Error("Header "
								+ value
								+ " is specified more than once. Correct the CHUR Excel, and re-run the test.");
						Log.Error("Exiting...");
						System.exit(0);
					}

					actionVer.add(value.toLowerCase());
					TotalActionArgs++;
					Log.Debug("Excel/FindTotalActionVerificationArgs : Key "
							+ value
							+ " is a Action argument and the number of Action arg so far is "
							+ TotalActionArgs);
				}
			}
			Log.Debug("Excel/FindTotalActionVerificationArgs : Total number of Action Arguments are "
					+ TotalActionArgs);
			Log.Debug("Excel/FindTotalActionVerificationArgs : Total number of Verification Arguments are "
					+ TotalVerificationArgs);
		}
		Log.Debug("Excel/FindTotalActionVerificationArgs : End of Function.");
	}

	// / Function specifically used for the TestCase Sheet. It is used to
	// identify the Verification/Action arguments position
	// / inside the Excel sheet..i.e. the Column position where the Arguments
	// belong
	// / <param name="worksheet">Object of the Wokrsheet</param>
	// / <param name="labelIndex"> This hashtable contain the label of the
	// different Columns present inside the TestCase sheet</param>
	// / <param name="mapHashTable">Hastbale which will finally contain the
	// mapping of the Verification/Action argument against the
	// / Excel Column number
	// / </param>
	// / <returns>The position where the Header is getting finished and the test
	// case is getting started</returns>
	// / NOTE: This is just a Dummy Function now and can be considered as
	// OBSOLETE. (NO MORE USED FOR ANY USEFUL PURPOSE).
	private int GetActionVerificationMap(HSSFSheet worksheet,
			Hashtable<Short, String> labelIndex,
			Hashtable<Short, String> mapHashTable) {
		Log.Debug("Excel/GetActionVerificationMap: Start of the Function to find the Mapping of the Verification/Action arguments against the Excel Sheet columns");
		int testCaseIndex = 0;
		int index = 2;
		try {
			testCaseIndex = (int) (getKey(labelIndex, "testcase id"));

			if (testCaseIndex == -1) {
				testCaseIndex = (int) (getKey(labelIndex, "molecule id"));
			}
			Log.Debug("Excel/GetActionVerificationMap: testCaseIndex = "
					+ testCaseIndex);

			int actionIndex = (int) (getKey(labelIndex, "action"));
			Log.Debug("Excel/GetActionVerificationMap: actionIndex = "
					+ actionIndex);

			int verificationIndex = (int) (getKey(labelIndex, "verify"));
			Log.Debug("Excel/GetActionVerificationMap: verificationIndex = "
					+ verificationIndex);

			HSSFCell col;

			for (; index <= worksheet.getLastRowNum(); index++) {
				Log.Debug("Excel/GetActionVerificationMap: Reading Index ["
						+ index + "] of the TestCase sheet");

				col = worksheet.getRow(index).getCell((short) testCaseIndex);
				if (col == null) {
					throw new Exception(
							String.format(
									"Excel/GetActionVerificationMap: Not able to read the value for testcase column in cell : Row/Col(%d/%d)",
									index, testCaseIndex));
				}

				String valueInTestCaseColumn = GetCellValueAsString(col);

				col = worksheet.getRow(index).getCell((short) actionIndex);
				if (col == null) {
					new Exception(
							String.format(
									"Excel/GetActionVerificationMap: Not able to read the value for action column in cell : Row/Col(%d/%d",
									index, actionIndex));
				}

				String valueInActionColumn = GetCellValueAsString(col);

				col = worksheet.getRow(index)
						.getCell((short) verificationIndex);
				String valueInVerificationColumn = "";
				if (col != null) {
					valueInVerificationColumn = GetCellValueAsString(col);
				}
				// throw new
				// Exception(String.format("Excel/GetActionVerificationMap: Not able to read the value for verification column in cell : Row/Col(%d/%d",index,verificationIndex));

				// / Break the loop if the test case or the comments starts.
				if (StringUtils.isNotBlank(valueInTestCaseColumn)) {
					Log.Debug("Excel/GetActionVerificationMap: Index ["
							+ index
							+ "] of the TestCase sheet. The header finished here and TestCase starts. ");
					break;
				}

				Log.Debug("Excel/GetActionVerificationMap: Index [" + index
						+ "] Checking if an Action Exists in this Row");
				if (StringUtils.isNotBlank(valueInActionColumn)) {
					String actionName = GetCellValueAsString(
							worksheet.getRow(index)
									.getCell((short) actionIndex))
							.toLowerCase();
					Log.Debug("Excel/GetActionVerificationMap: Index [" + index
							+ "] Action Name is " + actionName);

					// / Do nothing now..This is just a Dummy function.
				}
				Log.Debug("Excel/GetActionVerificationMap: Index [" + index
						+ "] Checking if an Verification Exists in this Row");
				if (StringUtils.isNotBlank(valueInVerificationColumn)) {
					String verificationName = GetCellValueAsString(
							worksheet.getRow(index).getCell(
									(short) verificationIndex)).toLowerCase();
					Log.Debug("Excel/GetActionVerificationMap: Index [" + index
							+ "] verification Name is {1} " + verificationName);
					// Do nothing now..This is just a Dummy function.
				}
			}
			Log.Debug("Excel/GetActionVerificationMap: End of the Function to find the Mapping of the Verification/Action arguments against the Excel Sheet columns. Index values Returned is : "
					+ index);
		} catch (Exception e) {
			Log.Error("Excel/GetActionVerificationMap: Exception occured, exception message is : "
					+ e.getMessage());
		}
		return index;
	}

	// / Function to read the TestCase sheet
	// / <param name="workBook">Object of WorkBook</param>
	public void ReadTestCaseSheet(HSSFWorkbook workBook, String nameSpace)
			throws Exception, MoleculeDefinitionException {

		String testCaseSheetName = TestCaseSheetName;
		Log.Debug("Excel/ReadTestCaseSheet : Start of the Function with TestCaseSheetName as "
				+ testCaseSheetName);
		HSSFSheet workSheet = null;

		Log.Debug("Excel/ReadTestCaseSheet : The worksheet object created");
		Log.Debug("Excel/ReadTestCaseSheet : Reading the WorkSheet "
				+ testCaseSheetName);

		try {
			Log.Debug("Excel/ReadTestCaseSheet : Getting all the Sheets from the WorkBook");
			workSheet = workBook.getSheet(testCaseSheetName);

			if (workSheet == null) {
				throw new Exception(
						"Excel/ReadTestCaseSheet : Could not find the TestCase sheet "
								+ testCaseSheetName);
			}

			Log.Debug("Excel/ReadTestCaseSheet : Worksheet "
					+ testCaseSheetName + " exists and read successfully");
		} catch (Exception e) {
			Log.Error("Excel/ReadTestCaseSheet : Could not find the TestCase sheet "
					+ testCaseSheetName
					+ " Exception message is : "
					+ e.getMessage());

			throw new Exception(
					"Excel/ReadTestCaseSheet : Could not find the TestCase sheet "
							+ testCaseSheetName + " Exception Message : "
							+ e.getMessage() + "\n");
		}

		try {
			Log.Debug("Excel/ReadTestCaseSheet : Getting the values from the TestCase Sheet. Calling GetTestCaseSheetValues ......");
			try{
			GetTestCaseSheetValues(workSheet, nameSpace);
			}catch(Exception e){
				e.printStackTrace();
			}
			Log.Debug("Excel/ReadTestCaseSheet : The values from the TestCase sheet is read successfully.");
		} catch (Exception ex) {
			String error = "Excel/ReadTestCaseSheet : Error occured while getting the values from TestCase Sheet "
					+ testCaseSheetName
					+ ". Exception Message is "
					+ ex.getMessage() + "\n\n";
			Log.Error(error);
			throw new Exception(error);
		}
		Log.Debug("Excel/ReadTestCaseSheet : End of the Function with TestCase SheetName as "
				+ testCaseSheetName);
	}// ReadTestCaseSheet

	// / Function to read the Data from the TestCase sheet. This function will
	// read the TestCase sheet and will
	// / populate the values in the DataStructures appropriately.
	// / <param name="worksheet"></param>
	private void GetTestCaseSheetValues(HSSFSheet worksheet, String nameSpace)
			throws Exception, MoleculeDefinitionException {
		Hashtable<Short, String> _mapHashTable = new Hashtable<Short, String>();
		int index = 1;
		try {

			Log.Debug("Excel/GetTestCaseSheetValues : Start of Function to read Test Case sheet");

			// / Read the Labels from the TestCase Sheet and find their index in
			// the TestSheet
			Hashtable<Short, String> labelIndex = new Hashtable<Short, String>();

			Log.Debug("Excel/GetTestCaseSheetValues : Calling getLabelIndex for Test Case sheet");
			GetLabelIndex(worksheet, labelIndex);

			// Find the number of Action and Verification Arguments.
			Log.Debug("Excel/GetTestCaseSheetValues : Calling FindTotalActionVerificationArgs for Test Case sheet");
			FindTotalActionVerificationArgs(labelIndex);

			Log.Debug("Excel/GetTestCaseSheetValues : Successfully Called FindTotalActionVerificationArgs for Test Case sheet");
			Log.Debug("Excel/GetTestCaseSheetValues : Total number of Action Arguments are "
					+ TotalActionArgs);
			Log.Debug("Excel/GetTestCaseSheetValues : Total number of Verification Arguments are "
					+ TotalVerificationArgs);

			Log.Debug("Excel/GetTestCaseSheetValues : Calling GetActionVerificationMap to read the Header from the Excel sheet.");
			GetActionVerificationMap(worksheet, labelIndex,
					_mapHashTable);

			Log.Debug("Excel/GetTestCaseSheetValues : Read the Header from the TestCase Excel sheet. Index returned is -> "
					+ index);

			// /Initially we assume that the test cases their actions and their
			// verification are not read.
			TestCase testCase = null;
			Action actionObj = null;
			Verification verifyObj = new Verification();

			int testCaseIndex = (int) (getKey(labelIndex, "testcase id"));
			Log.Debug("Excel/GetTestCaseSheetValues : testCaseIndex = "
					+ testCaseIndex);

			int actionIndex = (int) (getKey(labelIndex, "action"));
			Log.Debug("Excel/GetTestCaseSheetValues : actionIndex = "
					+ actionIndex);

			int verifyIndex = (int) (getKey(labelIndex, "verify"));
			Log.Debug("Excel/GetTestCaseSheetValues : verifyIndex = "
					+ verifyIndex);

			Log.Debug("Excel/GetTestCaseSheetValues : Last TestCase Row index to read is -> "
					+ worksheet.getLastRowNum());

			Log.Debug("Excel/GetTestCaseSheetValues : Started reading the TestCase Excel sheet at Index -> "
					+ index);

			HSSFCell col=null;
			String description = null;
			for (; index <= worksheet.getLastRowNum(); index++) {
				Log.Debug("Excel/GetTestCaseSheetValues : Reading the TestCase Excel sheet at Index -> "
						+ index);
				String valueInTestCaseColumn = null;
				// System.out.println("The GetTestCAse :: "+worksheet.getRow(index).getCell((short)testCaseIndex));
				try{
					col = worksheet.getRow(index).getCell((short) testCaseIndex);
				}catch(Exception e){
					Log.Debug("Warn: Exception in "+nameSpace+" due to empty line or cell in line number "+(index+1)+". "+e.getMessage());
					continue;
				}

				if (col != null) {
					valueInTestCaseColumn = GetCellValueAsString(col);
				}
				// / If this is a comment then Ignore

				if (valueInTestCaseColumn != null
						&& valueInTestCaseColumn.toLowerCase()
								.equals("comment")) {
					description = GetCellValueAsString(worksheet.getRow(index)
							.getCell((short) (testCaseIndex + 1)));
					// System.out.println("\nTest case Description::GETTESTCASE:: "+description);
					if (StringUtils.isNotBlank(description)
							&& index == worksheet.getLastRowNum()) {
						// System.out.println("The comment is provided in last row. ");
						throw new Exception("Comment cannot be provided in "
								+ nameSpace + ".xls Sheet: testcase Row: "
								+ worksheet.getLastRowNum());
					}
					Log.Debug("Excel/GetTestCaseSheetValues : This is a Comment Section/Row..Ignoring this as this is of no use to Automation. ");
					continue;
				}
				// System.out.println("The Descriptions 2d"+description);
				// / First checking for the Verification Methods
				String valueInVerificationColumn = null;

				col = worksheet.getRow(index).getCell((short) verifyIndex);
				if (col != null) {
					valueInVerificationColumn = GetCellValueAsString(col);
				}

				if (StringUtils.isNotBlank(valueInVerificationColumn)) {
					if (verifyObj != null && actionObj != null
							&& testCase != null) {
						actionObj.verification.add(verifyObj);
					}

					// / Read the Verification Section of the Row
					Log.Debug("Excel/GetTestCaseSheetValues : Calling ReadVerificationSection with row index as : "
							+ index);

					if (StringUtils.isNotBlank(valueInTestCaseColumn)) {
						verifyObj = ReadVerificationSection(worksheet,
								labelIndex, _mapHashTable, index, verifyIndex,
								TestCaseSheetName, null, testCaseIndex,
								nameSpace);
					} else {
						verifyObj = ReadVerificationSection(worksheet,
								labelIndex, _mapHashTable, index, verifyIndex,
								TestCaseSheetName, testCase, testCaseIndex,
								nameSpace);
					}
				} else {
					if (verifyObj != null && actionObj != null
							&& testCase != null) {
						actionObj.verification.add(verifyObj);
						verifyObj = null;
					}
				}

				// / The read the Action
				String valueInActionColumn = null;

				col = worksheet.getRow(index).getCell((short) actionIndex);
				if (col != null) {
					valueInActionColumn = GetCellValueAsString(col);
				}

				if (StringUtils.isNotBlank(valueInActionColumn)) {
					if (actionObj != null && testCase != null) {
						testCase.actions.add(actionObj);
					}

					// Read the Action Section of the Row
					Log.Debug("Excel/GetTestCaseSheetValues : Calling ReadActionSection with row index as : "
							+ index);

					if (StringUtils.isNotBlank(valueInTestCaseColumn)) {
						actionObj = ReadActionSection(worksheet, labelIndex,
								_mapHashTable, index, actionIndex,
								TestCaseSheetName, null, testCaseIndex,
								nameSpace);
						
					} else {
						actionObj = ReadActionSection(worksheet, labelIndex,
								_mapHashTable, index, actionIndex,
								TestCaseSheetName, testCase, testCaseIndex,
								nameSpace);
					}
					//System.out.println("Excel/"+actionObj.arguments);
					ArrayList<Action> tempActions = new ArrayList<Action>();
				
				}
				// Else if this is a new test case
				if (StringUtils.isNotBlank(valueInTestCaseColumn)) {
					if (testCase != null) {
						testCases.add(testCase);
					}

					// / Read the TestCase Section of the Row
					Log.Debug("Excel/GetTestCaseSheetValues : Calling ReadTestCase with row index as : "
							+ index);
					// System.out.println("The Descriptions 3d"+description);
					testCase = ReadTestCase(worksheet, labelIndex, index,
							testCaseIndex, description, nameSpace,true);
					description = null;
				}

				// / Checking the last Row and saving the
				// TestCases/Actions/Verification in their proper Datastructures
				if (index == worksheet.getLastRowNum()) {
					Log.Debug("Excel/GetTestCaseSheetValues : Read the last Row of the Test Case sheet : "
							+ index);
					Log.Debug("Excel/GetTestCaseSheetValues : Saving the Action/ Verification and the TestCase Datastructures: "
							+ index);

					if (verifyObj != null && actionObj != null
							&& testCase != null) {
						actionObj.verification.add(verifyObj);
					}

					if (actionObj != null && testCase != null) {
						testCase.actions.add(actionObj);
					}

					if (testCase != null) {
						testCases.add(testCase);
					}
				}
			}
			// for(TestCase ts:testCases)
			// {
			// //System.out.println("EXCEL/GetTestCaseSheetValues:: The testcase name "+ts.testCaseID);
			// if(ts.testCaseID.equalsIgnoreCase("moltest"))
			// {
			//
			// for(Action ac:ts.actions)
			// System.out.println("arguments are "+ac.actionArguments);
			// }
			// }

		} catch (Exception e) {
			Log.Error("Excel/GetTestCaseSheetValues : Exception occured while Getting TestCaseSheet Values, exception message is : "
					+ e.getMessage());
			throw new Exception(
					"Exception occured while Getting TestCaseSheet Values, exception message is : "
							+ e.getMessage());
		}
		Log.Debug("Excel/GetTestCaseSheetValues : End of Function to read Test Case sheet");
	}// /GetTestCaseSheetValues

	// / This is a temporary function just for testing, to display the values of
	// Hashtables on to console
	// / After integration of this module with others this function should be
	// removed
	private String readHashTable(Hashtable<?, ?> hTable) {
		Set<?> it = hTable.keySet();
		StringBuilder hashStream = new StringBuilder();
		for (Object s : it) {
			hashStream.append("Key : " + s.toString() + "  value : "
					+ hTable.get(s).toString());
			hashStream.append("\n");
		}
		return hashStream.toString();
	}
}// class

