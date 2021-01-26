package SourceCode.com.jda.qa.platform.framework.util;

import static com.jda.qa.platform.framework.locator.LocatorFormatter.formatResourceValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.jda.qa.platform.framework.util.ExcelData;
import com.jda.qa.platform.framework.util.FileUtils;
import com.jda.qa.platform.framework.util.InlineEditTableInterface;
import com.jda.qa.platform.framework.util.TableInterface;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import com.jda.qa.platform.framework.base.MainEngine;
import com.jda.qa.platform.framework.browser.TestBrowser;
import com.jda.qa.platform.framework.locator.LocatorFormatter;

public class FilterCriteriaTable implements TableInterface, InlineEditTableInterface {

	WebDriver driver;

	String tableProps;
	String dataSheetName;
	String excelFileName;
	String pkValue;

	Properties tableProperties = new Properties();
	TestBrowser browser;
	String tableFrame;
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

	String[] frames;

	String tablegridLoc = "//table[@id='tblUpdttblefltrDsply' or @id='tblUpdatefltrDisplay']";

	String tableColCntLoc = tablegridLoc
			+ "/descendant::td[@class='j-grid-column-header' or @class='columnHeader vpNoWrap']";
	String tableRowCntLoc = tablegridLoc
			+ "/descendant::tr[@class='j-grid-auto-checkbook' or @class='data checkbookOff' or @class='data checkbookOn']";

	public FilterCriteriaTable(TestBrowser browser, String tableProps, String dataSheetName, String pkValue,
			String tableFrame) {
		this(browser, tableProps, "Contents.xlsx", dataSheetName, pkValue, tableFrame);
	}

