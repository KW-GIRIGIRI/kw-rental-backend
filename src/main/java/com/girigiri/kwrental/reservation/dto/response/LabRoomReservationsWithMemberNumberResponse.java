package com.girigiri.kwrental.reservation.dto.response;

import java.util.Collection;

public record LabRoomReservationsWithMemberNumberResponse(
	Collection<LabRoomReservationWithMemberNumberResponse> reservations) {
}
