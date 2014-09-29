package com.automature.spark.gui.components;

import org.apache.commons.lang.StringUtils;

import com.automature.spark.gui.MacroEvaluator;

public class ArgumentHelper {
	
	private MacroEvaluator macroEvaluator;
	
	
	
	public ArgumentHelper( String fileName) {
		super();
		this.macroEvaluator = new MacroEvaluator(fileName);
	}



	public MacroEvaluator getMacroEvaluator() {
		return macroEvaluator;
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
