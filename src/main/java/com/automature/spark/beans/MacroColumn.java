package com.automature.spark.beans;

import java.util.List;

public class MacroColumn {
	
	private String name;
	private List<String> cols;
	private int index=-1;
	private String value;
	
	public MacroColumn(String name, List<String> cols) {
		super();
		this.name = name;
		this.cols = cols;
		
	}

	public String getName() {
		return name;
	}

	public List<String> getCols() {
		return cols;
	}

	public int getIndex() {
		return index;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return name ;
	}

	public void setValue(String value) {
		this.value = value;
	}	

}
