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
import com.jda.qa.platform.framework.locator.LocatorFormatter;

//Author - Tejaswi Naidu
public class ECMConsoleTable implements TableInterface {

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
	//String sPKColumnName;
	String sTablePkCol_DBName;
	String sPKValue;
	
	String sTable_ColsHeader_Location="//div[starts-with(@id,'twogridpanel') and @data-ref='innerCt']/div[1]//descendant::div[contains(@id,'headercontainer')]/div[@aria-hidden='false']//span[1][contains(@class,'header-text')]";
	String sTable_Content_ColLocation="//div[starts-with(@id,'twogridpanel') and @data-ref='innerCt']/div[1]//descendant::div[contains(@id,'view')][1]/div/table[1]//descendant::tr[1]/td";
	String sTable_Content_RowLocation="//div[starts-with(@id,'twogridpanel') and @data-ref='innerCt']/div[1]//descendant::div[contains(@id,'view')][1]//descendant::tr";
		
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
	
	/* constructor for ECMConsoleTable
	 * */
	public ECMConsoleTable (TestBrowser oBrowser, String sTableProps,
			String sDataSheetName, String sPKValue, String sTableFrame) {
		this(oBrowser, sTableProps, "Contents.xlsx", sDataSheetName, sPKValue,
				sTableFrame);
	}
	
	public ECMConsoleTable (TestBrowser oBrowser, String sTableProps,
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
		for (int i = 1; i < iCount; i++) {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();",
					driver.findElement(By.xpath("("+sTable_ColsHeader_Location+")"
							+ "[" + (i + 1) + "]")));
			oColHdrNames.put(oBrowser.getText("("+sTable_ColsHeader_Location+")" + "[" + (i + 1) + "]").trim(), i - 1);
			oColHeaders.add(oBrowser.getText("("+sTable_ColsHeader_Location+")" + "[" + (i + 1) + "]").trim());
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
					driver.findElement(By.xpath(sTable_Content_RowLocation + "["+ (iRow) + "]/td[1]")));

			oBrowser.click(sTable_Content_RowLocation + "["+ (iRow) + "]/td[1]");				
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
		
		for(int i=0;i<sTablePkCol_DBName.split(",").length;i++)
		{
			colforSelection.put(Integer.toString(i),objectDetails.get(sTablePkCol_DBName.split(",")[i]));
			
		}

					
		for (; iRow <= iRowCount; iRow++) {
			
			iCol=Integer.parseInt(colPosition.get("0"));
			String sCellLocation = sTable_Content_RowLocation + "["
					+ (iRow) + "]/td[" + (iCol + 2) + "]";
			
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();",
					driver.findElement(By.xpath(sCellLocation)));

			colContent = oBrowser.getText(sCellLocation);

			if (colContent.trim().equals(colforSelection.get("0"))) {
				foundStatus = true;
				
				for(int j=1;j<=colPosition.size()-1;j++)
				{
					iCol=Integer.parseInt(colPosition.get(Integer.toString(j)));
					sCellLocation = sTable_Content_RowLocation + "["
							+ (iRow) + "]/td[" + (iCol + 2) + "]";
					((JavascriptExecutor) driver).executeScript(
							"arguments[0].scrollIntoView();",
							driver.findElement(By.xpath(sCellLocation)));
					colContent = oBrowser.getText(sCellLocation);		
					if (!colContent.trim().equals(colforSelection.get(Integer.toString(j))))
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
	
	/* The below methods are not implemented yet, On need basis
	 * valiadateTableRowContent, getTableCellContent, validateTableCellContent, doTableAction 
	 */	
	public int getTableColumnNumber(String sColHdr_Name) {

		return (oColHdrNames.get(LocatorFormatter
				.formatResourceValue(sColHdr_Name))) + 1;

	}	

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
