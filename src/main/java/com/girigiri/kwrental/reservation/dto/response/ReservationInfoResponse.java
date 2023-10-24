package com.girigiri.kwrental.reservation.dto.response;

import com.girigiri.kwrental.reservation.domain.entity.Reservation;

public record ReservationInfoResponse(
	Long id,
	String name,
	String phoneNumber,
	String email) {
	public static ReservationInfoResponse from(final Reservation reservation) {
		return new ReservationInfoResponse(reservation.getId(), reservation.getName(), reservation.getPhoneNumber(),
			reservation.getEmail());
	}
}
