package net.namekdev.theconsole.commands;

import java.util.Stack;

public class CommandHistory {
	private Stack<String> commands = new Stack<String>();

	/**
	 * Stack position.
	 */
	private int pointer = 0;


	public void save(String command) {
		commands.add(command);
	}

	public boolean hasAny() {
		return commands.size() > 0;
	}

	public void morePast() {
		pointer = Math.min(pointer + 1, commands.size() - 1);
	}

	public boolean lessPast() {
		int prevPointer = pointer;
		pointer = Math.max(pointer - 1, 0);

		return prevPointer == 0 && pointer == 0;
	}

	public void resetPointer() {
		pointer = 0;
	}

	public String getCurrent() {
		return commands.get(commands.size() - pointer - 1);
	}
}
