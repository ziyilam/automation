package config;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import executionEngine.DriverScript;

public class ActionKeywords {
	public static WebDriver driver;
	private static WebElement source;
	private static WebElement target;
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
				
			case "htmlunit":
				driver = new HtmlUnitDriver();
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
				
				driver.findElement(By.xpath(sObjectLocator)).click();
				//source = driver.findElement(By.xpath(sObjectLocator));
				//if(source.isEnabled())
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
			Boolean bNot=false;
			logger.info("Action......Try Verify text");
			DriverScript.sCompareText = driver.findElement(By.xpath(sObjectLocator)).getText();
			String sInput = sTestData;
			String[] aWords = sInput.split("\\s",2);
			for(String w:aWords) {
				logger.info("w: " + w);
				if(w.equalsIgnoreCase("not")) {
					bNot=true;
					break;
				}
			}
			if(bNot==false) {
				if (DriverScript.sCompareText.equalsIgnoreCase(sTestData)) {
					logger.info(DriverScript.sCompareText + " Text verified to be the same ");
				} else {
					DriverScript.bResult = false;
					logger.info("Text is: " + DriverScript.sCompareText + " compared with expected: " + sTestData);
					logger.info("Text not the same");
				}
			} else {
				if (!DriverScript.sCompareText.equalsIgnoreCase(aWords[1])) {
					logger.info(DriverScript.sCompareText + " Text verified not the same ");
				} else {
					DriverScript.bResult = false;
					logger.info("Text is: " + DriverScript.sCompareText + " compared with expected: " + aWords[1]);
				}
			}
			/*DriverScript.sCompareText = driver.findElement(By.xpath(sObjectLocator)).getText();
			// if(DriverScript.sCompareText.equals(sTestData)){
			if (DriverScript.sCompareText.equalsIgnoreCase(sTestData)) {
				logger.info("Text verified");
			} else {
				DriverScript.bResult = false;
				logger.info("Text is: " + DriverScript.sCompareText + " compared with expected: " + sTestData);
				logger.info("Text not the same");
			}*/

		} catch (Exception e) {
			logger.error("TestStepID: " + DriverScript.sTestStepID + " ActionKeywords|tryVerify. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
	}
	
	public void trySwitch(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			for(String handle : driver.getWindowHandles()) {
				driver.switchTo().window(handle);
				logger.info(" windowHandle: " + handle);
			}
		} catch (Exception e) {
			logger.error(" ActionKeywords|trySwitch. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
	}
	
	public void tryPopup(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			Alert alert = driver.switchTo().alert();
			switch(sTestData.toLowerCase()) {
			//accept or dismiss popup
			case "accept":
				alert.accept();
				break;
			case "dismiss":
				alert.dismiss();
				break;
			//verify its contents
			default:
				DriverScript.sCompareText = alert.getText();
				if(DriverScript.sCompareText.equalsIgnoreCase(sTestData)) {
					logger.info(" text verified");
				}else {
					DriverScript.bResult = false;
					logger.info("Text is: " + DriverScript.sCompareText + " compared with expected: " + sTestData);
					logger.info("Text not the same");
				}
				break;
			
			}
		} catch (Exception e) {
			logger.error(" ActionKeywords|tryPopup. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
	}
	
	public void tryNavigate(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			switch(sTestData.toLowerCase()) {
			
			// move back a single "item" in the browser's history
			case "back":
				driver.navigate().back();
				break;
			// move a single "item" forward in the browser's history
			case "forward":
				driver.navigate().forward();
				break;
			// refresh the current page
			case "refresh":
				driver.navigate().refresh();
				break;
			// load a new web page in the current browser window
			default:
				driver.navigate().to(sTestData);
				break;
			}
		} catch (Exception e) {
			logger.error(" ActionKeywords|tryNavigate. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
	}
	
	public void dragAndDrop(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			source = driver.findElement(By.xpath(sObjectLocator));
			target = driver.findElement(By.xpath(sTestData));
			(new Actions(driver)).dragAndDrop(source, target).perform();
		} catch (Exception e) {
			logger.error(" ActionKeywords|DragAndDrop. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
		
	}
	
	public void selectDropDown(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			int iDropDownIndex = Integer.parseInt(sTestData);
			(new Select(driver.findElement(By.xpath(sObjectLocator)))).selectByIndex(iDropDownIndex);
			logger.info(" selecting dropdown item ");
		} catch (NumberFormatException e) {
			logger.error(" ActionKeywords|selectDropDown. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
	}
	
	public void verifyURL(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			DriverScript.sCompareText = driver.getCurrentUrl();
			if(DriverScript.sCompareText.equals(sObjectLocator)) {
				logger.info("URL verified");
				
			}else {
				DriverScript.bResult = false;
				logger.info("URL is: " + DriverScript.sCompareText + " compared with expected: " + sObjectLocator);
				logger.info("Text not the same");
			}
		} catch (Exception e) {
			logger.error(" ActionKeywords|verifyURL. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
		
	}
	
	public void trySlide(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			source = driver.findElement(By.xpath(sObjectLocator));
			
			int iXcoordinate = source.getLocation().getX();
			int iYcoordinate = source.getLocation().getY();
			logger.info("x1, y1: " + iXcoordinate + ", " + iYcoordinate);
			
			int iOffset = Integer.parseInt(sTestData);
			int iXoffset = iXcoordinate + iOffset;
			logger.info("x before offset, x after offset: " + iXcoordinate + ", " + iXoffset);
			(new Actions(driver)).dragAndDropBy(source, iXoffset, iYcoordinate).perform();
		} catch (NumberFormatException e) {
			logger.error(" ActionKeywords|trySlide. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
	}
	
	public void tryScroll(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			JavascriptExecutor js = (JavascriptExecutor)driver;
			source = driver.findElement(By.xpath(sObjectLocator));
			int iXcoordinate = source.getLocation().getX();
			int iYcoordinate = source.getLocation().getY();
			js.executeScript("window.scrollBy("+iXcoordinate+","+iYcoordinate+")","");
		} catch (Exception e) {
			logger.error(" ActionKeywords|tryScroll. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
	}
	
	public void waitUntil(String sObjectLocator, String sActionKeyword, String sTestData) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 20);
			switch(sTestData) {
			case "clickable":
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath(sObjectLocator)));
				break;
			default:
				wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(sObjectLocator)));
				break;
			}
		} catch (Exception e) {
			logger.error(" ActionKeywords|waitUntil. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
	}
}
