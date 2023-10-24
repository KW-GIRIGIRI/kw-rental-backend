package com.girigiri.kwrental.item.dto.request;

import java.time.LocalDate;

import com.girigiri.kwrental.asset.equipment.domain.Category;

import jakarta.validation.constraints.NotNull;

public record ItemHistoryRequest(
	Category category,
	@NotNull LocalDate from,
	@NotNull LocalDate to
) {
}
