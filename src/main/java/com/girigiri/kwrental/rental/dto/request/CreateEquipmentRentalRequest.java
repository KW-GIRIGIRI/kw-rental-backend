package com.girigiri.kwrental.rental.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateEquipmentRentalRequest {

    @NotNull
    private Long reservationId;

    @NotEmpty
    private List<RentalSpecsRequest> rentalSpecsRequests;

    private CreateEquipmentRentalRequest() {
    }

    public CreateEquipmentRentalRequest(final Long reservationId, final List<RentalSpecsRequest> rentalSpecsRequests) {
        this.reservationId = reservationId;
        this.rentalSpecsRequests = rentalSpecsRequests;
    }
}
