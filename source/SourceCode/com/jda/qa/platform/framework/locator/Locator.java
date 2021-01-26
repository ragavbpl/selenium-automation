package SourceCode.com.jda.qa.platform.framework.locator;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.jda.qa.platform.framework.locator.LocComparator;
import com.jda.qa.platform.framework.locator.LocatorFormatter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.jda.qa.platform.framework.base.MainEngine;
import com.jda.qa.platform.framework.browser.MethodInvoker;
import com.jda.qa.platform.framework.browser.TestBrowser;
import com.jda.qa.platform.framework.util.AutoItRunner;
import com.jda.qa.platform.framework.util.FileUtils;

/**
* 
 * This class will be used to work with a locators on a page
* 
 * 
 * @author Santosh Kumar Medchalam and Vinay Kumar Tangella
* 
 */

public class Locator {

       public static enum locatorType {
              LINK, TEXT_FIELD, TEXT_FIELD_VALUE, TEXT_AREA, BUTTON, SELECT, SELECT_VALUE, RADIO_BUTTON, CHECK_BOX, TEXT, COMBOSELECT, ROLE_COMBOSELECT, ASEARCH_COMBOSELECT, FILE, ELEMENT, DOUBLECLICK, SKIP, TABLE_LISTCOMBOSELECT, EXTJS_TEXTAREA_SELECTION, CLICK_DOUBLECLICK,FILE_UPLOAD,PASTE_SCREENSHOT, DD_MULTI_VALUES_SELECT;
       }

       public static enum locatorAction {
              WAIT, POP_UP, ALERT, CONFIRMATION_YES, CONFIRMATION_NO, FRAME, PROMPT, TYPE, SELECT, REMOVEALL, SKIP, SETELEMENTATTRIBUTE,REMOVEELEMENTATTRIBUTE, REMOVEALLSELECTIONS, HIGHLIGHT;
       }

       File locatorFile;
       String locatorFileLocation;
       TestBrowser browser;
       Properties locatorsProp = new Properties();
       MethodInvoker caller = MainEngine.caller;
       WebDriver driver;

       HashMap<String, String> locators;
       String tempLocator;

       /**
       * 
        * The constructor used to create the locator object
       * 
        * @param locatorFileLocation
       *            --> The Properties file which will be used to load the
       *            locators on the page.
       * @param browser
       *            --> The browser object using which the actions will be
       *            performed on the page
       */

       @SuppressWarnings({ "unchecked", "rawtypes" })
       public Locator(String locatorFileLocation, TestBrowser browser) {

              this.browser = browser;
              this.driver = (WebDriver) browser.getWebDriver();

              locatorFile = new File(FileUtils.getFileLocation("locators",
                           locatorFileLocation));

              try {

                     locatorsProp.load(new FileReader(locatorFile));
                     locators = new HashMap<String, String>((Map) locatorsProp);

              } catch (Exception e) {
                     System.out.println("Failed to load the Locators from file");
              }

       }

       /**
       * 
        * The method will return the locator of an element in the properties file
       * 
        * @param locatorId
       *            --> The element of which the locator needs to be returned.
       * @return --> the Locator of the element
       */

       public String getLocator(String locatorId) {

              if (locators.get(locatorId) == null) {
                     return null;
              } else {

                     String loc = locators.get(locatorId).toString();

                     String formatedLoc = LocatorFormatter.formatLocator(loc);

                     return formatedLoc;
              }

       }

       /**
       * 
        * The Type of the locator element
       * 
        * @param locatorId
       *            --> The locatorId whose type is returned
       * @return --> The type of the locator element
       */
       public String getLocatorType(String locatorId) {

              if (locators.get(locatorId + ".type") != null) {
                     return locators.get(locatorId + ".type").toString();
              } else {
                     return "";
              }

       }

       /**
       * 
        * The Event of the locator element
       * 
        * @param locatorId
       *            --> The locatorId whose event is returned
       * @return --> The event of the locator element
       */

       public String getLocatorEvent(String locatorId) {

              if (locators.get(locatorId + ".event") != null) {
                     return locators.get(locatorId + ".event").toString();
              } else {
                     return "";
              }

       }

       /**
       * 
        * The Action of the locator element
       * 
        * @param locatorId
       *            --> The locatorId whose Action is returned
       * @return --> The Action of the locator element
       */

