package net.namekdev.theconsole.scripts.core;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.ValueType;

import net.namekdev.theconsole.commands.AliasManager;
import net.namekdev.theconsole.scripts.ConsoleProxy;
import net.namekdev.theconsole.scripts.IScript;
import net.namekdev.theconsole.scripts.JsUtilsProvider;
import net.namekdev.theconsole.utils.Database;
import net.namekdev.theconsole.utils.JsonUtils;

public class AliasScript implements IScript {
	protected AliasManager aliasManager;
	protected Database.SectionAccessor storage;
	protected JsUtilsProvider utils;
	protected ConsoleProxy console;


	private final static String USAGE_INFO = "Usage:\n" +
		" - alias list\n" +
		" - alias remove <alias>\n" +
		" - alias <alias> <command> [param, [param, [...]]]";


	private Array<String> tmpArray = new Array<String>(true, 18, String.class);


	public AliasScript(AliasManager aliasManager, Database.SectionAccessor storage, JsUtilsProvider utils, ConsoleProxy console) {
		this.aliasManager = aliasManager;
		this.storage = storage;
		this.utils = utils;
		this.console = console;
	}

	@Override
	public Object run(String[] args) {
		utils.assertInfo(args.length != 0, USAGE_INFO);

		JsonReader reader = new JsonReader();
		JsonValue aliases = reader.parse(storage.root.asString());

		if (aliases == null) {
			aliases = new JsonValue(ValueType.object);
		}

		boolean shouldSave = true;

		if (args[0].equals("remove")) {
			utils.assertError(args.length == 2, "Usage: alias remove <name>");

			aliases.remove(args[1]);
		}
		else if (args[0].equals("list")) {
			utils.assertInfo(aliases.size > 0, "There is no even a single alias!");

			shouldSave = false;

			tmpArray.size = 0;
			tmpArray.ensureCapacity(aliases.size);

			JsonValue node = aliases.child;
			while (node != null) {
				tmpArray.add(node.name);
				node = node.next;
			}
			tmpArray.sort();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < tmpArray.size; ++i) {
				String name = tmpArray.items[i];
				JsonValue alias = aliases.get(name);

				sb.append(name);
				sb.append(": ");
				sb.append(alias.asString());
			}
			console.log(sb.toString());
		}
		else {
			utils.assertInfo(args.length >=2, USAGE_INFO);

			String aliasName = args[0];
			String aliasValue = utils.argsToString(args, 1);

			JsonUtils.getOrCreateChild(aliases, aliasName, ValueType.stringValue).set(aliasValue);

			aliasManager.put(aliasName, aliasValue);
		}

		if (shouldSave) {
			storage.root.set(aliases.toString());
			storage.save();
		}

		return null;
	}

}
