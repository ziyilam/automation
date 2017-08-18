package utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import config.Constants;
import executionEngine.DriverScript;

public class ExcelUtils {
	private static XSSFWorkbook ExcelWBook;
	private static XSSFSheet ExcelWSheet;
	private static org.apache.poi.ss.usermodel.Cell Cell;
	private static XSSFRow Row;

	private static final Logger logger = LogManager.getLogger(ExcelUtils.class.getName());

	public static void setExcelFile(String Path) throws Exception {
		try {
			FileInputStream ExcelFile = new FileInputStream(Path);
			ExcelWBook = new XSSFWorkbook(ExcelFile);
		} catch (Exception e) {
			logger.error("ExcelUtils|setExcelFile. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}

	}

	public static String getCellData(int RowNum, int ColNum, String SheetName) throws Exception {
		
		try {
			String CellData = "Empty cell";
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			Cell = ExcelWSheet.getRow(RowNum).getCell(ColNum);
			if(!Cell.getStringCellValue().isEmpty()) {
				CellData = Cell.getStringCellValue();
			}
			return CellData;
			

		} catch (Exception e) {
			logger.warn("No Cell Data is found and return empty cell");

			return "NULL";
		}
	}

	public static int getRowCount(String SheetName) {
		int iNumber = 0;
		try {
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			iNumber = ExcelWSheet.getLastRowNum();
		} catch (Exception e) {
			logger.error("ExcelUtils|getRowCount. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
		return iNumber;
	}

	public static int getRowStartWith(String sTestCaseName, int colNum, String SheetName) throws Exception {
		int iRowNum = 0;
		try {
			int rowCount = ExcelUtils.getRowCount(SheetName);
			logger.info(" rowCount: " + rowCount + " for Sheet: " + SheetName);
			for (; iRowNum <= rowCount; iRowNum++) {
				//logger.info("cellData: " + ExcelUtils.getCellData(iRowNum, colNum, SheetName));
				if (ExcelUtils.getCellData(iRowNum, colNum, SheetName).equalsIgnoreCase(sTestCaseName)) {
					break;
				}
			}
		} catch (Exception e) {
			logger.error("ExcelUtils|getRowContains. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
		//logger.info("RowNum: " + iRowNum);
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
			logger.error("ExcelUtils|getTestStepsCount. Exception Message - " + e.getMessage());
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
			logger.error("ExcelUtils|getColCount. Exception Message - " + e.getMessage());
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
			logger.info("Test result: " + sResult + "\n\n" + " written successfully on: "
					+ Constants.File_TestData + " - " + sSheetName);
		} catch (Exception e) {
			logger.error("ExcelUtils|setCellData. Exception Message - " + e.getMessage());
			DriverScript.bResult = false;
		}
	}

}