       public String getLocatorAction(String locatorId) {

              if (locators.get(locatorId + ".action") != null) {
                     return locators.get(locatorId + ".action").toString();
              } else {
                     return "";
              }

       }

       /**
       * 
        * The PreAction of the locator element
       * 
        * @param locatorId
       *            --> The locatorId whose PreAction is returned
       * @return --> The PreAction of the locator element
       */

       public String getLocatorPreAction(String locatorId) {

              if (locators.get(locatorId + ".preAction") != null) {
                     return locators.get(locatorId + ".preAction").toString();
              } else {
                     return "";
              }

       }

       /**
       * 
        * The DBColName of the locator element
       * 
        * @param locatorId
       *            --> The locatorId whose DBColName is returned
       * @return --> The DBColName of the locator element
       */

       public String getLocatorDBColName(String locatorId) {

              if (locators.get(locatorId + ".DBColName") != null) {
                     return locators.get(locatorId + ".DBColName").toString();
              } else {
                     return "";
              }

       }

       /**
       * 
        * The postAction of the locator element
       * 
        * @param locatorId
       *            --> The locatorId whose postAction is returned
       * @return --> The postAction of the locator element
       */

       public String getLocatorPostAction(String locatorId) {

              if (locators.get(locatorId + ".postAction") != null) {
                     return locators.get(locatorId + ".postAction").toString();
              } else {
                     return "";
              }

       }

       /**
       * 
        * This method will be used to do an action on the WebElement identified
       * with the help of locatorId
       * 
        * @param locatorId
       *            --> The locatorId upon which the action will be performed
       * @param value
       *            --> The Value using which the action is performed on the
       *            WebElement
       */

