package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.rental.dto.response.overduereservations.OverdueEquipmentReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.EquipmentReservationsWithRentalSpecsResponse;

import lombok.Getter;

@Getter
public class ReservationsWithRentalSpecsByEndDateResponse {
    private OverdueEquipmentReservationsWithRentalSpecsResponse overdueReservations;
    private EquipmentReservationsWithRentalSpecsResponse reservationsByEndDate;

    private ReservationsWithRentalSpecsByEndDateResponse() {
    }

    public ReservationsWithRentalSpecsByEndDateResponse(
        final OverdueEquipmentReservationsWithRentalSpecsResponse overdueReservations,
        final EquipmentReservationsWithRentalSpecsResponse reservationsByEndDate) {
        this.overdueReservations = overdueReservations;
        this.reservationsByEndDate = reservationsByEndDate;
    }
}
