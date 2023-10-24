package com.girigiri.kwrental.rental.dto.response;

import java.time.LocalDateTime;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;

public record RentalSpecWithName(

	String name,
	LocalDateTime acceptDateTime,
	LocalDateTime returnDateTime,
	RentalSpecStatus status
) {

	public RentalSpecWithName(final String name, final RentalDateTime acceptDateTime,
		final RentalDateTime returnDateTime, final RentalSpecStatus status) {
		this(name, acceptDateTime == null ? null : acceptDateTime.toLocalDateTime(),
			returnDateTime == null ? null : returnDateTime.toLocalDateTime(), status);
	}
}
