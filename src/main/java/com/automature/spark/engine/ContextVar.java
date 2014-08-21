package com.automature.spark.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ContextVar extends com.automature.zug.api.ContextVar{

    private static final ContextVar INSTANCE = new ContextVar();

    public ContextVar(){
        super();
    }

    public static ArrayList getAllContextVar() throws Exception {
        return  com.automature.zug.api.ContextVar.getAllContextVar();
    }

    public static void setContextVar(String name, String value)
            throws Exception {
        com.automature.zug.api.ContextVar.setContextVar(name,value);
    }

    public static String getContextVar(String name) throws Exception {
        return com.automature.zug.api.ContextVar.getContextVar(name);
    }

    public static String getContextVarName(String value) throws Exception {
        return com.automature.zug.api.ContextVar.getContextVarName(value);
    }

    public static void alterContextVar(String name, String value)
            throws Exception {
        com.automature.zug.api.ContextVar.alterContextVar(name,value);
    }

    public static void Delete(String name) throws Exception {
        com.automature.zug.api.ContextVar.Delete(name);
    }

    public static void DeleteAll(int processId) throws Exception {
        com.automature.zug.api.ContextVar.DeleteAll(processId);
    }
    
    public static void deleteVariables(String vars) throws Exception{
    	com.automature.zug.api.ContextVar.deleteVariables(vars);
    }
    
    public static Map<String,String> getAllLocalVariables() throws Exception{
    	return com.automature.zug.api.ContextVar.getAllLocalVariables();
    }
    
    public static Map<String,String> getAllVariables()throws Exception{
    	return com.automature.zug.api.ContextVar.getAllVariables();
    }

    public static Map<String,String> getAllContextVariables()throws Exception{
    	return com.automature.zug.api.ContextVar.getAllContextVariables();
    }
    
    public static Map<String,String> getVariables(String variables)throws Exception{
    	return com.automature.zug.api.ContextVar.getVariablesWithValues(variables);
    }
    
    public static List getContextVariables()throws Exception{
    	return com.automature.zug.api.ContextVar.getContextVariables();
    }
    
    public static List getLocalVariables()throws Exception{
    	return com.automature.zug.api.ContextVar.getLocalVariables();
    }
    
    public static boolean checkIfContexVarExists(String name) {
    	return com.automature.zug.api.ContextVar.checkIfContexVarExists(name);
    }
    
}