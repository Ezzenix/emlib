package com.ezzenix.emlib.event;

@FunctionalInterface
public interface TickCallback {
	Event<TickCallback> EVENT = Event.create(listeners -> () -> {
		for (TickCallback listener : listeners) {
			listener.onTick();
		}
	});

	void onTick();
}
