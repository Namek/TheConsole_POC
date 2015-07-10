package net.namekdev.theconsole.commands;

import java.util.Map;
import java.util.TreeMap;

import net.namekdev.theconsole.utils.Database;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class AliasManager {
	private Map<String, String> aliases = new TreeMap<String, String>();
	private Array<String> aliasNames = new Array<String>();


	public AliasManager(Database.SectionAccessor aliasStorage) {
		JsonReader reader = new JsonReader();
		JsonValue aliases = reader.parse(aliasStorage.root.asString());

		if (aliases == null) {
			// probably empty
			return;
		}

		JsonValue node = aliases.child;
		while (node != null) {
			put(node.name, node.asString());
			node = node.next;
		}
	}

	public void put(String aliasName, String command) {
		if (!aliases.containsKey(aliasName)) {
			aliasNames.add(aliasName);
			aliasNames.sort();
		}

		aliases.put(aliasName, command);
	}

	/**
	 * Gets code for given alias.
	 *
	 * @return returns {@code null} if there is no alias of given name
	 */
	public String get(String aliasName) {
		return aliases.get(aliasName);
	}

	public void remove(String aliasName, String command) {
		aliasNames.removeValue(aliasName, false);
		aliases.remove(aliasName);
	}

	public boolean has(String aliasName) {
		return aliases.containsKey(aliasName);
	}

	public void findAliasesStartingWith(String aliasNamePart, Array<String> outAliases) {
		if (aliasNamePart.length() == 0) {
			return;
		}

		for (String aliasName : aliasNames) {
			if (aliasName.indexOf(aliasNamePart) == 0) {
				outAliases.add(aliasName);
			}
		}
	}

	public Array<String> getAllAliasNames() {
		return aliasNames;
	}

	public int getAliasCount() {
		return aliasNames.size;
	}
}
