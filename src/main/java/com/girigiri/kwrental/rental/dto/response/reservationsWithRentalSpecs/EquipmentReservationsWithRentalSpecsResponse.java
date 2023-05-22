package com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.ReservationWithMemberNumber;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
public class EquipmentReservationsWithRentalSpecsResponse {

    private List<ReservedOrRentedReservationWithRentalSpecsResponse> reservations;

    private EquipmentReservationsWithRentalSpecsResponse() {
    }

    private EquipmentReservationsWithRentalSpecsResponse(final List<ReservedOrRentedReservationWithRentalSpecsResponse> reservations) {
        this.reservations = reservations;
    }

    public static EquipmentReservationsWithRentalSpecsResponse of(Collection<ReservationWithMemberNumber> reservations, final List<RentalSpec> rentalSpecs) {
        final List<ReservedOrRentedReservationWithRentalSpecsResponse> reservedOrRentedReservationWithRentalSpecsResponse = reservations.stream()
                .map(it -> ReservedOrRentedReservationWithRentalSpecsResponse.of(it, rentalSpecs))
                .toList();
        return new EquipmentReservationsWithRentalSpecsResponse(reservedOrRentedReservationWithRentalSpecsResponse);
    }

    public static EquipmentReservationsWithRentalSpecsResponse of(final Set<EquipmentReservationWithMemberNumber> reservations, final List<RentalSpec> rentalSpecs) {
        final List<ReservedOrRentedReservationWithRentalSpecsResponse> reservedOrRentedReservationWithRentalSpecsResponses = reservations.stream()
                .map(it -> ReservedOrRentedReservationWithRentalSpecsResponse.of(it, rentalSpecs))
                .toList();
        return new EquipmentReservationsWithRentalSpecsResponse(reservedOrRentedReservationWithRentalSpecsResponses);
    }
}
