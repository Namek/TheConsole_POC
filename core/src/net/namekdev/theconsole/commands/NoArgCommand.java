package net.namekdev.theconsole.commands;

public abstract class NoArgCommand implements ICommand {

	@Override
	public Object run(String[] args) {
		return run();
	}

	@Override
	public Object run(String firstArgument) {
		return run();
	}

}
