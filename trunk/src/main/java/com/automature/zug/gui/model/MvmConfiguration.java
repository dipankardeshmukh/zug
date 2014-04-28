package com.automature.zug.gui.model;

public class MvmConfiguration {
	
	private String memSize;
	private String value;
	
	public String getMemSize() {
		return memSize;
	}
	public void setMemSize(String memSize) {
		this.memSize = memSize;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "MvmConfiguration [memSize=" + memSize + ", value=" + value
				+ "]";
	}
	
}
