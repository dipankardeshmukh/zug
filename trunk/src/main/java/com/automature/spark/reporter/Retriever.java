package com.automature.spark.reporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public interface Retriever {
	public ArrayList<String> getProductList();
	public ArrayList<String> getTestPlanList(String pid);
	public ArrayList<String> getTestCycleList(String pid,String testPlanName);
	public ArrayList<String> getTestCycleTopologysetList(String tcid);
	public ArrayList<String> getBuildTagsForTestCycle(String tcid);
	public ArrayList<String> getTopoSetsByTestPlanId(String tpid);
	ArrayList<String> getBuildsByProductId(String pid);
}
