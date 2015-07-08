package net.namekdev.theconsole.commands;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.namekdev.theconsole.scripts.JsScript;
import net.namekdev.theconsole.scripts.JsScriptManager;
import net.namekdev.theconsole.view.ConsoleView;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;

/**
 * All about input field: auto-completion, history.
 *
 * @author Namek
 *
 */
public class CommandLineService {
	protected ConsoleView consoleView;
	protected TextField inputField;
	protected JsScriptManager scriptManager;

	protected CommandHistory history;
	protected AliasManager aliasManager;


	public CommandLineService(ConsoleView consoleView, TextField inputField, JsScriptManager scriptManager) {
		this.consoleView = consoleView;
		this.inputField = inputField;
		this.scriptManager = scriptManager;

		history = new CommandHistory();
		aliasManager = new AliasManager();

		inputField.addListener(new KeyListener());
	}


	class KeyListener extends InputListener {
		static final int SPACE_CHAR = 32;
		static final char NEW_LINE_CHAR = '\n';

		final Pattern paramRegex = Pattern.compile("(\\w+)|\"([^\"]*)\"|\'([^\"]*)\'|`([^\"]*)`");

		Array<String> commandNames = new Array<>(true, 100);
		Actor lastAddedEntry = null;
		String temporaryCommandName;


		public boolean keyTyped (InputEvent event, char character) {
			final int keyCode = event.getKeyCode();

			switch (event.getKeyCode()) {
				case Keys.TAB: {
					if (countSpacesInInput() == 0) {
						tryCompleteCommandName();
					}
					else {
						// TODO try to complete command parameters
					}
					break;
				}

				case Keys.ENTER: {
					final String fullCommand = getInput();

					if (fullCommand.length() == 0) {
						break;
					}

					consoleView.addInputEntry(fullCommand);
					setInput("");
					tryExecuteCommand(fullCommand, false);
					history.save(fullCommand);
					lastAddedEntry = null;
					temporaryCommandName = null;
					history.resetPointer();
					break;
				}

				case Keys.ESCAPE: {
					setInput("");
					lastAddedEntry = null;

					if (temporaryCommandName == null) {
						history.resetPointer();
					}
					else {
						temporaryCommandName = null;
					}

					break;
				}

				case Keys.BACKSPACE:
				case Keys.FORWARD_DEL: //DELETE
				{
					if (getInput().length() == 0) {
						// forget old entry
						lastAddedEntry = null;
					}
					break;
				}

				case Keys.UP: {
					if (history.hasAny()) {
						String input = getInput();

						if (input.equals(history.getCurrent()))
							history.morePast();
						else {
							temporaryCommandName = input;
						}

						setInput(history.getCurrent());
					}

					break;
				}

				case Keys.DOWN: {
					if (history.hasAny()) {
						if (history.lessPast()) {
							setInput(temporaryCommandName != null ? temporaryCommandName : "");
						}
						else {
							setInput(history.getCurrent());
						}
					}

					break;
				}
			}

			return true;
		}

		void setInput(String text) {
			inputField.setText(text);
			inputField.setCursorPosition(text.length());
		}

		String getInput() {
			return inputField.getText();
		}

		int countSpacesInInput() {
			int count = 0;
			String str = getInput();

			for (int i = 0, n = str.length(); i < n; ++i) {
				if (str.charAt(i) == SPACE_CHAR) {
					++count;
				}
			}

			return count;
		}

		void tryCompleteCommandName() {
			String namePart = getInput();

			// TODO search between aliases too
			commandNames.size = 0;
			commandNames.ensureCapacity(scriptManager.getScriptCount() + aliasManager.getAliasCount());
			scriptManager.findScriptNamesStartingWith(namePart, commandNames);
			aliasManager.findAliasesStartingWith(namePart, commandNames);

			// Complete this command
			if (commandNames.size == 1) {
				// complete to this one
				String commandName = commandNames.get(0);
				setInput(commandName);
				lastAddedEntry = null;
			}

			// Complete to the common part and show options to continue
			else if (commandNames.size > 1) {
				// TODO complete to the common part
				String commonPart = findBiggestCommonPart(commandNames);
				if (commonPart.length() > 0 && !getInput().equals(commonPart)) {
					setInput(commonPart);
				}
				else {
					// Present options
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < commandNames.size; ++i) {
						sb.append(commandNames.get(i));

						if (i != commandNames.size-1) {
							sb.append(NEW_LINE_CHAR);
						}
					}

					String text = sb.toString();

					// Don't add the same output second time
					if (!(lastAddedEntry instanceof Label && ((Label)lastAddedEntry).getText().toString().equals(text))) {
						lastAddedEntry = consoleView.addTextEntry(text);
					}
				}
			}

