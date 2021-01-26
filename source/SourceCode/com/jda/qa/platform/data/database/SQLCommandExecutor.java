package SourceCode.com.jda.qa.platform.data.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriverException;

import com.jda.qa.platform.framework.base.MainEngine;
import com.jda.qa.platform.framework.base.WebWorksCommandExecutor;
import com.jda.qa.platform.framework.util.FileUtils;

public class SQLCommandExecutor {

	Logger logFile = MainEngine.log;

	String SQL_COMMAND = "sqlplus";

	String sqlFileExtn = ".sql";

	private void runSQLCommand(String DBConnectionURL, String sqlCommand,
			String outputFile) {

		// Generate the .sqlFile using the sqlCommand given

		Random random = new Random();

		int randNum = random.nextInt();

		if (randNum > 0) {
			randNum = randNum * -1;
		}

		File sqlCommandFile = new File(FileUtils.getFileLocation("logs",
				"sqlfile" + randNum + ".sql"));

		if (sqlCommandFile.exists()) {
			sqlCommandFile.delete();
		}

		try {
			sqlCommandFile.createNewFile();

			BufferedWriter sqlCommandBufferedWriter = new BufferedWriter(
					new FileWriter(sqlCommandFile));

			sqlCommandBufferedWriter.newLine();

			sqlCommand = sqlCommand.endsWith(";") ? sqlCommand : sqlCommand
					+ ";";

			sqlCommandBufferedWriter.write(sqlCommand);

			sqlCommandBufferedWriter.newLine();

			sqlCommandBufferedWriter.write("commit;");
			sqlCommandBufferedWriter.newLine();
			sqlCommandBufferedWriter.write("exit;");

			sqlCommandBufferedWriter.flush();

			sqlCommandBufferedWriter.close();

		} catch (Exception exp) {
			MainEngine.log.info(exp.getMessage());
			MainEngine.log.info("Failed to create the sqlFile with command "
					+ sqlCommand);
			throw new WebDriverException(
					"Failed to create the sqlFile with command " + sqlCommand
							+ " " + exp.getMessage());
		}

		runSQLFile(DBConnectionURL, sqlCommandFile.getAbsolutePath(),
				outputFile);

	}

	private void runSQLFile(String DBConnectionURL, String sqlFile,
			String outputFile) {

		WebWorksCommandExecutor sqlFileExecutor = new WebWorksCommandExecutor();

		sqlFileExecutor.runCommand(outputFile, SQL_COMMAND, DBConnectionURL,
				"@" + sqlFile);

	}

	public void invokeSQLProcess(String DBConnectionURL, String sqlInput,
			String outputFile) {

		if (sqlInput.toLowerCase().endsWith(sqlFileExtn)) {
			runSQLFile(DBConnectionURL, sqlInput, outputFile);
		} else {
			runSQLCommand(DBConnectionURL, sqlInput, outputFile);
		}

	}

}