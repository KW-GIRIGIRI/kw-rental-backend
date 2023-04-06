package com.girigiri.kwrental.inventory.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UpdateInventoryRequest {
    private LocalDate rentalStartDate;
    private LocalDate rentalEndDate;
    private Integer amount;

    private UpdateInventoryRequest() {
    }

    public UpdateInventoryRequest(final LocalDate rentalStartDate, final LocalDate rentalEndDate, final Integer amount) {
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
        this.amount = amount;
    }
}
