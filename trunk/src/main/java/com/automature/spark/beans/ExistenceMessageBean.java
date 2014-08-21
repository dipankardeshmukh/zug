package com.automature.spark.beans;

public class ExistenceMessageBean {

	private boolean exists;
	private String message;
	
	public ExistenceMessageBean() {
		super();
	
	}

	
	public ExistenceMessageBean(boolean exists, String message) {
		super();
		this.exists = exists;
		this.message = message;
	}

	public boolean isExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	@Override
	public String toString() {
		return message;
	}
	
	
	
}
