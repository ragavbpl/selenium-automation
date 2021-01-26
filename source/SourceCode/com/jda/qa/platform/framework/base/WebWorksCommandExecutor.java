package SourceCode.com.jda.qa.platform.framework.base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

import com.jda.qa.platform.framework.base.MainEngine;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriverException;

import com.jda.qa.platform.framework.util.FileUtils;

public class WebWorksCommandExecutor {

	private static ProcessBuilder processBuilder = null;

	Logger logFile = MainEngine.log;

	public void runCommand(String logFileName, String... commandParams) {
		try {

			processBuilder = new ProcessBuilder(commandParams);

			processBuilder.redirectErrorStream(true);

			Process process = processBuilder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			StringBuilder builder = new StringBuilder();

			String line = reader.readLine();

			while (line != null) {
				builder.append(line);
				builder.append("\n");
				line = reader.readLine();
			}

			reader.close();

			File consoleOutFile = new File(FileUtils.getFileLocation("logs",
					logFileName));

			if (consoleOutFile.exists()) {
				consoleOutFile.delete();
			}

			consoleOutFile.createNewFile();

			BufferedWriter bw = new BufferedWriter(new FileWriter(
					consoleOutFile));

			bw.write(builder.toString());

			bw.flush();

			bw.close();

		} catch (Exception e) {

			logFile.info(e.getMessage());

			logFile.info("Failed to run the command with the mentioned arguments");

			for (int i = 0; i < commandParams.length; i++) {
				logFile.info("Argument" + i + " : " + commandParams[i]);
			}

			throw new WebDriverException	(
					"Failed to run the command with the mentioned arguments "
							+ e.getMessage());
		}

	}

}