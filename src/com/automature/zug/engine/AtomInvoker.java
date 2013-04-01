/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.automature.zug.engine;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.automature.zug.util.Log;
import com.automature.zug.util.ExtensionInterpreterSupport;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComException;
import com.jacob.com.Dispatch;
import java.util.Iterator;
import java.util.Set;
import org.apache.xbean.classloader.JarFileClassLoader;

/**
 *
 * @author automature
 */
public class AtomInvoker {

    /*
     * Variable initialization
     */
    private String EXTERNAL_PACKAGE_NAME = "";
    private String QUALIFED_JAR_FILE_PATH = "";
    private String ATOM_CLASS_NAME = "";
    public String NATIVE_DLL_FILE_PATH = "";
    public String NATIVE_DLL_NAME = "";
    public String COM_PROG_ID = "";
    private static final String JAVA = "Java";
    private static final String DLL = ".dll";
    public static final String DELIMITER = "^";
    
    private boolean method_found_flag;
    public boolean native_flag;
    public boolean com_flag;
    public ExtensionInterpreterSupport interpreter = new ExtensionInterpreterSupport();
    private ArrayList<URL> file_urls = new ArrayList<URL>();
    private HashMap<String, Class<?>> external_class_map = new HashMap<String, Class<?>>();
    private Method[] external_methods = null;
    //private static HashMap<String, Object> external_class_object_map = new HashMap<String, Object>();
    private Object external_class_object = null;
    private ClassLoader loader = null;
    private HashMap<String, ArrayList<String>> test = new HashMap<String, ArrayList<String>>();
    private Action inprocess_action;
    /*
     * Constructor
     */

    public AtomInvoker(String inprocesspackagename) throws Exception {


        Set<String> attribute_keys = interpreter.reteriveXmlTagAttributeValuesPair(Controller.inprocess_jar_xml_tag_path).keySet();
        Iterator<String> key_iter = attribute_keys.iterator();
        // debugMessage("AtomInvoker--"+pkg_name);
//debugMessage("Atom Invoker"+ interpreter.reteriveXmlTagAttributeValue(Controller.native_inprocess_xml_tag_path, Controller.inprocess_xml_tag_attribute_language).length);

        if (interpreter.reteriveXmlTagAttributeValue(Controller.native_inprocess_xml_tag_path, Controller.inprocess_xml_tag_attribute_language).length > 0) {

            while (key_iter.hasNext()) {
                String pkg_name = key_iter.next();
               
                if (inprocesspackagename.equalsIgnoreCase(pkg_name)) {
                    if (interpreter.reteriveXmlTagAttributeValuesPair(Controller.native_inprocess_xml_tag_path).get(pkg_name).equalsIgnoreCase(JAVA)) {
                        this.getFilePath(inprocesspackagename);
                        this.loadJarFile(this.QUALIFED_JAR_FILE_PATH);
                        this.native_flag = false;
                        this.com_flag = false;
                        break;
                    } else if (interpreter.reteriveXmlTagAttributeValuesPair(Controller.native_inprocess_xml_tag_path).get(pkg_name).equalsIgnoreCase("dll")) {
                        this.getNativeFilePath(inprocesspackagename);
                        this.getDllName(inprocesspackagename);
                        this.native_flag = true;
                        this.com_flag = false;
                        break;
                    } else if (interpreter.reteriveXmlTagAttributeValuesPair(Controller.native_inprocess_xml_tag_path).get(pkg_name).equalsIgnoreCase("com")) {
                        this.getCOMProgId(inprocesspackagename);
                        this.com_flag = true;
                        this.native_flag = false;
                        break;
                    }

                }

            }



            //debugMessage(inprocesspackagename + " Package name " + "Dll Name " + NATIVE_DLL_NAME + " File path " + NATIVE_DLL_FILE_PATH);
            //debugMessage("CLASS NAME--" + ATOM_CLASS_NAME + "\n Package path Name---" + QUALIFED_JAR_FILE_PATH + "\nFile pack Path----" + EXTERNAL_PACKAGE_NAME);
        }
    }

    /*
     *Set the Action object 
     * @param action
     *          Action
     */
    public void setInprocessAction(Action action) {
        this.inprocess_action = action;
    }
    /*
     * Get the Action Object
     */

    public Action getInprocessAction() {
        return this.inprocess_action;
    }
    /*
     * retreving the jar file path
     */

