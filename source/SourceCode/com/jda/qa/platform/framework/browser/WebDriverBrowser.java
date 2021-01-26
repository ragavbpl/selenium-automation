package SourceCode.com.jda.qa.platform.framework.browser;

import static com.jda.qa.platform.framework.locator.LocatorFormatter.formatLocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.jda.qa.platform.framework.browser.TestBrowser;
import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;
import com.jda.qa.platform.framework.base.MainEngine;

@SuppressWarnings("deprecation")
public class WebDriverBrowser implements TestBrowser {

	WebDriver webDriver;
	String URL = "";
	int defaultWaitTime = 10;
	Logger log = MainEngine.log;
	Set windowHandles = null;
	String browserType;
	boolean isBrowserClosed = true;
	boolean isBrowserStopped = true;
	String popUpWindowname = null;
	String mainWindowHandle=null;
	
	public WebDriverBrowser(String URL, String browserType) {

		this.browserType = browserType;
		this.URL = URL;

		String sDriversLocation = System.getProperty("user.dir")
				+ "//drivers//";

		if (browserType.contains("iexplore")) {
			
			DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			capabilities.setCapability("requireWindowFocus", true);
			capabilities.setCapability("browserstack.ie.enablePopups", "true");

			System.setProperty("webdriver.ie.driver", sDriversLocation
					+ "IEDriverServer.exe");

			webDriver = new InternetExplorerDriver(capabilities);
		} else if (browserType.contains("chrome")) {

			System.setProperty("webdriver.chrome.driver", sDriversLocation
					+ "chromedriver.exe");
			
			ChromeOptions options = new ChromeOptions();
			HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
			HashMap<String, Object> chromeOptionsMap = new HashMap<String, Object>();
			
			options.addArguments("chrome.switches", "--disable-extensions");
			options.addArguments("--no-sandbox");
			options.setExperimentalOption("prefs", chromePrefs);
			options.addArguments("--test-type");
			
			// Download Settings
			String downloadFilepath = System.getProperty("user.dir")+"\\"+MainEngine.downloadsDir;
			
			chromePrefs.put("profile.default_content_settings.popups", 0);
			chromePrefs.put("download.default_directory", downloadFilepath);
			
			DesiredCapabilities chromeCapabilities = DesiredCapabilities.chrome();
			chromeCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptionsMap);
			chromeCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
			
			webDriver = new ChromeDriver(chromeCapabilities);
		}
		
		setImplicitlyWaitTimeout(30, TimeUnit.SECONDS);
		setPageloadTimeout(60, TimeUnit.SECONDS);

		open(URL);
		
		mainWindowHandle = webDriver.getWindowHandle();
		
		System.out.println("Main Window handle is "+mainWindowHandle);
		
