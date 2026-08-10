package com.ezzenix.emlib.config;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.regex.Pattern;

//? if >=1.21.9
import net.minecraft.client.input.KeyEvent;

class ConfigEditBoxWidget extends EditBox {
	private static final Pattern INTEGER_ONLY = Pattern.compile("(-?[0-9]*)");
	private static final Pattern DECIMAL_ONLY = Pattern.compile("-?(\\d+\\.?\\d*|\\d*\\.?\\d+|\\.)");

	private final EntryInfo info;
	private final EmConfig.Entry e;

	private Pattern pattern = null;
	private String lastValidText = "";
	private boolean isUpdating = false;

	public ConfigEditBoxWidget(int x, int y, int width, int height, EntryInfo info) {
		super(Minecraft.getInstance().font, x, y, width, height, Component.empty());
		this.e = info.entry;
		this.info = info;

		if (info.getType() == int.class) {
			this.pattern = INTEGER_ONLY;
		} else if (info.getType() == float.class || info.getType() == double.class) {
			this.pattern = DECIMAL_ONLY;
		}

		this.lastValidText = String.valueOf(info.getValue());
		this.setValue(this.lastValidText);

		this.setResponder(this::textChanged);
	}

	private void textChanged(String text) {
		if (isUpdating) return;

		String trimmed = text.trim();

		boolean isIntermediate = trimmed.isEmpty() || trimmed.equals("-") || trimmed.equals(".") || trimmed.equals("-.");

		if (this.pattern != null && !isIntermediate && !this.pattern.matcher(trimmed).matches()) {
			this.isUpdating = true;
			int cursorPos = this.getCursorPosition();
			this.setValue(this.lastValidText);
			this.setCursorPosition(Math.max(0, cursorPos - 1));
			this.isUpdating = false;
			return;
		}

		this.lastValidText = text;

		boolean redText = false;

		try {
			if (info.getType() == int.class) {
				int value = Integer.parseInt(trimmed);
				int min = (int) e.min();
				int max = (int) e.max();
				if (value < min || value > max) redText = true;
				info.setValue(Mth.clamp(value, min, max));

			} else if (info.getType() == float.class) {
				float value = Float.parseFloat(trimmed);
				float min = (float) e.min();
				float max = (float) e.max();
				if (value < min || value > max) redText = true;
				info.setValue(Mth.clamp(value, min, max));

			} else if (info.getType() == double.class) {
				double value = Double.parseDouble(trimmed);
				double min = e.min();
				double max = e.max();
				if (value < min || value > max) redText = true;
				info.setValue(Mth.clamp(value, min, max));

			} else {
				info.setValue(trimmed);
			}
		} catch (NumberFormatException ignored) {
			return;
		}

		this.setTextColor(!redText ? -2039584 : 0xffff5555);

	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		if (!focused) {
			this.isUpdating = true;
			this.setValue(String.valueOf(info.getValue()));
			this.setTextColor(-2039584);
			this.isUpdating = false;
		}
	}

	private boolean onEnterPressed() {
		this.setFocused(false);
		return true;
	}

	@Override
	//? if >=1.21.9 {
	public boolean keyPressed(KeyEvent event) {
		if (event.isConfirmation()) {
			return onEnterPressed();
		}
		return super.keyPressed(event);
	//? } else {
	/*public boolean keyPressed(int key, int scanCode, int modifiers) {
		if (key == InputConstants.KEY_RETURN || key == InputConstants.KEY_NUMPADENTER) {
			return onEnterPressed();
		}
		return super.keyPressed(key, scanCode, modifiers);
	*///? }
	}
}