    private void getFilePath(String inprocesspackagename) {
        try {
        	//debugMessage(" printing method ");
        	//debugMessage(" the hashmap "+interpreter.readExternalJarFileArchitecture(inprocesspackagename));
        	if(interpreter.readExternalJarFileArchitecture(inprocesspackagename).size()>0)
            this.QUALIFED_JAR_FILE_PATH = interpreter.readExternalJarFileArchitecture(inprocesspackagename).get(inprocesspackagename).get(0);
        } catch (Exception ex) {
            Log.Error("Error in Interpreting ZugINI.xml for getting jar file path  " + ex.getMessage() +" "+ ex.getCause());
        }
    }
    /*
     * retreving the native dll file path
     */

    private void getNativeFilePath(String nativeinprocesspackagename) {
        try {
        	
            this.NATIVE_DLL_FILE_PATH = interpreter.readNativePackageArchitecture(nativeinprocesspackagename).get(nativeinprocesspackagename).get(0);
        } catch (Exception ex) {
            Log.Error("Error in Interpreting ZugINI.xml for Native DLL Path  " + ex.getMessage()+"  " + ex.getCause());
        }
    }

    /*
     * retreving the jar package architechture
     */
    private void getPackageArchitechure(String inprocesspackagename) {
        try {
if(interpreter.readExternalJarFileArchitecture(inprocesspackagename).get(inprocesspackagename).size()>0)
            this.EXTERNAL_PACKAGE_NAME = interpreter.readExternalJarFileArchitecture(inprocesspackagename).get(inprocesspackagename).get(1);
        } catch (Exception ex) {
            Log.Error("Error in Interpreting ZugINI.xml for Jar package " + ex.getMessage()+" " + ex.getCause());
        }
    }
    /*
     * retreving the class name of the package
     */

    private void getClassName(String inprocesspackagename) {
        try {
        	if(interpreter.readExternalJarFileArchitecture(inprocesspackagename).get(inprocesspackagename).size()>0)
            this.ATOM_CLASS_NAME = interpreter.readExternalJarFileArchitecture(inprocesspackagename).get(inprocesspackagename).get(2);


        } catch (Exception ex) {
            Log.Error("Error in Interpreting ZugINI.xml for Class instance " + ex.getMessage()+"  " + ex.getCause());

        }
        
    }
    /*
     * retreving the dll name of the package
     */

    private void getDllName(String nativeinprocesspackagename) {
        try {
            this.NATIVE_DLL_NAME = interpreter.readNativePackageArchitecture(nativeinprocesspackagename).get(nativeinprocesspackagename).get(1);


        } catch (Exception ex) {
            Log.Error("Error in Interpreting ZugINI.xml for Native Dll name " + ex.getMessage()+" " + ex.getCause());

        }
    }
    /*
     * Retrieving the program id of COM
     */

    private void getCOMProgId(String COMinprocesspackagename) {
        try {
            this.COM_PROG_ID = interpreter.readCOMPackageArchitecture(COMinprocesspackagename).get(COMinprocesspackagename).get(0);
        } catch (Exception e) {
            Log.Error("Error in Interpreting ZugINI.xml for COM progam ID " + e.getMessage() +" "+ e.getCause());
        }
    }
    /*
     * Load the File from the File path
     * @param String libDir
     */

    public void loadJarFile(String libDir) throws Exception {

        if (libDir.isEmpty()) {
            //System.out.println("[Warning] External Builtin Package tag in ZugINI.xml not defined. Please refer to the README.txt for more information");
            //throw new Exception();
        } else {
            try {
                File dependencyDirectory = new File(libDir);
                File[] jar_files = dependencyDirectory.listFiles();
                for (File f : jar_files) {
                    if (f.getName().endsWith(".jar")) {
                        file_urls.add(f.toURI().toURL());
                    }
                }
                loader = new JarFileClassLoader(EXTERNAL_PACKAGE_NAME, file_urls.toArray(new URL[file_urls.size()]));

            } catch (Exception ex) {
                Log.Error("Error in Interpreting ZugINI.xml " + libDir + "::JarFile not loaded from directory. Please refer to the README.txt for more information " + ex.getMessage());
            throw new Exception("Error in Interpreting ZugINI.xml " + libDir + "::JarFile not loaded from directory. Please refer to the README.txt for more information " + ex.getMessage());    
            }
        }
    }
    /*
     * Debug Message for Debugging
     * @param Obj as Object Class
     */

