	package SourceCode.com.jda.qa.platform.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.jda.qa.platform.framework.util.ExcelData;
import com.jda.qa.platform.framework.util.ExcelReader;
import com.jda.qa.platform.framework.util.FileUtils;
import com.jda.qa.platform.framework.util.InlineEditTableInterface;
import com.jda.qa.platform.framework.util.TableInterface;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.Actions;

import com.jda.qa.platform.data.database.DBConnection;
import com.jda.qa.platform.framework.base.MainEngine;
import com.jda.qa.platform.framework.browser.TestBrowser;
import com.jda.qa.platform.framework.locator.LocatorFormatter;

/**
 * @Author Vinay Tangella
 * 
 * @Description This class implements TableInterface and TableInterface. This
 *              class is specially to handle the Vp gird table for example FE
 *              and CWS instances
 *              
 *@PreRequisites using this method - As this method is primarily used to perform actions on the cells in the Flexible Editor,
 *most of the FE's have different primary keys in-order to maintain the flow and to avoid the absolute dynamic of the column names
 *one has to set the column display names prior to performing any actions.
 *
 *   The same names have to be mentioned in the corresponding Contents, Table files.
 *   
 *   The primary keys should be prefixed with * symbol in the FE table properties file.
 */

public class VPTableCascaded implements TableInterface, InlineEditTableInterface {

	WebDriver driver;

	String sFileName;
	String sTableProps;
	String sDataSheetName;
	String sExcelFileName;
	String sPKValue;

	Properties pTableProperties = new Properties();
	TestBrowser oBrowser;
	String sTableFrame;
	String sPKColumnName;
	int iPKColNum;
	boolean bHasFreezedColumn = true;
	String cwsTabNumber = "7_2_2_2";

	ArrayList<String> oColHeaders = new ArrayList<String>();

	String[] sFrames;

	String sTablePkCol_DBName;

	// These are the names of the column header names displayed in the FE
	List<String> oTableColNames = new ArrayList<String>();

	// This variable stores the multiple primary keys
	List<String> oPKColumnName = new ArrayList<String>();

	// These are the equivalent DB names in the Excel or DB
	List<String> oTableColNames_DB = new ArrayList<String>();

	int iRowCount = 0;
	int iColcount = 0;
	int iRecordCount = 0;

	// locator of the number of records in the FE
	String sRecordCount = "//*[@id='FEGRID_"+cwsTabNumber+"_recordCount']";

	HashMap<String, Integer> oColHdrNames = new HashMap<String, Integer>();

	HashMap<String, String> objectDetails = new HashMap<String, String>();

	HashMap<String, String> colContentList = new HashMap<String, String>();
	
	HashMap<String, String> colContentList_EntireRow = new HashMap<String, String>();

	// Locator of the col headers of the FE Table - this is used to get the row
	// count
	String sTable_ColsHeader_Location = "//*[@id='FEGRID_"+cwsTabNumber+"2IE']/tr[2]/td";

	// Locator of the col header text location
	String sTable_ColHeader_Content_Location = "//*[@id='FEGRID_"+cwsTabNumber+"_-1";

	// Locator of the CheckBox and the Actions icon
	String sFE_Actions_Location = "//*[@id='FEGRID_"+cwsTabNumber+"4IE']";

	// Locator of the first row of sSelect-Actions_Location
	String sTable_Actions_RowLocation = sFE_Actions_Location + "/tr";

	// Locator of the Actual Data in the table
	String sTable_Content_Location = "//*[@id='FEGRID_"+cwsTabNumber+"5']";

	// Locator of the first row of sTable_Content_Location
	String sTable_Content_RowLocation = "//*[@id='FEGRID_"+cwsTabNumber+"5IE']/tr";

	// Locator of the first column of sTable_Content_Location
	String sTable_Content_ColLocation = "//*[@id='FEGRID_"+cwsTabNumber+"5IE']/tr[1]/td";	
	

	/**
	 * @Author Vinay Tangella
	 * @Description Constructor of the class VPTable
	 */
	public VPTableCascaded(TestBrowser oBrowser, String sTableProps,
			String sDataSheetName, String sPKValue, String sTableFrame) {
		this(oBrowser, sTableProps, "Contents.xlsx", sDataSheetName, sPKValue,
				sTableFrame);
	}

	// ----------------------------------------------------------------------------------------

	/**
	 * @Author Vinay Tangella
	 * @Description Constructor of the class VPTable
	 */

