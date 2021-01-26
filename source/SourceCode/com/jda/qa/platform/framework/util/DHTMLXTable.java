package SourceCode.com.jda.qa.platform.framework.util;

import static com.jda.qa.platform.framework.locator.LocatorFormatter.formatResourceValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.jda.qa.platform.framework.util.ExcelData;
import com.jda.qa.platform.framework.util.FileUtils;
import com.jda.qa.platform.framework.util.InlineEditTableInterface;
import com.jda.qa.platform.framework.util.TableInterface;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import com.jda.qa.platform.framework.base.MainEngine;
import com.jda.qa.platform.framework.browser.TestBrowser;
import com.jda.qa.platform.framework.locator.LocatorFormatter;

public class DHTMLXTable implements TableInterface, InlineEditTableInterface {

	WebDriver driver;

	String tableProps;
	String dataSheetName;
	String excelFileName;
	String pkValue;

	Properties tableProperties = new Properties();
	TestBrowser browser;
	String tableFrame;
	String sColName, sSortOrder;
	String pkColumnName;
	int pkColNo;
	boolean hasFreezedColumn = true;

	String tablePkColDBName;

	List<String> tableColNames = new ArrayList<String>();
	List<String> tableColDBNames = new ArrayList<String>();

	int tableRowCount = 0;
	int tableColCount = 0;

	ArrayList<String> tableColHdrNames = new ArrayList<String>();
	ArrayList<String> tablePkColHdrNames = new ArrayList<String>();
	ArrayList<String> tableColTypes = new ArrayList<String>();

	HashMap<String, String> objectDetails = new HashMap<String, String>();

	ArrayList<String> colContentList = new ArrayList<String>();

	String LINK = "LINK";
	String CHECK_BOX = "CHECK_BOX";
	String PLAIN_TEXT = "PLAIN_TEXT";

	String tablegridLoc = "//div[contains(@id,'GridDiv')]";

	// Columns - Freezed Columns Header Names location
	String tablePkColCountLoc = tablegridLoc
			+ "/div[contains(@id,'cgrid2')]/div[@class='xhdr']/descendant::tr[2]/td";
	String tableColCountLoc = tablegridLoc
			+ "/div[2]/div[@class='xhdr']/descendant::tr[2]/td";

	// Columns - Non-Freezed Columns Header Names location
	String tablePkColCountLocNF = tablegridLoc
			+ "/div[contains(@id,'cgrid2')]/div[@class='xhdr']/descendant::tr[2]/td";
	String tableColCountLocNF = tablegridLoc
			+ "/div[@class='xhdr']/descendant::tr[2]/td";

	// Rows - Freezed Column 
	String tableRowLoc = tablegridLoc
			+ "/div[2]/div[@class='objbox']/descendant::tbody/tr";
	String tablePkRowLoc = tablegridLoc
			+ "/div[contains(@id,'cgrid2')]/div[@class='objbox']/descendant::tbody/tr";

	String tableRowCountLoc = tableRowLoc + "[contains(@class,'gridRow')]";
	String tablePkRowCountLoc = tablePkRowLoc + "[contains(@class,'gridRow')]";

	// Rows - Non-Freezed Column
	String tableRowLocNF = tablegridLoc
			+ "/div[@class='objbox']/descendant::tbody/tr";
	String tablePkRowLocNF = tablegridLoc
			+ "/div[@class='objbox']/descendant::tbody/tr";

	String tableRowCountLocNF = tableRowLocNF + "[contains(@class,'gridRow')]";
	String tablePkRowCountLocNF = tablePkRowLocNF
			+ "[contains(@class,'gridRow')]";

	public DHTMLXTable(TestBrowser browser, String tableFrame) {
		this.browser = browser;
		this.tableFrame = tableFrame;
	}

	public DHTMLXTable(TestBrowser browser, String tableProps,
			String dataSheetName, String pkValue, String tableFrame) {
		this(browser, tableProps, "Contents.xlsx", dataSheetName, pkValue,
				tableFrame);
	}

