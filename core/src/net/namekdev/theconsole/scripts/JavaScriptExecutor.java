package net.namekdev.theconsole.scripts;

import java.util.function.Function;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JavaScriptExecutor {
	private ScriptEngineManager engineManager;
	private ScriptEngine engine;
	private Invocable invocable;
	private Bindings engineBindings;


	public JavaScriptExecutor() {
		engineManager = new ScriptEngineManager();
		engine = engineManager.getEngineByName("nashorn");
		invocable = (Invocable) engine;

		engine.put("JavaClass", (Function<String, Class>)
			className -> {
				try {
					return Class.forName(className);
				}
				catch (Exception exc) {
					throw new RuntimeException(exc);
				}
			}
		);

		engineBindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);

		bindClass("System", System.class);
	}

	public void bindClass(String variableName, Class<?> cls) {
		bindClass(variableName, cls.getName());
	}

	public void bindClass(String variableName, String classPath) {
		try {
			engine.eval("var " + variableName + " = JavaClass('" + classPath + "').static");
		}
		catch (ScriptException e) { }
	}

	public void bindObject(String variableName, Object obj) {
		try {
			engineBindings.put(variableName, obj);
		}
		catch (Exception exc) { }
	}

	public Object eval(String scriptCode) {
		return eval(scriptCode, true);
	}

	public Object eval(String scriptCode, boolean returnExceptionObject) {
		Object ret = null;

		try {
			ret = engine.eval(scriptCode);
		}
		catch (Exception e) {
			if (returnExceptionObject) {
				ret = e;
			}

			if (!(e instanceof ScriptException)) {
				e.printStackTrace();
			}
		}

		return ret;
	}
}
