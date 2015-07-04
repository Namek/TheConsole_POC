package net.namekdev.theconsole.commands.basic;

import java.io.PrintWriter;

import net.namekdev.theconsole.commands.DummyCommand;
import net.namekdev.theconsole.scripts.JsUtilsProvider;

public class ExecCommand extends DummyCommand {
	private JsUtilsProvider jsUtils;
	private PrintWriter errorStream;


	public ExecCommand(JsUtilsProvider jsUtils, PrintWriter errorStream) {
		this.jsUtils = jsUtils;
		this.errorStream = errorStream;
	}

	@Override
	public Object run(String[] args) {
		Runtime runtime = Runtime.getRuntime();

		try {
			Process p = runtime.exec(args);
			return p.waitFor();
		}
		catch (Exception e) {
			e.printStackTrace(errorStream);
			return -1;
		}
	}

}
