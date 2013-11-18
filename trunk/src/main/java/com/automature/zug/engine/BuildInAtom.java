package com.automature.zug.engine;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.automature.zug.util.Log;

public class BuildInAtom implements Atom {

	/**
	 * @param args
	 */
	public static ArrayList<String> buildIns=new ArrayList<String>();
	
	static{
		buildIns.add("setcontextvar");
		buildIns.add("unsetcontextvar");
		buildIns.add("appendtocontextvar");
		buildIns.add("print");
		buildIns.add("GetValueAtIndex".toLowerCase());
		buildIns.add("GetCurrentIndex".toLowerCase());
		buildIns.add("wait");
		buildIns.add("GetValueAt".toLowerCase());
		buildIns.add("#define_args");
		buildIns.add("#define_arg");
		buildIns.add("#define");
	}
	

	public void run(GTuple action, String threadID) throws Exception {

		// EngUtil util=new EngUtil();
		String typeName = null;
		if (action instanceof Action) {
			typeName = "Action";
		} else {
			typeName = "Verification";
		}
		if (action.name.trim().compareToIgnoreCase("setcontextvar") == 0) {
			if (action.arguments.size() == 1) {
				try {
					String arg = Argument.NormalizeVariable(
							(String) action.arguments.get(0), threadID);

					Controller.message(String.format("\n[%s] " + typeName
							+ " %s Execution STARTED With Arguments [%s]",
							action.stackTrace.toUpperCase(),
							action.name.toUpperCase(), arg));
					Controller.CreateContextVariable(arg);

					Controller.message(String.format("\n[%s] " + typeName
							+ " %s SUCCESSFULLY Executed",
							action.stackTrace.toUpperCase(),
							action.name.toUpperCase()));

				} catch (Exception ex) {
					Controller.message("Error:" + ex.getMessage());
					throw ex;
				}
			} else if (action.arguments.size() > 1) {

				try {
					StringBuilder executionlist = new StringBuilder();

					executionlist.append("[");
					int cnt = 0;
					for (String argument : action.arguments) {
						cnt++;
						String ctx_arg = Argument.NormalizeVariable(
								(String) argument, threadID);
						executionlist.append(ctx_arg);
						if (cnt == action.arguments.size()) {
							break;
						} else {
							executionlist.append(",");
						}

					}
					executionlist.append("]");
					Controller
					.message(String
							.format("\n[%s] "
									+ typeName
									+ " %s Execution STARTED With Arguments %s",
									action.stackTrace.toUpperCase(),
									action.name.toUpperCase(),
									executionlist.toString()));
					for (String argument : action.arguments) {
						String ctx_arg = Argument.NormalizeVariable(
								(String) argument, threadID);

						Controller.CreateContextVariable(ctx_arg);

					}
					Controller.message(String.format("\n[%s] " + typeName
							+ " %s SUCCESSFULLY Executed",
							action.stackTrace.toUpperCase(),
							action.name.toUpperCase()));
				} catch (Exception ex) {
					Controller.message("Error:" + ex.getMessage());
					throw ex;
				}
			} else {
				Controller.message(String.format("\n[%s] " + typeName
						+ " %s Executed With Arguments []",
						action.stackTrace.toUpperCase(),
						action.name.toUpperCase()));
			}

		} else if (action.name.trim().compareToIgnoreCase("unsetcontextvar") == 0) {
			if (action.arguments.size() == 1) {
				try {
					String arg = Argument.NormalizeVariable(
							(String) action.arguments.get(0), threadID);
					Controller.message(String.format("\n[%s] " + typeName
							+ " %s Execution STARTED With Arguments %s",
							action.stackTrace.toUpperCase(),
							action.name.toUpperCase(), arg));
					Controller.DestroyContextVariable(arg);
					Controller.message(String.format("\n[%s] " + typeName
							+ " %s SUCCESSFULLY Executed",
							action.stackTrace.toUpperCase(),
							action.name.toUpperCase()));

				} catch (Exception ex) {
					Controller.message("Error:" + ex.getMessage());
					throw ex;
				}
			} else if (action.arguments.size() > 1) {

				try {
					StringBuilder executionlist = new StringBuilder();

					executionlist.append("[");
					int cnt = 0;
					for (String argument : action.arguments) {
						cnt++;
						String ctx_arg = Argument.NormalizeVariable(
								(String) argument, threadID);
						executionlist.append(ctx_arg);
						if (cnt == action.arguments.size()) {
							break;
						} else {
							executionlist.append(",");
						}

					}
					executionlist.append("]");
					Controller
					.message(String
							.format("\n[%s] "
									+ typeName
									+ " %s Execution STARTED With Arguments %s",
									action.stackTrace.toUpperCase(),
									action.name.toUpperCase(),
									executionlist.toString()));
					for (String argument : action.arguments) {
						String ctx_arg = Argument.NormalizeVariable(
								(String) argument, threadID);

						Controller.DestroyContextVariable(ctx_arg);
					}
					Controller.message(String.format("\n[%s] " + typeName
							+ " %s SUCCESSFULLY Executed",
							action.stackTrace.toUpperCase(),
							action.name.toUpperCase()));
				} catch (Exception ex) {
					Controller.message("Error:" + ex.getMessage());
					throw ex;

				}
			} else {
				Controller.message(String.format("\n[%s] " + typeName
						+ " %s SUCCESSFULLY Executed",
						action.stackTrace.toUpperCase(),
						action.name.toUpperCase()));
			}
		} else if (action.name.trim().compareToIgnoreCase("appendtocontextvar") == 0) {

			if (action.arguments.size() >= 2) {
				try {
					String contextVarName = StringUtils.EMPTY;
					StringBuilder appendValueBuilder = new StringBuilder();
					if (action.actualArguments.get(0).toLowerCase()
							.startsWith("contextvar=")) {
						for (int i = 0; i < action.arguments.size(); ++i) {
							String real_value[] = Excel
									.SplitOnFirstEquals(action.arguments.get(i));
							String actua_value[] = Excel
									.SplitOnFirstEquals(action.actualArguments
											.get(i));
							String valueToWorkWith = action.arguments.get(i);
							if (!real_value[0].equalsIgnoreCase(actua_value[0])) {
								valueToWorkWith = actua_value[0] + "="
										+ valueToWorkWith;
							}
							// message("The value edited "+valueToWorkWith);
							String opt = Argument.NormalizeVariable(
									(String) valueToWorkWith, threadID);

							int idx = opt.indexOf('=');
							// message("the index is "+idx);
							if (idx == -1) {
								continue;
							} else {
								// message("the opt string "+opt.substring(0,idx));
								if (opt.substring(0, idx).toLowerCase()
										.compareToIgnoreCase("contextvar") == 0) {
									contextVarName = opt.substring(idx + 1);
								} else {
									appendValueBuilder.append(opt
											.substring(idx + 1));

								}
							}
						}
					} else {
						contextVarName = Argument.NormalizeVariable(
								action.arguments.get(0), threadID);
						for (int i = 1; i < action.arguments.size(); ++i) {
							String arg_value = Argument.NormalizeVariable(
									action.arguments.get(i), threadID);

							appendValueBuilder.append(arg_value);
						}
					}
					Controller.message(String.format("\n[%s] " + typeName
							+ " %s Execution STARTED With Arguments "
							+ "contextvar = %s valueToAppend = %s",
							action.stackTrace.toUpperCase(),
							action.name.toUpperCase(), contextVarName,
							appendValueBuilder.toString()));
					String value = ContextVar.getContextVar(contextVarName
							.trim());
					if (value == null) {
						Log.Error("AppendtoContextVar: ContextVariable is not defined");
						throw new Exception("Context Variable not defined");
					}
					ContextVar.alterContextVar(contextVarName, value
							+ appendValueBuilder.toString());

					Controller.message(String.format("\n[%s] " + typeName
							+ " %s SUCCESSFULLY Executed",
							action.stackTrace.toUpperCase(),
							action.name.toUpperCase()));

				} catch (Exception e) {
					Controller.message("Error:" + e.getMessage());
					throw e;
				}
			} else {
				Controller.message(String.format("\n[%s] " + typeName
						+ " %s Executed With less than two Arguments",
						action.stackTrace.toUpperCase(),
						action.name.toUpperCase()));
			}
		} else if (action.name.trim().toLowerCase().contains("print")) {
			if (action.arguments.size() > 0) {

				for (int i = 0; i < action.arguments.size(); i++) {

					if (!action.arguments.get(i).isEmpty()) {

						if (action.arguments.get(i).startsWith("$$%")
								&& action.arguments.get(i).endsWith("%")) {
							// message("The values are "+action.actionArguments.get(i));
							String action_args = StringUtils.removeStart(
									action.arguments.get(i), "$$");
							action.arguments.set(i, Argument.NormalizeVariable(
									action_args, threadID));
						} else {
							action.arguments.set(
									i,
									Argument.NormalizeVariable(
											action.arguments.get(i), threadID));
						}

					}
				}

				Controller.message("\n\n"+String.format("[%s] " + typeName
						+ " %s Execution STARTED With Arguments %s ",
						action.stackTrace.toUpperCase(), action.name.toUpperCase(),
						action.arguments));
				// RunVerification(action, threadID);
			} else {
				Controller.message("\n\n"+String.format("[%s] " + typeName
						+ " %s Execution STARTED With Arguments %s ",
						action.stackTrace.toUpperCase(), action.name.toUpperCase(),
						action.arguments));
			}
		} else if (action.name.trim().equalsIgnoreCase("GetValueAtIndex")) {
			if (action.arguments.size() == 3) {
				try {
					String arg1 = Argument.NormalizeVariable(
							(String) action.arguments.get(0), threadID);
					String arg2 = Argument.NormalizeVariable(
							(String) action.arguments.get(1), threadID);
					String arg3 = Argument.NormalizeVariable(
							(String) action.arguments.get(2), threadID);
					Controller.message(String.format("\n[%s] " + typeName
							+ " %s Execution STARTED With Arguments %s",
							action.stackTrace.toUpperCase(),
							action.name.toUpperCase(), action.arguments));

					String args_list[] = arg1.split(",");

					if (args_list.length >= Integer.valueOf(arg2)) {
						// message(args_list[Integer.valueOf(arg2)].replace("{","").replace("}",""));
						Controller.CreateContextVariable(arg3
								+ "="
								+ args_list[Integer.valueOf(arg2) - 1].replace(
										"{", "").replace("}", ""));
						Controller.message(String.format("\n[%s] " + typeName
								+ " %s SUCCESSFULLY Executed",
								action.stackTrace.toUpperCase(),
								action.name.toUpperCase()));
					} else {
						throw new Exception(
								"\n\tIndex is greater than MVM argument length ");
					}

				} catch (Exception ex) {
					Controller.message("Error:" + ex.getMessage());
					throw ex;
				}
			} else {
				Controller
				.message("Error:"
						+ String.format(
								"\n\t %s Number of argument mismatch. Excpected argument length 3",
								action.name));
				throw new Exception(
						String.format(
								"\n\t %s Number of argument mismatch. Excpected argument length 3",
								action.name));
			}

		} else if (action.name.trim().equalsIgnoreCase("GetCurrentIndex")) {
			if (action.arguments.size() == 3) {
				try {
					String arg1 = Argument.NormalizeVariable(
							(String) action.arguments.get(0), threadID);
					String arg2 = Argument.NormalizeVariable(
							(String) action.arguments.get(1), threadID);
					String arg3 = Argument.NormalizeVariable(
							(String) action.arguments.get(2), threadID);
					Controller.message(String.format("\n[%s] " + typeName
							+ " %s Execution STARTED With Arguments %s",
							action.stackTrace.toUpperCase(),
							action.name.toUpperCase(), action.arguments));

					String args_list[] = arg1.split(",");
					boolean element_present = false;
					int count_index = 1;
					for (String arg : args_list) {
						arg = arg.replace("{", "").replace("}", "");
						if (arg.equalsIgnoreCase(arg2)) {
							// message("The count Index "+count_index);
							element_present = true;
							break;
						} else {
							element_present = false;
							count_index++;
						}

					}
					if (!element_present) {
						throw new Exception("\n\t" + arg2
								+ " Element is not present is given list "
								+ arg1);
					}
					// message(args_list[Integer.valueOf(arg2)].replace("{","").replace("}",""));
					Controller.CreateContextVariable(arg3 + "="
							+ new Integer(count_index).toString());
					Controller.message(String.format("\n[%s] " + typeName
							+ " %s SUCCESSFULLY Executed",
							action.stackTrace.toUpperCase(),
							action.name.toUpperCase()));

				} catch (Exception ex) {
					Controller.message("Error:" + ex.getMessage());
					throw ex;

				}
			} else {
				Controller
				.message("Error: "
						+ String.format(
								"\n\t %s Number of argument mismatch. Excpected argument length 3",
								action.name));
				throw new Exception(
						String.format(
								"\n\t %s Number of argument mismatch. Excpected argument length 3",
								action.name));
			}

		} else if (action.name.trim().equalsIgnoreCase("GetValueAt")) {
			if (action.arguments.size() == 4) {
				try {
					String arg1 = Argument.NormalizeVariable(
							(String) action.arguments.get(0), threadID);
					String arg2 = Argument.NormalizeVariable(
							(String) action.arguments.get(1), threadID);
					String arg3 = Argument.NormalizeVariable(
							(String) action.arguments.get(2), threadID);
					String arg4 = Argument.NormalizeVariable(
							(String) action.arguments.get(3), threadID);
					Controller.message(String.format("\n[%s] " + typeName
							+ " %s Execution STARTED With Arguments %s",
							action.stackTrace.toUpperCase(),
							action.name.toUpperCase(), action.arguments));

					String args1_list[] = arg1.split(",");
					String args2_list[] = arg2.split(",");
					if (args1_list.length == args2_list.length) {
						int count_source_index = 0;
						boolean element_found = false;
						for (String arg : args2_list) {
							arg = arg.replace("{", "").replace("}", "");
							if (arg.equalsIgnoreCase(arg3)) {
								element_found = true;
								break;
							} else {
								element_found = false;
								count_source_index++;
							}
						}
						if (!element_found) {
							throw new Exception(
									"\n\t"
											+ arg3
											+ " Element is not present is given Source list "
											+ arg2);
						}
						Controller.CreateContextVariable(arg4
								+ "="
								+ args1_list[count_source_index].replace("{",
										"").replace("}", ""));
						Controller.message(String.format("\n[%s] " + typeName
								+ " %s SUCCESSFULLY Executed",
								action.stackTrace.toUpperCase(),
								action.name.toUpperCase()));

					} else {
						throw new Exception(
								"\n\tTarget list and Source list are not similar.");
					}

				} catch (Exception ex) {
					Controller.message("Error:" + ex.getMessage());
					throw ex;
				}
			} else {
				Controller
				.message("Error: "
						+ String.format(
								"\n\t %s Number of argument mismatch. Excpected argument length 4",
								action.name));
				throw new Exception(
						String.format(
								"\n\t %s Number of argument mismatch. Excpected argument length 4",
								action.name));
			}

		} else if (action.name.trim().equalsIgnoreCase("wait")) {
			int arg_size = action.arguments.size();

			if (arg_size == 1) {
				Controller.message(String.format("\n[%s] " + typeName
						+ " %s Execution STARTED With Arguments " + "%s",
						action.stackTrace.toUpperCase(),
						action.name.toUpperCase(), action.arguments));
				try {
					long timetowait = Long.valueOf(Argument.NormalizeVariable(
							action.arguments.get(0), threadID)) * 1000;
					Thread.sleep(timetowait);
					// message("Sleeping done");
					// action.wait(timetowait);
					Controller.message(String.format("\n[%s] " + typeName
							+ " %s SUCCESSFULLY Executed",
							action.stackTrace.toUpperCase(),
							action.name.toUpperCase()));
				} catch (Exception e) {
					Controller.message("Error:" + e.getMessage());
					throw e;
				}
			} else {
				Controller.message("Error :Illegal number of arguments(Size): "
						+ arg_size + "\n Use only 1 argument");
				throw new Exception("Illegal number of arguments(Size): "
						+ arg_size + "\n Use only 1 argument");
			}

		} else {
			throw new Exception(" Unrecognized " + typeName + ":["
					+ action.name + "] for TestCase ID # " + action.testCaseID);

		}
	}

}
