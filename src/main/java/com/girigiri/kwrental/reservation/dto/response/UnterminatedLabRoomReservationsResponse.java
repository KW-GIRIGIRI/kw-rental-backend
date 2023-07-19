package com.girigiri.kwrental.reservation.dto.response;

import java.util.List;

import com.girigiri.kwrental.reservation.domain.entity.Reservation;

public record UnterminatedLabRoomReservationsResponse(
    List<UnterminatedLabRoomReservationResponse> reservations
) {
    public static UnterminatedLabRoomReservationsResponse from(final List<Reservation> reservations) {
        final List<UnterminatedLabRoomReservationResponse> unterminatedLabRoomReservationResponses = reservations.stream()
            .map(UnterminatedLabRoomReservationResponse::from)
            .toList();
        return new UnterminatedLabRoomReservationsResponse(unterminatedLabRoomReservationResponses);
    }
}
