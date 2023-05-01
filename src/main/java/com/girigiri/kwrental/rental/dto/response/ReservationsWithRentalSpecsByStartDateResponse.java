package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.reservation.domain.Reservation;
import lombok.Getter;

import java.util.List;

@Getter
public class ReservationsWithRentalSpecsByStartDateResponse {

    private List<ReservationResponse> reservations;

    private ReservationsWithRentalSpecsByStartDateResponse() {
    }

    private ReservationsWithRentalSpecsByStartDateResponse(final List<ReservationResponse> reservations) {
        this.reservations = reservations;
    }

    public static ReservationsWithRentalSpecsByStartDateResponse of(List<Reservation> reservations, final List<RentalSpecResponse> rentalSpecResponses) {
        final List<ReservationResponse> reservationResponses = reservations.stream()
                .map(it -> ReservationResponse.of(it, rentalSpecResponses))
                .toList();
        return new ReservationsWithRentalSpecsByStartDateResponse(reservationResponses);
    }
}