	public VPTableCascaded(TestBrowser oBrowser, String sTableProps,
			String excelFileName, String sDataSheetName, String sPKValue,
			String sTableFrame) {
		super();
		this.oBrowser = oBrowser;
		this.sTableFrame = sTableFrame;
		this.sTableProps = sTableProps;
		this.sDataSheetName = sDataSheetName;
		this.sPKValue = sPKValue;

		try {
			pTableProperties.load(new FileInputStream(new File(com.jda.qa.platform.framework.util.FileUtils
					.getFileLocation("locators", sTableProps))));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (sTableFrame.contains(">")) {
			sFrames = sTableFrame.split(">");
		} else {
			sFrames = new String[] { sTableFrame };
		}
		
		if (pTableProperties.getProperty("cwsTabNumber") != null) {
			this.cwsTabNumber = pTableProperties.getProperty("cwsTabNumber");
			
			// locator of the number of records in the FE
			sRecordCount = "//*[@id='FEGRID_"+cwsTabNumber+"_recordCount']";

			// Locator of the col headers of the FE Table - this is used to get the row
			// count
			sTable_ColsHeader_Location = "//*[@id='FEGRID_"+cwsTabNumber+"2IE']/tr[2]/td";

			// Locator of the col header text location
			sTable_ColHeader_Content_Location = "//*[@id='FEGRID_"+cwsTabNumber+"_-1";

			// Locator of the CheckBox and the Actions icon
			sFE_Actions_Location = "//*[@id='FEGRID_"+cwsTabNumber+"4IE']";

			// Locator of the first row of sSelect-Actions_Location
			sTable_Actions_RowLocation = sFE_Actions_Location + "/tr";

			// Locator of the Actual Data in the table
			sTable_Content_Location = "//*[@id='FEGRID_"+cwsTabNumber+"5']";

			// Locator of the first row of sTable_Content_Location
			sTable_Content_RowLocation = "//*[@id='FEGRID_"+cwsTabNumber+"5IE']/tr";

			// Locator of the first column of sTable_Content_Location
			sTable_Content_ColLocation = "//*[@id='FEGRID_"+cwsTabNumber+"5IE']/tr[1]/td";	
			
		}

		// If you have multiple primary keys - this variable holds all the names
		// of the primary keys
		oPKColumnName = Arrays.asList(pTableProperties
				.getProperty("pkColNames").toString().split(","));

		// This is the Column-Header name of the Primary Key in the Fe Table -
		// defined in properties file
		sPKColumnName = pTableProperties.getProperty("pkColName") != null ? LocatorFormatter
				.formatResourceValue(pTableProperties.getProperty("pkColName"))
				: "";

		// This is the equivalent Column-Header name of the Primary Key in the
		// Excel or DB - defined in properties file
		sTablePkCol_DBName = pTableProperties.getProperty("pkDBColValue");

		// These are the column header display names of displayed in the FE Grid
		// - defined in properties file
		oTableColNames = Arrays.asList(pTableProperties.get("tableColList")
				.toString().split(","));

		// These are the equivalent DB names in the Excel or DB - defined in the
		// properties file
		oTableColNames_DB = Arrays.asList(pTableProperties
				.get("tableDBColNamesList").toString().split(","));

		// if there are multiple primary keys then this variable holds all the
		// data of the

		// This returns an object with the data
		com.jda.qa.platform.framework.util.ExcelData oData = new ExcelData(excelFileName, sDataSheetName);
		objectDetails = oData.getExcelData(sPKValue);

		 /*This code has been added to the SCPO team code base*/
		
		  for (int index = 0; index < oTableColNames_DB.size(); index++) {
			  for(int index1=0;index1 < oPKColumnName.size();index1++){
			  
			  if(oTableColNames_DB.get(index).equalsIgnoreCase(oPKColumnName.get(index1).substring(1))){	 
				    
				  colContentList.put(oTableColNames.get(index),objectDetails.get(oPKColumnName.get(index1).substring(1)));
				  
				} 
			} 
		  	}
		  
		  for (int index = 0; index < oTableColNames_DB.size(); index++) {
			  for(int index1=0;index1 < oTableColNames.size();index1++){
			  
			  if(oTableColNames_DB.get(index).equalsIgnoreCase(oTableColNames.get(index1))){	 
				  String matchedKeyValue = getMatchedKeyValue(objectDetails, oTableColNames.get(index1));
				  if(matchedKeyValue != null) {
					  colContentList_EntireRow.put(oTableColNames.get(index),objectDetails.get(matchedKeyValue)); 
				  }
				} 
			} 
		  	}
			}
		 
		/*for (int index = 0; index < oTableColNames_DB.size(); index++) {
			colContentList.put(oTableColNames.get(index),
					objectDetails.get(oTableColNames_DB.get(index)));
		}
	}*/

	// ----------------------------------------------------------------------------------------

	public String getMatchedKeyValue(Map<String, String> mapObj, String keyVal) {		
		Set<String> keyValues = mapObj.keySet();
		for(String val : keyValues) {
			if(val.equalsIgnoreCase(keyVal)) {
				return val;
			}
		}
		return "";
	}
	/**
	 * @Author Vinay Tangella   & Santosh Medchalam
	 * @Description This method is used to get the basic information of the table
	 */

	@Override
	public void loadTable() {

		// compareExcelSheets oCE = new compareExcelSheets("BOM_data",
		// "FE_Data", "DB_Data");

		// System.out.println(oCE.compareSheets());
		
		System.out.println("");

		driver = (WebDriver) oBrowser.getWebDriver();

		switchtoTableFrame();

		oBrowser.sleep(1000);

		iColcount = oBrowser.getXpathCount(sTable_Content_ColLocation);
		iRowCount = oBrowser.getXpathCount(sTable_Content_RowLocation);
		iRecordCount = oBrowser.getXpathCount(sRecordCount);

		// verifyOptionStatus();

		if (oPKColumnName.size() > 1) {

			oColHdrNames = getTableColHeaders();
			
			//getTableRowNumber(objectDetails.get(sPKColumnName));
			
			//objectDetails.get(oTableColNames_DB.get(0));

			//iPKColNum = (oColHdrNames.get(oPKColumnName.get(0))) + 2;

		} else {
			// This will return a HashMap<String, Integer>
			oColHdrNames = getTableColHeaders();

			// This will return the index of the PK Col Header
			iPKColNum = oColHdrNames.get(sPKColumnName);
		}

	}

	// ----------------------------------------------------------------------------------------
	// This method is used to perform a context click or perform any other
	// operation like in-line edit etc in a cell in FE Grid

	/**
	 * @Author Vinay Tangella   & Santosh Medchalam 
	 * @Description This method is used to perform any action on a cell in the table. takes arguments row number and column header name
	 */	
	public void doTableAction(int iRow, String sColHdr_Name) {

		// String sTable_CellLoc = sTable_Content_RowLocation + "[" + (iRow+1) +
		// "]/td["
		// + (oColHdrNames.get(sColHdr_Name)+2) + "]";

		WebDriver driver;

		Boolean bStatus = false;

		String sColContent[];
		
		int iCol;

		driver = (WebDriver) oBrowser.getWebDriver();

		Actions oActions = (Actions) oBrowser.getActions();

		// oColHdrNames = getTableColHeaders();

		if (!sColHdr_Name.contains("ContextMenu")
				&& !sColHdr_Name.contains("Comment")) {

			sColContent = sColHdr_Name.split("-");
			String content=sColContent[1];
			
			for(int index=2;index<sColContent.length;index++){
				content = content+"-"+sColContent[index];
			}
			
			sColContent[1]= content;
			
			iCol = oColHdrNames.get(sColContent[0]);

			String sLocator = sTable_Content_RowLocation + "[" + (iRow + 1)
					+ "]/td[" + (iCol + 2) + "]";

			switchtoTableFrame();

			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();",
					driver.findElement(By.xpath(sLocator)));

			if (oBrowser.isElementPresent(sLocator
					+ "/child::input[@type='checkbox']", true)) {
				bStatus = Boolean.valueOf(oBrowser.getAttribute(sLocator
						+ "/child::input[@type='checkbox']", "value"));

				if (bStatus.equals(sColContent[1])) {
					System.out
							.println("The New value is equal to the existing value");
				} else {
					oBrowser.click(sLocator);
				}

			} else if (oBrowser.isElementPresent(sLocator + "/child::a", true)) {
				oActions.click(
						driver.findElement(By
								.xpath(sTable_Content_RowLocation + "["
										+ (iRow + 1) + "]/td[" + (iCol + 2)
										+ "]"))).build().perform();
				// If the locator is editable it will update it with the new
				// value
			} else if ((oBrowser.isElementPresent(sLocator + "/child::div",
					true))
					|| (oBrowser.isElementPresent(sLocator + "/child::span",
							true))) {

				oBrowser.click(sLocator);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println(oBrowser.getAttribute(sLocator, "class"));

				// If the element is drop down then do the select operation
				
				if (oBrowser.isElementPresent("//select[@id='FEGRID_"+cwsTabNumber+"_e_"
						+ iCol + "_i']", true)) {
					
					oBrowser.select("//select[@id='FEGRID_"+cwsTabNumber+"_e_"
						+ iCol + "_i']", sColContent[1]);
					oActions.sendKeys(
							driver.findElement(By
									.xpath("//*[@id='FEGRID_"+cwsTabNumber+"_e_" + iCol
											+ "']")), Keys.ENTER).build().perform();
				
				// Else the field is considered as Text Field
				} else {

				oActions.sendKeys(
						driver.findElement(By
								.xpath("//*[@id='FEGRID_"+cwsTabNumber+"_e_" + iCol
										+ "']")),
						Keys.chord(Keys.CONTROL, "a"), sColContent[1]).build()
						.perform();

				oActions.sendKeys(
						driver.findElement(By
								.xpath("//*[@id='FEGRID_"+cwsTabNumber+"_e_" + iCol
										+ "']")), Keys.ENTER).build().perform();
				}

				// If the locator is non-editable field then it will simply
				// write a
				// message to the console stating the field is not editable
			} else {
				System.out.println("This field cannot be edited");
			}
		} else if (sColHdr_Name.contains("ContextMenu")) {

			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();",
					driver.findElement(By.xpath("//*[@id='FEGRID_"+cwsTabNumber+"_"+(iRow-1)+"_aCol']")));

			oBrowser.click("//*[@id='FEGRID_"+cwsTabNumber+"_"+(iRow-1)+"_aCol']");

		} else {

			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();",
					driver.findElement(By.xpath(sFE_Actions_Location + "/tr["
							+ (iRow + 1) + "]/td[4]")));

