package net.namekdev.theconsole.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class PathUtils {
	public static final Path appSettingsDir = Paths.get(System.getenv("AppData"), "TheConsole");
	public static final Path scriptsDir = appSettingsDir.resolve("scripts");
	public static final Path workingDir;

	static {
		URL myURL = PathUtils.class.getProtectionDomain().getCodeSource().getLocation();
		URI myURI = null;

		try {
		    myURI = myURL.toURI();
		}
		catch (URISyntaxException exc) { }

		workingDir = Paths.get(myURI);
	}
}
