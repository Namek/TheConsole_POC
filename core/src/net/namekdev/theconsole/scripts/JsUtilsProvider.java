package net.namekdev.theconsole.scripts;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;

import net.namekdev.theconsole.utils.AudioFilePlayer;
import jdk.nashorn.internal.objects.NativeArray;

public class JsUtilsProvider {
	private PrintWriter errorStream;

	public final AudioFilePlayer audioFilePlayer = new AudioFilePlayer();


	public JsUtilsProvider(PrintWriter printWriter) {
		this.errorStream = printWriter;
	}

	/**
	 * Joins strings by single space between and quoting args containing any space.
	 * @param arr array of strings
	 * @return joined quoted strings
	 */
	public String argsToString(NativeArray arr) {
		StringBuilder sb = new StringBuilder();

		Iterator<Object> iter = arr.valueIterator();
		while (iter.hasNext()) {
			String arg = (String) iter.next();

			if (arg.length() == 0 || arg.indexOf(' ') >= 0) {
				sb.append("\"");
				sb.append(arg);
				sb.append("\"");
			}
			else {
				sb.append(arg);
			}

			if (iter.hasNext()) {
				sb.append(' ');
			}
		}

		return sb.toString();
	}

	/**
	 * Joins strings by single space between and quoting args containing any space.
	 * @param arr array of strings
	 * @return
	 */
	public String argsToString(String[] arr) {
		return argsToString(arr, 0);
	}

	/**
	 * Joins strings by single space between and quoting args containing any space.
	 * @param arr array of strings
	 * @return
	 */
	public String argsToString(String[] arr, int beginIndex) {
		StringBuilder sb = new StringBuilder();

		for (int i = beginIndex, n = arr.length; i < n; ++i) {
			String arg = arr[i];

			if (arg.length() == 0 || arg.indexOf(' ') >= 0) {
				sb.append("\"");
				sb.append(arg);
				sb.append("\"");
			}
			else {
				sb.append(arg);
			}

			if (i < n - 1) {
				sb.append(' ');
			}
		}

		return sb.toString();
	}

	public String getClassName(Object obj) {
		return obj.getClass().getName();
	}

	public String requestUrl(String url, String method) {
		if (url.indexOf(' ') >= 0) {
			errorStream.println("Utils.requestUrl() received url containing spaces. Use encodeURI() !");
			errorStream.flush();
			return null;
		}

		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");

			int response = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer sb = new StringBuffer();

			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();

			return sb.toString();
		}
		catch (Exception exc) {
			exc.printStackTrace(errorStream);
			return null;
		}
	}

	public String requestUrl(String url) {
		return requestUrl(url, "GET");
	}

	public void execAsync(String filepath) {
		Runtime runtime = Runtime.getRuntime();

		try {
			runtime.exec(filepath);
		}
		catch (IOException e) {
			e.printStackTrace(errorStream);
		}
	}

	public int exec(String filepath) {
		Runtime runtime = Runtime.getRuntime();

		try {
			Process p = runtime.exec(filepath);
			return p.waitFor();
		}
		catch (Exception e) {
			e.printStackTrace(errorStream);
			return -1;
		}
	}

	public void openUrl(String url) {
		if (url.indexOf(' ') >= 0) {
			errorStream.println("Utils.requestUrl() received url containing spaces. Use encodeURI() !");
			errorStream.flush();
			return;
		}

		Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(url));
        }
        catch (Exception e) {
            e.printStackTrace(errorStream);
        }
	}

	public void assertError(boolean condition, String error) {
		if (!condition) {
			throw new ScriptAssertError(error, true);
		}
	}

	public void assertInfo(Boolean condition, String text) {
		if (!condition) {
			throw new ScriptAssertError(text, false);
		}
	}
}
