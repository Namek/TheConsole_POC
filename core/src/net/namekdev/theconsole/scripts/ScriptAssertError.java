package net.namekdev.theconsole.scripts;

public class ScriptAssertError extends Error {
	public final String text;
	public final boolean isError;

	public ScriptAssertError(String text, boolean isError) {
		this.text = text;
		this.isError = isError;
	}
}