       @SuppressWarnings("incomplete-switch")
	public void doAction(String locatorId, String value) {
    	   
		value = LocatorFormatter.updateDBValues(value);
		
		String locator = getLocator(locatorId);
		String preLocator = locator;
		
		if (!value.equalsIgnoreCase("SKIP")) {
			// Do Pre Action
			 doPreAction(locatorId,value);
			 
			 locator = handleLocatorsWithValues(locatorId, value);
			 
			 moveToFormElement(locator,value);
		}
		
		if ((value != null) && value.contains(":-")
				&& preLocator.contains("#VALUE#")
				&& !value.toUpperCase().contains(":MULTIPLE")) {

			String[] vals = value.split(":-");

			value = vals[1];
		}

		if (value.equalsIgnoreCase("SKIP")) {

			MainEngine.log.info("Skipping the form action");

		} else {

			String locType = getLocatorType(locatorId).toUpperCase();

			if (!(locType.trim().length() > 0)) {
				locType = "SKIP";
			}

			
			
			switch (locatorType.valueOf(locType)) {

			case LINK: {
				browser.click(locator);
				break;
			}

			case DOUBLECLICK: {

				caller.runMethod("DOUBLECLICK", locator);

				break;

			}
			
			case CLICK_DOUBLECLICK: {

				browser.sleep(500);
				browser.click(locator);
				browser.sleep(250);
				caller.runMethod("DOUBLECLICK", locator);

				break;

			}

			case FILE: {

				browser.type(locator, value);
				break;
			}
			case BUTTON: {
				browser.click(locator);
				break;
			}
			case TEXT_FIELD:
			case TEXT_AREA:
			case TEXT_FIELD_VALUE: {

				if (value.startsWith("value:")) {
					value = value.replace("value:", "");
					browser.clear(locator);
					browser.setAttribute(locator, "value", value);
				} else {
					browser.clear(locator);
					browser.type(locator, value);
				}

				break;
			}

			case CHECK_BOX: {
				boolean checkedStatus = browser.isChecked(locator);

				if (value.equalsIgnoreCase("CHECK")
						|| value.equalsIgnoreCase("TRUE")) {

					if (!checkedStatus) {
						browser.click(locators.get(locatorId + ".loc")
								.toString());
					}

				} else if (value.equalsIgnoreCase("UNCHECK")
						|| value.equalsIgnoreCase("FALSE")) {

					if (checkedStatus) {
						browser.click(locators.get(locatorId + ".loc")
								.toString());
					}

				}

				break;
			}

			case RADIO_BUTTON: {

				boolean checkedStatus = false;

				try {
					checkedStatus = browser.isChecked(locator);
				} catch (Exception e) {
					checkedStatus = false;
				}

				if (value.equalsIgnoreCase("CHECK")
						|| value.equalsIgnoreCase("TRUE")) {

					if (!checkedStatus) {
						browser.click(locator);
					}

				} else if (value.equalsIgnoreCase("UNCHECK")
						|| value.equalsIgnoreCase("FALSE")) {

					if (checkedStatus) {
						browser.click(locator);
					}

				}

				break;
			}

			case SELECT: {

				if (value.endsWith(":multiple")) {

					value = value.replaceAll(":multiple", "");

					String[] selVals = value.split(",");

					for (int i = 0; i < selVals.length; i++) {
						browser.select(locator, selVals[i]);
					}
				} else {

					browser.select(locator, value);
				}

				break;
			}
			case SELECT_VALUE: {
				if (value.endsWith(":multiple")) {

					value = value.replaceAll(":multiple", "");
					value = value.replaceAll("value:", "");

					String[] selVals = value.split(",");

					for (int i = 0; i < selVals.length; i++) {
						browser.selectByValue(locator, selVals[i]);
					}
				} else {

					browser.selectByValue(locator, value);
				}
				break;
			}
			case DD_MULTI_VALUES_SELECT: {
				
				String locatorStr =getLocator(locatorId);
				
				if (value.endsWith(":multiple")) {

					value = value.replaceAll(":multiple", "");

					String[] selVals = value.split(",");	
					for (int i = 0; i < selVals.length; i++) {
						String locatorStr1 = locatorStr.replaceAll("#VALUE#",selVals[i]);
						browser.click(locatorStr1);
					}
				} else {
					locatorStr = locatorStr.replaceAll("#VALUE#", value);
					browser.click(locatorStr);
				}
				break;					
			}			
			case COMBOSELECT: {
				browser.select(locator, value);
				break;
			}

			case ROLE_COMBOSELECT:
			case ASEARCH_COMBOSELECT: {

				boolean isValueSelect = false;

				if (value.startsWith("value:")) {
					value = value.replace("value:", "");
					isValueSelect = true;
				}

				if (value.endsWith(":multiple")) {

					value = value.replaceAll(":multiple", "");

					String[] selVals = value.split(",");

					for (int i = 0; i < selVals.length; i++) {
						browser.addSelection(locator, selVals[i], isValueSelect);
					}

				} else {

					if (isValueSelect) {
						browser.selectByValue(locator, value);
					} else {
						browser.select(locator, value);
					}

				}

				break;

			}

			case TABLE_LISTCOMBOSELECT: {
				String Locatorvalue;
				String Locatorreplace;
				Locatorvalue = locators.get(locatorId + ".loc");

				if (value.endsWith(":multiple")) {

					String[] valuesAry = value.replace(":multiple", "").split(
							",");
					Actions oAc = (Actions) browser.getActions();
					oAc.keyDown(Keys.CONTROL);
					oAc.perform();

					if (browser.getBrowserType().equals("iexplore")) {
						try {
							Robot robot = new Robot();
							robot.keyPress(KeyEvent.VK_CONTROL);
						} catch (Exception e) {
							throw new WebDriverException(
									"Filed to perform the Robot Action");
						}
					}
					if (!Locatorvalue.contains("#VALUE1#")) {
						for (int index = 0; index < valuesAry.length; index++) {
							Locatorreplace = Locatorvalue;

							Locatorreplace = Locatorreplace.replace("#VALUE#",
									valuesAry[index]);
							browser.scrollIntoView(Locatorreplace);
							browser.click(Locatorreplace);
						}
					} else if (Locatorvalue.toUpperCase().contains("#VALUE1#")) {
						for (int index = 0; index < valuesAry.length; index++) {
							Locatorreplace = Locatorvalue;
							String[] ValueOrder = valuesAry[index].split(":-");
							for (int i = 1; i <= ValueOrder.length; i++) {
								Locatorreplace = Locatorreplace.replace(
										"#VALUE" + i + "#", ValueOrder[i - 1]);
							}
							browser.scrollIntoView(Locatorreplace);
							browser.click(Locatorreplace);
						}
					}
					oAc.keyUp(Keys.CONTROL);
					oAc.perform();

					if (browser.getBrowserType().equals("iexplore")) {
						try {
							Robot robot = new Robot();
							robot.keyRelease(KeyEvent.VK_CONTROL);
						} catch (Exception e) {
							throw new WebDriverException(
									"Filed to perform the Robot Action");
						}
					}

				} else {
					if (Locatorvalue.contains("#VALUE#")) {
						browser.click(Locatorvalue.replace("#VALUE#", value));
					} else if (Locatorvalue.contains("#VALUE1#")) {
						Locatorreplace = Locatorvalue;
						String[] ValueOrder = value.split(":-");
						for (int i = 1; i <= ValueOrder.length; i++) {
							Locatorreplace = Locatorreplace.replace("#VALUE"
									+ i + "#", ValueOrder[i - 1]);
						}
						browser.scrollIntoView(Locatorreplace);
						browser.click(Locatorreplace);
					}
				}
			}

				break;
			}

			// do the Add Operation of combo selects

			switch (locatorType.valueOf(locType)) {
			case COMBOSELECT: {
				browser.click("//img[@title='Add']");
				break;
			}

			case ROLE_COMBOSELECT: {
				browser.click("//*[@id='CSM_ROLE_add_selected']");
				break;
			}

			case ASEARCH_COMBOSELECT: {
				browser.click("//a[contains(@onclick,'addSelectedSearches')]");
				break;
			}
			
			case FILE_UPLOAD: {

				try {

					if (browser.getBrowserType().equalsIgnoreCase("Chrome")) {

						Runnable autoItRunner = new AutoItRunner(
								"chromeAutoITFileUpload.log",
								"ChromeFileUpload.exe", value);

						Thread fileUploadThread = new Thread(
								MainEngine.seleniumThreads, autoItRunner);

						fileUploadThread.setName("AutoItUploadThread");

						fileUploadThread.start();

						fileUploadThread.join(30 * 1000);

					} else {
						throw new WebDriverException(
								"File Upload for IE to be implemented");
					}

				} catch (Exception e) {

					throw new WebDriverException("File Upload Action Failed"
							+ e.getMessage());

				} finally {

					MainEngine.caller.runMethod("ASSERTTHREADLOGOUTPUT",
							"SQL_PROCESS_INVOKER,AUTO_IT_THREAD");

				}

				// Checking for the failure of upload

				File autoITLogFile = new File(FileUtils.getFileLocation("logs",
						"chromeAutoITFileUpload.log"));
				String autoITLogFileContent = "";

				try {
					autoITLogFileContent = org.apache.commons.io.FileUtils
							.readFileToString(autoITLogFile);
				} catch (Exception e) {
					throw new WebDriverException("File Upload Action Failed"
							+ e.getMessage());
				}

				if (autoITLogFileContent
						.contains("Exiting from Chrome File Uploader")
						&& !autoITLogFileContent
								.contains("The presence of the Open Dialogue Window failed, even after second wait, Hence Exiting")) {
					MainEngine.log.info("File Upload Success for the file : "
							+ value + " using autoItFileUploader");
				} else {
					MainEngine.log.info("File Upload Failed for the file : "
							+ value + " using autoItFileUploader");
					throw new WebDriverException(
							"File Upload Failed for the file : " + value
									+ " using autoItFileUploader");
				}

				break;

			}
			case PASTE_SCREENSHOT: {

				try {

					Robot robot = new Robot();

					robot.keyPress(KeyEvent.VK_PRINTSCREEN);

					robot.keyRelease(KeyEvent.VK_PRINTSCREEN);

					WebDriver driver = (WebDriver) browser.getWebDriver();

					WebElement element = null;

					if (locator.startsWith("//")) {
						element = driver.findElement(By.xpath(locator));
					} else {
						element = driver.findElement(By.id(locator));
					}
					element.sendKeys(Keys.CONTROL + "v");
					
				} catch (Exception e) {
					MainEngine.log.info("Failed to Paste the Screenshot.");
					throw new WebDriverException(
							"Failed to Paste the Screenshot." + e.getMessage());
				}

				break;
			}
			
			}

			// Call event

			callLocatorEvent(locatorId);

			// Do Post Action

			doPostAction(locatorId);

		}

	}

