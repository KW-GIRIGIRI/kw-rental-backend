package com.girigiri.kwrental.reservation.dto.response;

import lombok.Getter;

@Getter
public class ReservationPurposeResponse {
	private String purpose;

	private ReservationPurposeResponse() {
	}

	public ReservationPurposeResponse(String purpose) {
		this.purpose = purpose;
	}
}
