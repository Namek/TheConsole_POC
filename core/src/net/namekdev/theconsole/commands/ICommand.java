package net.namekdev.theconsole.commands;

public interface ICommand {
	static String[] EMPTY_ARGS = new String[] { };
	static final String[] ONE_ARG = new String[1];

	public Object run();
	public Object run(String[] args);
	public Object run(String firstArgument);
}
