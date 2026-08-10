package com.ezzenix.emlib.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * A generic, loader-agnostic event registry.
 */
public class Event<T> {
	// Thread-safe list to prevent ConcurrentModificationExceptions during registration
	private final List<T> listeners = new CopyOnWriteArrayList<>();
	private T invoker;
	private final Function<List<T>, T> invokerFactory;

	private Event(Function<List<T>, T> invokerFactory) {
		this.invokerFactory = invokerFactory;
		// Build the initial, empty invoker
		this.invoker = invokerFactory.apply(this.listeners);
	}

	/**
	 * Creates a new event.
	 * @param invokerFactory A function that dictates how the list of listeners is executed.
	 */
	public static <T> Event<T> create(Function<List<T>, T> invokerFactory) {
		return new Event<>(invokerFactory);
	}

	/**
	 * Registers a listener to this event.
	 */
	public void register(T listener) {
		if (listener == null) throw new IllegalArgumentException("Listener cannot be null");
		this.listeners.add(listener);
		// Rebuild the invoker to include the new listener
		this.invoker = this.invokerFactory.apply(this.listeners);
	}

	/**
	 * Returns the invoker used to fire the event.
	 */
	public T invoker() {
		return this.invoker;
	}
}
