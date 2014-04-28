package com.automature.zug.gui.model;

public class InprocessPackage {
	
	private String packageName;
	private String language;
	private String path;
	private String jarPackage;
	private String className;
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getJarPackage() {
		return jarPackage;
	}
	public void setJarPackage(String jarPackage) {
		this.jarPackage = jarPackage;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	@Override
	public String toString() {
		return "InprocessPackage [packageName=" + packageName + ", language="
				+ language + ", path=" + path + ", jarPackage=" + jarPackage
				+ ", className=" + className + "]";
	}
	
	
}
