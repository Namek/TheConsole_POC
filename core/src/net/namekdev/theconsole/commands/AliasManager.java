package net.namekdev.theconsole.commands;

import java.util.Map;
import java.util.TreeMap;

import com.badlogic.gdx.utils.Array;

public class AliasManager {
	private Map<String, String> aliases = new TreeMap<String, String>();
	private Array<String> aliasNames = new Array<String>();


	public AliasManager() {
		// TODO read saved aliases

		// TODO Test only
		put("wiki", "wikipedia");
		put("cls", "clear");
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