    public static void debugMessage(Object obj) {
        System.out.println(obj);
    }
    /*
     * Loading the Instance of the Class
     * @param classname as String
     */

    public void loadInstance(String inprocesspackagename) throws Exception {
        try {
            this.getPackageArchitechure(inprocesspackagename);
            this.getClassName(inprocesspackagename);


if(EXTERNAL_PACKAGE_NAME==null||ATOM_CLASS_NAME==null||ATOM_CLASS_NAME.isEmpty()||EXTERNAL_PACKAGE_NAME.isEmpty())
{
	
}else{
	//System.out.println("EXTERNAL_PACKAGE_NAME "+EXTERNAL_PACKAGE_NAME);
	//System.out.println("ATOM_CLASS_NAME "+ATOM_CLASS_NAME);
            external_class_map.put(inprocesspackagename, this.loader.loadClass(EXTERNAL_PACKAGE_NAME + "." + ATOM_CLASS_NAME));
            //System.out.println("Problem Start 2");

//Find out from here as it tries get null values and fails
            external_methods = external_class_map.get(inprocesspackagename).getMethods();

            external_class_object = external_class_map.get(inprocesspackagename).newInstance();
}            

        } catch (ClassNotFoundException ce) {
            Log.Error("Exception for Class not found::" + ATOM_CLASS_NAME + "\nMessage::" + ce.getMessage());
            throw ce;
        } catch (InstantiationException ie) {
            Log.Error("Exception for Instantiantion of ::" + inprocesspackagename + "\nMessage::" + ie.getMessage());
            throw ie;
        } catch (IllegalAccessException ia) {
            Log.Error("Exception for IllegalAccess of ::" + inprocesspackagename + "\nMessage::" + ia.getMessage());
            throw ia;
        } catch (Exception e) {
            Log.Error("Exception Occured ::" + inprocesspackagename + " Package is not present " + "\nException Due to Exception" + e.getClass());
            throw e;
        }
    }
    /*
     * Check if the Atom have negative propery
     * @param action name
     */

