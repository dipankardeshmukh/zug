package com.automature.spark.reporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public interface Retriever {
	public ArrayList<String> getProductList();
	public ArrayList<String> getTestPlanList(String pid,String ip);
	public ArrayList<String> getTestCycleList(String pid,String testPlanName,String ip);
	public ArrayList<String> getTestCycleTopologysetList(String tcid);
	public ArrayList<String> getBuildTagsForTestCycleAndTopologyset(String pid,String topoid,String tcid);
	public ArrayList<String> getTopoSetsByTestPlanId(String tpid);
	ArrayList<String> getBuildsByProductId(String pid);
}
