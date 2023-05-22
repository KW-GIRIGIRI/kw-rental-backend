package com.girigiri.kwrental.reservation.dto.response;

import lombok.Getter;

import java.util.Collection;

@Getter
public class LabRoomReservationsWithMemberNumberResponse {
    private Collection<LabRoomReservationWithMemberNumberResponse> reservations;

    private LabRoomReservationsWithMemberNumberResponse() {
    }

    public LabRoomReservationsWithMemberNumberResponse(final Collection<LabRoomReservationWithMemberNumberResponse> reservations) {
        this.reservations = reservations;
    }
}