		MainEngine.browserStack.add(this);

	}

	public String getURL() {
		return URL;
	}

	public String getBrowserType() {
		return this.browserType;
	}

	public void refresh() throws WebDriverException {
		try {
			log.info("Refresh Operation Performed");
			webDriver.navigate().refresh();
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("Refresh Operation Failed");
			throw new WebDriverException("Refresh Operation Failed");
		}

	}

	public Object getWebDriver() throws WebDriverException {

		return webDriver;
	}

	public void start() throws WebDriverException {
		try {
			log.info("Start Operation Performed");
			webDriver.manage().window().maximize();
			webDriver.get(URL);
			updateWindowHandles();
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("Start Operation Failed");
			throw new WebDriverException("Start Operation Failed");
		}
	}

	public void stop() throws WebDriverException {
		try {
			log.info("Stop Operation Performed");
			if (!isBrowserStopped) {
				webDriver.quit();
			}
			isBrowserStopped = true;
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("Stop Operation Failed");
			throw new WebDriverException("Stop Operation Failed");
		}

	}

	public void addSelection(String locator, String optionLocator)
			throws WebDriverException {

		Select select = null;
		try {

			log.info("Select Operation Performed with Locator : " + locator
					+ " Value : " + optionLocator);

			if (locator.startsWith("//")) {
				select = new Select(webDriver.findElement(By.xpath(locator)));
			} else {
				select = new Select(webDriver.findElement(By.id(locator)));
			}

			select.selectByVisibleText(optionLocator);
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("Select Operation Failed");
			throw new WebDriverException("Select Operation Failed");
		}

	}

	public String getAttribute(String locator, String attributeName)
			throws WebDriverException {
		String attribute = null;
		
		try {

			log.info("Get Attribute Operation Performed with Locator : "
					+ locator + " attributeName : " + attributeName);

			if (locator.startsWith("//")) {
				
				String locatorStyleChecker = locator+"[@"+attributeName+"]";
				
				if(isElementPresent(locatorStyleChecker, true)){
					attribute = webDriver.findElement(By.xpath(locator))
							.getAttribute(attributeName);	
				}else{
					attribute="";
				}
				
			} else {
				
				String locatorStyleChecker = "//*[@id='"+locator+"'][@style]";
				
				if(isElementPresent(locatorStyleChecker, true)){
					attribute = webDriver.findElement(By.id(locator)).getAttribute(
							attributeName);
				}else{
					attribute="";
				}
				
				
			}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("GetAttribute Operation Failed");
			throw new WebDriverException("GetAttribute Operation Failed");
		}

		return attribute;
	}
	
    
    public String getTagName(String attributeName, String attributeValue) 
            throws WebDriverException {
        String tagName = null;
        try
        {
            log.info("Get TagName Operation Performed with Attribute Name : "
                    + attributeName + " Attribute Value : " + attributeValue);
            switch(attributeName.toUpperCase())
            {
                case "NAME":
                {
                    tagName=webDriver.findElement(By.name(attributeValue)).getTagName();
                    break;
                }
                case "ID":
                {
                    tagName=webDriver.findElement(By.id(attributeValue)).getTagName();
                    break;
                }
            }
        }
        catch(Exception e)
        {
            log.info("Encountered with the exception : " + e.getMessage());
            log.info("getTagName Operation Failed");
            throw new WebDriverException("getTagName Operation Failed");        
        }
        return tagName;        
    }

	public boolean isAttribtuePresent(String locator, String attributeName) throws WebDriverException {
		
		WebElement element = webDriver.findElement(By.xpath(locator));
	    Boolean result = false;
	    try {
	        String value = element.getAttribute(attributeName);
	        if (value != null)
	            result = true;
	    } catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("isAttribtuePresent Operation Failed");
			throw new WebDriverException("isAttribtuePresent Operation Failed");	    	
	    }

	    return result;
	}
	
	public String getTitle() throws WebDriverException {
		String title = null;

		try {
			log.info("GetTitle Operation Performed");
			title = webDriver.getTitle();
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("GetTitle Operation Failed");
			throw new WebDriverException("GetTitle Operation Failed");
		}

		return title;
	}

	public int getXpathCount(String path) throws WebDriverException {

		List<WebElement> webElements = new ArrayList<WebElement>(0);

		try {

			log.info("getXpathCount Operation performed with xpath : " + path);
			
			setImplicitlyWaitTimeout(5, TimeUnit.SECONDS);

			if (path.startsWith("//")) {
				webElements = webDriver.findElements(By.xpath(path));
			} else {
				webElements = webDriver.findElements(By.id(path));
			}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("getXpathCount Operation Failed");
			throw new WebDriverException("getXpathCount Operation Failed");
		}finally{
			setImplicitlyWaitTimeout(30, TimeUnit.SECONDS);
		}

		return webElements.size();
	}

	public int getXpathCount(String path, String frame)
			throws WebDriverException {

		List<WebElement> webElements = new ArrayList<WebElement>(0);

		try {

			log.info("getXpathCount Operation Performed with the path : "
					+ path + " inside frame : " + frame);

			webDriver.switchTo().defaultContent();

			webDriver.switchTo().frame(frame);

			if (path.startsWith("//")) {
				webElements = webDriver.findElements(By.xpath(path));
			} else {
				webElements = webDriver.findElements(By.id(path));
			}
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("getXpathCount Operation Failed");
			throw new WebDriverException("getXpathCount Operation Failed");
		}

		return webElements.size();
	}

	public void open(String url) throws WebDriverException {
		try {
			log.info("Open Operation performed for the URL : " + url);
			webDriver.manage().window().maximize();
			webDriver.navigate().to(url);
			updateWindowHandles();
			isBrowserClosed = false;
			isBrowserStopped = false;
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("Open Operation Failed");
			throw new WebDriverException("Open Operation Failed");
		}
	}

	public void selectFrame(String name) throws WebDriverException {

		try {

			log.info("selectFrame Operation performed for the Frame : " + name);

			if (name.length() != 0) {

			String multiframes = name;
			
			if (! multiframes.contains(">")) {

				if (name.startsWith("//")) {
					webDriver.switchTo().frame(
							webDriver.findElement(By.xpath(name)));
				} else if(name.equalsIgnoreCase("TopWindow")) {
					switchToDefaultContent();
				} else {
					webDriver.switchTo().frame(
							webDriver.findElement(By.id(name)));
				}				
			} 
			
			else {

				String[] frame = multiframes.split(">");

				for (int frameindex = 0; frameindex < frame.length; frameindex++) {				
					if (frame[frameindex].startsWith("//")) {
						webDriver.switchTo().frame(
								webDriver.findElement(By.xpath(frame[frameindex])));
					} else if(frame[frameindex].equalsIgnoreCase("TopWindow")) {
						switchToDefaultContent();
					} else {
						webDriver.switchTo().frame(
								webDriver.findElement(By.id(frame[frameindex])));
					}
				}
			}			
		}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("selectFrame Operation Failed");
			throw new WebDriverException("selectFrame Operation Failed");
		}
	}

	public void selectWindow(String name) throws WebDriverException {
		try {
			System.out.println("selectWindow Operation Performed : "+name);
			log.info("selectWindow Operation Performed : "+name);
			webDriver.switchTo().window(name);
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("selectWindow Operation Failed");
			throw new WebDriverException("selectWindow Operation Failed");
		}

	}

	public void setSpeed(String value) throws WebDriverException {

		webDriver.manage().timeouts()
				.implicitlyWait(new Long(value), TimeUnit.SECONDS);

	}

	public void waitForPageToLoad() throws WebDriverException {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void waitForPageToLoad(long milliseconds) throws WebDriverException {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean isElementPresent(String locator) throws WebDriverException {

		WebElement element = null;

		try {
			log.info("isElementPresent Operation Performed for the locator : "
					+ locator);
			
			setImplicitlyWaitTimeout(5, TimeUnit.SECONDS);
			
			if (locator.startsWith("//") || locator.startsWith("(//")) {
				element = webDriver.findElement(By.xpath(locator));
			} else {
				element = webDriver.findElement(By.id(locator));
			}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("isElementPresent Operation Failed");
			throw new WebDriverException("isElementPresent Operation Failed");
		}finally{
			setImplicitlyWaitTimeout(30, TimeUnit.SECONDS);	
		}

		if (element != null) {
			return true;
		} else {
			return false;
		}

	}

	public boolean isElementPresent(final String locator, int waitTime)
			throws WebDriverException {
		/*
		 * webDriver.manage().timeouts() .implicitlyWait(waitTime,
		 * TimeUnit.SECONDS);
		 * 
		 * boolean status = isElementPresent(locator);
		 * 
		 * webDriver.manage().timeouts() .implicitlyWait(defaultWaitTime,
		 * TimeUnit.SECONDS);
		 * 
		 * return status;
		 */

		if (waitTime > 1000) {
			waitTime = waitTime / 100;
		}

		Wait<WebDriver> wait = new FluentWait<WebDriver>(webDriver)
				.withTimeout(waitTime, TimeUnit.SECONDS)
				.pollingEvery(5, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class);

		WebElement element = wait.until(new Function<WebDriver, WebElement>() {

			public WebElement apply(WebDriver driver) {

				if (locator.startsWith("//")) {
					return driver.findElement(By.xpath(locator));
				} else {
					return driver.findElement(By.id(locator));
				}
			}

		});

		if (element != null) {
			return true;
		} else {
			throw new WebDriverException(
					"Failed to find the Element in the provided time interval");
		}
	}

	public boolean isTextPresent(String pattern) throws WebDriverException {

		if (pattern.startsWith("//")) {
			webDriver.findElement(By.xpath(pattern)).getText();
		} else {
			webDriver.findElement(By.id(pattern)).getText();
		}

		return false;
	}

	public int getWaitLoadTime() throws WebDriverException {
		return 0;
	}

	public void setWaitLoadTime(int waitLoadTime) throws WebDriverException {
		webDriver.manage().timeouts()
				.pageLoadTimeout(waitLoadTime, TimeUnit.SECONDS);

	}

	public void keyPress(String locator, String keySequence)
			throws WebDriverException {
		try {

			log.info("keyPress Operation performed on the locator : " + locator
					+ " for the Key Sequence : " + keySequence);
			if (locator.startsWith("//")) {
				webDriver.findElement(By.xpath(locator)).sendKeys(keySequence);
			} else {
				webDriver.findElement(By.id(locator)).sendKeys(keySequence);
			}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("keyPress Operation Failed");
			throw new WebDriverException("keyPress Operation Failed");
		}
	}

	public void waitForPopUp(String windowID, int timeout)
			throws WebDriverException {
		try {
			int time = timeout / 10;

			while (time <= timeout) {

				boolean isWindowFound = false;

				Set<String> windowHandles = webDriver.getWindowHandles();

				Iterator<String> itr = windowHandles.iterator();

				while (itr.hasNext()) {
					String iterWindowID = (String) itr.next();

					if (windowID.equalsIgnoreCase(iterWindowID)) {
						isWindowFound = true;
						break;
					}

				}

				if (isWindowFound) {
					break;
				}

				sleep(timeout / 10);
				time = time + timeout / 10;
			}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("waitForPopUp Operation Failed");
			throw new WebDriverException("waitForPopUp Operation Failed");
		}

	}

	public void closePopUpWindow() throws WebDriverException {
		try {
			log.info("closePopUpWindow Operation Performed");
			webDriver.close();
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("closePopUpWindow Operation Failed");
			throw new WebDriverException("closePopUpWindow Operation Failed");
		}
	}

	public void closeWindow(String name) throws WebDriverException {
		try {
			log.info("closeWindow Operation performed for name : " + name);
			windowHandles.remove(name);
			webDriver.switchTo().window(name).close();
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("closeWindow Operation Failed");
			throw new WebDriverException("closeWindow Operation Failed");
		}

	}

	public int getWindowCount() throws WebDriverException {

		try {
			log.info("getWindowCount Operation Performed");
			return webDriver.getWindowHandles().size();
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("getWindowCount Operation Failed");
			throw new WebDriverException("getWindowCount Operation Failed");
		}
	}

	public void setDefaultFrame() throws WebDriverException {
		try {
			log.info("setDefaultFrame Operation performed");
			webDriver.switchTo().defaultContent();
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("setDefaultFrame Operation Failed");
			throw new WebDriverException("setDefaultFrame Operation Failed");
		}

	}

	public void waitUntilElementIspresent(String locator, long timeout) {
		try {
			WebDriverWait oWait; // we use this for explicit synchronization

			if (timeout > 1000) {
				timeout = timeout / 100;
			}

			oWait = new WebDriverWait(webDriver, timeout);

			if (locator.startsWith("//")) {
				oWait.until(ExpectedConditions.presenceOfElementLocated(By
						.xpath(locator)));
			} else {
				oWait.until(ExpectedConditions.presenceOfElementLocated(By
						.id(locator)));
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public String getBrowserTitle() throws WebDriverException {
		return webDriver.getTitle();
	}

	public String getAlert() throws WebDriverException {
		return webDriver.getWindowHandle();
	}

	public void waitForElementNotPresent(String elementId, int timeout)
			throws WebDriverException {
	}

	public String getLocation() throws WebDriverException {
		return webDriver.getCurrentUrl();
	}

	public WebDriver getBrowser() throws WebDriverException {
		// TODO Auto-generated method stub
		return webDriver;
	}

	public void windowMaximize() throws WebDriverException {
		webDriver.manage().window().maximize();
	}

	public String getText(String locator) throws WebDriverException {
		try {
			locator = formatLocator(locator);

			if (isElementPresent(locator)) {

				if (locator.startsWith("//") || locator.startsWith("(//")) {
					return webDriver.findElement(By.xpath(locator)).getText();
				} else {
					return webDriver.findElement(By.id(locator)).getText();
				}

			} else {
				return "";
			}
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("getText Operation Failed");
			throw new WebDriverException("getText Operation Failed");
		}

	}
	
	public String getText(String locator, boolean trimContent)
			throws WebDriverException {

		String locatorText = getText(locator);

		if (locatorText != null && trimContent) {
			locatorText = locatorText.trim();
		}

		return locatorText;

	}

	public void type(String locator, String content) throws WebDriverException {
		try {
			locator = formatLocator(locator);

			content = formatLocator(content);

			if (content.startsWith("Keys")) {

				if (content.toLowerCase().contains("enter")) {
					if (locator.startsWith("//")) {
						webDriver.findElement(By.xpath(locator)).sendKeys(
								Keys.ENTER);
					} else {
						webDriver.findElement(By.id(locator)).sendKeys(
								Keys.ENTER);
					}

				} else if (content.toLowerCase().contains("escape")) {
					if (locator.startsWith("//")) {
						webDriver.findElement(By.xpath(locator)).sendKeys(
								Keys.ESCAPE);
					} else {
						webDriver.findElement(By.id(locator)).sendKeys(
								Keys.ESCAPE);
					}
				} else if (content.toLowerCase().contains("tab")) {
					if (locator.startsWith("//")) {
						webDriver.findElement(By.xpath(locator)).sendKeys(
								Keys.TAB);
					} else {
						webDriver.findElement(By.id(locator))
								.sendKeys(Keys.TAB);
					}
				} else if (content.toLowerCase().contains("arrow_up")) {
					if (locator.startsWith("//")) {
						webDriver.findElement(By.xpath(locator)).sendKeys(
								Keys.ARROW_UP);
					} else {
						webDriver.findElement(By.id(locator)).sendKeys(
								Keys.ARROW_UP);
					}
				} else if (content.toLowerCase().contains("arrow_down")) {
					if (locator.startsWith("//")) {
						webDriver.findElement(By.xpath(locator)).sendKeys(
								Keys.ARROW_DOWN);
					} else {
						webDriver.findElement(By.id(locator)).sendKeys(
								Keys.ARROW_DOWN);
					}
				}

			} else {
				if (locator.startsWith("//")) {
					if (!content.contains(":\\")) {
						webDriver.findElement(By.xpath(locator)).clear();
					}

					webDriver.findElement(By.xpath(locator)).sendKeys(content);
				} else {
					if (!content.contains(":\\")) {
						webDriver.findElement(By.id(locator)).clear();
					}

					webDriver.findElement(By.id(locator)).sendKeys(content);
				}

			}
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("type Operation Failed");
			throw new WebDriverException("type Operation Failed");
		}

	}

	public void click(String locator) throws WebDriverException {
		try {

			locator = formatLocator(locator);

			if (!isElementPresent(locator,true)) {
				Thread.sleep(2000);
			}
			

			if (isElementPresent(locator)) {
				if (browserType.equalsIgnoreCase("iexplore")) {
					highlightBorder(locator);
				}
				try {

					Thread.sleep(1500);
					
					if (locator.startsWith("//")) {
						webDriver.findElement(By.xpath(locator)).click();
					} else {
						webDriver.findElement(By.id(locator)).click();
					}

				} catch (ElementNotVisibleException exception) {

					WebElement element = null;

					if (locator.startsWith("//")) {
						element = ((WebDriver) getWebDriver()).findElement(By
								.xpath(locator));
					} else {
						element = ((WebDriver) getWebDriver()).findElement(By
								.id(locator));
					}

					JavascriptExecutor js = (JavascriptExecutor) ((WebDriver) getWebDriver());
					js.executeScript("arguments[0].click();", element);

				}

			}
		} catch (Exception e) {
			if (e.getMessage().toString().contains("Element is not clickable")) {

				log.info("Performing the click operation using the Actions");

				try {
					Thread.sleep(2500);

					Actions clickAction = new Actions(webDriver);

					if (locator.startsWith("//")) {
						clickAction
								.moveToElement(
										webDriver.findElement(By.xpath(locator)))
								.click().build().perform();
					} else {
						clickAction
								.moveToElement(
										webDriver.findElement(By.id(locator)))
								.click().build().perform();
					}

				} catch (Exception e1) {
					log.info("Encountered with the exception : "
							+ e1.getMessage());
					log.info("click Operation Failed");
					throw new WebDriverException("click Operation Failed with the error : "+e1.getMessage());
				}
			} else{
				log.info("Encountered with the exception : "
						+ e.getMessage());
				log.info("click Operation Failed");
				throw new WebDriverException("click Operation Failed with the error : "+e.getMessage());
			}
		}

	}

	public boolean isChecked(String locator) throws WebDriverException {
		
		String attributeType="";
		try {
			if (isAttribtuePresent(locator, "aria-checked")){
			attributeType="aria-checked";
			if (locator.startsWith("//")) {
					if(webDriver.findElement(By.xpath(locator)).getAttribute(attributeType).toUpperCase().equals("TRUE")){
						return true;
					}else
						return false;

			} else {
					if(webDriver.findElement(By.id(locator)).getAttribute(attributeType).toUpperCase().equals("TRUE")){
						return true;
					}else
						return false;
			}
			}else {
					if (locator.startsWith("//")) {
						return webDriver.findElement(By.xpath(locator)).isSelected();
					} else {
						return webDriver.findElement(By.id(locator)).isSelected();
					}
			}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("isChecked Operation Failed");
			throw new WebDriverException("isChecked Operation Failed");
		}
	}

	public void highlight(String locator) {

		WebElement element = null;

		if (locator.startsWith("//")) {
			element = webDriver.findElement(By.xpath(locator));
		} else {
			element = webDriver.findElement(By.id(locator));
		}
		for (int i = 0; i < 2; i++) {
			JavascriptExecutor js = (JavascriptExecutor) webDriver;
			js.executeScript(
					"arguments[0].setAttribute('style', arguments[1]);",
					element, "color: red; border: 3px solid red;");
			sleep(100);
			js.executeScript(
					"arguments[0].setAttribute('style', arguments[1]);",
					element, "");
		}

	}
	
	public void highlightBorder(String locator) {

		WebElement element = null;

		if (locator.startsWith("//")) {
			element = webDriver.findElement(By.xpath(locator));
		} else {
			element = webDriver.findElement(By.id(locator));
		}
		
		String elementDefStyle = getAttribute(locator, "style");
		
		if(!(elementDefStyle !=null && elementDefStyle.trim().length()>0)){
			elementDefStyle="";
		}
		
		for (int i = 0; i < 2; i++) {
			JavascriptExecutor js = (JavascriptExecutor) webDriver;
			js.executeScript(
					"arguments[0].setAttribute('style', arguments[1]);",
					element, elementDefStyle+" border: 1px solid red;");
			sleep(100);
			js.executeScript(
					"arguments[0].setAttribute('style', arguments[1]);",
					element, elementDefStyle);
		}

	}

	public void highlightClick(String locator) {

		WebElement element = null;

		if (locator.startsWith("//")) {
			element = webDriver.findElement(By.xpath(locator));
		} else {
			element = webDriver.findElement(By.id(locator));
		}
		
		String elementDefStyle = getAttribute(locator, "style");
		
		if(!(elementDefStyle !=null && elementDefStyle.trim().length()>0)){
			elementDefStyle="";
		}
		
		for (int i = 0; i < 2; i++) {
			JavascriptExecutor js = (JavascriptExecutor) webDriver;
			js.executeScript(
					"arguments[0].setAttribute('style', arguments[1]);",
					element, elementDefStyle+" color: WhiteSmoke; border: 2px solid WhiteSmoke;");
			sleep(500);
			js.executeScript(
					"arguments[0].setAttribute('style', arguments[1]);",
					element, elementDefStyle);
		}

	}

	public void answerOnNextPrompt(String value) throws WebDriverException {

	}

	public void waitForPageToLoad(String waitTime) throws WebDriverException {

	}

	public void close() throws WebDriverException {
		try {

			if (!isBrowserClosed) {
				webDriver.close();
			}

			isBrowserClosed = true;
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("close Operation Failed");
			throw new WebDriverException("close Operation Failed");
		}

	}

	public void select(String locator, String text) throws WebDriverException {
		try {
			
			sleep(500);

			Select select = null;
			if (locator.startsWith("//")) {
				select = new Select(webDriver.findElement(By.xpath(locator)));
			} else {
				select = new Select(webDriver.findElement(By.id(locator)));
			}
			
			if(text.endsWith("#RM#")){
				
				text = text.replaceAll("#RM#", "");
				text = text.replaceAll(",", "\\.");
			}

			if (text.startsWith("value:")) {

				text = text.replaceAll("value:", "");

				select.selectByValue(text);

			} else {
				select.selectByVisibleText(text);
			}
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("select Operation Failed");
			throw new WebDriverException("select Operation Failed");
		}
	}

	public void selectByValue(String locator, String value)
			throws WebDriverException {
		try {
			value = value.replace("value:", "");
			
			sleep(500);

			Select select = null;
			if (locator.startsWith("//")) {
				select = new Select(webDriver.findElement(By.xpath(locator)));
			} else {
				select = new Select(webDriver.findElement(By.id(locator)));
			}
			
			if (value.endsWith("#RM#")) {

				value = value.replaceAll("#RM#", "");
				value = value.replaceAll(",", "\\.");
			}

			select.selectByValue(value);

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("selectByValue Operation Failed");
			throw new WebDriverException("selectByValue Operation Failed");
		}
	}

	public String getValue(String locator) throws WebDriverException {
		try {
			if (locator.startsWith("//")) {
				return webDriver.findElement(By.xpath(locator)).getAttribute(
						"value");
			} else {
				return webDriver.findElement(By.id(locator)).getAttribute(
						"value");
			}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("getValue Operation Failed");
			throw new WebDriverException("getValue Operation Failed");
		}
	}

	public boolean isSelected(String locator) throws WebDriverException {
		try {
			if (locator.startsWith("//")) {
				return webDriver.findElement(By.xpath(locator)).isSelected();
			} else {
				return webDriver.findElement(By.id(locator)).isSelected();
			}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("isSelected Operation Failed");
			throw new WebDriverException("isSelected Operation Failed");
		}
	}

	public void sleep(int time) throws WebDriverException {

		try {
			Thread.sleep(new Long(time));
		} catch (Exception e) {
		}
	}

	public String[] getSelectedLabels(String locator) throws WebDriverException {
		try {
			String[] optionsAry;

			Select select = null;
			if (locator.startsWith("//")) {
				select = new Select(webDriver.findElement(By.xpath(locator)));
			} else {
				select = new Select(webDriver.findElement(By.id(locator)));
			}

			// Select select = (Select)
			// webDriver.findElement(By.xpath(locator));

			List<WebElement> options = select.getAllSelectedOptions();

			optionsAry = new String[options.size()];

			for (int index = 0; index < options.size(); index++) {
				optionsAry[index] = options.get(index).getText();
			}

			return optionsAry;
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("getSelectedLabels Operation Failed");
			throw new WebDriverException("getSelectedLabels Operation Failed");
		}

	}

	public void clear(String locator) throws WebDriverException {
		try {

			String text = "";
			WebElement element = null;

			if (locator.startsWith("//")) {
				webDriver.findElement(By.xpath(locator)).clear();
				text = webDriver.findElement(By.xpath(locator)).getText();
				
			} else {
				webDriver.findElement(By.id(locator)).clear();
				text = webDriver.findElement(By.id(locator)).getText();
			}

			if (text.trim().length() != 0) {

				if (locator.startsWith("//")) {
					element = webDriver.findElement(By.xpath(locator));
				} else {
					element = webDriver.findElement(By.id(locator));
				}

				JavascriptExecutor js = (JavascriptExecutor) webDriver;
				js.executeScript(
						"arguments[0].setAttribute('value', arguments[1]);",
						element, "");

			}

			if (locator.startsWith("//")) {
				text = webDriver.findElement(By.xpath(locator)).getText();

			} else {
				text = webDriver.findElement(By.id(locator)).getText();
			}

			if (text.trim().length() != 0) {

				((Actions) getActions()).doubleClick(element).build().perform();

				element.sendKeys(Keys.DELETE);
			}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("clear Operation Failed");
			throw new WebDriverException("clear Operation Failed");
		}

	}

	public void sendKeys(String locator, Keys key) throws WebDriverException {
		try {
			if (locator.startsWith("//")) {
				webDriver.findElement(By.xpath(locator)).sendKeys(key);
			} else {
				webDriver.findElement(By.id(locator)).sendKeys(key);
			}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("sendKeys Operation Failed");
			throw new WebDriverException("sendKeys Operation Failed");
		}
	}

	public void switchToDefaultContent() throws WebDriverException {
		try {
			webDriver.switchTo().defaultContent();
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("switchToDefaultContent Operation Failed");
			throw new WebDriverException(
					"switchToDefaultContent Operation Failed");
		}
	}

	public void selectFrames(String[] frames) throws WebDriverException {
		try {
			webDriver.switchTo().defaultContent();

			for (int frameindex = 0; frameindex < frames.length; frameindex++) {
				selectFrame(frames[frameindex]);
			}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("Select Frames Operation Failed");
			throw new WebDriverException("Select Frames Operation Failed");
		}
	}

	@Override
	public void selectAll(String locator) throws WebDriverException {
		try {

			String[] sSelectedVals = getSelectedLabels(locator);
			Select select = null;
			if (locator.startsWith("//")) {
				select = new Select(webDriver.findElement(By.xpath(locator)));
			} else {
				select = new Select(webDriver.findElement(By.id(locator)));
			}

			for (int iForLoop = 0; iForLoop < sSelectedVals.length; iForLoop++) {
				select.selectByIndex(iForLoop);
			}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("selectAll Operation Failed");
			throw new WebDriverException("selectAll Operation Failed");
		}
	}

	public String[] getOptions(String locator, String attType)
			throws WebDriverException {
		try {
			String[] optionsAry;

			Select select = null;
			if (locator.startsWith("//")) {
				select = new Select(webDriver.findElement(By.xpath(locator)));
			} else {
				select = new Select(webDriver.findElement(By.id(locator)));
			}

			List<WebElement> options = select.getOptions();

			optionsAry = new String[options.size()];

			for (int index = 0; index < options.size(); index++) {

				if (attType.equalsIgnoreCase("TEXT")) {
					optionsAry[index] = options.get(index).getText().trim();
				} else if (attType.equalsIgnoreCase("VALUE")) {
					optionsAry[index] = options.get(index)
							.getAttribute("value").trim();
				}

			}

			return optionsAry;
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("getSelectedLabels Operation Failed");
			throw new WebDriverException("getSelectedLabels Operation Failed");
		}

	}

	public String[] getOptions(String locator) throws WebDriverException {
		return getOptions(locator, "TEXT");
	}

	public String[] getSelectedValues(String locator) throws WebDriverException {
		try {
			String[] optionsAry;

			Select select = null;
			if (locator.startsWith("//")) {
				select = new Select(webDriver.findElement(By.xpath(locator)));
			} else {
				select = new Select(webDriver.findElement(By.id(locator)));
			}

			List<WebElement> options = select.getAllSelectedOptions();

			optionsAry = new String[options.size()];

			for (int index = 0; index < options.size(); index++) {
				optionsAry[index] = options.get(index).getAttribute("value");
			}

			return optionsAry;
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("getSelectedLabels Operation Failed");
			throw new WebDriverException("getSelectedLabels Operation Failed");
		}

	}

	@Override
	public void setAttribute(String locator, String attributeName,
			String attributeValue) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) webDriver;

			WebElement element = null;
			if (locator.startsWith("//")) {
				element = webDriver.findElement(By.xpath(locator));
			} else {
				element = webDriver.findElement(By.id(locator));
			}

			js.executeScript("arguments[0].setAttribute('" + attributeName
					+ "', '" + attributeValue + "')", element);
		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("setAttribute Operation Failed");
			throw new WebDriverException("setAttribute Operation Failed");
		}

	}

	public List<WebElement> findElements(String locator) {

		List<WebElement> l = webDriver.findElements(By.xpath(locator));
		return l;

	}

	@Override
	public Object getActions() {

		Actions oAc = new Actions(webDriver);
		return oAc;

	}

	public boolean isElementPresent(String locator, boolean disolveException) {
		
		boolean status = false;

		try {
			status = isElementPresent(locator);
		} catch (Exception e) {
			if (!disolveException) {
				throw e;
			}
			log.info("Disolved the Expetion " + e.getLocalizedMessage());
		}

		return status;
	}

	@Override
	public void addSelection(String locator, String optionLocator,
			boolean isValueSelect) {

		if (isValueSelect) {
			selectByValue(locator, optionLocator);
		} else {
			select(locator, optionLocator);
		}

	}

	@Override
	public void waitForPageToLoad(String locator, String timeout) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object findElement(By xpath) {

		WebElement element = webDriver.findElement(xpath);

		return element;
	}
	
	@Override
	public Object findElement(String locator) {

		WebElement element = null;

		if (locator.startsWith("//")) {

			element = webDriver.findElement(By.xpath(locator));
		} else {
			element = webDriver.findElement(By.id(locator));
		}

		return element;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void updateWindowHandles() {

		boolean isMatched = true;

		Set windowHandlesLatest = webDriver.getWindowHandles();

		HashSet<String> windowHandlesLatestClone = new HashSet(
				windowHandlesLatest);
		HashSet<String> windowHandlesLatestClone1 = new HashSet(
				windowHandlesLatest);

		// Find the New Window Handles
		if (windowHandles != null) {
			windowHandlesLatestClone1.removeAll(windowHandles);

			// Remove the latest window handles from clone
			windowHandlesLatestClone.removeAll(windowHandlesLatestClone1);

			Iterator<String> windowHandleItr = windowHandles.iterator();
			Iterator<String> windowHandleCloneItr = windowHandlesLatestClone
					.iterator();

			while (windowHandleCloneItr.hasNext()) {

				String oldWindowHandle = windowHandleItr.next();
				String newWindowHandle = windowHandleCloneItr.next();

				if (!oldWindowHandle.equals(newWindowHandle)) {
					log.info("Update Window Handles Failed to match the windows, Hence we will reiterate");
					isMatched = false;
					break;
				}

			}

			if (!isMatched) {
				updateWindowHandles();
			}

		}

		windowHandles = windowHandlesLatest;

	}

	@Override
	public Set getWindowHandles() {
		return windowHandles;
	}

	public void moveToElement(String locator) {

		try {

			scrollIntoView(locator);

			Actions actions = (Actions) getActions();

			if (locator.startsWith("//")) {
				actions.moveToElement(webDriver.findElement(By.xpath(locator))).build().perform();
			} else {
				actions.moveToElement(webDriver.findElement(By.id(locator))).build().perform();
			}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("moveToElement Operation Failed");
			throw new WebDriverException("moveToElement Operation Failed");
		}

	}
	
	public void moveToElement(WebElement element) {

		try {

			scrollIntoView(element);

			Actions actions = (Actions) getActions();

			actions.moveToElement(element).build().perform();

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("moveToElement Operation Failed");
			throw new WebDriverException("moveToElement Operation Failed");
		}

	}

	public void scrollIntoView(String locator) {

		try {
			
			locator = formatLocator(locator);

			if (locator.startsWith("//")) {
				((JavascriptExecutor) webDriver).executeScript(
						"arguments[0].scrollIntoView();",
						webDriver.findElement(By.xpath(locator)));
			} else {
				((JavascriptExecutor) webDriver).executeScript(
						"arguments[0].scrollIntoView();",
						webDriver.findElement(By.id(locator)));
			}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("scrollIntoView Operation Failed");
			throw new WebDriverException("scrollIntoView Operation Failed");
		}

	}
	
	public void scrollIntoView(WebElement element) {

		try {

			((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView();", element);

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("scrollIntoView Operation Failed");
			throw new WebDriverException("scrollIntoView Operation Failed");
		}

	}

	public String getPopUpWindowname() {
		return popUpWindowname;
	}

	public void setPopUpWindowname(String popUpWindowname) {
		this.popUpWindowname = popUpWindowname;
	}
	
	public void manageConfirmation(String action){
		
		try {

			Alert alert = webDriver.switchTo().alert();
			
			if(action.equalsIgnoreCase("ACCEPT")){
				alert.accept();
			}else if (action.equalsIgnoreCase("DISMISS")){
				alert.dismiss();
			}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("manageConfirmation Operation Failed");
			throw new WebDriverException("manageConfirmation Operation Failed");
		}
		
	}
	
	public void manageConfirmationTextInput(String action,String inputText){
		
		try {
			Alert alert = webDriver.switchTo().alert();
			alert.sendKeys(inputText);			
			if(action.equalsIgnoreCase("ACCEPT")){
				alert.accept();
			}else if (action.equalsIgnoreCase("DISMISS")){
				alert.dismiss();
			}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("manageConfirmation Operation Failed");
			throw new WebDriverException("manageConfirmation Operation Failed");
		}
		
	}	
	public void validateConfirmationText(String expText){
		try {
			

			Alert alert = webDriver.switchTo().alert();
			
			String confText = alert.getText();
			
			if(!confText.equals(expText)){
				log.info("Failed to validate Confirmation Text");
				log.info("Actual Text : "+confText);
				log.info("Expected Text : "+confText);
				throw new WebDriverException("Failed to validate Confirmation Text");
			}

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("validateConfirmationText Operation Failed");
			throw new WebDriverException("validateConfirmationText Operation Failed");
		}
	}
	public void removeAttribute(String locator, String attributeName) {

		((JavascriptExecutor) webDriver).executeScript(
				"arguments[0].removeAttribute('" + attributeName + "')",
				webDriver.findElement(By.xpath(locator)));

	}

	public void removeAllSelections(String locator) {

		try {
			Select select = null;
			locator = formatLocator(locator);
			
			if (locator.startsWith("//")) {
				select = new Select(webDriver.findElement(By.xpath(locator)));
			} else {
				select = new Select(webDriver.findElement(By.id(locator)));
			}

			select.deselectAll();

		} catch (Exception e) {
			log.info("Encountered with the exception : " + e.getMessage());
			log.info("removeAllSelection Operation Failed");
			throw new WebDriverException("removeAllSelection Operation Failed");
		}

	}
	
	public void setImplicitlyWaitTimeout(long value,TimeUnit timeout){
		
		webDriver.manage().timeouts().implicitlyWait(value, timeout);
		
	}
	
	public void setPageloadTimeout(long value,TimeUnit timeout){
		
		webDriver.manage().timeouts().pageLoadTimeout(value, timeout);
		
	}

	public String getMainWindowHandle() {
		return mainWindowHandle;
	}
}