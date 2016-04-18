package com.automature.spark.engine;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONObject;

import com.automature.spark.beans.ExistenceMessageBean;
import com.automature.spark.util.Log;
import com.automature.zug.exceptions.AtomExecutionException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AtomHandler {
	
	private String existenseMessage="ok";
	//String scriptLocation=null;
	private List<String> scriptLocations;
	private FileFilter filter;
//	protected static boolean isOutpRocessAtomException=false;
//	public static ServerSocket cvConnection;
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
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		 filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
            	if(pathname.getAbsolutePath().endsWith(".xls")||pathname.getAbsolutePath().endsWith(".xlsx")){
            		return false;
            	}else{
            		 return pathname.isFile();
            	}
              
            }
      };
	}

	public Set getAllPackages(){
		return Spark.invokeAtoms.keySet();
	}

	/*public List getAtoms(String packageName){
		return	Spark.invokeAtoms.get(packageName).getMethods();
	}*/
	public void handle(GTuple action, String threadID) throws Exception {
		Log.Debug("AtomHandler/handle : Running Command "
				+ action.name + " for TestCase ID as : "
				+ action.testCaseID);
		if (action.name.startsWith("@")) {
					// TODO Auto-generated method stub
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

	public NavigableSet<String> getOutProcessAtoms(){
		NavigableSet<String> atoms=new TreeSet<>();
		
		for(String f:scriptLocations){
			File folder = new File(f);
			if(folder.exists()){
				File[] listOfFiles = folder.listFiles(filter);
				for (int i = 0; i < listOfFiles.length; i++) {
					atoms.add("@"+listOfFiles[i].getName());
				}
			}
		}
		return atoms;
	}

	public NavigableSet<String> getOutProcessAtoms(String prefix){
		NavigableSet<String> atoms=new TreeSet<>();
		
		for(String f:scriptLocations){
			File folder = new File(f);
			if(folder.exists()){
				File[] listOfFiles = folder.listFiles(filter);
				for (int i = 0; i < listOfFiles.length; i++) {
					
					if(listOfFiles[i].getName().startsWith(prefix)){
						atoms.add("@"+listOfFiles[i].getName());
					}
				}
			}
		}
		return atoms;
	}

	public NavigableSet<String> getInProcessPackages(String prefix){
		return (NavigableSet<String>) Spark.invokeAtoms.subMap( prefix, prefix + Character.MAX_VALUE ).keySet();
	}

	public NavigableSet<String> getInProcessPackageAtoms(String inprocessPackage){
		AtomInvoker ai=Spark.invokeAtoms.get(inprocessPackage.toLowerCase());
		if(ai!=null){
			Method [] external_methods=ai.getExternal_methods();
			NavigableSet<String> al=new TreeSet<String>();
			for(Method method:external_methods){
				String methodName=method.getName();
				if(ai.isDefaultMethod(methodName)){
					continue;
				}
				al.add(inprocessPackage+"."+methodName);
			}
			return al;
		}
		return null;
	}

	public NavigableSet<String> getInProcessPackageAtoms(String inprocessPackage,String prefix){
		AtomInvoker ai=Spark.invokeAtoms.get(inprocessPackage.toLowerCase());
		if(ai!=null){
			Method [] external_methods=ai.getExternal_methods();
			NavigableSet<String> al=new TreeSet<String>();
			for(Method method:external_methods){
				String methodName=method.getName();
				if(ai.isDefaultMethod(methodName)||!methodName.toLowerCase().startsWith(prefix)){
					continue;
				}
				al.add(inprocessPackage+"."+methodName);
			}
			return al;
		}
		return null;
	}

	public NavigableSet<String> getBuildInAtoms(String prefix){
		NavigableSet<String> al=new TreeSet<String>();
		for(String atom:BuildInAtom.buildIns){
			if(atom.startsWith(prefix)){
				al.add(atom);
			}
		}
		return al;
	}
	
	public NavigableSet<String> getAtoms(String prefix){
		if(prefix.startsWith("@")){
			return prefix.length()>1?getOutProcessAtoms(prefix.substring(1)):getOutProcessAtoms();
		}else{
			if(prefix.contains(".")){
				int indexOfPackage=prefix.indexOf('.');
				String inprocessPackage=prefix.substring(0,indexOfPackage);
				if(prefix.length()==indexOfPackage+1){
					return getInProcessPackageAtoms(inprocessPackage);
				}else{
					return getInProcessPackageAtoms(inprocessPackage, prefix.substring(indexOfPackage+1, prefix.length()));
				}
			}else{
				NavigableSet<String> al=new TreeSet<String>();
				al.addAll(getInProcessPackages(prefix));
				al.addAll(getBuildInAtoms(prefix));
				return al;
			}
		}
	}
}
