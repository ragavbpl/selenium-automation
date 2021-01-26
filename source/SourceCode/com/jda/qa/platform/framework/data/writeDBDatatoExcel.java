package SourceCode.com.jda.qa.platform.framework.data;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.jda.qa.platform.data.database.DBConnection;
import com.jda.qa.platform.framework.data.DataMap;
import com.jda.qa.platform.framework.util.ExcelReader;

public class writeDBDatatoExcel {

	public static void main(String[] args) throws Exception {

		DBConnection dbConn = new DBConnection("in2npdlnxdb14", "1521",
				"WWF1212A", "WWFMGR_DP3", "WWFMGR_DP3");

		ResultSet rs = dbConn
				.executeQuery("SELECT BOM.BOM_BOL AS BOM_BOM_BOL,TO_CHAR(BOM.BOM_DATE,'MM/DD/YYYY') AS BOM_BOM_DATE,TO_CHAR(BOM.BOM_DATETIME,'MM/DD/YYYY HH:MI AM') AS BOM_BOM_DATETIME,BOM.BOM_DECIMAL AS BOM_BOM_DECIMAL, BOM.BOM_DUR AS BOM_BOM_DUR, BOM.BOM_EMAIL AS BOM_BOM_EMAIL, BOM.BOM_INT AS BOM_BOM_INT, BOM.BOM_OPNNUM AS BOM_BOM_OPNNUM, BOM.BOM_OPNTXT AS BOM_BOM_OPNTXT, BOM.BOM_TEXT AS BOM_BOM_TEXT, BOM.BOM_TOD AS BOM_BOM_TOD, BOM.BOM_UN AS BOM_BOM_UN, BOM.BOM_URL AS BOM_BOM_URL, BOM.BOM_UT AS BOM_BOM_UT FROM SAMPLE_DP3.BOM ORDER BY BOM.ITEM,BOM.SUBORD,BOM.LOC,BOM.BOMNUM,BOM.EFF,BOM.OFFSET");

		ArrayList<HashMap<String, String>> resultListArray = dbConn
				.getResultsasArrayList(rs);

		ArrayList<ArrayList<String>> resultMap = dbConn
				.getDataMap(resultListArray);

		com.jda.qa.platform.framework.data.DataMap dataMap = new DataMap(resultMap);

		ArrayList<ArrayList<String>> data = dataMap.formatData(dbConn);

		String columnOrder = "BOM_BOM_BOL,BOM_BOM_DATE,BOM_BOM_DATETIME,BOM_BOM_DECIMAL,BOM_BOM_DUR,BOM_BOM_EMAIL,BOM_BOM_INT,BOM_BOM_OPNNUM,BOM_BOM_OPNTXT,BOM_BOM_TEXT,BOM_BOM_TOD,BOM_BOM_UN,BOM_BOM_URL,BOM_BOM_UT";

		ArrayList<ArrayList<String>> formattedData = dataMap
				.arrangeColumnOrder(data, columnOrder);

		//System.out.println(formattedData);
		
		writeDataToSheet(formattedData,"DB_DATA");

	}

	public static void writeDataToSheet(ArrayList<ArrayList<String>> data,
			String sheetName) {

		ExcelReader reader = new ExcelReader(
				"D:\\JDA\\Selenium\\data\\fevalidation\\Sample_Results.xlsx");

		/*boolean sheetExists = reader.isSheetExist(sheetName);

		if (sheetExists) {
			reader.removeSheet(sheetName);
		}*/

		//reader.addSheet(sheetName);

		// Add Columns

		ArrayList<String> columns = data.get(0);

		for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
			String columnName = (String) iterator.next();

			reader.addColumn(sheetName, columnName);

		}

		for (int index = 1; index < data.size(); index++) {
			ArrayList<String> rowData = data.get(index);
			int colNum = 0;

			for (Iterator iterator = rowData.iterator(); iterator.hasNext();) {
				String colContent = (String) iterator.next();

				reader.setCellData(sheetName, columns.get(colNum), index+1,
						colContent);
				
				colNum++;

			}

		}

		// Add Data

	}

}