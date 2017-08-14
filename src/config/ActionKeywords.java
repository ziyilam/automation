package config;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;

import executionEngine.DriverScript;

public class ActionKeywords {
	public static WebDriver driver;
	private static final Logger logger = LogManager.getLogger(ActionKeywords.class.getName());

	public void openBrowser(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			switch (sTestData.toLowerCase()) {
			case "chrome":
				System.setProperty(Constants.Chrome_Property1, Constants.Chrome_Property2);
				driver = new ChromeDriver();
				// driver.manage().window().maximize();
				break;

			case "ie":
				System.setProperty(Constants.IE_Property1, Constants.IE_Property2);
				driver = new InternetExplorerDriver();
				break;

			case "firefox":
				System.setProperty(Constants.Firefox_Property1, Constants.Firefox_Property2);
				driver = new FirefoxDriver();
				break;

			case "safari":
				driver = new SafariDriver();
				break;

			default:
				logger.warn("Browser name not match");
				break;
			}

			driver.get(sObjectLocator);
			driver.manage().deleteAllCookies();
			// driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
			logger.info("Action......Opening the browser");

		} catch (Exception e) {
			logger.error("TestStepID: " + DriverScript.sTestStepID + " ActionKeywords|openBrowser. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
	}

	public void tryClick(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			logger.info("Action......Clicking on ObjectLocator " + sObjectLocator);
			// driver.findElement(By.cssSelector(sObjectLocator)).click();
			driver.findElement(By.xpath(sObjectLocator)).click();
		} catch (Exception e) {
			logger.error("TestStepID: " + DriverScript.sTestStepID + " ActionKeywords|tryClick. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	public void tryInput(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			logger.info("Action......Input the text into ObjectLocator " + sObjectLocator);
			// driver.findElement(By.cssSelector(sObjectLocator)).sendKeys(sTestData);
			driver.findElement(By.xpath(sObjectLocator)).sendKeys(sTestData);
		} catch (Exception e) {
			logger.error("TestStepID: " + DriverScript.sTestStepID + " ActionKeywords|tryInput. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
	}

	public void tryClose(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			logger.info("Action......Closing the browser");
			driver.close();
			// driver.quit();
		} catch (Exception e) {
			logger.error("TestStepID: " + DriverScript.sTestStepID + " ActionKeywords|tryClose. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
		// System.exit(0);
	}

	public void tryVerify(String sObjectLocator, String sActionKeyword, String sTestData) {

		try {
			logger.info("Action......Try Verify text");
			DriverScript.sCompareText = driver.findElement(By.xpath(sObjectLocator)).getText();
			// if(DriverScript.sCompareText.equals(sTestData)){
			if (DriverScript.sCompareText.equalsIgnoreCase(sTestData)) {
				logger.info("Text verified");
			} else {
				DriverScript.bResult = false;
				logger.info("Text is: " + DriverScript.sCompareText + " compared with expected: " + sTestData);
				logger.info("Text not the same");
			}

		} catch (Exception e) {
			logger.error("TestStepID: " + DriverScript.sTestStepID + " ActionKeywords|tryVerify. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
	}
	
	public void trySwitch(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			for(String handle : driver.getWindowHandles()) {
				driver.switchTo().window(handle);
				logger.info(handle);
			}
		} catch (Exception e) {
			logger.error(" ActionKeywords|trySwitch. Exception Message - " + e.getMessage());
		}
	}

}
