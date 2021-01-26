package SourceCode.com.jda.qa.platform.framework.util;

import static com.jda.qa.platform.framework.locator.LocatorFormatter.formatResourceValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.jda.qa.platform.framework.util.ExcelData;
import com.jda.qa.platform.framework.util.FileUtils;
import com.jda.qa.platform.framework.util.MultiSelectTableInterface;
import com.jda.qa.platform.framework.util.TableInterface;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import com.jda.qa.platform.framework.base.MainEngine;
import com.jda.qa.platform.framework.browser.TestBrowser;
import com.jda.qa.platform.framework.locator.LocatorFormatter;

public class ParameterGroupsTable implements MultiSelectTableInterface, TableInterface {

//Class objects
	WebDriver driver;
	TestBrowser browser;

//Variables
	String[] sFrames;
	String[] sPKMultiValues;
	int iRowCount = 0;
	int iColcount = 0;
	int iPKColNum;
	
	String sTableFrame;
	String sTableProps;
	String sDataSheetName;
	String sTablePkCol_DBName;
	String sPKValue;
	
	String sTable_ColsHeader_Location;
	String sTable_Content_ColLocation;
	String sTable_Content_RowLocation;
	String sTable_Checkbox_RowLocation;
		
	Properties pTableProperties = new Properties();

//Collection Arrays
	ArrayList<String> oColHeaders = new ArrayList<String>();
	List<String> oPKColumnName = new ArrayList<String>();
	List<String> oTableColNames = new ArrayList<String>();
	HashMap<String, Integer> oColHdrNames = new HashMap<String, Integer>();
	
	
	LinkedHashMap<String, HashMap<String, String>> objectDetailsItr= new LinkedHashMap<String, HashMap<String, String>>();
	LinkedHashMap<String, ArrayList<String>> colContentListItr= new LinkedHashMap<String, ArrayList<String>>();
	LinkedHashMap<String, HashMap<String, String>> colforSelectionItr=new LinkedHashMap<String, HashMap<String, String>>();
	
	HashMap<String, String> objectDetails = new HashMap<String, String>();
	HashMap<String, String> colPosition = new HashMap<String, String>();
	HashMap<String, String> colforSelection = new HashMap<String, String>();
	List<String> oTableColNames_DB = new ArrayList<String>();
	ArrayList<String> colContentList = new ArrayList<String>();
	
	public ParameterGroupsTable (TestBrowser browser, String sTableProps,
			String sDataSheetName, String sPKValue, String sTableFrame) {
		this(browser, sTableProps, "Contents.xlsx", sDataSheetName, sPKValue,
				sTableFrame);
	}
	
