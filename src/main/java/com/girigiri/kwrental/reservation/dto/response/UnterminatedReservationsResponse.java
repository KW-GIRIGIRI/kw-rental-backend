package com.girigiri.kwrental.reservation.dto.response;

import com.girigiri.kwrental.reservation.domain.Reservation;
import lombok.Getter;

import java.util.List;

@Getter
public class UnterminatedReservationsResponse {

    private List<UnterminatedReservationResponse> reservations;

    private UnterminatedReservationsResponse() {
    }

    private UnterminatedReservationsResponse(final List<UnterminatedReservationResponse> reservations) {
        this.reservations = reservations;
    }

    public static UnterminatedReservationsResponse from(final List<Reservation> unterminatedReservations) {
        final List<UnterminatedReservationResponse> reservationResponses = unterminatedReservations.stream()
                .map(UnterminatedReservationResponse::from)
                .toList();
        return new UnterminatedReservationsResponse(reservationResponses);
    }
}
