package com.girigiri.kwrental.reservation.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record RentLabRoomRequest(
    @NotEmpty
    String name,
    @NotEmpty
    List<Long> reservationSpecIds) {
}
