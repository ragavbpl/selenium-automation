package SourceCode.com.jda.qa.platform.framework.util;

import com.jda.qa.platform.framework.util.FileUtils;
import org.apache.log4j.Logger;

import com.jda.qa.platform.framework.base.MainEngine;
import com.jda.qa.platform.framework.base.WebWorksCommandExecutor;

public class AutoItCommandExecutor {

	Logger logFile = MainEngine.log;

	public void runAutoItExecutable(String outputFile, String executableName,
			String param) {

		WebWorksCommandExecutor autoItExecutor = new WebWorksCommandExecutor();

		String executable = FileUtils.getFileLocation("executables",
				executableName);

		logFile.info("Launching the Executable : " + executable);

		logFile.info("Output will be redirected to : " + outputFile
				+ " in logs folder");

		autoItExecutor.runCommand(outputFile, executable, param);

		logFile.info("Completed the Execution of the  : " + executable);
	}

}