package com.ezzenix.emlib.util;

import net.minecraft.util.Mth;

public class ColorUtil {
	public static int hsvToRGBA(float h, float s, float v, float a){
		int rgb = java.awt.Color.HSBtoRGB(h, s, v);
		int alpha = (int)(a * 255) << 24;
		return alpha | (rgb & 0xFFFFFF);
	}

	public static float[] RGBAToHsv(int color){
		float alpha = ((color >> 24) & 0xFF) / 255f;

		float r = ((color >> 16) & 0xFF) / 255f;
		float g = ((color >> 8) & 0xFF) / 255f;
		float b = (color & 0xFF) / 255f;

		float max = Math.max(r, Math.max(g, b));
		float min = Math.min(r, Math.min(g, b));

		float delta = max - min;

		float hue = 0f;
		float saturation;

		if (max == 0) {
			saturation = 0;
		} else {
			saturation = delta / max;
		}

		if (delta != 0) {
			if (max == r) {
				hue = ((g - b) / delta) % 6;
			} else if (max == g) {
				hue = ((b - r) / delta) + 2;
			} else {
				hue = ((r - g) / delta) + 4;
			}

			hue /= 6f;
			if (hue < 0)
				hue += 1f;
		}

		return new float[]{ hue, saturation, max, alpha };
	}

	public static String toHexString(int color) {
		return toHexString(color, true);
	}

	public static String toHexString(int color, boolean includeAlpha) {
		if (includeAlpha) {
			return String.format("#%08x", color);
		} else {
			return String.format("#%06x", color & 0xFFFFFF);
		}
	}

	public static int lerp(float t, int from, int to) {
		int a = (int) Mth.lerp(t, (from >> 24) & 0xFF, (to >> 24) & 0xFF);
		int r = (int) Mth.lerp(t, (from >> 16) & 0xFF, (to >> 16) & 0xFF);
		int g = (int) Mth.lerp(t, (from >> 8) & 0xFF, (to >> 8) & 0xFF);
		int b = (int) Mth.lerp(t, from & 0xFF, to & 0xFF);

		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	public static int adjustBrightness(int color, float factor) {
		int alpha = (color >> 24) & 0xFF;
		int red = (color >> 16) & 0xFF;
		int green = (color >> 8) & 0xFF;
		int blue = color & 0xFF;

		red = clamp((int) (red * factor));
		green = clamp((int) (green * factor));
		blue = clamp((int) (blue * factor));

		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	private static int clamp(int value) {
		return Math.max(0, Math.min(255, value));
	}
}
