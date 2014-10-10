package com.automature.spark.gui.components;

import java.util.Set;

public class AutoCompleteArgFilter implements AutoCompleteFilter{

	private ArgumentHelper helper;
	
	
	
	public AutoCompleteArgFilter(ArgumentHelper helper) {
		super();
		this.helper = helper;
	}



	@Override
	public Set<String> getFilteredResult(String text) {
		// TODO Auto-generated method stub
		if(text.startsWith("$")){
			return helper.getMacroNavigableSet(text);
		}
		return null;
	}

}
