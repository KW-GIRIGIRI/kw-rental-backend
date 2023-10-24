package com.girigiri.kwrental.rental.dto.request;

import java.util.List;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateLabRoomRentalSpecStatusesRequest(
	@NotEmpty List<UpdateLabRoomRentalSpecStatusRequest> reservations) {

	public record UpdateLabRoomRentalSpecStatusRequest(@NotNull Long reservationId,
	                                                   @NotBlank RentalSpecStatus rentalSpecStatus) {
	}

}
