package SourceCode.com.jda.qa.platform.framework.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * This is an Utility class to work with the files
 * 
 * @author Santosh Kumar Medchalam and Vinay Kumar Tangella
 *
 */
public class FileUtils {
	
	/**
	 * This method will return the absolute path of the provided filename along with 
	 * the foldername with respect to the current user directory.
	 * 
	 * @param foldername  The name of the file along with extension
	 * @param filename The parent folder of the file
	 * @return
	 */

	public static String getFileLocation(String foldername, String filename) {

		String curr_path = new File("").getAbsolutePath();

		String file_location = curr_path.replace("\\", "\\\\") + "\\\\"
				+ foldername + "\\\\" + filename;

		return file_location;
	}
	
	/**
	 * 
	 * This method will retrun the current user directory.
	 * 
	 * @return The current user directory.
	 */

	public static String getRootDir() {

		String curr_path = new File("").getAbsolutePath();

		return curr_path;
	}
	
	/**
	 * 
	 * This method is used to copy a file from source location to target location
	 * 
	 * @param src The file to be copied
	 * @param trgt The desitnation location where the file needs to be copied 
	 */

	public static void copyFile(File src, File trgt) {
		try {
			org.apache.commons.io.FileUtils.copyFile(src, trgt);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is used to verify whether a file contains a particular string or not
	 * 
	 * 
	 * @param inputFile - The file to be search upon
	 * @param searchString - The Content to the searched upon
	 * @return - true if the file contains the searchString, false if the same is not available. 
	 * 			 (The comparision is CASE-SENSITIVE)
	 */
	
	public static boolean fileContains(File inputFile, String searchString) {

		boolean searchStatus = false;

		try {

			List<String> lines = org.apache.commons.io.FileUtils
					.readLines(inputFile);

			Iterator<String> linesItr = lines.iterator();

			while (linesItr.hasNext()) {
				String currentLine = linesItr.next();

				if (currentLine.contains(searchString)) {
					searchStatus = true;
					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return searchStatus;
	}
}
