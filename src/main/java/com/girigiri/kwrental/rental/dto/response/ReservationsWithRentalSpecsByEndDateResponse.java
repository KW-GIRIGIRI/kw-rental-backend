package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.rental.dto.response.overduereservations.OverdueReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.ReservationsWithRentalSpecsResponse;
import lombok.Getter;

@Getter
public class ReservationsWithRentalSpecsByEndDateResponse {
    private OverdueReservationsWithRentalSpecsResponse overdueReservations;
    private ReservationsWithRentalSpecsResponse reservationsByEndDate;

    private ReservationsWithRentalSpecsByEndDateResponse() {
    }

    public ReservationsWithRentalSpecsByEndDateResponse(final OverdueReservationsWithRentalSpecsResponse overdueReservations, final ReservationsWithRentalSpecsResponse reservationsByEndDate) {
        this.overdueReservations = overdueReservations;
        this.reservationsByEndDate = reservationsByEndDate;
    }
}
