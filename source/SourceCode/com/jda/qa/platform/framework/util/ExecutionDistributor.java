package SourceCode.com.jda.qa.platform.framework.util;

import com.jda.qa.platform.framework.util.ExcelReader;
import com.jda.qa.platform.framework.util.FileUtils;

import static com.jda.qa.platform.framework.util.ExcelUtil.getBrowserNumber;
import static com.jda.qa.platform.framework.util.ExcelUtil.getRunMode;
import static com.jda.qa.platform.framework.util.ExcelUtil.getTestSuites;
import static com.jda.qa.platform.framework.util.ExcelUtil.getTestSuitesPaths;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

public class ExecutionDistributor {

	public static String distributorFileName;

	public static void main(String[] args) {

		File distributorFile;

		if (args.length == 0) {
			distributorFileName = "Distribution Plan.xlsx";
		} else {
			distributorFileName = args[0];
		}
		
		distributorFile = new File(System.getProperty("user.dir")
				+ "\\suites\\" + distributorFileName);
		
		System.out.println(distributorFile.getAbsolutePath());

		if (!distributorFile.exists()) {
			System.out
					.println("Please Provide a valid Distributor Excel File as input");
			System.exit(0);
		}

		// Read the Input Distributor Excel File and gather details

		com.jda.qa.platform.framework.util.ExcelReader suiteReader = new com.jda.qa.platform.framework.util.ExcelReader(
				distributorFile.getAbsolutePath());

		ArrayList<String> testSuites = getTestSuites(suiteReader);

		ArrayList<String> testSuitesFiles = getTestSuitesPaths(suiteReader);

		ArrayList<String> testSuiteRunMode = getRunMode(suiteReader);

		ArrayList<String> testSuiteBrowserNumber = getBrowserNumber(suiteReader);

		// Identify the Number of browsers

		HashSet<String> browserNumberSet = new HashSet<String>(
				testSuiteBrowserNumber);

		Iterator<String> browserNumberIterator = browserNumberSet.iterator();

		while (browserNumberIterator.hasNext()) {

			int rowIterator = 2;

			String browserNumber = (String) browserNumberIterator.next();

			// Suite File Generation

			File templateFile = new File(System.getProperty("user.dir")
					+ "\\data\\Sample_Results.xlsx");

			File suiteFile = new File(System.getProperty("user.dir")
					+ "\\suites\\Distributions\\Test Suites " + browserNumber
					+ ".xlsx");

			if (suiteFile.exists()) {
				suiteFile.delete();
			}

			com.jda.qa.platform.framework.util.FileUtils.copyFile(templateFile, suiteFile);

			// Distribution Properties file Generation

			File distributionTemplateFile = new File(
					System.getProperty("user.dir")
							+ "\\settings\\distributionTemplate.properties");

			File distributionActualFile = new File(
					System.getProperty("user.dir") + "\\settings\\distributor"
							+ browserNumber + ".properties");

			if (distributionActualFile.exists()) {
				distributionActualFile.delete();
			}

			FileUtils
					.copyFile(distributionTemplateFile, distributionActualFile);

			// Update the properties

			Properties actualDistributionProperites = new Properties();

			try {
				actualDistributionProperites.load(new FileInputStream(
						distributionActualFile));

				actualDistributionProperites
						.setProperty("suiteFile", "Distributions\\Test Suites "
								+ browserNumber + ".xlsx");

				actualDistributionProperites.setProperty("log.logFileName",
						"logs/AutomationBrowser" + browserNumber + "Instance.log");

				actualDistributionProperites.setProperty("browserNumber",
						browserNumber + "");

				actualDistributionProperites.setProperty("browserCount",
						browserNumberSet.size() + "");

				if (browserNumber.equals("1")) {
					String browserNumberSeperator = "";
					for (int index = 1; index <= browserNumberSet.size(); index++) {
						browserNumberSeperator = browserNumberSeperator + index
								+ ",";
					}

					browserNumberSeperator = browserNumberSeperator.substring(
							0, browserNumberSeperator.length() - 1);

					actualDistributionProperites.setProperty("browserItrParam",
							browserNumberSeperator);
				}

				actualDistributionProperites
						.store(new FileOutputStream(distributionActualFile),
								"Distribution Properties File for Browser "+browserNumber);

			} catch (IOException e) {
				System.out
						.println("Failed Generating the Distribution Properties files");
				System.exit(0);
			}

			com.jda.qa.platform.framework.util.ExcelReader suiteFileExcelReader = new ExcelReader(
					suiteFile.getAbsolutePath());

			// Add Sheet

			suiteFileExcelReader.addSheet("Test Suite");

			// Add the Columns

			suiteFileExcelReader.addColumn("Test Suite", "TestSuiteName");
			suiteFileExcelReader.addColumn("Test Suite", "TestSuitePath");
			suiteFileExcelReader.addColumn("Test Suite", "RunMode");

			// Copy the base file to fit the number of browsers

			// Update the contents of the files as the suite files

			for (int index = 0; index < testSuiteBrowserNumber.size(); index++) {
				String browserNum = testSuiteBrowserNumber.get(index);

				if (browserNum.equals(browserNumber)) {
					suiteFileExcelReader
							.setCellData("Test Suite", "TestSuiteName",
									rowIterator, testSuites.get(index));
					suiteFileExcelReader.setCellData("Test Suite",
							"TestSuitePath", rowIterator,
							testSuitesFiles.get(index));
					suiteFileExcelReader.setCellData("Test Suite", "RunMode",
							rowIterator, testSuiteRunMode.get(index));
					rowIterator++;
				}
			}

		}

	}
}
