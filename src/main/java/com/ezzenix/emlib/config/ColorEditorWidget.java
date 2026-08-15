package com.ezzenix.emlib.config;

import com.ezzenix.emlib.util.EmGraphics;
import com.ezzenix.emlib.util.EmId;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

import static com.ezzenix.emlib.EmLib.MOD_ID;
import static com.ezzenix.emlib.util.ColorUtil.RGBAToHsv;
import static com.ezzenix.emlib.util.ColorUtil.hsvToRGBA;
import static com.ezzenix.emlib.util.EmPort.containsPoint;

//? if >=1.21.9
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Mth;

public class ColorEditorWidget extends AbstractWidget {
	private static final Identifier colorTextureId = EmId.of(MOD_ID, "color_texture");
	private static DynamicTexture colorTexture = null;
	private static final Identifier hueTextureId = EmId.of(MOD_ID, "hue_texture");
	private static DynamicTexture hueTexture = null;
	private static final Identifier alphaTextureId = EmId.of(MOD_ID, "alpha_texture");
	private static DynamicTexture alphaTexture = null;

	private Type dragType = null;

	private float hue = 0f;
	private float saturation = 1f;
	private float value = 1f;
	private float alpha = 1f;

	private boolean allowAlpha;

	private Consumer<Integer> responder;

	public ColorEditorWidget(int x, int y, int width, int height, int color, boolean allowAlpha) {
		super(x, y, width, height, Component.literal("Color editor"));
		fromRGBA(color);
		invalidateAllTextures();
		this.allowAlpha = allowAlpha;
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		EmGraphics em = new EmGraphics(graphics);
		em.pushMatrix();
		em.translate(0, 0, 10);
		em.rect(this.getRectangle(), 0xff000000);
		em.outline(this.getRectangle(), 0xd2454545);

		drawColorRect(graphics);
		drawHueRect(graphics);
		if (this.allowAlpha) {
			drawAlphaRect(graphics);
		}

		em.popMatrix();
	}

	public void setResponder(Consumer<Integer> responder) {
		this.responder = responder;
	}

	protected void drawColorRect(GuiGraphicsExtractor graphics) {
		if (colorTexture == null)
			createColorTexture();

		ScreenRectangle rect = this.colorRect();
		EmGraphics em = new EmGraphics(graphics);
		em.texture(colorTextureId, rect);

		int x = rect.left() + (int)(saturation * rect.width());
		int y = rect.top() + (int)((1f - value) * rect.height());

		em.fill(
			x-1,
			y-1,
			x+1,
			y+1,
			0xffffffff
		);
	}

	protected void drawHueRect(GuiGraphicsExtractor graphics) {
		if (hueTexture == null)
			createHueTexture();

		ScreenRectangle rect = this.hueRect();
		EmGraphics em = new EmGraphics(graphics);
		em.texture(hueTextureId, rect);

		int y = rect.top() + (int)(hue * rect.height());
		em.fill(
			rect.left()-1,
			y-1,
			rect.right()+1,
			y,
			0xffffffff
		);
	}

	private void drawAlphaRect(GuiGraphicsExtractor graphics) {
		if (alphaTexture == null)
			createAlphaTexture();

		ScreenRectangle rect = this.alphaRect();
		EmGraphics em = new EmGraphics(graphics);
		em.texture(alphaTextureId, rect);

		int x = rect.left() + (int)(alpha * rect.width());
		em.fill(
			x-1,
			rect.top()-1,
			x,
			rect.bottom()+1,
			0xffffffff
		);
	}

	private static final int padding = 5;

	private ScreenRectangle colorRect() {
		return new ScreenRectangle(
			getX()+padding,
			getY()+padding,
			getWidth()-padding*3-15,
			getHeight()-(this.allowAlpha ? padding*3+10 : padding*2)
		);
	}

	private ScreenRectangle hueRect() {
		return new ScreenRectangle(
			colorRect().right()+padding,
			colorRect().top(),
			15,
			colorRect().height()
		);
	}

	private ScreenRectangle alphaRect() {
		if (!this.allowAlpha) return new ScreenRectangle(0, 0, 0, 0);
		return new ScreenRectangle(
			getX()+padding,
			colorRect().bottom()+padding,
			colorRect().width()+padding+15,
			10
		);
	}

	private int getCurrentColor() {
		return hsvToRGBA(hue, this.saturation, this.value, this.allowAlpha ? this.alpha : 1f);
	}

