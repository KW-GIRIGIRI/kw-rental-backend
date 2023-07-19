package com.girigiri.kwrental.reservation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;

public record LabRoomReservationWithMemberNumberResponse(
	String labRoomName,
	LocalDateTime acceptTime,
	List<LabRoomReservationSpecWithMemberNumberResponse> specsWithMemberNumber
) {
	public LabRoomReservationWithMemberNumberResponse(final String labRoomName, final RentalDateTime acceptTime,
		final List<LabRoomReservationSpecWithMemberNumberResponse> specsWithMemberNumber) {
		this(labRoomName, acceptTime == null ? null : acceptTime.toLocalDateTime(), specsWithMemberNumber);
	}

	public LabRoomReservationWithMemberNumberResponse {
	}
}
