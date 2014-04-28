package com.automature.zug.gui.model;

public class ReporterParams {
	
	private String dbhostname;
	private String dbname;
	private String dbusername;
	private String dbuserpassword;
	public String getDbhostname() {
		return dbhostname;
	}
	public void setDbhostname(String dbhostname) {
		this.dbhostname = dbhostname;
	}
	public String getDbname() {
		return dbname;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	public String getDbusername() {
		return dbusername;
	}
	public void setDbusername(String dbusername) {
		this.dbusername = dbusername;
	}
	public String getDbuserpassword() {
		return dbuserpassword;
	}
	public void setDbuserpassword(char []dbuserpassword) {
		this.dbuserpassword = new String(dbuserpassword);
	}
	public void setDbuserpassword(String dbuserpassword) {
		this.dbuserpassword = dbuserpassword;
	}
	@Override
	public String toString() {
		return "ReporterParams [dbhostname=" + dbhostname + ", dbname="
				+ dbname + ", dbusername=" + dbusername + ", dbuserpassword="
				+ dbuserpassword + "]";
	}
	
	
	
}
