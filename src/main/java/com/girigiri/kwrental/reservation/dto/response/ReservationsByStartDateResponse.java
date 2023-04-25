package com.girigiri.kwrental.reservation.dto.response;

import com.girigiri.kwrental.reservation.domain.Reservation;
import lombok.Getter;

import java.util.List;

@Getter
public class ReservationsByStartDateResponse {

    private List<ReservationResponse> reservations;

    private ReservationsByStartDateResponse() {
    }

    private ReservationsByStartDateResponse(final List<ReservationResponse> reservations) {
        this.reservations = reservations;
    }

    public static ReservationsByStartDateResponse from(List<Reservation> reservations) {
        final List<ReservationResponse> reservationResponses = reservations.stream()
                .map(ReservationResponse::from)
                .toList();
        return new ReservationsByStartDateResponse(reservationResponses);
    }
}
