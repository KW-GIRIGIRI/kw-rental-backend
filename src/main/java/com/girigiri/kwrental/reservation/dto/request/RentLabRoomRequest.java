package com.girigiri.kwrental.reservation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class RentLabRoomRequest {

    @NotEmpty
    private String name;

    @NotEmpty
    private List<Long> reservationSpecIds;

    private RentLabRoomRequest() {
    }

    @Builder
    private RentLabRoomRequest(final String name, final List<Long> reservationSpecIds) {
        this.name = name;
        this.reservationSpecIds = reservationSpecIds;
    }
}
