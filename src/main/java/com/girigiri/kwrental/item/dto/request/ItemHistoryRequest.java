package com.girigiri.kwrental.item.dto.request;

import com.girigiri.kwrental.equipment.domain.Category;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ItemHistoryRequest(
        Category category,
        @NotNull LocalDate from,
        @NotNull LocalDate to
) {
}