	public FilterCriteriaTable(TestBrowser browser, String tableProps, String excelFileName, String dataSheetName,
			String pkValue, String tableFrame) {
		super();
		this.browser = browser;
		this.tableFrame = tableFrame;
		this.tableProps = tableProps;
		this.dataSheetName = dataSheetName;
		this.pkValue = pkValue;

		try {
			tableProperties.load(new FileInputStream(new File(FileUtils.getFileLocation("locators", tableProps))));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (tableFrame.contains(">")) {
			frames = tableFrame.split(">");
		} else {
			frames = new String[] { tableFrame };
		}

		pkColumnName = formatResourceValue(tableProperties.getProperty("pkColName"));
		tablePkColDBName = tableProperties.getProperty("pkDBColValue");

		tableColNames = Arrays.asList(tableProperties.get("tableColList").toString().split(","));
		tableColDBNames = Arrays.asList(tableProperties.get("tableDBColNamesList").toString().split(","));

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

		browser.sleep(1000);

		tableColCount = browser.getXpathCount(tableColCntLoc);
		tableRowCount = browser.getXpathCount(tableRowCntLoc);

		// Add other column names

		for (int colCount = 1; colCount <= tableColCount; colCount++) {

			String tableColNameLoc = tableColCntLoc + "[" + colCount + "]";

			String colName = browser.getText(tableColNameLoc);

			tableColHdrNames.add(colName);

		}

		pkColNo = tableColHdrNames.indexOf(pkColumnName) + 1;

	}

	@Override
	public void doTableAction(int rowNum, String colName) {

		String colContentLoc = "";
		switchtoTableFrame();

		// Skip the row as the first row is empty
		colContentLoc = tableRowCntLoc + "[" + rowNum + "]" + "/td[" + getTableColumnNumber(colName) + "]";

		String checkBoxLoc = colContentLoc + "/input[@type='CHECKBOX']";

		if (browser.isElementPresent(checkBoxLoc, true)) {
			browser.click(checkBoxLoc);
		} else {
			browser.highlight(colContentLoc);
		}

		browser.switchToDefaultContent();

	}

	@Override
	public void doTableAction(String colHdrName) {

		int rowNum = getTableRowNumber(objectDetails.get(tablePkColDBName));

		doTableAction(rowNum, colHdrName);

	}

	@Override
	public String getTableCellContent(int rowNum, String colHdrName) {
		String colContentLoc = "";
		switchtoTableFrame();

		// Skip the row as the first row is empty
		colContentLoc = tableRowCntLoc + "[" + rowNum + "]" + "/td[" + getTableColumnNumber(colHdrName) + "]";

		String selectColContentLoc = colContentLoc + "/select";
		String inputColContentLoc = colContentLoc + "/descendant::input[@type='text']";

		if (browser.isElementPresent(selectColContentLoc, true)) {

			String[] values = browser.getSelectedLabels(selectColContentLoc);
			return values[0];

		} else if (browser.isElementPresent(inputColContentLoc, true)) {
			return browser.getAttribute(inputColContentLoc, "value");
		} else {
			return browser.getText(colContentLoc);
		}

	}

	@Override
	public boolean validateTableCellContent(int rowNum, String colHdrName, String colContent) {
		boolean compStatus = false;

		switchtoTableFrame();

		if (colContent.equalsIgnoreCase("SKIP")) {

			compStatus = true;

		} else {

			String actColContent = getTableCellContent(rowNum, colHdrName);

			if (actColContent.trim().equals(colContent.replace("value:", ""))) {
				compStatus = true;
			}

			if (!compStatus) {
				MainEngine.log.info("Failed validating the column content : ");
				MainEngine.log.info("Actual : " + actColContent);
				MainEngine.log.info("Expected : " + colContent);
			}
		}

		return compStatus;
	}

	@Override
	public int getTableRowNumber(String pkColValue) {
		String colContent = "";
		switchtoTableFrame();
		int rowNum = 1;
		boolean foundStatus = false;

		for (; rowNum < tableRowCount + 2; rowNum++) {

			String rowLoc = tableRowCntLoc + "[" + rowNum + "]" + "/td[" + pkColNo + "]";

			colContent = browser.getText(rowLoc);

			if (colContent.trim().equals(pkColValue)) {
				foundStatus = true;
				break;
			}
		}

		if (foundStatus) {
			return rowNum;
		} else {
			return 0;
		}
	}

	@Override
	public void selectTableRow() {

		int rowNum = getTableRowNumber(objectDetails.get(tablePkColDBName));

		if (rowNum == 0) {
			throw new WebDriverException("Table row not found");

		} else {
			String selRowLoc = tableRowCntLoc + "[" + rowNum + "]/td[1]/input";

			browser.click(selRowLoc);
		}
	}

	@Override
	public boolean valiadateTableRowContent() {

		boolean compStatus = true;

		switchtoTableFrame();

		int rowNum = getTableRowNumber(objectDetails.get(tablePkColDBName));

		if (rowNum != 0) {

			for (int index = 0; index < tableColNames.size(); index++) {

				compStatus = validateTableCellContent(rowNum, tableColNames.get(index),
						LocatorFormatter.formatLocator(colContentList.get(index)));

				if (!compStatus) {
					break;
				}

			}
		} else {
			compStatus = false;
		}

		return compStatus;
	}

	public void switchtoTableFrame() {

		browser.setDefaultFrame();

		browser.selectFrames(frames);

	}

	public int getTableColumnNumber(String colHdrName) {

		return tableColHdrNames.indexOf(formatResourceValue(colHdrName)) + 1;

	}

	@Override
	public void setTableCellContent(int rowNum, String colHdrName, String colContent) {

		if (colContent.equalsIgnoreCase("SKIP")) {

		} else {

			String colContentLoc = "";
			switchtoTableFrame();

			// Skip the row as the first row is empty
			colContentLoc = tableRowCntLoc + "[" + rowNum + "]" + "/td[" + getTableColumnNumber(colHdrName) + "]";

			String selectColContentLoc = colContentLoc + "/select";
			String inputColContentLoc = colContentLoc + "/descendant::input[@type='text']";

			if (browser.isElementPresent(selectColContentLoc, true)) {
				browser.addSelection(selectColContentLoc, colContent);
			} else if (browser.isElementPresent(inputColContentLoc, true)) {
				browser.removeAttribute(inputColContentLoc, "readonly");
				browser.setAttribute(inputColContentLoc, "value", colContent);
			} else {
				browser.highlight(colContent);
			}
		}

	}

	@Override
	public void setTableRowContent() {

		switchtoTableFrame();

		int rowNum = getTableRowNumber(objectDetails.get(tablePkColDBName));

		if (rowNum != 0) {

			for (int index = 0; index < tableColNames.size(); index++) {

				if (LocatorFormatter.formatResourceValue(tableColNames.get(index)).equals(pkColumnName)) {
					continue;
				}

				if (tableColNames.get(index).trim().length() > 0) {
					setTableCellContent(rowNum, tableColNames.get(index),
							LocatorFormatter.formatLocator(colContentList.get(index)));
				}

			}
		} else {
			throw new WebDriverException("Failed to perform the inline Edit");
		}
	}

}
