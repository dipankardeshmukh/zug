package com.automature.zug.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.automature.zug.util.Log;
import com.automature.zug.util.Utility;

public class OutProcessAtom implements Atom{
	
	private String FindWorkingDirectory(String command,
			String workingDirectoryList) {
		Log.Debug("OutProcessAtomFindWorkingDirectory: Function started with arguments command = "
				+ command + " workingDirectoryList = " + workingDirectoryList);
		if (StringUtils.isBlank(workingDirectoryList)) {
			return workingDirectoryList;
		}
		String[] workingDirectoryArray = workingDirectoryList.split(";");
		for (String workingDirectory : workingDirectoryArray) {

			if (StringUtils.isBlank(workingDirectory)) {
				File f = new File(command);
				if (f.exists()) {
					Log.Debug(String
							.format("OutProcessAtomFindWorkingDirectory: Found with argument as %s when Working Directory value is %s",
									command, workingDirectory));
					return workingDirectory;
				}
			} else {
				File f = new File(workingDirectory + SysEnv.SLASH + command);
				if (f.exists()) {
					Log.Debug(String
							.format("OutProcessAtomFindWorkingDirectory: Found with argument as %s when Working Directory value is %s",
									command, workingDirectory));

					return workingDirectory;
				}
			}
		}
		return StringUtils.EMPTY;

	}

	/***
	 * Function to execute the Default Action/Verification
	 * 
	 * @param command
	 *            Name of the Default Action to run manually/debug mode.
	 */
	private void ExecuteDefaultCommand(String command, String parentTestCaseID)
			throws Exception {
		Log.Debug(String
				.format("OutProcessAtom/ExecuteDefaultCommand : Start of function with Action = %s and parentTestCaseID = %s ",
						command, parentTestCaseID));
		File pathOfDefaultexe;
		if (SysEnv.OS_FLAG) {
			pathOfDefaultexe = new File("./DefaultAction.exe");
		} else {
			pathOfDefaultexe = new File("./DefaultAction");
		}
		ProcessBuilder pr = new ProcessBuilder();
		pr.directory(pathOfDefaultexe.getParentFile());

		ArrayList<String> CommandParam = new ArrayList<String>();

		Process process = null;
		try {
			CommandParam.add("ExeWrapper.exe");
			CommandParam.add(command);
			CommandParam.add(parentTestCaseID);

			pr.command(CommandParam);
			process = pr.start();

			Log.Debug(String
					.format("OutProcessAtom/ExecuteDefaultCommand  :Arguments for the Command DefaultAction.exe is %s. ",
							command));

			Log.Debug(String
					.format("OutProcessAtom/ExecuteDefaultCommand :Started the Process for the Command DefaultAction.exe with Arguments as %s . ",
							command));

			Log.Debug(String
					.format("OutProcessAtom/ExecuteDefaultCommand  :Waiting for the Process to Exit for the Command DefaultAction.exe with Arguments as %s . ",
							command));

			process.waitFor();

			Log.Debug(String
					.format("OutProcessAtom/ExecuteDefaultCommand :Process Exited Gracefully for the Command DefaultAction.exe with Arguments as %s . ",
							command));

			if (!(process.exitValue() == 0)) {
			
				throw new Exception(
						String.format(
								"OutProcessAtom/ExecuteDefaultCommand : Exit Status for DefaultAction.exe Command is %s ",
								process.exitValue()));
			}
		} catch (Exception ex) {
			String error = String
					.format("OutProcessAtom/ExecuteDefaultCommand : Exception raised while running Command DefaultAction.exe. Exception Message is : %s ",
							ex.getMessage() + ex.getCause()
									+ ex.getStackTrace() + ex.toString());
			throw new Exception(error);
		} finally {
			// Close the process created.
			if (process != null) {
				try {
					// No exception means that the process has exited -
					// otherwise kill it
					process.exitValue();
				} catch (Exception e) {
					Log.Debug("OutProcessAtom/ExecuteDefaultCommand  : Closing and Disposing the Process.");
					process.destroy();
					Log.Debug("OutProcessAtom/ExecuteDefaultCommand : Successfully Closed and Disposed the Process.");
				}
			}
		}
		Log.Debug(String
				.format("OutProcessAtom/ExecuteDefaultCommand : End of function with command = DefaultAction.exe, Arguments = %s and parentTestCaseID = %s ",
						command, parentTestCaseID));

	}

