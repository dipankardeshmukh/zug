package com.automature.zug.engine;

import java.io.File;
import java.math.MathContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
//Internal package imports
import com.automature.zug.util.Log;

public class ContextVar {

	private static final String _dbName = "sqlite-"+ Controller.harnessPIDValue+".db3";
	private static final String _tableName = "ContextVariable";
	private static final int LOOP_NUM=5;

	// private static final String _dbPath = System.getenv("APPDATA") +
	// "/Biel Logs";

	private static final String _dbPath = com.automature.zug.engine.Controller.LOGLOCATION
			+ com.automature.zug.engine.Controller.SLASH + "ZUG Logs";
	// private static String justForLock = "";
	private static Integer justForLock = 0;

	private static final ContextVar INSTANCE = new ContextVar();

	public static String Quotedstring(String src) {
		String quotedStr = "'" + src.replace("'", "''") + "'";
		return quotedStr;
	}

	private ContextVar() {
		Log.Debug("ContextVar: Start of Static Constructor");
		Connection conn = null;
		try {
			Log.Debug("ContextVar: Checking if Directory Exists");

			File dir = new File(_dbPath);
			if (!dir.exists()) {
				Log.Debug("ContextVar: Creating Directory : " + _dbPath);
				// Create one directory
				boolean success = (new File(_dbPath)).mkdir();
				if (!success) {
					Log.Error("ContextVar: Not able to create Directory: "
							+ _dbPath);
				}
			}

			Class.forName("org.sqlite.JDBC");

			conn = DriverManager.getConnection("jdbc:sqlite:" + _dbPath + "/"
					+ _dbName);

			Statement stat = conn.createStatement();
			stat.executeUpdate("CREATE TABLE If Not Exists  " + _tableName
					+ "  ( [ProcessId] TEXT, [Name] TEXT, "
					+ "[Value] TEXT, PRIMARY KEY ([ProcessId],[Name]))");
			conn.close();
		} catch (Exception ex) {
			Log.Error("ContextVar: Exception is : " + ex.getMessage()
					+ ex.getStackTrace());
		} finally {
			Log.Debug("ContextVar: Connection is getting closed ");
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
				}
			Log.Debug("ContextVar: Connection is closed. End of Function ");
		}
	}

	/***
	 * Set context variable if not exist else update the value.
	 * 
	 * @param name
	 *            Context Variable Name.
	 * @param value
	 *            Context Variable Value.
	 */
	public static void setContextVar(String name, String value)
			throws Exception {

		Log.Debug("SetContextVar: Start of Function with with name as : "
				+ name + " and Value as = " + value);
		Connection conn = null;
		try {
			// Log.Debug("SetContextVar: Calling GetContextVar with name = " +
			// name);
			if (getContextVar(name) != null) {
				// Log.Debug("SetContextVar: Calling AlterContextVar with name = "
				// + name + " and value as : " + value);
				// Update context variable
				alterContextVar(name, value);
				// Log.Debug("SetContextVar: Successfully Executed AlterContextVar with name = "
				// + name + " and value as : " + value);
				return;
			}

			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + _dbPath + "/"
					+ _dbName);

			// Log.Debug("SetContextVar: Connection Opened .");

			int parentPId = (int) Controller.harnessPIDValue;
			// Log.Debug("SetContextVar: Parent ID = " + parentPId);

			Statement stat = conn.createStatement();
			Log.Debug("SetContextVar: Firing Command = " + "Insert Into  "
					+ _tableName + "  (ProcessId, Name, Value) values ("
					+ Quotedstring(Integer.toString(parentPId)) + ", "
					+ Quotedstring(name) + ", " + Quotedstring(value) + ")");
			int numtoloop=LOOP_NUM;
			while (numtoloop>0) {
				try {
					synchronized (justForLock) {
						stat.executeUpdate("Insert Into  " + _tableName
								+ "  (ProcessId, Name, Value) values ("
								+ Quotedstring(Integer.toString(parentPId))
								+ ", " + Quotedstring(name) + ", "
								+ Quotedstring(value) + ")");
						
						break;
					}
					
				} catch (SQLException e) {
					numtoloop--;
					Thread.sleep(100);
					Log.Debug("ContextVar/SetContextVar:: [WARN] exception occured while setting the contextvar "
							+ name
							+ " \nmessage:"
							+ e.getMessage()
							+ "\ncausing class: " + e.getClass());
					System.out
							.println("ContextVar/SetContextVar:: [WARN] exception occured while setting the contextvar "
									+ name
									+ " \nmessage:"
									+ e.getMessage()
									+ "\ncausing class: " + e.getClass());
				}
			}
			conn.close();
			// Log.Debug("SetContextVar: Executed Command = " + "Insert Into  "
			// + _tableName + "  (ProcessId, Name, Value) values (" +
			// Quotedstring(Integer.toString(parentPId)) + ", " +
			// Quotedstring(name) + ", " + Quotedstring(value) + ")");

		} catch (Exception ex) {
			Log.Debug("ContextVar/SetContextVar: Exception is : "
					+ ex.getMessage() + ex.getStackTrace());
			throw ex;
		} finally {
			// Log.Debug("ContextVar/SetContextVar: Connection is getting closed ");
			if (conn != null)
				conn.close();
			// Log.Debug("ContextVar/SetContextVar: Connection is closed. End of Function ");
		}
	}

	public static String getContextVar(String name) throws Exception {
		Log.Debug("getContextVar: Start of Function with name as : " + name);
		Connection conn = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + _dbPath + "/"
					+ _dbName);

			// Log.Debug("getContextVar: Connection Opened .");

			int parentPId = (int) Controller.harnessPIDValue;
			// Log.Debug("getContextVar: Parent ID = " + parentPId);

			Statement stat = conn.createStatement();
			// Log.Debug("getContextVar: Firing Command = " +
			// "Select value From " + _tableName + " Where ProcessId=" +
			// Quotedstring( Integer.toString(parentPId) ) + " And " + "Name=" +
			// Quotedstring(name) + "");
			String value = null;
			synchronized (justForLock) {
				ResultSet rs = stat.executeQuery("Select value From "
						+ _tableName + " Where ProcessId="
						+ Quotedstring(Integer.toString(parentPId)) + " And "
						+ "Name=" + Quotedstring(name) + "");

				if (rs.next()) {
					value = rs.getString("value");
				}
				rs.close();

				conn.close();
			}
			Log.Debug("getContextVar: Executed Command = "
					+ "Select value From " + _tableName + " Where ProcessId="
					+ Quotedstring(Integer.toString(parentPId)) + " And "
					+ "Name=" + Quotedstring(name) + ""
					+ " and Value returned is : " + value);

			return value;

		} catch (Exception ex) {
			Log.Error("ContextVar/getContextVar: Exception is : "
					+ ex.getMessage() + ex.getStackTrace());
			throw new Exception("ContextVar/getContextVar: Exception is : "
					+ ex.getMessage() + ex.getStackTrace());
		} finally {
			// Log.Debug("ContextVar/getContextVar: Connection is getting closed ");
			if (conn != null)
				conn.close();
			// Log.Debug("ContextVar/getContextVar: Connection is closed. End of Function ");
		}
	}

	public static String getContextVarName(String value) throws Exception {
		Log.Debug("getContextVarName: Start of Function with value as : "
				+ value);
		Connection conn = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + _dbPath + "/"
					+ _dbName);

			Log.Debug("getContextVar: Connection Opened .");

			int parentPId = (int) Controller.harnessPIDValue;
			Log.Debug("getContextVar: Parent ID = " + parentPId);

			Statement stat = conn.createStatement();
			Log.Debug("getContextVar: Firing Command = " + "Select Name From "
					+ _tableName + " Where ProcessId="
					+ Quotedstring(Integer.toString(parentPId)) + " And "
					+ "Value=" + Quotedstring(value) + "");

			ResultSet rs = stat.executeQuery("Select Name From " + _tableName
					+ " Where ProcessId="
					+ Quotedstring(Integer.toString(parentPId)) + " And "
					+ "Value=" + Quotedstring(value) + "");

			String name = null;
			if (rs.next()) {
				name = rs.getString("Name");
			}
			rs.close();

			conn.close();
			Log.Debug("getContextVar: Executed Command = "
					+ "Select Name From " + _tableName + " Where ProcessId="
					+ Quotedstring(Integer.toString(parentPId)) + " And "
					+ "Value=" + Quotedstring(name) + ""
					+ " and Name returned is : " + name);

			return name;

		} catch (Exception ex) {
			Log.Error("ContextVar/getContextVar: Exception is : "
					+ ex.getMessage() + ex.getStackTrace());
			throw new Exception("ContextVar/getContextVar: Exception is : "
					+ ex.getMessage() + ex.getStackTrace());
		} finally {
			Log.Debug("ContextVar/getContextVar: Connection is getting closed ");
			if (conn != null)
				conn.close();
			Log.Debug("ContextVar/getContextVar: Connection is closed. End of Function ");
		}
	}

	public static void alterContextVar(String name, String value)
			throws Exception {

		Log.Debug("alterContextVar: Start of Function with with name as : "
				+ name + " and Value as = " + value);
		Connection conn = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + _dbPath + "/"
					+ _dbName);

			// Log.Debug("alterContextVar: Connection Opened .");

			int parentPId = (int) Controller.harnessPIDValue;
			// Log.Debug("alterContextVar: Parent ID = " + parentPId);

			Statement stat = conn.createStatement();
			// Log.Debug("alterContextVar: Firing Command = " + "Update " +
			// _tableName + " Set Value=" + Quotedstring(value) +" Where " +
			// "ProcessId=" + Quotedstring(Integer.toString(parentPId)) +
			// " And Name=" + Quotedstring(name) + "");
			int numtoloop=LOOP_NUM;
			while(numtoloop>0)
			{
				try{
			
			synchronized (justForLock) {
				stat.executeUpdate("Update " + _tableName + " Set Value="
						+ Quotedstring(value) + " Where " + "ProcessId="
						+ Quotedstring(Integer.toString(parentPId))
						+ " And Name=" + Quotedstring(name) + "");
				
				
			}
			Log.Debug("alterContextVar: Executed Command = " + "Update "
					+ _tableName + " Set Value=" + Quotedstring(value)
					+ " Where " + "ProcessId="
					+ Quotedstring(Integer.toString(parentPId)) + " And Name="
					+ Quotedstring(name) + "");
			break;
				}catch(SQLException se)
				{
					numtoloop--;
					Thread.sleep(100);
					Log.Debug("ContextVar/alterContextVar:: [WARN] exception occured while setting the contextvar "
							+ name
							+ " \nmessage:"
							+ se.getMessage()
							+ "\ncausing class: " + se.getClass());
					System.out
							.println("ContextVar/alterContextVar:: [WARN] exception occured while setting the contextvar "
									+ name
									+ " \nmessage:"
									+ se.getMessage()
									+ "\ncausing class: " + se.getClass());
				}
			}
			conn.close();
		} catch (Exception ex) {
			Log.Error("ContextVar/alterContextVar: Exception is : "
					+ ex.getMessage() + ex.getStackTrace());
			throw ex;
		} finally {
			// Log.Debug("ContextVar/alterContextVar: Connection is getting closed ");
			if (conn != null)
				conn.close();
			// Log.Debug("ContextVar/alterContextVar: Connection is closed. End of Function ");
		}
	}

	/***
	 * Context variable name to be deleted.
	 * 
	 * @param name
	 */
	public static void Delete(String name) throws Exception {
		
			Log.Debug("Delete: Start of Function with with name as : " + name);
			Connection conn = null;
			try {
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection("jdbc:sqlite:" + _dbPath
						+ "/" + _dbName);

				Log.Debug("ContextVar/Delete: Connection Opened .");

				int parentPId = (int) Controller.harnessPIDValue;
				Log.Debug("ContextVar/Delete: Parent ID = " + parentPId);

				Statement stat = conn.createStatement();
				Log.Debug("ContextVar/Delete: Firing Command = "
						+ "Delete From " + _tableName + " Where ProcessId='"
						+ parentPId + "' And Name='" + name + "'");
				synchronized (justForLock) {
				stat.executeUpdate("Delete From " + _tableName
						+ " Where ProcessId='" + parentPId + "' And Name='"
						+ name + "'");
				conn.close();
				Log.Debug("ContextVar/Delete: Executed Command = "
						+ "Delete From " + _tableName + " Where ProcessId='"
						+ parentPId + "' And Name='" + name + "'");
				}
			} catch (Exception ex) {
				Log.Error("ContextVar/Delete: Exception is : "
						+ ex.getMessage() + ex.getStackTrace());
				throw ex;
			} finally {
				Log.Debug("ContextVar/Delete: Connection is getting closed ");
				if (conn != null)
					conn.close();
				Log.Debug("ContextVar/Delete: Connection is closed. End of Function ");
			}
		

	}

	/***
	 * Delete all context variables of harness.
	 * 
	 * @param processId
	 *            Harness Process Id.
	 */
	public static void DeleteAll(int processId) throws Exception {
		Log.Debug("DeleteAll: Start of Function with with processId as : "
				+ processId);
		Connection conn = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + _dbPath + "/"
					+ _dbName);

			Log.Debug("DeleteAll: Connection Opened .");

			Statement stat = conn.createStatement();
			Log.Debug("DeleteAll: Firing Command = " + "Delete From "
					+ _tableName + " Where ProcessId='" + processId + "'");

			stat.executeUpdate("Delete From " + _tableName
					+ " Where ProcessId='" + processId + "'");
			conn.close();
			Log.Debug("DeleteAll: Executed Command = " + "Delete From "
					+ _tableName + " Where ProcessId='" + processId + "'");

		} catch (Exception ex) {
			Log.Error("ContextVar/DeleteAll: Exception is : " + ex.getMessage()
					+ ex.getStackTrace());
			throw ex;
		} finally {
			Log.Debug("ContextVar/DeleteAll: Connection is getting closed ");
			if (conn != null)
				conn.close();
			Log.Debug("ContextVar/DeleteAll: Connection is closed. End of Function ");
		}
	}
}
