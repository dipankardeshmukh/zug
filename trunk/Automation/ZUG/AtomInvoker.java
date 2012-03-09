/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ZUG;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import logs.Log;
import org.apache.xbean.classloader.JarFileClassLoader;

/**
 *
 * @author automature
 */
public class AtomInvoker {

    /*
     * Variable initialization
     */
    private static String EXTERNAL_PACKAGE_NAME = "";
    private static String QUALIFED_JAR_FILE_PATH = "";
    private static String ATOM_CLASS_NAME = "";
    private boolean method_found_flag;
    public ExtensionInterpreterSupport jarInterpreter = new ExtensionInterpreterSupport();
    private ArrayList<URL> file_urls = new ArrayList<URL>();
    private static HashMap<String, Class<?>> external_class_map = new HashMap<String, Class<?>>();
    private static Method[] external_methods = null;
    //private static HashMap<String, Object> external_class_object_map = new HashMap<String, Object>();
    private static Object external_class_object = null;
    private static ClassLoader loader = null;
    private HashMap<String,ArrayList<String>> test = new HashMap<String, ArrayList<String>>();
    /*
     * Constructor
     */

    public AtomInvoker(String builtinpackagename) throws Exception {

        
    for(String pkg_name:jarInterpreter.reteriveXmlTagAttributeValue(Controller.external_jar_xml_tag_path,Controller.external_jar_xml_tag_attribute_name))
    {
       // debugMessage("AtomInvoker--"+pkg_name);
        if (builtinpackagename.equalsIgnoreCase(pkg_name)) {

        //this.getPackageArchitechure(builtinpackagename);
        this.getFilePath(builtinpackagename);
       // this.getClassName(builtinpackagename);
//            this.QUALIFED_JAR_FILE_PATH=test.get(builtinpackagename).get(0);
//            this.EXTERNAL_PACKAGE_NAME=test.get(builtinpackagename).get(1);
//            this.ATOM_CLASS_NAME=test.get(builtinpackagename).get(2);
        this.loadJarFile(this.QUALIFED_JAR_FILE_PATH);
        //this.loadInstance(builtinpackagename);
break;
    }
// else{
//            throw new Exception(builtinpackagename+" not Matching with xml definition ");
//
//
// }
        }

    //debugMessage("CLASS NAME--"+ATOM_CLASS_NAME+"\n Package path Name---"+QUALIFED_JAR_FILE_PATH+"\nFile pack Path----"+EXTERNAL_PACKAGE_NAME);
    }
 
    /*
     * retreving the jar file path
     */

    private void getFilePath(String builtinpackagename) {
        try {
            this.QUALIFED_JAR_FILE_PATH = jarInterpreter.readExternalJarFileArchitecture(builtinpackagename).get(builtinpackagename).get(0);
        } catch (Exception ex) {
            Log.Error("Error in Interpreting ZugINI.xml  " + ex.getMessage());
        }
    }  /*
     * retreving the jar package architechture
     */
    private void getPackageArchitechure(String builtinpackagename) {
        try {
            this.EXTERNAL_PACKAGE_NAME = jarInterpreter.readExternalJarFileArchitecture(builtinpackagename).get(builtinpackagename).get(1);
        } catch (Exception ex) {
            Log.Error("Error in Interpreting ZugINI.xml  " + ex.getMessage());
        }
    }
    /*
     * retreving the class name of the oackage
     */

    private void getClassName(String builtinpackagename) {
        try {
            this.ATOM_CLASS_NAME = jarInterpreter.readExternalJarFileArchitecture(builtinpackagename).get(builtinpackagename).get(2);
            

        } catch (Exception ex) {
            Log.Error("Error in Interpreting ZugINI.xml  " + ex.getMessage());

        }
    }
    /*
     * Load the File from the File path
     * @param String libDir
     */

    public void loadJarFile(String libDir) throws Exception {
        try {
        if(libDir.isEmpty())
        {
        System.out.println("External Builtin Package folder location is not defined. Please refer to the README.txt for more information");
        throw new Exception();
        }
            File dependencyDirectory = new File(libDir);
            File[] jar_files = dependencyDirectory.listFiles();
            for (File f : jar_files) {
                if (f.getName().endsWith(".jar")) {
                    file_urls.add(f.toURI().toURL());
                }
            }
            loader = new JarFileClassLoader(EXTERNAL_PACKAGE_NAME, file_urls.toArray(new URL[file_urls.size()]));
         
        } catch (Exception ex) {
            Log.Error("Error in Interpreting ZugINI.xml " + libDir + "::JarFile not loaded from directory " + ex.getMessage());
        }
    }
    /*
     * Debug Message for Debugging
     * @param Obj as Object Class
     */
    public static void debugMessage(Object obj)
    {
        System.out.println(obj);
    }
    /*
     * Loading the Instance of the Class
     * @param classname as String
     */

    public void loadInstance(String builtinpackagename) throws Exception {
                    try {
                        this.getPackageArchitechure(builtinpackagename);
                        this.getClassName(builtinpackagename);

                
                external_class_map.put(builtinpackagename, this.loader.loadClass(EXTERNAL_PACKAGE_NAME + "." + ATOM_CLASS_NAME));
                
                external_methods = external_class_map.get(builtinpackagename).getMethods();
             
                external_class_object = external_class_map.get(builtinpackagename).newInstance();

            } catch (ClassNotFoundException ce) {
                Log.Error("Exception for Class not found::" + ATOM_CLASS_NAME + "\nMessage::" + ce.getMessage());
                throw ce;
            } catch (InstantiationException ie) {
                Log.Error("Exception for Instantiantion of ::" + builtinpackagename + "\nMessage::" + ie.getMessage());
                throw ie;
            } catch (IllegalAccessException ia) {
                Log.Error("Exception for IllegalAccess of ::" + builtinpackagename + "\nMessage::" + ia.getMessage());
                throw ia;
            } catch (Exception e) {
                Log.Error("Exception Occured ::" + builtinpackagename + "\nMessage::" + e.getMessage()+"\nException Due to Exception"+e.getClass());
                throw e;
            }
        } 
    

    public void invokeMethod(String method_name, ArrayList<String> inputs) throws InvocationTargetException, IllegalAccessException, Exception {
        try {
            Object param[] = {method_name, inputs};
            for (Method imethod : external_methods) {
                if (imethod.getName().equalsIgnoreCase("dispatch")) {
                    imethod.invoke(external_class_object, param);
                    method_found_flag=true;
                    break;
                }
            }
            if(method_found_flag==false)
            {
                Log.Error("ZUG/AtomInvoker::dispatch method not present in class");
                throw new Exception("Zuoz Class Definition Inappropriate");
            }

        } catch (InvocationTargetException ex) {
            Log.Error("Exception while invoking method for method::" + method_name + "\nMessage::" + ex.getMessage());
throw ex;
        } catch (IllegalAccessException ia) {
            Log.Error("Exception while accessing the method::" + method_name + "\nMessage::" + ia.getMessage());
throw ia;
        }
    }
}
