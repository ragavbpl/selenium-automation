package SourceCode.com.jda.qa.platform.framework.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.jda.qa.platform.framework.base.MainEngine;
import com.jda.qa.platform.framework.browser.MethodInvoker;
import com.jda.qa.platform.framework.browser.TestBrowser;
import com.jda.qa.platform.framework.locator.LocatorFormatter;
import com.jda.qa.platform.framework.util.ExcelReader;

/**
 * 
 * @Author Vinay Tangella
 * @Description This class is used to call an excel sheet from another excel sheet.
 *
 */

public class ExcelSheetCaller {

	TestBrowser browser;
	HashMap<String, String> oObject_Details = new HashMap<String, String>();
	String sForm_Props;
	String sExcel_FileName;
	String sSheet_Name;
	HashMap<String, String> paramsMap = new HashMap<String, String>();
	HashMap<String, String> splParamsMap = new HashMap<String, String>();
	int splParamIndex = 0;
	public MethodInvoker caller;
	
	/**
	 * @Author Vinay Tangella
	 * @Description This is the default constructor of the class 
	 * @Param0 browser
	 * @Param1 sExcel_FileName
	 * @Param2 sSheet_Name
	 */
	public ExcelSheetCaller(TestBrowser browser, String sExcel_FileName,
			String sSheet_Name) {

		this.caller= com.jda.qa.platform.framework.base.MainEngine.caller;
		this.browser = browser;
		this.sExcel_FileName = sExcel_FileName+".xlsx";
		this.sSheet_Name = sSheet_Name;

	}

	/**
	 * @Author Vinay Tangella
	 * @Description This method is used to call an excel sheet from another sheet
	 * @param params
	 */
	public void callSheet(String params) {
		// To Handle the last {
		
		boolean isLast = false;
		
		int tempIndex = params.indexOf("{");
		String tempStr ="";
		
		if(tempIndex!=-1){
		tempStr = params.substring(tempIndex+1);
		
		if(!tempStr.contains("{")&&tempIndex==0){
			isLast= true;
			params = params.replace("}", "");
			params = params.replace("{", "");
		}
		
		}
		
		if (params.contains("{") && !isLast) {

			ArrayList<Integer> startParanthesisList = new ArrayList<Integer>();
			ArrayList<Integer> endParanthesisList = new ArrayList<Integer>();

			String startParamGiver = params;
			String endParamGiver = params;
			String replacedParams = params;

			while (startParamGiver.contains("{")) {
				int position = startParamGiver.indexOf("{");

				startParanthesisList.add(position);
				
				startParamGiver = replaceStringChar(startParamGiver,"{", "|");

			}

			while (endParamGiver.contains("}")) {
				int position = endParamGiver.indexOf("}");

				endParanthesisList.add(position);
				
				endParamGiver = replaceStringChar(startParamGiver,"}", "|");

				//endParamGiver = endParamGiver.substring(position);
			}
			
			// Store Spl Params
			
			
			for(int index = startParanthesisList.size()-1;index>=0;index--){
				splParamsMap.put("SPLPPARAM"+splParamIndex, params.substring(
						startParanthesisList.get(index),
						endParanthesisList.get(endParanthesisList.size()-1
								- index)+1));
				
				
				int subStringlength = params.substring(
						startParanthesisList.get(index),
						endParanthesisList.get(endParanthesisList.size()-1
								- index)+1).length();
				

				for(int replaceIndex=startParanthesisList.size()-1;replaceIndex>=0;replaceIndex--){
					String startPart = replacedParams.substring(0,startParanthesisList.get(replaceIndex));
					String endPart = replacedParams.substring(endParanthesisList.get(endParanthesisList.size()-replaceIndex-1)+1);
					replacedParams = startPart+"SPLPPARAM"+splParamIndex+endPart;
				}
				
				// update the endParenthesisList
				
				System.out.println("Santosh ");
				

				
				for(int endIndex=splParamIndex;endIndex<endParanthesisList.size();endIndex++){
					String paramString = "SPLPPARAM"+splParamIndex;
					int intialVal = endParanthesisList.get(endIndex);
					int newVal = intialVal-(subStringlength+paramString.length());
					endParanthesisList.remove(endIndex);
					endParanthesisList.add(endIndex, newVal);
				}
				
				splParamIndex++;
			}
			
			
			params = replacedParams;

		} 

		String[] paramVals = params.split(",");
		
		for (int paramIndex = 0; paramIndex < paramVals.length; paramIndex++) {
			
			System.out.println(paramVals[paramIndex]);
			if (!paramVals[paramIndex].contains("SYS_PROPS@"))
			{
			this.paramsMap.put("PARAM" + paramIndex, paramVals[paramIndex]
					.toString().replaceAll("\\.", ","));
			}
			else
			{
				this.paramsMap.put("PARAM" + paramIndex, paramVals[paramIndex]
						.toString());	
			}

		}
		
		
		int tempSplParamIndex = splParamIndex;
		
		while(tempSplParamIndex>0){
			
			Set<String> keys  = paramsMap.keySet();
			
			for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				
				String value = paramsMap.get(key);
				
				if(value.contains("SPLPPARAM")){
					int index = --tempSplParamIndex;
					
					value  = value.replaceAll("SPLPPARAM"+index, splParamsMap.get("SPLPPARAM"+index));
				}
				
				paramsMap.put(key, value);
			}
			
			
		}


