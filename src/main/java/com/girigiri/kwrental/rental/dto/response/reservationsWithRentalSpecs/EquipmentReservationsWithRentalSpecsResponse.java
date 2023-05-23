package com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs;

import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class EquipmentReservationsWithRentalSpecsResponse {

    private List<EquipmentReservationWithRentalSpecsResponse> reservations;

    private EquipmentReservationsWithRentalSpecsResponse() {
    }

    private EquipmentReservationsWithRentalSpecsResponse(final List<EquipmentReservationWithRentalSpecsResponse> reservations) {
        this.reservations = reservations;
    }

    public static EquipmentReservationsWithRentalSpecsResponse of(final Set<EquipmentReservationWithMemberNumber> reservations, final List<EquipmentRentalSpec> rentalSpecs) {
        final List<EquipmentReservationWithRentalSpecsResponse> equipmentReservationWithRentalSpecsRespons = reservations.stream()
                .map(it -> EquipmentReservationWithRentalSpecsResponse.of(it, rentalSpecs))
                .toList();
        return new EquipmentReservationsWithRentalSpecsResponse(equipmentReservationWithRentalSpecsRespons);
    }
}
