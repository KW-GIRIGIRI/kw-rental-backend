package com.girigiri.kwrental.reservation.service;

import com.girigiri.kwrental.reservation.domain.entity.Reservation;

@FunctionalInterface
public interface ReserveValidator {
	static ReserveValidator noExtraValidation() {
		return reservation -> {
		};
	}

	void validate(Reservation reservation);
}
