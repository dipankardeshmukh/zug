package com.automature.spark.engine;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.automature.spark.util.Log;
import com.automature.spark.util.Utility;

public class Verification extends GTuple {
	
	boolean isVerifyNegative=false;
	public Verification() {
		super();
	
	}

	public Verification(Verification ver) {
		super(ver);
		this.isVerifyNegative = ver.isVerifyNegative;
	}
	
	//public Boolean isVerifyNegative = false;

	public void run(String threadID)throws Exception{
		try{
			boolean exceptionOccured=true;
			try {
				Log.Debug("Verification/run : Name of the Action is : " + this.name
						+ " for TestCaseID  : " + testCaseID);
				super.run(threadID);
				exceptionOccured=false;
				if(this.isVerifyNegative){
					throw new Exception(
							String.format(
									"\n\nException: The test step  passed while property is set to negative-verify or neg-verify."));
				}
				
			}catch(Exception e){
				if (this.isVerifyNegative && exceptionOccured) {
					if (!StringUtils
							.isBlank(TestSuite.errorMessageDuringTestCaseExecution
									.get(this.parentTestCaseID))) {
						TestSuite.errorMessageDuringTestCaseExecution
						.put(this.parentTestCaseID,StringUtils.EMPTY);
					}
					ContextVar.setContextVar("ZUG_VERIFY_EXCEPTION",
							Argument.NormalizeVariable(
									String.format(
											"\n\nException in Verification %s (%s:%s).\n\t Message: %s",
											this.name,
											this.sheetName,
											this.lineNumber,
											e.getMessage()), threadID,this));

				}else {						
					throw new Exception(
							String.format(
									"\n\nException in Verification %s (%s:%s).\n\t Message: %s",
									this.name, this.sheetName,
									this.lineNumber+1, e.getMessage()));
				}
			}
		
		}catch(Exception e){
		//	this.putExceptionMessage(e);
			throw e;
		}
	}
	}
