package SourceCode.com.jda.qa.platform.framework.browser;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * 
 * @Author Vinay Tangella & Santosh Medchalam
 * This interface has all the methods the web-driver-API offers
 *
 */
@SuppressWarnings("deprecation")
public interface TestBrowser {
	
	public String getURL() ;

	/**
	 * 
	 * This will return the type of the Browser
	 * 
	 */
	
	public String getBrowserType();

	/**
	 * This performs a refresh operation on the current Web Page  
	 */
	public void refresh();

	/**
	 * This returns a WebDriver object  
	 */
	public Object getWebDriver();

	/**
	 * This Method invokes the Browser with the given URL 
	 */
	public void start();

	/**
	 * This Method closes the WebDriver instance  
	 */
	public void stop();
	
	/**
	 * This Method is used to select the options from the Select Drop Down  
	 */
	public void addSelection(String locator, String optionLocator);

	/**
	 * This Method is used to select the options from the Select Drop Down by verifying whether the given locator is already selected
	 */
	public void addSelection(String locator, String optionLocator,
                             boolean isValueSelect);

	/**
	 * This Method returns the Attribute value of a locator
	 */
	public String getAttribute(String locator, String attributeName);
	
	/**
	 * This Method returns the tag name of this element value of a locator
	 */	
	public String getTagName(String attributeName, String attributeValue);
	
	/**
	 * This Method returns the Title of the browser
	 */
	public boolean isAttribtuePresent(String locator, String attributeName);

	
	public String getTitle();

	/**
	 * This Method returns the Xpath count of the location passed
	 */
	public int getXpathCount(String path);

	/**
	 * This Method returns the Xpath count of the location inside a specific frame passed
	 */
	public int getXpathCount(String path, String frame);

	/**
	 * This Method invokes the Browser with the given URL
	 */
	public void open(String url);
	
	/**
	 * This Method switches the WebDriver browser control to the Frame content 
	 */
	public void selectFrame(String name);
	
	/**
	 * This Method switches the WebDriver browser control to the Frame content 
	 */
	public void selectWindow(String name);
	
	/**
	 * This Method sets the time frame for which the execution should be paused 
	 */
	public void setSpeed(String value);

	/**
	 * This Method sets the time frame of default 2seconds for which the execution should be paused 
	 */
	public void waitForPageToLoad();

	/**
	 * This Method allows to pass the time frame in milliseconds to pause the execution 
	 */
	public void waitForPageToLoad(long milliseconds);
	
	/**
	 * This Method allows to pass the time frame in milliseconds to wait for the specific locator
	 */
	public void waitForPageToLoad(String locator, String timeout);

	/**
	 * This Method returns a boolean value after verifying the presence of a locator 
	 */
	public boolean isElementPresent(String locator);
	
	/**
	 * This Method returns a boolean value after verifying the presence of a locator 
	 */
	public boolean isElementPresent(String locator, boolean disolveException);

	/**
	 * This Method returns a boolean value after verifying the presence of a locator for a certain amount of time
	 */
	public boolean isElementPresent(final String locator, int waitTime);

	/**
	 * This Method returns a boolean value after verifying the presence of a text matching the pattern
	 */
	public boolean isTextPresent(String pattern);

	/**
	 * This Method returns waiting time for an element to load
	 */
	public int getWaitLoadTime();

	/**
	 * This Method sets waiting time for an element to load
	 */
	public void setWaitLoadTime(int waitLoadTime);

	/**
	 * This Method is used to enter characters though keyboard
	 */
	public void keyPress(String locator, String keySequence);
	
	/**
	 * This Method is used to wait for the window handler
	 */
	public void waitForPopUp(String windowID, int timeout);

	/**
	 * This Method is used to make the web-driver instance wait until some condition is true 
	 */
	public void waitUntilElementIspresent(String locator, long timeout);

	/**
	 * This method is used to close the pop-up window
	 */
	public void closePopUpWindow();

	/**
	 * This method is used to close the pop-up window that matches with the name passed
	 */
	public void closeWindow(String name);

	/**
	 * This method is used to get the window count
	 */
	public int getWindowCount();

	/**
	 * This method sets the web-driver control to the default frame
	 */
	public void setDefaultFrame();

