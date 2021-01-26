package SourceCode.com.jda.qa.platform.framework.util;

import com.jda.qa.platform.data.database.SQLCommandExecutor;

public class SQLRunner implements Runnable {

	String DBConnectionURL, sqlInput, outputFile;

	public SQLRunner(String DBConnectionURL, String sqlInput,
			String outputFile) {

		this.DBConnectionURL = DBConnectionURL;
		this.sqlInput = sqlInput;
		this.outputFile = outputFile;
	}

	@Override
	public void run() {
		SQLCommandExecutor exec = new SQLCommandExecutor();

		exec.invokeSQLProcess(DBConnectionURL, sqlInput, outputFile);
	}

}
