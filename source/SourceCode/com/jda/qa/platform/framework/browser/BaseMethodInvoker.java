package SourceCode.com.jda.qa.platform.framework.browser;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.State;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;

import com.jda.qa.platform.framework.browser.MethodInvoker;
import com.jda.qa.platform.framework.browser.TestBrowser;
import com.jda.qa.platform.framework.browser.WebDriverBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.jda.qa.platform.framework.base.ExcelSheetCaller;
import com.jda.qa.platform.framework.base.MainEngine;
import com.jda.qa.platform.framework.locator.LocatorFormatter;
import com.jda.qa.platform.framework.util.BasicTable;
import com.jda.qa.platform.framework.util.DHTMLXTable;
import com.jda.qa.platform.framework.util.ExcelData;
import com.jda.qa.platform.framework.util.FEPropertiesTable;
import com.jda.qa.platform.framework.util.FileUtils;
import com.jda.qa.platform.framework.util.Form;
import com.jda.qa.platform.framework.util.InlineEditTableInterface;
import com.jda.qa.platform.framework.util.MultiInlineEditTableInterface;
import com.jda.qa.platform.framework.util.MultiSelectTableInterface;
import com.jda.qa.platform.framework.util.SQLRunner;
import com.jda.qa.platform.framework.util.TableInterface;
import com.jda.qa.platform.framework.util.VPTable;
import com.jda.qa.platform.framework.util.compareExcelSheets;

/**
 * @Author Santosh Medchalam & Vinay Tangella
 * @Description This class contains method that is invoked from the
 *              MainEngine.java and all the keywords are individually handled
 */

@SuppressWarnings("unused")
public class BaseMethodInvoker implements com.jda.qa.platform.framework.browser.MethodInvoker {

