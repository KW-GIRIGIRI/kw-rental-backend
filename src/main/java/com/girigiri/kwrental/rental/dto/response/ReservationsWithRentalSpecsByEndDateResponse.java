package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.rental.dto.response.overduereservations.OverdueReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.ReservationsWithRentalSpecsAndMemberNumberResponse;
import lombok.Getter;

@Getter
public class ReservationsWithRentalSpecsByEndDateResponse {
    private OverdueReservationsWithRentalSpecsResponse overdueReservations;
    private ReservationsWithRentalSpecsAndMemberNumberResponse reservationsByEndDate;

    private ReservationsWithRentalSpecsByEndDateResponse() {
    }

    public ReservationsWithRentalSpecsByEndDateResponse(final OverdueReservationsWithRentalSpecsResponse overdueReservations, final ReservationsWithRentalSpecsAndMemberNumberResponse reservationsByEndDate) {
        this.overdueReservations = overdueReservations;
        this.reservationsByEndDate = reservationsByEndDate;
    }
}
