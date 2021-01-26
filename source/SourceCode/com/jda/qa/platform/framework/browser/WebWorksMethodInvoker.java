package SourceCode.com.jda.qa.platform.framework.browser;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.List;

import com.jda.qa.platform.framework.browser.BaseMethodInvoker;
import com.jda.qa.platform.framework.browser.TestBrowser;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.jda.qa.platform.framework.base.MainEngine;

public class WebWorksMethodInvoker extends BaseMethodInvoker {

	public void runMethod(String methodName, String... args)
			throws WebDriverException {

		TestBrowser browserObj = MainEngine.browser;

		try {

			switch (methodName.toUpperCase()) {
						
			case "WAIT": {

				Thread.sleep(Long.parseLong(args[0]));
				break;
			}
			
			case "SETDEFAULTPWBRANKINGS": {

				browserObj.switchToDefaultContent();

				browserObj.selectFrame("//iframe[@id='appFrame']");
				
				int rowCount = browserObj.getXpathCount("//table[@id='columnRankGrid']/descendant::tr[contains(@id,'colRankRow')]");
				
				for(int index=0;index<rowCount;index++){
					String locator = "//table[@id='columnRankGrid']/descendant::tr[contains(@id,'colRankRow"+index+"')]";
					browserObj.scrollIntoView(locator);
					browserObj.click(locator);
					Thread.sleep(1000);
				}		
							
				rowCount = browserObj.getXpathCount("//table[@id='hierarchyRankGrid']/descendant::tr[contains(@id,'hierRankRow')]");
				
				for(int index=0;index<rowCount;index++){
					String locator = "//table[@id='hierarchyRankGrid']/descendant::tr[contains(@id,'hierRankRow"+index+"')]";
					browserObj.scrollIntoView(locator);
					browserObj.click(locator);
					Thread.sleep(1000);
				}
				
				browserObj.click("pwb\\rankingScreen:::saveBtn");
				
				browserObj.switchToDefaultContent();
								
				break;
			}
			
			case "SELECTALLTABLEROWS":{
				
				String rowXPath=args[0];

				Actions oAc = (Actions) browserObj.getActions();

				List<WebElement> rows = browserObj.findElements(rowXPath);

				if (rows.size() >= 1) {
					
					oAc.keyDown(Keys.CONTROL);
					oAc.perform();
					
					if (browserObj.getBrowserType().equals("iexplore")) {
						Robot robot = new Robot();
						robot.keyPress(KeyEvent.VK_CONTROL);
					}
					
					for (int i = 1; i <= rows.size(); i++) {

						String locator = rowXPath+"["+i+"]";
						browserObj.scrollIntoView(locator);
						browserObj.click(locator);
						Thread.sleep(1000);

					}
					
					oAc.keyUp(Keys.CONTROL);
					oAc.perform();

					if (browserObj.getBrowserType().equals("iexplore")) {
						Robot robot = new Robot();
						robot.keyRelease(KeyEvent.VK_CONTROL);
					}

				}

				
				break;
			}
			
			default: {
				super.runMethod(methodName, args);
			}

			}

		} catch (Exception e) {
			throw new WebDriverException(e.getMessage());
		}
	}
}