	public DHTMLXTable(TestBrowser browser, String tableProps,
			String excelFileName, String dataSheetName, String pkValue,
			String tableFrame) {
		super();
		this.browser = browser;
		this.tableFrame = tableFrame;
		this.tableProps = tableProps;
		this.dataSheetName = dataSheetName;
		this.pkValue = pkValue;

		try {
	tableProperties.load(new FileInputStream(new File(FileUtils
					.getFileLocation("locators", tableProps))));
		} catch (IOException e) {
			e.printStackTrace();
		}

		pkColumnName = formatResourceValue(tableProperties
				.getProperty("pkColName"));
		tablePkColDBName = tableProperties.getProperty("pkDBColValue");

		tableColNames = Arrays.asList(tableProperties.get("tableColList")
				.toString().split(","));
		tableColDBNames = Arrays.asList(tableProperties
				.get("tableDBColNamesList").toString().split(","));

		com.jda.qa.platform.framework.util.ExcelData data = new ExcelData(excelFileName, dataSheetName);
		objectDetails = data.getExcelData(pkValue);

		for (int index = 0; index < tableColDBNames.size(); index++) {
			colContentList.add(objectDetails.get(tableColDBNames.get(index)));
		}
	}

	@Override
	public void loadTable() {

		driver = (WebDriver) browser.getWebDriver();

		switchtoTableFrame();

		browser.select("//select[contains(@id,'PageSize')]", "250");

		browser.sleep(1000);

		hasFreezedColumn = browser.isElementPresent(tablegridLoc
				+ "/div[contains(@id,'cgrid2')]", true);

		if (hasFreezedColumn) {

			tableColCount = browser.getXpathCount(tableColCountLoc);
			tableRowCount = browser.getXpathCount(tableRowCountLoc);

			int tablePkColCount = browser.getXpathCount(tablePkColCountLoc);

			// Add the Primary Key Columns

			for (int colCount = 1; colCount <= tablePkColCount; colCount++) {

				String tableColNameLoc = tablePkColCountLoc + "[" + colCount
						+ "]/descendant::span";

				String colName = browser.getText(tableColNameLoc);

				tablePkColHdrNames.add(colName);

			}

			tableColHdrNames.addAll(tablePkColHdrNames);

			// Add other column names
			for (int colCount = 1 + tablePkColCount; colCount <= tableColCount; colCount++) {

				String tableColNameLoc = tableColCountLoc + "[" + colCount
						+ "]/descendant::span";

				String colName = browser.getText(tableColNameLoc);

				tableColHdrNames.add(colName);

			}

		} else {

			tableColCount = browser.getXpathCount(tableColCountLocNF);
			tableRowCount = browser.getXpathCount(tableRowCountLocNF);

			int tablePkColCount = browser.getXpathCount(tablePkColCountLocNF);

			// Add the Primary Key Columns

			for (int colCount = 1; colCount <= tablePkColCount; colCount++) {

				String tableColNameLoc = tablePkColCountLocNF + "[" + colCount
						+ "]/descendant::span";

				String colName = browser.getText(tableColNameLoc);

				tablePkColHdrNames.add(colName);

				pkColumnName = colName;

				pkColNo = tablePkColHdrNames.indexOf(pkColumnName) + 1;

			}

			tableColHdrNames.addAll(tablePkColHdrNames);

			// Add other column names
			for (int colCount = 1 + tablePkColCount; colCount <= tableColCount; colCount++) {

				String tableColNameLoc = tableColCountLocNF + "[" + colCount
						+ "]/descendant::span";

				String colName = browser.getText(tableColNameLoc);

				tableColHdrNames.add(colName);

			}

		}

		pkColNo = tableColHdrNames.indexOf(pkColumnName) + 1;

	}

	@Override
	public void doTableAction(int rowNum, String colHdrName) {

	}
	
