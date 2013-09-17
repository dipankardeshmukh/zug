package com.automature.zug.engine;


import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.automature.zug.util.Log;

public class AtomHandler {

	String existenseMessage="ok";
	String scriptLocation=null;
	public AtomHandler() {
		// TODO Auto-generated constructor stub
	}
	
	public AtomHandler(String location){
		scriptLocation=location;
	}
	
	public Set getAllPackages(){
		return Controller.invokeAtoms.keySet();
	}
	
	public List getAtoms(String packageName){
		return	Controller.invokeAtoms.get(packageName).getMethods();
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
	
	public String verfiyInProcessExistense(String name){
		String packageName=name.substring(0, name.indexOf("."));
		if(Controller.invokeAtoms.containsKey(packageName.toLowerCase())){
			String methodName=name.substring(name.indexOf(".")+1 );
			if(Controller.invokeAtoms.get(packageName.toLowerCase()).methodExists(methodName)){
				return existenseMessage;
			}else{
				return methodName+" does not exists in the package "+packageName;
			}
		}else{
			return packageName+" is not there in the Inprocess packages list";
		}
	}
	
	public String verifyOutProcessExistence(String name){
		String []locs=scriptLocation.split(";");
		for(String loc:locs){
			File f=new File(loc+ SysEnv.SLASH + name);
			if(f!=null && f.exists()){
				return existenseMessage;
			}
		}
		return name+" not present in the script locations : "+scriptLocation;
	}
	
	public String verifyBuildInAtom(String name){
		if(BuildInAtom.buildIns.contains(name.trim().toLowerCase())){
			return existenseMessage;
		}else{
			return name+" is not a built in atom.";
		}
	}
	
	public String verifyExistence(String name){
		if(this.isInProcess(name)){
			return this.verfiyInProcessExistense(name);
		}else if(this.isOutProcess(name)){
			return this.verifyOutProcessExistence(name);
		}else if(!name.startsWith("&")){
			return verifyBuildInAtom(name);
		}
		return existenseMessage;
	}
}
