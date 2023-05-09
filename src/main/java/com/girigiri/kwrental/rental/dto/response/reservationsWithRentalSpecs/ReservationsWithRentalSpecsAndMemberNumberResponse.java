package com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.repository.dto.ReservationWithMemberNumber;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

@Getter
public class ReservationsWithRentalSpecsAndMemberNumberResponse {

    private List<ReservationWithRentalSpecsResponse> reservations;

    private ReservationsWithRentalSpecsAndMemberNumberResponse() {
    }

    private ReservationsWithRentalSpecsAndMemberNumberResponse(final List<ReservationWithRentalSpecsResponse> reservations) {
        this.reservations = reservations;
    }

    public static ReservationsWithRentalSpecsAndMemberNumberResponse of(Collection<ReservationWithMemberNumber> reservations, final List<RentalSpec> rentalSpecs) {
        final List<ReservationWithRentalSpecsResponse> reservationWithRentalSpecsResponse = reservations.stream()
                .map(it -> ReservationWithRentalSpecsResponse.of(it, rentalSpecs))
                .toList();
        return new ReservationsWithRentalSpecsAndMemberNumberResponse(reservationWithRentalSpecsResponse);
    }
}