       /**
       * 
        * This method will be used to do a pre-action on the WebElement identified
       * with the help of locatorId
       * 
        * @param locatorId
       *            --> The locatorId upon which the pre-action will be performed
       */

       @SuppressWarnings("incomplete-switch")
       public void doPreAction(String locatorId,String value) {

              String locType = getLocatorPreAction(locatorId).toUpperCase();
              String locator = handleLocatorsWithValues(locatorId,value);

              if (!(locType.trim().length() > 0)) {
                     locType = "SKIP";
              }

              switch (locatorAction.valueOf(locType)) {
              case ALERT: {
                     browser.getAlert();
                     break;
              }

              case PROMPT: {
                     browser.answerOnNextPrompt(locators.get(locatorId + ".PromptValue")
                                  .toString());
                     break;
              }
              case POP_UP: {
                     browser.waitForPopUp(locators.get(locatorId + ".PopupName")
                                  .toString(), 2000);
                     browser.selectWindow(locators.get(locatorId + ".PopupNameVal")
                                  .toString());
                     break;
              }
              case WAIT: {
                     browser.waitForPageToLoad();
                     break;
              }
             case HIGHLIGHT: {
            	 
            	 String highLightLoc = locators.get(locatorId + ".loc").toString();
            	 
            	 if(highLightLoc.contains("#VALUE#")){
            		 highLightLoc = highLightLoc.replaceAll("#VALUE#", value);
            	 }
                browser.highlight(highLightLoc);
                  break;
             }			  
              
              case FRAME: {

                     String multiframes = locators.get(locatorId + ".FrameName");

                     if (multiframes.contains(">")) {
                           String[] frame = multiframes.split(">");

                           for (int frameindex = 0; frameindex < frame.length; frameindex++) {
                                  browser.selectFrame(frame[frameindex]);
                           }

                     } else {
                           browser.selectFrame(locators.get(locatorId + ".FrameName")
                                         .toString());
                     }
                     break;
              }

              case REMOVEALL: {

                     String compType = getLocatorType(locatorId).toUpperCase();

                     switch (locatorType.valueOf(compType)) {
                     case COMBOSELECT: {
                           browser.selectAll("//select[@name='assignedroles']");
                           browser.click("//img[@title='Delete']");
                           break;
                     }

                     case ROLE_COMBOSELECT: {
                           browser.click("//*[@id='CSM_ROLE_remove_all']");
                           browser.waitForPageToLoad(1000);
                           break;
                     }

                     case ASEARCH_COMBOSELECT: {
                           browser.click("//a[contains(@onclick,'removeAllSearches')]");
                           browser.waitForPageToLoad(1000);

                           break;
                     }

                     }

                     break;
              }
              case SETELEMENTATTRIBUTE: {
                     String[] events = locators.get(locatorId + ".events").split(",");

                     for (String event : events) {
                           browser.setAttribute(locator, event, "false");
                     }

                     break;
              }
              case REMOVEELEMENTATTRIBUTE: {
                  String[] attributeName = locators.get(locatorId + ".attribute").split(",");
      
                  for (String attribute : attributeName)
                  browser.removeAttribute(locator, attribute);
      
                  break;
              }
              
              case REMOVEALLSELECTIONS: {
            	  
                  browser.removeAllSelections(locator);
      
                  break;
              }

              }

       }

