package com.automature.zug.util;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.automature.zug.util.Log;
import com.automature.zug.engine.Controller;
import com.automature.zug.engine.SysEnv;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.traversal.NodeIterator;

public class ExtensionInterpreterSupport {

    //Atom Extention Support
    String extension;
    String interpreterPath;
    String interpreterCommand;
    String InterpreterOption;
    //Inprocess Jar Support
    String jarfilepath;
    String jarpackage;
    String classname;
    // Native Inprocess
    String nativefilepath;
    String nativedllname;
    //COM inprocess
    String comprogid;
    //Configuration Support
    String machine_memorysize;
    String mvm_cardinality;
    Boolean optionPrecedence; // This flag is set when option preceds the script filename
    //List of mvm configuration java max memory size
    public static List<Double> jvm_max_memory_list=new ArrayList<Double>();
    public Set<String> inprocesspackageError = new HashSet<String>();


    public static String getNode(String path)throws Exception{

        String filename = "ZugINI.xml";
        Document document = null;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filename));
            document.getDocumentElement().normalize();
        } catch (Exception FileLoadException) {
            Log.Error("Zug/FileExtensionSupport: Failed to load the xml file " + filename);
            throw FileLoadException;

        }
        Node ele = org.apache.xpath.XPathAPI.selectSingleNode(document, path);//.selectNodeList(document, path);
        if(ele==null)
            return null;
        return ele.getTextContent();
    }

    private static ArrayList<ExtensionInterpreterSupport> readConfigFile() throws Exception {
        String Pathlist = new String(System.getenv(SysEnv.PATH_CHECK));
        ArrayList<ExtensionInterpreterSupport> extensionList = new ArrayList<ExtensionInterpreterSupport>();
        String filename = "ZugINI.xml";
        Document document = null;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filename));
            document.getDocumentElement().normalize();
        } catch (Exception FileLoadException) {
            Log.Error("Zug/FileExtensionSupport: Failed to load the xml file " + filename);
            throw FileLoadException;

        }
        NodeList languagesList = org.apache.xpath.XPathAPI.selectNodeList(document, "//root//languages//language");
        for (int i = 0; i < languagesList.getLength(); i++) {
            Element languageElement = (Element) languagesList.item(i);
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

    public static Hashtable<String, String> ReadFileExtension() {
        try {
            String Path = new String(System.getenv(SysEnv.PATH_CHECK));
            String[] PathList = Path.split(SysEnv.SEPARATOR);//changes from ";"
            Hashtable<String, String> fileExtensionSupport = new Hashtable<String, String>();
            String iniFileName = "Zug.ini";
            File iniFile = new File(iniFileName);
            Scanner sc = new Scanner(iniFile);
            do {
                if (sc.nextLine().equalsIgnoreCase("[languages]")) {
                    break;
                }
            } while (sc.hasNextLine());
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.contains("[")) {
                    break;
                }
                String interpreterPath = null;
                boolean foundPath = false;
                String[] result = line.split(" ", 3);
                if (result.length == 3) {
                    // extension support given with interpreter and fully qualified path
                    if (result[2].endsWith("\"")) {
                        result[2] = result[2].substring(0, result[2].length() - 1);
                        if (result[2].endsWith("\\")) {
                            fileExtensionSupport.put(result[0], result[2] + result[1] + "\"");
                        } else {
                            fileExtensionSupport.put(result[0], result[2] + "\\" + result[1] + "\"");
                        }
                    } else if (result[2].endsWith("\\")) {
                        fileExtensionSupport.put(result[0], result[2] + result[1]);
                    } else {
                        fileExtensionSupport.put(result[0], result[2] + "\\" + result[1]);
                    }

                } else if (result.length == 2) {
                    // extension support given with interpreter and path to be searched from system PATH listing
                    String interpreter = result[1].subSequence(0, result[1].indexOf(".")).toString();
                    for (String PathVar : PathList) {
                        if (PathVar.toLowerCase().contains(interpreter.toLowerCase())) {
                            //						Log.Debug("Controller/ExecuteCommand : Path Listing found with "+interpreter+" string");
                            foundPath = true;
                            if (PathVar.endsWith("\\")) {
                                fileExtensionSupport.put(result[0], PathVar + result[1]);
                            } else {
                                fileExtensionSupport.put(result[0], PathVar + "\\" + result[1]);
                            }
                            break;
                        }
                    }

                } else {
                    // extension with no specified interpreter.
                    // this will be called in process without any interpreter prefix.
                }
                //System.out.println(line);
            }
            return fileExtensionSupport;
        } catch (FileNotFoundException fe) {
            Log.Error("Zug.ini file not found for file extension support");
        }
        return null;

    }

    public static Hashtable<String, String[]> ReadFileExtensionXML() throws Exception {

        String Path = new String(System.getenv(SysEnv.PATH_CHECK));
        String[] PathList = Path.split(SysEnv.SEPARATOR);//Changed from ";"
        Log.Debug("Environment variable Path + " + Path);
        String filename = "ZugINI.xml";
        File file = new File(filename);
        Document document;
        Hashtable<String, String[]> fileExtensionSupport = new Hashtable<String, String[]>();
        ArrayList<ExtensionInterpreterSupport> extensionsList = new ArrayList<ExtensionInterpreterSupport>();
        extensionsList = readConfigFile();
        // Load the XML file
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            document.getDocumentElement().normalize();
        } catch (Exception fileLoadException) {
            Log.Debug("XMLPrimitive/GetAttribute(): Failed to load the xml file " + filename);
//			Log.Error("XMLPrimitive/GetAttribute(): Failed to load the xml file "+ filename);
            throw fileLoadException;
        }
        NodeList languagesList = org.apache.xpath.XPathAPI.selectNodeList(document, "//root//languages//language");
        for (ExtensionInterpreterSupport extensionConfig : extensionsList) {
            String extensionName = extensionConfig.extension.toLowerCase();
            String interpreterName = extensionConfig.interpreterCommand.toLowerCase();
            String interpreterOption = extensionConfig.InterpreterOption.toLowerCase();
            String interpreterPath = extensionConfig.interpreterPath.toLowerCase();
            //System.out.println("the path is\t"+interpreterName);
            Log.Debug("ExtensionInterpreterSupport/ReadFileExtension :: Values of language interpreter configurations "
                    + " extension name " + extensionName + " interpreter name " + interpreterName
                    + " interpreter option " + interpreterOption + " interpreter path " + interpreterPath);
            if (interpreterPath.compareTo("") == 0) {
                // extension support given with interpreter and path to be searched from system PATH listing
                boolean foundPath = false;
                String[] nameOfInterpreter = interpreterName.split("\\.");
                Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: " + "Extension is " + extensionName);
                Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Name searching in path = " + nameOfInterpreter[0]);
                for (String PathVar : PathList) {
                    if (nameOfInterpreter.length == 0) {
                        break;
                    }
                    if (PathVar.toLowerCase().contains(nameOfInterpreter[0].toLowerCase())) {
//						Log.Debug("Controller/ExecuteCommand : Path Listing found with "+interpreter+" string");
                        foundPath = true;
                        if (PathVar.endsWith("\\")) {
                            String[] interpreterList = {PathVar + interpreterName, interpreterOption};
                            Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Command = " + interpreterList[0]);
                            Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Option = " + interpreterList[1]);
                            fileExtensionSupport.put(extensionName, interpreterList);
                        } else {
                            String[] interpreterList = {PathVar + "\\" + interpreterName, interpreterOption};
                            Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Command = " + interpreterList[0]);
                            Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Option = " + interpreterList[1]);
                            fileExtensionSupport.put(extensionName, interpreterList);
                        }
                        break;
                    }
                }
                if (foundPath == false) {
                    String[] interpreterList = {interpreterName, interpreterOption};
                    Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Command = " + interpreterList[0]);
                    Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Option = " + interpreterList[1]);
                    fileExtensionSupport.put(extensionName, interpreterList);
                }

            } else if (interpreterName.compareTo("") == 0) {
                String[] interpreterList = {"", ""};
                Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Command = " + interpreterList[0]);
                Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Option = " + interpreterList[1]);
                fileExtensionSupport.put(extensionName, interpreterList);
            } else {
                // extension support given with interpreter and fully qualified path
                if (interpreterPath.endsWith("\"")) {
                    interpreterPath = interpreterPath.substring(0, interpreterPath.length() - 1);
                    if (interpreterPath.endsWith("\\")) {
                        String[] interpreterList = {interpreterPath + interpreterName + "\"", interpreterOption};
                        Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Command = " + interpreterList[0]);
                        Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Option = " + interpreterList[1]);

                        fileExtensionSupport.put(extensionName, interpreterList);
                    } else {
                        String[] interpreterList = {interpreterPath + "\\" + interpreterName + "\"", interpreterOption};
                        Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Command = " + interpreterList[0]);
                        Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Option = " + interpreterList[1]);

                        fileExtensionSupport.put(extensionName, interpreterList);
                    }
                } else if (interpreterPath.endsWith("\\")) {
                    String[] interpreterList = {interpreterPath + interpreterName, interpreterOption};
                    Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Command = " + interpreterList[0]);
                    Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Option = " + interpreterList[1]);
                    fileExtensionSupport.put(extensionName, interpreterList);
                } else {
                    String[] interpreterList = {interpreterPath + "/" + interpreterName, interpreterOption};// "\\" changed to "/"l
                    Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Interpreter Command = " + interpreterList[0]);
                    Log.Debug("ExtensionInterpreterSupport/ReadFileExtensionXML :: Option = " + interpreterList[1]);
                    //System.out.println("Paths\t"+interpreterList[0]);
                    fileExtensionSupport.put(extensionName, interpreterList);
                }
            }

        }
        return fileExtensionSupport;
    }

    public HashMap<String, ArrayList<String>> readExternalJarFileArchitecture(String attributeValue) throws Exception {
        String Path = new String(System.getenv(SysEnv.PATH_CHECK));
        String[] PathList = Path.split(SysEnv.SEPARATOR);//Changed from ";"
        Log.Debug("Environment variable Path + " + Path);
        String filename = "ZugINI.xml";
        File file = new File(filename);
        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            document.getDocumentElement().normalize();
        } catch (Exception fileLoadException) {
            Log.Debug("XMLPrimitive/readExternalJarFileArchitecture(): Failed to load the xml file " + filename);
            throw fileLoadException;
        }

        ExtensionInterpreterSupport jarinterpreter = new ExtensionInterpreterSupport();
        NodeList locationList;
        boolean oldZugINI = false;

        locationList = org.apache.xpath.XPathAPI.selectNodeList(document,Controller.inprocess_packages_file_path);

        if (locationList.getLength()<1){
            oldZugINI = true;
            Log.Error("[WARNING] Package: "+attributeValue+". Please update the ZugINI format, the current format is deprecated and may not be supported in future releases.");
            locationList = org.apache.xpath.XPathAPI.selectNodeList(document,Controller.inprocess_jar_xml_tag_path);
        }

        ArrayList<String> forJar = new ArrayList<String>();
        HashMap<String, ArrayList<String>> builtinpackagemap = new HashMap<String, ArrayList<String>>();

        for (int i=0; i < locationList.getLength(); i++) {

            Node inprocessPackage =  locationList.item(i);
            String inprocessPackageName = inprocessPackage.getAttributes().getNamedItem("name").getNodeValue();
            String inprocessPackageLanguage = inprocessPackage.getAttributes().getNamedItem("language").getNodeValue();

            if (inprocessPackageName.equalsIgnoreCase(attributeValue)) {

                if(inprocessPackageLanguage.equalsIgnoreCase("Java"))
                {

                    if(oldZugINI){
                        Node path_node = org.apache.xpath.XPathAPI.selectSingleNode(inprocessPackage, "file-path");
                        jarinterpreter.jarfilepath = path_node.getTextContent();
                    }else{
                        jarinterpreter.jarfilepath = inprocessPackage.getParentNode().getAttributes().getNamedItem("dir").getNodeValue();
                    }

                    forJar.add(jarinterpreter.jarfilepath);
                    Node jar_package_arch = org.apache.xpath.XPathAPI.selectSingleNode(inprocessPackage, "jar-package");
                    jarinterpreter.jarpackage = jar_package_arch.getTextContent();

                    forJar.add(jarinterpreter.jarpackage);
                    Node class_name = org.apache.xpath.XPathAPI.selectSingleNode(inprocessPackage, "class-name");
                    jarinterpreter.classname = class_name.getTextContent();

                    forJar.add(jarinterpreter.classname);
                    builtinpackagemap.put(inprocessPackageName, forJar);

                    break;
                }
                else{

                    inprocesspackageError.add(attributeValue);
                    builtinpackagemap.put(inprocessPackageName, forJar);
                }
            }
            forJar.clear();
        }


        return builtinpackagemap;
    }





    public String[] reteriveXmlTagAttributeValue(String tagarchitechture, String attributeName) throws Exception {
        String filename = "ZugINI.xml";
        File file = new File(filename);
        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            document.getDocumentElement().normalize();
        } catch (Exception fileLoadException) {
            Log.Debug("XMLPrimitive/reteriveXmlTagAttributeValue(): Failed to load the xml file " + filename);
//			Log.Error("XMLPrimitive/GetAttribute(): Failed to load the xml file "+ filename);
            throw fileLoadException;
        }
        ExtensionInterpreterSupport jarinterpreter = new ExtensionInterpreterSupport();
        NodeList locationList = org.apache.xpath.XPathAPI.selectNodeList(document, tagarchitechture);
        String attributevalue[] = new String[locationList.getLength()];
        for (int i = 0; i < locationList.getLength(); i++) {
            Element pathElement = (Element) locationList.item(i);

            attributevalue[i] = pathElement.getAttribute(attributeName);

        }


        return attributevalue;
    }
    /*
     * Reads the attribute key=value pair 
     * as packagename=language
     */
    public HashMap<String,String> reteriveXmlTagAttributeValuesPair(String tagarchitechture) throws Exception {
        String filename = "ZugINI.xml";
        File file = new File(filename);
        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            document.getDocumentElement().normalize();
        } catch (Exception fileLoadException) {
            Log.Debug("XMLPrimitive/reteriveXmlTagAttributeValue(): Failed to load the xml file " + filename);
//			Log.Error("XMLPrimitive/GetAttribute(): Failed to load the xml file "+ filename);
            throw fileLoadException;
        }
        ExtensionInterpreterSupport jarinterpreter = new ExtensionInterpreterSupport();
        NodeList locationList = org.apache.xpath.XPathAPI.selectNodeList(document, tagarchitechture);
        HashMap<String,String> attributevalue = new HashMap<String, String>();
        for (int i = 0; i < locationList.getLength(); i++) {
            Element pathElement = (Element) locationList.item(i);

            attributevalue.put(pathElement.getAttribute("name"),pathElement.getAttribute("language"));

        }


        return attributevalue;
    }
    /*Reads a Configuration for Cartesian Product memorysize and mvm cardinality
     *
     *
     * returns a HashMap with specific configuration.
     */

    //public HashMap<String, ArrayList<String>> readConfigurationForCartestisanProduct(String attributeValue) throws Exception {
    public HashMap<String,String> readConfigurationForCartestisanProduct() throws Exception {


        String Path = new String(System.getenv(SysEnv.PATH_CHECK));
        String[] PathList = Path.split(SysEnv.SEPARATOR);//Changed from ";"
        Log.Debug("Environment variable Path + " + Path);
        String filename = "ZugINI.xml";
        File file = new File(filename);
        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            document.getDocumentElement().normalize();
        } catch (Exception fileLoadException) {
            Log.Debug("XMLPrimitive/readConfigurationForCartestisanProduct(): Failed to load the xml file " + filename);
//			Log.Error("XMLPrimitive/GetAttribute(): Failed to load the xml file "+ filename);
            throw fileLoadException;
        }
        ExtensionInterpreterSupport configurationInterpreter = new ExtensionInterpreterSupport();
        // NodeList locationList = org.apache.xpath.XPathAPI.selectNodeList(document, "//root//configurations//mvm-configuration");
        NodeList locationList = org.apache.xpath.XPathAPI.selectNodeList(document, "//root//configurations");
        ArrayList<String> config_list = new ArrayList<String>();
        HashMap<String, ArrayList<String>> configurationMap = new HashMap<String, ArrayList<String>>();
        HashMap<String, String> mvm_config_map = new HashMap<String, String>();


        for (int i = 0; i < locationList.getLength(); i++) {
            Element pathElement = (Element) locationList.item(i);
            NodeList mvm_config_node_list = org.apache.xpath.XPathAPI.selectNodeList(pathElement, "mvm-configuration");
            for (int j = 0; j < mvm_config_node_list.getLength(); j++) {
                Element mvm_config_node = (Element) mvm_config_node_list.item(j);
                mvm_config_map.put(mvm_config_node.getAttribute("jvm-max-memorysize"), mvm_config_node.getTextContent());
                jvm_max_memory_list.add(new Double(mvm_config_node.getAttribute("jvm-max-memorysize")));
                // System.out.println("jvm-max-memorysize:: " + mvm_config_node.getAttribute("jvm-max-memorysize") + " cardinality:: " + mvm_config_node.getTextContent());
            }


        }

        //return configurationMap;
        return mvm_config_map;
    }

    /*Reads a native inprocess package architecture
     *
     * @param atrributeValue
     *                      as String
     * returns a HashMap with specific configuration.
     */
    public HashMap<String, ArrayList<String>> readNativePackageArchitecture(String attributeValue) throws Exception {
        String Path = new String(System.getenv(SysEnv.PATH_CHECK));
        String[] PathList = Path.split(SysEnv.SEPARATOR);//Changed from ";"
        Log.Debug("Environment variable Path + " + Path);
        String filename = "ZugINI.xml";
        File file = new File(filename);
        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            document.getDocumentElement().normalize();
        } catch (Exception fileLoadException) {
            Log.Debug("XMLPrimitive/readExternalJarFileArchitecture(): Failed to load the xml file " + filename);
            //Log.Error("XMLPrimitive/GetAttribute(): Failed to load the xml file "+ filename);
            throw fileLoadException;
        }
        //System.out.println("The Zug INI "+filename+" document "+document.getDocumentURI());
        ExtensionInterpreterSupport nativeinterpreter = new ExtensionInterpreterSupport();
        NodeList locationList = org.apache.xpath.XPathAPI.selectNodeList(document,Controller.native_inprocess_xml_tag_path);
        ArrayList<String> forNativeLib = new ArrayList<String>();
        HashMap<String, ArrayList<String>> nativepackagemap = new HashMap<String, ArrayList<String>>();


        for (int i = 0; i < locationList.getLength(); i++) {
            Element pathElement = (Element) locationList.item(i);
            // System.out.println("The elementss "+pathElement.getAttribute("language")+"The name "+pathElement.getAttribute("name"));
            if (pathElement.getAttribute("name").equalsIgnoreCase(attributeValue)&&pathElement.getAttribute("language").equalsIgnoreCase("dll")) {
                Node path_node = org.apache.xpath.XPathAPI.selectSingleNode(pathElement, "file-path");
                nativeinterpreter.nativefilepath = path_node.getTextContent();

                forNativeLib.add(nativeinterpreter.nativefilepath);
                Node jar_package_arch = org.apache.xpath.XPathAPI.selectSingleNode(pathElement, "dll-name");
                nativeinterpreter.nativedllname = jar_package_arch.getTextContent();
                forNativeLib.add(nativeinterpreter.nativedllname);

                nativepackagemap.put(pathElement.getAttribute("name"), forNativeLib);
//System.out.println("HASHMAP\t"+nativepackagemap);
                break;
            }
            forNativeLib.clear();
        }
        return nativepackagemap;
    }
   /*Reads a COM inprocess package architecture
     *
     * @param atrributeValue
     *                      as String
     * returns a HashMap with specific configuration.
     */

    public HashMap<String, ArrayList<String>> readCOMPackageArchitecture(String attributeValue) throws Exception {
        String Path = new String(System.getenv(SysEnv.PATH_CHECK));
        String[] PathList = Path.split(SysEnv.SEPARATOR);//Changed from ";"
        Log.Debug("Environment variable Path + " + Path);
        String filename = "ZugINI.xml";
        File file = new File(filename);
        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            document.getDocumentElement().normalize();
        } catch (Exception fileLoadException) {
            Log.Debug("XMLPrimitive/readExternalJarFileArchitecture(): Failed to load the xml file " + filename);
//			Log.Error("XMLPrimitive/GetAttribute(): Failed to load the xml file "+ filename);
            throw fileLoadException;
        }
        //System.out.println("The Zug INI "+filename+" document "+document.getDocumentURI());
        ExtensionInterpreterSupport COMinterpreter = new ExtensionInterpreterSupport();
        NodeList locationList = org.apache.xpath.XPathAPI.selectNodeList(document,Controller.native_inprocess_xml_tag_path);
        ArrayList<String> forCOMLib = new ArrayList<String>();
        HashMap<String, ArrayList<String>> COMpackagemap = new HashMap<String, ArrayList<String>>();


        for (int i = 0; i < locationList.getLength(); i++) {
            Element pathElement = (Element) locationList.item(i);
            //System.out.println("The elementss "+pathElement.getAttribute("language")+"The name "+pathElement.getAttribute("name"));
            if (pathElement.getAttribute("name").equalsIgnoreCase(attributeValue)&&pathElement.getAttribute("language").equalsIgnoreCase("COM")) {//changed to jni from dll
                Node path_node = org.apache.xpath.XPathAPI.selectSingleNode(pathElement, "prog-id");
                COMinterpreter.comprogid = path_node.getTextContent();

                forCOMLib.add(COMinterpreter.comprogid);


                COMpackagemap.put(pathElement.getAttribute("name"), forCOMLib);
//System.out.println("HASHMAP\t"+nativepackagemap);
                break;
            }
            forCOMLib.clear();
        }
        return COMpackagemap;
    }
}