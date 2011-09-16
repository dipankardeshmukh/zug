/***
 * ProgramOpitions.java
 * 		This is the basic Utility Class. It contains the functionality to read the Arguments.
 * 		Helper class that supports the configuration of program options
 * 		from command line arguments or console input 
 */
package ZUG;

import java.io.File;
import java.util.Hashtable;

import jline.ConsoleReader;
import logs.Log;

import org.apache.commons.lang.StringUtils;

public class ProgramOptions {
	
public static String filelocation=null;
	private Hashtable<String, String> _opts;
	protected static String currentPath,workingDirectory;
	/*
	 * Creates a new instance.
	 * 
	 * @param opts: A hashtable containing name/value pairs or switches
	 */
	ProgramOptions(Hashtable<String, String> opts) {
		_opts = opts;
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
	
	public static ProgramOptions parse(String[] args) throws Exception {
		
		Hashtable<String, String> ht = new Hashtable<String, String>();		
/*		for (String opt : args) {
			int indexOfEqual = opt.indexOf("=");
			
			String[] nv = new String[2];
			if(indexOfEqual == -1)
			{
			 	nv[0] = opt.toLowerCase(); 
			 	nv[1] = "true"; 
			 }
			else
			{
				 	nv[0] = opt.substring(0, indexOfEqual).toLowerCase();
				 	nv[1] = opt.substring(indexOfEqual+1, opt.length()).trim();
			 }
			
				ht.put(nv[0], nv[1].trim().replaceAll("\"", "").replaceAll("'", "").trim());
		}
*/		for (String opt : args)
		{
			int indexOfDash = opt.indexOf("-");
			String[] nv = new String[2];
			if(indexOfDash==0)
			{
				int indexOfEqual = opt.indexOf("=");
				if (indexOfEqual == -1)
				{
					// this block is for switch options without equal sign like -Repeat
					nv[0] = opt.substring(1).toLowerCase();
					nv[1] = "true" ;
				}
				else
				{
					// this block is for name=value options with equal sign like -Count=12
					nv[0] = opt.substring(1, indexOfEqual).toLowerCase();
					nv[1] = opt.substring(indexOfEqual + 1);
				
				if(opt.contains("pwd"))
				{
					workingDirectory=opt.substring(indexOfEqual+1);
					//System.out.println("Working D\t"+workingDirectory);
				}
				
					
				}
			}
			else if (indexOfDash == -1 || indexOfDash != 0)
			{
				// this block is for option without Dash '-' . for inputfile
				
				if(ht.containsKey("inputfile"))
				{
					Log.Error("ProgramOptions/parse: Error : Repeated input file.");
					System.out.println("\n\nRedundant value : Input File " +
					"\n Use -help for Usage information\n\n");
					Log.Debug("ProgramOptions/parse: Error : Repeated input file. Program exiting");
					System.exit(1);

				}
				nv[0] = "inputfile";
				if(opt.contains(":"))  //Checks for the Absolute path of the input file
				{
					filelocation="";
					nv[1] = opt;
					String tempStrings[]=opt.split("\\\\");
					for(String divs:tempStrings)
					{
						if(!divs.contains(".xls"))
						filelocation+=divs+"\\";
					
					}
					
					
				}
				else
				{
					for(String str:args) //Chekching the whole input arguments again in a for loop
					{
						int indexofEq=str.indexOf("=");
						if(str.contains("pwd")) //Checking if it contains any -pwd= switch or not
						{currentPath=str.substring(indexofEq+1);	
							//System.out.println("This the Pats\t"+currentPath);
					nv[1]=currentPath.replaceAll("\\\\", "/")+"/"+opt; //Replacing the Current working Directory path provided by the batch file
						}
					}
				//System.out.println("The Path \t"+nv[1]);
					Log.Debug("Command Line Path Showing the Current Directory:\t"+nv[1]);
				}
			}
			if(opt.contains("-$")||opt.contains("-$$"))
			{
				Controller.macroentry=true;
				//controller.message("Macro Command Line Arguments\t"+macroentry);
				String macro,macrokey="$",macrovalue,namespaces="";
					macro=opt.replace("-$","");
				//macro=macro.replace("$", "");
				String fileArr[]=null;
				String tempPath=ht.get("inputfile").replaceAll("\\\\","/");
			
				
					fileArr=tempPath.split("/");
				for(String filename:fileArr)
				{
					
					if(filename.endsWith(".xls"))
					{
						
						filename=filename.replaceAll(".xls","");
						namespaces=filename.toLowerCase();
						//System.out.println("Namespace "+namespaces);
						Log.Debug(String.format("The Namespace is Created  %s",namespaces));
					//macrokey+=filename.toLowerCase();
					}
					
				}
				
				//controller.message("Macro Value is\t"+macro);
				String temp[]=null;
				temp=macro.split("=");
				if(temp.length==2)
				{
			    macrokey+=temp[0];
				macrovalue=temp[1];
				//controller.message("The MacroKey is "+macrokey+" The MacroValue is "+macrovalue);
				//macrocommandlineinputs.put(macrokey, macrovalue);
				Excel ee=new Excel();
				macrokey=ee.AppendNamespace(macrokey, namespaces);
				macrovalue=ee.ExpandMacrosValue(macrovalue, macrokey, namespaces);
				//ht.put(macrokey, macrovalue);
				//Creating the command line Macro hashmap 
				Controller.macrocommandlineinputs.put(macrokey,macrovalue);
				
				}
				else
				{
					//controller.message(macro+"-->The Value Assigned contains more than one =");
					Log.Error(macro+"->The Value Assigned Contains More Than One '=' ");
				}
			}
			ht.put(nv[0], nv[1].trim().replaceAll("\"", "").replaceAll("'", "").trim());
		}
//System.out.println("The File Path\t"+ht.get("inputfile")+"\n"+"The Total Hast Table\n"+ht);
		return new ProgramOptions(ht);
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

		if (StringUtils.isNotBlank(pwd))
		{
			return pwd;
		}
		return null;
	}
	
	/**
	 * Check if Hash table contains the specified key.
	 *@return true if the specified option is set else false.
	 */
	public boolean isSet (String opt)
	{
		return _opts.containsKey ( opt.toLowerCase() );
	}
	
	/**
	 * Check if string is null or empty.
	 * @return true if the Value is Null or Empty, else False.
	 */
	public static boolean isNullOrEmpty(String value)
	{
		if(value != null && value.trim().length() != 0)
			return false;
		
		return true;
	}
	
	/**
	 * @return Gets the current value for the specified option 
	 * 	the specified default if the option is not set
	 */
	public String getString (String opt, String dflt) throws Exception
	{
		String val;
		if ( isSet( opt.toLowerCase() ) )
		{
			val = (String)_opts.get(opt.toLowerCase());
			
			if(!isNullOrEmpty(val))
			{
				return val;
			}
		}
		
			return dflt;
	}
	
	/**
	 * Returns true if Help option is specified in the argument; False otherwise.
	 */
	public boolean isHelpRequest ( )
	{
		return isSet( "help" )
				||
			isSet( "-help" )
				||
			isSet( "--help" )
				||
			isSet( "-h" )
				||
			isSet("h")
				||
			isSet( "--h" )
				||
			isSet( "?" )
				||
			isSet( "-?" )
				||
			isSet( "/?" );
	}
	
	/**
	 * Check if the user is asking for version of tool.
	 * @return true - If the option contains string such as version,-version,--version,-v,--v
	 */
	public boolean isVersionRequest()
	{
		return isSet("version")
				||
			isSet("-version")
				||
			isSet("--version")
				||
			isSet("-v")
				||
			isSet("v")
				||
			isSet("--v");
	}

}
