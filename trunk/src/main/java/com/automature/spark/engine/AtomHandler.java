package com.automature.spark.engine;


import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.automature.spark.beans.ExistenceMessageBean;
import com.automature.spark.util.Log;

public class AtomHandler {

	String existenseMessage="ok";
	//String scriptLocation=null;
	List<String> scriptLocations;
	public AtomHandler() {
		// TODO Auto-generated constructor stub
	}
	
/*	public AtomHandler(String location){
		scriptLocation=location;
	}
*/	
	public AtomHandler(List<String> scriptLocations) {
		// TODO Auto-generated constructor stub
		this.scriptLocations=scriptLocations;
	}

	public Set getAllPackages(){
		return Spark.invokeAtoms.keySet();
	}
	
	public List getAtoms(String packageName){
		return	Spark.invokeAtoms.get(packageName).getMethods();
	}
	
	public void handle(GTuple action, String threadID) throws Exception {
		Log.Debug("AtomHandler/handle : Running Command "
				+ action.name + " for TestCase ID as : "
				+ action.testCaseID);
		if (action.name.startsWith("@")) {
			OutProcessAtom opa=new OutProcessAtom();
			try {
				opa.run(action, threadID);
			} catch (Exception e) {
				throw e;
			}
		} else if (action.name.trim().contains(".")){
			//&& !action.actionName.startsWith("&")) {
				InProcessAtom ipa=new InProcessAtom();		
				ipa.run(action,threadID);
			
		} else if (action.name.trim().startsWith("#define")) {
		} else {
			BuildInAtom bia = new BuildInAtom();
			bia.run(action, threadID);
			
		}		Log.Debug("AtomHandler/handle : End of function with TestCaseID as : "
				+ action.testCaseID);
	} 
	
	public boolean isInProcess(String name){
		 if (name.trim().contains(".") && !isOutProcess(name) && !name.startsWith("&")){
			 return true;
		 }
		return false;
	}
	
	public boolean isOutProcess(String name){
		if(name.startsWith("@")){
			return true;
		}
		return false;
	}
	
	public ExistenceMessageBean verfiyInProcessExistense(String name){
		ExistenceMessageBean emb=new ExistenceMessageBean();
		String packageName=name.substring(0, name.indexOf("."));
		if(Spark.invokeAtoms.containsKey(packageName.toLowerCase())){
			
			String methodName=name.substring(name.indexOf(".")+1 );
			if(Spark.invokeAtoms.get(packageName.toLowerCase()).methodExists(methodName)){
				emb.setExists(true);
				List<String> methodList=Spark.invokeAtoms.get(packageName.toLowerCase()).getMethods(methodName);
				String message="";
				for(String meth:methodList){
					message+=meth+"\n";
				}
				emb.setMessage(  message);
			}else{
				emb.setExists(false);
				emb.setMessage( methodName+" does not exist in the package "+packageName);
			}
		}else{
			emb.setExists(false);
			emb.setMessage( packageName+" is not there in the Inprocess packages list");
		}
		return emb;
	}
	
	public ExistenceMessageBean verifyOutProcessExistence(String name){
		name=name.substring(1);
		ExistenceMessageBean emb=new ExistenceMessageBean();
		//String []locs=scriptLocation.split(";");
		for(String loc:scriptLocations){
			File f=new File(loc+ File.separator + name);
			if(f.exists()){
				emb.setExists(true);
				emb.setMessage("");//name+" is in the following path "+loc);
				return emb;
			}
		}
		 emb.setMessage(name+" not present in the script locations : "+scriptLocations);
		 emb.setExists(false);
		 return emb;
	}
	
	public ExistenceMessageBean verifyBuildInAtom(String name){
		ExistenceMessageBean emb=new ExistenceMessageBean();
		if(BuildInAtom.buildIns.contains(name.trim().toLowerCase())){
			emb.setExists(true);
			emb.setMessage("");
		}else{
			
			 emb.setExists(false);
			 emb.setMessage(name+" is not a built in atom.");
		}
		return emb;
	}
	
	public ExistenceMessageBean verifyExistence(String name){
		if(this.isInProcess(name)){
			return this.verfiyInProcessExistense(name);
		}else if(this.isOutProcess(name)){
			//return null;
			return this.verifyOutProcessExistence(name);
		}else if(!name.startsWith("&")){
			return verifyBuildInAtom(name);
		}
		return null;
	}
}
