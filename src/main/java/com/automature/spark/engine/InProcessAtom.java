package com.automature.spark.engine;



import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.automature.spark.util.ExtensionInterpreterSupport;


public class InProcessAtom  implements Atom{

	public void run(GTuple action, String threadID) throws Exception {
	
		String type = "";
		boolean isAction = false;
		if (action instanceof Action) {
			type = "Action";
			isAction = true;
		} else {
			type = "Verification";
		}
		HiPerfTimer hft=null;
		try {
			boolean PkgstructureFound = false;
			String package_struct[] = action.name.trim().split("\\.",2);
			List<String> args=new ArrayList<String>();
			for (int i = 0; i < action.arguments.size(); i++) {
				if (!action.arguments.get(i).isEmpty()) {

					if (action.arguments.get(i).startsWith("$$%")
							&& action.arguments.get(i).endsWith("%")) {
						String action_args = StringUtils.removeStart(
								action.arguments.get(i), "$$");
						args.add( Argument.DoSomeFineTuning(
								action_args, threadID,action,false));
					} else {
						args.add(
								Argument.DoSomeFineTuning(
										action.arguments.get(i), threadID,action,false));
					}
				}else{
					args.add("");
				}
			}
			for (int i = 0; i < action.arguments.size(); i++) {
				if (!action.arguments.get(i).isEmpty()) {

					if (action.arguments.get(i).startsWith("$$%")
							&& action.arguments.get(i).endsWith("%")) {
						String action_args = StringUtils.removeStart(
								action.arguments.get(i), "$$");
						action.arguments.set(i, Argument.DoSomeFineTuning(
								action_args, threadID,action,true));
					} else {
						action.arguments.set(
								i,
								Argument.DoSomeFineTuning(
										action.arguments.get(i), threadID,action,true));
					}
				}
			}

			// TODO need to make the builtin_atom_package_name as
			// threadsafe....
			if(Spark.opts.showTime){
				
				hft=new HiPerfTimer();
				hft.Start();
			}
			
			Spark.message(String.format(
					"[%s] "+type+" %s Execution STARTED With Arguments %s ",
					action.stackTrace.toUpperCase(), action.name.toUpperCase(),
					args));
			for (String pkg_name : new ExtensionInterpreterSupport()
			.reteriveXmlTagAttributeValue(
					Spark.inprocess_jar_xml_tag_path,
					Spark.inprocess_jar_xml_tag_attribute_name)) {
				if (pkg_name.equalsIgnoreCase(package_struct[0])) {
					
					if (isAction) {
						Spark.invokeAtoms.get(pkg_name.toLowerCase())
						.setInprocessAction((Action) action);
					} 
					Spark.invokeAtoms.get(pkg_name.toLowerCase()).invokeMethod(
							package_struct[1].trim(), action.arguments);
				
					PkgstructureFound = true;
					break;
				}
				
			}

			if (PkgstructureFound == false) {
				throw new Exception(
						String.format(
								" %s Package architecture is not matching with Spark.ini definition ", package_struct[0]));
			}
			if(Spark.opts.showTime){
				hft.Stop();
				if(Spark.atomPerformance.containsKey(action.name.toLowerCase())){
					List l=Spark.atomPerformance.get(action.name.toLowerCase());
					l.add(hft.Duration());
					Spark.atomPerformance.put(action.name.toLowerCase(),l);
				}else{
					ArrayList al=new ArrayList();
					al.add(hft.Duration());
					Spark.atomPerformance.put(action.name.toLowerCase(),new ArrayList(al));
				}
				Spark
				.message(String.format(
						"\n[%s] "+type+" %s SUCCESSFULLY Executed in %s milli sec.",
						action.stackTrace.toUpperCase(),
						action.name.toUpperCase(),hft.Duration()));
			}else{
			Spark
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
