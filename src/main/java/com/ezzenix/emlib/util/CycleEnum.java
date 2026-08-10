package com.ezzenix.emlib.util;

public interface CycleEnum<T extends Enum<T>> {
	default T next() {
		T[] values = values();
		int nextIndex = (ordinal() + 1) % values.length;
		return values[nextIndex];
	}

	default T previous() {
		T[] values = values();
		int previousIndex = ordinal() - 1;

		if (previousIndex < 0) {
			previousIndex = values.length - 1;
		}

		return values[previousIndex];
	}

	@SuppressWarnings("unchecked")
	private T[] values() {
		return (T[]) ((Enum<?>) this).getDeclaringClass().getEnumConstants();
	}

	int ordinal();
}
