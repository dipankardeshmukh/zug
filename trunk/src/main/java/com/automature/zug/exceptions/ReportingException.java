package com.automature.zug.exceptions;

public class ReportingException extends Exception{
	public  ReportingException(String message){
		super(message);
	}
	
	public ReportingException(String message,Exception e){
		super(message,e);
	}
}