			// Just present command list
			else {
				final Array<String> allScriptNames = scriptManager.getAllScriptNames();
				final Array<String> allAliasNames = aliasManager.getAllAliasNames();
				commandNames.size = 0;
				commandNames.addAll(allScriptNames);
				commandNames.addAll(allAliasNames);
				commandNames.sort();

				StringBuilder sb = new StringBuilder();

				for (int i = 0; i < commandNames.size; ++i) {
					sb.append(commandNames.get(i));

					if (i != commandNames.size-1) {
						sb.append(NEW_LINE_CHAR);
					}
				}

				if (lastAddedEntry == null || !(lastAddedEntry instanceof Label)) {
					lastAddedEntry = consoleView.addTextEntry(sb.toString());
				}
				else {
					// modify existing text entry
					((Label)lastAddedEntry).setText(sb.toString());
				}
			}
		}

		// TODO find between alias
		void tryExecuteCommand(String fullCommand, boolean ignoreAliases) {
			boolean runAsJavaScript = false;
			Matcher matcher = paramRegex.matcher(fullCommand);

			if (!matcher.find()) {
				// Expression is so weird that cannot be a command, try to run it as JS code.
				runAsJavaScript = true;
			}
			else {
				// Read command name
				String commandName = "";
				int commandNameEndIndex = -1;

				for (int i = 1; i <= matcher.groupCount(); ++i) {
					String group = matcher.group(i);

					if (group != null && group.length() > commandName.length()) {
						commandName = group;
						commandNameEndIndex = matcher.end(i);
					}
				}

				// Read command arguments
				ArrayList<String> args = new ArrayList<String>();

				while (matcher.find()) {
					String parameterValue = "";

					for (int i = 1; i <= matcher.groupCount(); ++i) {
						String group = matcher.group(i);

						if (group != null && group.length() > parameterValue.length()) {
							parameterValue = group;
						}
					}

					args.add(parameterValue);
				}

				// Look for script of such name
				JsScript script = scriptManager.get(commandName);

				if (script != null) {
					// TODO validate arguments here

					Object result = script.run(args.toArray(new String[args.size()]));
					if (result != null) {
						if (result instanceof Exception) {
							consoleView.addErrorEntry(result.toString());
						}
						else {
							consoleView.addTextEntry(result + "");
						}
					}
				}
				else if (!ignoreAliases) {
					// There is no script named by `commandName` so look for aliases
					String command = aliasManager.get(commandName);

					if (command != null) {
						String newFullCommand = command + fullCommand.substring(commandNameEndIndex);
						tryExecuteCommand(newFullCommand, true);
					}
					else {
						runAsJavaScript = true;
					}
				}
				else {
					runAsJavaScript = true;
				}
			}

			if (runAsJavaScript) {
				// script was not found, so try to execute it as pure JavaScript!
//				consoleView.addErrorEntry("Command not found, running as JavaScript code...");
				Object result = scriptManager.runJs(fullCommand);
				consoleView.addTextEntry(result + "");
			}
		}

		String findBiggestCommonPart(Array<String> names) {
			if (names.size == 1) {
				return names.get(0);
			}
			else if (names.size == 0) {
				return "";
			}

			int charIndex = 0;

			searching: while (true) {
				final String firstName = names.get(0);

				if (firstName.length() <= charIndex) {
					break;
				}

				char c = firstName.charAt(charIndex);

				for (int i = 1; i < names.size; ++i) {
					final String name = names.get(i);

					if (name.length() <= charIndex || name.charAt(charIndex) != c) {
						break searching;
					}
				}

				++charIndex;
			}

			return names.get(0).substring(0, charIndex);
		}
	}
}
