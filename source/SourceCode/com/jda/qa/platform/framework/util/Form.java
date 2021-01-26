package SourceCode.com.jda.qa.platform.framework.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.jda.qa.platform.framework.base.MainEngine;
import com.jda.qa.platform.framework.browser.TestBrowser;
import com.jda.qa.platform.framework.locator.Locator;
import com.jda.qa.platform.framework.locator.Locator.locatorType;
import com.jda.qa.platform.framework.locator.LocatorFormatter;
import com.jda.qa.platform.framework.util.ExcelData;

/**
 * 
 * This is the class that is used to work with any application form.
 * 
 * 
 * @author Santosh Kumar Medchalam and Vinay Kumar Tangella
 * 
 *
 */

public class Form {
	
	/**
	 * Class Variables
	 */
	
	TestBrowser browser;
	HashMap<String, String> objectDetails = new HashMap<String, String>();
	String formProps;
	String excelFileName;
	String sheetName;
	String pkValue;
	String formFrame;
	Locator locator;
	boolean isMultipleFrames = false;
	String[] frames;
	
	/**
	 * 
	 * Constructor to create the Form Object
	 * 
	 * @param browser  The Browser Object
	 * @param formProps  The properties file which will be used to Fill/Read the form
	 * @param sheetName  The Sheet from which the data will be fetched
	 * @param pkValue  The pkValue for the row for which the data is fetched
	 * @param formFrame  The frame where the form is located
	 */

	public Form(TestBrowser browser, String formProps, String sheetName,
			String pkValue, String formFrame) {

		this(browser, formProps, "Contents.xlsx", sheetName, pkValue, formFrame);

	}
	
	/**
	 * 
	 * Constructor to create the Form Object
	 * 
	 * @param browser  The Browser Object
	 * @param formProps  The properties file which will be used to Fill/Read the form
	 * @param excelFileName  The Excel file from which the data will be loaded
	 * @param sheetName  The Sheet from which the data will be fetched
	 * @param pkValue  The pkValue for the row for which the data is fetched
	 * @param formFrame  The frame where the form is located
	 */

	public Form(TestBrowser browser, String formProps, String excelFileName,
			String sheetName, String pkValue, String formFrame) {

		this.browser = browser;
		this.formProps = formProps;
		this.excelFileName = excelFileName;
		this.sheetName = sheetName;
		this.pkValue = pkValue;
		this.formFrame = formFrame;

		// Prepare Locators

		locator = new Locator(formProps, browser);

		// Prepare Forms

		if (formFrame.contains(">")) {
			frames = formFrame.split(">");
		} else {
			frames = new String[] { formFrame };
		}

		// PrepareData

		ExcelData data = new ExcelData(excelFileName, sheetName);

		objectDetails = data.getExcelData(pkValue);

	}
	
	/**
	 * 
	 * This Method is used to fill the form with object details
	 * 
	 */

	public void fill() {
		fill(false);
	}
	
	/**
	 * This Method is used to fill the form with object details
	 * 
	 * @param isUpdate -> This will specify whether the form fill operation needs 
	 *                    to be done using create action or update action
	 */

	public void fill(boolean isUpdate) {

		Set<String> locIds = locator.getSortedLocatorIdSet();

		Iterator<String> locIdIter = locIds.iterator();

		browser.selectFrames(frames);

		while (locIdIter.hasNext()) {
			String locatorId = (String) locIdIter.next();

			locatorId = locatorId.substring(0, locatorId.indexOf(".loc"));
			
			browser.sleep(500);

			if (isUpdate
					&& (locator.getLocator(locatorId + ".isUpdate") != null)
					&& (locator.getLocator(locatorId + ".isUpdate")
							.equalsIgnoreCase("true"))) {
				locator.doAction(locatorId, objectDetails.get(locator
						.getLocatorDBColName(locatorId)));

			} else if (!isUpdate
					&& (locator.getLocator(locatorId + ".isCreate") != null)
					&& (locator.getLocator(locatorId + ".isCreate")
							.equalsIgnoreCase("true"))) {
				locator.doAction(locatorId, objectDetails.get(locator
						.getLocatorDBColName(locatorId)));

			}

		}

		// Select main frame
		browser.switchToDefaultContent();

	}
	
	/**
	 * This method is used to perform the read operation of the form
	 * 
	 * @return  will return the read status of the from values against the object details
	 */

	public boolean read() {

		boolean result = false;

		Set<String> locIds = locator.getSortedLocatorIdSet();

		Iterator<String> locIdIter = locIds.iterator();

		browser.selectFrames(frames);

		while (locIdIter.hasNext()) {
			String locatorId = (String) locIdIter.next();

			locatorId = locatorId.substring(0, locatorId.indexOf(".loc"));

			if ((locator.getLocator(locatorId + ".isRead") != null)
					&& (locator.getLocator(locatorId + ".isRead")
							.equalsIgnoreCase("true"))) {

				String values = objectDetails.get(locator
						.getLocatorDBColName(locatorId));
				if (locator.getLocatorType(locatorId).equalsIgnoreCase(
						locatorType.SELECT.toString())
						|| locator.getLocatorType(locatorId).equalsIgnoreCase(
								locatorType.SELECT_VALUE.toString())
						|| locator.getLocatorType(locatorId).equalsIgnoreCase(
								locatorType.TEXT_FIELD_VALUE.toString())
						|| locator.getLocatorType(locatorId).equalsIgnoreCase(
								locatorType.ASEARCH_COMBOSELECT.toString())) {
					values = values.replaceAll("value:", "");
				}
				ArrayList<String> expectedVals = new ArrayList<String>();

				if (values.endsWith(":multiple")) {

					String[] valuesAry = values.replace(":multiple", "").split(
							",");
					
					//expectedVals = (ArrayList<String>) Arrays.asList(valuesAry);
					expectedVals = new ArrayList<String>(Arrays.asList(valuesAry));
					
				} else {
					expectedVals.add(LocatorFormatter.updateDBValues(values));
				}

				if (expectedVals.contains("SKIP")) {
					result = true;
					continue;
				}

				// Click on the save button
				ArrayList<String> actVals = locator.getLocatorAppValue(locatorId);
				result = validateValues(actVals, expectedVals);

				if (!result) {

					MainEngine.log.info("Detailed Read Comparision Failed :");
					MainEngine.log.info("Actual Values:" + actVals);
					MainEngine.log.info("Expected Values:" + expectedVals);

					break;
				}

			}

		}

		// Select main frame
		browser.switchToDefaultContent();

		return result;

	}
	
	
	/**
	 * 
	 * This is used to compare the ArrayLists
	 * 
	 * @param actualVals  ArrayList containing the actual values
	 * @param expectedVals  ArrayList containing the expected values
	 * @return  Will return the comparison status actualVals ArrayList against expectedVals ArrayList
	 */

	public boolean validateValues(ArrayList<String> actualVals,
			ArrayList<String> expectedVals) {

		boolean compStatus = false;

		Collections.sort(actualVals);
		Collections.sort(expectedVals);

		if (actualVals.size() == expectedVals.size()) {

			actualVals.removeAll(expectedVals);

			if (actualVals.size() != 0) {
				compStatus = false;
			} else {
				compStatus = true;
			}

		} else {
			compStatus = false;
		}

		return compStatus;
	}
}
