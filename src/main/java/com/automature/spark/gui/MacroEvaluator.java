package com.automature.spark.gui;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.automature.spark.gui.components.SheetTabPane;
import com.automature.spark.gui.sheets.SpreadSheet;

public class MacroEvaluator {
	
	private String fileName;

	
	public MacroEvaluator(String fileName) {
		super();
		this.fileName = fileName;
	}
	
	public String getMacroValue(SpreadSheet sp,String macro){
		
		String tmpMacro=macro;
		String macroSign = macro.startsWith("$$") ? "$$" : "$";
		if (macro.contains(".")) {
			String fileName = macro.substring(1, macro.indexOf("."));
			macro = macroSign + macro.substring(macro.indexOf(".") + 1);
			sp=SpreadSheet.findSpreadSheet(fileName);
			/*Iterator it = SpreadSheet.getUniqueSheets().keySet().iterator();
			while (it.hasNext()) {
				String fileUQ = (String) it.next();
				String file = new File(fileUQ).getName();
				if (file != null) {
					if (file.substring(0, file.lastIndexOf("."))
							.equalsIgnoreCase(fileName)) {
						sp = SpreadSheet.getUniqueSheets().get(fileUQ);
						break;
					}
				}
			}*/
		}
		if (sp != null) {
			String macroValue = sp.getMacroSheet().getMacroValue(macro.toLowerCase());
			//System.out.println(sp.getMacroSheet().getMacros());
			//System.out.println("macroValue "+macroValue);
			if(macroValue!=null)
				return macroValue;
		}else{
			//System.out.println("sp="+sp);
		}
		return tmpMacro;
	}
	
	public String getMacroValue(String macro){
		//System.out.println("file name "+fileName);
		
		SpreadSheet	sp=SpreadSheet.getUniqueSheets().get(fileName);
	//System.out.println("sp"+sp);
		return getMacroValue(sp, macro);
	}
	
	public String getMacroValue(String file,String macro){
		
	
		SpreadSheet	sp=SpreadSheet.getUniqueSheets().get(file);
		
		return getMacroValue(sp, macro);
	}
	
	public void evaluateMacrosValue(List<String> args){
		//System.out.println("file Name "+fileName);
		//System.out.println("args "+args);
		SpreadSheet	sp=SpreadSheet.getUniqueSheets().get(fileName);
		evaluateMacrosValue(sp, args);
	}
	
	public void evaluateMacrosValue(SpreadSheet sp,List<String> args){
		for(int i=0;i<args.size();i++){
			String arg=args.get(i);
			if(arg.startsWith("$")){
				args.set(i, getMacroValue(sp,arg));
			}
		}
	}

	public String getFileName() {
		return fileName;
	}
	
	
}