	private void ExecuteCommand(String command, ArrayList<String> argument,
			String workingDirectoryList, UserData data,
			String parentTestCaseId, String step) throws Exception {
		Log.Debug(String
				.format("OutProcessAtom/ExecuteCommand : Start of function with command = %s, Arguments = %s and Working Directory = %s & parentTestCaseID = %s ",
						command, argument.toString(), workingDirectoryList,
						parentTestCaseId));

		String actualCommand = command;
		Log.Debug("OutProcessAtom/ExecuteCommand: Calling Controller/FindWorkingDirectory()");
		String workingDirectory = FindWorkingDirectory(command,
				workingDirectoryList);

		if (Controller.opts.debugMode == true) {

			if (StringUtils.isBlank(workingDirectory)) {
				File f = new File(command);
				if (!f.exists()) {
					Log.Debug(String
							.format("OutProcessAtom/ExecuteCommand : Calling ExecuteDefaultCommand with argument as %s when Working Directory is Empty",
									command));
					ExecuteDefaultCommand(command, parentTestCaseId);
					Log.Debug(String
							.format("OutProcessAtom/ExecuteCommand : End of function with command = %s, Arguments = %s and Working Directory = %s ",
									command, argument.toString(), workingDirectory));
					return;
				}
			} else {
				File f = new File(workingDirectory + SysEnv.SLASH + command);// Changed
				if (!f.exists()) {

					Log.Debug(String
							.format("OutProcessAtom/ExecuteCommand : Calling ExecuteDefaultCommand with argument as %s when Working Directory value is %s",
									command, workingDirectory));
					ExecuteDefaultCommand(command, parentTestCaseId);
					Log.Debug(String
							.format("OutProcessAtom/ExecuteCommand : End of function with command = %s, Arguments = %s and Working Directory = %s ",
									command, argument.toString(), workingDirectory));
					return;
				}

			}
		}
		if (SysEnv.OS_FLAG) {
			workingDirectory = Utility.TrimStartEnd(workingDirectory, '/', 1);
			workingDirectory = Utility.TrimStartEnd(workingDirectory, '\\', 1);
			actualCommand = new File(workingDirectory, command)
					.getCanonicalPath();// Removed the method getCanonical Path
		} else {
			actualCommand = new File(workingDirectory, command).getPath();
		}
		// Part changed by Suddha and Modified by Sankho
		// In this part it is coming with .class
		String FileName = "";
		boolean Flags = false;
		if (actualCommand.endsWith("class") || actualCommand.endsWith("CLASS")) {
			// Here .class is removed.

			Flags = true;
			String Path[] = actualCommand.split("\\\\");
			for (String name : Path) {

				if (name.endsWith(".class") || name.endsWith(".CLASS")) {
					FileName = name.replaceAll(".class", "").trim();
				} else {
					// FileName="C:\\Program Files\\Automature\\ZUG\\DefaultAction.exe";
					continue;

				}
			}

		} else {
			FileName = actualCommand;
		}

		ProcessBuilder pr = new ProcessBuilder();
		String commandValue[];

	
		Process process = null;
		ArrayList<String> commandparam = new ArrayList<String>(); // ArrayList
		
		int exitValue;
		// Add command(file) to be execute into Arraylist.

		if (command.contains(".")) {
			Log.Debug("OutProcessAtom/ExecuteCommand :: Looking for commands in interpreter Lists");
			String[] commandNameExtension = command.split("\\.");
			if (Controller.fileExtensionSupport
					.containsKey(commandNameExtension[1].toLowerCase())) {
				String[] commandList = Controller.fileExtensionSupport
						.get(commandNameExtension[1]);
				for (String cmd : commandList) {
					Log.Debug("OutProcessAtom/ExecuteCommand: Adding Command parameters - "
							+ cmd);
					if (!cmd.isEmpty())// Chekcing for Null Inclution
					{
						commandparam.add(cmd.trim());
					} else {
						// System.out.println("Empty Value");
					}

				}
			}
		}

	
		commandparam.add(FileName.trim());

		// Add the Command param to arrayList

	//	commandparam.add(FileName.trim());
		int size=argument.size();
		if(size>0){
			String tmp=argument.get(size-1);
			tmp=fineTuneArgument(actualCommand,tmp);
			argument.remove(size-1);
			argument.add(tmp);
		}
		commandparam.addAll(argument);

		try {
			pr.command(commandparam);
			if (StringUtils.isNotBlank(workingDirectory)) {
				pr.directory(new File(workingDirectory));
				Log.Debug(String
						.format("OutProcessAtom/ExecuteCommand  :workingDirectory for the Command %s is %s . ",
								command, workingDirectory));
			}

			Log.Debug(String
					.format("OutProcessAtom/ExecuteCommand  :Arguments for the Command %s is %s . ",
							command, argument.toString()));

			Log.Debug(String
					.format("OutProcessAtom/ExecuteCommand :Started the Process for the Command %s with Arguments as %s . ",
							command, argument.toString()));
			process = pr.start();

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					process.getErrorStream()));