    @Deprecated
    public boolean checkIfNegativeAtom(String inprocess_atom_name) {
        try {
            //debugMessage("The Atom "+inprocess_atom_name);
            Action inpr_actn = getInprocessAction();
            String inprocess_action_name = inpr_actn.name.split("\\.")[1];
            //debugMessage("The action Name "+inprocess_action_name);
            if (inprocess_atom_name.equalsIgnoreCase(inprocess_action_name)) {
                //debugMessage("The checks "+inpr_actn.isNegative+"The Only Action "+inpr_actn.isActionNegative);
                if (inpr_actn.isNegative) {
                    return true;
                } else {
                    return false;
                }
            } else {
                Log.Debug("AtomInvoker/checkIfNegativeAtom: Atom is not Matching where atom: " + inprocess_atom_name + "\n Or Its is a Verification Atom");
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
    /*
     * Invokes the in process atom
     */

    static {


        String split_paths[] = System.getProperty("java.library.path").split(";");
        boolean dll_found = false,jni_loaded=false;
        String exception_message="";
        for (String paths : split_paths) {

            if (new File(paths + "\\JNILoader-x86.dll").exists()||new File(paths + "\\JNILoader-x64.dll").exists()) {

                dll_found = true;
                
                try{if (SysEnv.OS_ARCH.toLowerCase().contains("x86")) {
                    System.loadLibrary("JNILoader-x86");
                    jni_loaded=true;
                    break;
                } else {
                    System.loadLibrary("JNILoader-x64");
                    jni_loaded=true;
                    break;
                }
                }catch(UnsatisfiedLinkError Ur)
                {
                    jni_loaded=false;
                    //Ur.printStackTrace();
                    exception_message=Ur.getMessage();
                    //Log.Error("[Error] JNI loading error: "+Ur.getMessage());
                }

            }
            

        }
        if(!dll_found)
            {
                Log.Debug("[Warrning] JNI Loader Dll Not Found. Please Refer to README.TXT");
            }
        if(!jni_loaded&&dll_found)
        {
            Log.Debug("[Error] JNI loading error: "+exception_message+"\n\tPlease refer to README.TXT for more Information.");
        }
    }

    public native static void JNILoader(String method_name, String input_list, String native_file_path, String dll_name);

    public void invokeMethod(String method_name, ArrayList<String> inputs) throws InvocationTargetException, IllegalAccessException, Exception {
        try {
            //debugMessage("**********method name************ "+method_name);
            String input_list = "";

            if (native_flag) {
                //debugMessage("the actionname "+this.getInprocessAction().actionName.trim()+" The native file path "+this.NATIVE_DLL_FILE_PATH+" The dll name "+this.NATIVE_DLL_NAME);  
                int count = 0;
                for (String inp : inputs) {
                    if (count >= inputs.size() - 1) {
                        break;
                    }
                    input_list += inp + DELIMITER;
                    count++;
                }
                try {
                    JNILoader(this.getInprocessAction().name.toLowerCase().trim(), input_list, this.NATIVE_DLL_FILE_PATH, this.NATIVE_DLL_NAME + DLL);
                } catch (UnsatisfiedLinkError e) {
                    debugMessage("Error in Loading JNILoader.dll " + e.getMessage());
                    
                    throw new Exception("Error in Loading JNILoader.dll " + e.getMessage(), e);
                    //throw e;
                } catch (Exception ex) {
                    throw new Exception("Error in JNILoader.dll " + ex.getMessage() + "\n\t Message: " + ex.getCause().getMessage(), ex);
                }
                method_found_flag = true;
            } else if (com_flag) {
                try {//debugMessage("The prog id "+this.COM_PROG_ID);
                    String input_com_list = "";
                    ActiveXComponent COMObj = new ActiveXComponent(this.COM_PROG_ID);
                    //int respose = Dispatch.call(COMObj,"AlterContextVar", inputs.get(0),inputs.get(1)).toInt();
                    //Object com_param[]=inputs.toArray();
                    int i = 0;
                    for (String comparam : inputs) {
                        //debugMessage("The count "+i);
                        if (i ==inputs.size()) {
                            break;
                        }
                        i++;
                        input_com_list += comparam + DELIMITER;
                        //debugMessage("Com list increment "+input_com_list);
                    }
input_com_list=input_com_list.substring(0, input_com_list.length()-1);
//debugMessage("The COM Arguments are \n\tCOM ProgID: "+this.COM_PROG_ID+"\n\tMethod Name: "+method_name+"\n\tInputList: "+input_com_list);
                    method_found_flag = true;
                    int response = Dispatch.call(COMObj, "dispatch", method_name, input_com_list).toInt();

                } catch (ComException Ce) {
                    Log.Error("Exception in COM Dll. Please refer to Logs(Atom.log,Error.log) \n\tMessage: " + Ce.getMessage() + "\n\tSource: " + Ce.getSource());
                    
                    throw new Exception("Exception in COM Dll.", Ce);
                }catch(UnsatisfiedLinkError c)
                {
                  throw new Exception("Jacob Dll not found in java.library.path", c);
                }
                //debugMessage("Method Called "+response);

            } else {
                Object param[] = {method_name, inputs};
                for (Method imethod : external_methods) {
                    if (imethod.getName().equalsIgnoreCase("dispatch")) {
                        imethod.invoke(external_class_object, param);
                        //debugMessage("method invoked "+param.toString());
                        method_found_flag = true;
                        break;
                    }
                }
            }
            if (method_found_flag == false) {
                Log.Error("ZUG/AtomInvoker::dispatch method not present in class");
                throw new Exception("Zuoz Class Definition Inappropriate");
            }
        } catch (Exception e) {
            //debugMessage("2nd Level Exception");
            String exception_message = "";
            if (e.getMessage() != null) {
                exception_message += e.getMessage() + "\n";
  
            }
            if (e.getCause().getMessage() != null) {
                exception_message += e.getCause().getMessage() + "\n";
     
            }
            if(!com_flag)
            {
            if (e.getCause().getCause().getMessage() != null) {
                exception_message += e.getCause().getCause().getMessage() + "\n";
     
            }
            }
            if (exception_message.equals("")) {
                Log.Error("No Exception Message Not Found. Please Check Logs(Atom.log,Error.log)");
             
                throw e;
            }
            
//            if(checkIfNegativeAtom(method_name))
//            {
//             ContextVar.setContextVar("ZUG_EXCEPTION",exception_message);
//             //debugMessage("Comming to if Clause ");
//              Log.Error("Exception while invoking method :: " + method_name + "\nMessage:: " + exception_message);
//            }else
//            {
//               //debugMessage("Comming to else Clause ");
            if(Controller.opts.verbose)
            Log.Error("Exception while invoking method :: " + method_name + "\nMessage:: " + exception_message);
     
            throw new Exception(exception_message, e);
            // }

        }


    }
}
