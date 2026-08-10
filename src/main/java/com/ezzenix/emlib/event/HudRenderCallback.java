package com.ezzenix.emlib.event;

import net.minecraft.client.gui.GuiGraphicsExtractor;

@FunctionalInterface
public interface HudRenderCallback {
	Event<HudRenderCallback> EVENT = Event.create(listeners -> (guiGraphics, tickDelta) -> {
		for (HudRenderCallback listener : listeners) {
			listener.onHudRender(guiGraphics, tickDelta);
		}
	});

	void onHudRender(GuiGraphicsExtractor guiGraphics, float tickDelta);
}
