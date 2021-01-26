package SourceCode.com.jda.qa.platform.framework.util;

import com.jda.qa.platform.framework.base.MainEngine;
import com.jda.qa.platform.framework.util.ExcelReader;

/**
 * 
 * @author Vinay Tangella & Santosh Medchalam
 * @Description This class contains methods to compare two excel sheets of same
 *              excel file.
 *
 */
public class compareExcelSheets {

	Boolean bStatus = false;

	String actualExcelFile, expectedExcelFile, actualSheetName,
			expectedSheetName;

	String sTestDir = System.getProperty("user.dir") + "\\fedatavalidation\\";

	public compareExcelSheets(String actualExcelFile, String actualSheetName,
			String expectedExcelFile, String expectedSheetName) {
		this.actualExcelFile = actualExcelFile;
		this.actualSheetName = actualSheetName;
		this.expectedSheetName = expectedSheetName;
		this.expectedExcelFile = expectedExcelFile;
	}

	/**
	 * 
	 * @author Vinay Tangella & Santosh Medchalam
	 * @Description This method compares two excel sheets of same excel file and
	 *              return the boolean value
	 *
	 */
	public boolean compareSheets() {

		int iRowCount_Sheet1, iColCount_Sheet1;
		int iRowCount_Sheet2, iColCount_Sheet2;
		int iFailCount = 0;

		com.jda.qa.platform.framework.util.ExcelReader actualExcelFileReader = new com.jda.qa.platform.framework.util.ExcelReader(sTestDir
				+ actualExcelFile + ".xlsx");
		com.jda.qa.platform.framework.util.ExcelReader expectedExcelFileReader = new ExcelReader(sTestDir
				+ expectedExcelFile + ".xlsx");

		iRowCount_Sheet1 = actualExcelFileReader.getRowCount(actualSheetName);
		iColCount_Sheet1 = actualExcelFileReader
				.getColumnCount(actualSheetName);

		iRowCount_Sheet2 = expectedExcelFileReader
				.getRowCount(expectedSheetName);
		iColCount_Sheet2 = expectedExcelFileReader
				.getColumnCount(expectedSheetName);

		if (iRowCount_Sheet1 != iRowCount_Sheet2
				|| iColCount_Sheet1 != iColCount_Sheet2) {
			return false;
		} else {
			for (int iRowNum = 1; iRowNum < iRowCount_Sheet1; iRowNum++) {
				for (int iColNum = 1; iColNum < iColCount_Sheet1; iColNum++) {

					if (actualExcelFileReader.getCellData(actualSheetName,
							iColNum, iRowNum).equals(
							expectedExcelFileReader.getCellData(
									expectedSheetName, iColNum, iRowNum))) {

					} else {
						iFailCount++;
						MainEngine.log
								.info("The data in column number: "
										+ (iColNum + 1)
										+ "and Row number : "
										+ (iRowNum)
										+ " of sheet :"
										+ actualSheetName
										+ " does not match with the data in column number: "
										+ (iColNum + 1) + "and Row number : "
										+ (iRowNum) + " of sheet :"
										+ expectedSheetName);
						MainEngine.log.info(actualExcelFileReader.getCellData(
								actualSheetName, iColNum, iRowNum));
						MainEngine.log.info(expectedExcelFileReader
								.getCellData(expectedSheetName, iColNum,
										iRowNum));
					}
				}
			}

			if (iFailCount >= 1) {
				actualExcelFileReader = null;
				return false;

			} else {
				actualExcelFileReader = null;
				return true;
			}
		}

	}

}
