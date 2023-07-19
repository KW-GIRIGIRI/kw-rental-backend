package com.girigiri.kwrental.reservation.dto.request;

import lombok.Builder;

@Builder
public record AddEquipmentReservationRequest(
	String renterName,
	String renterPhoneNumber,
	String renterEmail,
	String rentalPurpose
) {
}
