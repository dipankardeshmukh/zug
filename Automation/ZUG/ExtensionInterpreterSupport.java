package ZUG;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import logs.Log;



public class ExtensionInterpreterSupport{

	String extension;
	String interpreterPath;
	String interpreterCommand;
	String InterpreterOption;
	Boolean optionPrecedence; // This flag is set when option preceds the script filename
	private static ArrayList<ExtensionInterpreterSupport> readConfigFile() throws Exception
	{
		String Pathlist = new String(System.getenv("Path"));
		ArrayList<ExtensionInterpreterSupport> extensionList = new ArrayList<ExtensionInterpreterSupport>();
		String filename = "ZugINI.xml";
		Document document=null;
		try{
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filename));
			document.getDocumentElement().normalize();
		}
		catch(Exception FileLoadException)
		{
			Log.Error("Zug/FileExtensionSupport: Failed to load the xml file "+ filename);
			throw FileLoadException;
			
		}
		NodeList languagesList = org.apache.xpath.XPathAPI.selectNodeList(document, "//languages//language");
		for(int i=0;i<languagesList.getLength();i++)
		{
			Element languageElement = (Element)languagesList.item(i);
			ExtensionInterpreterSupport languageObject = new ExtensionInterpreterSupport();
			Node node = org.apache.xpath.XPathAPI.selectSingleNode(languageElement, "extension");
			languageObject.extension = node.getTextContent();
			node = org.apache.xpath.XPathAPI.selectSingleNode(languageElement, "interpreter");
			languageObject.interpreterCommand = node.getTextContent();
			node = org.apache.xpath.XPathAPI.selectSingleNode(languageElement, "option");
			languageObject.InterpreterOption = node.getTextContent();
			node = org.apache.xpath.XPathAPI.selectSingleNode(languageElement, "path");
			languageObject.interpreterPath = node.getTextContent();			
			extensionList.add(languageObject);
			languageObject.optionPrecedence = true;
		}
		return extensionList; 
		
	}
	public static Hashtable<String,String> ReadFileExtension()
	{
		try{
			String Path = new String(System.getenv("PATH"));
			String[] PathList = Path.split(";");
			Hashtable<String,String> fileExtensionSupport =	new Hashtable<String,String>();
			String iniFileName = "Zug.ini";
			File iniFile=new File(iniFileName);
			Scanner sc = new Scanner(iniFile);
			do{
				if(sc.nextLine().equalsIgnoreCase("[languages]")) break;
			}while(sc.hasNextLine());
			while(sc.hasNextLine())
			{
				String line = sc.nextLine();
				if(line.contains("["))	break;
				String interpreterPath = null;
				boolean foundPath = false; 
				String[] result=line.split(" ", 3);
				if(result.length==3)
				{
					// extension support given with interpreter and fully qualified path
					if(result[2].endsWith("\"")) 
					{
						result[2]=result[2].substring(0, result[2].length()-1);
						if(result[2].endsWith("\\")) fileExtensionSupport.put(result[0], result[2]+result[1]+"\"");
						else fileExtensionSupport.put(result[0], result[2]+"\\"+result[1]+"\"");					
					}
					else if(result[2].endsWith("\\")) fileExtensionSupport.put(result[0], result[2]+result[1]);
					else fileExtensionSupport.put(result[0], result[2]+"\\"+result[1]);					
	
				}
				else if(result.length==2)
				{
					// extension support given with interpreter and path to be searched from system PATH listing
					String interpreter = result[1].subSequence(0, result[1].indexOf(".")).toString(); 
					for (String PathVar : PathList)
					{
						if(PathVar.toLowerCase().contains(interpreter.toLowerCase()))
						{
	//						Log.Debug("Controller/ExecuteCommand : Path Listing found with "+interpreter+" string");
							foundPath=true;
							if (PathVar.endsWith("\\"))
								fileExtensionSupport.put(result[0], PathVar + result[1]);
							else 
								fileExtensionSupport.put(result[0], PathVar + "\\"+ result[1]);
							break;
						}
					}
					
				}
				else
				{
					// extension with no specified interpreter.
					// this will be called in process without any interpreter prefix.
				}
				//System.out.println(line);
			}
			return fileExtensionSupport;
		}catch(FileNotFoundException fe){
			Log.Error("Zug.ini file not found for file extension support");
		}
		return null;
		
	}
	public static  Hashtable<String,String[]> ReadFileExtensionXML() throws Exception
	{
		
		String Path = new String(System.getenv("PATH"));
		String[] PathList = Path.split(";");
		Log.Debug("Environment variable Path + " + Path);
		String filename="ZugINI.XML";
		File file = new File(filename);
		Document document;
		Hashtable<String,String[]> fileExtensionSupport =	new Hashtable<String,String[]>();
		ArrayList<ExtensionInterpreterSupport> extensionsList = new ArrayList<ExtensionInterpreterSupport>();
		extensionsList = readConfigFile();
		// Load the XML file
		try
		{
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			document.getDocumentElement().normalize();
		}
		catch(Exception fileLoadException)
		{
			Log.Debug("XMLPrimitive/GetAttribute(): Failed to load the xml file "+ filename);
//			Log.Error("XMLPrimitive/GetAttribute(): Failed to load the xml file "+ filename);
			throw fileLoadException;
		}
		NodeList languagesList = org.apache.xpath.XPathAPI.selectNodeList(document, "//languages//language");
		for(ExtensionInterpreterSupport extensionConfig : extensionsList)
		{
			String extensionName = extensionConfig.extension.toLowerCase();
			String interpreterName = extensionConfig.interpreterCommand.toLowerCase();
			String interpreterOption = extensionConfig.InterpreterOption.toLowerCase();
			String interpreterPath = extensionConfig.interpreterPath.toLowerCase();
			Log.Debug("ExtensionInterpreterSupport/ReadFileExtension :: Values of language interpreter configurations "
					+ " extension name " + extensionName + " interpreter name " + interpreterName 
					+ " interpreter option " + interpreterOption + " interpreter path " + interpreterPath);
			if(interpreterPath.compareTo("")==0)
			{
				// extension support given with interpreter and path to be searched from system PATH listing
				boolean foundPath=false;
				String[] nameOfInterpreter = interpreterName.split("\\.");
				Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: "+"Extension is " + extensionName);
				Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Name searching in path = " + nameOfInterpreter[0]);
				for (String PathVar : PathList)
				{
					if(nameOfInterpreter.length==0)
						break;
					if(PathVar.toLowerCase().contains(nameOfInterpreter[0].toLowerCase()))
					{
//						Log.Debug("Controller/ExecuteCommand : Path Listing found with "+interpreter+" string");
						foundPath=true;
						if (PathVar.endsWith("\\"))
						{
							String[] interpreterList = {PathVar + interpreterName,interpreterOption};
							Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Command = " + interpreterList[0]);
							Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Option = " + interpreterList[1]);
							fileExtensionSupport.put(extensionName,interpreterList);
						}
						else
						{
							String[] interpreterList={PathVar + "\\"+ interpreterName,interpreterOption};
							Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Command = " + interpreterList[0]);
							Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Option = " + interpreterList[1]);
							fileExtensionSupport.put(extensionName, interpreterList);
						}
						break;
					}
				}
				if(foundPath==false)
				{
					String[] interpreterList = {interpreterName,interpreterOption};
					Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Command = " + interpreterList[0]);
					Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Option = " + interpreterList[1]);
					fileExtensionSupport.put(extensionName, interpreterList);
				}
				
			}
			else if(interpreterName.compareTo("")==0)
			{
				String[] interpreterList={"",""};
				Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Command = " + interpreterList[0]);
				Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Option = " + interpreterList[1]);
				fileExtensionSupport.put(extensionName, interpreterList);
			}
			else
			{
				// extension support given with interpreter and fully qualified path
				if(interpreterPath.endsWith("\"")) 
				{
					interpreterPath=interpreterPath.substring(0, interpreterPath.length()-1);
					if(interpreterPath.endsWith("\\"))
					{
						String[] interpreterList = {interpreterPath+interpreterName+"\"",interpreterOption};
						Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Command = " + interpreterList[0]);
						Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Option = " + interpreterList[1]);
						fileExtensionSupport.put(extensionName, interpreterList);
					}
					else 
					{
						String[] interpreterList = {interpreterPath+"\\"+interpreterName+"\"",interpreterOption};
						Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Command = " + interpreterList[0]);
						Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Option = " + interpreterList[1]);
						fileExtensionSupport.put(extensionName, interpreterList);					
					}
				}
				else if(interpreterPath.endsWith("\\")) 
				{
					String[] interpreterList= {interpreterPath+interpreterName,interpreterOption};
					Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Command = " + interpreterList[0]);
					Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Option = " + interpreterList[1]);
					fileExtensionSupport.put(extensionName, interpreterList);
				}
				else 
				{
					String[] interpreterList = {interpreterPath+"\\"+interpreterName, interpreterOption};
					Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Command = " + interpreterList[0]);
					Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Option = " + interpreterList[1]);
					fileExtensionSupport.put(extensionName, interpreterList);					
				}
			}
			
		}
		return fileExtensionSupport;
	}


}
