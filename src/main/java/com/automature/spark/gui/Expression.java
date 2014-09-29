package com.automature.spark.gui;

import java.util.ArrayList;
import java.util.List;

public class Expression {
	
	private String action;
	private ArrayList<String> arguments;
	private String property;
	
	public Expression(){
		
	}
	public Expression(String action,ArrayList<String> tokens){
		this.action=action;
		arguments=tokens;
	}
	
	public String getAction() {
		return action;
	}
	public ArrayList<String> getArguments() {
		return arguments;
	}
	public String getProperty() {
		return property;
	}
	
	
}
