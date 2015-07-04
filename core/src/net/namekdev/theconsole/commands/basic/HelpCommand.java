package net.namekdev.theconsole.commands.basic;

import net.namekdev.theconsole.commands.NoArgCommand;
import net.namekdev.theconsole.view.ConsoleView;

public class HelpCommand extends NoArgCommand {
	ConsoleView consoleView;

	public HelpCommand(ConsoleView consoleView) {
		this.consoleView = consoleView;
	}

	@Override
	public Object run() {
		consoleView.addTextEntry(
			"|\n" +
			"|  Welcome to command line of The Console\n" +
			"|\n" +
			"|   ---- the only console you'll need\n" +
			"|\n\n" +
			"Hit TAB to see command list.\n"
		);
		return null;
	}

}
