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

import com.jda.qa.platform.framework.base.MainEngine;
import com.jda.qa.platform.framework.browser.TestBrowser;
import com.jda.qa.platform.framework.locator.LocatorFormatter;

public class ParameterWorkbenchTable implements TableInterface {

	WebDriver driver;
	
	String gridType="Rule";
	
	String tablePkColHdrLoc;
	String tableNonPkColHdrLoc;
	
	int tablePkColCount;
	int tableNonPkColCount;
	
	String tablePkRowLoc;
	String tableNonPkRowLoc;

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

	ArrayList<String> tablePkColHdrNames = new ArrayList<String>();
	ArrayList<String> tableNonPkColHdrNames = new ArrayList<String>();
	ArrayList<String> tableColTypes = new ArrayList<String>();

	HashMap<String, String> objectDetails = new HashMap<String, String>();

	ArrayList<String> colContentList = new ArrayList<String>();

	public ParameterWorkbenchTable(TestBrowser browser, String tableProps,
			String dataSheetName, String pkValue, String tableFrame) {
		this(browser, tableProps, "Contents.xlsx", dataSheetName, pkValue,
				tableFrame);
	}

	public ParameterWorkbenchTable(TestBrowser browser, String tableProps,
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
		
		if(tableProperties.getProperty("gridType")!=null){
			gridType = tableProperties.getProperty("gridType");
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
		
		tablePkRowLoc="//table[@id='"+gridType+"GridRowHeader']/descendant::tr[contains(@class,'data')]";
		tableNonPkRowLoc="//table[@id='"+gridType+"GridData']/descendant::tr[contains(@class,'data')]";
		
		// Primary Keys Columns
		tablePkColHdrLoc= "//table[@id='"+gridType+"GridCorner']/tbody/tr[2]/td[contains(@class,'columnHeader') and contains(@class,'vpNoWrap')]";
		
		// Non-Primary Keys Columns
		tableNonPkColHdrLoc= "//table[@id='"+gridType+"GridColumnHeader']/tbody/tr[2]/td[contains(@class,'columnHeader') and contains(@class,'vpNoWrap')]";

		switchtoTableFrame();
		
		browser.sleep(1000);

		// Get PK Col Headers Count
		tablePkColCount = browser.getXpathCount(tablePkColHdrLoc);
		
		// Get Non-PK Col Headers Count
		tableNonPkColCount = browser.getXpathCount(tableNonPkColHdrLoc);
		
		// Get the Row Count	
		tableRowCount = browser.getXpathCount(tablePkRowLoc);
		
		// Get PK Col Names
		for (int colCount = 1; colCount <= tablePkColCount; colCount++) {

			String tablePkColNameLoc = tablePkColHdrLoc + "[" + colCount + "]";
			
			scrollIntoCellView(tablePkColNameLoc);

			String colName = browser.getText(tablePkColNameLoc,true);

			tablePkColHdrNames.add(colName);

		}

		// Get Non-PK Col Names
		for (int colCount = 1; colCount <= tableNonPkColCount; colCount++) {

			String tableNonPkColNameLoc = tableNonPkColHdrLoc + "[" + colCount + "]";
			
			scrollIntoCellView(tableNonPkColNameLoc);

			String colName = browser.getText(tableNonPkColNameLoc,true);

			tableNonPkColHdrNames.add(colName);

		}

		browser.switchToDefaultContent();

	}

	public void doTableAction(String colName) {

		int rowNum = getTableRowNumber(objectDetails.get(tablePkColDBName));

		doTableAction(rowNum, colName);

	}

	public void doTableAction(int rowNum, String colName) {
		
		String tableCellLoc = "";
		
		
		
		if (tablePkColHdrNames.contains(colName)
				|| colName.equalsIgnoreCase("edit")
				|| colName.equalsIgnoreCase("cascade") || (gridType.equalsIgnoreCase("RULE") && colName.equalsIgnoreCase("copy"))) {
			
			if (tablePkColHdrNames.contains(colName)) {
				int index = tablePkColHdrNames.indexOf(colName) + 2;
				tableCellLoc = tablePkRowLoc + "[" + rowNum + "]/td[" + index + "]";
			} else if (colName.equalsIgnoreCase("edit")) {
				tableCellLoc = tablePkRowLoc + "[" + rowNum
						+ "]/descendant::img[contains(@onclick,'edit')]";
			} else if (colName.equalsIgnoreCase("cascade")) {
				tableCellLoc = tablePkRowLoc + "[" + rowNum
						+ "]/descendant::img[contains(@onclick,'show')]";
			}else if (colName.equalsIgnoreCase("copy")) {
				tableCellLoc = tablePkRowLoc + "[" + rowNum
						+ "]/descendant::img[contains(@onclick,'copy')]";
			}
			
		} else{
			int index = tableNonPkColHdrNames.indexOf(colName);
			tableCellLoc = tableNonPkRowLoc + "[" + rowNum + "]/td[" + index + "]";
		}
		
		switchtoTableFrame();
		
		if (!browser.isElementPresent(tableCellLoc, true)) {
			throw new WebDriverException("Not a valid Table Action");
		}
		
		String attribute = browser.getAttribute(tableCellLoc, "onclick");


		

		if (attribute != null) {
			browser.click(tableCellLoc);

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

		if (tablePkColHdrNames.contains(colHdrName)) {
			
			
			int index = getTableColumnNumber(colHdrName) + 2;
			
			if (gridType.equalsIgnoreCase("RuleReport")) {
				index = index-2;
			}

			colContentLoc = tablePkRowLoc + "[" + rowNum + "]" + "/td[" + index
					+ "]";
		} else {
			colContentLoc = tableNonPkRowLoc + "[" + rowNum + "]" + "/td["
					+ getTableColumnNumber(colHdrName) + "]";
		}

		((JavascriptExecutor) driver).executeScript(
				"arguments[0].scrollIntoView();",
				driver.findElement(By.xpath(colContentLoc)));

		return browser.getText(colContentLoc, true);
	}

	@Override
	public boolean validateTableCellContent(int rowNum, String colHdrName,
			String colContent) {

		boolean compStatus = false;

		switchtoTableFrame();

		String actColContent = getTableCellContent(rowNum, colHdrName);

		if (actColContent.equals(LocatorFormatter.formatLocator(colContent))) {
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

		browser.switchToDefaultContent();
		
		return compStatus;
	}

	@Override
	public int getTableRowNumber(String pkColValue) {

		String colContent = "";
		switchtoTableFrame();
		int rowNum = 1;
		boolean foundStatus = false;
		
		for (; rowNum <= tableRowCount; rowNum++) {
			
			int index = getTableColumnNumber(pkColumnName) + 2;
			
			if (gridType.equalsIgnoreCase("RuleReport")) {
				index = index-2;
			}

			String rowLoc = tablePkRowLoc + "[" + rowNum + "]" + "/td["+index+"][contains(@style,'text-align:left')]";
			
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();",
					driver.findElement(By.xpath(rowLoc)));
			
			colContent = browser.getText(rowLoc,true);

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
			String selRowLoc = tablePkRowLoc + "[" + rowNum + "]/td[1]/input";

			scrollIntoCellView(selRowLoc);

			if (!browser.isChecked(selRowLoc)) {
				browser.click(selRowLoc);
			} else {
				MainEngine.log
						.info("Row already selected, hence skipping the row selection");
			}

		}

		browser.switchToDefaultContent();

	}

	public void switchtoTableFrame() {

		browser.setDefaultFrame();

		browser.selectFrame(tableFrame);

	}

	public int getTableColumnNumber(String colHdrName) {

		if (tablePkColHdrNames.contains(colHdrName)) {
			return tablePkColHdrNames.indexOf(formatResourceValue(colHdrName)) + 1;
		} else {
			return tableNonPkColHdrNames
					.indexOf(formatResourceValue(colHdrName)) + 1;
		}

	}
	
	public void scrollIntoCellView(String cellLocator) {
		((JavascriptExecutor) driver).executeScript(
				"arguments[0].scrollIntoView();",
				driver.findElement(By.xpath(cellLocator)));
	}

}
