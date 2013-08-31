package com.automature.zug.engine;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.automature.zug.util.Log;
import com.automature.zug.util.Utility;

public interface Atom {
	public abstract void run(GTuple action,String threadID)throws Exception;
}
