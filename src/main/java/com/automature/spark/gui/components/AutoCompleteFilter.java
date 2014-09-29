package com.automature.spark.gui.components;

import java.util.List;
import java.util.Set;

public interface AutoCompleteFilter {

	public Set<String> getFilteredResult(String text);
}
