package com.ezzenix.emlib.util;

import net.minecraft.util.Mth;

public class SmoothFloat {
	private final float speed;

	private float value = Float.MAX_VALUE;

	public SmoothFloat(float speed) {
		this.speed = speed;
	}

	public void update(float target, float tickDelta) {
		if (this.value == Float.MAX_VALUE) this.value = target;
		float adaptiveSpeed = 1.0f - (float)Math.pow(1.0f - this.speed, tickDelta);
		this.value = Mth.lerp(adaptiveSpeed, this.value, target);
	}

	public float get() {
		return this.value;
	}
}
