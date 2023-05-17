package com.girigiri.kwrental.reservation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CancelReservationSpecRequest {
    @NotNull
    @Min(1L)
    private Integer amount;

    private CancelReservationSpecRequest() {
    }

    public CancelReservationSpecRequest(final Integer amount) {
        this.amount = amount;
    }
}
