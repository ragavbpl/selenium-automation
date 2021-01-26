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
import com.jda.qa.platform.framework.util.TableInterface;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import com.jda.qa.platform.framework.browser.TestBrowser;

public class BasicTable implements TableInterface {

	WebDriver driver;

	String tableLoc;
	String tableRowCountLoc;
	String tableColCountLoc;
	String tableRowLoc;
	String tableColLoc;

	int rowCount;
	int colCount;

	String tableProps;
	String dataSheetName;
	String excelFileName;
	String pkValue;

	Properties tableProperties = new Properties();
	TestBrowser browser;
	String tableFrame;
	String pkColumnName;
	int pkColNo;

	String tablePkColDBName;

	List<String> tableColNames = new ArrayList<String>();
	List<String> tableColDBNames = new ArrayList<String>();

	int tableRowCount = 0;
	int tableColCount = 0;

	ArrayList<String> tableColHdrNames = new ArrayList<String>();
	ArrayList<String> tableColTypes = new ArrayList<String>();

	HashMap<String, String> objectDetails = new HashMap<String, String>();

	ArrayList<String> colContentList = new ArrayList<String>();

	String LINK = "LINK";
	String CHECK_BOX = "CHECK_BOX";
	String PLAIN_TEXT = "PLAIN_TEXT";

	public BasicTable(TestBrowser browser, String tableProps,
			String dataSheetName, String pkValue, String tableFrame) {
		this(browser, tableProps, "Contents.xlsx", dataSheetName, pkValue,
				tableFrame);
	}

	public BasicTable(TestBrowser browser, String tableProps,
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

	public void loadTable() {

		driver = (WebDriver) browser.getWebDriver();

		switchtoTableFrame();

		tableLoc = "//table[@id='userTable']";
		tableRowCountLoc = tableLoc
				+ "/descendant::table[@id='userTable_data']/tbody/tr";
		tableColCountLoc = tableLoc
				+ "/descendant::table[@id='userTable_header']/tbody/tr/td";

		browser.sleep(1000);

		tableColCount = browser.getXpathCount(tableColCountLoc);

		String rowCntText = browser
				.getText("//*[@id='userTable']/div/div[1]/table/tbody/tr/td[2]");

		String[] vals = rowCntText.split(" ");

		tableRowCount = Integer.parseInt(vals[vals.length - 1]);

		for (int colCount = 1; colCount <= tableColCount; colCount++) {

			String tableColNameLoc = tableColCountLoc + "[" + colCount + "]";
			
			scrollIntoCellView(tableColNameLoc);

			String colName = browser.getText(tableColNameLoc);

			tableColHdrNames.add(colName);

		}

		pkColNo = tableColHdrNames.indexOf(pkColumnName) + 2;

		browser.switchToDefaultContent();

	}

	public void doTableAction(String colName) {

		int rowNum = getTableRowNumber(pkColumnName);

		doTableAction(rowNum, colName);

	}

	public void doTableAction(int rowNum, String colName) {

		String tableCellLoc = tableRowCountLoc + "[" + rowNum + "]/td["
				+ tableColNames.indexOf(colName) + "]";
		String tableCellLocLink = tableCellLoc + "/a";
		String tableCellLocChkBox = tableCellLoc + "/input[@type='checkbox']";

		browser.selectFrame(tableFrame);

		if (browser.isElementPresent(tableCellLocLink)) {
			browser.click(tableCellLocLink);
			browser.waitForPageToLoad("20000");
		} else if (browser.isElementPresent(tableCellLocChkBox)) {

			if (!browser.isChecked(tableCellLocChkBox)) {
				browser.click(tableCellLocChkBox);
			}

		} else {
			System.out.println("Normal Text Cell");
			browser.highlightBorder(tableCellLoc);
		}

		browser.switchToDefaultContent();

	}

	@Override
	public String getTableCellContent(int rowNum, String colHdrName) {

		String colContentLoc = "";
		switchtoTableFrame();
		
		// Skip the row as the first row is empty
		colContentLoc = tableRowCountLoc + "[" + rowNum + "]" + "/td["
				+ getTableColumnNumber(colHdrName) + "]";

		((JavascriptExecutor) driver).executeScript(
				"arguments[0].scrollIntoView();",
				driver.findElement(By.xpath(colContentLoc)));

		return browser.getText(colContentLoc);
	}

	@Override
	public boolean validateTableCellContent(int rowNum, String colHdrName,
			String colContent) {

		boolean compStatus = false;

		switchtoTableFrame();

		String actColContent = getTableCellContent(rowNum, colHdrName);

		if (actColContent.equals(colContent)) {
			compStatus = true;
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
		int rowNum = 1;
		boolean foundStatus = false;

		for (; rowNum < tableRowCount + 1; rowNum++) {

			String rowLoc = tableRowCountLoc + "[" + rowNum + "]" + "/td["
					+ pkColNo + "]";

			colContent = browser.getText(rowLoc);

			browser.click(rowLoc);

			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();",
					driver.findElement(By.xpath(rowLoc)));

			if (colContent.equals(pkColValue)) {
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
			String selRowLoc = tableLoc
					+ "/descendant::table[@id='userTable_data']/tbody/tr["
					+ rowNum + "]/td[1]";

			browser.click(selRowLoc);
		}

	}

	public void switchtoTableFrame() {

		browser.setDefaultFrame();

		browser.selectFrame(tableFrame);

	}

	public int getTableColumnNumber(String colHdrName) {

		return tableColHdrNames.indexOf(formatResourceValue(colHdrName)) + 2;

	}
	
	public void scrollIntoCellView(String cellLocator) {
		((JavascriptExecutor) driver).executeScript(
				"arguments[0].scrollIntoView();",
				driver.findElement(By.xpath(cellLocator)));
	}

}
