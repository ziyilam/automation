package executionEngine;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.log4j.xml.DOMConfigurator;

import config.ActionKeywords;
import config.Constants;
import utility.ExcelUtils;
import utility.Log;

public class DriverScript {
	public static ActionKeywords actionKeywords;
	public static String sActionKeyword;
	public static String sObjectLocator;
	public static String sTestData;
	public static String sTestDataItem;
	public static String sTestStepID;
	public static String sTestCaseID;
	public static String sRunMode;
	public static String sCompareText;
	public static boolean bResult;
	public static Method method[];
	public static ArrayList<String> alCellHeader;
	public static int iTotalTestCases;
	public static int iCellHeaderIndex;
	public static int iStartTestData;
	public static int iLastTestData;
	public static int iCountTestData;
	public static int iStartTestStep;
	public static int iLastTestStep;
	public static int iCountTestStep;
	public static int iCountCol;

	public DriverScript() throws NoSuchMethodException, SecurityException {
		actionKeywords = new ActionKeywords();
		method = actionKeywords.getClass().getMethods();
		alCellHeader = new ArrayList<>();
	}

	public static void main(String[] args) throws Exception {

		ExcelUtils.setExcelFile(Constants.Path_TestData);
		DOMConfigurator.configure("log4j.xml");
		// DOMConfigurator.configure(Constants.Path_log4j);
		DriverScript startEngine = new DriverScript();
		startEngine.execute_TestCase();

	}

	private void execute_TestCase() throws Exception {
		iTotalTestCases = ExcelUtils.getRowCount(Constants.Sheet_TestCases);
		Log.info("Total TestCases: " + iTotalTestCases);

		record_HeaderName();

		Log.info("---------------------------------------   START  ---------------------------------------");
		// Loop from test case no.1 to the last test case
		for (int iTestcase = 1; iTestcase <= iTotalTestCases; iTestcase++) {
			// Every new Test case the bResult is reset to true
			bResult = true;
			Log.info("TestCase no.: " + iTestcase);
			sTestCaseID = ExcelUtils.getCellData(iTestcase, Constants.Col_TestCaseID, Constants.Sheet_TestCases);
			Log.info("TestCaseID: " + sTestCaseID);
			sRunMode = ExcelUtils.getCellData(iTestcase, Constants.Col_RunMode, Constants.Sheet_TestCases);
			Log.info("sRunMode: " + sRunMode);
			Log.info("-");
			Log.info("-");
			Log.info("-");
			// Only execute the test case with run mode equals to yes
			if (sRunMode.equalsIgnoreCase("Yes")) {
				Log.startTestCase(sTestCaseID);
				iStartTestStep = ExcelUtils.getRowStartWith(sTestCaseID, Constants.Col_TestCaseID,
						Constants.Sheet_TestSteps);
				Log.info("1st TestStep at row: " + iStartTestStep);
				iLastTestStep = ExcelUtils.getStepsCount(Constants.Sheet_TestSteps, sTestCaseID, iStartTestStep);
				Log.info("Last TestStep at row: " + iLastTestStep);
				iStartTestData = ExcelUtils.getRowStartWith(sTestCaseID, Constants.Col_TestCaseID,
						Constants.Sheet_TestData);
				iLastTestData = ExcelUtils.getStepsCount(Constants.Sheet_TestData, sTestCaseID, iStartTestData);
				Log.info("1st TestData at row: " + iStartTestData);
				Log.info("Last TestData at row: " + iLastTestData);
				
				// Execute for sets of different test data
				for (iCountTestData = iStartTestData; iCountTestData <= iLastTestData; iCountTestData++) {
					// every new set of test data the bResult is reset to true
					bResult = true;
					Log.info("Test start for new TestData");
					Log.info("-");
					Log.info("-");
					Log.info("-");
						// Loop for all test steps
						for (iCountTestStep = iStartTestStep; iCountTestStep <= iLastTestStep; iCountTestStep++) {
							Log.info("TestStep row no.: " + iCountTestStep);
							sObjectLocator = ExcelUtils.getCellData(iCountTestStep, Constants.Col_ObjectLocator,
									Constants.Sheet_TestSteps);
							sActionKeyword = ExcelUtils.getCellData(iCountTestStep, Constants.Col_ActionKeyword,
									Constants.Sheet_TestSteps);
							sTestStepID = ExcelUtils.getCellData(iCountTestStep, Constants.Col_TestStepID,
									Constants.Sheet_TestSteps);

							sTestData = ExcelUtils.getCellData(iCountTestStep, Constants.Col_TestData,
									Constants.Sheet_TestSteps);

							fetch_TestData(sTestData);

							execute_Action();
						
							// Record test case fail and close browser
							if (bResult == false) {
								ExcelUtils.setCellData(Constants.KEYWORD_FAIL, iTestcase, Constants.Col_CaseResults,
								Constants.Sheet_TestCases);
								Log.warn("......Test Case Failed for " + sTestCaseID + "......");
								Log.info("-");
								Log.info("-");
								Log.info("-");
								actionKeywords.tryClose("", "", "");
								Log.info("close browser from TestCase loop");
								break;
								}

						}
						// Record test case pass
						if (bResult == true) {
							ExcelUtils.setCellData(Constants.KEYWORD_PASS, iTestcase, Constants.Col_CaseResults,
							Constants.Sheet_TestCases);
							Log.info("......All TestStep Completed for " + sTestCaseID + "......");
						}
						// If test case fail, skip for the rest of the data set
						if (bResult == false) {
							break;
						}
						
				}
				Log.endTestCase(sTestCaseID);
			}
		}
		Log.info("......No more TestCase with 'Yes' RunMode......");
		Log.info("---------------------------------------   E-N-D  ---------------------------------------");
	}

