package com.girigiri.kwrental.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UpdateInventoryRequest {

    @NotNull
    private LocalDate rentalStartDate;

    @NotNull
    private LocalDate rentalEndDate;

    @Positive
    private Integer amount;

    private UpdateInventoryRequest() {
    }

    public UpdateInventoryRequest(final LocalDate rentalStartDate, final LocalDate rentalEndDate, final Integer amount) {
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
        this.amount = amount;
    }
}
