package com.girigiri.kwrental.rental.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class CreateRentalRequest {

    private Long reservationId;
    private List<RentalSpecsRequest> rentalSpecsRequests;

    private CreateRentalRequest() {
    }


    public CreateRentalRequest(final Long reservationId, final List<RentalSpecsRequest> rentalSpecsRequests) {
        this.reservationId = reservationId;
        this.rentalSpecsRequests = rentalSpecsRequests;
    }
}
