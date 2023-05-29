package com.girigiri.kwrental.reservation.dto.response;

import com.girigiri.kwrental.reservation.domain.Reservation;
import lombok.Getter;

import java.util.List;

@Getter
public class UnterminatedEquipmentReservationsResponse {

    private List<UnterminatedEquipmentReservationResponse> reservations;

    private UnterminatedEquipmentReservationsResponse() {
    }

    private UnterminatedEquipmentReservationsResponse(final List<UnterminatedEquipmentReservationResponse> reservations) {
        this.reservations = reservations;
    }

    public static UnterminatedEquipmentReservationsResponse from(final List<Reservation> unterminatedReservations) {
        final List<UnterminatedEquipmentReservationResponse> reservationResponses = unterminatedReservations.stream()
                .map(UnterminatedEquipmentReservationResponse::from)
                .toList();
        return new UnterminatedEquipmentReservationsResponse(reservationResponses);
    }
}
