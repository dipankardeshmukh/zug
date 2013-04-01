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
 * Class for storing multivaluedmacrovariables
 * @author Sankho
 *
 */
class MultiValuedMacro
{
	public String action_name=null;
	public int action_index=0;
	//public ArrayList<String> action_arg_index_list=new ArrayList<String>();
	public int action_arg_index=0;
	
	
}

