package com.girigiri.kwrental.reservation.service.cancel;

@FunctionalInterface
public interface CancelAlerter<T> {
	static <T> CancelAlerter<T> doNothing() {
		return t -> {
		};
	}

	void alert(T t);
}
