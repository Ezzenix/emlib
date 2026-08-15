package com.ezzenix.emlib.util;

public class SmoothColor {
	private final float speed;

	private int value = Integer.MAX_VALUE;

	public SmoothColor(float speed) {
		this.speed = speed;
	}

	public void update(int target, float tickDelta) {
		if (this.value == Integer.MAX_VALUE) this.value = target;
		float adaptiveSpeed = 1.0f - (float)Math.pow(1.0f - this.speed, tickDelta);
		this.value = ColorUtil.lerp(adaptiveSpeed, this.value, target);
	}

	public int get() {
		return this.value;
	}
}
