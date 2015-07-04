package net.namekdev.theconsole.scripts;

import net.namekdev.theconsole.commands.CommandManager;
import net.namekdev.theconsole.commands.ICommand;

/**
 * This class doesn't have any intelligence since it's totally managed/modified by {@link CommandManager}.
 *
 * @author Namek
 * @see CommandManager
 */
public class JsScript implements ICommand {
	CommandManager manager;
	String code;

	JsScript(CommandManager manager) {
		this.manager = manager;
	}

	JsScript(CommandManager manager, String code) {
		this.manager = manager;
		this.code = code;
	}

	public static ICommand create(CommandManager manager, String code) {
		return new JsScript(manager, code);
	}

	public Object run() {
		return run(EMPTY_ARGS);
	}

	public Object run(String[] args) {
		return manager.runScopedJs(this.code, args);
	}

	public Object run(String firstArgument) {
		return run(new String[] { firstArgument });
	}
}
