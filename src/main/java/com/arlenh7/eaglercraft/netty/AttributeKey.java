package com.arlenh7.eaglercraft.netty;

import java.util.HashMap;
import java.util.Map;

public final class AttributeKey<T> {

	private static final Map<String, AttributeKey<?>> keys = new HashMap<>();

	private final String name;

	private AttributeKey(String name) {
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	public static <T> AttributeKey<T> valueOf(String name) {
		AttributeKey<?> key = keys.get(name);
		if(key == null) {
			key = new AttributeKey<T>(name);
			keys.put(name, key);
		}
		return (AttributeKey<T>) key;
	}

	public String name() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

}