		String sTestDir = System.getProperty("user.dir") + "\\scenarios\\";

		ExcelReader oEr = new ExcelReader(sTestDir + sExcel_FileName);

		int iTest_Steps = oEr.getRowCount(sSheet_Name);

		//
		for (int iTestStep_Counter = 2; iTestStep_Counter <= iTest_Steps; iTestStep_Counter++) {

			// get the keyword of the stepsKeyWord = oEr.getCellData(sSheet_Name, "KeyWord",
			String sKeyWord = oEr.getCellData(sSheet_Name, "KeyWord",
					iTestStep_Counter);

			// get the option1 of the step
			String sOption1 = oEr.getCellData(sSheet_Name, "Option1",
					iTestStep_Counter);

			// get the option2 of the step
			String sOption2 = oEr.getCellData(sSheet_Name, "Option2",
					iTestStep_Counter);

			if (sOption2.length() > 0 && sOption2.startsWith("PARAM")) {
				
				String actOption = "";
				if (sKeyWord.equalsIgnoreCase("callexcelsheet")) {
					String[] vals = sOption2.split(",");

					for (int valIndex = 0; valIndex < vals.length; valIndex++) {
						actOption = actOption
								+ paramsMap.get(vals[valIndex]);
						
						if(!actOption.contains("{")){
							actOption = actOption.replaceAll(",",
										"\\.") + ",";
					}

						
					}

					actOption = actOption + ",";
					actOption = actOption.replaceAll(",,", "");

					sOption2 = actOption;

				} else {
					sOption2 = paramsMap.get(sOption2);
					
					if ((sOption2.contains("{"))
							&& (sOption2.indexOf("{") == sOption2
									.lastIndexOf("{"))) {
						sOption2 = sOption2.replace("{", "");
						sOption2 = sOption2.replace("}", "");
				}
			}
			}

			// log the respective info
			com.jda.qa.platform.framework.base.MainEngine.log.info("KeyWord : " + sKeyWord);
			com.jda.qa.platform.framework.base.MainEngine.log.info(" -- Option1 : " + sOption1);
			MainEngine.log.info(" -- Option2 : " + sOption2);

			// format the locator of Option1
			if (sOption2.trim().length() == 0) {
				sOption1 = LocatorFormatter.formatLocator(sOption1);
				caller.runMethod(sKeyWord,  sOption1);
			} else {
				sOption1 = LocatorFormatter.formatLocator(sOption1);
				sOption2 = LocatorFormatter.formatLocator(sOption2);
				caller.runMethod(sKeyWord, sOption1, sOption2);
			}
		}

	}
	
	public String replaceStringChar(String input, String replacable,String replacer){
		
		int position = input.indexOf(replacable);
		
		String beforeString = input.substring(0,position);
		String afterString = input.substring(position+1);
		
		
		
		return beforeString+replacer+afterString;
		
	}
}
