package com.girigiri.kwrental.rental.dto.response.overduereservations;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class OverdueReservationsWithRentalSpecsResponse {

    private List<OverdueReservationResponse> reservations;

    private OverdueReservationsWithRentalSpecsResponse() {
    }

    private OverdueReservationsWithRentalSpecsResponse(final List<OverdueReservationResponse> reservations) {
        this.reservations = reservations;
    }

    public static OverdueReservationsWithRentalSpecsResponse of(final Set<EquipmentReservationWithMemberNumber> equipmentReservations, final List<RentalSpec> rentalSpecs) {
        final List<OverdueReservationResponse> reservationResponses = equipmentReservations.stream()
                .map(it -> OverdueReservationResponse.of(it, rentalSpecs))
                .toList();
        return new OverdueReservationsWithRentalSpecsResponse(reservationResponses);
    }
}
