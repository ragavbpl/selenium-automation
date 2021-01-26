package SourceCode.com.jda.qa.platform.reports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import com.jda.qa.platform.reports.ResultSubReport;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.jda.qa.platform.framework.base.MainEngine;

/**
 * 
 * @author Vinay Tangella
 * @Description This class contains methods to generate HTML reports
 *
 */
@SuppressWarnings("unused")
public class WriteResulsToHTML {

	private static String sFolderLocation = System.getProperty("user.dir")
			+ "\\reports";

	private static File oAllFilesinfolder = new File(sFolderLocation);

	private static String sInputSheetName = "Suite Results";

	private static String[] oAllFiles = oAllFilesinfolder.list();

	static ArrayList<String> oResultsFile = new ArrayList<String>();
	static ArrayList<String> keys = new ArrayList<String>();

	private static String sHeader_BGColor = "FFFFEE";
	private static String sHeader_FontFamily = "Verdana";
	private static String sHeader_FontSize = "12px";

	private static int iTotal_FCount = 0, iTotal_SCount = 0, iTotal_PCount = 0, iTotal_ECount = 0,
			iTotal_TestCases = 0, iTotal_MCount = 0;

	/**
	 * 
	 * @author Vinay Tangella
	 * @Description This method is used to generate HTML reports
	 *
	 */
	public static void generateReports() {

		int iCounter = 0;
		int iForLoop;

		// add only xls files to the array list oResultsFile
		try {
			for (iForLoop = 0; iForLoop < oAllFiles.length; iForLoop++) {
				if (oAllFiles[iForLoop].endsWith("xlsx")) {
					oResultsFile.add(iCounter, oAllFiles[iForLoop].toString());
					iCounter++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Call the method read_ExcelFiles with the parameter oresults file
		read_ExcelFiles(oResultsFile);

	}

	// This method will read all the results in the excel files and write them
	// to an HTMl file called results.html

	/**
	 * 
	 * @author Vinay Tangella
	 * @Description This method will read all the results in the excel files and
	 *              write them to an HTMl file called results.html
	 *
	 */
	public static void read_ExcelFiles(ArrayList<String> oResultsFile) {

		int iNumberOfFiles, iForLoop, iRowCount;
		String sEndDate, sStartDate;
		Date d2, d1;

		Workbook oExcelWorkBook;// WorkBook Object
		Sheet oSheet;// Sheet Object
		Row oRow;// Row Object

		ArrayList<String> oAllHtmlFiles = new ArrayList<String>();
		ArrayList<int[]> oArgs = new ArrayList<int[]>();
		HashMap<String, ArrayList<String>> oFailedTestCases = new HashMap<String, ArrayList<String>>();

		FileInputStream oInputReader;// InputReader Object

		iNumberOfFiles = oResultsFile.size();

		for (iForLoop = 0; iForLoop < iNumberOfFiles; iForLoop++) {

			// Check whether there are any files in the specified folder
			try {
				if (iNumberOfFiles == 0) {
					throw new Exception(
							"There are no files in the specified directory:"
									+ sFolderLocation);
				}

				oInputReader = new FileInputStream(sFolderLocation + "\\"
						+ oResultsFile.get(iForLoop));
				oExcelWorkBook = WorkbookFactory.create(oInputReader);
				oSheet = oExcelWorkBook.getSheet(sInputSheetName);

				if (oSheet == null) {
					throw new Exception("Input Sheet Not Found! = "
							+ sInputSheetName);
				}

				iRowCount = oSheet.getLastRowNum();// row index in POI starts
													// from
													// zero

				if (iRowCount < 1) {// if the row is only 1 that would be the
									// header
									// row
					throw new Exception("No input Rows Found!");
				}

				String sFileName = oResultsFile.get(iForLoop);
				String sFileName1 = sFileName.substring(0,
						(sFileName.length() - 5));

				com.jda.qa.platform.reports.ResultSubReport oResultSubReportObject = new com.jda.qa.platform.reports.ResultSubReport(
						sFolderLocation + "\\Html_Reports\\" + sFileName1
								+ ".html");

				oResultSubReportObject.startHtml();

				oResultSubReportObject.addHead();

				oResultSubReportObject
						.addTitle("Automation Test Result Generated:"
								+ getTimeStamp());

				oResultSubReportObject.endHead();

				oResultSubReportObject.startBody(sHeader_BGColor,
						sHeader_FontFamily, sHeader_FontSize);

				oResultSubReportObject.addBreak();
				oResultSubReportObject.addBreak();

				oResultSubReportObject.startCenter();

				oResultSubReportObject.insertInBold(
						"Automation Test Result Generated on: "
								+ getTimeStamp(), "small");

				oResultSubReportObject.endCenter();

				oResultSubReportObject.addBreak();
				oResultSubReportObject.addBreak();

				oResultSubReportObject.startTable("center", "800");

				oResultSubReportObject.startTableHeader("center");

				oResultSubReportObject.startTableRow("93BFFF");

				oResultSubReportObject.addTableHeaderColumn("Test Case ID");
				oResultSubReportObject.addTableHeaderColumn("Description");
				oResultSubReportObject.addTableHeaderColumn("Result");
				oResultSubReportObject.addTableHeaderColumn("Time Taken");
				oResultSubReportObject.addTableHeaderColumn("Comments");
				oResultSubReportObject.endTableRow();

				oResultSubReportObject.endTableHeader();

				oResultSubReportObject.startTableBody("center");

				int iPassCount = 0, iFailCount = 0, iErrCount = 0, iManualCount = 0, iSkipCount=0;
				for (int i = 1; i <= iRowCount; i++) {

					oRow = oSheet.getRow(i);
					if (oRow.getCell(3).toString().equals("Pass")) {
						iPassCount++;
					} else if (oRow.getCell(3).toString().equals("Fail")) {
						iFailCount++;
						String sTestcasename = oRow.getCell(0).toString();
						String sComment = oRow.getCell(4).toString();
						String sDescription = "";
						ArrayList<String> oDetails = new ArrayList<String>();
						
						if(oRow.getCell(5)!=null){
							sDescription=oRow.getCell(5).toString();
						}
						
						oDetails.add(sFileName1);
						oDetails.add(sTestcasename);
						oDetails.add(sDescription);
						oDetails.add(sComment);
						oFailedTestCases.put(sFileName1 + i + ".suite",
								oDetails);
						keys.add(sFileName1 + i + ".suite");

					} else if (oRow.getCell(3).toString().equals("Skip")) {
						iSkipCount++;
					} else if (oRow.getCell(3).toString().equals("Manual")) {
						iManualCount++;
					} else {
						iErrCount++;
					}

					oResultSubReportObject.startTableRow("E0EDF0");
					oResultSubReportObject.addTableColumn(oRow.getCell(0)
							.toString());

					if (oRow.getCell(5) == null) {
						oResultSubReportObject.addTableColumn("", "left");
					} else {
						oResultSubReportObject.addTableColumn(oRow.getCell(5)
								.toString(), true);
					}

					oResultSubReportObject.addTableColumn(oRow.getCell(3)
							.toString() + "");

					sEndDate = oRow.getCell(2).toString();
					sStartDate = oRow.getCell(1).toString();

					SimpleDateFormat oDF = new SimpleDateFormat(
							"yyyy/mm/dd hh:mm:ss");

					d2 = oDF.parse(sEndDate);
					d1 = oDF.parse(sStartDate);

					long diff = d2.getTime() - d1.getTime();

					long diffSeconds = diff / (1000);

					oResultSubReportObject.addTableColumn(diffSeconds
							+ " seconds", "center");

					oResultSubReportObject.addTableColumn((oRow.getCell(4)
							.toString()),true);

					oResultSubReportObject.endTableRow();
				}

				int iTotalCount = iFailCount + iPassCount + iErrCount+iSkipCount;

				oArgs.add(iForLoop, new int[] { iPassCount, iFailCount,
						iErrCount, iManualCount, iTotalCount, iSkipCount });

				oResultSubReportObject.endTableBody();
				oResultSubReportObject.endTable();
				oResultSubReportObject.endBody();
				oResultSubReportObject.endHtml();
				oResultSubReportObject.closeWriter();

				oAllHtmlFiles.add(iForLoop, sFolderLocation
						+ "\\Html_Reports\\" + sFileName1 + ".html");

				iTotal_FCount = iTotal_FCount + iFailCount;
				iTotal_PCount = iTotal_PCount + iPassCount;
				iTotal_ECount = iTotal_ECount + iErrCount;
				iTotal_MCount = iTotal_PCount + iManualCount;
				iTotal_SCount = iTotal_SCount+ iSkipCount;
				iTotal_TestCases = iTotal_ECount + iTotal_FCount
						+ iTotal_MCount + iTotal_PCount+iTotal_SCount;

			} catch (Throwable t) {
				t.printStackTrace();
			}

		}
		iTotal_TestCases = iTotal_FCount + iTotal_PCount + iTotal_ECount+iTotal_SCount;
		create_IndexLinks(oAllHtmlFiles);
		create_MainResultsFile(oAllHtmlFiles, oArgs);
		create_FailedResultsFile(oFailedTestCases);
		generateResultsProperties();
	}

	// This method will create the index link html file
	// Create a html with all the links to the htnl reports generated

	/**
	 * 
	 * @author Vinay Tangella
	 * @Description This method will create the index link html file. Creates a
	 *              html with all the links to the html reports generated.
	 *
	 */
	public static void create_IndexLinks(ArrayList<String> oAllHtmlFiles) {
		try {

			int iNumberOfFiles, iForLoop, iRowCount, iPassCount = 0, iFailCount = 0;

			iNumberOfFiles = oAllHtmlFiles.size();

			com.jda.qa.platform.reports.ResultSubReport oResultSubReportObject = new com.jda.qa.platform.reports.ResultSubReport(
					sFolderLocation + "\\Html_Reports\\wd_IndexListLinks.html");

			oResultSubReportObject.startHtml();

			oResultSubReportObject.startBody(sHeader_BGColor,
					sHeader_FontFamily, sHeader_FontSize);

			oResultSubReportObject.addBreak();
			oResultSubReportObject.addBreak();

			oResultSubReportObject.startCenter();
			oResultSubReportObject.endCenter();

			oResultSubReportObject.startTable("center", "300");

			oResultSubReportObject.startTableHeader("center");

			oResultSubReportObject.startTableRow("93BFFF");

			oResultSubReportObject.addTableHeaderColumn("Tests");
			oResultSubReportObject.endTableRow();

			oResultSubReportObject.endTableHeader();

			oResultSubReportObject.startTableBody("center");

			for (int iForLoop1 = 0; iForLoop1 < iNumberOfFiles; iForLoop1++) {

				String sFileName = getHtmlFileName(iForLoop1, oAllHtmlFiles);

				oResultSubReportObject.startTableRow("E0EDF0");
				oResultSubReportObject.addTableColumn("<a href=" + sFileName
						+ ".html target=\"in2\" >" + sFileName + "</a>");
				oResultSubReportObject.endTableRow();
			}

			oResultSubReportObject.endTableBody();
			oResultSubReportObject.endTable();
			oResultSubReportObject.endBody();
			oResultSubReportObject.endHtml();
			oResultSubReportObject.closeWriter();
		} catch (Throwable t) {
			System.out.println(t.getMessage());
		}

	}

	// Generate Main Results File

	// This method will create the index.html file

	/**
	 * 
	 * @author Vinay Tangella
	 * @Description This method will create the index.html file. Generate Main
	 *              Results File.
	 */
	public static void create_MainResultsFile(ArrayList<String> oAllHtmlFiles,
			ArrayList<int[]> oArgs) {
		try {
			int iForLoop, iNumberOfFiles;

			iNumberOfFiles = oAllHtmlFiles.size();

			com.jda.qa.platform.reports.ResultSubReport oResultSubReportObject = new com.jda.qa.platform.reports.ResultSubReport(
					sFolderLocation
							+ "\\Html_Reports\\wd_IndexFrameContent.html");

			oResultSubReportObject.startHtml();

			oResultSubReportObject.startBody(sHeader_BGColor,
					sHeader_FontFamily, sHeader_FontSize);

			oResultSubReportObject.addBreak();
			oResultSubReportObject.addBreak();

			oResultSubReportObject.startCenter();
			oResultSubReportObject.insertInBold("Snapshot of the Test Suite",
					"small");
			oResultSubReportObject.endCenter();

			oResultSubReportObject.addBreak();

			oResultSubReportObject.startTable("center", "800");

			oResultSubReportObject.startTableHeader("center");

			oResultSubReportObject.startTableRow("93BFFF");

			oResultSubReportObject.addTableHeaderColumn("Test Cases");
			oResultSubReportObject.addTableHeaderColumn("Passed");
			oResultSubReportObject.addTableHeaderColumn("Skipped");
			oResultSubReportObject.addTableHeaderColumn("Failures");
			oResultSubReportObject.addTableHeaderColumn("Errors");
			oResultSubReportObject.addTableHeaderColumn("Pass %");
			oResultSubReportObject.endTableRow();

			oResultSubReportObject.endTableHeader();

			oResultSubReportObject.startTableBody("center");

			oResultSubReportObject.startTableRow("E0EDF0");

			oResultSubReportObject.addTableColumn(
					String.valueOf(iTotal_TestCases), "center");
			oResultSubReportObject.addTableColumn(
					String.valueOf(iTotal_PCount), "center");
			oResultSubReportObject.addTableColumn(
					String.valueOf(iTotal_SCount), "center");
			oResultSubReportObject.addTableColumn(
					"<a href=\"wd_FailedTestCases.html\">"
							+ String.valueOf(iTotal_FCount) + "</a>", "center");
			oResultSubReportObject.addTableColumn(
					String.valueOf(iTotal_ECount), "center");

			double percent = (((double)iTotal_PCount+(double) iTotal_SCount)/(double)iTotal_TestCases)*100;

			oResultSubReportObject.addTableColumn(String.valueOf(percent),
					"center");
			oResultSubReportObject.endTableRow();
			oResultSubReportObject.endTableBody();
			oResultSubReportObject.endTable();

			oResultSubReportObject.addBreak();
			oResultSubReportObject.addBreak();

			oResultSubReportObject.startCenter();
			oResultSubReportObject.insertInBold("Individual Test Results",
					"small");
			oResultSubReportObject.endCenter();

			oResultSubReportObject.startTable("center", "800");

			oResultSubReportObject.startTableHeader("center");

			oResultSubReportObject.startTableRow("93BFFF");

			oResultSubReportObject.addTableHeaderColumn("Index");
			oResultSubReportObject.addTableHeaderColumn("Component Name");
			oResultSubReportObject.addTableHeaderColumn("Total Test cases");
			oResultSubReportObject.addTableHeaderColumn("Passed");
			oResultSubReportObject.addTableHeaderColumn("Skipped");
			oResultSubReportObject.addTableHeaderColumn("Failed");
			oResultSubReportObject.addTableHeaderColumn("Manual Test Cases");
			oResultSubReportObject.addTableHeaderColumn("Error");

			oResultSubReportObject.endTableRow();

			oResultSubReportObject.endTableHeader();

			oResultSubReportObject.startTableBody("center");

			for (iForLoop = 0; iForLoop < iNumberOfFiles; iForLoop++) {

				String sFileName = getHtmlFileName(iForLoop, oAllHtmlFiles);
				
				oResultSubReportObject.startTableRow("E0EDF0");
				oResultSubReportObject.addTableColumn(String
						.valueOf((iForLoop + 1)));
				oResultSubReportObject.addTableColumn("<a href=" + sFileName
						+ ".html target=\"in2\" >" + sFileName + "</a>");
				oResultSubReportObject.addTableColumn(String.valueOf(oArgs
						.get(iForLoop)[4]));
				oResultSubReportObject.addTableColumn(String.valueOf(oArgs
						.get(iForLoop)[0]));
				oResultSubReportObject.addTableColumn(String.valueOf(oArgs
						.get(iForLoop)[5]));
				oResultSubReportObject.addTableColumn(String.valueOf(oArgs
						.get(iForLoop)[1]));
				oResultSubReportObject.addTableColumn(String.valueOf(oArgs
						.get(iForLoop)[3]));
				oResultSubReportObject.addTableColumn(String.valueOf(oArgs
						.get(iForLoop)[2]));
				oResultSubReportObject.endTableRow();
			}
			oResultSubReportObject.endTableBody();
			oResultSubReportObject.endTable();
			oResultSubReportObject.endBody();
			oResultSubReportObject.endHtml();
			oResultSubReportObject.closeWriter();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// Method to get the current time in our formatted way

	// This method will create the FailedTestCases.html file
	/**
	 * 
	 * @Author Vinay Tangella
	 * @Description This method will create the FailedTestCases.html file.
	 */
	public static void create_FailedResultsFile(
			HashMap<String, ArrayList<String>> oFailedTestCases) {

		try {
			int iForLoop, iNumberOfFailures;

			iNumberOfFailures = oFailedTestCases.size();

			com.jda.qa.platform.reports.ResultSubReport oResultSubReportObject = new ResultSubReport(
					sFolderLocation + "\\Html_Reports\\wd_FailedTestCases.html");

			oResultSubReportObject.startHtml();

			oResultSubReportObject.startBody(sHeader_BGColor,
					sHeader_FontFamily, sHeader_FontSize);

			oResultSubReportObject.addBreak();
			oResultSubReportObject.addBreak();

			oResultSubReportObject.addBreak();

			oResultSubReportObject.startTable("center", "800");

			oResultSubReportObject.startTableHeader("center");

			oResultSubReportObject.startTableRow("93BFFF");

			oResultSubReportObject.addTableHeaderColumn("Test Suite Name");
			oResultSubReportObject.addTableHeaderColumn("Test Case ID");
			oResultSubReportObject.addTableHeaderColumn("Description");
			oResultSubReportObject.addTableHeaderColumn("Comment");
			oResultSubReportObject.endTableRow();
			oResultSubReportObject.endTableHeader();

			oResultSubReportObject.startTableBody("center");

			oResultSubReportObject.startTableRow("E0EDF0");

			oResultSubReportObject.startTableBody("center");

			for (iForLoop = 0; iForLoop < iNumberOfFailures; iForLoop++) {

				String sTestSuiteName = oFailedTestCases.get(
						keys.get(iForLoop).toString()).get(0);
				String sTestCaseName = oFailedTestCases.get(
						keys.get(iForLoop).toString()).get(1);
				String sDescription = oFailedTestCases.get(
						keys.get(iForLoop).toString()).get(2);
				String sComment = oFailedTestCases.get(
						keys.get(iForLoop).toString()).get(3);

				oResultSubReportObject.startTableRow("E0EDF0");
				oResultSubReportObject.addTableColumn(sTestSuiteName);
				oResultSubReportObject.addTableColumn(sTestCaseName,true);
				oResultSubReportObject.addTableColumn(sDescription,true);
				oResultSubReportObject.addTableColumn(sComment,true);
				oResultSubReportObject.endTableRow();
			}
			oResultSubReportObject.endTableBody();
			oResultSubReportObject.endTable();
			oResultSubReportObject.endBody();
			oResultSubReportObject.endHtml();
			oResultSubReportObject.closeWriter();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	// This method will give the time stamp
	/**
	 * 
	 * @Author Vinay Tangella
	 * @Description This method will generates the time stamp
	 */
	public static String getTimeStamp() {
		try {
			Date oDate = new Date();

			SimpleDateFormat oDateFormat = new SimpleDateFormat(
					"dd_MMM_YYYY-hh:mm:ssa", Locale.ENGLISH);// a here is to
																// mention the
																// am pm
			String sStamp;

			sStamp = oDateFormat.format(oDate);
			return sStamp;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}

	}

	// returns only the file name of the HTML results file generated

	// This method will return the html file name

	/**
	 * 
	 * @Author Vinay Tangella
	 * @Description This method will return the html file name
	 * 
	 */
	public static String getHtmlFileName(int index,
			ArrayList<String> oAllHtmlFiles) {
		try {
			File oFile = new File(oAllHtmlFiles.get(index));
			String sFileName = oFile.getName();
			String sOnlyFileName = sFileName.substring(0,
					(sFileName.length() - 5));

			return sOnlyFileName;
		} catch (Throwable t) {
			System.out.println(t.getMessage());
			return null;
		}
	}
	
	public static void generateResultsProperties() {
		String resultsPropFile = sFolderLocation + "\\results.properties";

		Properties resultsProp = new Properties();

		File resultsFile = new File(resultsPropFile);

		try {
			if (resultsFile.exists()) {
				resultsFile.delete();
			}
			resultsFile.createNewFile();

			resultsProp.put("PASSED", iTotal_PCount + "");
			resultsProp.put("FAILED", iTotal_FCount + "");
			resultsProp.put("TOTAL", iTotal_TestCases + "");
			resultsProp.put("SKIPPED", iTotal_SCount + "");
			resultsProp.put("ERROR", iTotal_ECount + "");
			resultsProp.put("runType", MainEngine.runType);

			resultsProp.store(new FileOutputStream(resultsFile),
					"Updated the Results Properties File");

		} catch (Throwable t) {
			System.out.println(t.getMessage());
		}

	}

}

// --------------------------------End of the
// file-------------------------------------//
