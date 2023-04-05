package com.girigiri.kwrental.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AddInventoryRequest {

    @NotNull
    private Long equipmentId;

    @NotNull
    private LocalDate rentalStartDate;

    @NotNull
    private LocalDate rentalEndDate;

    @Positive
    private Integer amount;

    private AddInventoryRequest() {
    }

    public AddInventoryRequest(Long equipmentId, LocalDate rentalStartDate, LocalDate rentalEndDate, Integer amount) {
        this.equipmentId = equipmentId;
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
        this.amount = amount;
    }
}
