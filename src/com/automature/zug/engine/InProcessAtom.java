package com.automature.zug.engine;



import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.automature.zug.util.ExtensionInterpreterSupport;


public class InProcessAtom  implements Atom{

	public void run(GTuple action, String threadID) throws Exception {
	
		String type = "";
		boolean isAction = false;
		if (action instanceof Action) {
			type = "Action";
			isAction = true;
		} else {
			type = "verification";
		}
		HiPerfTimer hft=null;
		try {
			boolean PkgstructureFound = false;
			String package_struct[] = action.name.trim().split("\\.",2);
			for (int i = 0; i < action.arguments.size(); i++) {
				if (!action.arguments.get(i).isEmpty()) {

					if (action.arguments.get(i).startsWith("$$%")
							&& action.arguments.get(i).endsWith("%")) {
						String action_args = StringUtils.removeStart(
								action.arguments.get(i), "$$");
						action.arguments.set(i, Argument.NormalizeVariable(
								action_args, threadID));
					} else {
						action.arguments.set(
								i,
								Argument.NormalizeVariable(
										action.arguments.get(i), threadID));
					}
				}
			}

			// TODO need to make the builtin_atom_package_name as
			// threadsafe....
			if(Controller.opts.showTime){
				
				hft=new HiPerfTimer();
				hft.Start();
			}
			
			Controller.message(String.format(
					"[%s] Execution Started "+type+" %s with values %s ",
					action.stackTrace.toUpperCase(), action.name,
					action.arguments));
			for (String pkg_name : new ExtensionInterpreterSupport()
			.reteriveXmlTagAttributeValue(
					Controller.inprocess_jar_xml_tag_path,
					Controller.inprocess_jar_xml_tag_attribute_name)) {
				if (pkg_name.equalsIgnoreCase(package_struct[0])) {
					
					if (isAction) {
						Controller.invokeAtoms.get(pkg_name)
						.setInprocessAction((Action) action);
					} 
					Controller.invokeAtoms.get(pkg_name).invokeMethod(
							package_struct[1].trim(), action.arguments);
				
					PkgstructureFound = true;
					break;
				}
				
			}

			if (PkgstructureFound == false) {
				throw new Exception(
						String.format(
								" %s Package architecture is not matching with ZugINI.xml definition ", package_struct[0]));
			}
			if(Controller.opts.showTime){
				hft.Stop();
				if(Controller.atomPerformance.containsKey(action.name.toLowerCase())){
					List l=Controller.atomPerformance.get(action.name.toLowerCase());
					l.add(hft.Duration());
					Controller.atomPerformance.put(action.name.toLowerCase(),l);
				}else{
					ArrayList al=new ArrayList();
					al.add(hft.Duration());
					Controller.atomPerformance.put(action.name.toLowerCase(),new ArrayList(al));
				}
				Controller
				.message(String.format(
						"\n[%s] "+type+" %s SUCCESSFULLY Executed in %s milli sec.",
						action.stackTrace.toUpperCase(),
						action.name.toUpperCase(),hft.Duration()));
			}else{
			Controller
			.message(String.format(
					"\n[%s] "+type+" %s SUCCESSFULLY Executed",
					action.stackTrace.toUpperCase(),
					action.name.toUpperCase()));
			}
		
		} catch (Exception e) {
			throw e;
		
		}finally{
			if(hft!=null){
				hft.Stop();
			}
		}

	}
}
