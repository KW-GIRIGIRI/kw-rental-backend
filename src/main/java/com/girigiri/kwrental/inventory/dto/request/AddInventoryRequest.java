package com.girigiri.kwrental.inventory.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AddInventoryRequest {

    private Long equipmentId;
    private LocalDate rentalStartDate;
    private LocalDate rentalEndDate;
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
