package com.automature.spark.engine;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.automature.spark.util.Log;
import com.automature.spark.util.Utility;

public interface Atom {
	public abstract void run(GTuple action,String threadID)throws Exception;
}
