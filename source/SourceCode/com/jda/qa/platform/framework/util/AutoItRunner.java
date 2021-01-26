package SourceCode.com.jda.qa.platform.framework.util;


import com.jda.qa.platform.framework.util.AutoItCommandExecutor;

public class AutoItRunner implements Runnable {

	String outputFile, executableName, param;

	public AutoItRunner(String outputFile, String executableName, String param) {

		this.outputFile = outputFile;
		this.executableName = executableName;
		this.param = param;
	}

	@Override
	public void run() {
		AutoItCommandExecutor exec = new AutoItCommandExecutor();

		exec.runAutoItExecutable(outputFile, executableName, param);
	}

}
