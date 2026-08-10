package com.ezzenix.emlib.util;

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
		return String.format("#%08x", color);
	}

	public static int changeBrightness(int color, float factor) {
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
