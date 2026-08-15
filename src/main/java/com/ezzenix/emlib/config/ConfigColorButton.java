package com.ezzenix.emlib.config;

import com.ezzenix.emlib.util.EmGraphics;
import com.ezzenix.emlib.util.widget.ColorEditorWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import static com.ezzenix.emlib.util.ColorUtil.toHexString;

//~ if >=1.21.11 ' Button ' -> ' Button.Plain '
class ConfigColorButton extends Button.Plain {
	private final EntryInfo info;
	private final ColorEditorWidget editor;
	private final ConfigScreen screen;

	public ConfigColorButton(int x, int y, int width, int height, EntryInfo info, ColorEditorWidget editor, ConfigScreen screen) {
		super(x, y, width, height, Component.literal(toHexString((int)info.getValue(), info.option.allowAlpha())), b -> {
			if (screen.activeColorEditor == editor) {
				screen.activeColorEditor = null;
			} else {
				screen.activeColorEditor = editor;
				ColorEditorWidget.invalidateAllTextures();
			}
		}, Button.DEFAULT_NARRATION);

		this.info = info;
		this.editor = editor;
		this.screen = screen;

		editor.setResponder(color -> {
			info.setValue(color);
			this.setMessage(Component.literal(toHexString(color, info.option.allowAlpha())));
		});
	}

	@Override
	public void setY(int y) {
		super.setY(y);

		if (this.screen.activeColorEditor != this.editor) return;

		int bottomY = y + this.getHeight() + editor.getHeight();
		if (bottomY > Minecraft.getInstance().getWindow().getGuiScaledHeight()) {
			this.editor.setY(y - editor.getHeight());
		} else {
			this.editor.setY(y + this.getHeight());
		}
	}

	@Override
	//? if >=26.1 {
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractContents(graphics, mouseX, mouseY, a);
	//? } else if >=1.21.11 {
	/*protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractContents(graphics, mouseX, mouseY, a);
	*///? } else {
	/*protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
	*///? }
		EmGraphics em = new EmGraphics(graphics);
		em.rect(this.getX() + 16, this.getY() + 5, 10, 10, (int)info.getValue());
		em.outline(this.getX() + 16, this.getY() + 5, 10, 10, 0xffffffff);
	}

}
