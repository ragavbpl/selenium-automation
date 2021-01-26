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
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.jda.qa.platform.framework.browser.TestBrowser;
import com.jda.qa.platform.framework.locator.LocatorFormatter;

public class ConditionalFormatTable implements TableInterface,
        InlineEditTableInterface {

	WebDriver driver;

	String tableProps;
	String dataSheetName;
	String excelFileName;
	String pkValue;

	Properties tableProperties = new Properties();
	TestBrowser browser;
	String tableFrame;
	String pkColumnName;
	List<String> pkColumnNames;
	int pkColNo;
	boolean hasFreezedColumn = true;

	String tablePkColDBName;
	List<String> tablePkColDBNames;

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

	String gridName = "FEDataFormatGrid";

	String tablegridLoc = "//div[@id='" + gridName + "']";

	String tableColCntLoc = tablegridLoc + "/descendant::div[@id='" + gridName
			+ "_tc']/descendant::tbody[@id='" + gridName + "2IE']/tr[2]/td";
	String tableRowCntLoc = tablegridLoc + "/descendant::div[@id='" + gridName
			+ "_c']/descendant::tbody[@id='" + gridName + "5IE']/tr";
	String tableRowCntLocLeft = tablegridLoc + "/descendant::div[@id='"
			+ gridName + "_l']/descendant::tbody[@id='" + gridName + "4IE']/tr";

	public ConditionalFormatTable(TestBrowser browser, String tableProps,
			String dataSheetName, String pkValue, String tableFrame) {
		this(browser, tableProps, "Contents.xlsx", dataSheetName, pkValue,
				tableFrame);
	}

	public ConditionalFormatTable(TestBrowser browser, String tableProps,
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

		if (tableFrame.contains(">")) {
			frames = tableFrame.split(">");
		} else {
			frames = new String[] { tableFrame };
		}

		pkColumnName = formatResourceValue(tableProperties
				.getProperty("pkColName"));

		// To Handle Multiple Primary Key Columns

		if (pkColumnName.contains(",")) {
			String[] vals = pkColumnName.split(",");

			pkColumnNames = Arrays.asList(vals);

			pkColumnName = pkColumnNames.get(0);
		}

		tablePkColDBName = tableProperties.getProperty("pkDBColValue");

		if (tablePkColDBName.contains(",")) {
			String[] vals = tablePkColDBName.split(",");

			tablePkColDBNames = Arrays.asList(vals);

			tablePkColDBName = tablePkColDBNames.get(0);
		}

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

		tableColHdrNames = new ArrayList<String>();

		driver = (WebDriver) browser.getWebDriver();

		switchtoTableFrame();

		browser.sleep(1000);

		if (!browser.isElementPresent(tablegridLoc, true)) {
			gridName = "FEColumnFiltersGrid";

			tablegridLoc = "//div[@id='" + gridName + "']";

			tableColCntLoc = tablegridLoc + "/descendant::div[@id='" + gridName
					+ "_tc']/descendant::tbody[@id='" + gridName
					+ "2IE']/tr[2]/td";
			tableRowCntLoc = tablegridLoc + "/descendant::div[@id='" + gridName
					+ "_c']/descendant::tbody[@id='" + gridName + "5IE']/tr";
			tableRowCntLocLeft = tablegridLoc + "/descendant::div[@id='"
					+ gridName + "_l']/descendant::tbody[@id='" + gridName
					+ "4IE']/tr";
		}

		tableColCount = browser.getXpathCount(tableColCntLoc);
		tableRowCount = browser.getXpathCount(tableRowCntLoc);

		// Add other column names

		for (int colCount = 1; colCount <= tableColCount; colCount++) {

			String tableColNameLoc = tableColCntLoc + "[" + colCount + "]";

			scrollIntoCellView(tableColNameLoc);

			String colName = browser.getText(tableColNameLoc);

			tableColHdrNames.add(colName);

		}

		pkColNo = tableColHdrNames.indexOf(pkColumnName) + 1;

	}

	@Override
	public void doTableAction(int rowNum, String colName) {

		String tableCellLoc = tableRowCntLoc + "[" + rowNum + "]/td["
				+ getTableColumnNumber(colName) + "]";
		String tableCellLocLink = tableCellLoc + "/descendant::a";
		String tableCellLocChkBox = tableCellLoc + "/input[@type='checkbox']";

		switchtoTableFrame();

		if (browser.isElementPresent(tableCellLocLink, true)) {
			browser.click(tableCellLocLink);
			browser.waitForPageToLoad("20000");
		} else if (browser.isElementPresent(tableCellLocChkBox, true)) {

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
	public void doTableAction(String colHdrName) {

		int rowNum = getTableRowNumber(objectDetails.get(tablePkColDBName));

		doTableAction(rowNum, colHdrName);

	}

	@Override
	public String getTableCellContent(int rowNum, String colHdrName) {
		String colContentLoc = "";
		switchtoTableFrame();

		// Skip the row as the first row is empty
		colContentLoc = tableRowCntLoc + "[" + rowNum + "]" + "/td["
				+ getTableColumnNumber(colHdrName) + "]";

		scrollIntoCellView(colContentLoc);

		if (browser.isElementPresent(
				colContentLoc + "/input[@type='checkbox']", true)) {
			boolean checkedStatus = browser.isChecked(colContentLoc
					+ "/input[@type='checkbox']");

			if (checkedStatus) {
				return "CHECK";
			} else {
				return "UNCHECK";
			}

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

		if (actColContent.trim().equals(colContent.replace("value:", ""))) {
			compStatus = true;
		}

		return compStatus;
	}

	@Override
	public int getTableRowNumber(String pkColValue) {
		String colContent = "";
		switchtoTableFrame();
		int rowNum = 2;
		boolean foundStatus = false;

		if (pkColumnNames.size() == 1) {
			for (; rowNum < tableRowCount + 2; rowNum++) {

				colContent = getTableCellContent(rowNum, pkColumnName);

				if (colContent.trim().equals(pkColValue)) {
					foundStatus = true;
					break;
				}
			}
		} else if (pkColumnNames.size() > 1) {

			// iterate through rows

			for (; rowNum < tableRowCount + 2; rowNum++) {

				// iterate through columns

				ArrayList<String> actualColContent = new ArrayList<String>();

				ArrayList<String> expectedColContent = new ArrayList<String>();

				for (int colIndex = 0; colIndex < pkColumnNames.size(); colIndex++) {

					actualColContent.add(getTableCellContent(rowNum,
							pkColumnNames.get(colIndex)).toString().trim());

					expectedColContent.add(objectDetails.get(
							tablePkColDBNames.get(colIndex)).trim());

				}
				actualColContent.removeAll(expectedColContent);

				if (actualColContent.size() == 0) {
					foundStatus = true;
					break;
				}

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
			String selRowLoc = tableRowCntLocLeft + "[" + rowNum + "]/td[2]";

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

				compStatus = validateTableCellContent(rowNum,
						tableColNames.get(index),
						LocatorFormatter.formatLocator(colContentList
								.get(index)));

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

	public void setTableCellContent(int rowNum, String colHdrName,
			String colContent) {

		String colContentLoc = "";

		switchtoTableFrame();

		colContentLoc = tableRowCntLoc + "[" + rowNum + "]" + "/td["
				+ getTableColumnNumber(colHdrName) + "]";

		scrollIntoCellView(colContentLoc);

		String cellClass = browser.getAttribute(colContentLoc, "class");

		if (cellClass.contains("editabledata")) {

			Actions act = (Actions) browser.getActions();

			WebElement colElem = driver.findElement(By.xpath(colContentLoc));
			
			browser.moveToElement(colElem);

			act.doubleClick(colElem).build().perform();

			String textFieldLoc = "//span[contains(@style,'display: block;') and contains(@id,'"
					+ gridName + "_e')]/input";
			String selectLoc = "//span[contains(@style,'display: block;') and contains(@id,'"
					+ gridName + "_e')]/select";

			// Handle Text Field

			if (browser.isElementPresent(textFieldLoc, true)) {

				browser.clear(textFieldLoc);
				browser.type(textFieldLoc, colContent);

				// Handle Select Tag
			} else if (browser.isElementPresent(selectLoc, true)) {

				browser.select(selectLoc, colContent);

			}

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

	public void scrollIntoCellView(String cellLocator) {
		((JavascriptExecutor) driver).executeScript(
				"arguments[0].scrollIntoView();",
				driver.findElement(By.xpath(cellLocator)));
	}

}
