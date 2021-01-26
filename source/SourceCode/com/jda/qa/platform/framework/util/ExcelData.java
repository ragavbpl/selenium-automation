package SourceCode.com.jda.qa.platform.framework.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;


import com.jda.qa.platform.framework.base.MainEngine;
import com.jda.qa.platform.framework.locator.LocatorFormatter;
import com.jda.qa.platform.framework.util.ExcelReader;
import com.jda.qa.platform.framework.util.FileUtils;

public class ExcelData {

	String fileName;
	LinkedHashMap<String, HashMap<String, String>> data = new LinkedHashMap<String, HashMap<String, String>>();
	LinkedHashMap<String, HashMap<String, String>> extnData = new LinkedHashMap<String, HashMap<String, String>>();
	LinkedHashMap<String, HashMap<String, String>> finalData = new LinkedHashMap<String, HashMap<String, String>>();
	String sheetName;
	ArrayList<String> colNames = new ArrayList<String>();

	/**
	 * Sample Class for Excel Data
	 * 
	 * @param fileName
	 * @param sheetName
	 */

	public ExcelData(String fileName, String sheetName) {

		this.fileName = fileName;
		this.sheetName = sheetName;

	}

	public HashMap<String, HashMap<String, String>> loadExcelData() {

		int colCount = 0;
		int rowCount = 0;
		HashMap<String, String> rowData = new HashMap<String, String>();

		if (new File(FileUtils.getRootDir() + "\\data\\" + fileName).exists()) {

			ExcelReader dataReader = new ExcelReader(FileUtils.getRootDir()
					+ "\\data\\" + fileName);

			if (dataReader.isSheetExist(sheetName)) {

				colCount = dataReader.getColumnCount(sheetName);
				rowCount = dataReader.getRowCount(sheetName);

				// Get all the Column names

				for (int colCountIndex = 0; colCountIndex < colCount; colCountIndex++) {
					colNames.add(dataReader.getCellData(sheetName,
							colCountIndex, 1));
				}

				for (int rowIndex = 2; rowIndex <= rowCount; rowIndex++) {

					String pkColValue = dataReader.getCellData(sheetName,
							colNames.get(0), rowIndex);

					rowData = new HashMap<String, String>();

					for (int colCountIndex = 1; colCountIndex <= colCount; colCountIndex++) {
						String colName = colNames.get(colCountIndex - 1);
						String colContent = dataReader.getCellData(sheetName,
								colName, rowIndex);
						
						rowData.put(colName, colContent);
					}

					data.put(pkColValue, rowData);
				}
			}

		}

		fileName = fileName.replace(".xlsx", "Extension.xlsx");

		if (new File(FileUtils.getRootDir() + "\\data\\" + fileName).exists()) {

			ArrayList<String> extnColNames = new ArrayList<String>();

			ExcelReader dataReader = new ExcelReader(FileUtils.getRootDir()
					+ "\\data\\" + fileName);

			if (dataReader.isSheetExist(sheetName)) {

				colCount = dataReader.getColumnCount(sheetName);
				rowCount = dataReader.getRowCount(sheetName);

				// Get all the Column names

				for (int colCountIndex = 0; colCountIndex < colCount; colCountIndex++) {

					extnColNames.add(dataReader.getCellData(sheetName,
							colCountIndex, 1));
				}

				colNames.addAll(extnColNames);

				for (int rowIndex = 2; rowIndex <= rowCount; rowIndex++) {

					String pkColValue = dataReader.getCellData(sheetName,
							extnColNames.get(0), rowIndex);

					rowData = new HashMap<String, String>();

					for (int colCountIndex = 1; colCountIndex <= colCount; colCountIndex++) {
						String colName = extnColNames.get(colCountIndex - 1);
						String colContent = dataReader.getCellData(sheetName,
								colName, rowIndex);

						rowData.put(colName, colContent);
					}

					extnData.put(pkColValue, rowData);
				}
			}

		}

		// merge both the data.

		Set<String> dataKeys = data.keySet();

		for (Iterator<String> iterator = dataKeys.iterator(); iterator
				.hasNext();) {
			String key = (String) iterator.next();

			HashMap<String, String> dataRowData = data.get(key);

			HashMap<String, String> extnDataRowData = extnData.get(key);

			if (extnDataRowData != null) {
				dataRowData.putAll(extnDataRowData);
				finalData.put(key, dataRowData);
				extnData.remove(key);
			}

			finalData.put(key, dataRowData);

		}

		dataKeys = extnData.keySet();

		for (Iterator<String> iterator = dataKeys.iterator(); iterator
				.hasNext();) {
			String key = (String) iterator.next();

			HashMap<String, String> extnDataRowData = extnData.get(key);

			if (extnDataRowData != null) {
				finalData.put(key, extnDataRowData);
			}

		}

		return finalData;

	}

	public HashMap<String, String> getExcelData(String pkValue) {

		loadExcelData();

		HashMap<String, String> finalParsedData = finalData.get(pkValue);

		Set<String> columns = new HashSet<String>();

		columns = new HashSet<String>(colNames);

		// work on the locale specific data

		Iterator<String> columnsItr = columns.iterator();

		ArrayList<String> locSpecificCloumns = new ArrayList<String>();

		Set<String> locSpecificTransCloumns = new HashSet<String>();

		while (columnsItr.hasNext()) {

			String colName = columnsItr.next();

			if (colName.contains("_TRANS_")) {
				locSpecificCloumns.add(colName);

				String langIrrespColName = colName.substring(0,
						colName.indexOf("_TRANS_"));

				locSpecificTransCloumns.add(langIrrespColName);
			}

		}

		HashMap<String, String> transFinalData = new HashMap<String, String>();

		for (Iterator<String> iterator = locSpecificCloumns.iterator(); iterator
				.hasNext();) {
			String transColName = iterator.next();

			// check for the column existance

			transFinalData.put(transColName, finalParsedData.get(transColName));

			finalParsedData.remove(transColName);
		}

		// Put the trans specific data

		for (Iterator<String> iterator = locSpecificTransCloumns.iterator(); iterator
				.hasNext();) {
			String colName = iterator.next();

			String colContent = transFinalData.get(colName + "_TRANS_"
					+ MainEngine.getLocale().toUpperCase());

			if (colContent != null) {
				finalParsedData.put(colName, colContent);
			}

		}

		return finalParsedData;

	}

	public static void main(String[] args) {

		ExcelData excelData = new ExcelData("Contents.xlsx", "Enterprise");

		System.out.println(excelData.getExcelData("ENT2"));

	}

}
