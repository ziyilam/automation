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
		logger.info("\n\n---------------------------------------   START  ---------------------------------------\n\n\n");
		iTotalTestCases = ExcelUtils.getRowCount(Constants.Sheet_TestCases);
		logger.info("Total TestCases: " + iTotalTestCases);
		
		// record the column available in the testData sheet
		record_HeaderName();

		// Loop from test case no.1 to the last test case
		for (int iTestcase = 1; iTestcase <= iTotalTestCases; iTestcase++) {
			// Every new Test case the bResult is reset to true
			bResult = true;
			logger.info("TestCase no.: " + iTestcase);
			sTestCaseID = ExcelUtils.getCellData(iTestcase, Constants.Col_TestCaseID, Constants.Sheet_TestCases);
			logger.info("TestCaseID: " + sTestCaseID);
			sRunMode = ExcelUtils.getCellData(iTestcase, Constants.Col_RunMode, Constants.Sheet_TestCases);
			logger.info("\n\nsRunMode: " + sRunMode
					+ "\n\n\n");
			// Only execute the test case with run mode equals to yes
			if (sRunMode.equalsIgnoreCase("Yes")) {
				//startTestCase
				logger.warn("\n\n-------------------             " + sTestCaseID
						+ " TestStep begins                -------------------\n\n\n");
				iStartTestStep = ExcelUtils.getRowStartWith(sTestCaseID, Constants.Col_TestCaseID,
						Constants.Sheet_TestSteps);
				logger.info("1st TestStep at row: " + iStartTestStep);
				iLastTestStep = ExcelUtils.getStepsCount(Constants.Sheet_TestSteps, sTestCaseID, iStartTestStep);
				logger.info("Last TestStep at row: " + iLastTestStep);
				
				// if there is any testData for this testCase only calculate TestData total row
				iStartTestData = ExcelUtils.getRowStartWith(sTestCaseID, Constants.Col_TestCaseID,
						Constants.Sheet_TestData);
				iLastTestData = ExcelUtils.getStepsCount(Constants.Sheet_TestData, sTestCaseID, iStartTestData);
				logger.info("1st TestData at row: " + iStartTestData);
				logger.info("Last TestData at row: " + iLastTestData);

				// Execute for sets of different test data
				// if there is testData, loop testData, else run without test data
				// at least run one time
				check_IfGotTestData(iStartTestData, iLastTestData);
				
				for (iCountTestData = iStartTestData; iCountTestData <= iLastTestData; iCountTestData++) {
					
					// every new set of test data the bResult is reset to true
					bResult = true;
					logger.info("\n\nTest start for new TestData"
							+ "\n\n\n");
					
					// Loop for all test steps
					for (iCountTestStep = iStartTestStep; iCountTestStep <= iLastTestStep; iCountTestStep++) {
						
						logger.info("TestStep row no.: " + iCountTestStep);
						
						// get all data from test step columns, need iCountTestStep
						fetch_TestSteps(iCountTestStep);
								
						// get testData from TestData Sheet
						fetch_TestData(sTestData);
						
						// perform keyword actions
						execute_Action();

						// Record test case fail and close browser, need iTestcase
						// check_TestCaseResult(bResult, iTestcase);
						if (bResult == false) {
							ExcelUtils.setCellData(Constants.KEYWORD_FAIL, iTestcase, Constants.Col_CaseResults,
									Constants.Sheet_TestCases);
							logger.warn("\n\n......Test Case Failed for " + sTestCaseID + "......"
									+ "\n\n\n");
							actionKeywords.tryClose("", "", "");
							logger.info("close browser from TestCase loop");
							break;
						}

					}
					// Record test case pass, need iTestcase
					if (bResult == true) {
						ExcelUtils.setCellData(Constants.KEYWORD_PASS, iTestcase, Constants.Col_CaseResults,
								Constants.Sheet_TestCases);
						logger.info("\n\n......All TestStep Completed for " + sTestCaseID + "......\n\n\n");
					}
					// If test case fail, skip for the rest of the data set
					if (bResult == false) {
						break;
					}
					
				} 
				//endTestCase
				logger.warn("\n\n--------------------------------" + sTestCaseID + " TestCase " + " Ended"
						+ "------------------------------------\n\n\n");
			}
		}
		logger.info("\n\n......No more TestCase with 'Yes' RunMode......\n\n"
				+ "---------------------------------------   E-N-D  ---------------------------------------" 
				+ "\n\n\n");
		
	}
	
	/*private static void check_TestCaseResult(Boolean bRsult, int iTcase) {
		
	}*/
	private static void check_IfGotTestData(int iStart, int iLast) {
		int iCount = iLastTestData - iStartTestData;
		logger.info("icount: " + iCount);
		if(iCount < 0) {
			iLastTestData = iStartTestData;
			logger.info("1st TestData at row: " + iStartTestData);
			logger.info("Last TestData at row: " + iLastTestData);
		}
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
	
	private void fetch_TestData(String sTestData) throws Exception {
		try {
			sTestDataItem = sTestData;
			logger.info(" sTestDataItem: " + sTestDataItem);
			for(String sHeader:alCellHeader) {
				
				if(sHeader.equalsIgnoreCase(sTestData)) {
					iCellHeaderIndex = alCellHeader.indexOf(sHeader);
					sTestDataItem = ExcelUtils.getCellData(iCountTestData, iCellHeaderIndex, Constants.Sheet_TestData);
					logger.info(" TestData sheet column: " + sHeader);
					logger.info("Fetching TestData Item: " + sTestDataItem);
					break;
				}
			}
			
		} catch (Exception e) {
			logger.error("DriverScript|fetch_TestData. Exception message: " + e.getMessage());
		}
	}
	
	private void fetch_TestSteps(int iTestStepCount) throws Exception{
		try {
			sObjectLocator = ExcelUtils.getCellData(iCountTestStep, Constants.Col_ObjectLocator,
					Constants.Sheet_TestSteps);
			sActionKeyword = ExcelUtils.getCellData(iCountTestStep, Constants.Col_ActionKeyword,
					Constants.Sheet_TestSteps);
			sTestStepID = ExcelUtils.getCellData(iCountTestStep, Constants.Col_TestStepID,
					Constants.Sheet_TestSteps);

			sTestData = ExcelUtils.getCellData(iCountTestStep, Constants.Col_TestData,
					Constants.Sheet_TestSteps);
			logger.info(" Action: " + sActionKeyword);
			logger.info(" sTestData from TestSteps sheet: " + sTestData);
		} catch (Exception e) {
			logger.error("DriverScript|fetch_TestSteps. Exception message: " + e.getMessage());
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


}
