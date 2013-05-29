package com.automature.zug.engine;


import com.automature.zug.util.Log;

public class AtomHandler {

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
}
