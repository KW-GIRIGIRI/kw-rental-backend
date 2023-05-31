package com.girigiri.kwrental.rental.dto.request;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateLabRoomRentalSpecStatusRequest {
    @NotNull
    private Long reservationId;
    @NotBlank
    private RentalSpecStatus rentalSpecStatus;

    private UpdateLabRoomRentalSpecStatusRequest() {
    }

    public UpdateLabRoomRentalSpecStatusRequest(final Long reservationId, final RentalSpecStatus rentalSpecStatus) {
        this.reservationId = reservationId;
        this.rentalSpecStatus = rentalSpecStatus;
    }
}
