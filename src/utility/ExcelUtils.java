package utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;

//import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//import config.ActionKeywords;
import config.Constants;
import executionEngine.DriverScript;

public class ExcelUtils {
	private static XSSFWorkbook ExcelWBook;
	private static XSSFSheet ExcelWSheet;
	private static org.apache.poi.ss.usermodel.Cell Cell;
	private static XSSFRow Row;

	public static void setExcelFile(String Path) throws Exception {
		try {
			FileInputStream ExcelFile = new FileInputStream(Path);
			ExcelWBook = new XSSFWorkbook(ExcelFile);
		} catch (Exception e) {
			Log.error("ExcelUtils|setExcelFile. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	public static String getCellData(int RowNum, int ColNum, String SheetName) throws Exception {
		ExcelWSheet = ExcelWBook.getSheet(SheetName);
		try {
			Cell = ExcelWSheet.getRow(RowNum).getCell(ColNum);
			String CellData = Cell.getStringCellValue();
			return CellData;

		} catch (Exception e) {
			Log.warn("No Cell Data is found and return empty cell");
			// DriverScript.bResult = false;
			return "";
		}
	}

	public static int getRowCount(String SheetName) {
		int iNumber = 0;
		try {
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			iNumber = ExcelWSheet.getLastRowNum();
		} catch (Exception e) {
			Log.error("ExcelUtils|getRowCount. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
		return iNumber;
	}

	public static int getRowStartWith(String sTestCaseName, int colNum, String SheetName) throws Exception {
		int iRowNum = 0;
		try {
			int rowCount = ExcelUtils.getRowCount(SheetName);
			for (; iRowNum <= rowCount; iRowNum++) {
				if (ExcelUtils.getCellData(iRowNum, colNum, SheetName).equalsIgnoreCase(sTestCaseName)) {
					break;
				}
			}
		} catch (Exception e) {
			Log.error("ExcelUtils|getRowContains. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
		return iRowNum;
	}

	public static int getStepsCount(String SheetName, String sTestCaseID, int iTestCaseStart) throws Exception {
		int i = iTestCaseStart;
		int rowCount = ExcelUtils.getRowCount(SheetName);
		try {
			for (; i <= rowCount; i++) {
				if (!sTestCaseID.equalsIgnoreCase(ExcelUtils.getCellData(i, Constants.Col_TestCaseID, SheetName))) {
					break;
				}
			}
		} catch (Exception e) {
			Log.error("ExcelUtils|getTestStepsCount. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
		return i - 1;
	}

	public static int getColCount(String SheetName, int rowNum) {
		int colCount = 0;
		try {
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			colCount = ExcelWSheet.getRow(rowNum).getLastCellNum();

		} catch (Exception e) {
			Log.error("ExcelUtils|getColCount. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
		return colCount;
	}

	@SuppressWarnings("static-access")
	public static void setCellData(String sResult, int iRowNum, int iColNum, String sSheetName) throws Exception {
		try {
			ExcelWSheet = ExcelWBook.getSheet(sSheetName);
			Row = ExcelWSheet.getRow(iRowNum);
			Cell = Row.getCell(iColNum, Row.RETURN_BLANK_AS_NULL);
			if (Cell == null) {
				Cell = Row.createCell(iColNum);
				Cell.setCellValue(sResult);
			} else {
				Cell.setCellValue(sResult);
			}
			FileOutputStream fileOut = new FileOutputStream(Constants.Path_TestData);
			ExcelWBook.write(fileOut);
			fileOut.close();
			ExcelWBook = new XSSFWorkbook(new FileInputStream(Constants.Path_TestData));
			Log.info("Test result: " + sResult + " written successfully on: " + sSheetName + " of " + Constants.File_TestData);
		} catch (Exception e) {
			Log.error("ExcelUtils|setCellData. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
	}

}