	private static void execute_Action() throws Exception {
		Boolean bKeyword = false;
		try {
			for (int i = 0; i < method.length; i++) {
				if (method[i].getName().equalsIgnoreCase(sActionKeyword)) {
					bKeyword = true;
					method[i].invoke(actionKeywords, sObjectLocator, sActionKeyword, sTestDataItem);
					Log.info("Executed TestData Item: " + sTestDataItem);
					if (bResult == true) {
						
						Log.info("bResult: " + bResult + " for iStartTestStep: " + iCountTestStep);
						ExcelUtils.setCellData(Constants.KEYWORD_PASS, iCountTestStep, Constants.Col_StepResults,
								Constants.Sheet_TestSteps);
						break;
					} else {
						Log.info("bResult:..." + bResult + "...for iStartTestStep:..." + iCountTestStep);
						ExcelUtils.setCellData(Constants.KEYWORD_FAIL, iCountTestStep, Constants.Col_StepResults,
								Constants.Sheet_TestSteps);
						// actionKeywords.tryClose("", "", "");
						// Log.info("close browser from TestStep loop");
						break;
							}
				}
				
			}
			if (bKeyword == false) {
				Log.warn("......No such keyword......" + sActionKeyword);
				bResult = false;
				Log.info("bResult:..." + bResult + "...for iStartTestStep:..." + iCountTestStep);
				ExcelUtils.setCellData(Constants.KEYWORD_FAIL, iCountTestStep, Constants.Col_StepResults,
						Constants.Sheet_TestSteps);
			}

		} catch (Exception e) {
			Log.error("DriverScript|execute_Action. Exception message: " + e.getMessage());
		}
		
	}

	private static void record_HeaderName() throws Exception {

		try {
			iCountCol = ExcelUtils.getColCount(Constants.Sheet_TestData, 0);
			Log.info("TestData sheet ColCount: " + iCountCol);
			for (int i = 0; i < iCountCol; i++) {

				alCellHeader.add(ExcelUtils.getCellData(0, i, Constants.Sheet_TestData));

			}
			Log.info("Header Name for TestData Sheet: " + alCellHeader);
		} catch (Exception e) {
			Log.error("DriverScript|record_HeaderName. Exception message: " + e.getMessage());
		}

	}

	private void fetch_TestData(String sTestData) throws Exception {
		try {
			//String str = sTestData.toLowerCase();
			switch (sTestData.toLowerCase()) {
			case "username":
				iCellHeaderIndex = alCellHeader.indexOf("username");
				sTestDataItem = ExcelUtils.getCellData(iCountTestData, iCellHeaderIndex, Constants.Sheet_TestData);
				Log.info("Fetching TestData Item: " + sTestDataItem);
				break;
			case "password":
				iCellHeaderIndex = alCellHeader.indexOf("password");
				sTestDataItem = ExcelUtils.getCellData(iCountTestData, iCellHeaderIndex, Constants.Sheet_TestData);
				Log.info("Fetching TestData Item: " + sTestDataItem);
				break;
			case "browser":
				iCellHeaderIndex = alCellHeader.indexOf("browser");
				sTestDataItem = ExcelUtils.getCellData(iCountTestData, iCellHeaderIndex, Constants.Sheet_TestData);
				Log.info("Fetching TestData Item: " + sTestDataItem);
				break;
			default:
				// should assign sTestDataItem = sTestData when there is no such Test Data in
				// TestData sheet
				sTestDataItem = sTestData;
				break;
			}
		} catch (Exception e) {
			Log.error("DriverScript|fetch_TestData. Exception message: " + e.getMessage());
		}
	}

}
