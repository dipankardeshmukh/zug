package com.automature.spark.engine;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.automature.davos.exceptions.DavosExecutionException;
import com.automature.spark.businesslogics.TestCaseResult;
import com.automature.spark.util.Log;
import com.automature.spark.util.Utility;

/**
 *  Class to collect the data for test case getting executed for storing it
 *   to the Result Database. 
 */
public class ExecutedTestCase
{
	public String testCaseID 				= null;
	public String testCaseStatus 			= null;
	public String testCaseExecutionComments = null;
	public String testcasedescription=null;
	public int timeToExecute 				= 0;
	public Date testCaseCompletetionTime 	= Utility.dateNow();
	

}