	private boolean updateFromMouse(double mouseX, double mouseY, boolean isClick) {
		if (dragType == Type.COLOR || (isClick && containsPoint(colorRect(), mouseX, mouseY))) {
			ScreenRectangle rect = this.colorRect();
			float sat = (float)((mouseX - rect.left()) / rect.width());
			float value = 1f - (float)((mouseY - rect.top()) / rect.height());
			this.saturation = Mth.clamp(sat, 0f, 1f);
			this.value = Mth.clamp(value, 0f, 1f);
			invalidateAlphaTexture();
			if (this.responder != null) this.responder.accept(this.getCurrentColor());
			return true;
		}

		if (dragType == Type.HUE || (isClick && containsPoint(hueRect(), mouseX, mouseY))) {
			ScreenRectangle rect = hueRect();
			hue = (float)((mouseY - rect.top()) / rect.height());
			hue = Mth.clamp(hue, 0f, 1f);
			invalidateColorTexture();
			invalidateAlphaTexture();
			if (this.responder != null) this.responder.accept(this.getCurrentColor());
			return true;
		}

		if (dragType == Type.ALPHA || (isClick && containsPoint(alphaRect(), mouseX, mouseY)) && this.allowAlpha) {
			ScreenRectangle rect = alphaRect();
			alpha = (float)((mouseX - rect.left()) / rect.width());
			alpha = Mth.clamp(alpha, 0f, 1f);
			if (this.responder != null) this.responder.accept(this.getCurrentColor());
			return true;
		}

		return false;
	}

	@Override
	//? if >=1.21.9 {
	public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
		double x = event.x();
		double y = event.y();
	//? } else {
	/*public boolean mouseClicked(double x, double y, int button) {
	*///? }
		if (containsPoint(colorRect(), x, y)) {
			this.dragType = Type.COLOR;
		} else if (containsPoint(hueRect(), x, y)) {
			this.dragType = Type.HUE;
		} else if (containsPoint(alphaRect(), x, y)) {
			this.dragType = Type.ALPHA;
		}
		return updateFromMouse(x, y, true);
	}

	@Override
	//? if >=1.21.9 {
	public boolean mouseReleased(MouseButtonEvent event) {
	//? } else
	//public boolean mouseReleased(double x, double y, int button) {
		this.dragType = null;
		return true;
	}

	@Override

	//? if >=1.21.9 {
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
		double x = event.x();
		double y = event.y();
	//? } else {
	/*public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
	*///? }
		return updateFromMouse(x, y, false);
	}

	private void createColorTexture() {
		colorTexture = createTexture(colorRect(), colorTextureId, "color_texture", (image, x, y, w, h) -> {
			float sat = x / (float)(w - 1);
			float value = 1f - y / (float)(h - 1);
			int color = hsvToRGBA(hue, sat, value, 1);
			//~ if >=1.21.2 'setPixelRGBA' -> 'setPixel'
			image.setPixel(x, y, color);
		});
	}

	private void createHueTexture() {
		hueTexture = createTexture(hueRect(), hueTextureId, "hue_texture", (image, x, y, w, h) -> {
			float hue = y / (float)(h - 1);
			int color = hsvToRGBA(hue, 1, 1, 1);
			//~ if >=1.21.2 'setPixelRGBA' -> 'setPixel'
			image.setPixel(x, y, color);
		});
	}

	private void createAlphaTexture() {
		alphaTexture = createTexture(alphaRect(), alphaTextureId, "alpha_texture", (image, x, y, w, h) -> {
			float a = x / (float)(w - 1);
			int color = hsvToRGBA(hue, saturation, value, a);
			//~ if >=1.21.2 'setPixelRGBA' -> 'setPixel'
			image.setPixel(x, y, color);
		});
	}

	private DynamicTexture createTexture(ScreenRectangle rect, Identifier identifier, String name, ImagePixelConsumer consumer) {
		int w = rect.width();
		int h = rect.height();

		NativeImage image = new NativeImage(w, h, false);

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				consumer.accept(image, x, y, w, h);
			}
		}

		//~ if >=1.21.5 'image' -> '() -> name, image'
		DynamicTexture texture = new DynamicTexture(() -> name, image);
		Minecraft.getInstance().getTextureManager().register(identifier, texture);
		return texture;
	}

	private void fromRGBA(int color){
		float[] args = RGBAToHsv(color);
		this.hue = args[0];
		this.saturation = args[1];
		this.value = args[2];
		this.alpha = this.allowAlpha ? args[3] : 1f;
	}

	public static void invalidateAllTextures() {
		invalidateColorTexture();
		invalidateHueTexture();
		invalidateAlphaTexture();
	}

	private static void invalidateColorTexture() {
		if (colorTexture != null) {
			colorTexture.close();
			colorTexture = null;
		}
	}

	private static void invalidateHueTexture() {
		if (hueTexture != null) {
			hueTexture.close();
			hueTexture = null;
		}
	}

	private static void invalidateAlphaTexture() {
		if (alphaTexture != null) {
			alphaTexture.close();
			alphaTexture = null;
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {

	}

	@FunctionalInterface
	private interface ImagePixelConsumer {
		void accept(NativeImage image, int x, int y, int w, int h);
	}

	private enum Type {
		COLOR,
		HUE,
		ALPHA;
	}

}
