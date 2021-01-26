package SourceCode.com.jda.qa.platform.framework.locator;

import java.util.Comparator;
import java.util.HashMap;

/**
 * This is a Comparator class that is used to compare the locator elements using the ".index" value
 *  
 * @author Santosh Kumar Medchalam and Vinay Kumar Tangella
 *
 */

public class LocComparator implements Comparator<String> {

	HashMap<String, String> locator;
	
	
	/**
	 * 
	 * The Constructor used to create the LocComparator Object
	 * 
	 * @param locators   The locators object using which the locators are compared  
	 */
	public LocComparator(HashMap<String, String> locators) {

		this.locator = locators;
	}
	
	@Override
	public int compare(String actValue, String expValue) {

		String actValueIndex = actValue.substring(0, actValue.indexOf(".loc"))
				+ ".index";
		String expValueIndex = expValue.substring(0, expValue.indexOf(".loc"))
				+ ".index";
		int actValueInt = new Integer(locator.get(actValueIndex));
		int expValueInt = new Integer(locator.get(expValueIndex));

		if (actValueInt > expValueInt) {
			return 1;
		} else if (actValueInt < expValueInt) {
			return -1;
		} else {
			return 0;
		}

	}

}
