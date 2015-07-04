package net.namekdev.theconsole.commands.basic;

import net.namekdev.theconsole.commands.NoArgCommand;
import net.namekdev.theconsole.view.ConsoleView;

public class ClearScreenCommand extends NoArgCommand {
	ConsoleView consoleView;

	public ClearScreenCommand(ConsoleView consoleView) {
		this.consoleView = consoleView;
	}

	@Override
	public Object run() {
		consoleView.clearEntries();
		return null;
	}
}
