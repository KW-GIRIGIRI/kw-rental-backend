package com.girigiri.kwrental.asset.dto.response;

import java.time.LocalDate;

public record RemainQuantityPerDateResponse(
	LocalDate date,
	Integer remainQuantity) {

	public RemainQuantityPerDateResponse createEmptyQuanittyWithSameDate() {
		return new RemainQuantityPerDateResponse(this.date, 0);
	}
}
