package com.girigiri.kwrental.reservation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CancelReservationSpecRequest(
	@NotNull
	@Min(1L)
	Integer amount
) {
}
