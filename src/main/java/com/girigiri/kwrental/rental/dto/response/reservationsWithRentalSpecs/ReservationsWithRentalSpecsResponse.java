package com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.Reservation;
import lombok.Getter;

import java.util.List;

@Getter
public class ReservationsWithRentalSpecsResponse {

    private List<ReservationWithRentalSpecsResponse> reservations;

    private ReservationsWithRentalSpecsResponse() {
    }

    private ReservationsWithRentalSpecsResponse(final List<ReservationWithRentalSpecsResponse> reservations) {
        this.reservations = reservations;
    }

    public static ReservationsWithRentalSpecsResponse of(List<Reservation> reservations, final List<RentalSpec> rentalSpecs) {
        final List<ReservationWithRentalSpecsResponse> reservationWithRentalSpecsResponse = reservations.stream()
                .map(it -> ReservationWithRentalSpecsResponse.of(it, rentalSpecs))
                .toList();
        return new ReservationsWithRentalSpecsResponse(reservationWithRentalSpecsResponse);
    }
}
