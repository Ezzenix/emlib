package com.ezzenix.emlib.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

//~ if >=1.21.11 ' Button ' -> ' Button.Plain '
public class IconButtonWidget extends Button.Plain {
	private final Identifier sprite;
	private Identifier hoverSprite;
	private final int spriteSize;
	private boolean showBackground = true;

	public IconButtonWidget(int x, int y, OnPress onPress, Identifier sprite, int spriteSize, Component message) {
		super(x, y, 20, 20, message, onPress, supplier -> Component.empty().append(message));
		this.sprite = sprite;
		this.hoverSprite = sprite;
		this.spriteSize = spriteSize;
	}

	public IconButtonWidget(OnPress onPress, Identifier sprite, int spriteSize, Component message) {
		this(0, 0, onPress, sprite, spriteSize, message);
	}

	public void setHoverSprite(Identifier hoverSprite) {
		this.hoverSprite = hoverSprite;
	}

	public void setShowBackground(boolean show) {
		this.showBackground = show;
	}

	@Override
	//? if >=1.21.11 {
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		if (this.showBackground) {
			//~ if >=26.1 'render' -> 'extract'
			super.extractDefaultSprite(graphics);
		}
	//? } else {
	/*protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		if (this.showBackground) {
			super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
		}
	*///? }

		EmGraphics em = new EmGraphics(graphics);
		if (!this.showBackground && this.isMouseOver(mouseX, mouseY)) {
			em.outline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xffffffff);
		}
		int padding = (this.getWidth() - this.spriteSize) / 2;
		Identifier sprite = EmPort.containsPoint(this.getRectangle(), mouseX, mouseY) ? this.hoverSprite : this.sprite;
		em.texture(sprite, this.getX() + padding, this.getY() + padding, this.getWidth() - padding*2, this.getHeight() - padding*2);
	}

	//? if <1.21.11 {
	/*@Override
	public void renderString(GuiGraphicsExtractor graphics, Font font, int i) { }
	*///? }

}
