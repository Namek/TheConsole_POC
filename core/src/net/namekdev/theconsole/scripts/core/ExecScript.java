package net.namekdev.theconsole.scripts.core;

import net.namekdev.theconsole.scripts.IScript;
import net.namekdev.theconsole.scripts.JsUtilsProvider;

public class ExecScript implements IScript {
	protected JsUtilsProvider utils;


	public ExecScript(JsUtilsProvider utils) {
		this.utils = utils;
	}

	@Override
	public Object run(String[] args) {
		utils.assertInfo(args.length > 0, "Usage: exec <command_name/app_path + arguments>");

		String filepath = utils.argsToString(args);
		utils.execAsync(filepath);

		return filepath;
	}

}
