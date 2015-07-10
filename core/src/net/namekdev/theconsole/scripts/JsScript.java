package net.namekdev.theconsole.scripts;


/**
 * This class doesn't have any intelligence since it's totally managed/modified by {@link JsScriptManager}.
 *
 * @author Namek
 * @see JsScriptManager
 */
public class JsScript {
	JsScriptManager manager;
	String name;
	String code;
	Context context;


	JsScript(JsScriptManager manager, String name, String code) {
		this.manager = manager;
		this.name = name;
		this.code = code;

		context = new Context();
		context.Storage = manager.createScriptStorage(name);
	}

	public Object run(String[] args) {
		return manager.runScopedJs(this.code, args, context);
	}

	public Object run(String firstArgument) {
		return run(new String[] { firstArgument });
	}

	public class Context {
		public Object Storage;
	}
}
