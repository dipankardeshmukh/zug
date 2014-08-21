package com.automature.spark.beans;

public class ActionInfoBean {

	String name;
	String arguments;
	int lineNo;
	
	
	public ActionInfoBean() {
		super();
	}
	
	
	public ActionInfoBean(String name, String arguments, int lineNo) {
		super();
		this.name = name;
		this.arguments = arguments;
		this.lineNo = lineNo;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getArguments() {
		return arguments;
	}
	public void setArguments(String arguments) {
		this.arguments = arguments;
	}
	public int getLineNo() {
		return lineNo;
	}
	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}


	@Override
	public String toString() {
		return "ActionInfoBean [name=" + name + ", arguments=" + arguments
				+ ", lineNo=" + lineNo + "]";
	}
	
	
}
