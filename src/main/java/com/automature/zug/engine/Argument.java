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




	static String NormalizeVariable(String argument, String threadID)
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
					+ DoSomeFineTuning(tempValue, threadID);
		} // First Check in the Context Variable
		else {
			tempValue = DoSomeFineTuning(tempValue, threadID);
		}

		Log.Debug("Argument/NormalizeVariable : End of function with variableToFind = "
				+ argument + " and its value is -> " + tempValue);

		return tempValue;
	}

	/*static String replaceCV(String variable, String threadID)
			throws Exception {
		String value="";
		boolean isContextVar = false;
		Log.Debug("Argument/DoSomeFineTuning : Start of Function with Variable Name as : "
				+ variable);
		if (StringUtils.isBlank(variable)) {
			Log.Debug("Argument/DoSomeFineTuning : Variable is Empty, so returning an Empty String.");
			return StringUtils.EMPTY;
		}
		int firstOccuranceOfPercentage = -1;
		int secondOccuranceOfPercentage = -1;

		int indexer = -1;
		char prev_char; // previous character to check if the % was escaped
		prev_char = ' ';
		String escapedVar= "";

		for (char varChar : variable.toCharArray()) {
			indexer++;
			// Log.Debug(String.format("Argument/DoSomeFineTuning : Working at Indexes[%d]=%s in Variable %s.",
			// indexer, varChar, variable));

			// keep the indexes of escaped % signs
			if(varChar == '%' && prev_char == '\\'){
				//	escapedIndexes.add(indexer-1);
				prev_char=varChar;
				continue;
			}

			if (varChar == '%' && prev_char != '\\') {
				// Log.Debug(String.format("Argument/DoSomeFineTuning : Indexes=%d in Variable %s.",
				// indexer, variable));
				if (firstOccuranceOfPercentage >= 0) {
					secondOccuranceOfPercentage = indexer;
					break;
					// Log.Debug(String.format("Argument/DoSomeFineTuning : secondOccuranceOfPercentage=%d in Variable %s.",
					// indexer, variable));

				} else {
					firstOccuranceOfPercentage = indexer;
					// Log.Debug(String.format("Argument/DoSomeFineTuning : firstOccuranceOfPercentage=%d in Variable %s.",
					// indexer, variable));
				}
			}
			prev_char=varChar;
		}

		// replace \% signs with %
		// message("THe first Occurrance is "+firstOccuranceOfPercentage+"\n The second Ouccurance "+secondOccuranceOfPercentage);

		Log.Debug(String
				.format("Argument/DoSomeFineTuning : firstOccuranceOfPercentage=%s && secondOccuranceOfPercentage=%s.",
						firstOccuranceOfPercentage, secondOccuranceOfPercentage));

		if (firstOccuranceOfPercentage >= 0 && secondOccuranceOfPercentage >= 0) {

			String tempVariable = variable
					.substring(firstOccuranceOfPercentage,
							secondOccuranceOfPercentage + 1);
			// message("The String subs.. "+tempVariable);
			Log.Debug(String.format(
					"Argument/DoSomeFineTuning : Context Variable = ",
					tempVariable));

			// First Check in the Macro Sheet
			if (tempVariable.startsWith("%") && tempVariable.endsWith("%")) {

				tempVariable = Utility.TrimStartEnd(tempVariable, '%', 0);
				tempVariable = Utility.TrimStartEnd(tempVariable, '%', 1);

				if (tempVariable.endsWith("##")) {
					tempVariable = Utility.TrimStartEnd(tempVariable, '#', 0);
					tempVariable = Utility.TrimStartEnd(tempVariable, '#', 1);
					tempVariable += threadID;
				}

				// if (StringUtils.isNotBlank(ContextVar
				// .getContextVar(tempVariable))) {
				isContextVar = true;
				// }
				tempVariable =Controller.ReadContextVariable(tempVariable);

				Log.Debug(String
						.format("Argument/DoSomeFineTuning : After Context Variable Parsing, variableToFind = %s ",
								tempVariable));
			}

			StringBuffer actualValue = new StringBuffer(variable.substring(0,
					firstOccuranceOfPercentage)
					+ variable.substring(firstOccuranceOfPercentage,
							variable.length()));
			Log.Debug(String.format(
					"Argument/DoSomeFineTuning : actualValue = %s",
					actualValue.toString()));

			if (isContextVar) {
				actualValue.replace(firstOccuranceOfPercentage,
						secondOccuranceOfPercentage + 1, tempVariable);
			} else {
				actualValue = actualValue.insert(firstOccuranceOfPercentage,
						tempVariable);
			}

			// message("The actual value after insert "+actualValue);
			Log.Debug(String
					.format("Argument/DoSomeFineTuning : End of Function. Function returning %s for Variable %s ",
							actualValue, variable));

			value= new String(actualValue);

		} else {
			String tempVariable = variable;
			if (tempVariable.endsWith("##")) {
				tempVariable = Utility.TrimStartEnd(tempVariable, '#', 0);
				tempVariable = Utility.TrimStartEnd(tempVariable, '#', 1);
				tempVariable += threadID;
			}
			Log.Debug(String
					.format("Argument/DoSomeFineTuning : End of Function. Function returning %s for Variable %s ",
							tempVariable, variable));

			value= tempVariable;
			return value;
		}
		String str[]=value.split("(?<!\\\\)%");
		if(str.length>1){
			String tmp[]=str[1].split("(?<!\\\\)%");
			if(tmp.length>1){
				return replaceCV(value, threadID);
			}
		}
		return value;

	}
	static String DoSomeFineTuningRecursive(String variable, String threadID)
			throws Exception {
		variable=replaceCV(variable, threadID);
		variable.replaceAll("\\%", "%");
		return variable;
	}*/

	/***
	 * This is just a temporary Utility. Could not think of a good name for this
	 * Function. This function actually checks a variable and looks for if it
	 * contains any Context Variable. If it contains a Context Variable then,
	 * the function just substitutes the Value of the Context Variable.
	 * 
	 * @param variable
	 *            = Name of the Variable for which we need to do FINE
	 *            Tuning</param> <returns>A Fine tuned Variable.</returns>
	 */
	/*static String DoSomeFineTuningOld(String variable, String threadID)
			throws Exception {
		boolean isContextVar = false;
		Log.Debug("Argument/DoSomeFineTuning : Start of Function with Variable Name as : "
				+ variable);
		if (StringUtils.isBlank(variable)) {
			Log.Debug("Argument/DoSomeFineTuning : Variable is Empty, so returning an Empty String.");
			return StringUtils.EMPTY;
		}
		int firstOccuranceOfPercentage = -1;
		int secondOccuranceOfPercentage = -1;

		int indexer = -1;
		// Log.Debug("Argument/DoSomeFineTuning : Checking for the Occurance of % and their Indexes in Variable :"
		// + variable);

		char prev_char; // previous character to check if the % was escaped
		prev_char = ' ';
		String escapedVar= "";
		ArrayList<Integer> escapedIndexes = new ArrayList<Integer>();


		for (char varChar : variable.toCharArray()) {
			indexer++;
			// Log.Debug(String.format("Argument/DoSomeFineTuning : Working at Indexes[%d]=%s in Variable %s.",
			// indexer, varChar, variable));

			// keep the indexes of escaped % signs
			if(varChar == '%' && prev_char == '\\'){
				//	escapedIndexes.add(indexer-1);
				prev_char=varChar;
				continue;
			}

			if (varChar == '%' && prev_char != '\\') {
				// Log.Debug(String.format("Argument/DoSomeFineTuning : Indexes=%d in Variable %s.",
				// indexer, variable));
				if (firstOccuranceOfPercentage >= 0) {
					secondOccuranceOfPercentage = indexer;
					// Log.Debug(String.format("Argument/DoSomeFineTuning : secondOccuranceOfPercentage=%d in Variable %s.",
					// indexer, variable));
					break;
				} else {
					firstOccuranceOfPercentage = indexer;
					// Log.Debug(String.format("Argument/DoSomeFineTuning : firstOccuranceOfPercentage=%d in Variable %s.",
					// indexer, variable));
				}
			}
			prev_char=varChar;
		}

		// replace \% signs with %
		char[] charValues = variable.toCharArray();
		for(int i=0;i<variable.toCharArray().length;i++){

			if(escapedIndexes.contains(i))
				continue;
			escapedVar+=String.valueOf(charValues[i]);
		}

		if(!escapedVar.isEmpty())
			variable=escapedVar;
		// message("THe first Occurrance is "+firstOccuranceOfPercentage+"\n The second Ouccurance "+secondOccuranceOfPercentage);

		Log.Debug(String
				.format("Argument/DoSomeFineTuning : firstOccuranceOfPercentage=%s && secondOccuranceOfPercentage=%s.",
						firstOccuranceOfPercentage, secondOccuranceOfPercentage));

		if (firstOccuranceOfPercentage >= 0 && secondOccuranceOfPercentage >= 0) {

			String tempVariable = variable
					.substring(firstOccuranceOfPercentage,
							secondOccuranceOfPercentage + 1);
			// message("The String subs.. "+tempVariable);
			Log.Debug(String.format(
					"Argument/DoSomeFineTuning : Context Variable = ",
					tempVariable));

			// First Check in the Macro Sheet
			if (tempVariable.startsWith("%") && tempVariable.endsWith("%")) {

				tempVariable = Utility.TrimStartEnd(tempVariable, '%', 0);
				tempVariable = Utility.TrimStartEnd(tempVariable, '%', 1);

				if (tempVariable.endsWith("##")) {
					tempVariable = Utility.TrimStartEnd(tempVariable, '#', 0);
					tempVariable = Utility.TrimStartEnd(tempVariable, '#', 1);
					tempVariable += threadID;
				}

				// if (StringUtils.isNotBlank(ContextVar
				// .getContextVar(tempVariable))) {
				isContextVar = true;
				// }
				tempVariable =Controller.ReadContextVariable(tempVariable);

				Log.Debug(String
						.format("Argument/DoSomeFineTuning : After Context Variable Parsing, variableToFind = %s ",
								tempVariable));
			}

			StringBuffer actualValue = new StringBuffer(variable.substring(0,
					firstOccuranceOfPercentage)
					+ variable.substring(firstOccuranceOfPercentage,
							variable.length()));
			Log.Debug(String.format(
					"Argument/DoSomeFineTuning : actualValue = %s",
					actualValue.toString()));

			if (isContextVar) {
				actualValue.replace(firstOccuranceOfPercentage,
						secondOccuranceOfPercentage + 1, tempVariable);
			} else {
				actualValue = actualValue.insert(firstOccuranceOfPercentage,
						tempVariable);
			}

			// message("The actual value after insert "+actualValue);
			Log.Debug(String
					.format("Argument/DoSomeFineTuning : End of Function. Function returning %s for Variable %s ",
							actualValue, variable));

			return new String(actualValue);
		} else {
			String tempVariable = variable;
			if (tempVariable.endsWith("##")) {
				tempVariable = Utility.TrimStartEnd(tempVariable, '#', 0);
				tempVariable = Utility.TrimStartEnd(tempVariable, '#', 1);
				tempVariable += threadID;
			}
			Log.Debug(String
					.format("Argument/DoSomeFineTuning : End of Function. Function returning %s for Variable %s ",
							tempVariable, variable));

			return tempVariable;
		}
	}

*/

	static String getContextVarValues(String variable,String threadID) throws Exception{

		String tempVariable = variable;
		boolean isContextVar=false;
		// message("The String subs.. "+tempVariable);
		Log.Debug(String.format(
				"Argument/DoSomeFineTuning : Context Variable = ",
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

		tempVariable =Controller.ReadContextVariable(tempVariable);

		Log.Debug(String
				.format("Argument/DoSomeFineTuning : After Context Variable Parsing, variableToFind = %s ",
						tempVariable));
	
		return tempVariable;

		//return new String(actualValue);
	} 



	static String DoSomeFineTuning(String variable, String threadID) throws Exception
			 {
		boolean isContextVar = false;
		Log.Debug("Argument/DoSomeFineTuning : Start of Function with Variable Name as : "
				+ variable);
		if (StringUtils.isBlank(variable)) {
			Log.Debug("Argument/DoSomeFineTuning : Variable is Empty, so returning an Empty String.");
			return StringUtils.EMPTY;
		}
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
		

		String var=variable;
		StringBuffer sb=new StringBuffer();
		if (contextVars.size()>0){
			String []tmp=null;
			for(int i=0;i<contextVars.size();i++){
				tmp=var.split(contextVars.get(i), 2);
				String cv=getContextVarValues(contextVars.get(i),threadID);
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
		} else {
			String tempVariable = variable.replace("\\%", "%");
			if (tempVariable.endsWith("##")) {
				tempVariable = Utility.TrimStartEnd(tempVariable, '#', 0);
				tempVariable = Utility.TrimStartEnd(tempVariable, '#', 1);
				tempVariable += threadID;
			}
			Log.Debug(String
					.format("Argument/DoSomeFineTuning : End of Function. Function returning %s for Variable %s ",
							tempVariable, variable));

			return tempVariable;
		}
	
	
	}
}