       /**
       * 
        * This method will be used to do a post-action on the WebElement identified
       * with the help of locatorId
       * 
        * @param locatorId
       *            --> The locatorId upon which the post-action will be performed
       */

       @SuppressWarnings("incomplete-switch")
       public void doPostAction(String locatorId) {

              String locType = getLocatorPostAction(locatorId).toUpperCase();

              if (!(locType.trim().length() > 0)) {
                     locType = "SKIP";
              }

              switch (locatorAction.valueOf(locType)) {
              case ALERT: {
                     browser.getAlert();
                     break;
              }

              case PROMPT: {
                     browser.answerOnNextPrompt(locators.get(locatorId + ".PromptValue")
                                  .toString());
                     break;
              }
              case POP_UP: {
                     browser.waitForPopUp(locators.get(locatorId + ".PopupName")
                                  .toString(), 2000);
                     browser.selectWindow(locators.get(locatorId + ".PopupNameVal")
                                  .toString());
                     break;
              }
              case WAIT: {
                     browser.waitForPageToLoad();
                     break;
              }

              case FRAME: {
                     browser.selectFrame(locators.get(locatorId + ".FrameName")
                                  .toString());
                     break;
              }

              }

       }

       /**
       * This is used to update the property listed in the locator properties file
       * 
        * @param propKey
       *            --> The Key of the property to be updated
       * @param propValue
       *            --> The value of the property to be updated
       */

