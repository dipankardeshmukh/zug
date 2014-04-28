package com.automature.zug.gui.model;

import java.util.List;
import java.util.Set;

public class OutProcessPackage {
	
	Set<String> scriptLocations;
	Set<LanguagePackage> langPacks;
	
	public Set<String> getScriptLocations() {
		return scriptLocations;
	}
	public void setScriptLocations(Set<String> scriptLocations) {
		this.scriptLocations = scriptLocations;
	}
	public Set<LanguagePackage> getLangPacks() {
		return langPacks;
	}
	public void setLangPacks(Set<LanguagePackage> langPacks) {
		this.langPacks = langPacks;
	}
	@Override
	public String toString() {
		return "OutProcessPackage [scriptLocations=" + scriptLocations
				+ ", langPacks=" + langPacks + "]";
	}
	
	
}
