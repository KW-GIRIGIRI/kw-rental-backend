package com.girigiri.kwrental.inventory.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record UpdateInventoryRequest(@NotNull LocalDate rentalStartDate, @NotNull LocalDate rentalEndDate,
                                     @Positive Integer amount) {
}
