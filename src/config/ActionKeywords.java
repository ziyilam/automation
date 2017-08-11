package config;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;

import executionEngine.DriverScript;
import utility.Log;


public class ActionKeywords {
	public static WebDriver driver;

	public void openBrowser(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			switch (sTestData.toLowerCase()) {
			case "chrome":
				System.setProperty(Constants.Chrome_Property1, Constants.Chrome_Property2);
				driver = new ChromeDriver();
				//driver.manage().window().maximize();
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
				Log.warn("Browser name not match");
				break;
			}

			driver.get(sObjectLocator);
			driver.manage().deleteAllCookies();
			//driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
			Log.info("Action......Opening the browser");
			
		} catch (Exception e) {
			Log.error("TestStepID: " + DriverScript.sTestStepID + " ActionKeywords|openBrowser. Exception Message - "
					+ e.getMessage());
			DriverScript.bResult = false;
		}
	}

	public void tryClick(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			Log.info("Action......Clicking on ObjectLocator " + sObjectLocator);
			//driver.findElement(By.cssSelector(sObjectLocator)).click();
			driver.findElement(By.xpath(sObjectLocator)).click();
		} catch (Exception e) {
			Log.error("TestStepID: " + DriverScript.sTestStepID + " ActionKeywords|tryClick. Exception Message - "
					+ e.getMessage());
			DriverScript.bResult = false;
		}

	}

	public void tryInput(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			Log.info("Action......Input the text into ObjectLocator " + sObjectLocator);
			//driver.findElement(By.cssSelector(sObjectLocator)).sendKeys(sTestData);
			driver.findElement(By.xpath(sObjectLocator)).sendKeys(sTestData);
		} catch (Exception e) {
			Log.error("TestStepID: " + DriverScript.sTestStepID + " ActionKeywords|tryInput. Exception Message - "
					+ e.getMessage());
			DriverScript.bResult = false;
		}
	}

	public void tryClose(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			Log.info("Action......Closing the browser");
			driver.close();
			//driver.quit();
		} catch (Exception e) {
			Log.error("TestStepID: " + DriverScript.sTestStepID + " ActionKeywords|tryClose. Exception Message - "
					+ e.getMessage());
			DriverScript.bResult = false;
		}
		// System.exit(0);
	}
	
	public void tryVerify (String sObjectLocator, String sActionKeyword, String sTestData) {
		
		try {
			Log.info("Action......Try Verify text");
			DriverScript.sCompareText = driver.findElement(By.xpath(sObjectLocator)).getText();
			// if(DriverScript.sCompareText.equals(sTestData)){ 
			if(DriverScript.sCompareText.equalsIgnoreCase(sTestData)) {
				Log.info("Text verified");
			}else {
				DriverScript.bResult = false;
				Log.info("Text is: " + DriverScript.sCompareText + " compared with expected: " + sTestData);
				Log.info("Text not the same");
			}
			
		} catch (Exception e) {
			Log.error("TestStepID: " + DriverScript.sTestStepID + " ActionKeywords|tryVerify. Exception Message - "
					+ e.getMessage());
			DriverScript.bResult = false;
		}
	}

}
