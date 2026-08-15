package com.ezzenix.emlib.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class Event<T> {
	private final List<T> listeners = new ArrayList<>();

	public void register(T listener) {
		if (listener == null) throw new IllegalArgumentException("Listener cannot be null");
		listeners.add(listener);
	}

	public void post(Consumer<T> invoker) {
		listeners.forEach(invoker);
	}
}