	/**
	 * @Author: Santosh Medchalam & Vinay Tangella
	 * @Description: This method take is invoked from the MainEngine.java and
	 *               all the keywords are individually handled
	 */
	public void runMethod(String methodName, String... args)
			throws WebDriverException {

		com.jda.qa.platform.framework.browser.TestBrowser browserObj = MainEngine.browser;

		args = buildDynamicProperties(methodName, args);

		try {

			switch (methodName.toUpperCase()) {
			case "WAIT": {

				Thread.sleep(Long.parseLong(args[0]));
				break;
			}
			case "WAITFORPAGETOLOAD": {

				if (args[0].length() == 0) {
					browserObj.waitForPageToLoad();
				} else {
					browserObj.waitForPageToLoad(Long.parseLong(args[0]));
				}

				break;
			}
			case "CLICKREPLACE": {

				String locator = args[0].replace("#VALUE#", args[1]);
				browserObj.click(locator);
				break;
			}
			case "CLICK": {
				browserObj.click(args[0]);
				break;
			}
			case "SELECTBYVALUE": {
				browserObj.selectByValue(args[0], args[1]);
				break;
			}
			case "SELECT": {
				browserObj.select(args[0], args[1]);
				break;
			}
			case "SELECTALL": {
				browserObj.selectAll(args[0]);
				break;
			}
			case "CLEAR": {
				browserObj.clear(args[0]);
				break;
			}
			case "TYPE": {
				browserObj.type(args[0], args[1]);
				break;
			}
			case "SETELEMENTATTRIBUTE": {

				String[] vals = args[1].split(":");

				browserObj.setAttribute(args[0], vals[0], vals[1]);

				break;
			}
			
			case "REMOVEELEMENTATTRIBUTE": {
                String[] vals = args[1].split(",");
                
                browserObj.removeAttribute(args[0], vals[0]); 
                
                break;
            }            
			
			case "GETTAGNAME_SETVALUE": {
				String TagName=null;
				String xpathString=null;
				String xpathStringContains=null;				
				String[] propertySets=null;				
				
				args[1]=args[1].replace("SYS_PROPS@", "");
				propertySets=args[1].split(";");
				
				for(String propertySet : propertySets){
					
					String[] vals1 = propertySet.split(":");
					TagName=browserObj.getTagName(args[0],vals1[0]);
					xpathString="//"+TagName+"[@"+args[0]+"='"+vals1[0]+"']";				
					
					switch(TagName.toUpperCase()){
						case "SELECT":
						{
							browserObj.select(xpathString, vals1[1]);
							break;
						}
						case "INPUT":
						{
							xpathStringContains="//"+TagName+"[ contains(@"+args[0]+", '"+vals1[0]+"')"
									+ " and not (@"+args[0]+"='"+vals1[0]+"')]";
							
							if((browserObj.getAttribute(xpathString, "type")).toUpperCase().equals("TEXT"))
							{
								browserObj.clear(xpathString);
								browserObj.type(xpathString, vals1[1]);
								break;							
							}
							else if((browserObj.getAttribute(xpathStringContains, "type")).toUpperCase().equals("CHECKBOX"))
							{	
								if ((browserObj.isChecked(xpathStringContains)+"").toUpperCase().equals("FALSE") && vals1[1].toUpperCase().equals("TRUE"))
								{
									browserObj.click(xpathStringContains);
								}
								else if ((browserObj.isChecked(xpathStringContains)+"").toUpperCase().equals("TRUE") && vals1[1].toUpperCase().equals("FALSE"))
								{
									browserObj.click(xpathStringContains);
								}
							}

						}					
					}					
				}		

				//select[@name='com.i2.x2.util.configurability.searchType']				
				break;
			}
			case "SENDKEYS": {
				browserObj.type(args[0], args[1]);
				break;
			}
			case "CHECKBOX": {
				boolean checkedStatus = browserObj.isChecked(args[0]);
				if (args[1].trim().equalsIgnoreCase("CHECK") || args[1].trim().equalsIgnoreCase("TRUE")) {
					if (!checkedStatus) {
						browserObj.click(args[0]);
					}
				} else if (args[1].trim().equalsIgnoreCase("UNCHECK") || args[1].trim().equalsIgnoreCase("FALSE")) {
					if (checkedStatus) {
						browserObj.click(args[0]);
					}
				}
				break;
			}			
			case "SELECTFRAME": {
				browserObj.selectFrame(args[0]);
				break;
			}
			case "SWITCHFRAME": {
				browserObj.selectFrame(args[0]);
				break;
			}
			case "FILLFORM": {

				String[] vals = args[1].split(",");

				Form form = new Form(browserObj, args[0], vals[0], vals[1],
						vals[2]);

				form.fill();

				break;
			}
			case "READFORM": {

				String[] vals = args[1].split(",");

				Form form = new Form(browserObj, args[0], vals[0], vals[1],
						vals[2]);

				boolean readStatus = form.read();

				if (!readStatus) {
					throw new WebDriverException("Detailed Read Failed");
				}

				break;
			}

			case "UPDATEFORM": {

				String[] vals = args[1].split(",");

				Form form = new Form(browserObj, args[0], vals[0], vals[1],
						vals[2]);

				form.fill(true);

				break;
			}

			case "DELETEALLFECOLS": {

				Actions oAc = (Actions) browserObj.getActions();

				List<WebElement> l = browserObj.findElements(args[0]);

				if (l.size() >= 2) {
					for (int i = 2; i <= l.size(); i++) {

						oAc.keyDown(Keys.CONTROL);
						oAc.perform();

						if (browserObj.getBrowserType().equals("iexplore")) {
							Robot robot = new Robot();
							robot.keyPress(KeyEvent.VK_CONTROL);
						}

						String locator = "//*[@id='editableGridDiv']/div[2]/table/tbody/tr["
								+ i + "]/td[2]";
						browserObj.scrollIntoView(locator);

						browserObj.click(locator);

						oAc.keyUp(Keys.CONTROL);
						oAc.perform();

						if (browserObj.getBrowserType().equals("iexplore")) {
							Robot robot = new Robot();
							robot.keyRelease(KeyEvent.VK_CONTROL);
						}

						Thread.sleep(1000);

					}

					browserObj.click("//*[@id='editableGridHeader_Delete']");
				}

				break;
			}

			case "FECOLSETTINGS": {

				Actions oAc = (Actions) browserObj.getActions();

				List<WebElement> l = browserObj.findElements(args[0]);

				if (l.size() >= 2) {

					browserObj
							.click("//*[@id='editableGridDiv']/div[2]/table/tbody/tr[3]");

					List<WebElement> l1 = browserObj
							.findElements("//*[@id='editableGridDiv']/div[2]/table/tbody/tr[3]/td");

					l1.get(0).click();

				}

				break;
			}

			case "FEDISPLAYNAME": {

				Actions oAc = (Actions) browserObj.getActions();

				List<WebElement> l = browserObj.findElements(args[0]);

				if (l.size() >= 2) {
					browserObj
							.click("//*[@id='editableGridDiv']/div[2]/table/tbody/tr[2]");

					List<WebElement> l1 = browserObj
							.findElements("//*[@id='editableGridDiv']/div[2]/table/tbody/tr[2]/td");

					oAc.doubleClick(l1.get(2)).perform();

					oAc.sendKeys(args[1]).build().perform();

				}

				break;
			}

			case "RUNFESEARCH": {

				browserObj.click("//*[@id='search_content']");
				browserObj.clear("//*[@id='search_content']");

				break;

			}

			case "VALIDATETABLEROW": {

				String[] vals = args[1].split(",");
				Properties props = new Properties();

				try {

					props.load(new FileInputStream(new File(FileUtils
							.getFileLocation("locators", args[0]))));

				} catch (Exception e) {
					MainEngine.log
							.info("Failed to load the Table Properties, hence using the DHTMLXtable");
				}

				TableInterface table;

				if (props.get("tableImplementedClassName") != null) {

					Class<?> c = Class.forName(props.get(
							"tableImplementedClassName").toString());
					Constructor<?> cons = c.getConstructor(com.jda.qa.platform.framework.browser.TestBrowser.class,
							String.class, String.class, String.class,
							String.class);
					table = (TableInterface) cons.newInstance(browserObj,
							args[0], vals[0], vals[1], vals[2]);

				} else {
					table = new DHTMLXTable(browserObj, args[0], vals[0],
							vals[1], vals[2]);
				}

				table.loadTable();

				if (!table.valiadateTableRowContent()) {

					throw new WebDriverException(
							"Failed to validate the Table Row Content");
				}

				break;
			}
			
			case "VALIDATETABLEROWS": {

				String[] vals = args[1].split(",");
				Properties props = new Properties();

				try {

					props.load(new FileInputStream(new File(FileUtils
							.getFileLocation("locators", args[0]))));

				} catch (Exception e) {
					MainEngine.log
							.info("Failed to load the Table Properties, hence using the DHTMLXtable");
				}

				MultiSelectTableInterface table;
				table=null;

				if (props.get("tableImplementedClassName") != null) {

					Class<?> c = Class.forName(props.get(
							"tableImplementedClassName").toString());
					Constructor<?> cons = c.getConstructor(com.jda.qa.platform.framework.browser.TestBrowser.class,
							String.class, String.class, String.class,
							String.class);
					table = (MultiSelectTableInterface) cons.newInstance(browserObj,
							args[0], vals[0], vals[1], vals[2]);

				}
				table.loadTable();

				if (!table.valiadateTableRowsContent()) {

					throw new WebDriverException(
							"Failed to validate the Table Row Content");
				}

				break;
			}			

			case "SETTABLEROWCONTENT": {

				String[] vals = args[1].split(",");
				Properties props = new Properties();

				try {

					props.load(new FileInputStream(new File(FileUtils
							.getFileLocation("locators", args[0]))));

				} catch (Exception e) {
					MainEngine.log
							.info("Failed to load the Table Properties, hence using the FEPropertiesTable");
				}

				TableInterface table;

				if (props.get("tableImplementedClassName") != null) {

					Class<?> c = Class.forName(props.get(
							"tableImplementedClassName").toString());
					Constructor<?> cons = c.getConstructor(com.jda.qa.platform.framework.browser.TestBrowser.class,
							String.class, String.class, String.class,
							String.class);
					table = (TableInterface) cons.newInstance(browserObj,
							args[0], vals[0], vals[1], vals[2]);

				} else {
					table = new FEPropertiesTable(browserObj, args[0], vals[0],
							vals[1], vals[2]);
				}

				table.loadTable();

				((InlineEditTableInterface) table).setTableRowContent();

				break;
			}

			case "SETTABLEROWSCONTENT": {
				String[] vals = args[1].split(",");
				Properties props = new Properties();
				try {
					props.load(new FileInputStream(new File(FileUtils
							.getFileLocation("locators", args[0]))));
				} catch (Exception e) {
					MainEngine.log
							.info("Failed to load the Table Properties, hence using the FEPropertiesTable");
				}
				MultiSelectTableInterface table=null;
				if (props.get("tableImplementedClassName") != null) {
					Class<?> c = Class.forName(props.get(
							"tableImplementedClassName").toString());
					Constructor<?> cons = c.getConstructor(com.jda.qa.platform.framework.browser.TestBrowser.class,
							String.class, String.class, String.class,
							String.class);
					table = (MultiSelectTableInterface) cons.newInstance(browserObj,
							args[0], vals[0], vals[1], vals[2]);
				}
				table.loadTable();				
				((MultiInlineEditTableInterface) table).setTableRowsContent();
				break;
			}
			case "VALIDATEBASICTABLEROW": {

				String[] vals = args[1].split(",");

				TableInterface table = new BasicTable(browserObj, args[0],
						vals[0], vals[1], vals[2]);

				table.loadTable();

				boolean compStatus = table.valiadateTableRowContent();

				if (!compStatus) {
					throw new WebDriverException(
							"Failed to validate the Table Row Content");
				}

				break;
			}

			case "SELECTTABLEROW": {

				String[] vals = args[1].split(",");
				Properties props = new Properties();

				try {

					props.load(new FileInputStream(new File(FileUtils
							.getFileLocation("locators", args[0]))));

				} catch (Exception e) {
					MainEngine.log
							.info("Failed to load the Table Properties, hence using the DHTMLXtable");
				}

				TableInterface table;

				if (props.get("tableImplementedClassName") != null) {

					Class<?> c = Class.forName(props.get(
							"tableImplementedClassName").toString());
					Constructor<?> cons = c.getConstructor(com.jda.qa.platform.framework.browser.TestBrowser.class,
							String.class, String.class, String.class,
							String.class);

					table = (TableInterface) cons.newInstance(browserObj,
							args[0], vals[0], vals[1], vals[2]);

				} else {
					table = new DHTMLXTable(browserObj, args[0], vals[0],
							vals[1], vals[2]);
				}

				table.loadTable();

				table.selectTableRow();

				break;
			}
			
			case "SELECTTABLEROWS": {

				String[] vals = args[1].split(",");
				Properties props = new Properties();

				try {

					props.load(new FileInputStream(new File(FileUtils
							.getFileLocation("locators", args[0]))));

				} catch (Exception e) {
					MainEngine.log
							.info("Failed to load the Table Properties, hence using the DHTMLXtable");
				}

				MultiSelectTableInterface table;
				table=null;

				if (props.get("tableImplementedClassName") != null) {

					Class<?> c = Class.forName(props.get(
							"tableImplementedClassName").toString());
					Constructor<?> cons = c.getConstructor(com.jda.qa.platform.framework.browser.TestBrowser.class,
							String.class, String.class, String.class,
							String.class);

					table = (MultiSelectTableInterface) cons.newInstance(browserObj,
							args[0], vals[0], vals[1], vals[2]);

				}
				table.loadTable();

				table.selectTableRows();

				break;
			}
			
			case "DOTABLEACTION": {

				String[] vals = args[1].split(",");

				Properties props = new Properties();

				try {

					props.load(new FileInputStream(new File(FileUtils
							.getFileLocation("locators", args[0]))));

				} catch (Exception e) {
					MainEngine.log
							.info("Failed to load the Table Properties, hence using the DHTMLXtable");
				}

				TableInterface table;

				if (props.get("tableImplementedClassName") != null) {

					Class<?> c = Class.forName(props.get(
							"tableImplementedClassName").toString());
					Constructor<?> cons = c.getConstructor(com.jda.qa.platform.framework.browser.TestBrowser.class,
							String.class, String.class, String.class,
							String.class);

					table = (TableInterface) cons.newInstance(browserObj,
							args[0], vals[0], vals[1], vals[2]);

				} else {
					table = new DHTMLXTable(browserObj, args[0], vals[0],
							vals[1], vals[2]);
				}

				table.loadTable();

				table.doTableAction(vals[3]);

				break;
			}
			case "SELECTBASICTABLEROW": {

				String[] vals = args[1].split(",");

				TableInterface table = new BasicTable(browserObj, args[0],
						vals[0], vals[1], vals[2]);

				table.loadTable();

				table.selectTableRow();

				break;
			}

			case "NAVIGATETOPAGE": {

				Actions act = (Actions) browserObj.getActions();
				
				boolean isPageNavigationSuccessful = false;

				// Type the Content in the Input Field

				browserObj.switchToDefaultContent();

				browserObj.sleep(1000);

				browserObj.type("//input[@id='_directoryInput']", args[0]);

				browserObj.waitForPageToLoad();

				// get the List of the available Web Elements
				List<WebElement> oList = browserObj
						.findElements("//*[contains(@class,'listBoxItem directory')]");

				// Locator of the directory listing
				WebElement oWe = ((WebDriver) browserObj.getWebDriver())
						.findElement(By
								.xpath("//*[contains(@class,'directoryDetailsInner')]"));

				// If the path of the directory search element is given then
				// it
				// will check the patch given and then click the element

				WebDriver driver = (WebDriver) browserObj.getWebDriver();

				if (args.length == 2) {

					String val = oWe.getText();

					for (WebElement dirListingWebElement : oList) {

						((JavascriptExecutor) driver).executeScript(
								"arguments[0].scrollIntoView();",
								dirListingWebElement);

						act.moveToElement(dirListingWebElement).build()
								.perform();

						String listingName = dirListingWebElement.getText();
						String listingExtension = oWe.getText();

						if (listingName.equals(args[0])
								&& listingExtension.equals(args[1])) {
							dirListingWebElement.click();
							isPageNavigationSuccessful= true;
							browserObj
							  .highlightBorder("//span[contains(@class,'shellBannerText')]");

							break;

						}
					}

				} else {

					for (WebElement dirListingWebElement : oList) {

						((JavascriptExecutor) driver).executeScript(
								"arguments[0].scrollIntoView();",
								dirListingWebElement);

						String listingName = dirListingWebElement.getText();

						if (listingName.equals(args[0])) {
							dirListingWebElement.click();
							isPageNavigationSuccessful= true;
							browserObj
								.highlightBorder("//span[contains(@class,'shellBannerText')]");
							break;

						}
					}
				}

				if(!isPageNavigationSuccessful){
					throw new WebDriverException(
							"Failed to Navigate to the Page : "+args[0]);
				}
				
				break;
			}
			case "VERIFYELEMENTPRESENT": {

				if (args.length == 2) {
					
					String val = args[1];
					
					if(val.endsWith("#RM#")){
						
						val = val.replaceAll("#RM#", "");
						val = val.replaceAll(",", "\\.");
					}
					
					args[1] = val;
					
					args[0] = LocatorFormatter.formatLocator(args[0],
							args[1].split("#"));
				} else {
					args[0] = LocatorFormatter.formatLocator(args[0]);
				}

				if (!browserObj.isElementPresent(args[0])) {
					throw new WebDriverException(
							"Failed to verify the element presence");
				}

				break;
			}
			case "VERIFYELEMENTNOTPRESENT": {

				if (args.length == 2) {
					args[0] = LocatorFormatter.formatLocator(args[0],
							args[1].split("#"));
				} else {
					args[0] = LocatorFormatter.formatLocator(args[0]);
				}

				if (browserObj.isElementPresent(args[0], true)) {
					throw new WebDriverException(
							"Failed to verify the element absence");
				}

				break;
			}

			case "CALLEXCELSHEET": {

				String[] sOption1_Values = args[0].split(",");
				String sFileName = sOption1_Values[0];
				String sSheetname = sOption1_Values[1];
				String params = "";
				if (args.length == 1) {
					params = "";
				} else {
					params = args[1];
				}

				ExcelSheetCaller oExcelCaller = new ExcelSheetCaller(
						browserObj, sFileName, sSheetname);
				oExcelCaller.callSheet(params);
				break;
			}

			case "VERIFYELEMENTATTRIBUTE": {

				String sOption1 = args[0];

				String[] sOption2Vals = args[1].split(",");

				String sActualValue = browserObj.getAttribute(args[0],
						sOption2Vals[0]);

				String sExpectedValue = sOption2Vals[1];

				if (sActualValue.equals(sExpectedValue)) {
					System.out
							.println("The Actual value is equal to the Expected Value. Actual Value: "
									+ sActualValue
									+ "Expected Value: "
									+ sExpectedValue);
				} else {
					System.out
							.println("The Actual value is not equal to the Expected Value. Actual Value: "
									+ sActualValue
									+ "Expected Value: "
									+ sExpectedValue);
				}
				break;
			}
			case "VERIFYSORTONCOLUMNDATA": {

				boolean bActualValue = false, bExpectedValue = true;

				String sOption1 = args[0];

				String[] sOption2 = args[1].split(",");

				DHTMLXTable oTable = new DHTMLXTable(browserObj, sOption2[2]);
				oTable.loadTable();

				bActualValue = oTable.verifySortOnTableData(sOption2[0],
						sOption2[1]);

				if (bActualValue) {
					System.out
							.println("The Actual value is equal to the Expected Value. Actual Value: "
									+ bActualValue
									+ "Expected Value: "
									+ bExpectedValue);
				} else {

					System.out
							.println("The Actual value is not equal to the Expected Value. Actual Value: "
									+ bActualValue
									+ "Expected Value: "
									+ bExpectedValue);

					throw new WebDriverException(
							"Failed to verify the element presence");

				}

				break;
			}

			case "COMPAREEXCELSHEETS": {

				boolean bActualValue = false, bExpectedValue = true;

				String[] vals = args[1].split(",");

				compareExcelSheets oComp = new compareExcelSheets(vals[0],
						vals[1], vals[2], vals[3]);
				bActualValue = oComp.compareSheets();

				if (bActualValue) {
					System.out
							.println("The Actual value is equal to the Expected Value. Actual Value: "
									+ bActualValue
									+ " Expected Value: "
									+ bExpectedValue);
				} else {

					System.out
							.println("The Actual value is not equal to the Expected Value. Actual Value: "
									+ bActualValue
									+ " Expected Value: "
									+ bExpectedValue);

					throw new WebDriverException(
							"Failed to verify the element presence");
				}

				break;
			}

			case "WAITFORELEMENT": {

				Thread.sleep(2000);

				browserObj.isElementPresent(args[0], Integer.parseInt(args[1]));

				break;

			}

			case "VERIFYELEMENTTEXT": {

				String actText = browserObj.getText(args[0]).trim();

				MainEngine.log.info("Comparing the Text");
				MainEngine.log.info("Actual Text : " + actText);
				MainEngine.log.info("Expected Text : " + args[1]);

				if (!actText.equals(args[1])) {
					throw new WebDriverException("Failed to validate the text");
				} else {
					MainEngine.log.info("Text Comparision successfull");
				}

				break;
			}

			case "VERIFYELEMENTVALUE": {

				String actText = browserObj.getValue(args[0]).trim();

				MainEngine.log.info("Comparing the Text");
				MainEngine.log.info("Actual Text : " + actText);
				MainEngine.log.info("Expected Text : " + args[1]);

				if (!actText.equals(args[1])) {
					throw new WebDriverException(
							"Failed to validate the element value");
				} else {
					MainEngine.log.info("Text Comparision successfull");
				}

				break;
			}

			case "VERIFYELEMENTSELECTEDSTATUS": {

				String actText = browserObj.isSelected(args[0]) + "".trim();

				MainEngine.log.info("Comparing the Text");
				MainEngine.log.info("Actual Text : " + actText);
				MainEngine.log.info("Expected Text : " + args[1]);

				if (!actText.equals(args[1])) {
					throw new WebDriverException(
							"Failed to validate the element value");
				} else {
					MainEngine.log
							.info("Selected status comparision successfull");
				}

				break;

			}

			case "SWITCHTOWINDOW": {
				
				WebDriver driver = (WebDriver) browserObj.getWebDriver();

				browserObj.updateWindowHandles();

				LinkedHashSet<String> windowHandles = new LinkedHashSet<String>(
						browserObj.getWindowHandles());

				ArrayList<String> listWindowHandles = new ArrayList<String>(
						windowHandles);

				String windowHandleName = listWindowHandles.get(new Integer(
						args[0]));

				browserObj.setPopUpWindowname(windowHandleName);

				browserObj.selectWindow(windowHandleName);

				break;

			}
			 
			case "MUOROWSELECTION": {
				
				if (args[0].contains("#VALUE#")) {
					args[0] = args[0].replace("#VALUE#", args[1]);
				}

                String[] myStringlist = args[0].split(",");                
                String appendR=" and ";

                for (int i = 0; i < myStringlist.length; i++) {
                    String[] a = myStringlist[i].split("~");
                    String LocatorStr="";
                    String selRowLoc="//div[contains(@id,'gridview')]//descendant::tr[";
                    for (int j=0;j<a.length;j++ )
                    {
                        LocatorStr = LocatorStr + "td/div='"+ a[j]+"'";
                        if (j<a.length-1)
                        {
                            LocatorStr =LocatorStr+appendR;
                        }
                    }
                    selRowLoc=selRowLoc+LocatorStr+"]/td[1]";
                    browserObj.click(selRowLoc);
                }

                break;

            }
			case "REPLACEFILECONTENT": {
				try
				{
				String line="";
				String oldtext="";

				String fileLocation=args[1].split(",")[0];
				String searchText=args[1].split(",")[1];
				String replaceText=args[1].split(",")[2];
				Boolean replacement=false;
				
				fileLocation=fileLocation.replace("$", ".");
				if(!(fileLocation.contains(":\\") || fileLocation.contains(":/")))
				{
					fileLocation="#SETUP_DIR#/data/"+fileLocation;
					fileLocation=LocatorFormatter.updateDBValues(fileLocation).toString();
				}
				

				File genericFile = new File(fileLocation);
				BufferedReader linecontent = new BufferedReader(new FileReader(genericFile));
					while ((line=linecontent.readLine())!=null)
					{
						if(line.contains(searchText))
						{						
							line=line.replaceAll(searchText,replaceText);
							replacement=true;
						}						
						oldtext+=line+"\r\n";
					}
					linecontent.close();
					if (replacement==true)
					{
					FileWriter writer = new FileWriter(fileLocation);
					writer.write(oldtext);
					writer.flush();
					writer.close();
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			break;

			case "CLOSEWINDOW": {
				browserObj.closeWindow(browserObj.getPopUpWindowname());
				break;
			}

			case "SWITCHTOMAINWINDOW": {
				WebDriver driver = (WebDriver) browserObj.getWebDriver();

				browserObj.updateWindowHandles();

				HashSet<String> windowHandles = new HashSet<String>(
						browserObj.getWindowHandles());

				ArrayList<String> listWindowHandles = new ArrayList<String>(
						windowHandles);

				String windowHandleName = listWindowHandles
						.get(listWindowHandles.size() - 1);

				driver.switchTo().window(browserObj.getMainWindowHandle());
				
				browserObj.setPopUpWindowname(null);

				break;
			}

			case "HIGHLIGHT": {
				browserObj.highlight(args[0]);
				break;
			}

			case "WRITEFEDATATOEXCEL": {

				String[] vals = args[1].split(",");

				VPTable table = new VPTable(browserObj, args[0], vals[0],
						vals[1], vals[2]);

				table.loadTable();

				table.writeFEDatatoExcel();

				break;
			}

			case "MOVETOELEMENT": {

				browserObj.moveToElement(args[0]);
				break;

			}
			case "MOVETOELEMENTREPLACE": {

				String locator = args[0].replace("#VALUE#", args[1]);
				browserObj.moveToElement(locator);
				break;

			}

			case "RELAUNCHBROWSER": {
				
				try{
					browserObj.close();
					browserObj.stop();
				} catch(Throwable t){
					t.printStackTrace();
					MainEngine.log.info("Failed to Close the browser, via RELAUNCHBROWSER "+t.getMessage());
				}finally{
					String URL = MainEngine.browser.getURL();
					String browserType = MainEngine.browser.getBrowserType();
	
					com.jda.qa.platform.framework.browser.TestBrowser newBrowser = new com.jda.qa.platform.framework.browser.WebDriverBrowser(URL, browserType);
	
					MainEngine.browser = newBrowser;
	
					browserObj = MainEngine.browser;
				}

				break;

			}

			case "REFRESHPAGE": {
				browserObj.refresh();
				break;
			}

			case "VERIFYXPATHCOUNT": {
				int actualXpathCount = browserObj.getXpathCount(args[0]);

				if (actualXpathCount == new Integer(args[1])) {
					MainEngine.log.info("XPath count comparision successful");
				} else {
					MainEngine.log.info("Failed to validate the XPath count");
					MainEngine.log.info("Actual XPath count : "
							+ actualXpathCount);
					MainEngine.log.info("Expected XPath count : " + args[1]);
					throw new WebDriverException(
							"Failed to validate the XPath count");
				}

				break;
			}
			case "DOUBLECLICK": {

				Actions act = (Actions) browserObj.getActions();

				WebElement locator = (WebElement) browserObj
						.findElement(args[0]);

				act.doubleClick(locator).build().perform();

				break;
			}
			case "ACCEPTCONFIRMATION": {
				
                browserObj.manageConfirmation("ACCEPT");

                break;
            }
			case "ACCEPTCONFIRMATIONTEXTINPUT": {

				browserObj.manageConfirmationTextInput("ACCEPT",args[1]);
				               
                break;
			}
            case "CANCELCONFIRMATION": {

                browserObj.manageConfirmation("DISMISS");

                break;
            }
            
            case "VALIDATECONFIRMATIONTEXT": {

                browserObj.validateConfirmationText(args[0]);                

                break;
            }
            
            case "SCROLLTOELEMENT": {

                browserObj.scrollIntoView(args[0]);                

                break;
            }
            
            case "STORETEXTTOVARIABLE":{
            	
            	String appText = browserObj.getText(args[0], true);
            	
            	MainEngine.varManager.storeVariable(args[1], appText);
            	
            	break;
            }
            
            case "REMOVEALLSELECTIONS":{
            	
            	browserObj.removeAllSelections(args[0]);
            	
            	break;
            }
            
			case "INVOKESQLPROCESS": {
				
				String DBURL = LocatorFormatter.formatLocator(args[0]);

				String[] params = args[1].split(",");

				ExcelData data = new ExcelData("Contents.xlsx", params[0]);
				HashMap<String, String> objectDetails = data
						.getExcelData(params[1]);
				
				Runnable sqlRunner = new SQLRunner(DBURL,
						LocatorFormatter.formatLocator(objectDetails
								.get("SQL_INPUT")),
						LocatorFormatter.formatLocator(objectDetails
								.get("LOG_FILE")));

				Thread sqlThread = new Thread(MainEngine.seleniumThreads, sqlRunner);
				
				sqlThread.setName(objectDetails.get("THREAD_NAME"));

				sqlThread.start();

				break;
			}
            
			case "ASSERTTHREADLOGOUTPUT": {

				String[] params = args[0].split(",");

				ExcelData data = new ExcelData("Contents.xlsx", params[0]);
				HashMap<String, String> objectDetails = data
						.getExcelData(params[1]);

				String threadName = LocatorFormatter
						.formatLocator(objectDetails.get("THREAD_NAME"));

				String fileName = LocatorFormatter.formatLocator(objectDetails
						.get("LOG_FILE"));

				String searchContent = LocatorFormatter
						.formatLocator(objectDetails.get("SEARCH_CONTENT"));

				long waitTime = new Long(
						LocatorFormatter.formatLocator(objectDetails
								.get("WAIT_TIME")));

				Thread actualThread = null;

				ThreadGroup threadGroupObj = MainEngine.seleniumThreads;

				Thread[] allThreads = new Thread[MainEngine.seleniumThreads.activeCount()];

				threadGroupObj.enumerate(allThreads);

				for (Thread currentThread : allThreads) {

					actualThread = currentThread;

					threadName = currentThread.getName();

					if (threadName.equals(threadName)) {
						break;
					}
					actualThread = null;
				}
				
				if(actualThread!=null){

					if (actualThread.getState() == State.RUNNABLE
							&& actualThread.isAlive()) {
						Thread.sleep(waitTime);
					}

				if (actualThread.getState() == State.RUNNABLE
							&& actualThread.isAlive()) {
						MainEngine.log
								.info("Thread "
										+ threadName
										+ " is still active, hence interupting the Thread");
						if (!actualThread.isInterrupted()) {
							actualThread.interrupt();
						} else {
							actualThread.destroy();
						}
					}
				
				}

				boolean searchStatus = FileUtils.fileContains(new File(
						FileUtils.getFileLocation("logs", fileName)),
						searchContent);

				if (!searchStatus) {
					MainEngine.log.info("Unable to find the text "
							+ searchContent + " in the file " + fileName);
					throw new WebDriverException("Failed to find the text "
							+ searchContent + " in the file " + fileName);
				} else {
					MainEngine.log.info("Found the text " + searchContent
							+ " in the file " + fileName);
				}

				break;
			}
			
			case "UPDATEMETHODINVOKER":{
				com.jda.qa.platform.framework.browser.MethodInvoker caller = (com.jda.qa.platform.framework.browser.MethodInvoker) Class.forName(args[0])
						.getConstructor().newInstance();
				MainEngine.setCaller(caller);
				break;
			}
			
			case "SWITCHTOBASEINVOKER":{
				com.jda.qa.platform.framework.browser.MethodInvoker caller = (MethodInvoker) Class
						.forName(MainEngine.methodInvokerClassName)
						.getConstructor().newInstance();
				MainEngine.setCaller(caller);
				break;
			}
			
			case "SWITCHTOBROWSER":{
				MainEngine.browser = MainEngine.browserStack.get(new Integer(
						args[0]));
				break;
			}
			case "SWITCHTOBASEBROWSER":{
				MainEngine.browser = MainEngine.browserStack.get(0);
				break;
			}
			case "LAUNCHNEWBROSWER" : {
				com.jda.qa.platform.framework.browser.TestBrowser newBrowser = new WebDriverBrowser(browserObj.getURL(), browserObj.getBrowserType());
				break;
			}
			case "CLOSEBROWSER": {
				TestBrowser closedBrowser = MainEngine.browser;

				closedBrowser.close();
				closedBrowser.stop();

				MainEngine.browserStack.remove(closedBrowser);

				if (MainEngine.browserStack.get(0) != null) {
					MainEngine.browser = MainEngine.browserStack.get(0);
				}

				break;

			}
			case "UPDATELOCALE": {
				MainEngine.setLocale(args[0]);
				break;
			}
			
			case "RESETLOCALE": {
				MainEngine.resetLocale();
				break;
			}
			
			case "DRAGANDDROP":{
				
				MainEngine.log.info("Performing the Drag and Drop for the Source locators :"+args[0]+" to Target Locator : "+args[1]);
				
				Actions actions = (Actions) browserObj.getActions();
				
				WebElement sourceLocator = (WebElement) browserObj
						.findElement(args[0]);
				
				WebElement targetLocator = (WebElement) browserObj
						.findElement(args[1]);
				
				MainEngine.log.info("Working on Drag and Drop for the Source locators :"+args[0]+" to Target Locator : "+args[1]);
				
				actions.dragAndDrop(sourceLocator, targetLocator).build().perform();
				
				MainEngine.log.info("Completed on Drag and Drop for the Source locators :"+args[0]+" to Target Locator : "+args[1]);
				
			}
			
			case "RIGHTCLICK":{
				
				MainEngine.log.info("Performing the Right/Context Click operation for the locator :"+args[0]);
				
				Actions actions = (Actions) browserObj.getActions();
				
				WebElement sourceLocator = (WebElement) browserObj
						.findElement(args[0]);
				
				MainEngine.log.info("Working the Right/Context Click operation for the locator :"+args[0]);
				
				actions.contextClick(sourceLocator).build().perform();
				
				MainEngine.log.info("Completed the Right/Context Click operation for the locator :"+args[0]);
			}
			
			case "PASS": {
				System.out.println("passed");
				break;
			}
			case "FAIL": {
				System.out.println("failed");
				throw new WebDriverException("Sample Fail");
			}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new WebDriverException(e.getMessage());
		}
	}

	public String[] buildDynamicProperties(String methodName, String[] args) {

		if (methodName.toUpperCase().contains("TABLE")
				|| methodName.toUpperCase().contains("FORM")) {

			String param0 = args[0];
			String param1 = args[1];

			String[] param1Vals = args[1].split(",");

			if (param1Vals[0].contains("\\")
					&& param1Vals[0].toLowerCase().endsWith(":properties")) {
				args[0] = param1Vals[0].replace(":properties", ".properties");

				param1 = param1.substring(param1.indexOf(",") + 1,
						param1.length());

				args[1] = param1;
			}

			return args;
		} else {

			return args;
		}

	}
}