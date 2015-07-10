package net.namekdev.theconsole.scripts;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.function.BiConsumer;

import net.namekdev.theconsole.utils.Database;
import net.namekdev.theconsole.utils.PathUtils;
import net.namekdev.theconsole.utils.RecursiveWatcher;
import net.namekdev.theconsole.utils.RecursiveWatcher.FileChangeEvent;

import com.badlogic.gdx.utils.Array;

/**
 * Installs file watcher on scripts, notifies about changes of scripts.
 * @author Namek
 *
 */
public class JsScriptManager {
	private final String SCRIPT_FILE_EXTENSION = "js";

	private Map<String, IScript> scripts = new TreeMap<String, IScript>();
	private Array<String> scriptNames = new Array<String>();

	protected JavaScriptExecutor jsEnv;
	protected JsUtilsProvider jsUtils;
	protected ConsoleProxy console;
	protected Database settingsDatabase;
	protected Database.SectionAccessor scriptsDatabase;

	final Path scriptsWatchDir = PathUtils.scriptsDir;
	private PathMatcher scriptExtensionMatcher;

	private final TemporaryArgs tempArgs;


	public JsScriptManager(JsUtilsProvider jsUtils, ConsoleProxy console, Database database) {
		this.jsUtils = jsUtils;
		this.console = console;
		this.settingsDatabase = database;
		scriptsDatabase = settingsDatabase.getScriptsSection();

		tempArgs = new TemporaryArgs();

		createJsEnvironment();

		final FileSystem fs = FileSystems.getDefault();
		scriptExtensionMatcher = fs.getPathMatcher("glob:**/*." + SCRIPT_FILE_EXTENSION);

		if (!Files.isDirectory(scriptsWatchDir)) {
			String path = scriptsWatchDir.toAbsolutePath().toString();
			console.log("No scripts folder found, creating a new one: " + path);
			new File(path).mkdirs();
		}

		// TODO if the scripts folder doesn't exist, then create it and copy standard scripts from internals

		analyzeScriptsFolder(scriptsWatchDir);

		try {

			final RecursiveWatcher watcher = new RecursiveWatcher(scriptsWatchDir, 500, new ScriptsFileWatcher());
			watcher.start();
		}
		catch (IOException exc) {
			console.error(exc.toString());
		}
	}

	private void createJsEnvironment() {
		jsEnv = new JavaScriptExecutor();
		jsEnv.bindObject("Utils", jsUtils);
		jsEnv.bindObject("TemporaryArgs", tempArgs);
		jsEnv.bindObject("console", console);

		jsEnv.bindObject("assert", (BiConsumer<Boolean, String>)
			(condition, error) -> {
				jsUtils.assertError(condition, error);
			}
		);

		jsEnv.bindObject("assertInfo", (BiConsumer<Boolean, String>)
			(condition, text) -> {
				jsUtils.assertInfo(condition, text);
			}
		);
	}

	public IScript get(String name) {
		return scripts.get(name);
	}

	public JsScriptManager put(String name, IScript script) {
		scripts.put(name, script);

		if (!scriptNames.contains(name, false)) {
			scriptNames.add(name);
			scriptNames.sort();
		}

		return this;
	}

	public void remove(String name) {
		scripts.remove(name);
		scriptNames.removeValue(name, false);
	}

	public int getScriptCount() {
		return scripts.size();
	}

	/**
	 * Returns internal array for performance. Do not modify it!
	 */
	public Array<String> getAllScriptNames() {
		return scriptNames;
	}

	/**
	 * Run JavaScript code without creating any scope and getting any additional context.
	 */
	public Object runJs(String code) {
		return jsEnv.eval(code);
	}

	/**
	 * Run JavaScript code in new scope within given context.
	 */
	public Object runScopedJs(String code, Object[] args, Object context) {
		tempArgs.args = args;
		tempArgs.context = context;

		return runJs("(function(args, Storage) {" + code + "})(Java.from(TemporaryArgs.args), TemporaryArgs.context.Storage)");
	}

	Database.SectionAccessor createScriptStorage(String name) {
		return scriptsDatabase.getSection(name, true);
	}

	public void findScriptNamesStartingWith(String namePart, Array<String> outNames) {
		if (namePart.length() == 0) {
			return;
		}

		for (String scriptName : scriptNames) {
			if (scriptName.indexOf(namePart) == 0) {
				outNames.add(scriptName);
			}
		}
	}

	private int analyzeScriptsFolder(Path folder) {
		int diff = 0;

		try {
			console.log("Analyzing folder structure for ." + SCRIPT_FILE_EXTENSION + " files: " + folder);

			int scriptsCount = scriptNames.size;

			Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
				@Override
			    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
					if (!attr.isRegularFile()) {
						return CONTINUE;
					}

					tryReadScriptFile(file);

					return CONTINUE;
				}
			});

			diff = scriptNames.size - scriptsCount;

			if (diff == 0) {
				console.log("No scripts were loaded.");
			}
		}
		catch (IOException exc) {
			console.error(exc.toString());
		}

		return diff;
	}

	private void tryReadScriptFile(Path path) {
		if (!scriptExtensionMatcher.matches(path)) {
			return;
		}

		final String scriptName = pathToScriptName(path);

		try {
			String code = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);

			// TODO try to pre-compile script for errors-check

			IScript script = get(scriptName);

			if (script == null) {
				console.log("Loading script: " + scriptName);
				script = new JsScript(this, scriptName, code);
				put(scriptName, script);
			}
			else if (script instanceof JsScript) {
				console.log("Reloading script: " + scriptName);
				((JsScript) script).code = code;
			}
			else {
				console.error("Cannot overwrite core script: " + scriptName);
			}
		}
		catch (IOException exc) {
			console.error(exc.toString());
		}
	}

	private void removeScriptByPath(Path path) {
		final String scriptName = pathToScriptName(path);
		remove(scriptName);
	}

	private String pathToScriptName(Path path) {
		String filename = path.getFileName().toString();

		if (filename.toLowerCase().endsWith(SCRIPT_FILE_EXTENSION)) {
			filename = filename.substring(0, filename.length() - SCRIPT_FILE_EXTENSION.length() - 1);
		}

		return filename;
	}


	public static class TemporaryArgs {
		public Object[] args;
		public Object context;
	}

	class ScriptsFileWatcher implements RecursiveWatcher.WatchListener {
		@Override
		public void onWatchEvents(Queue<FileChangeEvent> events) {
			for (FileChangeEvent evt : events) {
				Path fullPath = evt.parentFolderPath.resolve(evt.relativePath);

				if (evt.eventType == ENTRY_CREATE) {
					tryReadScriptFile(fullPath);
				}
				else if (evt.eventType == ENTRY_MODIFY) {
					tryReadScriptFile(fullPath);
				}
				else if (evt.eventType == ENTRY_DELETE) {
					removeScriptByPath(fullPath);
				}
			}
		}
	}
}