			oBrowser.click(sFE_Actions_Location + "/tr[" + (iRow + 1)
					+ "]/td[4]");

		}

		oBrowser.switchToDefaultContent();

	}

	// ----------------------------------------------------------------------------------------
	// This method identifies the row number as per the column header name and
	// calls a dotableAction method with the row number and the Column header
	// name

	/**
	 * @Author Vinay Tangella   & Santosh Medchalam
	 * @Description This method internally calls the doTableaction with arguments row number and column header name by calculating the row number
	 */	
	@Override
	public void doTableAction(String sColHdr_Name) {

		int rowNum;

		if (oPKColumnName.size() > 1) {

			rowNum = getTableRowNumber(sPKColumnName);
			
			if(rowNum > 0){
				doTableAction(rowNum, sColHdr_Name);	
			}else{
				throw new WebDriverException("Table Row not found");
			}
			
			
		} else {
			String sActualString;
			sActualString = sPKColumnName.substring(1).toUpperCase().trim();

			rowNum = getTableRowNumber(objectDetails.get(sActualString));
			
			if(rowNum > 0){
				doTableAction(rowNum, sColHdr_Name);
			}else{
				throw new WebDriverException("Table Row not found");
			}
		}

	}

	// ----------------------------------------------------------------------------------------
	// Returns the Col count of a FE

	/**
	 * @Author Vinay Tangella   & Santosh Medchalam
	 * @Description This method returns the column count of the table
	 */
	public int getTableColCount() {

		int iColCount;

		iColCount = oBrowser.getXpathCount(sTable_Content_ColLocation);

		return iColCount - 1;
	}

	// ----------------------------------------------------------------------------------------
	// Returns the Row count of a FE

	/**
	 * @Author Vinay Tangella  & Santosh Medchalam
	 * @Description This method returns the row count of the table
	 */
	public int getTableRowCount() {

		int iRowCount;

		iRowCount = oBrowser.getXpathCount(sTable_Content_RowLocation);

		return iRowCount - 1;
	}

	// ----------------------------------------------------------------------------------------
	// This returns a cell content

	/**
	 * @Author Vinay Tangella   & Santosh Medchalam
	 * @Description This method returns the cell content of the table row. Takes parameters row number and the column header name
	 */
	@Override
	public String getTableCellContent(int iRow, String sColHdr_Name) {

		// oColHdrNames = getTableColHeaders();

		Boolean bStatus = false;

		int iCol = oColHdrNames.get(sColHdr_Name);

		String sCellLocation = sTable_Content_RowLocation + "[" + (iRow + 1)
				+ "]/td[" + (iCol + 2) + "]";

		// oBrowser.isElementPresent(sCellLocation+"/child::input[@type='checkbox']",true);

		if (oBrowser.isElementPresent(sCellLocation
				+ "/child::input[@type='checkbox']", true)) {
			bStatus = Boolean.valueOf(oBrowser.getAttribute(sCellLocation
					+ "/child::input[@type='checkbox']", "value"));
			if (bStatus) {
				return "CHECK";
			} else {
				return "UNCHECK";
			}
		} else {
			return oBrowser.getText(sCellLocation);
		}
	}

	// ----------------------------------------------------------------------------------------
	// This returns the entire primary keys data in key value pairs as row1 -
	// d1,d2.d3
	/**
	 * @Author Vinay Tangella & Santosh Medchalam
	 * @Description This method returns the row count of the table
	 */
	public HashMap<String, HashMap<String, String>> getTable_PrimayRowContent() {

		HashMap<String, HashMap<String, String>> oTableData = new HashMap<String, HashMap<String, String>>();

		String sRow;
		int iCol1;
		String sColName;

		// ArrayList<String> sRow_Content = new ArrayList<String>();

		for (int iRow = 1; iRow < iRowCount - 1; iRow++) {
			HashMap<String, String> sRow_Content = new HashMap<String, String>();
			for (int iCol = 1; iCol <= oPKColumnName.size(); iCol++) {

				sColName = oPKColumnName.get(iCol - 1);
				iCol1 = getTableColumnNumber(oPKColumnName.get(iCol - 1));

				((JavascriptExecutor) driver).executeScript(
						"arguments[0].scrollIntoView();",
						driver.findElement(By.xpath(sTable_Content_RowLocation
								+ "[" + (iRow + 1) + "]/td[" + (iCol1 + 1)
								+ "]")));

				sRow = oBrowser.getText(sTable_Content_RowLocation + "["
						+ (iRow + 1) + "]/td[" + (iCol1 + 1) + "]");
				sRow_Content.put(sColName, sRow);
			}

			oTableData.put("Row" + iRow, sRow_Content);
			boolean bStatus1 = compareLists(oTableData.get("Row" + iRow), colContentList);
			
			if(bStatus1){
				System.out.println("asa");
			}
			
			sRow = "";
			// sRow_Content.clear();
		}

		return oTableData;
	}

	// ----------------------------------------------------------------------------------------
	// This returns the entire table row content Primary Keys data and
	// non-primary keys data

	/**
	 * @Author Vinay Tangella & Santosh Medchalam
	 * @Description This method returns the entire table data in the form of HashMap
	 */	
	public HashMap<String, HashMap<String, String>> getEntireTableRowContent() {

		HashMap<String, HashMap<String, String>> oTableData = new HashMap<String, HashMap<String, String>>();

		String sRow;
		int iCol1;
		String sColName;
		boolean bStatus2=false;

		// ArrayList<String> sRow_Content = new ArrayList<String>();

		for (int iRow = 1; iRow <= iRowCount - 1; iRow++) {
			HashMap<String, String> sRow_Content = new HashMap<String, String>();
			for (int iCol = 1; iCol <= oColHeaders.size(); iCol++) {

				sColName = oColHeaders.get(iCol - 1);
				
				
				if (sColName.contains("*")) {
					sColName = sColName.substring(1);					
				}
				
				
				iCol1 = getTableColumnNumber(oColHeaders.get(iCol - 1));

				if (oBrowser.isElementPresent(sTable_Content_RowLocation + "["
						+ (iRow + 1) + "]/td[" + (iCol1 + 1) + "]"
						+ "/child::input[@type='checkbox']", true)) {

					sRow = oBrowser.getAttribute(sTable_Content_RowLocation
							+ "[" + (iRow + 1) + "]/td[" + (iCol1 + 1) + "]"
							+ "/child::input[@type='checkbox']", "value");

					if (sRow.equalsIgnoreCase("true")) {
						sRow = "1";
					} else {
						sRow = "0";
					}

				} else {
					sRow = oBrowser.getText(sTable_Content_RowLocation + "["
							+ (iRow + 1) + "]/td[" + (iCol1 + 1) + "]");
				}

				sRow_Content.put(sColName, sRow);				
			}
			oTableData.put("Row" + iRow, sRow_Content);
			
			bStatus2 = compareLists(oTableData.get("Row" + iRow), colContentList_EntireRow, oTableColNames_DB);
			
			if(bStatus2){
				break;				
			}
			
			sRow = "";
			// sRow_Content.clear();
		}
		
		if(bStatus2==false){
			System.out.println("The content you are searching is not available in the page");
		}
		return oTableData;
	}

	// ----------------------------------------------------------------------------------------
	// This method validates a Table Cell Content
	/**
	 * @Author Vinay Tangella  & Santosh Medchalam
	 * @Description This method returns the boolean value after validating the table cell content
	 */	
	@Override
	public boolean validateTableCellContent(int iRow, String sColHdr_Name,
			String sColContent) {
		boolean compStatus = false;

		switchtoTableFrame();

		String sActCol_Content = getTableCellContent(iRow, sColHdr_Name);

		if (sActCol_Content.trim().equals(sColContent.replace("value:", ""))) {
			compStatus = true;
		}

		return compStatus;
	}

	// ----------------------------------------------------------------------------------------
	// This returns the table row number from FE
	/**
	 * @Author Vinay Tangella   & Santosh Medchalam
	 * @Description This method returns the row number by taking the primary key value as the argument 
	 */		
	@Override
	public int getTableRowNumber(String pkColValue) {

		String colContent = "";
		switchtoTableFrame();
		int iRow = 1;
		int i1 = 0;
		int iCol;
		boolean foundStatus = false;
		boolean compStatus = false;
		HashMap<String, HashMap<String, String>> oTableData = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> sRow_Content = new HashMap<String, String>();

		iCol = iPKColNum;

		if (oPKColumnName.size() > 1) {					

			String sRow;
			int iCol1;
			String sColName;
			
			int iFERowNum, iMatchedRowNum=0;

			// ArrayList<String> sRow_Content = new ArrayList<String>();

			for (iFERowNum = 1; iFERowNum <= iRowCount - 1; iFERowNum++) {
				HashMap<String, String> sFERow_Content = new HashMap<String, String>();
				for (int iFECol = 1; iFECol <= oPKColumnName.size(); iFECol++) {

					sColName = oPKColumnName.get(iFECol - 1);
					iCol1 = getTableColumnNumber(oPKColumnName.get(iFECol - 1));

					((JavascriptExecutor) driver).executeScript(
							"arguments[0].scrollIntoView();",
							driver.findElement(By.xpath(sTable_Content_RowLocation
									+ "[" + (iFERowNum + 1) + "]/td[" + (iCol1 + 1)
									+ "]")));

					sRow = oBrowser.getText(sTable_Content_RowLocation + "["
							+ (iFERowNum + 1) + "]/td[" + (iCol1 + 1) + "]");
					sFERow_Content.put(sColName, sRow);
				}

				oTableData.put("Row" + iFERowNum, sFERow_Content);
				boolean bStatus1 = compareLists(oTableData.get("Row" + iFERowNum), colContentList);
				
				if(bStatus1){
					iMatchedRowNum=iFERowNum;
					break;
					
				}
				
				sRow = "";
			}
			
			return iMatchedRowNum;
			

			
		} else {
			for (; iRow <= iRowCount; iRow++) {

				String sCellLocation = sTable_Content_RowLocation + "["
						+ (iRow + 1) + "]/td[" + (iCol + 2) + "]";

				colContent = oBrowser.getText(sCellLocation);

				if (colContent.trim().equals(pkColValue)) {
					foundStatus = true;
					break;
				}
			}

			if (foundStatus) {
				return iRow;
			} else {
				return -1;
			}
		}
	}

	// ----------------------------------------------------------------------------------------
	// This returns the column number from a FE
	/**
	 * @Author Vinay Tangella  & Santosh Medchalam
	 * @Description This method returns the column number by taking the column
	 *              header name as the argument
	 */
	public int getTableColumnNumber(String sColHdr_Name) {

		return (oColHdrNames.get(LocatorFormatter
				.formatResourceValue(sColHdr_Name))) + 1;

	}

	// ----------------------------------------------------------------------------------------

	// Click a context menu and select a option in FE on a particular row
	/**
	 * @Author Vinay Tangella  & Santosh Medchalam
	 * @Description This method clicks in the context menu of a row
	 * 
	 */
	public void clickContextMenu() {
		int iRow_Num = getTableRowNumber(sPKColumnName);

		oBrowser.click(sFE_Actions_Location + "/tr[" + (iRow_Num + 1)
				+ "]/td[3]");

	}

	// ----------------------------------------------------------------------------------------
	// Selects a FE table Row
	/**
	 * @Author Vinay Tangella   & Santosh Medchalam
	 * @Description This method clicks in the check box next to a row 
	 */
	@Override
	public void selectTableRow() {
		
		System.out.println(" ");

		int iRow;

		if (oPKColumnName.size() > 1) {
			
			iRow = getTableRowNumber(objectDetails.get(sPKColumnName));

			if (iRow == 0) {
				throw new WebDriverException("Table row not found");

			} else {
				oBrowser.click("//*[@id='FEGRID_"+cwsTabNumber+"_"+(iRow - 1)+"_cCol']");				
			}
		} else {

			iRow = getTableRowNumber(objectDetails.get(sTablePkCol_DBName));

			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();",
					driver.findElement(By.xpath(sFE_Actions_Location + "/tr["
							+ (iRow + 1) + "]/td[2]")));

			oBrowser.click(sFE_Actions_Location + "/tr[" + (iRow + 1)
					+ "]/td[2]");
		}
	}

	// ----------------------------------------------------------------------------------------
	// To switch to a frame
	/**
	 * @Author Vinay Tangella   & Santosh Medchalam
	 * @Description This method is used to switch the frames to the frame in
	 *              which the table resides
	 */
	public void switchtoTableFrame() {

		oBrowser.setDefaultFrame();

		oBrowser.selectFrames(sFrames);

	}

	// ----------------------------------------------------------------------------------------
	// This is to Click a particular cell in the FE
	/**
	 * @Author Vinay Tangella  & Santosh Medchalam
	 * @Description This method is used to click a cell in the table
	 */
	public void clickCell(TestBrowser oBrowser, int iRow, int iCol) {

		driver = (WebDriver) oBrowser.getWebDriver();

		oColHdrNames = getTableColHeaders();

		String sColHdr_Name = "BOM_URL";

		int iCol1 = oColHdrNames.get(sColHdr_Name);

		System.out.println(iCol1);

		oBrowser.click(sTable_Content_RowLocation + "[" + (iRow + 1) + "]/td["
				+ (iCol1 + 2) + "]");

		Actions oActions = (Actions) oBrowser.getActions();

		// List<WebElement> we =
		// driver.findElements(By.xpath(sTable_Content_RowLocation+"["+(iRow+1)+"]/td"));

		// System.out.println(we.size());

		oActions.click(
				driver.findElement(By.xpath(sTable_Content_RowLocation + "["
						+ (iRow + 1) + "]/td[" + (iCol1 + 2) + "]/a"))).build()
				.perform();

		// oActions.sendKeys(driver.findElement(By.xpath("//*[@id='FEGRID_"+cwsTabNumber+"_e_"+iCol1+"']")),Keys.chord(Keys.CONTROL,"a"),
		// "2.352").build().perform();

		// oActions.sendKeys(driver.findElement(By.xpath("//*[@id='FEGRID_"+cwsTabNumber+"_e_"+iCol1+"']")),Keys.ENTER).build().perform();

	}

	// ----------------------------------------------------------------------------------------
	// This returns a HashMap of TableCol Headers
	/**
	 * @Author Vinay Tangella  & Santosh Medchalam
	 * @Description This method retuns the column header names of the table
	 */
	public HashMap<String, Integer> getTableColHeaders() {

		WebDriver driver;
		driver = (WebDriver) oBrowser.getWebDriver();

		int iCount;

		iCount = oBrowser.getXpathCount(sTable_ColsHeader_Location);
		System.out.println(iCount);

		System.out.println(iRowCount);

		for (int i = 1; i < iCount; i++) {

			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();",
					driver.findElement(By.xpath(sTable_ColsHeader_Location
							+ "[" + (i + 1) + "]")));

			/*
			 * ((JavascriptExecutor) driver).executeScript(
			 * "arguments[0].scrollIntoView();", driver.findElement(By
			 * .xpath(sTable_ColHeader_Content_Location + "_" + i +
			 * "']/descendant::span")));
			 */

			/*
			 * oColHdrNames.put( oBrowser.getText(
			 * sTable_ColHeader_Content_Location + "_" + i +
			 * "']/descendant::span").trim(), i);
			 */

			oColHdrNames.put(
					oBrowser.getText(
							sTable_ColsHeader_Location + "[" + (i + 1) + "]")
							.trim(), i - 1);

			oColHeaders.add(oBrowser.getText(
					sTable_ColsHeader_Location + "[" + (i + 1) + "]").trim());

			/*
			 * oColHeaders.add(oBrowser.getText(
			 * sTable_ColHeader_Content_Location + "_" + i +
			 * "']/descendant::span").trim());
			 */

		}
		return oColHdrNames;
	}

	// ----------------------------------------------------------------------------------------
	// This is to set a particular cell in the FE Table
	/**
	 * @Author Vinay Tangella & Santosh Medchalam
	 * @Description This method is used set content in the table cell
	 */
	public void setTableCellContent(int iRow, String sColHdr_Name,
			String sColContent) {

		WebDriver driver;

		Boolean bStatus = false;

		int iCol;

		driver = (WebDriver) oBrowser.getWebDriver();

		Actions oActions = (Actions) oBrowser.getActions();

		oColHdrNames = getTableColHeaders();

		iCol = oColHdrNames.get(sColHdr_Name);

		String sLocator = sTable_Content_RowLocation + "[" + (iRow + 1)
				+ "]/td[" + (iCol + 2) + "]";

		switchtoTableFrame();

		oBrowser.click(sLocator);

		if (oBrowser.isElementPresent(sLocator
				+ "/child::input[@type='checkbox']", true)) {
			bStatus = Boolean.valueOf(oBrowser.getAttribute(sLocator
					+ "/child::input[@type='checkbox']", "value"));
			// depending upon the input for teh checkbox we've to handle here
			// If the locator is a link then it will click the link
		} else if (oBrowser.isElementPresent(sLocator + "/child::a", true)) {
			oActions.click(
					driver.findElement(By.xpath(sTable_Content_RowLocation
							+ "[" + (iRow + 1) + "]/td[" + (iCol + 2) + "]")))
					.build().perform();
			// If the locator is editable it will update it with the new value
		} else if (oBrowser.isElementPresent(sLocator + "/child::div", true)) {
			System.out.println(oBrowser.getAttribute(sLocator, "class"));

			oActions.sendKeys(
					driver.findElement(By.xpath("//*[@id='FEGRID_"+cwsTabNumber+"_e_"
							+ iCol + "']")), Keys.chord(Keys.CONTROL, "a"),
					"2.352").build().perform();

			oActions.sendKeys(
					driver.findElement(By.xpath("//*[@id='FEGRID_"+cwsTabNumber+"_e_"
							+ iCol + "']")), Keys.ENTER).build().perform();

			// If the locator is non-editable field then it will simply write a
			// message to the console stating the field is not editable
		} else {
			System.out.println("This field cannot be edited");
		}

		oBrowser.switchToDefaultContent();

	}

	// ----------------------------------------------------------------------------------------
	// This returns a boolean value after comparing the FE Row content
	@Override
	/**
	 * @Author Vinay Tangella  & Santosh Medchalam
	 * @Description This method is used validate the table row content and returns a boolean value 
	 */
	public boolean valiadateTableRowContent() {

		HashMap<String, HashMap<String, String>> oTableData = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> sRow_Content = new HashMap<String, String>();
		int iRow = 1;
		boolean compStatus = true;

		switchtoTableFrame();

		if (oPKColumnName.size() == 0) {

			int rowNum = getTableRowNumber(objectDetails
					.get(sTablePkCol_DBName));

			if (rowNum != 0) {

				for (int index = 0; index < oTableColNames.size(); index++) {

					compStatus = validateTableCellContent(rowNum,
							oTableColNames.get(index),
							LocatorFormatter.formatLocator(colContentList
									.get(index)));

					if (!compStatus) {

						break;
					}

				}
			} else {
				compStatus = false;
			}
		} else {

			oTableData = getEntireTableRowContent();
			for (; iRow <= iRowCount - 1; iRow++) {

				sRow_Content = oTableData.get("Row" + iRow);

				compStatus = compareLists(sRow_Content, colContentList_EntireRow,oTableColNames);

				if (compStatus) {
					compStatus = true;
					break;
				}
			}

		}

		return compStatus;
	}

	// ----------------------------------------------------------------------------------------
	// This needs to be developed
	/**
     *@Author Vinay Tangella  & Santosh Medchalam
	 * @Description This method is not yet developed
	 */
	public int getTableRowNum_Content() {

		boolean compStatus = true;

		int iRowNum = 0;

		switchtoTableFrame();

		int rowNum = getTableRowNumber(objectDetails.get(sTablePkCol_DBName));

		if (rowNum != 0) {

			for (int index = 0; index < oTableColNames.size(); index++) {

				compStatus = validateTableCellContent(rowNum,
						oTableColNames.get(index),
						LocatorFormatter.formatLocator(colContentList
								.get(index)));

				if (!compStatus) {
					iRowNum = index;
					break;
				}

			}
		} else {

			compStatus = false;
		}

		return iRowNum;
	}

	// This needs to be developed
	// ----------------------------------------------------------------------------------------

	@Override
	/**
	 * @Author Vinay Tangella  & Santosh Medchalam
	 * @Description This method is not yet developed
	 */
	public void setTableRowContent() {
		// TODO Auto-generated method stub
	}

	// ----------------------------------------------------------------------------------------
	// This method is basically used to compare two array lists
	/**
	 * @Author Vinay Tangella  & Santosh Medchalam
	 * @Description This method returns a boolean value after comparing the two
	 *              array lists
	 */
	public boolean compareLists(HashMap<String, String> oExpected,
			HashMap<String, String> oActual) {

		Boolean bStatus = false;
		String sActualString;

		int iFailCounter = 0;

		for (int i = 0; i < oExpected.size(); i++) {

			// sActualString
			sActualString = oPKColumnName.get(i).substring(1);

			if (oActual.get(sActualString).equalsIgnoreCase(
					oExpected.get(oPKColumnName.get(i)))) {
				//System.out.println("true");

			} else {
				iFailCounter++;
			}

			//The following piece of code has been changed to successfully multiple table rows in a VPTable
			if (iFailCounter >= 1) {
				bStatus=false;
				return bStatus;
			} else {
				bStatus = true;
			}
		}

		return bStatus;
	}
	
	public boolean compareLists(HashMap<String, String> oExpected,
			HashMap<String, String> oActual, List<String> oTableColNames) {

		Boolean bStatus = false;
		String columnName;

		int iFailCounter = 0;

		for (int i = 0; i < oTableColNames.size(); i++) {

			// sActualString
			columnName = oTableColNames.get(i);

			if (oActual.get(columnName).equalsIgnoreCase(
					oExpected.get(columnName))) {
				//System.out.println("true");

			} else {
				iFailCounter++;
			}

			//The following piece of code has been changed to successfully multiple table rows in a VPTable
			if (iFailCounter >= 1) {
				bStatus=false;
				return bStatus;
			} else {
				bStatus = true;
			}
		}

		return bStatus;
	}

	// ----------------------------------------------------------------------------------------
	// This method is used to write the FE Grid data to Excel Sheet
	/**
	 * @Author Vinay Tangella  & Santosh Medchalam
	 * @Description This method is used to write the FE Grid data to Excel Sheet
	 */
	public void writeFEDatatoExcel() throws FileNotFoundException {

		String sFETitleLocator = "//*[@id='tblvpApptitleTitle']";

		sFileName = oBrowser.getText(sFETitleLocator);

		String sTestDir = System.getProperty("user.dir")
				+ "\\fedatavalidation\\";

		HashMap<String, HashMap<String, String>> oTableData = new HashMap<String, HashMap<String, String>>();

		// This variable holds the entire table data as key value pairs example
		// - row1,data1 row2,data2
		oTableData = getEntireTableRowContent();

		HashMap<String, String> sContent = new HashMap<String, String>();

		com.jda.qa.platform.framework.util.ExcelReader oEr;

		// This block is to create an Xcel file
		File feDataFile = new File(System.getProperty("user.dir")
				+ "\\fedatavalidation\\" + sFileName + "_data.xlsx");
		
		if(feDataFile.exists()){
			feDataFile.delete();
		}

		File templateFile = new File(System.getProperty("user.dir")
				+ "\\data\\Sample_Results.xlsx");

		FileUtils.copyFile(templateFile, feDataFile);

		oEr = new com.jda.qa.platform.framework.util.ExcelReader(feDataFile.getAbsolutePath());

		// Here we add a new sheet with name FE_Data
		oEr.addSheet("FE_Data");

		// First add all the columns to the Excel Sheet
		for (int i = 0; i < oColHeaders.size(); i++) {
			oEr.addColumn("FE_Data", oColHeaders.get(i));
		}

		// adding the contents of the FE table under the right column heading
		for (int iRow = 1; iRow <= oTableData.size(); iRow++) {
			for (int i = 1; i <= oColHeaders.size(); i++) {

				// System.out.println(oColHeaders.get(i - 1));

				oEr.setCellData(
						"FE_Data",
						oColHeaders.get(i - 1),
						iRow + 1,
						oTableData.get("Row" + (iRow)).get(
								oColHeaders.get(i - 1)));
			}
		}
	}

	// ----------------------------------------------------------------------------------------
	// This reads the data from DB and writes it to an Excel Sheet
	/**
	 * @Author Vinay Tangella
	 * @Description This fetches the data from DB and writes it to an Excel
	 *              Sheet
	 */
	@SuppressWarnings("deprecation")
	public void writeDBDatatoExcel() {

		String sQuery, sQuery_LogicalDT, sPartialQuery_LogicalDT = "", sPartialQuery = "", sStamp, sRow, sFirstColumn = "", sTempColumn_Name, sTempColumn_Name_1;

		HashMap<String, String> colDataType;

		SimpleDateFormat oDateFormat;
		Date oDate;

		Connection oConn;

		ResultSet oRes_Set = null, oData_Types = null;

		ArrayList<HashMap<String, String>> oResult = new ArrayList<HashMap<String, String>>();

		ArrayList<String> oPlainColHeaders = new ArrayList<String>();

		HashMap<String, HashMap<String, String>> oDB_Data = new HashMap<String, HashMap<String, String>>();

		DBConnection oDB;

		com.jda.qa.platform.framework.util.ExcelReader oEr;

		oEr = new ExcelReader(System.getProperty("user.dir")
				+ "\\fedatavalidation\\" + sFileName + "_data.xlsx");

		// This section of code removes the '*' symbol from the column names for
		// the primary keys
		for (int i = 0; i < oColHeaders.size(); i++) {
			if (i == 0) {
				sTempColumn_Name = oColHeaders.get(i).toString();

				if (sTempColumn_Name.contains("*")) {
					sTempColumn_Name = sTempColumn_Name.substring(1);
					oPlainColHeaders.add(sTempColumn_Name);
				}

				sPartialQuery_LogicalDT = "'" + sTempColumn_Name + "'";

				sPartialQuery = sTempColumn_Name;
			} else {
				sTempColumn_Name_1 = oColHeaders.get(i).toString();

				if (sTempColumn_Name_1.contains("*")) {
					sTempColumn_Name_1 = sTempColumn_Name_1.substring(1);
				}
				if (sTempColumn_Name_1.equalsIgnoreCase("Description")) {
					sTempColumn_Name_1 = "Descr";
				}

				oPlainColHeaders.add(sTempColumn_Name_1);
				sPartialQuery = sPartialQuery + "," + sTempColumn_Name_1;
				sPartialQuery_LogicalDT = sPartialQuery_LogicalDT + "," + "'"
						+ sTempColumn_Name_1 + "'";

			}
		}

		// This is the actual query
		sQuery = "Select " + sPartialQuery + " from " + MainEngine.sampleDBUser
				+ "." + sFileName;

		// this query is to get the logical data types of the columns of FE to
		// format the data
		sQuery_LogicalDT = "SELECT COLUMN_NAME, LOGICAL_DATA_TYPE_NAME FROM "
				+ MainEngine.wwfDBUser + "."
				+ "MD_COLUMN_INFO WHERE TABLE_NAME = '" + sFileName
				+ "' AND COLUMN_NAME IN (" + sPartialQuery_LogicalDT
				+ ") ORDER BY COLUMN_NAME";

		// System.out.println(sQuery_LogicalDT);

		oDB = new DBConnection("in2npdlnxdb14", "1521", "tst1212a", "System",
				"manager");

		try {
			oConn = oDB.getConnection();
			oRes_Set = oDB.executeQuery(sQuery);

			oData_Types = oDB.executeQuery(sQuery_LogicalDT);

			ArrayList<HashMap<String, String>> resultListArray = oDB
					.getResultsasArrayList(oData_Types);

			oResult = oDB.getResultsasArrayList(oRes_Set);

			for (int i = 0; i < oResult.size(); i++) {

				HashMap<String, String> oDBRow_Content = new HashMap<String, String>();

				for (int j = 0; j < oPlainColHeaders.size(); j++) {

					sRow = oResult.get(i).get(oPlainColHeaders.get(j));

					String s1 = oPlainColHeaders.get(j);

					String scolDataType = "";

					for (int iCounter = 0; iCounter < resultListArray.size(); iCounter++) {

						colDataType = resultListArray.get(iCounter);

						if (colDataType.get("COLUMN_NAME").equals(s1)) {

							scolDataType = colDataType
									.get("LOGICAL_DATA_TYPE_NAME");
							break;
						}

					}

					switch (scolDataType) {

					case "DATE":

						if (sRow != "NULL") {

							oDateFormat = new SimpleDateFormat(
									"yyyy-M-dd hh:mm:ss.S");// This is the
															// format of
															// the date that is
															// fetched from the
															// DB

							// System.out.println(oResult.get(i).get(
							// oPlainColHeaders.get(j)));

							// System.out.println(oPlainColHeaders.get(j));

							oDate = oDateFormat.parse(oResult.get(i).get(
									oPlainColHeaders.get(j)));

							oDateFormat = new SimpleDateFormat("M/d/yy");
							sStamp = oDateFormat.format(oDate);

							if (sStamp.equals("1/1/70")) {

								sRow = "";
							} else {
								sRow = sStamp;
							}
						}

						break;

					case "DATETIME":

						String dateTimeValue = sRow;
						Date date = null;

						if (dateTimeValue != "NULL") {

							DateFormat formatter = new SimpleDateFormat(
									"MM/dd/yyyy hh:mm a");
							date = (Date) formatter.parse(dateTimeValue);
							formatter = new SimpleDateFormat(
									MainEngine.dateTimeFormat);

							dateTimeValue = formatter.format(date);
						}

						if (dateTimeValue
								.startsWith(new SimpleDateFormat("M/d/yy")
										.format(new Date("1/1/70")).toString())
								|| sRow.equalsIgnoreCase("NULL")) {
							sRow = "";
						} else {
							sRow = new SimpleDateFormat(
									MainEngine.dateTimeFormat).format(date);
						}

						break;

					case "TIME_OF_DAY":

						String amOrPm = "AM";

						int timeVal = Integer.valueOf(sRow);

						int hour = (int) timeVal / 60;

						int min = (int) timeVal % 60;

						if (hour > 12) {
							hour = hour - 12;
							amOrPm = "PM";
						}

						if (hour == 0) {
							hour = 12;
						}

						String hourStr = (hour < 9) ? "0" + hour : hour + "";
						String minStr = (min < 9) ? "0" + min : min + "";

						sRow = hourStr + ":" + minStr + " " + amOrPm;

						break;

					case "DURATION":

						Long durData = Long.valueOf(sRow);

						int days = (int) (durData / 1440);

						int value = (int) (durData % 1440);

						int hours = value / 60;

						int minutes = (int) (value % 60);

						sRow = days + "D" + hours + "H" + minutes + "M";

						break;

					case "INTEGER":
						DecimalFormat intfrmt = new DecimalFormat(
								MainEngine.integerFormat);

						Double.parseDouble(sRow.toString());
						break;

					case "DECIMAL":
						DecimalFormat decfrmt = new DecimalFormat(
								MainEngine.decimalFormat);

						decfrmt.format(Double.parseDouble(sRow.toString()));
						break;

					case "BOOLEAN":
						if (sRow.equalsIgnoreCase("NULL")) {
							sRow = "FALSE";
						} else if (sRow.equalsIgnoreCase("1")) {
							sRow = "TRUE";
						}

						break;

					case "URL":

						if (sRow.equalsIgnoreCase("NULL")) {
							sRow = "";
						}

						break;
					}

					oDBRow_Content.put(oColHeaders.get(j), sRow);
				}
				oDB_Data.put("Row" + i, oDBRow_Content);
				sRow = "";
			}

			System.out.println(oDB_Data.size());

			// Here we add a new sheet with name FE_Data
			oEr.addSheet("DB_Data");

			// First add all the columns to the Excel Sheet
			for (int i = 0; i < oColHeaders.size(); i++) {
				oEr.addColumn("DB_Data", oColHeaders.get(i));
			}

			// adding the contents of the FE table under the right column
			// heading
			for (int iRow = 1; iRow <= oDB_Data.size(); iRow++) {
				for (int i = 1; i <= oColHeaders.size(); i++) {

					// (String sheetName, String colName, int rowNum, String
					// data)
					oEr.setCellData(
							"DB_Data",
							oColHeaders.get(i - 1),
							(iRow + 1),
							oDB_Data.get("Row" + (iRow - 1)).get(
									oColHeaders.get(i - 1)));
				}
			}

			System.out.println("completed");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// ----------------------------------------------------------------------------------------
	// This is to verify the count after Adding , Copying , Deleting the row in
	// FE
	/**
	 * @Author - Vinay Tangella
	 * @Descripiton * This method is to verify the count after Adding , Copying
	 *              , Deleting the row in FE
	 */
	public int verifyRecordCount() {
		int iSize = oBrowser.getText(sRecordCount).length();

		int iSize1 = Integer.valueOf(oBrowser.getText(sRecordCount).substring(
				(iSize - 6), iSize - 4));

		return iSize1;

	}

	// ----------------------------------------------------------------------------------------
	// This is to find the status of the FE options
	/**
	 * @Author - Vinay Tangella
	 * @Description This method is to find the status of the FE options
	 */
	public boolean verifyOptionStatus() {

		for (int i = 0; i < oTableColNames_DB.size(); i++) {
			String sOptionsLocator = "//div[@id='FEGRID_"+cwsTabNumber+"_"
					+ oTableColNames_DB.get(i) + "_ButtonDiv']";

			if (oBrowser.getAttribute(sOptionsLocator, "class").toString()
					.contains("disabled")) {
				System.out.println("true");
			} else {
				System.out.println("false");
			}

		}

		return false;
	}

	// ---------------------------------End OF
	// File--------------------------------------------

}