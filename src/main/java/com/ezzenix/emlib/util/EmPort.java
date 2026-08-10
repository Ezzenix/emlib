package com.ezzenix.emlib.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

//? if >=26.1 {
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
//? }

//~ if >1.20.1 'TextAndImageButton' -> 'SpriteIconButton'
import net.minecraft.client.gui.components.SpriteIconButton;

public class EmPort {

	//~ if >1.20.1 'TextAndImageButton' -> 'SpriteIconButton'
	public static SpriteIconButton createIconButton(Component message, Button.OnPress onPress, Identifier sprite, int spriteSize, int size) {
		//? if >1.20.1 {
		SpriteIconButton button = SpriteIconButton.builder(message,
			//?} else {
			/*TextAndImageButton button = TextAndImageButton.builder(message, sprite,
			 *///?}
			onPress
			//? if >1.20.1 {
			, true).sprite(sprite, spriteSize, spriteSize).size(size, size).build();
		//?} else {
		/*).textureSize(spriteSize, spriteSize).usedTextureSize(spriteSize, spriteSize).offset(0, 0).build();
		button.setWidth(size);
		*///?}
		return button;
	}

	public static Screen screen() {
		//? if >=26.2 {
		return Minecraft.getInstance().gui.screen();
		//?} else
		//return Minecraft.getInstance().screen;
	}

	public static void screen(Screen screen) {
		//? if >=26.2 {
		Minecraft.getInstance().gui.setScreen(screen);
		//?} else
		//Minecraft.getInstance().setScreen(screen);
	}

	public static long window() {
		//? if >=1.21.9 {
		return Minecraft.getInstance().getWindow().handle();
		//?} else
		//return Minecraft.getInstance().getWindow().getWindow();
	}

	public static boolean isKeyDown(int key) {
		//? if >=1.21.9 {
		return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), key);
		//?} else
		//return InputConstants.isKeyDown(window(), key);
	}

	public static long dayTime(Level level) {
		//? if >=26.1 {
		Holder<WorldClock> overworldClock = level.registryAccess()
			.lookupOrThrow(Registries.WORLD_CLOCK)
			.getOrThrow(WorldClocks.OVERWORLD);
		long totalTicks = level.clockManager().getTotalTicks(overworldClock);
		return totalTicks % 24000;
		//? } else {
		/*return level.getDayTime() % 24000;
		*///? }
	}

	public static double tickRate(Level level) {
		//? if >=1.20.3 {
		return level.tickRateManager().tickrate();
		//? } else
		//return 20;
	}

	public static void sendOverlay(Component component) {
		//? if >=26.2 {
		ChatListener chatListener = Minecraft.getInstance().gui.chatListener();
		//? } else
		//ChatListener chatListener = Minecraft.getInstance().getChatListener();

		//? if >=26.1 {
		chatListener.handleOverlay(component);
		//? } else
		//chatListener.handleSystemMessage(component, true);
	}

	public static boolean containsPoint(ScreenRectangle rect, double x, double y) {
		return containsPoint(rect, (int)x, (int)y);
	}

	public static boolean containsPoint(ScreenRectangle rect, int x, int y) {
		return x >= rect.left() && x < rect.right() && y >= rect.top() && y < rect.bottom();
	}

}
