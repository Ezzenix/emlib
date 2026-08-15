package com.ezzenix.emlib.util;

import net.minecraft.resources.Identifier;

public class EmId {
	public static Identifier of(String full) {
		Identifier result = Identifier.tryParse(full);
		if (result == null) {
			throw new RuntimeException("Invalid identifier: " + full);
		}
		return result;
	}

	public static Identifier of(String namespace, String path) {
		return of(namespace + ":" + path);
	}

	public static Identifier withDefaultNamespace(String path) {
		return of("minecraft:" + path);
	}

}
