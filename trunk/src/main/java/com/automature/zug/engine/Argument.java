package com.automature.zug.engine;

import org.apache.commons.lang.StringUtils;

import com.automature.zug.util.Log;
import com.automature.zug.util.Utility;

import org.apache.poi.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class Argument {



	/***
	 * Function to get the correct argument at Runtime to pass to an Action or
	 * Verification... The function will check if this is a Context Variable or
	 * not and pass the Value to the Actions accordingly.
	 * 
	 * @param argument
	 *            Name of the Argument
	 * @throws Exception 
	 */




	static String NormalizeVariable(String argument, String threadID,GTuple action)
			throws Exception {
		if (StringUtils.isBlank(argument)) {
			Log.Debug("Argument/NormalizeVariable : Start of function with variableToFind = EMPTY and its value is -> EMPTY. ");
			return StringUtils.EMPTY;
		}
		Log.Debug(String
				.format("Argument/NormalizeVariable : Start of function with argument = %s .",
						argument));
		String tempValue = argument;
		if (argument.contains("=")) {
			Log.Debug("Argument/NormalizeVariable : The Variable to Find contains an = sign ");
			String[] splitVariableToFind =argument.split("=", 2);// Excel.SplitOnFirstEquals(argument);

			Log.Debug("Argument/NormalizeVariable : Length of  splitVariableToFind = "
					+ splitVariableToFind.length);
			if (splitVariableToFind.length <= 1) {
				String tempVariable = argument;
				if (tempVariable.endsWith("##")) {
					tempVariable = Utility.TrimStartEnd(tempVariable, '#', 0);
					tempVariable = Utility.TrimStartEnd(tempVariable, '#', 1);
					tempVariable += threadID;
				}
				Log.Debug("Argument/NormalizeVariable : End of function with variableToFind = "
						+ argument + " and its value is -> " + tempVariable);
				return tempVariable;
			}

			tempValue = splitVariableToFind[1];
			Log.Debug("Argument/NormalizeVariable : variableToFind = "
					+ tempValue);

			String tempVariableOutside = splitVariableToFind[0];
			if (tempVariableOutside.endsWith("##")) {
				tempVariableOutside = Utility.TrimStartEnd(tempVariableOutside,
						'#', 0);
				tempVariableOutside = Utility.TrimStartEnd(tempVariableOutside,
						'#', 1);
				tempVariableOutside += threadID;
			}

			tempValue = tempVariableOutside + "="
					+ DoSomeFineTuning(tempValue, threadID,action,false);
		} // First Check in the Context Variable
		else {
			tempValue = DoSomeFineTuning(tempValue, threadID,action,false);
		}

		Log.Debug("Argument/NormalizeVariable : End of function with variableToFind = "
				+ argument + " and its value is -> " + tempValue);

		return tempValue;
	}





	static String getVarValues(String variable,String threadID,GTuple action) throws Exception{

		String tempVariable = variable;
		boolean isContextVar=false;
		// message("The String subs.. "+tempVariable);
		Log.Debug(String.format(
				"Argument/getVarValues : Context Variable = ",
				tempVariable));
		// First Check in the Macro Sheet
		if (tempVariable.startsWith("%") && tempVariable.endsWith("%")) {

			tempVariable = Utility.TrimStartEnd(tempVariable, '%', 0);
			tempVariable = Utility.TrimStartEnd(tempVariable, '%', 1);
		}
		if (tempVariable.endsWith("##")) {
			tempVariable = Utility.TrimStartEnd(tempVariable, '#', 0);
			tempVariable = Utility.TrimStartEnd(tempVariable, '#', 1);
			tempVariable += threadID;
		}

		// if (StringUtils.isNotBlank(ContextVar
		// .getContextVar(tempVariable))) {
		isContextVar = true;
		// }
		String var=action.parent.getVariableDBReference(tempVariable);
		tempVariable=(var==null?Controller.ReadContextVariable(tempVariable):Controller.ReadContextVariable(var));
		
		Log.Debug(String
				.format("Argument/getVarValues : After  Variable Parsing, variableToFind = %s ",
						tempVariable));

		return tempVariable;

		//return new String(actualValue);
	} 


	public static  List<String> getContextVariableList(String variable){
		boolean isContextVar = false;
		/*	System.out.println("Argument/DoSomeFineTuning : Start of Function with Variable Name as : "
				+ variable);*/

		int firstOccuranceOfPercentage = -1;
		int secondOccuranceOfPercentage = -1;
		int indexer = -1;

		char prev_char= ' '; // previous character to check if the % was escaped


		List<String> contextVars=new ArrayList<String>();		
		for (char varChar : variable.toCharArray()) {
			indexer++;
			if(varChar == '%' && prev_char == '\\'){
				//		escapedIndexes.add(indexer-1);
				prev_char=varChar;
				continue;
			}

			if (varChar == '%' && prev_char != '\\') {
				if (firstOccuranceOfPercentage >= 0) {
					secondOccuranceOfPercentage = indexer;
					contextVars.add(variable.substring(firstOccuranceOfPercentage,secondOccuranceOfPercentage+1));
					//System.out.println("second occurence "+secondOccuranceOfPercentage+" CV "+variable.substring(firstOccuranceOfPercentage,secondOccuranceOfPercentage+1));
					firstOccuranceOfPercentage=-1;
					secondOccuranceOfPercentage=-1;

				} else {
					firstOccuranceOfPercentage = indexer;
					//Log.Debug(String.format("Argument/DoSomeFineTuning : firstOccuranceOfPercentage=%d in Variable %s.",
					//indexer, variable));
				}
			}
			prev_char=varChar;
		}
		return contextVars;

	}

	public static String parseContextVariables(List<String> contextVars,String var,String threadID,GTuple action) throws Exception{

		StringBuffer sb=new StringBuffer();
		String []tmp=null;
		for(int i=0;i<contextVars.size();i++){
			tmp=var.split(contextVars.get(i), 2);
			//	System.out.println("Argument/cv :"+contextVars.get(i));
			 String cv=getVarValues(contextVars.get(i),threadID,action);
			//		System.out.println("Argument/cv value "+cv);
			if(tmp.length>1){
				var=tmp[1];
				sb.append(tmp[0].replace("\\%", "%"));					
			}
			sb.append(cv);
		}
		if(tmp!=null&&tmp.length>1){
			sb.append(tmp[1].replace("\\%", "%"));
		}
		//System.out.println("value returned cv >1 :"+sb+"\tend");

		return sb.toString();
	}

	public static String parseLocalVarArgumentsName(String variable,String threadID,GTuple action) throws Exception{
		
		String tempVariable = variable;
		if (tempVariable.endsWith("##")) {
			tempVariable = Utility.TrimStartEnd(tempVariable, '#', 0);
			tempVariable = Utility.TrimStartEnd(tempVariable, '#', 1);
			tempVariable += threadID;
		}
		String variableref=action.parent.getVariableDBReference(tempVariable);
		if(variableref!=null){
			return variableref;
		}else{
			if(tempVariable.startsWith("'")&&tempVariable.endsWith("'")){
				tempVariable=tempVariable.substring(1,tempVariable.length()-1);
			}/*else{
				
				tempVariable = tempVariable.replace("\\'", "'");
				
			}*/
			tempVariable = tempVariable.replace("\\%", "%");	
		}
		Log.Debug(String
				.format("Argument/parseLocalVarArgumentsName : End of Function. Function returning %s for Variable %s ",
						tempVariable, variable));
		/*	System.out.println(String
			.format("Argument/DoSomeFineTuning : End of Function. Function returning %s for Variable %s ",
					tempVariable, variable));*/
		return tempVariable;
	}


	public static String parseNoLocalVarArguments(String variable,String threadID) throws Exception{
		String tempVariable = variable;
		tempVariable = tempVariable.replace("\\%", "%");
		if (tempVariable.endsWith("##")) {
			tempVariable = Utility.TrimStartEnd(tempVariable, '#', 0);
			tempVariable = Utility.TrimStartEnd(tempVariable, '#', 1);
			tempVariable += threadID;
		}
		Log.Debug(String
				.format("Argument/parseNoLocalVarArguments : End of Function. Function returning %s for Variable %s ",
						tempVariable, variable));
		/*	System.out.println(String
		.format("Argument/DoSomeFineTuning : End of Function. Function returning %s for Variable %s ",
				tempVariable, variable));*/
		return tempVariable;

	}

	static String DoSomeFineTuning(String variable, String threadID,GTuple action,boolean parseForLVName)throws Exception{
		Log.Debug("Argument/DoSomeFineTuning : Start of Function with Variable Name as : "
				+ variable);
		if (StringUtils.isBlank(variable)) {
			Log.Debug("Argument/DoSomeFineTuning : Variable is Empty, so returning an Empty String.");
			return StringUtils.EMPTY;
		}
		List<String> contextVars=getContextVariableList(variable);

		if ( contextVars.size()>0){
			return parseContextVariables(contextVars, variable, threadID,action);
		} else {
			if(parseForLVName){
				return parseLocalVarArgumentsName(variable, threadID,action);
			}else{
				return parseNoLocalVarArguments(variable, threadID);	
			}
		}
	}

}