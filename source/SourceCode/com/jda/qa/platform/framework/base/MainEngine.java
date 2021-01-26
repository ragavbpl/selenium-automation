package SourceCode.com.jda.qa.platform.framework.base;

import static com.jda.qa.platform.framework.util.ExcelUtil.getRunMode;
import static com.jda.qa.platform.framework.util.ExcelUtil.getTestSuites;
import static com.jda.qa.platform.framework.util.ExcelUtil.getTestSuitesPaths;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.jda.qa.platform.framework.browser.MethodInvoker;
import com.jda.qa.platform.framework.browser.TestBrowser;
import com.jda.qa.platform.framework.browser.WebDriverBrowser;
import com.jda.qa.platform.framework.locator.LocatorFormatter;
import com.jda.qa.platform.framework.util.ExcelReader;
import com.jda.qa.platform.framework.util.FileUtils;
import com.jda.qa.platform.framework.util.VariableManager;
import com.jda.qa.platform.reports.WriteResulsToHTML;

/**
 * This class is the heart of the entire framework. This will be called to
 * execute the test cases.
 * 
 * @author Santosh Kumar Medchalam and Vinay Kumar Tangella
 *
 */

public class MainEngine {

	public static ArrayList<String> loadedRepositoryBunbles = new ArrayList<String>();

	public static ArrayList<String> loadedLocators = new ArrayList<String>();

	public static HashMap<String, Properties> locatorRepository = new HashMap<String, Properties>();

	public static Logger log = Logger.getLogger("Selenium Log");

	public static TestBrowser browser;

	public static ArrayList<TestBrowser> browserStack = new ArrayList<TestBrowser>();

	private static HashMap<String, ResourceBundle> resource = new HashMap<String, ResourceBundle>();

	private static String locale = "en";

	public static String sampleDBUser = "SAMPLE";

	public static String wwfDBUser = "WWFMGR";

	public static String downloadsDir = "downloads";

	public static String dbURL = "";

	public static String dateFormat = "M/d/yy";

	public static String dateTimeFormat = "M/d/yy hh:mm a";

	public static String decimalFormat = "###,###.000";

	public static String integerFormat = "###,###";

	public static String suiteFile;
	
	public static String runType="Smoke";

	public static MethodInvoker caller;

	public static String methodInvokerClassName;

	public static VariableManager varManager = new VariableManager();

	public static ThreadGroup seleniumThreads = new ThreadGroup(
			"seleniumThreads");

	// Properties related to Distributed Execution

	private static boolean isDistributed = false;

	private static Properties distributionProperies = new Properties();
	
	private static Properties suiteProps = new Properties();

