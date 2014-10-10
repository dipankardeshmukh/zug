package com.automature.spark.gui.components;

import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.automature.spark.gui.MacroEvaluator;
import com.automature.spark.gui.sheets.SpreadSheet;

public class ArgumentHelper {
	
	private MacroEvaluator macroEvaluator;
	
	
	
	public ArgumentHelper( String fileName) {
		super();
		this.macroEvaluator = new MacroEvaluator(fileName);
	}



	public MacroEvaluator getMacroEvaluator() {
		return macroEvaluator;
	}

	
	public NavigableSet<String> getMacroNavigableSet(String prefix){
		SpreadSheet sp=null;
		int indexOfPackage=0;
		String namespace="";
		String macroPrefix=prefix;
		NavigableSet<String> set=null;
		if(prefix.contains(".")){
			indexOfPackage=prefix.indexOf('.');
			namespace=prefix.substring(1, indexOfPackage);
			macroPrefix="$"+prefix.substring(indexOfPackage+1);
			sp=SpreadSheet.findSpreadSheet(namespace);
			Set<String> moleculeId=sp.getMacroSheet().getMacros().keySet();
			set=new TreeSet<String>();
			namespace="$"+namespace+".";
			//System.out.println((indexOfPackage+1) +" "+prefix.length());
			if(indexOfPackage+1==prefix.length()){
				for(String s:moleculeId){
					set.add(namespace+s.substring(1));
				}
			//	System.out.println("set "+set);
			}else{
				for(String s:moleculeId){
					if(s.startsWith(macroPrefix)){
						set.add(namespace+s.substring(1));
					}
				}
			}
		}else{
		//	System.out.println("spreadSheet "+spreadSheet);
			sp = SpreadSheet.getUniqueSheets().get(macroEvaluator.getFileName());

			if(sp!=null){
				Set<String> moleculeId=sp.getMacroSheet().getMacros().keySet();
		//		System.out.println("moleucle ids "+moleculeId);
				set=new TreeSet<String>();
				for(String s:moleculeId){
					if(s.startsWith(macroPrefix)){
						set.add(s);
					}
				}
			}else{
		//		System.out.println("sp "+sp);
			}
		}
	//	System.out.println("returning set "+set);
		return set;
		
	}


	public String getToolTipForArgs(String item) {
		if(StringUtils.isBlank(item)){
			return "";
		}
	//	System.out.println("argument "+item);
		if (item.startsWith("$")) {
			String tooltip=macroEvaluator.getMacroValue(item);
	//		System.out.println("tool tip "+tooltip);
			return tooltip.equals(item)?null:tooltip;
		} else if (item.contains("=") && item.contains("$")) {
			String[] temp = item.split("=", 2);
			String arg1=null;
			String arg2=null;

			if(temp[1].startsWith("$")){
				arg1=macroEvaluator.getMacroValue(temp[0]);
			}
			if (temp.length == 2) {
				if (temp[1].startsWith("$")) {
					arg2= macroEvaluator.getMacroValue(temp[1]);

				}
			}

			return (arg1!=null && arg2!=null)?arg1+"="+arg2:(arg1==null?arg2:arg1);

		}

		return null;
	}
}
