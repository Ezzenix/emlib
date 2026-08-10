//? if fabric {
package com.ezzenix.emlib.impl;

import com.ezzenix.emlib.EmLib;
import com.ezzenix.emlib.config.EmConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

//? if >=26.1
import com.terraformersmc.modmenu.util.NullScreenFactory;

import java.util.HashMap;
import java.util.Map;

public class ModMenuImpl implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		if (EmConfig.instances.containsKey(EmLib.MOD_ID)) {
			return (parent) -> EmConfig.getScreen(parent, EmLib.MOD_ID);
		}
		//? if >=26.1 {
		return new NullScreenFactory<>();
		//? } else {
		/*return p -> null;
		*///? }
	}

	@Override
	public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
		HashMap<String, ConfigScreenFactory<?>> map = new HashMap<>();
		EmConfig.instances.forEach((modId, c) -> {
			map.put(modId, parent -> EmConfig.getScreen(parent, modId));
		});
		return map;
	}
}
//? }
