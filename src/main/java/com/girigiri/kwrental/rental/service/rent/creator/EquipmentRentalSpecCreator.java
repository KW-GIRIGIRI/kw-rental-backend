package com.girigiri.kwrental.rental.service.rent.creator;

import java.util.List;

import org.springframework.stereotype.Component;

import com.girigiri.kwrental.rental.domain.entity.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.domain.entity.RentalSpec;
import com.girigiri.kwrental.rental.dto.request.CreateEquipmentRentalRequest;
import com.girigiri.kwrental.rental.dto.request.CreateEquipmentRentalRequest.EquipmentRentalSpecsRequest;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EquipmentRentalSpecCreator implements RentalSpecCreator<CreateEquipmentRentalRequest> {

	@Override
	public List<RentalSpec> create(final CreateEquipmentRentalRequest rentalSpecRequest) {
		return rentalSpecRequest.rentalSpecsRequests().stream()
			.map(it -> mapToRentalSpecPerReservationSpec(rentalSpecRequest.reservationId(), it))
			.flatMap(List::stream)
			.toList();
	}

	private List<RentalSpec> mapToRentalSpecPerReservationSpec(final Long reservationId,
		final EquipmentRentalSpecsRequest equipmentRentalSpecsRequest) {
		final Long reservationSpecId = equipmentRentalSpecsRequest.reservationSpecId();
		return equipmentRentalSpecsRequest.propertyNumbers().stream()
			.map(propertyNumber -> mapToRentalSpec(reservationId, reservationSpecId, propertyNumber))
			.toList();
	}

	private RentalSpec mapToRentalSpec(final Long reservationId, final Long reservationSpecId,
		final String propertyNumber) {
		return EquipmentRentalSpec.builder()
			.reservationId(reservationId)
			.reservationSpecId(reservationSpecId)
			.propertyNumber(propertyNumber)
			.acceptDateTime(RentalDateTime.now())
			.build();
	}
}
