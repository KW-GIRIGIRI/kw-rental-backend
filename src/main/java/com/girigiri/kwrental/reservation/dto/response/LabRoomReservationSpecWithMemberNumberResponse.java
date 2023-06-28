package com.girigiri.kwrental.reservation.dto.response;

import lombok.Builder;

@Builder
public record LabRoomReservationSpecWithMemberNumberResponse(

	Long id,
	Long reservationId,
	String renterName,
	String memberNumber,
	Integer rentalAmount,
	String phoneNumber
) {
}
