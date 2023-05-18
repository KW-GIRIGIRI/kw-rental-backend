package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.rental.dto.response.overduereservations.OverdueReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.ReservedOrRentedReservationsWithRentalSpecsAndMemberNumberResponse;
import lombok.Getter;

@Getter
public class ReservationsWithRentalSpecsByEndDateResponse {
    private OverdueReservationsWithRentalSpecsResponse overdueReservations;
    private ReservedOrRentedReservationsWithRentalSpecsAndMemberNumberResponse reservationsByEndDate;

    private ReservationsWithRentalSpecsByEndDateResponse() {
    }

    public ReservationsWithRentalSpecsByEndDateResponse(final OverdueReservationsWithRentalSpecsResponse overdueReservations, final ReservedOrRentedReservationsWithRentalSpecsAndMemberNumberResponse reservationsByEndDate) {
        this.overdueReservations = overdueReservations;
        this.reservationsByEndDate = reservationsByEndDate;
    }
}
