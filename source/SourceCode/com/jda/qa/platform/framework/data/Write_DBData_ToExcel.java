package SourceCode.com.jda.qa.platform.framework.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.jda.qa.platform.data.database.DBConnection;
import com.jda.qa.platform.framework.base.MainEngine;
import com.jda.qa.platform.framework.util.ExcelReader;

/**
 * 
 * @Author Vinay Tangella & Santosh Medchalam
 * @Description This class is used to fetch data from the DB and write it to an Excel Sheet
 *
 */
public class Write_DBData_ToExcel{

	/**
	 * 
	 * @Author Vinay Tangella & Santosh Medchalam
	 * @Description This method is used to fetch data from the DB and write it to an Excel Sheet
	 * @Param0 Folder Name of the Excel File
	 * @Param1 File Name to which the data to be added
	 * @Param2 ArrayList of Column Header names
	 * @Param3 Query to fetch the data from the DB
	 *
	 */
public void writeDBDatatoExcel(String FolderName, String FileName, ArrayList oColumnHeaders, String Query) {

		String sQuery=Query, sPartialQuery_LogicalDT = "", sStamp, sRow, sFirstColumn = "",sTempColumn_Name,sTempColumn_Name_1,sQuery_LogicalDT=null;
		
		String sFolderName=FolderName;
		
		String sFileName = FileName;
		
		ArrayList<String> oColHeaders =  oColumnHeaders; 
		
		HashMap<String, String> colDataType;

		SimpleDateFormat oDateFormat;
		
		Date oDate;

		Connection oConn;

		ResultSet oRes_Set = null, oData_Types = null;

		ArrayList<HashMap<String, String>> oResult = new ArrayList<HashMap<String, String>>();

		ArrayList<String> oPlainColHeaders = new ArrayList<String>();

		HashMap<String, HashMap<String, String>> oDB_Data = new HashMap<String, HashMap<String, String>>();

		DBConnection oDB;

		ExcelReader oEr;

		oEr = new ExcelReader(System.getProperty("user.dir")
				+"\\" + sFolderName + "\\" + sFileName + "_data.xlsx");

		//This section of code removes the '*' symbol from the column names for the primary keys
		for (int i = 0; i < oColHeaders.size(); i++) {
			if (i == 0) {
				sTempColumn_Name = oColHeaders.get(i).toString();
				
					if (sTempColumn_Name.contains("*")) {
						sTempColumn_Name = sTempColumn_Name.substring(1);
						oPlainColHeaders.add(sTempColumn_Name);
					}
				
					sPartialQuery_LogicalDT = "'" + sTempColumn_Name + "'";				
					
				} else {
					sTempColumn_Name_1 = oColHeaders.get(i).toString();
					
					if (sTempColumn_Name_1.contains("*")) {
						sTempColumn_Name_1 = sTempColumn_Name_1.substring(1);
						}
					if (sTempColumn_Name_1.equalsIgnoreCase("Description")) {
						sTempColumn_Name_1 = "Descr";
						}
				
				oPlainColHeaders.add(sTempColumn_Name_1);
				
				sPartialQuery_LogicalDT = sPartialQuery_LogicalDT + "," + "'"
						+ sTempColumn_Name_1 + "'";

			}
		}
	
		//this query is to get the logical data types of the columns of FE to format the data
		sQuery_LogicalDT = "SELECT COLUMN_NAME, LOGICAL_DATA_TYPE_NAME FROM "
				+ MainEngine.wwfDBUser + "."
				+ "MD_COLUMN_INFO WHERE TABLE_NAME = '" + sFileName
				+ "' AND COLUMN_NAME IN (" + sPartialQuery_LogicalDT
				+ ") ORDER BY COLUMN_NAME";
		
		oDB = new DBConnection("in2npdlnxdb14", "1521", "tst1212a", "System",
				"manager");

		try {
			oConn = oDB.getConnection();
			oRes_Set = oDB.executeQuery(sQuery);

			oData_Types = oDB.executeQuery(sQuery_LogicalDT);

			ArrayList<HashMap<String, String>> resultListArray = oDB
					.getResultsasArrayList(oData_Types);

			oResult = oDB.getResultsasArrayList(oRes_Set);

			for (int i = 0; i < oResult.size(); i++) {

				HashMap<String, String> oDBRow_Content = new HashMap<String, String>();

				for (int j = 0; j < oPlainColHeaders.size(); j++) {

					sRow = oResult.get(i).get(oPlainColHeaders.get(j));

					String s1 = oPlainColHeaders.get(j);

					String scolDataType = "";

					for (int iCounter = 0; iCounter < resultListArray.size(); iCounter++) {

						colDataType = resultListArray.get(iCounter);

						if (colDataType.get("COLUMN_NAME").equals(s1)) {

							scolDataType = colDataType
									.get("LOGICAL_DATA_TYPE_NAME");
							break;
						}

					}

					switch (scolDataType) {

					case "DATE":

						if (sRow != "NULL") {

							oDateFormat = new SimpleDateFormat(
									"yyyy-M-dd hh:mm:ss.S");// This is the
															// format of
															// the date that is
															// fetched from the
															// DB

							// System.out.println(oResult.get(i).get(
							// oPlainColHeaders.get(j)));

							// System.out.println(oPlainColHeaders.get(j));

							oDate = oDateFormat.parse(oResult.get(i).get(
									oPlainColHeaders.get(j)));

							oDateFormat = new SimpleDateFormat("M/d/yy");
							sStamp = oDateFormat.format(oDate);

							if (sStamp.equals("1/1/70")) {

								sRow = "";
							} else {
								sRow = sStamp;
							}
						}

						break;

					case "DATETIME":

						String dateTimeValue = sRow;
						Date date = null;

						if (dateTimeValue != "NULL") {

							DateFormat formatter = new SimpleDateFormat(
									"MM/dd/yyyy hh:mm a");
							date = (Date) formatter.parse(dateTimeValue);
							formatter = new SimpleDateFormat(
									MainEngine.dateTimeFormat);

							dateTimeValue = formatter.format(date);
						}

						if (dateTimeValue
								.startsWith(new SimpleDateFormat("M/d/yy")
										.format(new Date("1/1/70")).toString())
								|| sRow.equalsIgnoreCase("NULL")) {
							sRow = "";
						} else {
							sRow = new SimpleDateFormat(
									MainEngine.dateTimeFormat).format(date);
						}

						break;

					case "TIME_OF_DAY":

						String amOrPm = "AM";

						int timeVal = Integer.valueOf(sRow);

						int hour = (int) timeVal / 60;

						int min = (int) timeVal % 60;

						if (hour > 12) {
							hour = hour - 12;
							amOrPm = "PM";
						}

						if (hour == 0) {
							hour = 12;
						}

						String hourStr = (hour < 9) ? "0" + hour : hour + "";
						String minStr = (min < 9) ? "0" + min : min + "";

						sRow = hourStr + ":" + minStr + " " + amOrPm;

						break;

					case "DURATION":

						Long durData = Long.valueOf(sRow);

						int days = (int) (durData / 1440);

						int value = (int) (durData % 1440);

						int hours = value / 60;

						int minutes = (int) (value % 60);

						sRow = days + "D" + hours + "H" + minutes + "M";

						break;

					case "INTEGER":
						DecimalFormat intfrmt = new DecimalFormat(
								MainEngine.integerFormat);

						Double.parseDouble(sRow.toString());
						break;

					case "DECIMAL":
						DecimalFormat decfrmt = new DecimalFormat(
								MainEngine.decimalFormat);

						decfrmt.format(Double.parseDouble(sRow.toString()));
						break;

					case "BOOLEAN":
						if (sRow.equalsIgnoreCase("NULL")) {
							sRow = "FALSE";
						} else if (sRow.equalsIgnoreCase("1")) {
							sRow = "TRUE";
						}

						break;

					case "URL":

						if (sRow.equalsIgnoreCase("NULL")) {
							sRow = "";
						}

						break;
					}

					oDBRow_Content.put(oColHeaders.get(j), sRow);
				}
				oDB_Data.put("Row" + i, oDBRow_Content);
				sRow = "";
			}
			
			// Here we add a new sheet with name FE_Data
			oEr.addSheet("DB_Data");

			// First add all the columns header names to the Excel Sheet
			for (int i = 0; i < oColHeaders.size(); i++) {
				oEr.addColumn("DB_Data", oColHeaders.get(i));
			}

			// adding the contents of the FE table under the right column
			// heading
			for (int iRow = 1; iRow <= oDB_Data.size(); iRow++) {
				for (int i = 1; i <= oColHeaders.size(); i++) {

					// (String sheetName, String colName, int rowNum, String
					// data)
					oEr.setCellData(
							"DB_Data",
							oColHeaders.get(i - 1),
							(iRow + 1),
							oDB_Data.get("Row" + (iRow - 1)).get(
									oColHeaders.get(i - 1)));
				}
			}

			System.out.println("completed");

		} catch (Exception e) {
			e.printStackTrace();
		}
}
}
	