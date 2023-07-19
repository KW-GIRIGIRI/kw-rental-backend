package com.girigiri.kwrental.reservation.dto.response;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;

import lombok.Builder;

public record LabRoomReservationsWithMemberNumberResponse(
	Collection<LabRoomReservationWithMemberNumberResponse> reservations) {

	public record LabRoomReservationWithMemberNumberResponse(
		String labRoomName,
		LocalDateTime acceptTime,
		List<LabRoomReservationWithMemberNumberResponse.LabRoomReservationSpecWithMemberNumberResponse> specsWithMemberNumber
	) {
		public LabRoomReservationWithMemberNumberResponse(final String labRoomName, final RentalDateTime acceptTime,
			final List<LabRoomReservationWithMemberNumberResponse.LabRoomReservationSpecWithMemberNumberResponse> specsWithMemberNumber) {
			this(labRoomName, acceptTime == null ? null : acceptTime.toLocalDateTime(), specsWithMemberNumber);
		}

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
	}
}