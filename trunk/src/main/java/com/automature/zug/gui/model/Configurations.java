package com.automature.zug.gui.model;

import java.util.List;
import java.util.Map;

public class Configurations {
	
	private static List<MvmConfiguration> mvmConfigurations;
	private static Map<String,String> adapterParams;
	private ReporterParams reporterParams;
	
	public static List<MvmConfiguration> getMvmConfigurations() {
		return mvmConfigurations;
	}
	public static void setMvmConfigurations(List<MvmConfiguration> mvmConfigurations) {
		Configurations.mvmConfigurations = mvmConfigurations;
	}
	public ReporterParams getReporterParams() {
		return reporterParams;
	}
	public void setReporterParams(ReporterParams reporterParams) {
		this.reporterParams = reporterParams;
	}
	
	
	public static Map<String, String> getAdapterParams() {
		return adapterParams;
	}
	public static void setAdapterParams(Map<String, String> adapterParams) {
		Configurations.adapterParams = adapterParams;
	}
	@Override
	public String toString() {
		return "Configurations [reporterParams=" + reporterParams + "]"+mvmConfigurations+"\n"+adapterParams;
	}
	
	
	
}
