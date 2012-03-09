package ZUG;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import logs.Log;
import org.w3c.dom.NamedNodeMap;

public class ExtensionInterpreterSupport {

    //Atom Extention Support
    String extension;
    String interpreterPath;
    String interpreterCommand;
    String InterpreterOption;
    //External Builtin Support
    String jarfilepath;
    String jarpackage;
    String classname;
    //Configuration Support
    String machine_memorysize;
    String mvm_cardinality;
    Boolean optionPrecedence; // This flag is set when option preceds the script filename

    private static ArrayList<ExtensionInterpreterSupport> readConfigFile() throws Exception {
        String Pathlist = new String(System.getenv(Controller.PATH_CHECK));
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
            String Path = new String(System.getenv(Controller.PATH_CHECK));
            String[] PathList = Path.split(Controller.SEPARATOR);//changes from ";"
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

        String Path = new String(System.getenv(Controller.PATH_CHECK));
        String[] PathList = Path.split(Controller.SEPARATOR);//Changed from ";"
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
        String Path = new String(System.getenv(Controller.PATH_CHECK));
        String[] PathList = Path.split(Controller.SEPARATOR);//Changed from ";"
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
        ExtensionInterpreterSupport jarinterpreter = new ExtensionInterpreterSupport();
        NodeList locationList = org.apache.xpath.XPathAPI.selectNodeList(document, "//root//builtinpackages//builtinpackage");
        ArrayList<String> forJar = new ArrayList<String>();
        HashMap<String, ArrayList<String>> builtinpackagemap = new HashMap<String, ArrayList<String>>();


        for (int i = 0; i < locationList.getLength(); i++) {
            Element pathElement = (Element) locationList.item(i);
            if (pathElement.getAttribute("name").equalsIgnoreCase(attributeValue)) {
                Node path_node = org.apache.xpath.XPathAPI.selectSingleNode(pathElement, "file-path");
                jarinterpreter.jarfilepath = path_node.getTextContent();

                forJar.add(jarinterpreter.jarfilepath);
                Node jar_package_arch = org.apache.xpath.XPathAPI.selectSingleNode(pathElement, "jar-package");
                jarinterpreter.jarpackage = jar_package_arch.getTextContent();
                forJar.add(jarinterpreter.jarpackage);
                Node class_name = org.apache.xpath.XPathAPI.selectSingleNode(pathElement, "class-name");
                jarinterpreter.classname = class_name.getTextContent();
                forJar.add(jarinterpreter.classname);
                builtinpackagemap.put(pathElement.getAttribute("name"), forJar);
//System.out.println("HASHMAP\t"+builtinpackagemap);
                break;
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
    /*Reads a Configuration for Cartesian Product memorysize and mvm cardinality
     *
     * @param atrributeValue
     *                      as String
     * returns a HashMap with specific configuration.
     */

    public HashMap<String, ArrayList<String>> readConfigurationForCartestisanProduct(String attributeValue) throws Exception {


        String Path = new String(System.getenv(Controller.PATH_CHECK));
        String[] PathList = Path.split(Controller.SEPARATOR);//Changed from ";"
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
        NodeList locationList = org.apache.xpath.XPathAPI.selectNodeList(document, "//root//configurations//mvm-configuration");
        ArrayList<String> config_list = new ArrayList<String>();
        HashMap<String, ArrayList<String>> configurationMap = new HashMap<String, ArrayList<String>>();


        for (int i = 0; i < locationList.getLength(); i++) {
            Element pathElement = (Element) locationList.item(i);
            if (pathElement.getAttribute("name").equalsIgnoreCase(attributeValue)) {
                Node memory_node = org.apache.xpath.XPathAPI.selectSingleNode(pathElement, "jvm-max-memorysize");
                configurationInterpreter.machine_memorysize = memory_node.getTextContent();
                config_list.add(configurationInterpreter.machine_memorysize);
                Node mvm_cardinality_node = org.apache.xpath.XPathAPI.selectSingleNode(pathElement, "mvm-cardinality");
                configurationInterpreter.mvm_cardinality = mvm_cardinality_node.getTextContent();
                config_list.add(configurationInterpreter.mvm_cardinality);
                configurationMap.put(pathElement.getAttribute("name").toLowerCase(), config_list);
                break;
            }
            config_list.clear();
        }
       // Log.Error("THE MAP " + configurationMap);
        return configurationMap;
    }
}
