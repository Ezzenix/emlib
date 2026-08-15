package com.ezzenix.emlib.config;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

class ConfigSliderWidget extends AbstractSliderButton {
	private final EntryInfo info;
	private final EmConfig.Option option;

	public ConfigSliderWidget(int x, int y, int width, int height, double value, EntryInfo info) {
		super(x, y, width, height, Component.empty(), Math.min(Math.max(value, 0), 1));
		this.option = info.option;
		this.info = info;

		this.setMessage(buildLabel());
	}

	private Component buildLabel() {
		if (info.option.offText() && String.valueOf(info.getValue()).equals("0")) {
			return Component.literal("OFF");
		}
		return Component.literal(info.getValue() + info.option.suffix());
	}

	@Override
	protected void updateMessage() {
		setMessage(buildLabel());
	}

	@Override
	protected void applyValue() {
		if (info.getType() == int.class) info.setValue(((Number) (option.min() + value * (option.max() - option.min()))).intValue());
		else if (info.getType() == double.class)
			info.setValue(Math.round((option.min() + value * (option.max() - option.min())) * (double) option.precision()) / (double) option.precision());
		else if (info.getType() == float.class)
			info.setValue(Math.round((option.min() + value * (option.max() - option.min())) * (float) option.precision()) / (float) option.precision());
	}

}
