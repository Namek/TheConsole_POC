package net.namekdev.theconsole.utils;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.ValueType;

public abstract class JsonUtils {
	public static JsonValue getOrCreateChild(JsonValue root, String key, ValueType type) {
		JsonValue tree = root.get(key);

		if (tree == null) {
			tree = new JsonValue(type);
			tree.setName(key);

			if (root.size == 0) {
				root.child = tree;
			}
			else {
				JsonValue prev = root.get(root.size - 1);
				prev.setNext(tree);
				tree.setPrev(prev);
			}

			++root.size;
		}

		return tree;
	}
}
