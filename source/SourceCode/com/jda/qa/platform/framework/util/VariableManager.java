package SourceCode.com.jda.qa.platform.framework.util;

import java.util.HashMap;

public class VariableManager {

	public HashMap<String, String> varHolder = new HashMap<String, String>();

	public void storeVariable(String varName, String varValue) {
		varHolder.put(varName, varValue);

	}

	public String getVariableValue(String varName) {
		return varHolder.get(varName);
	}

}
