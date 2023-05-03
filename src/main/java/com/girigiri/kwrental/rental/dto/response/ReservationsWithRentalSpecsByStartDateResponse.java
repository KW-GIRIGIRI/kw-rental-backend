package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.Reservation;
import lombok.Getter;

import java.util.List;

@Getter
public class ReservationsWithRentalSpecsByStartDateResponse {

    private List<ReservationByStartDateResponse> reservations;

    private ReservationsWithRentalSpecsByStartDateResponse() {
    }

    private ReservationsWithRentalSpecsByStartDateResponse(final List<ReservationByStartDateResponse> reservations) {
        this.reservations = reservations;
    }

    public static ReservationsWithRentalSpecsByStartDateResponse of(List<Reservation> reservations, final List<RentalSpec> rentalSpecs) {
        final List<ReservationByStartDateResponse> reservationByStartDateResponse = reservations.stream()
                .map(it -> ReservationByStartDateResponse.of(it, rentalSpecs))
                .toList();
        return new ReservationsWithRentalSpecsByStartDateResponse(reservationByStartDateResponse);
    }
}
