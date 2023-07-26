package com.girigiri.kwrental.rental.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
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
