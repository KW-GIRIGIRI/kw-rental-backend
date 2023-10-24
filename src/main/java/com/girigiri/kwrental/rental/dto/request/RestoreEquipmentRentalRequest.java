package com.girigiri.kwrental.rental.dto.request;

import java.util.List;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RestoreEquipmentRentalRequest(@NotNull Long reservationId,
                                            @NotEmpty List<ReturnRentalSpecRequest> rentalSpecs
) {
	@Builder
	public record ReturnRentalSpecRequest(@NotNull Long id, @NotNull RentalSpecStatus status) {
	}
}
