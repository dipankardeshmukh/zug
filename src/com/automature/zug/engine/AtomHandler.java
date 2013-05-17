package com.automature.zug.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.automature.zug.util.ExtensionInterpreterSupport;
import com.automature.zug.util.Log;
import com.automature.zug.util.Utility;

public class AtomHandler {

	public void handle(GTuple action, String threadID) throws Exception {
		UserData user = action.userObj;
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
}
