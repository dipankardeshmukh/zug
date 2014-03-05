package com.automature.zug.engine;

import java.util.ArrayList;


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

}