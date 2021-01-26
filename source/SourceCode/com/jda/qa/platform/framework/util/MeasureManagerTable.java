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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.gargoylesoftware.htmlunit.javascript.host.BroadcastChannel;
import com.jda.qa.platform.framework.base.MainEngine;
import com.jda.qa.platform.framework.browser.TestBrowser;
import com.jda.qa.platform.framework.locator.LocatorFormatter;

//Author - Tejaswi Naidu
public class MeasureManagerTable implements TableInterface, InlineEditTableInterface {

//Class objects
	WebDriver driver;
	TestBrowser oBrowser;

//Variables
	String[] sFrames;
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
		
	Properties pTableProperties = new Properties();

//Collection Arrays
	ArrayList<String> oColHeaders = new ArrayList<String>();
	List<String> oPKColumnName = new ArrayList<String>();
	List<String> oTableColNames = new ArrayList<String>();
	HashMap<String, Integer> oColHdrNames = new HashMap<String, Integer>();
	HashMap<String, String> objectDetails = new HashMap<String, String>();
	HashMap<String, String> colPosition = new HashMap<String, String>();
	HashMap<String, String> colforSelection = new HashMap<String, String>();
	List<String> oTableColNames_DB = new ArrayList<String>();
	ArrayList<String> colContentList = new ArrayList<String>();
	
	/* constructor for ECMConsoleTable
	 * */
	public MeasureManagerTable (TestBrowser oBrowser, String sTableProps,
			String sDataSheetName, String sPKValue, String sTableFrame) {
		this(oBrowser, sTableProps, "Contents.xlsx", sDataSheetName, sPKValue,
				sTableFrame);
	}
	
