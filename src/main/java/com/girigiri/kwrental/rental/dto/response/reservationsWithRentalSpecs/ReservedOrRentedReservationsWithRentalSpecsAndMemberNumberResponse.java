package com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.ReservationWithMemberNumber;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

@Getter
public class ReservedOrRentedReservationsWithRentalSpecsAndMemberNumberResponse {

    private List<ReservedOrRentedReservationWithRentalSpecsResponse> reservations;

    private ReservedOrRentedReservationsWithRentalSpecsAndMemberNumberResponse() {
    }

    private ReservedOrRentedReservationsWithRentalSpecsAndMemberNumberResponse(final List<ReservedOrRentedReservationWithRentalSpecsResponse> reservations) {
        this.reservations = reservations;
    }

    public static ReservedOrRentedReservationsWithRentalSpecsAndMemberNumberResponse of(Collection<ReservationWithMemberNumber> reservations, final List<RentalSpec> rentalSpecs) {
        final List<ReservedOrRentedReservationWithRentalSpecsResponse> reservedOrRentedReservationWithRentalSpecsResponse = reservations.stream()
                .map(it -> ReservedOrRentedReservationWithRentalSpecsResponse.of(it, rentalSpecs))
                .toList();
        return new ReservedOrRentedReservationsWithRentalSpecsAndMemberNumberResponse(reservedOrRentedReservationWithRentalSpecsResponse);
    }
}
