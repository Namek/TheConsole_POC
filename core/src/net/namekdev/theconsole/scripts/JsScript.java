package net.namekdev.theconsole.scripts;


/**
 * This class doesn't have any intelligence since it's totally managed/modified by {@link JsScriptManager}.
 *
 * @author Namek
 * @see JsScriptManager
 */
public class JsScript {
	JsScriptManager manager;
	String code;

	JsScript(JsScriptManager manager) {
		this.manager = manager;
	}

	JsScript(JsScriptManager manager, String code) {
		this.manager = manager;
		this.code = code;
	}

	public static JsScript create(JsScriptManager manager, String code) {
		return new JsScript(manager, code);
	}

	public Object run(String[] args) {
		return manager.runScopedJs(this.code, args);
	}

	public Object run(String firstArgument) {
		return run(new String[] { firstArgument });
	}
}
