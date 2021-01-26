package SourceCode.com.jda.qa.platform.framework.util;

import com.jda.qa.platform.framework.util.ExcelReader;

import java.util.ArrayList;

/**
 * 
 * This is an Utility Class for reading the Test Suites, Test Suites Paths and Test Suites Runmodes from a suite excel file. 
 * 
 * @author Santosh Kumar Medchalam and Vinay Kumar Tangella
 *
 */

public class ExcelUtil {
	
	/**
	 * 
	 * This method will return all the Test Suites defined in the Suite File as Array List.
	 * 
	 * @param suiteReader The File from which the Test Suites will be fetched
	 * @return The Array List of the Test Suites defined in the Suite File
	 */

	public static ArrayList<String> getTestSuites(ExcelReader suiteReader) {

		ArrayList<String> testCaseIDs = new ArrayList<String>();

		int testCaseCount = suiteReader.getRowCount("Test Suite");

		for (int index = 2; index <= testCaseCount; index++) {

			testCaseIDs.add(suiteReader.getCellData("Test Suite",
					"TestSuiteName", index));
		}

		return testCaseIDs;

	}

	/**
	 * This method will return all the Test Suite paths defined in the Suite File as Array List.
	 * 
	 * 
	 * @param suiteReader The File from which the Test Suite paths will be fetched
	 * @return The Array List of the Test Suite paths defined in the Suite File
	 */
	public static ArrayList<String> getTestSuitesPaths(ExcelReader suiteReader) {

		ArrayList<String> testCaseIDs = new ArrayList<String>();

		int testCaseCount = suiteReader.getRowCount("Test Suite");

		for (int index = 2; index <= testCaseCount; index++) {

			testCaseIDs.add(suiteReader.getCellData("Test Suite",
					"TestSuitePath", index));
		}

		return testCaseIDs;

	}
	
	/**
	 * This method will return all the RunModes defined in the Suite File as Array List.
	 * 
	 * @param suiteReader The File from which the RunModes will be fetched
	 * @return The Array List of the RunModes defined in the Suite File
	 */

	public static ArrayList<String> getRunMode(ExcelReader suiteReader) {

		ArrayList<String> runModes = new ArrayList<String>();

		int testCaseCount = suiteReader.getRowCount("Test Suite");

		for (int index = 2; index <= testCaseCount; index++) {

			runModes.add(suiteReader
					.getCellData("Test Suite", "RunMode", index));
		}

		return runModes;

	}
	
	public static ArrayList<String> getResultsFolder(ExcelReader suiteReader) {
		ArrayList<String> runModes = new ArrayList<String>();

		int testCaseCount = suiteReader.getRowCount("Test Suite");

		for (int index = 2; index <= testCaseCount; index++) {

			runModes.add(suiteReader
					.getCellData("Test Suite", "Results", index));
		}

		return runModes;
	}
	
	/**
	 * This method will return all the BrowserNumber defined in the Excel File as Array List.
	 * 
	 * @param suiteReader The File from which the BrowserNumbers will be fetched
	 * @return The Array List of the BrowserNumbers defined in the Suite File
	 */

	public static ArrayList<String> getBrowserNumber(ExcelReader suiteReader) {

		ArrayList<String> runModes = new ArrayList<String>();

		int testCaseCount = suiteReader.getRowCount("Test Suite");

		for (int index = 2; index <= testCaseCount; index++) {

			runModes.add(suiteReader
					.getCellData("Test Suite", "BrowserNumber", index));
		}

		return runModes;

	}

}
