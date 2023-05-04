package com.girigiri.kwrental.rental.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReturnRentalRequest {
    @NotNull
    private Long reservationId;
    @NotEmpty
    private List<ReturnRentalSpecRequest> rentalSpecs;

    private ReturnRentalRequest() {
    }

    private ReturnRentalRequest(final Long reservationId, final List<ReturnRentalSpecRequest> rentalSpecs) {
        this.reservationId = reservationId;
        this.rentalSpecs = rentalSpecs;
    }
}
