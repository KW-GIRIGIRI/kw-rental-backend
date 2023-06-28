package com.girigiri.kwrental.reservation.dto.response;

import java.util.List;

import com.girigiri.kwrental.reservation.domain.Reservation;

public record RelatedReservationsInfoResponse(
	List<ReservationInfoResponse> reservations) {

	public static RelatedReservationsInfoResponse from(final List<Reservation> reservations) {
		List<ReservationInfoResponse> reservationInfoResponses = reservations.stream()
			.map(ReservationInfoResponse::from)
			.toList();
		return new RelatedReservationsInfoResponse(reservationInfoResponses);
	}
}
