package com.automature.spark.gui.components;


import java.util.NavigableSet;

public class AutoCompleteActionFilter implements AutoCompleteFilter{

	private ActionHelper helper;
	
	public AutoCompleteActionFilter(ActionHelper helper){
		this.helper=helper;
	}
	
	@Override
	public NavigableSet<String> getFilteredResult(String prefix) {
		// TODO Auto-generated method stub
		if(prefix.startsWith("&")){
			return helper.getMolecules(prefix);
		}else{
			return helper.getAtoms(prefix);			
		}

	}

}
