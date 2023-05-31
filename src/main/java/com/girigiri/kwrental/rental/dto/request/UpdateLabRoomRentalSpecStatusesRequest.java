package com.girigiri.kwrental.rental.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class UpdateLabRoomRentalSpecStatusesRequest {
    @NotEmpty
    private List<UpdateLabRoomRentalSpecStatusRequest> reservations;

    private UpdateLabRoomRentalSpecStatusesRequest() {
    }

    public UpdateLabRoomRentalSpecStatusesRequest(final List<UpdateLabRoomRentalSpecStatusRequest> reservations) {
        this.reservations = reservations;
    }
}