	/**
	 * This method is used to get the Browser Title
	 */
	public String getBrowserTitle();

	/**
	 * This method is used to get the control on the window handler
	 */
	public String getAlert();

	/**
	 * This method is used to make the web-driver wait for a certain amount of time until the element is not present 
	 */
	public void waitForElementNotPresent(String elementId, int timeout);

	/**
	 * This method returns the current url 
	 */
	public String getLocation();

	/**
	 * This method returns the browser control
	 */
	public WebDriver getBrowser();

	/**
	 * This method is used to maximize the browser Window 
	 */
	public void windowMaximize();

	/**
	 * This method returns the text attribute value for the passed locator 
	 */
	public String getText(String locator);

	/**
	 * This Method is used to enter characters though keyboard
	 */
	public void type(String locator, String content);

	/**
	 * This Method is used to click the locator on the page
	 */
	public void click(String locator);

	/**
	 * This Method returns a boolean value after verifying the checked status of the locator
	 */
	public boolean isChecked(String locator);

	/**
	 * This Method is used to highlight the locator passed
	 */
	public void highlight(String locator);
	
	/**
	 * This Method is used to highlight the border locator passed
	 */
	public void highlightBorder(String locator);

	/**
	 * This Method is not yet completely developed
	 */
	public void answerOnNextPrompt(String value);

	/**
	 * This Method allows to pass the time frame in milliseconds to pause the execution 
	 */
	public void waitForPageToLoad(String waitTime);

	/**
	 * This Method closes the WebDriver instance  
	 */
	public void close();
	
	/**
	 * This Method is used to select the option from the drop down
	 */

	public void select(String locator, String value);

	/**
	 * This Method is used to select the option from the drop down using the value attribute
	 */
	public void selectByValue(String locator, String value);

	/**
	 * This Method is used to clear the value from the locator
	 */
	public void clear(String locator);

	/**
	 * This Method is used to enter values from the keyboard
	 */
	public void sendKeys(String locator, Keys key);

	/**
	 * This Method returns value from the locator
	 */
	public String getValue(String locator);

	/**
	 * This Method returns selected values of the select drop down
	 */
	public String[] getSelectedLabels(String locator);

	/**
	 * This Method pauses the execution for a certain amount of time
	 */
	public void sleep(int time);

	/**
	 * This Method returns the control of the web-driver to the default window
	 */
	public void switchToDefaultContent();

	/**
	 * This Method is used to select the frames
	 */
	public void selectFrames(String[] frames);

	/**
	 * This Method is used to select all the values from the drop down 
	 */
	public void selectAll(String locatorId);

	/**
	 * This Method returns the list of the WebElements of the locator
	 */
	public List<WebElement> findElements(String locator);

	/**
	 * This Method returns options for the locator of the attribute type
	 */
	public String[] getOptions(String locator, String attType);

	/**
	 * This Method returns options for the locator
	 */
	public String[] getOptions(String locator);
	
	/**
	 * This Method returns the selected values of the drop-down 
	 */
	public String[] getSelectedValues(String locator);

	/**
	 * This Method is used to set the attribute value
	 */
	public void setAttribute(String locator, String attributeName,
                             String attributeValue);

	/**
	 * This Method returns Actions object
	 */
	public Object getActions();

	/**
	 * This Method returns WebElement object
	 */
	public Object findElement(By xpath);
	
	/**
	 * This Method returns a boolean value after verifying whether the locator is selected or not
	 */
	public boolean isSelected(String locator);

	public void updateWindowHandles();
	
	public Set getWindowHandles();
	
	public void moveToElement(String xpath);
	
	public void moveToElement(WebElement element);
	
	public void scrollIntoView(String locator);
	
	public void scrollIntoView(WebElement element);
	
	public String getPopUpWindowname();

	public void setPopUpWindowname(String popUpWindowname) ;

	public Object findElement(String locator);

    public void manageConfirmation(String action);
    
    public void manageConfirmationTextInput(String action, String inputText);
    
    public void validateConfirmationText(String expText);
	
	public void removeAttribute(String locator, String attributeName);
	
	public String getText(String locator, boolean trimContent);
	
	public void removeAllSelections(String locator);
	
	public String getMainWindowHandle();
	
}
