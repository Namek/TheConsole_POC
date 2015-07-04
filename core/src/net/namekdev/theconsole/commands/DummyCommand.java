package net.namekdev.theconsole.commands;

public class DummyCommand implements ICommand {

	@Override
	public Object run() {
		return run(EMPTY_ARGS);
	}

	@Override
	public Object run(String[] args) {
		return null;
	}

	@Override
	public Object run(String firstArgument) {
		ONE_ARG[0] = firstArgument;
		return run(ONE_ARG);
	}

}