       public void updateProperty(String propKey, String propValue) {

              locators.put(propKey, propValue);

       }

       /**
       * 
        * This will be used to list the locators in the properties file
       * 
        * @return An Hashmap of locators listed in the propertied file
       */

       public HashMap<String, String> getLocatorIdMap() {
              HashMap<String, String> locatorMap = locators;

              HashMap<String, String> locatorIdMap = new HashMap<String, String>();

              Set<String> locatorMapSet = locatorMap.keySet();

              Iterator<String> locatorMapKsIter = locatorMapSet.iterator();

              while (locatorMapKsIter.hasNext()) {
                     String key = (String) locatorMapKsIter.next();

                     if (!key.contains(".")) {
                           locatorIdMap.put(key, locatorMap.get(key));
                     }

              }

              return locatorIdMap;

       }

       /**
       * 
        * This will be used to list the locators in the properties file sorted as
       * per the locator index
       * 
        * @return An Hashmap of locators listed in the propertied file sorted as
       *         per the locator index
       */

       public Set<String> getSortedLocatorIdSet() {

              HashMap<String, String> locatorMap = locators;

              TreeSet<String> sortedLocatorSet = new TreeSet<String>(
                           new LocComparator(locators));

              Iterator<String> locatorMapKsIter = locatorMap.keySet().iterator();

              while (locatorMapKsIter.hasNext()) {
                     String key = (String) locatorMapKsIter.next();

                     if (key.endsWith(".loc")
                                  && locators.get(key.substring(0, key.indexOf(".loc"))
                                                + ".index") != null) {
                           sortedLocatorSet.add(key);
                     }

              }

              return sortedLocatorSet;

       }

       /**
       * 
        * This is used to fetch the value assigned to the web element in the web
       * page
       * 
        * @param locatorId
       *            --> The locatorId using which the Web Element will be
       *            identified
       * @return --> The value assigned to the web element in the web page
       */

       @SuppressWarnings("incomplete-switch")
       public ArrayList<String> getLocatorAppValue(String locatorId) {

              ArrayList<String> appValues = new ArrayList<String>();
              
              String locatorIdVal=getLocator(locatorId);
              
              if(browser.isElementPresent(locatorIdVal, true)){
            	  
              if (locatorIdVal.startsWith("//")) {
					((JavascriptExecutor) browser.getWebDriver())
							.executeScript("arguments[0].scrollIntoView();",
									((WebDriver) browser.getWebDriver())
											.findElement(By.xpath(locatorIdVal)));
				} else {
					((JavascriptExecutor) browser.getWebDriver())
							.executeScript("arguments[0].scrollIntoView();",
									((WebDriver) browser.getWebDriver())
											.findElement(By.id(locatorIdVal)));
				}
              
              } else{
            	  MainEngine.log.error("Unable to scroll into element as the same is not available");
              }

              switch (locatorType.valueOf(getLocatorType(locatorId).toUpperCase())) {

              case TEXT_FIELD:
              case TEXT_AREA: {
                     appValues.add(browser.getValue(locatorIdVal));
                     break;
              }

              case TEXT_FIELD_VALUE: {
                     appValues.add(browser.getAttribute(locatorIdVal, "value"));
                     break;
              }

              case CHECK_BOX: {
                     boolean checkedStatus = browser.isChecked(locatorIdVal);

                     if (!checkedStatus) {
                           appValues.add("UNCHECK");
                     } else {
                           appValues.add("CHECK");
                     }

                     break;
              }

              case RADIO_BUTTON: {
                     boolean checkedStatus = browser.isChecked(locatorIdVal);

                     if (!checkedStatus) {
                           appValues.add("UNCHECK");
                     } else {
                           appValues.add("CHECK");
                     }

                     break;
              }

              case SELECT: {
                     String[] lables = browser.getSelectedLabels(locatorIdVal);

                     appValues = new ArrayList<String>(Arrays.asList(lables));
                     break;
              }

              case SELECT_VALUE: {

                     String[] lables = browser.getSelectedValues(locatorIdVal);

                     appValues = new ArrayList<String>(Arrays.asList(lables));

                     break;
              }

              case TEXT: {
                     String text = browser.getText(locatorIdVal);

                     appValues.add(text);
                     break;
              }

              case ROLE_COMBOSELECT: {
                     String[] lables = browser.getOptions(locatorIdVal);

                     appValues = new ArrayList<String>(Arrays.asList(lables));

                     break;
              }

              case ASEARCH_COMBOSELECT: {

                     String[] lables = browser.getOptions(locatorIdVal, "VALUE");

                     appValues = new ArrayList<String>(Arrays.asList(lables));

                     break;
              }

              case ELEMENT: {
                     boolean elementStatus = browser.isElementPresent(
                                  locatorIdVal, true);
                     appValues.add(elementStatus + "");
                     break;
              }
              case EXTJS_TEXTAREA_SELECTION: {
                  boolean elementStatus = browser.isElementPresent(locatorIdVal, true);
                  String textAreaElement;
                  if(elementStatus!=false)
                  {      								
    				List<WebElement> allElements = driver.findElements(By.xpath(locatorIdVal));
    				for (WebElement element: allElements) {    				      
    				      textAreaElement=element.getText();
    				      if( !textAreaElement.trim().isEmpty())
    				      {
    				    	  appValues.add(textAreaElement);
    				      }
    				}
    				break;	                	  
                	  
                  }

                  appValues.add(elementStatus + "");
              }              
              }

              return appValues;

       }

