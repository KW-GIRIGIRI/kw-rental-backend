package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.rental.dto.response.overduereservations.OverdueReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.EquipmentReservationsWithRentalSpecsResponse;
import lombok.Getter;

@Getter
public class ReservationsWithRentalSpecsByEndDateResponse {
    private OverdueReservationsWithRentalSpecsResponse overdueReservations;
    private EquipmentReservationsWithRentalSpecsResponse reservationsByEndDate;

    private ReservationsWithRentalSpecsByEndDateResponse() {
    }

    public ReservationsWithRentalSpecsByEndDateResponse(final OverdueReservationsWithRentalSpecsResponse overdueReservations, final EquipmentReservationsWithRentalSpecsResponse reservationsByEndDate) {
        this.overdueReservations = overdueReservations;
        this.reservationsByEndDate = reservationsByEndDate;
    }
}
