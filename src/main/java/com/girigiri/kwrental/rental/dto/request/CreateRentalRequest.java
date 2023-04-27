package com.girigiri.kwrental.rental.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateRentalRequest {

    @NotNull
    private Long reservationId;

    @NotEmpty
    private List<RentalSpecsRequest> rentalSpecsRequests;

    private CreateRentalRequest() {
    }


    public CreateRentalRequest(final Long reservationId, final List<RentalSpecsRequest> rentalSpecsRequests) {
        this.reservationId = reservationId;
        this.rentalSpecsRequests = rentalSpecsRequests;
    }
}
