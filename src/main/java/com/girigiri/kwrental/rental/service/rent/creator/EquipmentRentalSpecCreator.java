package com.girigiri.kwrental.rental.service.rent.creator;

import java.util.List;

import org.springframework.stereotype.Service;

import com.girigiri.kwrental.rental.domain.AbstractRentalSpec;
import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.dto.request.CreateEquipmentRentalRequest;
import com.girigiri.kwrental.rental.dto.request.RentalSpecsRequest;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EquipmentRentalSpecCreator implements RentalSpecCreator<CreateEquipmentRentalRequest> {

	@Override
	public List<AbstractRentalSpec> create(final CreateEquipmentRentalRequest rentalSpecRequest) {
		return rentalSpecRequest.getRentalSpecsRequests().stream()
			.map(it -> mapToRentalSpecPerReservationSpec(rentalSpecRequest.getReservationId(), it))
			.flatMap(List::stream)
			.toList();
	}

	private List<AbstractRentalSpec> mapToRentalSpecPerReservationSpec(final Long reservationId,
		final RentalSpecsRequest rentalSpecsRequest) {
		final Long reservationSpecId = rentalSpecsRequest.getReservationSpecId();
		return rentalSpecsRequest.getPropertyNumbers().stream()
			.map(propertyNumber -> mapToRentalSpec(reservationId, reservationSpecId, propertyNumber))
			.toList();
	}

	private AbstractRentalSpec mapToRentalSpec(final Long reservationId, final Long reservationSpecId,
		final String propertyNumber) {
		return EquipmentRentalSpec.builder()
			.reservationId(reservationId)
			.reservationSpecId(reservationSpecId)
			.propertyNumber(propertyNumber)
			.acceptDateTime(RentalDateTime.now())
			.build();
	}
}
