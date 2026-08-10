package com.ezzenix.emlib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.List;

//? if >=1.21.2 && <=1.21.5
//import net.minecraft.client.renderer.RenderType;

public class EmGraphics {
	private final GuiGraphicsExtractor instance;

	public EmGraphics(GuiGraphicsExtractor instance) {
		this.instance = instance;
	}

	public void pushMatrix() {
		//~ if >=1.21.6 'pushPose' -> 'pushMatrix'
		this.instance.pose().pushMatrix();
	}

	public void popMatrix() {
		//~ if >=1.21.6 'popPose' -> 'popMatrix'
		this.instance.pose().popMatrix();
	}

	public void translate(float x, float y, float z) {
		//~ if >=1.21.6 '(x, y, z)' -> '(x, y)'
		this.instance.pose().translate(x, y);
	}

	public void translate(float x, float y) {
		//~ if >=1.21.6 '(x, y, 0)' -> '(x, y)'
		this.instance.pose().translate(x, y);
	}

	public void scale(float x, float y) {
		//~ if >=1.21.6 '(x, y, 0)' -> '(x, y)'
		this.instance.pose().scale(x, y);
	}

	public void scale(float xy) {
		//~ if >=1.21.6 '(xy, xy, 0)' -> '(xy, xy)'
		this.instance.pose().scale(xy, xy);
	}

	public void text(Font font, Component text, int x, int y, int color, boolean dropShadow) {
		//~ if >=26.1 'drawString(' -> 'text('
		this.instance.text(font, text, x, y, color, dropShadow);
	}

	public void text(Font font, Component text, int x, int y, int color) {
		//~ if >=26.1 'drawString(' -> 'text('
		this.instance.text(font, text, x, y, color, true);
	}

	public void text(Font font, String text, int x, int y, int color, boolean dropShadow) {
		//~ if >=26.1 'drawString(' -> 'text('
		this.instance.text(font, text, x, y, color, dropShadow);
	}

	public void text(Font font, String text, int x, int y, int color) {
		//~ if >=26.1 'drawString(' -> 'text('
		this.instance.text(font, text, x, y, color, true);
	}

	public void centeredText(Font font, String text, int x, int y, int color) {
		//~ if >=26.1 'drawCenteredString(' -> 'centeredText('
		this.instance.centeredText(font, text, x, y, color);
	}

	public void centeredText(Font font, Component text, int x, int y, int color) {
		//~ if >=26.1 'drawCenteredString(' -> 'centeredText('
		this.instance.centeredText(font, text, x, y, color);
	}

	public void centeredText(Font font, FormattedCharSequence text, int x, int y, int color) {
		//~ if >=26.1 'drawCenteredString(' -> 'centeredText('
		this.instance.centeredText(font, text, x, y, color);
	}

	public void item(ItemStack stack, int x, int y) {
		//~ if >=26.1 'renderItem' -> 'item'
		this.instance.item(stack, x, y);
	}

	public void texture(Identifier texture, int x, int y, int width, int height) {
		//? if >=1.21.6
		this.instance.blit(texture, x, y, x + width, y + height, 0.0f, 1.0f, 0.0f, 1.0f);
		//? if >=1.21.2 && <=1.21.5
		//this.instance.blit(RenderType::guiTextured, texture, x, y, 0, 0, width, height, width, height);
		//? if <1.21.2
		//this.instance.blit(texture, x, y, 0, 0, width, height, width, height);
	}

	public void texture(Identifier texture, ScreenRectangle rect) {
		this.texture(texture, rect.left(), rect.top(), rect.width(), rect.height());
	}

	public void fill(int x0, int y0, int x1, int y1, int color) {
		this.instance.fill(x0, y0, x1, y1, color);
	}

	public void rect(int x, int y, int width, int height, int color) {
		this.instance.fill(x, y, x+width, y+height, color);
	}

	public void rect(ScreenRectangle rect, int color) {
		this.rect(rect.left(), rect.top(), rect.width(), rect.height(), color);
	}

	public void outline(int x, int y, int width, int height, int color) {
		this.fill(x, y, x + width, y + 1, color);
		this.fill(x, y + height - 1, x + width, y + height, color);
		this.fill(x, y + 1, x + 1, y + height - 1, color);
		this.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
	}

	public void outline(ScreenRectangle rect, int color) {
		this.outline(rect.left(), rect.top(), rect.width(), rect.height(), color);
	}

	public void tooltip(Font font, List<Component> lines, int x, int y) {
		//~ if >=1.21.6 'renderComponentTooltip' -> 'setComponentTooltipForNextFrame'
		this.instance.setComponentTooltipForNextFrame(Minecraft.getInstance().font, lines, x, y);
	}

}
