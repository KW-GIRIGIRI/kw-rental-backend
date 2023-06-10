package com.girigiri.kwrental.asset.dto.response;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class RemainQuantityPerDateResponse {
	private LocalDate date;
	private Integer remainQuantity;

	private RemainQuantityPerDateResponse() {
	}

	public RemainQuantityPerDateResponse(final LocalDate date, final Integer remainQuantity) {
		this.date = date;
		this.remainQuantity = remainQuantity;
	}

	public void setRemainQuantity(final int remainQuantity) {
		this.remainQuantity = remainQuantity;
	}
}