	/**
	 * This is the main method invoked by the JVM to execute the test cases
	 * 
	 * @param args
	 *            The command line arguments.
	 */
	public static void main(String[] args) {
		
		try {
			
			suiteProps.load(new FileReader(FileUtils.getFileLocation(
					"settings", "suite.properties")));
			
		} catch (Exception exp) {
			
			log.info("Failed to load the Suite Properties");
			exp.printStackTrace();
			System.exit(0);
			
		}
		
		runType = suiteProps.getProperty("runType") != null ? suiteProps.getProperty("runType") : "Smoke";

		if (args.length > 0 && (args[0]).equalsIgnoreCase("GenReports")) {
			
			generateReports();

		} else if (args.length > 0 && (args[0]).equalsIgnoreCase("-properties")) {

			isDistributed = true;

			try {
				distributionProperies.load(new FileReader(FileUtils
						.getFileLocation("settings", args[1])));
				
			} catch (IOException e) {

				System.out
						.println("Failed to load Properties for distribution : "
								+ e.getMessage());
				System.out.println("Hence Exiting");
				System.exit(0);

			}

		}

		DateFormat dateFormatReports = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss");

		try {

			// Load the method invoker
			methodInvokerClassName = (suiteProps
					.getProperty("methodInvokerClassName") != null ? suiteProps
					.getProperty("methodInvokerClassName")
					: "com.jda.qa.platform.framework.browser.WebWorksMethodInvoker");

			caller = (MethodInvoker) Class.forName(methodInvokerClassName)
					.getConstructor().newInstance();

		} catch (Exception exp) {
			log.info("Failed to load the MethodInvoker");
			exp.printStackTrace();
			System.exit(0);
		}

		locale = suiteProps.getProperty("locale");

		sampleDBUser = suiteProps.getProperty("sampleDBUser");

		wwfDBUser = suiteProps.getProperty("wwfDBUser");

		dbURL = suiteProps.getProperty("DBURL");

		downloadsDir = (suiteProps.getProperty("downloadsDir") == null) ? "downloads"
				: suiteProps.getProperty("downloadsDir");

		browser = new WebDriverBrowser(suiteProps.getProperty("serverURL"),
				suiteProps.getProperty("browserType"));

		// set the preferences

		dateFormat = suiteProps.getProperty("dateFormat");

		dateTimeFormat = suiteProps.getProperty("dateTimeFormat");

		decimalFormat = suiteProps.getProperty("decimalFormat");

		integerFormat = suiteProps.getProperty("integerFormat");

		suiteFile = suiteProps.getProperty("suiteFile");	

		if (isDistributed) {
			suiteFile = distributionProperies.getProperty("suiteFile");

			RollingFileAppender logFileAppender = new RollingFileAppender();

			logFileAppender.setName(distributionProperies
					.getProperty("log.loggerName"));
			logFileAppender.setFile(distributionProperies
					.getProperty("log.logFileName"));
			logFileAppender.setLayout(new PatternLayout(distributionProperies
					.getProperty("log.layout.ConversionPattern")));
			logFileAppender.setThreshold(Level.toLevel(distributionProperies
					.getProperty("log.level")));
			logFileAppender.setAppend(true);
			logFileAppender.activateOptions();

			Logger.getRootLogger().addAppender(logFileAppender);
		}

		if (suiteFile == null
				|| suiteFile.trim().equals("")
				|| suiteFile.equals("Test Suites.xlsx")
				|| !(new File(System.getProperty("user.dir") + "\\suites\\"
						+ suiteFile).exists())) {
			suiteFile = "Test Suites.xlsx";
		}

		// Load Resources

		resource = loadResources(locale);

		// Load the Suite excel file

		String testsDir = System.getProperty("user.dir") + "\\suites\\";

		ExcelReader suiteReader = new ExcelReader(testsDir + suiteFile);

		ArrayList<String> testSuites = getTestSuites(suiteReader);

		ArrayList<String> testSuitesFiles = getTestSuitesPaths(suiteReader);

		ArrayList<String> testSuiteRunMode = getRunMode(suiteReader);

		File suiteResultsFile;

		ExcelReader suiteResultsExcelReader;

		String loginDetailsForIE = "";

		for (int i = 0; i < testSuites.size(); i++) {

			String testSuiteName = testSuites.get(i);

			log.info("Execution Started for Test Suite : " + testSuiteName);

			// Generate the Suite Results Excel File

			suiteResultsFile = new File(System.getProperty("user.dir")
					+ "\\reports\\" + testSuiteName + "_Results.xlsx");

			if (suiteResultsFile.exists()) {
				suiteResultsFile.delete();
			}

			if (testSuiteRunMode.get(i).equalsIgnoreCase("N")) {

				log.info("Execution Skipped for Test Suite : " + testSuiteName);

				continue;

			} else {

				// Create the Suite Results File
				try {
					// Copy the Sample_Results.xlsx from data to reports

					File templateFile = new File(System.getProperty("user.dir")
							+ "\\data\\Sample_Results.xlsx");

					FileUtils.copyFile(templateFile, suiteResultsFile);

				} catch (Exception e) {
					log.info("Failed to Create the Suite Results File, hence the execution is not allowed");
					System.exit(0);
				}

				// Add the Suite Results sheet to the Results excel file

				suiteResultsExcelReader = new ExcelReader(
						suiteResultsFile.getAbsolutePath());

				suiteResultsExcelReader.addSheet("Suite Results");

				// Add the Columns

				suiteResultsExcelReader.addColumn("Suite Results",
						"Test Case Name");
				suiteResultsExcelReader
						.addColumn("Suite Results", "Start Time");
				suiteResultsExcelReader.addColumn("Suite Results", "End Time");
				suiteResultsExcelReader.addColumn("Suite Results", "Result");
				suiteResultsExcelReader.addColumn("Suite Results", "Comment");

			}

			// Load the Test Suite

			ExcelReader suite = new ExcelReader(testsDir
					+ testSuitesFiles.get(i));

			int testCaseCount = suite.getRowCount("Test Suite");

			ArrayList<String> testCaseNames = new ArrayList<String>();

			ArrayList<String> testCaseDesc = new ArrayList<String>();

			ArrayList<String> testCaseRunModes = getRunMode(suite);

			// load the test cases in test suite

			for (int tcCount = 2; tcCount <= testCaseCount; tcCount++) {

				testCaseNames.add(suite.getCellData("Test Suite",
						"TestCaseName", tcCount));

			}

			boolean isDescCol = true;
			String cellContent = "";

			cellContent = suite.getCellData("Test Suite", "TestCaseDesc", 1);

			if (cellContent.trim().length() == 0) {
				log.info("The Description Column is not available");
				isDescCol = false;
			}

			if (isDescCol) {

				for (int tcCount = 2; tcCount <= testCaseCount; tcCount++) {

					testCaseDesc.add(suite.getCellData("Test Suite",
							"TestCaseDesc", tcCount));

				}

				suiteResultsExcelReader.addColumn("Suite Results",
						"Test Case Desc");

			}

			// Run the test cases in the test Suite

			for (int tcCount = 2; tcCount <= testCaseCount; tcCount++) {

				boolean failedStatus = false;
				String comment = "";

				String testCaseName = testCaseNames.get(tcCount - 2);

				log.info("Execution Started for Test Case : " + testCaseName);

				// Add the Test case Name to the Suite Results
				suiteResultsExcelReader.setCellData("Suite Results",
						"Test Case Name", tcCount, testCaseName);

				// Add the Test case description to the Suite Results
				if (testCaseDesc.size() > 0) {
					suiteResultsExcelReader.setCellData("Suite Results",
							"Test Case Desc", tcCount,
							testCaseDesc.get(tcCount - 2));
				}

				// Add the Start Time to the Suite Results

				suiteResultsExcelReader.setCellData("Suite Results",
						"Start Time", tcCount, dateFormatReports
								.format(Calendar.getInstance().getTime()));

				if (testCaseRunModes.get(tcCount - 2).equalsIgnoreCase("N")) {
					log.info("Execution Skipped for Test Case : "
							+ testCaseName);
					suiteResultsExcelReader.setCellData("Suite Results",
							"End Time", tcCount, dateFormatReports
									.format(Calendar.getInstance().getTime()));
					suiteResultsExcelReader.setCellData("Suite Results",
							"Result", tcCount, "Skip");
					suiteResultsExcelReader.setCellData("Suite Results",
							"Comment", tcCount, "Skipped as Runmode is N");
					continue;
				}

				int testSteps = suite.getRowCount(testCaseName);

				boolean userAlreadyLoggedOut = false;

				for (int testStepCnt = 2; testStepCnt <= testSteps; testStepCnt++) {

					failedStatus = false;
					comment = "";

					String keyWord = suite.getCellData(testCaseName, "KeyWord",
							testStepCnt);

					String option1 = suite.getCellData(testCaseName, "Option1",
							testStepCnt);

					String option2 = suite.getCellData(testCaseName, "Option2",
							testStepCnt);

					String option3 = "";

					int testcaseOptionCount = suite
							.getColumnCount(testCaseName);

					if (testcaseOptionCount > 3) {
						option3 = suite.getCellData(testCaseName, "Option3",
								testStepCnt).trim();
					}

					boolean skipLogin = false;

					if (keyWord.equalsIgnoreCase("CallExcelSheet")
							&& option1.toUpperCase().contains(
									"LoginScenarios,Login".toString()
											.toUpperCase())) {
						loginDetailsForIE = option2;

						skipLogin = true;

					}

					if (testStepCnt == 2
							&& loginDetailsForIE.length() != 0
							&& browser.getBrowserType().equalsIgnoreCase(
									"iexplore")) {

						if (testStepCnt == 2 && userAlreadyLoggedOut
								&& tcCount != 2) {
							continue;
						}

						caller.runMethod("callExcelSheet",
								"LoginScenarios,LoginRelaunchBrowser",
								loginDetailsForIE);

						if (skipLogin) {
							continue;
						}
					}

					// Update the Suite Locale

					if (option1.contains("SUITE_LOCALE")) {
						option1 = option1.replaceAll("SUITE_LOCALE", locale);
					}

					if (option2.contains("SUITE_LOCALE")) {
						option2 = option2.replaceAll("SUITE_LOCALE", locale);
					}

					log.info("KeyWord : " + keyWord);
					log.info(" -- Option1 : " + option1);
					log.info(" -- Option2 : " + option2);

					// Dynamic Form support

					option2 = option2.replaceAll(".properties", ":properties");

					// To Handle the Dynamic Option1 sent via Option2

					if (option1.equalsIgnoreCase("OPTION2")) {
						option1 = option2;
						option2 = "";
					}

					try {

						if (option2.trim().length() == 0) {
							option1 = LocatorFormatter.formatLocator(option1);
							caller.runMethod(keyWord, option1);
						} else if (testcaseOptionCount >= 3
								&& option2.trim().length() != 0
								&& option3.trim().length() == 0) {
							option1 = LocatorFormatter.formatLocator(option1);
							option2 = LocatorFormatter.formatLocator(option2);
							caller.runMethod(keyWord, option1, option2);
						} else {
							String[] options = new String[testcaseOptionCount - 1];
							int optionCount = 1;

							for (optionCount = 1; optionCount < testcaseOptionCount; optionCount++) {
								String optionValue = suite.getCellData(
										testCaseName, "Option" + optionCount,
										testStepCnt);
								optionValue = LocatorFormatter
										.formatLocator(optionValue);

								if (optionValue.trim().length() == 0) {
									break;
								} else if (optionValue.trim().length() > 0
										&& optionValue.trim().toString()
												.equalsIgnoreCase("NULL")) {
									break;
								}
								options[optionCount - 1] = optionValue;
							}

							String[] optionNew = new String[optionCount - 1];

							for (int j = 0; j < optionCount - 1; j++) {
								optionNew[j] = options[j];
							}

							caller.runMethod(keyWord, optionNew);
						}
					} catch (Throwable e) {
						try {

							failedStatus = true;
							comment = e.getMessage();

							browser.waitForPageToLoad();

							File screenshot = ((TakesScreenshot) ((WebDriver) browser
									.getWebDriver()))
									.getScreenshotAs(OutputType.FILE);

							File screenshotAct = new File(
									System.getProperty("user.dir") + "\\logs\\"
											+ testSuiteName + "_"
											+ testCaseName + "_" + testStepCnt
											+ ".jpeg");

							if (screenshotAct.exists()) {
								screenshotAct.delete();
							}

							if (browser.getPopUpWindowname() != null
									&& !browser
											.getPopUpWindowname()
											.equalsIgnoreCase(
													browser.getMainWindowHandle())) {

								browser.closeWindow(browser
										.getPopUpWindowname());

								browser.setPopUpWindowname(null);

								browser.selectWindow(browser
										.getMainWindowHandle());

								browser.switchToDefaultContent();
							}

							FileUtils.copyFile(screenshot, screenshotAct);

							browser.refresh();

						} catch (Throwable t) {
							failedStatus = true;
							comment = e.getMessage();
						}
					}

					if (failedStatus) {
						break;
					}

				}

				System.out.println("Execution Completed for Test Case : "
						+ testCaseName);

				log.info("Execution Completed for Test Case : " + testCaseName);

				suiteResultsExcelReader.setCellData("Suite Results",
						"End Time", tcCount, dateFormatReports.format(Calendar
								.getInstance().getTime()));
				if (failedStatus) {
					suiteResultsExcelReader.setCellData("Suite Results",
							"Result", tcCount, "Fail");
				} else {
					suiteResultsExcelReader.setCellData("Suite Results",
							"Result", tcCount, "Pass");
				}

				suiteResultsExcelReader.setCellData("Suite Results", "Comment",
						tcCount, comment);

			}

			log.info("Execution Completed for Test Suite : " + testSuiteName);

			// Relaunch the browser for every suite

			ArrayList<String> runModesLauncher = new ArrayList<String>(
					testSuiteRunMode.subList(i + 1, testSuitesFiles.size()));

			if (i < (testSuitesFiles.size() - 1) && runModesLauncher.size() > 0
					&& runModesLauncher.contains("Y")) {
				caller.runMethod("relaunchBrowser", "", "");
			}

		}
		
		try{

		if (browserStack.size() > 1) {
			for (TestBrowser browser : browserStack) {
				browser.close();
				browser.stop();
			}
		} else {
			browser.close();
			browser.stop();
		}
		
		}catch(Throwable error){
			log.info("Error occured while closing the browser. "+error.getMessage());
		}

		if (isDistributed) {

			File execCompletionFile = new File(System.getProperty("user.dir")
					+ "\\logs\\" + "ExecutionCompletion"
					+ distributionProperies.getProperty("browserNumber")
					+ ".txt");
			if (execCompletionFile.exists()) {
				execCompletionFile.delete();
			}

			try {
				execCompletionFile.createNewFile();
			} catch (IOException e) {
				log.info("Failed to create the exection completion file ");
				log.info("With Error : " + e.getMessage());
				System.exit(0);
			}

			// Check for other suites completion here

			File dir = new File(System.getProperty("user.dir") + "\\logs\\");
			FileFilter fileFilter = new WildcardFileFilter(
					"ExecutionCompletion*.txt");
			File[] files = dir.listFiles(fileFilter);

			if (files.length == new Integer(
					distributionProperies.getProperty("browserCount"))) {
				generateReports();
			}
		}

	}

