package config;

public class Constants {
	public static final String Chrome_Property1 = "webdriver.chrome.driver";
	public static final String Chrome_Property2 = ".\\Browser-Driver\\chromedriver.exe";
	public static final String Firefox_Property1 = "webdriver.gecko.driver";
	public static final String Firefox_Property2 = ".\\Browser-Driver\\geckodriver.exe";
	public static final String IE_Property1 = "webdriver.ie.driver";
	public static final String IE_Property2 = ".\\Browser-Driver\\IEDriverServer.exe";
	
	//public static final String Path_TestData = "C:\\Users\\zy.lam\\eclipse-workspace\\Hybrid Keyword Driven\\src\\dataEngine\\DataEngine.xlsx";
	public static final String Path_TestData = ".\\src\\dataEngine\\DataEngine.xlsx";
	//public static final String Path_TestData = "..\\scr\\dataEngine\\DataEngine.xlsx";
	public static final String File_TestData = "DataEngine.xlsx";
	//public static final String Path_log4j = ".\\src\\xml\\log4j.xml";

	public static final int Col_TestCaseID = 0;
	public static final int Col_TestStepID = 1;
	public static final int Col_ObjectLocator = 3;
	public static final int Col_ActionKeyword = 4;
	public static final int Col_TestData = 5;
	public static final int Col_CaseResults = 3;
	public static final int Col_StepResults = 6;
	
	public static final int Col_RunMode = 2;

	public static final String Sheet_TestCases = "TestCases";
	public static final String Sheet_TestSteps = "TestSteps";
	public static final String Sheet_TestData = "TestData";
	public static final String KEYWORD_FAIL = "FAIL";
	public static final String KEYWORD_PASS = "PASS";

}
