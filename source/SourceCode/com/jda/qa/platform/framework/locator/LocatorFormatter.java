package SourceCode.com.jda.qa.platform.framework.locator;

import static com.jda.qa.platform.framework.base.MainEngine.getResource;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Properties;

import com.jda.qa.platform.framework.base.MainEngine;
import com.jda.qa.platform.framework.util.ExcelData;
import com.jda.qa.platform.framework.util.FileUtils;

/**
 * 
 * This Class is used to format the locator for supporting the object
 * repositories and resource bundles
 * 
 * @author Santosh Kumar Medchalam and Vinay Kumar Tangella
 *
 */

public class LocatorFormatter {

	/**
	 * 
	 * This method will format the locator using the object repositories and
	 * resource bundles
	 * 
	 * @param locatorString
	 *            The locator which needs to be formatted
	 * @return The formatted locator
	 */

	public static String formatLocator(String locatorString) {

		// Get the Locator resource bundle object repository

		if (locatorString.contains(":::")) {

			String[] vals = locatorString.split(":::");

			String repositoryBundleName = vals[0];

			String locatorKey = vals[1];

			if (!MainEngine.loadedLocators.contains(repositoryBundleName)) {

				File repositoryFile = new File(FileUtils.getFileLocation(
						"locators", repositoryBundleName + ".properties"));

				File repositoryExtnFile = new File(FileUtils.getFileLocation(
						"locators", repositoryBundleName
								+ ".extension.properties"));

				MainEngine.loadedLocators.add(repositoryBundleName);

				if (repositoryFile.exists()) {
					Properties resProperties = new Properties();
					try {
						resProperties.load(new FileReader(repositoryFile));
					} catch (Exception e) {
						System.out
								.println("Failed to load the Locator Repository"
										+ repositoryBundleName + ".properties");
					}

					MainEngine.locatorRepository.put(repositoryBundleName,
							resProperties);

				}

				if (repositoryExtnFile.exists()) {
					Properties resProperties = new Properties();
					try {
						resProperties.load(new FileReader(repositoryExtnFile));
					} catch (Exception e) {
						System.out
								.println("Failed to load the Locator Repository"
										+ repositoryBundleName
										+ ".extension.properties");
					}

					MainEngine.locatorRepository.put(repositoryBundleName,
							resProperties);

				}

			}

			if (MainEngine.locatorRepository.get(repositoryBundleName)
					.getProperty(locatorKey) != null) {
				locatorString = MainEngine.locatorRepository.get(
						repositoryBundleName).getProperty(locatorKey);

			}
		}

		// Replace the Resource Values

		while (locatorString.contains("Resource(")) {

			int beginIndex = locatorString.indexOf("Resource(");
			int endIndex = locatorString.substring(beginIndex,
					locatorString.length()).indexOf(")");

			String resourceName = locatorString.substring(beginIndex + 9,
					endIndex + beginIndex);

			String[] resVals = resourceName.split("@");

			String resource = getResource().get(new String(resVals[0]))
					.getString(resVals[1]);

			// Update the logic here later

			if (resource.trim().equals("")) {
				resource = resVals[1];
			}

			locatorString = locatorString.replace("Resource(" + resourceName
					+ ")", resource);

		}
		
		// Replace all the dynamic values
		
		while (locatorString.contains("DynamicValue(")) {

			int beginIndex = locatorString.indexOf("DynamicValue(");
			int endIndex = locatorString.substring(beginIndex,
					locatorString.length()).indexOf(")");

			String variableName = locatorString.substring(beginIndex
					+ new String("DynamicValue(").length(), endIndex
					+ beginIndex);

			String variableValue = MainEngine.varManager
					.getVariableValue(variableName);

			locatorString = locatorString.replace("DynamicValue("
					+ variableName + ")", variableValue);

		}

		locatorString = updateDBValues(locatorString);

		return locatorString;

	}

	/**
	 * 
	 * This method will replace the text like {0},{1} etc using the values in
	 * the subsVals array
	 * 
	 * @param locatorString
	 *            The Locator which contains the replaceable text.
	 * @param subsVals
	 *            The values which needs to be substituted in the locatorString.
	 * @return The Formatted locator after replacing the placeholder.
	 */

