package com.girigiri.kwrental.reservation.dto.request;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record AddLabRoomReservationRequest(
	LocalDate startDate,
	LocalDate endDate,
	String labRoomName,
	String renterName,
	String renterPhoneNumber,
	String renterEmail,
	String rentalPurpose,
	Integer renterCount
) {
}
