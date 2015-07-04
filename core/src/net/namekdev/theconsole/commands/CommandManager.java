package net.namekdev.theconsole.commands;

import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

import net.namekdev.theconsole.scripts.ConsoleProxy;
import net.namekdev.theconsole.scripts.JavaScriptExecutor;
import net.namekdev.theconsole.scripts.JsScript;
import net.namekdev.theconsole.scripts.JsUtilsProvider;

import com.badlogic.gdx.utils.Array;

/**
 * Installs file watcher on scripts, notifies about changes of scripts.
 * @author Namek
 *
 */
public class CommandManager {
	private Map<String, ICommand> executables = new TreeMap<String, ICommand>();
	private Array<String> executableNames = new Array<String>();

	protected JavaScriptExecutor jsEnv;
	protected JsUtilsProvider jsUtils;

	private final TemporaryArgs tempArgs;


	public CommandManager(JsUtilsProvider jsUtils, ConsoleProxy consoleProxy) {
		this.jsUtils = jsUtils;

		tempArgs = new TemporaryArgs();

		jsEnv = new JavaScriptExecutor();
		jsEnv.bindObject("Utils", jsUtils);
		jsEnv.bindObject("TemporaryArgs", tempArgs);
		jsEnv.bindObject("console", consoleProxy);
	}

	public ICommand get(String name) {
		return executables.get(name);
	}

	public CommandManager put(String name, ICommand executable) {
		executables.put(name, executable);

		if (!executableNames.contains(name, false)) {
			executableNames.add(name);
			executableNames.sort();
		}

		return this;
	}

	public int getCommandCount() {
		return executables.size();
	}

	/**
	 * Returns internal array for performance. Do not modify it!
	 */
	public Array<String> getAllCommandNames() {
		return executableNames;
	}

	/**
	 * Run JavaScript code without creating new scope.
	 */
	public Object runJs(String code) {
		return jsEnv.eval(code);
	}

	/**
	 * Run JavaScript code in new scope.
	 */
	public Object runScopedJs(String code, Object[] args) {
		tempArgs.args = args;
		return jsEnv.eval("(function(args) {" + code + "})(TemporaryArgs.args)");
	}

	public void findCommandNamesStartingWith(String namePart, Array<String> outNames) {
		if (namePart.length() == 0) {
			return;
		}

		for (String scriptName : executableNames) {
			if (scriptName.indexOf(namePart) == 0) {
				outNames.add(scriptName);
			}
		}
	}



	public static class TemporaryArgs {
		public Object[] args;
	}
}