       /**
       * 
        * This method is used to call a mentioned event on the web element on the
       * web page
       * 
        * @param locatorId
       *            --> The locatorId using which the Web Element is identified on
       *            the Web Page.
       */

       public void callLocatorEvent(String locatorId) {

              String event = getLocatorEvent(locatorId);

              if (event.length() != 0) {

                     try {
                           WebDriver driver = (WebDriver) browser.getWebDriver();
                           if (driver instanceof JavascriptExecutor) {
                                  ((JavascriptExecutor) driver).executeScript(event);
                           }

                           Thread.sleep(1000);
                     } catch (Exception e) {	
						MainEngine.log.error("Issue Related to Event", e);
						throw new WebDriverException("Filed to perform the Robot Action");
						//System.out.println("Issue Related to Event");
                     }

              }

       }
       
       public String handleLocatorsWithValues(String locatorId,String value){
    	   
    	   String locator = getLocator(locatorId);
    	   
    	   if(value.equalsIgnoreCase("SKIP")){
    		   return locator;
    	   }
    	   
			if (locator.contains("#VALUE#") && (value != null)
					&& (!value.toUpperCase().contains(":MULTIPLE"))) {

				String actLocatorType = getLocatorType(locatorId);

				if (actLocatorType.equalsIgnoreCase(locatorType.TEXT_AREA.toString())
						|| actLocatorType.equalsIgnoreCase(locatorType.TEXT_FIELD.toString())
									|| actLocatorType.equalsIgnoreCase(locatorType.TEXT_FIELD_VALUE.toString())
									|| actLocatorType.equalsIgnoreCase(locatorType.SELECT.toString())) {

					if (value.contains(":-")) {
						String[] vals = value.split(":-");

						locator = locator.replaceAll("#VALUE#", vals[0]);
						
						tempLocator = getLocator(locatorId);
						
						locators.put(locatorId+".loc", locator);

						value = vals[1];
					}

				} else {
					locator = locator.replaceAll("#VALUE#", value);
				}

			}

			return locator;
    	   
       }
       
	public void moveToFormElement(String locator, String value) {

		if (!value.equalsIgnoreCase("SKIP")) {

			if (!(locator.contains("]|//") | locator.contains("#VALUE1#") | locator
					.contains("#VALUE#"))) {

				if (locator.startsWith("//")) {
					((JavascriptExecutor) browser.getWebDriver())
							.executeScript("arguments[0].scrollIntoView();",
									((WebDriver) browser.getWebDriver())
											.findElement(By.xpath(locator)));
				} else {
					((JavascriptExecutor) browser.getWebDriver())
							.executeScript("arguments[0].scrollIntoView();",
									((WebDriver) browser.getWebDriver())
											.findElement(By.id(locator)));
				}

				browser.waitUntilElementIspresent(locator, 30);

			}
		}
	}
}