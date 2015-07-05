package net.namekdev.theconsole.scripts;

import java.util.Map;
import java.util.TreeMap;

import com.badlogic.gdx.utils.Array;

/**
 * Installs file watcher on scripts, notifies about changes of scripts.
 * @author Namek
 *
 */
public class JsScriptManager {
	private Map<String, JsScript> scripts = new TreeMap<String, JsScript>();
	private Array<String> scriptNames = new Array<String>();

	protected JavaScriptExecutor jsEnv;
	protected JsUtilsProvider jsUtils;

	private final TemporaryArgs tempArgs;


	public JsScriptManager(JsUtilsProvider jsUtils, ConsoleProxy consoleProxy) {
		this.jsUtils = jsUtils;

		tempArgs = new TemporaryArgs();

		jsEnv = new JavaScriptExecutor();
		jsEnv.bindObject("Utils", jsUtils);
		jsEnv.bindObject("TemporaryArgs", tempArgs);
		jsEnv.bindObject("console", consoleProxy);
	}

	public JsScript get(String name) {
		return scripts.get(name);
	}

	public JsScriptManager put(String name, JsScript script) {
		scripts.put(name, script);

		if (!scriptNames.contains(name, false)) {
			scriptNames.add(name);
			scriptNames.sort();
		}

		return this;
	}

	public int getScriptCount() {
		return scripts.size();
	}

	/**
	 * Returns internal array for performance. Do not modify it!
	 */
	public Array<String> getAllScriptNames() {
		return scriptNames;
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

	public void findScriptNamesStartingWith(String namePart, Array<String> outNames) {
		if (namePart.length() == 0) {
			return;
		}

		for (String scriptName : scriptNames) {
			if (scriptName.indexOf(namePart) == 0) {
				outNames.add(scriptName);
			}
		}
	}



	public static class TemporaryArgs {
		public Object[] args;
	}
}
