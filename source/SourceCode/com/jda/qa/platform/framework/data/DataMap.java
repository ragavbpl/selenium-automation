package SourceCode.com.jda.qa.platform.framework.data;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import com.jda.qa.platform.data.database.DBConnection;
import com.jda.qa.platform.framework.base.MainEngine;

public class DataMap {

	ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
	DBConnection dbConn;

	public DataMap(ArrayList<ArrayList<String>> data) {
		this.data = data;
	}

	public int getDataMapSize() {
		ArrayList<String> innerData = data.get(0);
		return innerData.size();
	}

	public ArrayList<String> getColumnNames() {

		ArrayList<String> colNamesAL = new ArrayList<String>();

		try {

			if (data.size() > 0) {

				colNamesAL = data.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return colNamesAL;
	}

	public int getColumnIndex(String colName) {

		return getColumnNames().indexOf(colName);
	}

	public ArrayList<String> getColData(String colName) {

		return getColData(colName, false);
	}

	public ArrayList<String> getColData(String colName, boolean noNull) {

		ArrayList<String> colData = new ArrayList<String>();

		try {

			int colIndex = getColumnIndex(colName);

			ListIterator<ArrayList<String>> Itr = data.listIterator();

			while (Itr.hasNext()) {
				ArrayList<String> currentRowAL = (ArrayList<String>) Itr.next();

				String data = currentRowAL.get(colIndex);

				if (noNull && data.equalsIgnoreCase("NULL")) {
					colData.add("");
				} else {
					colData.add(currentRowAL.get(colIndex));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return colData;
	}

	public ArrayList<ArrayList<String>> formatData(DBConnection dbConn)
			throws Exception {

		ArrayList<String> colNames = getColumnNames();

		System.out.println(colNames);

		for (String colName : colNames) {

			int index = colName.indexOf("_");

			String tableName = colName.substring(0, index);
			String columnName = colName.substring(index + 1, colName.length());

			ResultSet metaDataRS = dbConn
					.executeQuery("SELECT LOGICAL_DATA_TYPE_NAME FROM MD_COLUMN_INFO WHERE TABLE_NAME = '"
							+ tableName
							+ "' AND COLUMN_NAME = '"
							+ columnName
							+ "'");

			ArrayList<HashMap<String, String>> resultListArray = dbConn
					.getResultsasArrayList(metaDataRS);

			ArrayList<ArrayList<String>> metaDataResultMap = dbConn
					.getDataMap(resultListArray);

			DataMap metaDataDataMap = new DataMap(metaDataResultMap);

			String colDataType = metaDataDataMap.getColData(
					"LOGICAL_DATA_TYPE_NAME").get(1);

			int colIndex = getColumnIndex(colName);

			for (int rowIndex = 1; rowIndex < data.size(); rowIndex++) {

				switch (colDataType) {

				case "DATE":

					String dateValue = data.get(rowIndex).get(colIndex);

					if (dateValue != "NULL") {

						DateFormat formatter = new SimpleDateFormat(
								"MM/dd/yyyy");
						Date date = (Date) formatter.parse(data.get(rowIndex)
								.get(colIndex));
						formatter = new SimpleDateFormat(MainEngine.dateFormat);

						dateValue = formatter.format(date);
					}

					if (dateValue.equalsIgnoreCase(new SimpleDateFormat(
							"M/d/yy").parse("1/1/70").toString())
							|| data.get(rowIndex).get(colIndex)
									.equalsIgnoreCase("NULL")) {
						data.get(rowIndex).set(colIndex, "");
					} else {
						data.get(rowIndex).set(colIndex, dateValue);
					}

					break;

				case "DATETIME":

					String dateTimeValue = data.get(rowIndex).get(colIndex);
					Date date = null;

					if (dateTimeValue != "NULL") {

						DateFormat formatter = new SimpleDateFormat(
								"MM/dd/yyyy hh:mm a");
						date = (Date) formatter.parse(data.get(rowIndex).get(
								colIndex));
						formatter = new SimpleDateFormat(
								MainEngine.dateTimeFormat);

						dateTimeValue = formatter.format(date);
					}

					if (dateTimeValue.startsWith(new SimpleDateFormat("M/d/yy")
							.format(new Date("1/1/70")).toString())
							|| data.get(rowIndex).get(colIndex)
									.equalsIgnoreCase("NULL")) {
						data.get(rowIndex).set(colIndex, "");
					} else {
						data.get(rowIndex).set(
								colIndex,
								new SimpleDateFormat(MainEngine.dateTimeFormat)
										.format(date));
					}

					break;

				case "BOOLEAN":
					if (data.get(rowIndex).get(colIndex).equalsIgnoreCase("0")
							|| data.get(rowIndex).get(colIndex)
									.equalsIgnoreCase("NULL")) {
						data.get(rowIndex).set(colIndex, "FALSE");
					} else if (data.get(rowIndex).get(colIndex)
							.equalsIgnoreCase("1")) {
						data.get(rowIndex).set(colIndex, "TRUE");
					}

					break;

				case "DURATION":

					Long durData = Long.valueOf(data.get(rowIndex)
							.get(colIndex));

					int days = (int) (durData / 1440);

					int value = (int) (durData % 1440);

					int hours = value / 60;

					int minutes = (int) (value % 60);

					data.get(rowIndex).set(colIndex,
							days + "D" + hours + "H" + minutes + "M");

					break;

				case "TIME_OF_DAY":

					String amOrPm = "AM";

					int timeVal = Integer.valueOf(data.get(rowIndex).get(
							colIndex));

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

					data.get(rowIndex).set(colIndex,
							hourStr + ":" + minStr + " " + amOrPm);

					break;

				case "URL":

					if (data.get(rowIndex).get(colIndex)
							.equalsIgnoreCase("NULL")) {
						data.get(rowIndex).set(colIndex, "");
					} else {
						data.get(rowIndex).set(colIndex, "Details");
					}

					break;

				case "DECIMAL":
					DecimalFormat decfrmt = new DecimalFormat(
							MainEngine.decimalFormat);

					data.get(rowIndex).set(
							colIndex,
							decfrmt.format(Double.parseDouble(data
									.get(rowIndex).get(colIndex).toString())));
					break;

				case "INTEGER":
					DecimalFormat intfrmt = new DecimalFormat(
							MainEngine.integerFormat);

					data.get(rowIndex).set(
							colIndex,
							intfrmt.format(Double.parseDouble(data
									.get(rowIndex).get(colIndex).toString())));
					break;

				default:
					if (data.get(rowIndex).get(colIndex)
							.equalsIgnoreCase("NULL")) {
						data.get(rowIndex).set(colIndex, "");
					}

					break;
				}

			}

		}

		return data;

	}

	public ArrayList<ArrayList<String>> arrangeColumnOrder(
			ArrayList<ArrayList<String>> inputData, String columnOrder)
			throws Exception {

		ArrayList<ArrayList<String>> outputData = new ArrayList<ArrayList<String>>();

		// Convert the array to column string to array list

		String[] colNameArray = columnOrder.split(",");

		List<String> colNamesList = Arrays.asList(colNameArray);
		
		ArrayList<String> colNamesAL = new ArrayList<String>(colNamesList);

		ArrayList<String> colNamesInputDataAL = inputData.get(0);

		// Convert the input data into the individual array list

		outputData.add(colNamesAL);

		for (int dataRows = 1; dataRows < inputData.size(); dataRows++) {

			ArrayList<String> dataRow = inputData.get(dataRows);

			ArrayList<String> outputDataRow = new ArrayList<String>();

			for (int colNum = 0; colNum < colNamesAL.size(); colNum++) {

				String content = dataRow.get(colNamesInputDataAL
						.indexOf(colNamesAL.get(colNum)));

				outputDataRow.add(content);
			}

			outputData.add(outputDataRow);
		}

		// Regenerate the array list

		return outputData;
	}

}
