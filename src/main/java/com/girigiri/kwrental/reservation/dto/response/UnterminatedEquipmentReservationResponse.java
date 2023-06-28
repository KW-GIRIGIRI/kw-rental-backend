package com.girigiri.kwrental.reservation.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.girigiri.kwrental.reservation.domain.Reservation;

public record UnterminatedEquipmentReservationResponse(

    LocalDate startDate,
    LocalDate endDate,
    List<UnterminatedEquipmentReservationSpecResponse> reservationSpecs
) {
    public static UnterminatedEquipmentReservationResponse from(final Reservation unterminatedReservation) {
        final List<UnterminatedEquipmentReservationSpecResponse> reservationSpecResponses = unterminatedReservation.getReservationSpecs()
            .stream()
            .map(UnterminatedEquipmentReservationSpecResponse::from)
            .toList();
        return new UnterminatedEquipmentReservationResponse(unterminatedReservation.getStartDate(),
            unterminatedReservation.getEndDate(), reservationSpecResponses);
    }
}
