
package com.automature.spark.reporter;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.NavigableMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.automature.davos.exceptions.DavosExecutionException;
import com.automature.spark.engine.ExecutedTestCase;
import com.automature.spark.engine.Spark;
import com.automature.spark.exceptions.ReportingException;


public abstract class Reporter {

	public abstract boolean connect() throws ReportingException, Throwable;

	public abstract boolean ValidateDatabaseEntries()
			throws InterruptedException, Exception, Throwable;

	public void saveTestCaseResults(NavigableMap<String, ExecutedTestCase> executedTestCaseData)
			throws  Exception {
		
		if (executedTestCaseData.size() > 0) {
	
			Set<String> TestCaseKey = executedTestCaseData.keySet();
			for (String s : TestCaseKey) {
				ExecutedTestCase testCaseResult = (ExecutedTestCase) executedTestCaseData
						.get(s);
				Spark.message("\n" + testCaseResult.testCaseID
						+ "\t" + testCaseResult.testCaseStatus + "\t"
						+ testCaseResult.timeToExecute + "\t"
						+ testCaseResult.testCaseExecutionComments);
			}
		}
		Spark
		.message("\n\n\n");
/*		Spark
				.message("\n-----------------------------------------------------------------------------------------------------------------------");
		Spark
				.message("\n-----------------------------------------------------------------------------------------------------------------------");
*/
	}

	public abstract void heartBeat(String sessionid)
			throws InterruptedException, ReportingException;

	public abstract void SaveTestCaseResultEveryTime(ExecutedTestCase etc)
			throws Exception, ReportingException;

	public abstract void archiveLog() throws ReportingException;

	public abstract void saveTestCaseVariables(
			HashMap<String, String> variablemap, String testcase_id,
			String testsuite_name) throws ReportingException,
			InterruptedException;

	public abstract void destroySession(String sessionId)
			throws ReportingException;

	public abstract void setTestCycleTopologySetValues(String env_list)
			throws ReportingException;

	public void testCycleCleanup(String tcyclid,String tsname,String pid) throws InterruptedException,
			ReportingException {
		// TODO Auto-generated method stub
		
	}

	public void testCycleClearTestCases(String pid,String tpid,String tcid,String bldid,String tsname) throws InterruptedException, ReportingException {}

	public void testCycleClearTestCases(String testSuitName)  throws ReportingException {
		// TODO Auto-generated method stub
		
	}
	public void updateMachineIp() throws ReportingException{}
}
