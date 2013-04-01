package com.automature.zug.reporter;

import java.util.HashMap;
import java.util.Hashtable;

import org.apache.commons.lang.StringUtils;
import com.automature.davos.exceptions.DavosExecutionException;
import com.automature.zug.engine.ExecutedTestCase;
import com.automature.zug.exceptions.ReportingException;

public interface Reporter {
	
	public boolean connect()throws ReportingException;

	public boolean ValidateDatabaseEntries()throws InterruptedException,Exception;
	
	public void saveTestCaseResults(Hashtable ht)throws ReportingException,Exception ;

	public void heartBeat(String sessionid)throws InterruptedException,ReportingException;

	public void SaveTestCaseResultEveryTime(ExecutedTestCase etc)throws Exception,ReportingException;

	public void archiveLog() throws ReportingException ;

	public void saveTestCaseVariables(HashMap<String, String> variablemap,
			String testcase_id, String testsuite_name)
					throws ReportingException, InterruptedException ;
	
}
