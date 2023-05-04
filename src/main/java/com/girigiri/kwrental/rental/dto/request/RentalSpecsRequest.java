package com.girigiri.kwrental.rental.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class RentalSpecsRequest {

    @NotNull
    private Long reservationSpecId;

    @NotEmpty
    private List<String> propertyNumbers;

    private RentalSpecsRequest() {
    }

    public RentalSpecsRequest(final Long reservationSpecId, final List<String> propertyNumbers) {
        this.reservationSpecId = reservationSpecId;
        this.propertyNumbers = propertyNumbers;
    }
}
