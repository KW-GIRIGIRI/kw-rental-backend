package com.girigiri.kwrental.reservation.dto.request;

import lombok.Builder;

@Builder
public record AddReservationRequest(
	String renterName,
	String renterPhoneNumber,
	String renterEmail,
	String rentalPurpose
) {
}
