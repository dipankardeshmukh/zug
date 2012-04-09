/***
 * Data.java
 *		This class contain the Different Data Structures that is used through out the 
 *		Automation/Controller Lifecycle.
 *
 */


package com.automature.zug.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import com.automature.zug.util.Utility;

///****************** The Following Set of Classes are used for reading the 
///****************** Excel Input File ******************************/
 
/**
 *  Class to represent the User. It will contain the User Credentials
 */
class UserData
{    
	// Name of the User
	public String userName;
	
    // Password of the User
    public String userPassword;

    //Domain of the User.
    public String domain;
 }

 
/**
 *  Class to collect the data for test case getting executed for storing it
 *   to the Result Database. 
 */
class ExecutedTestCase
{
	public String testCaseID 				= null;
	public String testCaseStatus 			= null;
	public String testCaseExecutionComments = null;
	public int timeToExecute 				= 0;
    public Date testCaseCompletetionTime 	= Utility.dateNow();

}

 
/**
 *  This class will hold the Topology Set Details. 
 * This is required by to upload the Result to the Result/Framework Database.
 */
 class TopologySet
 {
	 public String topologyID 	= null;
	 public String topologyRole = null;
	 public String buildNumber 	= null;
 }

 
 /**
  * Class to contain the Properties of a Formal Arguments.
  * This is basically used for the Prototyping.
  */
class Arguments
{
	 public String argumentName 	= null;

	 public Boolean isScalar 		= true;
	 public Boolean isVector 		= false;
	 public Boolean isValue 		= true;
	 public Boolean isRef 			= false;
	 public Boolean isMandatory 	= true;
	 public String defaultValue 	= null;
	 public Boolean isDefaultValue 	= false;
}	


/**
 * Class to store the information for the Prototypes for Primitives
 * and Molecule.
 */
class Prototype
{
	public String prototypeName 		= null;
	public String prototypeDescription 	= null;
	public int argumentCount 			= 0;

	/*By default the Prototype specified will be a Molecule
	unless until specified as a primitive*/
	public Boolean isMolecule 		= true;

	/*By default the Prototype specified will be a Out-Of-Process
	unless until specified as a In-process.*/
	public String InProcessDllName 		= null;
	
	/* ArrayList to store the Information for Prototype Arguments.*/
	public ArrayList<Arguments> arguments 			= new ArrayList<Arguments>();
}

 
 /**
  * Class to represent a test case. A test case contain a number of Actions and these actions can itself contain
  * a number of verifications
  */ 
class TestCase
{
	/* This is only for significance for an Abstract Test Case
    This tells which is the parent ID of the test case which calls this Molecule*/
    public String parentTestCaseID 	= null;

    public String nameSpace 		= null;

    public String stackTrace 		= null;

    public String testCaseID 		= null;

    public String testCaseDescription = null;

    public String user 				= null;
    public UserData userObj;
   
    /* DataStructure to represent multiple actions for a test case.*/
    public ArrayList<Action> actions 		= new ArrayList<Action>();

    /* Two properties of a Test Case 
    1. Is the Test Case an Automated Test Case or Not
    2. On Expansion, whether we need to run the test cases in paralle.
    By default the test case will be automated unless until it is marked as a Manual Test Case.*/
    public Boolean automated = true;
    public Boolean concurrentExecutionOnExpansion = false;
    
    //This is the thread ID of the Test Case or the parent Process
    public String threadID = null;
//    //Molecule argument Definition
  public ArrayList<String> _testcasemoleculeArgDefn=new ArrayList<String>();
   //public HashMap<String,String>_testcasemoleculekeyvalueDefn=new HashMap<String, String>();
 }	

  
/**
 * Class to represent the Action of a TestCase
 */
class Action
{
	/*  This is only for significance for an Abstract Test Case
	This tells which is the parent ID of the test case which calls this Molecule*/
    public String parentTestCaseID = null;

    public String nameSpace = null;

    public String stackTrace = null;
    
    // The test case ID to which this Action belongs
    public String testCaseID = null;
     
    // Name of the Action
    public String actionName = null;

    /* Step of an Action. If two or more actions have same step, then they need to be executed simultaneously. 
    However this is not supported in the Controller yet, and is just provided for the Support for the Manual testers*/
    public String step = null;

    /* Data structure to store multiple actionArguments.*/
    public ArrayList<String> actionArguments = new ArrayList<String>();
     
    /*Datastructure to store multiple actionArguments. Note that they are the Actual Arguments, without any Manipulation.*/
    public ArrayList<String> actionActualArguments = new ArrayList<String>();

    /* There can be n number of Verification for an Action. The following DataStructure will hold that.*/
    public ArrayList<Verification> verification = new ArrayList<Verification>();

    /*The line number where this Action is present in the Excel Sheet*/
    public int lineNumber = 0;
    public String sheetName = null;

    /* Is this Action a Comment*/
    public Boolean isComment = false;

    public UserData userObj;
    //Molecule argument Definition
    public ArrayList<String> _actionmoleculeArgDefn=new ArrayList<String>();
    //public HashMap<String,String>_actionmoleculekeyvalueDefn=new HashMap<String, String>();
  }

/**\
 * Class to represent a verification of an Action.
 */
class Verification
{
	/*   This is only for significance for an Abstract Test Case
	  This tells which is the parent ID of the test case which calls this Molecule*/
	public String parentTestCaseID 	= null;
	public String nameSpace 		= null;
	public String stackTrace 		= null;


	/* The test case ID to which this Verification belongs*/
	public String testCaseID 		= null;
	public String verificationName 	= null;
	
	//TODO expected result has not been implemented as thought of. So it turned off
	//public String expectedResult 	= null;

	/* DataStructure to store multiple Verification Arguments.*/
	public ArrayList<String> verificationArguments = new ArrayList<String>();

    /* Data structure to store multiple Verification Arguments. Note that they are the Actual Arguments, without any manipulation.*/
	public ArrayList<String> verificationActualArguments = new ArrayList<String>();

	/*The line number where this Action is present in the Excel Sheet*/
	public int lineNumber 			= 0;
	public String sheetName 		= null;
	
	/* Is this Verification a Comment*/
	public Boolean isComment 		= false;

	public UserData userObj;
}