	public MeasureManagerTable (TestBrowser oBrowser, String sTableProps,
			String excelFileName, String sDataSheetName, String sPKValue,
			String sTableFrame) {
		super();
		this.oBrowser = oBrowser;
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
		
		sTable_ColsHeader_Location=pTableProperties.getProperty("sTable_ColsHeader_Location").toString();
		sTable_Content_ColLocation=pTableProperties.getProperty("sTable_Content_ColLocation").toString();
		sTable_Content_RowLocation=pTableProperties.getProperty("sTable_Content_RowLocation").toString();
		
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
		objectDetails = oData.getExcelData(sPKValue);
		for (int index = 0; index < oTableColNames_DB.size(); index++) {
			colContentList.add(objectDetails.get(oTableColNames_DB.get(index)));
		}
		for(int i=0;i<sTablePkCol_DBName.split(",").length;i++)
		{
			colforSelection.put(Integer.toString(i),objectDetails.get(sTablePkCol_DBName.split(",")[i]));
			
		}
		
	}
	
	
	public void loadTable() {
		driver = (WebDriver) oBrowser.getWebDriver();
		switchtoTableFrame();
		oBrowser.sleep(1000);
		iColcount = oBrowser.getXpathCount(sTable_Content_ColLocation);
		iRowCount = oBrowser.getXpathCount(sTable_Content_RowLocation);
		oColHdrNames = getTableColHeaders();
		
		for(int i=0;i<oPKColumnName.size();i++)
		{
			colPosition.put(Integer.toString(i), oColHdrNames.get(oPKColumnName.get(i)).toString());			
		}
	}
	
	
	public HashMap<String, Integer> getTableColHeaders() {
		driver = (WebDriver) oBrowser.getWebDriver();
		int iCount;
		iCount = oBrowser.getXpathCount(sTable_ColsHeader_Location);
		for (int i = 1; i <= iCount; i++) {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();",
					driver.findElement(By.xpath("("+sTable_ColsHeader_Location+")"
							+ "[" + (i) + "]")));
			oColHdrNames.put(oBrowser.getText("("+sTable_ColsHeader_Location+")" + "[" + (i) + "]").trim(), i - 1);
			oColHeaders.add(oBrowser.getText("("+sTable_ColsHeader_Location+")" + "[" + (i) + "]").trim());
		}		
		return oColHdrNames;
	}
	
	public void selectTableRow(){

		int iRow = getTableRowNumber(sTablePkCol_DBName);
		if (iRow == -1) {
			throw new WebDriverException("Table row not found");
		} 
		else {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();",
					driver.findElement(By.xpath(sTable_Content_RowLocation + "["+ (iRow) + "]//td[1]")));

			oBrowser.click(sTable_Content_RowLocation + "["+ (iRow) + "]//td[1]");				
		}
	}

	public void switchtoTableFrame() {
		oBrowser.setDefaultFrame();
		oBrowser.selectFrames(sFrames);
	}	

	
	
	public int getTableRowNumber(String sTablePkCol_DBName) {


		String colContent = "";
		switchtoTableFrame();
		int iRow = 1;
		int iCol;
		boolean foundStatus = false;
		
		for (; iRow <= iRowCount; iRow++) {
			
			iCol=Integer.parseInt(colPosition.get("0"));
			String sCellLocation = sTable_Content_RowLocation + "["
					+ (iRow) + "]//td[not(contains(@class,'checkcolumn'))][" + (iCol + 1) + "]";
			
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();",
					driver.findElement(By.xpath(sCellLocation)));

			colContent = oBrowser.getText(sCellLocation);
			
			if (colContent.trim().equalsIgnoreCase(LocatorFormatter.updateDBValues(colforSelection.get("0")))) {
				foundStatus = true;
				
				for(int j=1;j<=colPosition.size()-1;j++)
				{
					iCol=Integer.parseInt(colPosition.get(Integer.toString(j)));
					sCellLocation = sTable_Content_RowLocation + "["
							+ (iRow) + "]//td[not(contains(@class,'checkcolumn'))][" + (iCol + 1) + "]";
					((JavascriptExecutor) driver).executeScript(
							"arguments[0].scrollIntoView();",
							driver.findElement(By.xpath(sCellLocation)));
					colContent = oBrowser.getText(sCellLocation);		
					if (!colContent.trim().equalsIgnoreCase(LocatorFormatter.updateDBValues(colforSelection.get(Integer.toString(j)))))
					{
						foundStatus = false;
						break;
					}
					else
						foundStatus = true;
						
				}
			}
			if (foundStatus) {break;} 	
		}
		if (!foundStatus) return -1;
		else return iRow;
	}
	
	public int getTableColumnNumber(String sColHdr_Name) {

		return (oColHdrNames.get(LocatorFormatter
				.formatResourceValue(sColHdr_Name))) + 1;

	}
	
	public void setTableRowContent() {

		switchtoTableFrame();
		int rowNum = getTableRowNumber(sTablePkCol_DBName);

		if (rowNum != -1) {

			for (int index = 0; index < oTableColNames.size(); index++) {

				if (oTableColNames.get(index).trim().length() > 0) {
					setTableCellContent(rowNum, oTableColNames.get(index),
							LocatorFormatter.formatLocator(colContentList
									.get(index)));
				}
				if (index != oTableColNames.size()-1)
				rowNum = getTableRowNumber(sTablePkCol_DBName);	

			}
		} else {
			throw new WebDriverException("Failed to perform the inline Edit");
		}
	}
	
	public void setTableCellContent(int rowNum, String colHdrName,
			String colContent) {

		if (!colContent.equalsIgnoreCase("SKIP")) {

			String colContentLoc = "";			

			switchtoTableFrame();

			colContentLoc = sTable_Content_RowLocation + "[" + rowNum + "]" + "//td["
					+ getTableColumnNumber(colHdrName) + "]";

			String textBoxLoc = colContentLoc + "//input[@type='text']";
			String SelectLoc = colContentLoc + "//select";
			

				Actions act = (Actions) oBrowser.getActions();

				WebElement colElem = driver
						.findElement(By.xpath(colContentLoc));
				
				oBrowser.moveToElement(colElem);
				
				act.click(colElem).build().perform();

				//act.doubleClick(colElem).build().perform();

				if (oBrowser.isElementPresent(textBoxLoc, true)) {
					oBrowser.clear(textBoxLoc);
					oBrowser.type(textBoxLoc, colContent);
				} 
				else if (oBrowser.isElementPresent(SelectLoc, true)) {
					oBrowser.select(SelectLoc, colContent);
				}				
				else {
					oBrowser.highlightBorder(colContentLoc);
					MainEngine.log.info("No Action Performed");
				}

		      WebElement textbox = driver.findElement(By.xpath(textBoxLoc));
		      textbox.sendKeys(Keys.ENTER);
		      oBrowser.isElementPresent(colContentLoc, 2);
			        

		} else {
			MainEngine.log.info("Skipping the table inline edit action");
		}

	}


	
	/* The below methods are not implemented yet, On need basis
	 * valiadateTableRowContent, getTableCellContent, validateTableCellContent, doTableAction 
	 */	

	public boolean valiadateTableRowContent(){
		return true;
	}
	public String getTableCellContent(int iRow, String sColHdr_Name){
		return "Nothing";
	}
	public boolean validateTableCellContent(int iRow, String sColHdr_Name,String sColContent){
		return true;
	}
	public void doTableAction(int iRow, String sColHdr_Name) {
		
	}
	public void doTableAction(String sColHdr_Name){
		
	}
}