			// read the output from the command
			String primitiveStreams = null;
			String errorPrimitiveMessage = StringUtils.EMPTY;

			// The standard output of the command
			while ((primitiveStreams = stdInput.readLine()) != null) {
				Log.Debug("OutProcessAtom/ExecuteCommand : [AtomLog/" + FileName
						+ "] - " + primitiveStreams);
				// System.out.println("OUTPUT->\t"+primitiveStreams);
			}

			// read any errors from the attempted command

			// The standard error of the command

			while ((primitiveStreams = stdError.readLine()) != null) {
				Log.Debug("OutProcessAtom/ExecuteCommand : [AtomLog/" + FileName
						+ "] - " + primitiveStreams);
				Log.Error("[PrimitiveLog/" + FileName + "] - "
						+ primitiveStreams);
				errorPrimitiveMessage += primitiveStreams;
			}
			Log.Debug(String
					.format("OutProcessAtom/ExecuteCommand  :Waiting for the Process to Exit for the Command %s with Arguments as %s . ",
							command, argument.toString()));

			if (Controller.opts.debugMode
					|| (Controller.opts.doCleanupOnTimeout && step.endsWith("c"))) {
				exitValue = process.waitFor();
			} else {
				while (true) {
					if (!Controller.opts.debugMode) {
						if (TestSuite._testPlanStopper
								|| ((Boolean) TestSuite._testStepStopper
										.get(parentTestCaseId))) {
							// kill the process....
							try {
								process.destroy();
								process = null;
							} catch (Exception e) {
								Log.Error(String
										.format("OutProcessAtom/ExecuteCommand : Exception occured while killing the process"));
							}

							if (TestSuite._testPlanStopper) {
								// Log.Error(String.format("OutProcessAtomExecuteCommand : Command %s took longer time to execute. Test Plan Time Specified = %s seconds  is over.",
								// command,
								// ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")));
								throw new Exception(
										String.format(
												"OutProcessAtom/ExecuteCommand : Command %s took longer time to execute. Test Plan Time Specified = %s seconds  is over.",
												command,
												Controller
														.ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")));
							}

							if (((Boolean) TestSuite._testStepStopper
									.get(parentTestCaseId))) {
								// Log.Error(String.format("OutProcessAtomExecuteCommand : Command %s took longer time to execute. Test Step Time Specified = %s seconds  is over.",
								// command,
								// ReadContextVariable("ZUG_TESTSTEP_TIMEOUT")));
								throw new Exception(
										String.format(
												"OutProcessAtom/ExecuteCommand : Command %s took longer time to execute. Test Step Time Specified = %s seconds  is over.",
												command,
												Controller
														.ReadContextVariable("ZUG_TESTSTEP_TIMEOUT")));
							}
						}
					}
					// Check process has exited
					try {
						exitValue = process.exitValue();
						break;
					} catch (IllegalThreadStateException ex) {
						Log.Debug(String
								.format("OutProcessAtom/ExecuteCommand : Process with name : %s \n is still RUNNING.  IllegalThreadState Exception. Message is %s ",
										FileName.toString(), ex.getMessage()));
						Log.Error(String
								.format("OutProcessAtom/ExecuteCommand : Process with name : %s \n is still RUNNING.  IllegalThreadState Exception. Message is %s ",
										FileName.toString(), ex.getMessage()));
					} catch (Exception e) {
						Log.Debug(String
								.format("OutProcessAtom/ExecuteCommand : Process with name : %s \n is still RUNNING.  Unknown Exception. Message is %s ",
										FileName.toString(), e.getMessage()));
						Log.Error(String
								.format("OutProcessAtom/ExecuteCommand : Process with name : %s \n is still RUNNING.  Unknown Exception. Message is %s ",
										FileName.toString(), e.getMessage()));
					}
					// Wait for some time interval..
					Thread.sleep(20);
				}
			}

			String errorMessage = ""; // TODO
			// ContextVar.getContextVar("ErrorMessage_"
			// + process
			// ProcessMonitorThread.currentThread().getId());
			//ContextVar.Delete("ErrorMessage_" + Thread.currentThread().getId());

			if (StringUtils.isNotBlank(errorMessage)) {
				// Log.Error(String.format("OutProcessAtomExecuteCommand : Errors for %s Command is %s",
				// command, errorMessage));
				throw new Exception(
						String.format(
								"OutProcessAtom/ExecuteCommand : Errors for %s Command is %s",
								command, errorMessage));
			}

			Log.Debug(String
					.format("OutProcessAtom/ExecuteCommand :Process Exited Gracefully for the Command %s with Arguments as %s . ",
							command, argument.toString()));

			if (!(process.exitValue() == 0)) {
				// Log.Error(String.format("OutProcessAtomExecuteCommand : Exit Status for %s Command is %s",
				// command, process.exitValue()));
				throw new Exception(String.format(
						"Exit Status for %s Command is %s\n%s", command,
						process.exitValue(), errorPrimitiveMessage));
			}
		} catch (Exception ex) {
			String error = String
					.format("Exception in Command %s.\n\tException Message is :\n\tFoot Print:\n %s",
							command, ex.getMessage());
			// Log.Error(error);

			throw new Exception(error);
		} finally {
			// Close the process created.
			if (process != null) {
				try {
					// This will throw an exception when the process has not
					// exited.
					process.exitValue();

				} catch (Exception e) {

					Log.Debug("OutProcessAtom/ExecuteCommand  : Closing and Disposing the Process.");
					process.destroy();
					process = null;
					Log.Debug("OutProcessAtom/ExecuteCommand : Successfully Closed and Disposed the Process.");

				}
			}
		}
		Log.Debug(String
				.format("OutProcessAtom/ExecuteCommand : End of function with command = %s, Arguments = %s and Working Directory = %s ",
						command, argument.toString(), workingDirectory));
	}
	
