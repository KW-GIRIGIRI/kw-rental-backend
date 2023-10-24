package com.girigiri.kwrental.rental.dto.response;

import java.time.LocalDate;
import java.util.Set;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;

public record EquipmentRentalsDto(
	Set<EquipmentRentalDto> rentals) {
	public record EquipmentRentalDto(
		LocalDate startDate,
		LocalDate endDate,
		Set<EquipmentRentalSpecDto> rentalSpecs) {
		public record EquipmentRentalSpecDto(
			Long id,
			String modelName,
			RentalSpecStatus status) {
		}
	}

}
