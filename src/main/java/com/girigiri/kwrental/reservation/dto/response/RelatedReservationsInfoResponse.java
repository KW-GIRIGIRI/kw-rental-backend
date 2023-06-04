package com.girigiri.kwrental.reservation.dto.response;

import java.util.List;

import com.girigiri.kwrental.reservation.domain.Reservation;

import lombok.Getter;

@Getter
public class RelatedReservationsInfoResponse {
	private List<ReservationInfoResponse> reservations;

	private RelatedReservationsInfoResponse() {
	}

	private RelatedReservationsInfoResponse(List<ReservationInfoResponse> reservations) {
		this.reservations = reservations;
	}

	public static RelatedReservationsInfoResponse from(final List<Reservation> reservations) {
		List<ReservationInfoResponse> reservationInfoResponses = reservations.stream()
			.map(ReservationInfoResponse::from)
			.toList();
		return new RelatedReservationsInfoResponse(reservationInfoResponses);
	}
}
