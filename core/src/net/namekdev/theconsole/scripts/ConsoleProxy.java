package net.namekdev.theconsole.scripts;

import com.badlogic.gdx.graphics.Color;

import net.namekdev.theconsole.view.ConsoleView;

public class ConsoleProxy {
	private ConsoleView consoleView;
	private final Color tmpColor = new Color();


	public ConsoleProxy(ConsoleView consoleView) {
		this.consoleView = consoleView;
	}

	public void log(String text) {
		consoleView.addTextEntry(text);
	}

	/**
	 *
	 * @param text
	 * @param colorHex Hex in format 0xRRGGBB, example red: 0xFF0000.
	 */
	public void log(String text, int colorHex) {
		tmpColor.set((colorHex << 8) | 0xFF);
		consoleView.addTextEntry(text, tmpColor);
	}

	public void error(String text) {
		consoleView.addErrorEntry(text);
	}

	public void clear() {
		consoleView.clearEntries();
	}
}