	public void doTableAction(String colName) {

		int rowNum = getTableRowNumber(colName);

		doTableAction(rowNum, colName);

	}

	@Override
	public String getTableCellContent(int rowNum, String colHdrName) {

		String colContentLoc = "";
		switchtoTableFrame();

		// Skip the row as the first row is empty
		rowNum = rowNum + 1;

		if (hasFreezedColumn) {

			if (tablePkColHdrNames.contains(formatResourceValue(colHdrName))) {
				colContentLoc = tablePkRowLoc + "[" + rowNum + "]" + "/td["
						+ getTableColumnNumber(colHdrName) + "]";
			} else {
				colContentLoc = tableRowLoc + "[" + rowNum + "]" + "/td["
						+ getTableColumnNumber(colHdrName) + "]";
			}

		} else {

			if (tablePkColHdrNames.contains(formatResourceValue(colHdrName))) {
				colContentLoc = tablePkRowLocNF + "[" + rowNum + "]" + "/td["
						+ getTableColumnNumber(colHdrName) + "]";
			} else {
				colContentLoc = tableRowLocNF + "[" + rowNum + "]" + "/td["
						+ getTableColumnNumber(colHdrName) + "]";
			}
		}

		String checkBoxLoc = colContentLoc + "/input[@type='checkbox']";
		if (browser.isElementPresent(checkBoxLoc, true)) {
			return browser.isSelected(checkBoxLoc) + "";
		} else {
		return browser.getText(colContentLoc);
		}
	}

	@Override
	public boolean validateTableCellContent(int rowNum, String colHdrName,
			String colContent) {

		boolean compStatus = false;

		switchtoTableFrame();

		String actColContent = getTableCellContent(rowNum, colHdrName);

		if (actColContent.equalsIgnoreCase(colContent)) {
			compStatus = true;
		}else{
			MainEngine.log
			.info("Actual Content : "+actColContent);
			MainEngine.log
			.info("Expected Content : "+colContent);
		}

		return compStatus;
	}

	@Override
	public boolean valiadateTableRowContent() {

		boolean compStatus = true;

		switchtoTableFrame();

		int rowNum = getTableRowNumber(objectDetails.get(tablePkColDBName));

		if (rowNum != 0) {

			for (int index = 0; index < tableColNames.size(); index++) {

				compStatus = validateTableCellContent(rowNum,
						tableColNames.get(index), colContentList.get(index));

				if (!compStatus) {
					break;
				}

			}
		} else {
			compStatus = false;
		}

		return compStatus;
	}

	@Override
	public int getTableRowNumber(String pkColValue) {

		String colContent = "";
		switchtoTableFrame();
		int rowNum = 2;
		boolean foundStatus = false;
		String rowCheckBoxCheckLoc ="";

		for (; rowNum <= tableRowCount + 1; rowNum++) {

			String rowLoc = "";

			int val = pkColNo + 2;

			if (hasFreezedColumn) {
				colContent = browser.getText(tablePkRowLoc + "[" + rowNum + "]"
						+ "/td[" + pkColNo + "]");
				rowLoc = tableRowLoc + "[" + rowNum + "]" + "/td[" + val + "]";
				rowCheckBoxCheckLoc = tableRowLoc + "[" + rowNum + "]" + "/td[" + val + "]/input[@type='checkbox']";
			} else {
				colContent = browser.getText(tablePkRowLocNF + "[" + rowNum
						+ "]" + "/td[" + pkColNo + "]");
				rowLoc = tableRowLocNF + "[" + rowNum + "]" + "/td[" + val
						+ "]";
				rowCheckBoxCheckLoc = tableRowLocNF + "[" + rowNum + "]" + "/td[" + val
						+ "]/input[@type='checkbox']";
			}
			
			//Check for checkbox
			
			if(browser.isElementPresent(rowCheckBoxCheckLoc, true)){
				
					browser.click(rowCheckBoxCheckLoc);
					browser.sleep(1000);
					browser.click(rowCheckBoxCheckLoc);
				
			}else{
			browser.click(rowLoc);
			}

			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();",
					driver.findElement(By.xpath(rowLoc)));

			if (colContent.equals(pkColValue)) {
				foundStatus = true;
				break;
			}
		}

