package com.ezzenix.emlib.mixin;

import com.ezzenix.emlib.event.HudRenderCallback;
import com.ezzenix.emlib.util.EmPort;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//~ if >=26.2 'Gui' -> 'Hud'
import net.minecraft.client.gui.Hud;

//? if >=1.21
import net.minecraft.client.DeltaTracker;

//~ if >=26.2 'Gui' -> 'Hud'
@Mixin(Hud.class)
public class HudMixin {
	@Inject(method = "extractRenderState", at=@At("RETURN"))
	//? if >=1.21 {
	private void onRenderHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		float f = deltaTracker.getGameTimeDeltaTicks();
	//? } else {
	/*private void onRenderHud(GuiGraphicsExtractor graphics, float f, CallbackInfo ci) {
	*///? }

		//? if >=26.2 {
		boolean hideGui = ((Hud)(Object)this).isHidden();
		//? } else
		//boolean hideGui = Minecraft.getInstance().options.hideGui;

		Screen screen = EmPort.screen();
		if (screen instanceof LevelLoadingScreen) return;

		if (!hideGui || screen != null) {
			HudRenderCallback.EVENT.invoker().onHudRender(graphics, f);
		}
	}
}
