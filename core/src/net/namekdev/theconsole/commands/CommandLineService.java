package net.namekdev.theconsole.commands;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.namekdev.theconsole.scripts.JsScript;
import net.namekdev.theconsole.scripts.JsScriptManager;
import net.namekdev.theconsole.view.ConsoleView;

import com.badlogic.gdx.Input.Keys;
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

	public CommandLineService(ConsoleView consoleView, TextField inputField, JsScriptManager scriptManager) {
		this.consoleView = consoleView;
		this.inputField = inputField;
		this.scriptManager = scriptManager;

		inputField.addListener(new KeyListener());
	}


	class KeyListener extends InputListener {
		static final int SPACE_CHAR = 32;
		static final char NEW_LINE_CHAR = '\n';

		final Pattern paramRegex = Pattern.compile("(\\w+)|\"([^\"]*)\"|\'([^\"]*)\'|`([^\"]*)`");

		Array<String> commandNames = new Array<>(true, scriptManager.getScriptCount()*3);
		Actor lastAddedEntry = null;


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
					final String fullCommand = inputField.getText();

					if (fullCommand.length() == 0) {
						break;
					}

					consoleView.addInputEntry(fullCommand);
					inputField.setText("");
					tryExecuteCommand(fullCommand);
					lastAddedEntry = null;
					break;
				}
				case Keys.ESCAPE: {
					inputField.setText("");
					lastAddedEntry = null;
					break;
				}
				case Keys.BACKSPACE:
				case Keys.FORWARD_DEL: //DELETE
				{
					if (inputField.getText().length() == 0) {
						// forget old entry
						lastAddedEntry = null;
					}
					break;
				}
			}

			return true;
		}

		int countSpacesInInput() {
			int count = 0;
			String str = inputField.getText();

			for (int i = 0, n = str.length(); i < n; ++i) {
				if (str.charAt(i) == SPACE_CHAR) {
					++count;
				}
			}

			return count;
		}

		void tryCompleteCommandName() {
			String namePart = inputField.getText();

			// TODO search between aliases too
			commandNames.size = 0;
			scriptManager.findScriptNamesStartingWith(namePart, commandNames);

			// Complete this command
			if (commandNames.size == 1) {
				// complete to this one
				String commandName = commandNames.get(0);
				inputField.setText(commandName);
				inputField.setCursorPosition(commandName.length());
			}

			// Complete to the common part and show options to continue
			else if (commandNames.size > 1) {
				// TODO complete to the common part
				String commonPart = findBiggestCommonPart(commandNames);
				if (commonPart.length() > 0 && !inputField.getText().equals(commonPart)) {
					inputField.setText(commonPart);
					inputField.setCursorPosition(commonPart.length());
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
				final Array<String> allCommandNames = scriptManager.getAllScriptNames();
				StringBuilder sb = new StringBuilder();

				for (int i = 0; i < allCommandNames.size; ++i) {
					sb.append(allCommandNames.get(i));

					if (i != allCommandNames.size-1) {
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
		void tryExecuteCommand(String fullCommand) {
			boolean runAsJavaScript = false;
			Matcher matcher = paramRegex.matcher(fullCommand);

			if (!matcher.find()) {
				runAsJavaScript = true;
			}

			if (!runAsJavaScript) {
				String commandName = matcher.group(1);
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

				JsScript script = scriptManager.get(commandName);

				if (script != null) {
					// TODO validate arguments here

					Object result = script.run(args.toArray(new String[args.size()]));
					if (result != null) {
						consoleView.addTextEntry(result + "");
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