	private String fineTuneArgument(String fileName,String argument){
		if(fileName.contains(".bat")){
			//	System.out.println("Its a bat file");
			if(argument.contains(" ")){
				argument=argument+"\"";
			}
		}
		return argument;
	}

	/***
	 * Function to execute an Action of a Test Case
	 * 
	 * @param action
	 *            Action Object to Execute
	 * @param user
	 *            User to perform/execute this action.
	 */
	public void run(GTuple action, String threadID) throws Exception {
		String type = "";
		String step = "";
		HiPerfTimer hft=null;
		try {
			if (action instanceof Action) {
				type = "Action";
				Action act = (Action) action;
				step = act.step;
			} else {
				type = "Verification";
			}
			Log.Debug(String
					.format("OutProcessAtom/run : Start of function with command = %s ",
							action.name));

			Log.Debug(String
					.format("OutProcessAtom/run : Before trimming command = %s with @ ",
							action.name));
			String command = Utility.TrimStartEnd(action.name, '@', 1);
			Log.Debug(String
					.format("OutProcessAtom/run : After trimming command = %s with @ ",
							command));

			StringBuilder arguments = new StringBuilder();
			UserData user = action.userObj;
			Log.Debug("OutProcessAtom/run : Number of Arguments are : "
					+ action.arguments.size());
			ArrayList<String> arg=new ArrayList<String>();
			for (int i = 0; i < action.arguments.size(); ++i) {
				Log.Debug(String
						.format("OutProcessAtom/run : Working on Arguments[%d] = %s",
								i, action.arguments.get(i).toString()));
				arguments.append("\""
						+ Argument.NormalizeVariable(action.arguments.get(i)
								.toString(), threadID) + "\"");
				arg.add(Argument.NormalizeVariable(action.arguments.get(i)
						.toString(), threadID));
				arguments.append(" ");
			}
			Log.Debug(String
					.format("OutProcessAtom/run : Calling ExecuteCommand with Command as %s ,  arguments =%s and parentTestCaseID = %s",
							command, arguments.toString().trim(),
							action.parentTestCaseID));
			if(Controller.opts.showTime){
				hft=new HiPerfTimer();
				hft.Start();
			}	
			if (StringUtils.isBlank(arguments.toString())) {
				Controller
						.message(String
								.format("\n[%s] "+type+" %s Execution STARTED With NO Arguments  ",
										action.stackTrace.toUpperCase(),
										action.name.toUpperCase()));
			} else {
				Controller
						.message(String
								.format("\n[%s] "+type+" %s Execution STARTED With Arguments %s ",
										action.stackTrace.toUpperCase(),
										action.name.toUpperCase(),
										arg));
			}

			String protoTypeName = Excel.AppendNamespace(command,
					action.nameSpace);

			if (TestSuite.prototypeHashTable.get(protoTypeName) != null) {
				// check Action is In-Process primitive.
				Prototype prototype = (Prototype) TestSuite.prototypeHashTable
						.get(protoTypeName);

				if (StringUtils.isNotBlank(prototype.InProcessDllName)) {
				
				} else {
					// Action is out-of-process.
					if (type.equalsIgnoreCase("action")) {
						Action act = (Action) action;
						ExecuteCommand(command, arg,
								Controller.opts.scriptLocation, user,
								action.parentTestCaseID, act.step);
					}

				}
			} else {
				// To support old input sheet where Prototype sheet is not
				// define. So all primitive are out-of process.
				ExecuteCommand(command, arg,
						Controller.opts.scriptLocation, user,
						action.parentTestCaseID, step);
			}

			if(Controller.opts.showTime){
				hft.Stop();
				if(Controller.atomPerformance.containsKey(action.name.toLowerCase())){
					List l=Controller.atomPerformance.get(action.name.toLowerCase());
					l.add(hft.Duration());
					Controller.atomPerformance.put(action.name.toLowerCase(),l);
				}else{
					ArrayList al=new ArrayList();
					al.add(hft.Duration());
					Controller.atomPerformance.put(action.name.toLowerCase(),al);
				}
				Controller
				.message(String.format(
						"\n[%s] "+type+" %s SUCCESSFULLY Executed in %s milli sec.",
						action.stackTrace.toUpperCase(),
						action.name.toUpperCase(),hft.Duration()));
			}else{
			Controller
					.message(String.format(
							"\n[%s] Action %s SUCCESSFULLY Executed",
							action.stackTrace.toUpperCase(),
							action.name.toUpperCase()));
			}
			Log.Debug(String
					.format("OutProcessAtom/run : Successfully Executed ExecuteCommand with Command as %s and  arguments =%s ",
							command, arguments.toString()));

			Log.Debug(String
					.format("OutProcessAtom/run : End of function with command = %s ",
							action.name));
	

		} catch (Exception ex) {
			throw ex;
	
		
		}finally{
			if(hft!=null){
				hft.Stop();
			}
		}
	}
}