	public ParameterGroupsTable (TestBrowser browser, String sTableProps,
			String excelFileName, String sDataSheetName, String sPKValue,
			String sTableFrame) {
		super();
		this.browser = browser;
		this.sTableFrame = sTableFrame;
		this.sTableProps = sTableProps;
		this.sDataSheetName = sDataSheetName;
		this.sPKValue = sPKValue;
		try {
			pTableProperties.load(new FileInputStream(new File(FileUtils
					.getFileLocation("locators", sTableProps))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (sTableFrame.contains(">")) {
			sFrames = sTableFrame.split(">");
		} else {
			sFrames = new String[] { sTableFrame };
		}
		
		sTable_ColsHeader_Location=pTableProperties.getProperty("sTable_ColsHeader_Location");
		sTable_Content_ColLocation=pTableProperties.getProperty("sTable_Content_ColLocation");
		sTable_Content_RowLocation=pTableProperties.getProperty("sTable_Content_RowLocation");
		sTable_Checkbox_RowLocation=pTableProperties.getProperty("sTable_Checkbox_RowLocation");
		
		for (int i=0;i<pTableProperties.getProperty("pkColNames").toString().split(",").length;i++)
		{
			oPKColumnName.add(i, formatResourceValue(pTableProperties.getProperty("pkColNames").toString().split(",")[i]));
		}
		
		//sPKColumnName =formatResourceValue(pTableProperties.getProperty("pkColName"));
		sTablePkCol_DBName = pTableProperties.getProperty("pkDBColValue");
		for(int j=0;j<pTableProperties.get("tableColList").toString().split(",").length;j++)
		{
			oTableColNames.add(j, formatResourceValue(pTableProperties.get("tableColList").toString().split(",")[j]));			
		}
		oTableColNames_DB = Arrays.asList(pTableProperties.get("tableDBColNamesList").toString().split(","));
		com.jda.qa.platform.framework.util.ExcelData oData = new ExcelData(excelFileName, sDataSheetName);
		if (sPKValue.contains(":multiple"))
		{			
			String sPKValues=sPKValue.replaceAll(":multiple", "");
			sPKMultiValues=sPKValues.split(":");
		}
		else
		{
				sPKMultiValues=sPKValue.split(":");
		}
		for(String sPKMultiValue:sPKMultiValues)
		{
			objectDetails = oData.getExcelData(sPKMultiValue);
			objectDetailsItr.put(sPKMultiValue, objectDetails);
			ArrayList<String> colContentList1 = new ArrayList<String>();
			for (int index = 0; index < oTableColNames_DB.size(); index++) {
				colContentList1.add(objectDetails.get(oTableColNames_DB.get(index)));

			}
			colContentList=colContentList1;
			colContentListItr.put(sPKMultiValue, colContentList);				
			
			HashMap<String, String> colforSelection1= new HashMap<String, String>();
			for(int i=0;i<sTablePkCol_DBName.split(",").length;i++)
			{
				colforSelection1.put(Integer.toString(i),objectDetails.get(sTablePkCol_DBName.split(",")[i]));
				colforSelection=colforSelection1;
				colforSelectionItr.put(sPKMultiValue, colforSelection);
			}			
		}		
	}
	
	
	public void loadTable() {
		driver = (WebDriver) browser.getWebDriver();
		switchtoTableFrame();
		browser.sleep(1000);
		iColcount = browser.getXpathCount(sTable_Content_ColLocation);
		iRowCount = browser.getXpathCount(sTable_Content_RowLocation);
		oColHdrNames = getTableColHeaders();
		
		for(int i=0;i<oPKColumnName.size();i++)
		{
			colPosition.put(Integer.toString(i), oColHdrNames.get(oPKColumnName.get(i)).toString());			
		}
	}
	
	
	public HashMap<String, Integer> getTableColHeaders() {
		driver = (WebDriver) browser.getWebDriver();
		int iCount;
		iCount = browser.getXpathCount(sTable_ColsHeader_Location);
		for (int i = 1; i <= iCount; i++) {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();",
					driver.findElement(By.xpath("("+sTable_ColsHeader_Location+")"
							+ "[" + (i) + "]")));
			oColHdrNames.put(browser.getText("("+sTable_ColsHeader_Location+")" + "[" + (i) + "]").trim(), i - 1);
			oColHeaders.add(browser.getText("("+sTable_ColsHeader_Location+")" + "[" + (i) + "]").trim());
		}		
		return oColHdrNames;
	}
	
	public void selectTableRow(){
		//Please use selectTableRows, this can perform single and multi row selection. 
	}

	public void selectTableRows() {
		int selectionCount = 0;
		if (colforSelectionItr.keySet().isEmpty() != true) {
			Set<String> keyValues = colforSelectionItr.keySet();

			for (String val : keyValues) {
				this.colforSelection = colforSelectionItr.get(val);

				for (String Vals : colforSelection.keySet()) {
					if (colforSelection.get(Vals).toUpperCase().equals("SKIP")) {
						++selectionCount;
					}
				}
				if (colforSelection.size() != selectionCount) {
					int iRow = getTableRowNumber(sTablePkCol_DBName);
					if (iRow == -1) {
						throw new WebDriverException("Table row not found");
					} else {

						((JavascriptExecutor) driver).executeScript(
								"arguments[0].scrollIntoView();",
								driver.findElement(By
										.xpath(sTable_Checkbox_RowLocation
												+ "[" + (iRow) + "]/input")));

						String checkboxLoc = sTable_Checkbox_RowLocation + "["
								+ (iRow) + "]/input";
						
						if(!browser.isChecked(checkboxLoc)){
							browser.click(checkboxLoc);
						}else{
							MainEngine.log
							.info("Row already selected, hence skipping the row selection");
						}
					}
				}
			}
		}
	
		browser.switchToDefaultContent();
	}
	
	public void switchtoTableFrame() {
		browser.setDefaultFrame();
		browser.selectFrames(sFrames);
	}	

	
	
	public int getTableRowNumber(String sTablePkCol_DBName) {

		String colContent = "";
		switchtoTableFrame();
		int iRow = 1;
		int iCol;
		boolean foundStatus = false;

		for (; iRow <= iRowCount; iRow++) {

			iCol = Integer.parseInt(colPosition.get("0"));
			String sCellLocation = sTable_Content_RowLocation + "[" + (iRow)
					+ "]/td[" + (iCol + 1) + "]";

			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();",
					driver.findElement(By.xpath(sCellLocation)));

			colContent = browser.getText(sCellLocation);

			if (colContent.trim().equals(colforSelection.get("0"))) {
				foundStatus = true;

				for (int j = 1; j <= colPosition.size() - 1; j++) {
					iCol = Integer
							.parseInt(colPosition.get(Integer.toString(j)));
					sCellLocation = sTable_Content_RowLocation + "[" + (iRow)
							+ "]/td[" + (iCol + 1) + "]";
					((JavascriptExecutor) driver).executeScript(
							"arguments[0].scrollIntoView();",
							driver.findElement(By.xpath(sCellLocation)));
					colContent = browser.getText(sCellLocation);
					if (!colContent.trim().equals(
							colforSelection.get(Integer.toString(j)))) {
						foundStatus = false;
						break;
					} else
						foundStatus = true;

				}
			}
			if (foundStatus) {
				break;
			}
		}
		if (!foundStatus)
			return -1;
		else
			return iRow;
	}
	
	public int getTableColumnNumber(String sColHdr_Name) {

		return (oColHdrNames.get(LocatorFormatter
				.formatResourceValue(sColHdr_Name))) + 1;

	}
	
	public void doTableAction(String sColHdr_Name) {

		int iRow = getTableRowNumber(objectDetails.get(sTablePkCol_DBName));

		if (iRow == -1) {
			throw new WebDriverException("Table row not found");
		} else {
			doTableAction(iRow, sColHdr_Name);
		}

	}

	public void doTableAction(int iRow, String sColHdr_Name) {

		String tableCellLoc = null;

		tableCellLoc = sTable_Content_RowLocation + "[" + iRow + "]/td["
				+ getTableColumnNumber(sColHdr_Name) + "]";

		switchtoTableFrame();

		System.out.println("Normal Text Cell");
		browser.highlightBorder(tableCellLoc);

		browser.switchToDefaultContent();

	}

	
	public boolean valiadateTableRowsContent() {

		switchtoTableFrame();
		String isCheckedElement;
		Boolean selectionStatus = true;

		Set<String> keyValues = colContentListItr.keySet();
		for (String val : keyValues) {
			this.colContentList = colContentListItr.get(val);
			int rowNum = getTableRowNumber(sTablePkCol_DBName);

			if (rowNum != -1) {
				((JavascriptExecutor) driver).executeScript(
						"arguments[0].scrollIntoView();",
						driver.findElement(By.xpath(sTable_Content_RowLocation
								+ "[" + (rowNum) + "]/td[1]")));

				isCheckedElement = sTable_Content_RowLocation + "[" + (rowNum)
						+ "]//td[1]//input";
				if (!browser.isChecked(isCheckedElement)) {
					selectionStatus = false;
					break;
				}

			} else {
				throw new WebDriverException(
						"Failed to valiadate the row content");
			}
		}
		return selectionStatus;
	}
	

	/* The below methods are not implemented yet, On need basis
	 * valiadateTableRowContent, getTableCellContent, validateTableCellContent, doTableAction 
	 */		
	public boolean valiadateTableRowContent() {
		return true;
	}
	public String getTableCellContent(int iRow, String sColHdr_Name){
		return "Nothing";
	}
	public boolean validateTableCellContent(int iRow, String sColHdr_Name,String sColContent){
		return true;
	}
}
