package com.automature.zug.gui.model;

public class LanguagePackage {
	
	private String language;
	private String interpreter;
	private String path;
	private String options;
	private String extension;
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getInterpreter() {
		return interpreter;
	}
	public void setInterpreter(String interpreter) {
		this.interpreter = interpreter;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getOptions() {
		return options;
	}
	public void setOptions(String options) {
		this.options = options;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	@Override
	public String toString() {
		return "LanguagePackage [language=" + language + ", interpreter="
				+ interpreter + ", path=" + path + ", options=" + options
				+ ", extension=" + extension + "]";
	}
	
	

}
