package com.girigiri.kwrental.rental.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class LabRoomReservationsResponse {

    private List<LabRoomReservationResponse> reservations;

    private LabRoomReservationsResponse() {
    }

    public LabRoomReservationsResponse(final List<LabRoomReservationResponse> reservations) {
        this.reservations = reservations;
    }
}
