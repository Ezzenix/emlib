package com.ezzenix.emlib.event;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class EmEvents {
	public static final Event<TickListener> TICK = new Event<>();
	public static final Event<HudRenderListener> HUD_RENDER = new Event<>();

	static void postTick() {
		TICK.post(TickListener::invoke);
	}

	static void postHudRender(GuiGraphicsExtractor graphics, float tickDelta) {
		HUD_RENDER.post(listener -> {
			listener.invoke(graphics, tickDelta);
		});
	}

	@FunctionalInterface
	public interface TickListener {
		void invoke();
	}

	@FunctionalInterface
	public interface HudRenderListener {
		void invoke(GuiGraphicsExtractor graphics, float partialTick);
	}
}
