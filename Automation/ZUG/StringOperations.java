/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ZUG;

import java.lang.reflect.Method;
import java.util.ArrayList;
import logs.Log;
/**
 *
 * @author Sankho
 */
public class StringOperations {
 private boolean method_found_flag=false;
    public synchronized void StringOperationsMethod(String method_name,ArrayList<String> inputs)throws AtomNotFoundException,StringIndexOutOfBoundsException,Exception
    {
        try {
            //Loading the Class Dynamically
            Class iClass = Class.forName("ZUG.StringOperations");
            //printMessage("The Class is found");
            //Getting all the methods
            Method[] iMethods = iClass.getMethods();
            Object iObject = iClass.newInstance();
            Object iParams[] = inputs.toArray();
            method_found_flag = false;
            for (Method lMethod : iMethods) {

                if (lMethod.getName().equalsIgnoreCase(method_name)) {
                    //printMessage("Methods found\t"+lMethod);
                    lMethod.invoke(iObject, iParams);
                    method_found_flag = true;
                    break;
                }
            }
            if (method_found_flag == false) {
                Log.Error("StringOperations.StringOperationsMethod- Atom Not Found-" + inputs);
                Log.Primitive("StringOperations.StringOperationsMethod- Atom Not Found" + inputs);
                throw new AtomNotFoundException();
            }

        } catch (Exception r) {
            Log.Error("StringOperations.StringOperationsMethod- Error Occured-" + method_name + inputs + ". Exception:: "+r.getMessage());
            Log.Primitive("StringOperations.StringOperationsMethod- Error Occured-" + method_name + inputs + ". Exception:: "+r.getMessage());
            throw r;
        }

    }
    public synchronized void getSubstring(String str1, String start_index, String end_index, String ContextVarName) throws Exception{

        try{

            Integer start = Integer.parseInt(start_index);
            Integer end = Integer.parseInt(end_index);
            ContextVar.setContextVar(ContextVarName, str1.substring(start,end));


        }
        catch(Exception e){
            Log.Primitive("BuiltInWebDriver.getSubstring-Problem generating substring. Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.getSubstring-Problem generating substring. Exception:: " + e.getMessage());
            throw e;

        }


    }
    public synchronized void getLength(String str,String contextvarname) throws StringIndexOutOfBoundsException,Exception
    {
        try
        {
           
            ContextVar.setContextVar(contextvarname, String.valueOf(str.length()));
        }
        catch(Exception e)
        {
Log.Primitive("StringOperationsMethod.getLength-Problem generating length. Exception:: " + e.getMessage());
            Log.Error("StringOperationsMethod.getLength-Problem generating substring. Exception:: " + e.getMessage());
            throw e;

        }
    }

}