	/**
	 * This method will be used to fetch the Resource Bundles as Hashmap
	 * 
	 * @return The Hashmap of the Resource Bundles.
	 */

	public static HashMap<String, ResourceBundle> getResource() {
		return resource;
	}

	/**
	 * Will method will be used to fetch the locale of the suite.
	 * 
	 * @return The locale of the suite.
	 */
	public static String getLocale() {
		return locale;
	}

	public static void setCaller(MethodInvoker invoker) {
		caller = invoker;
	}
	
	public static void setLocale(String newLocale){
		locale = newLocale;
		resource = loadResources(newLocale);
	}
	
	public static void resetLocale(){
		locale = suiteProps.getProperty("locale");
		resource = loadResources(locale);
	}
	
	public static HashMap<String, ResourceBundle> loadResources(String locale){
		
		Properties resourceProperties = new Properties();
		
		HashMap<String, ResourceBundle> resource = new HashMap<String, ResourceBundle>();

		try {
			resourceProperties.load(new FileReader(FileUtils.getFileLocation(
					"settings", "loadableResources.properties")));

			String res = resourceProperties.getProperty("resources");

			String[] resArray = res.split(",");

			for (int index = 0; index < resArray.length; index++) {
				resource.put(resArray[index], ResourceBundle.getBundle(
						resArray[index], new Locale(locale)));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resource;
	}

	public static void generateReports() {
		try {
			WriteResulsToHTML.generateReports();
		} catch (Exception e) {
			log.error(e.getMessage());
			log.error("Failed to generate the reports", e);
			System.out.println("Failed to generate the reports : "
					+ e.getMessage());
		} finally {
			System.exit(0);
		}
	}

}