package com.ezzenix.emlib.config;

import com.ezzenix.emlib.EmLib;
import com.ezzenix.emlib.util.EmPort;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;

class EntryInfo {
	public EmConfig.Entry entry;
	public EmConfig.Comment comment;
	public Field field;
	public final Object defaultValue;

	public EntryInfo(Field field) {
		this.field = field;
		this.entry = field.getAnnotation(EmConfig.Entry.class);
		this.comment = field.getAnnotation(EmConfig.Comment.class);
		this.defaultValue = getValue();
	}

	public Component getName(String modId) {
		return Component.translatable(modId + ".config." + this.field.getName());
	}

	public Tooltip getTooltip(String modId) {
		String languageKey = modId + ".config." + this.field.getName() + ".tooltip";
		if (Language.getInstance().has(languageKey)) {
			return Tooltip.create(Component.translatable(languageKey));
		}
		return null;
	}

	public Class<?> getType() {
		if (this.field == null) return null;
		return this.field.getType();
	}

	public Object getValue() {
		try {
			return field.get(null);
		} catch (IllegalAccessException e) {
			EmLib.LOGGER.error("Failed to get config value", e);
			return null;
		}
	}

	public void setValue(Object newValue) {
		try {
			field.set(null, newValue);
		} catch (IllegalAccessException e) {
			EmLib.LOGGER.error("Failed to set config value", e);
		}

		if (EmPort.screen() instanceof ConfigScreen configScreen) {
			configScreen.changed();
		}
	}
}