	public static String formatLocator(String locatorString, String... subsVals) {

		locatorString = formatLocator(locatorString);

		String sValue;

		String[] subValsCopy = subsVals;

		if (subValsCopy.length <= 1 && !subsVals[0].contains(",")) {
			sValue = subValsCopy[0];
		} else {

			for (int i = 0; i < subValsCopy.length; i++) {

				String[] dataVals = subValsCopy[i].toString().split(",");

				ExcelData data = new ExcelData("Contents.xlsx", dataVals[0]);

				HashMap<String, String> objectDetails = data
						.getExcelData(dataVals[1]);

				subsVals[i] = objectDetails.get(dataVals[2]);
			}
		}

		if (locatorString.contains("{") && locatorString.contains("}")
				&& subsVals.length > 0) {

			for (int index = 0; index < subsVals.length; index++) {
				locatorString = locatorString.replace("{" + index + "}",
						subsVals[index]);

			}

		}

		// Update this part of the code to fix for IE browser
		// locatorString = locatorString.replace("  ", " ");

		locatorString = updateDBValues(locatorString);

		return locatorString;

	}

	/**
	 * 
	 * This method will be return the resource bundle value for the specified
	 * resourceBundleName and keyName
	 * 
	 * @param resourceBundleName_keyName
	 *            The input in the form of resourceBundleName@keyName
	 * @return the resource bundle value for the specified resourceBundleName
	 *         and keyName
	 */
	public static String formatResourceValue(String resourceBundleName_keyName) {

		String[] values = resourceBundleName_keyName.split("@");

		if (values.length == 2) {
			return resourceBundleValue(values[0], values[1]);
		} else {
			return resourceBundleName_keyName;
		}

	}

	/**
	 * 
	 * This method will be return the resource bundle value for the specified
	 * resourceBundleName and keyName
	 * 
	 * @param resourceBundleName
	 *            The name of the resource bundle
	 * @param keyName
	 *            the name of the key in the resource bundle
	 * @return the resource bundle value for the specified resourceBundleName
	 *         and keyName
	 */

	public static String resourceBundleValue(String resourceBundleName,
			String keyName) {

		String translatedResource = getResource().get(resourceBundleName)
				.getString(keyName);

		return translatedResource;

	}

	/**
	 * 
	 * This method will update the values of the Database schema names in the
	 * locator if there are any
	 * 
	 * @param locator
	 *            The locator which contains the database schema names
	 * @return The formatted locator with the updated database schema names
	 */

	public static String updateDBValues(String locator) {
		
		if (locator.contains("#SAMPLE_SCHEMA#"))
		{
			locator = locator.replaceAll("#SAMPLE_SCHEMA#", MainEngine.sampleDBUser);			
		}
		else if (locator.contains("#WWF_SCHEMA#"))
		{
			locator = locator.replaceAll("#WWF_SCHEMA#", MainEngine.wwfDBUser);	
		}
		else if (locator.contains("#DB_URL#"))
		{
			locator = locator.replaceAll("#DB_URL#", MainEngine.dbURL);	
		}
		else if(locator.contains("#localhost#"))
		{
			locator = locator.replaceAll("#localhost#",MainEngine.browser.getURL());	
		}
		else if(locator.contains("#SETUP_DIR#"))
		{
			locator = locator.replaceAll("#SETUP_DIR#",System.getProperty("user.dir").replace("\\", "\\\\"));			
			//locator = locator.replaceAll("#SETUP_DIR#",new File(System.getProperty("user.dir")).getAbsolutePath());
			
			String directory = FileUtils.getRootDir().substring(0,2);
			
			if (MainEngine.browser.getBrowserType().equalsIgnoreCase("iexplore")
					&& locator.toUpperCase().startsWith(directory.toUpperCase())) {

				while(locator.contains("/")){
					locator = locator.replace("/", "\\");
				}
				
			}
		}
		
		
		
		return locator;
	}

}
