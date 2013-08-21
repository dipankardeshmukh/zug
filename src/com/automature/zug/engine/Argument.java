package com.automature.zug.engine;

import org.apache.commons.lang.StringUtils;

import com.automature.zug.util.Log;
import com.automature.zug.util.Utility;

public class Argument {
	
	
	
	/***
	 * Function to get the correct argument at Runtime to pass to an Action or
	 * Verification... The function will check if this is a Context Variable or
	 * not and pass the Value to the Actions accordingly.
	 * 
	 * @param argument
	 *            Name of the Argument
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
			String[] splitVariableToFind = argument.split("=", 2);//Excel.SplitOnFirstEquals(argument);
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
	static String DoSomeFineTuning(String variable, String threadID)
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
		for (char varChar : variable.toCharArray()) {
			indexer++;
			// Log.Debug(String.format("Argument/DoSomeFineTuning : Working at Indexes[%d]=%s in Variable %s.",
			// indexer, varChar, variable));
			if (varChar == '%') {
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
		}
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
				tempVariable =Controller. ReadContextVariable(tempVariable);

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

}
