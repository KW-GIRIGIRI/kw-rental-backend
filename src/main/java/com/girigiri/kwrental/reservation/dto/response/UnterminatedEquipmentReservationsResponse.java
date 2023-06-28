package com.girigiri.kwrental.reservation.dto.response;

import java.util.List;

import com.girigiri.kwrental.reservation.domain.Reservation;

public record UnterminatedEquipmentReservationsResponse(

    List<UnterminatedEquipmentReservationResponse> reservations
) {
    public static UnterminatedEquipmentReservationsResponse from(final List<Reservation> unterminatedReservations) {
        final List<UnterminatedEquipmentReservationResponse> reservationResponses = unterminatedReservations.stream()
            .map(UnterminatedEquipmentReservationResponse::from)
            .toList();
        return new UnterminatedEquipmentReservationsResponse(reservationResponses);
    }
}
