package executionEngine;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import config.ActionKeywords;
import config.Constants;
import utility.ExcelUtils;

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

	private static final Logger logger = LogManager.getLogger(DriverScript.class.getName());

	public DriverScript() throws NoSuchMethodException, SecurityException {
		actionKeywords = new ActionKeywords();
		method = actionKeywords.getClass().getMethods();
		alCellHeader = new ArrayList<>();
	}

	public static void main(String[] args) throws Exception {

		ExcelUtils.setExcelFile(Constants.Path_TestData);
		DriverScript startEngine = new DriverScript();
		startEngine.execute_TestCase();

	}

	private void execute_TestCase() throws Exception {
		iTotalTestCases = ExcelUtils.getRowCount(Constants.Sheet_TestCases);
		logger.info("Total TestCases: " + iTotalTestCases);

		record_HeaderName();

		logger.info("---------------------------------------   START  ---------------------------------------");
		// Loop from test case no.1 to the last test case
		for (int iTestcase = 1; iTestcase <= iTotalTestCases; iTestcase++) {
			// Every new Test case the bResult is reset to true
			bResult = true;
			logger.info("TestCase no.: " + iTestcase);
			sTestCaseID = ExcelUtils.getCellData(iTestcase, Constants.Col_TestCaseID, Constants.Sheet_TestCases);
			logger.info("TestCaseID: " + sTestCaseID);
			sRunMode = ExcelUtils.getCellData(iTestcase, Constants.Col_RunMode, Constants.Sheet_TestCases);
			logger.info("sRunMode: " + sRunMode);
			logger.info("-");
			logger.info("-");
			logger.info("-");
			// Only execute the test case with run mode equals to yes
			if (sRunMode.equalsIgnoreCase("Yes")) {
				startTestCase(sTestCaseID);
				iStartTestStep = ExcelUtils.getRowStartWith(sTestCaseID, Constants.Col_TestCaseID,
						Constants.Sheet_TestSteps);
				logger.info("1st TestStep at row: " + iStartTestStep);
				iLastTestStep = ExcelUtils.getStepsCount(Constants.Sheet_TestSteps, sTestCaseID, iStartTestStep);
				logger.info("Last TestStep at row: " + iLastTestStep);
				iStartTestData = ExcelUtils.getRowStartWith(sTestCaseID, Constants.Col_TestCaseID,
						Constants.Sheet_TestData);
				iLastTestData = ExcelUtils.getStepsCount(Constants.Sheet_TestData, sTestCaseID, iStartTestData);
				logger.info("1st TestData at row: " + iStartTestData);
				logger.info("Last TestData at row: " + iLastTestData);

				// Execute for sets of different test data
				for (iCountTestData = iStartTestData; iCountTestData <= iLastTestData; iCountTestData++) {
					// every new set of test data the bResult is reset to true
					bResult = true;
					logger.info("Test start for new TestData");
					logger.info("-");
					logger.info("-");
					logger.info("-");
					// Loop for all test steps
					for (iCountTestStep = iStartTestStep; iCountTestStep <= iLastTestStep; iCountTestStep++) {
						logger.info("TestStep row no.: " + iCountTestStep);
						sObjectLocator = ExcelUtils.getCellData(iCountTestStep, Constants.Col_ObjectLocator,
								Constants.Sheet_TestSteps);
						sActionKeyword = ExcelUtils.getCellData(iCountTestStep, Constants.Col_ActionKeyword,
								Constants.Sheet_TestSteps);
						sTestStepID = ExcelUtils.getCellData(iCountTestStep, Constants.Col_TestStepID,
								Constants.Sheet_TestSteps);

						sTestData = ExcelUtils.getCellData(iCountTestStep, Constants.Col_TestData,
								Constants.Sheet_TestSteps);
						logger.info(" sTestData from TestSteps: " + sTestData);
						fetch_TestData(sTestData);

						execute_Action();

						// Record test case fail and close browser
						if (bResult == false) {
							ExcelUtils.setCellData(Constants.KEYWORD_FAIL, iTestcase, Constants.Col_CaseResults,
									Constants.Sheet_TestCases);
							logger.warn("......Test Case Failed for " + sTestCaseID + "......");
							logger.info("-");
							logger.info("-");
							logger.info("-");
							actionKeywords.tryClose("", "", "");
							logger.info("close browser from TestCase loop");
							break;
						}

					}
					// Record test case pass
					if (bResult == true) {
						ExcelUtils.setCellData(Constants.KEYWORD_PASS, iTestcase, Constants.Col_CaseResults,
								Constants.Sheet_TestCases);
						logger.info("......All TestStep Completed for " + sTestCaseID + "......");
					}
					// If test case fail, skip for the rest of the data set
					if (bResult == false) {
						break;
					}

				}
				endTestCase(sTestCaseID);
			}
		}
		logger.info("......No more TestCase with 'Yes' RunMode......");
		logger.info("---------------------------------------   E-N-D  ---------------------------------------");
	}

	private static void execute_Action() throws Exception {
		Boolean bKeyword = false;
		try {
			for (int i = 0; i < method.length; i++) {
				if (method[i].getName().equalsIgnoreCase(sActionKeyword)) {
					bKeyword = true;
					method[i].invoke(actionKeywords, sObjectLocator, sActionKeyword, sTestDataItem);
					logger.info("Executed TestData Item: " + sTestDataItem);
					if (bResult == true) {

						logger.info("bResult: " + bResult + " for iStartTestStep: " + iCountTestStep);
						ExcelUtils.setCellData(Constants.KEYWORD_PASS, iCountTestStep, Constants.Col_StepResults,
								Constants.Sheet_TestSteps);
						break;
					} else {
						logger.info("bResult:..." + bResult + "...for iStartTestStep:..." + iCountTestStep);
						ExcelUtils.setCellData(Constants.KEYWORD_FAIL, iCountTestStep, Constants.Col_StepResults,
								Constants.Sheet_TestSteps);
						// actionKeywords.tryClose("", "", "");
						// logger.info("close browser from TestStep loop");
						break;
					}
				}

			}
			if (bKeyword == false) {
				logger.warn("......No such keyword......" + sActionKeyword);
				bResult = false;
				logger.info("bResult:..." + bResult + "...for iStartTestStep:..." + iCountTestStep);
				ExcelUtils.setCellData(Constants.KEYWORD_FAIL, iCountTestStep, Constants.Col_StepResults,
						Constants.Sheet_TestSteps);
			}

		} catch (Exception e) {
			logger.error("DriverScript|execute_Action. Exception message: " + e.getMessage());
		}

	}

	private static void record_HeaderName() throws Exception {

		try {
			iCountCol = ExcelUtils.getColCount(Constants.Sheet_TestData, 0);
			logger.info("TestData sheet ColCount: " + iCountCol);
			for (int i = 0; i < iCountCol; i++) {

				alCellHeader.add(ExcelUtils.getCellData(0, i, Constants.Sheet_TestData));

			}
			logger.info("Header Name for TestData Sheet: " + alCellHeader);
		} catch (Exception e) {
			logger.error("DriverScript|record_HeaderName. Exception message: " + e.getMessage());
		}

	}

	private void fetch_TestData(String sTestData) throws Exception {
		try {
			// String str = sTestData.toLowerCase();
			switch (sTestData.toLowerCase()) {
			case "d_username":
				iCellHeaderIndex = alCellHeader.indexOf("d_username");
				sTestDataItem = ExcelUtils.getCellData(iCountTestData, iCellHeaderIndex, Constants.Sheet_TestData);
				logger.info("Fetching TestData Item: " + sTestDataItem);
				break;
			case "d_password":
				iCellHeaderIndex = alCellHeader.indexOf("d_password");
				sTestDataItem = ExcelUtils.getCellData(iCountTestData, iCellHeaderIndex, Constants.Sheet_TestData);
				logger.info("Fetching TestData Item: " + sTestDataItem);
				break;
			case "d_browser":
				iCellHeaderIndex = alCellHeader.indexOf("d_browser");
				sTestDataItem = ExcelUtils.getCellData(iCountTestData, iCellHeaderIndex, Constants.Sheet_TestData);
				logger.info("Fetching TestData Item: " + sTestDataItem);
				break;
			default:
				// should assign sTestDataItem = sTestData when there is no such Test Data in
				// TestData sheet
				sTestDataItem = sTestData;
				break;
			}
		} catch (Exception e) {
			logger.error("DriverScript|fetch_TestData. Exception message: " + e.getMessage());
		}
	}

	private static void startTestCase(String sTestCaseName) {

		logger.info("----------------------------------------------------------------------------------------");
		logger.info("----------------------------------------------------------------------------------------");
		logger.warn("-------------------             " + sTestCaseName
				+ " TestStep begins                -------------------");
		logger.info("----------------------------------------------------------------------------------------");
		logger.info("----------------------------------------------------------------------------------------");

	}

	private static void endTestCase(String sTestCaseName) {
		logger.warn("--------------------------------TestCase " + sTestCaseName + " Ended"
				+ "------------------------------------");
		logger.info("-");
		logger.info("-");
		logger.info("-");
		logger.info("-");

	}

}
