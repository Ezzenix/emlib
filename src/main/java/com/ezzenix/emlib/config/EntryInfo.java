package com.ezzenix.emlib.config;

import com.ezzenix.emlib.EmLib;
import com.ezzenix.emlib.util.EmPort;
import com.ezzenix.emlib.util.Platform;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;

class EntryInfo {
	public EmConfig.Option option;
	public EmConfig.Comment comment;
	public EmConfig.Requires[] requires;
	public Field field;
	public String modId;
	public final Object defaultValue;
	public boolean locked = false;

	public EntryInfo(Field field, String modId) {
		this.field = field;
		this.field.setAccessible(true);
		this.modId = modId;
		this.option = field.getAnnotation(EmConfig.Option.class);
		this.comment = field.getAnnotation(EmConfig.Comment.class);
		this.requires = field.getAnnotationsByType(EmConfig.Requires.class);
		this.defaultValue = getValue();
	}

	public String getId() {
		return this.field.getName();
	}

	public Component getName() {
		return Component.translatable(modId + ".config." + this.field.getName());
	}

	public Tooltip getTooltip() {
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

	public void updateLocked() {
		boolean wasLocked = this.locked;
		this.locked = false;

		for (EmConfig.Requires require : this.requires) {
			if (!require.modId().isEmpty() && !Platform.isModLoaded(require.modId())) {
				this.locked = true;
				break;
			}
			if (!require.option().isEmpty() && !require.value().isEmpty()) {
				for (EntryInfo info : EmConfig.instances.get(this.modId).entries) {
					if (info.getId().equalsIgnoreCase(require.option())) {
						if (!info.getValue().toString().equalsIgnoreCase(require.value())) {
							this.locked = true;
							break;
						}
					}
				}
			}
		}

		if (wasLocked != this.locked) {
			EmConfig.instances.get(this.modId).needsScreenUpdate = true;
		}
	}

	public Object getValue() {
		try {
			return field.get(EmConfig.instances.get(this.modId));
		} catch (IllegalAccessException e) {
			EmLib.LOGGER.error("Failed to get config value", e);
			return null;
		}
	}

	public void setValue(Object newValue) {
		try {
			field.set(EmConfig.instances.get(this.modId), newValue);
		} catch (IllegalAccessException e) {
			EmLib.LOGGER.error("Failed to set config value", e);
		}

		if (EmPort.screen() instanceof ConfigScreen configScreen) {
			configScreen.changed();
			EmConfig.instances.get(this.modId).entries.forEach(EntryInfo::updateLocked);
		}
	}
}
