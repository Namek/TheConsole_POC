package net.namekdev.theconsole.scripts;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class JsUtilsProvider {
	protected PrintWriter errorStream;


	public JsUtilsProvider(PrintWriter printWriter) {
		this.errorStream = printWriter;
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

	public String test() {
		return "tested properly";
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
}
