package com.automature.zug.engine;



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
			Controller
			.message(String.format(
					"\n[%s] "+type+" %s SUCCESSFULLY Executed",
					action.stackTrace.toUpperCase(),
					action.name.toUpperCase()));
		
		
		} catch (Exception e) {
			throw e;
		
		}

	}
}
