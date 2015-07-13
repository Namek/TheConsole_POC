package net.namekdev.theconsole.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

import net.namekdev.theconsole.utils.Database.SectionAccessor;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.JsonValue.ValueType;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

/**
 * Reads database file once. Gives a possibility to overwrite it.
 * Simple abstraction made for future security purposes.
 *
 * @author Namek
 *
 */
public class Database {
	protected final String ALIASES_SECTION = "aliases";
	protected final String SCRIPTS_SECTION = "scripts";

	private File file;
	public JsonValue content;


	public Database(String filePath) {
		file = new File(filePath);
		file.getParentFile().mkdirs();

		if (!file.exists()) {

			try {
				file.createNewFile();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			if (file.exists()) {
				FileReader fileStream = new FileReader(file);
				JsonReader reader = new JsonReader();
				content = reader.parse(fileStream);
				fileStream.close();
			}

			if (content == null) {
				content = new JsonValue(ValueType.object);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			FileWriter stream = new FileWriter(file, false);
			stream.write(content.toString());
			stream.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SectionAccessor getSection(String section, boolean createIfDoesntExist) {
		return getSection(content, section, createIfDoesntExist);
	}

	public SectionAccessor getAliasesSection() {
		return getSection(ALIASES_SECTION, true);
	}

	public SectionAccessor getScriptsSection() {
		return getSection(SCRIPTS_SECTION, true);
	}

	private SectionAccessor getSection(JsonValue root, String section, boolean createIfDoesntExist) {
		JsonValue tree = null;

		if (createIfDoesntExist) {
			tree = JsonUtils.getOrCreateChild(root, section, ValueType.object);
		}
		else {
			tree = root.get(section);
		}

		return new SectionAccessor(tree);
	}


	public class SectionAccessor {
		public final JsonValue root;

		SectionAccessor(JsonValue tree) {
			this.root = tree;
		}

		public boolean has(String key) {
			return root.has(key);
		}

		public String get(String key) {
			return get(key, false);
		}

		public String get(String key, boolean emptyStringIfDoesntExist) {
			return root.has(key) ? root.get(key).asString() : (emptyStringIfDoesntExist ? "" : null);
		}

		public void set(String key, String value) {
			JsonValue tree = JsonUtils.getOrCreateChild(root, key, ValueType.stringValue);
			tree.set(value);
		}

		public void remove(String key) {
			if (root.has(key)) {
				root.remove(key);
			}
		}

		public void save() {
			Database.this.save();
		}

		public SectionAccessor getSection(String section, boolean createIfDoesntExist) {
			return Database.this.getSection(root, section, createIfDoesntExist);
		}

		// FIXME? This one's name doesn't fit in this local context but I didn't want to create
		// some weird OOP templatish or compositional abstraction for it - for now. :)
		public SectionAccessor getGlobalStorage(String storageName) {
			return Database.this.getSection(content, storageName, true);
		}
	}
}