		if (foundStatus) {
			return --rowNum;
		} else {
			return 0;
		}
	}

	public int getTableColumnNumber(String colHdrName) {

		return tableColHdrNames.indexOf(formatResourceValue(colHdrName)) + 1;

	}

	public void switchtoTableFrame() {

		browser.setDefaultFrame();

		browser.selectFrame(tableFrame);

	}

	public void selectTableRow() {

		int rowNum = 0;

		switchtoTableFrame();

		rowNum = getTableRowNumber(objectDetails.get(tablePkColDBName));

		if (rowNum != 0) {

			rowNum = rowNum + 1;

			String tableRowSelectLoc = "";

			if (hasFreezedColumn) {
				tableRowSelectLoc = tableRowLoc + "[" + rowNum + "]/td";
			} else {
				tableRowSelectLoc = tableRowLocNF + "[" + rowNum + "]/td";
			}

			browser.click(tableRowSelectLoc);
		} else {
			throw new WebDriverException("Failed to Select the Table Row");
		}

	}

	// This will return a boolean value based on the comparison of the values in
	// the given column name

	public boolean verifySortOnTableData(String sColName, String sSortOrder) {
		try {

			int iFailCount = 0;

			ArrayList<String> oColumnData = new ArrayList<String>();
			ArrayList<String> oAscData = new ArrayList<String>();

			driver = (WebDriver) browser.getWebDriver();

			browser.select("//select[contains(@id,'PageSize')]", "250");

			browser.sleep(1000);

			hasFreezedColumn = browser.isElementPresent(tablegridLoc
					+ "/div[contains(@id,'cgrid2')]");

			if (hasFreezedColumn && tablePkColHdrNames.contains(sColName)) {

				tableColCount = browser.getXpathCount(tableColCountLoc);
				tableRowCount = browser.getXpathCount(tableRowCountLoc);

				int tablePkColCount = browser.getXpathCount(tablePkColCountLoc);

				for (int colCount = 1; colCount <= tablePkColCount; colCount++) {

					String tableColNameLoc = tablePkColCountLoc + "["
							+ colCount + "]/descendant::span";

					String sPkColName = browser.getText(tableColNameLoc);

					if (sPkColName.equalsIgnoreCase(sColName)) {

						if (sSortOrder.equalsIgnoreCase("Des")) {

							browser.click(tablePkColCountLoc);
						}
						break;
					}
				}

				int iTotalRowCount = browser.getXpathCount(tableRowLoc);

				for (int iForLoop = 2; iForLoop <= iTotalRowCount; iForLoop++) {

					String colContentLoc = tablePkRowLoc + "[" + iForLoop + "]";

					browser.click(colContentLoc);

					((JavascriptExecutor) driver).executeScript(
							"arguments[0].scrollIntoView();",
							driver.findElement(By.xpath(colContentLoc)));

					oColumnData.add(browser.getText(colContentLoc));

				}
			} else {

				int tableNonPkColCount = browser
						.getXpathCount(tableColCountLoc);

				for (int colCount = 2; colCount <= tableNonPkColCount; colCount++) {

					String tableColNameLoc = tableColCountLoc + "[" + colCount
							+ "]/descendant::span";

					String sNonPkColName = browser.getText(tableColNameLoc);

					if (sNonPkColName.equalsIgnoreCase(sColName)) {

						if (sSortOrder.equalsIgnoreCase("Des")) {

							browser.click(tableColNameLoc);
							browser.click(tableColNameLoc);
						}
						
						if (sSortOrder.equalsIgnoreCase("Asc")) {

							browser.click(tableColNameLoc);
							
						}
						
						break;
					}
				}

				int iTotalRowCount = browser.getXpathCount(tableRowLoc);

				for (int iForLoop = 2; iForLoop < iTotalRowCount; iForLoop++) {

					String colContentLoc = tableRowLoc + "[" + iForLoop
							+ "]/td[" + getTableColumnNumber(sColName) + "]";

					browser.click(tablePkRowLoc + "[" + iForLoop + "]");

					((JavascriptExecutor) driver).executeScript(
							"arguments[0].scrollIntoView();",
							driver.findElement(By.xpath(colContentLoc)));

					oColumnData.add(browser.getText(colContentLoc));
				}
			}

			oAscData.addAll(oColumnData);

			Collections.sort(oAscData);

			for (int iForLoop = 0; iForLoop < oAscData.size(); iForLoop++) {

				if (oAscData.get(iForLoop).toString()
						.equals(oColumnData.get(iForLoop).toString())) {

				} else if (sSortOrder.equalsIgnoreCase("Des")) {

				} else {
					iFailCount++;
				}

			}

			if (iFailCount > 0) {
				return false;// If the sorting order is descending
			} else {
				return true;// sorting order is ascending
			}

		} catch (Exception e) {
			e.getMessage();
			return false;
		}

	}

	public void setTableCellContent(int rowNum, String colHdrName,
			String colContent) {
		String colContentLoc = "";
		switchtoTableFrame();

		// Skip the row as the first row is empty
		rowNum = rowNum + 1;
		if (hasFreezedColumn) {
			if (tablePkColHdrNames.contains(formatResourceValue(colHdrName))) {
				colContentLoc = tablePkRowLoc + "[" + rowNum + "]" + "/td["
						+ getTableColumnNumber(colHdrName) + "]";
			} else {
				colContentLoc = tableRowLoc + "[" + rowNum + "]" + "/td["
						+ getTableColumnNumber(colHdrName) + "]";
			}
		} else {
			if (tablePkColHdrNames.contains(formatResourceValue(colHdrName))) {
				colContentLoc = tablePkRowLocNF + "[" + rowNum + "]" + "/td["
						+ getTableColumnNumber(colHdrName) + "]";
			} else {
				colContentLoc = tableRowLocNF + "[" + rowNum + "]" + "/td["
						+ getTableColumnNumber(colHdrName) + "]";
			}
		}
		String checkBoxLoc = colContentLoc + "/input[@type='checkbox']";
		String textBoxLoc = colContentLoc + "/input[@type='text']";
		if (browser.isElementPresent(checkBoxLoc, true)) {
			boolean status = browser.isChecked(checkBoxLoc);
			if (status) {
				if (colContent.equalsIgnoreCase("UNCHECK")
						|| colContent.equalsIgnoreCase("FALSE")) {
					browser.click(checkBoxLoc);
				}
			} else {
				if (colContent.equalsIgnoreCase("CHECK")
						|| colContent.equalsIgnoreCase("TRUE")) {
					browser.click(checkBoxLoc);
				}
			}
		} else {
			if (browser.isElementPresent(textBoxLoc, true)) {
				browser.clear(textBoxLoc);
				browser.type(textBoxLoc, colContent);
			} else {
				browser.highlightBorder(colContentLoc);
				MainEngine.log.info("No Action Performed");
			}
			browser.click(colContentLoc);
		}
	}
	public void setTableRowContent() {
		switchtoTableFrame();
		int rowNum = getTableRowNumber(objectDetails.get(tablePkColDBName));
		if (rowNum != 0) {
			for (int index = 0; index < tableColNames.size(); index++) {
				if (tableColNames.get(index).trim().length() > 0) {
					setTableCellContent(rowNum, tableColNames.get(index),
							LocatorFormatter.formatLocator(colContentList
									.get(index)));
				}
			}
		} else {
			throw new WebDriverException("Failed to perform the inline Edit");
		}
	}
}
