package com.girigiri.kwrental.reservation.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpecStatus;

public record UnterminatedLabRoomReservationsResponse(
	List<UnterminatedLabRoomReservationResponse> reservations
) {
	public static UnterminatedLabRoomReservationsResponse from(final List<Reservation> reservations) {
		final List<UnterminatedLabRoomReservationResponse> unterminatedLabRoomReservationResponses = reservations.stream()
			.map(UnterminatedLabRoomReservationResponse::from)
			.toList();
		return new UnterminatedLabRoomReservationsResponse(unterminatedLabRoomReservationResponses);
	}

	public record UnterminatedLabRoomReservationResponse(

		Long reservationId,
		Long reservationSpecId,
		LocalDate startDate,
		LocalDate endDate,
		String name,
		Integer amount,
		ReservationSpecStatus status
	) {
		public static UnterminatedLabRoomReservationResponse from(final Reservation reservation) {
			final ReservationSpec spec = reservation.getReservationSpecs().iterator().next();
			return new UnterminatedLabRoomReservationResponse(reservation.getId(), spec.getId(), spec.getStartDate(),
				spec.getEndDate(), spec.getRentable().getName(), spec.getAmount().getAmount(), spec.getStatus());
		}
	}
}
