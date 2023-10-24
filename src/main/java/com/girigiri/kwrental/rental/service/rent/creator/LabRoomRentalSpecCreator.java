package com.girigiri.kwrental.rental.service.rent.creator;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.girigiri.kwrental.rental.domain.entity.LabRoomRentalSpec;
import com.girigiri.kwrental.rental.domain.entity.RentalSpec;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;
import com.girigiri.kwrental.reservation.dto.request.CreateLabRoomRentalRequest;
import com.girigiri.kwrental.reservation.service.ReservationRetrieveService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LabRoomRentalSpecCreator implements RentalSpecCreator<CreateLabRoomRentalRequest> {
	private final ReservationRetrieveService reservationRetrieveService;

	@Override
	public List<RentalSpec> create(final CreateLabRoomRentalRequest rentalSpecRequest) {
		final Map<Long, Long> reservationIdByReservationSpecId = reservationRetrieveService.findLabRoomReservationIdsBySpecIds(
			rentalSpecRequest.reservationSpecIds());
		return mapToRentalSpecs(reservationIdByReservationSpecId);
	}

	private List<RentalSpec> mapToRentalSpecs(final Map<Long, Long> reservationIdByReservationSpecId) {
		return reservationIdByReservationSpecId.keySet()
			.stream()
			.map(reservationSpecId -> mapToRentalSpec(reservationIdByReservationSpecId.get(reservationSpecId),
				reservationSpecId))
			.toList();
	}

	private RentalSpec mapToRentalSpec(final Long reservationId, final Long reservationSpecId) {
		return LabRoomRentalSpec.builder()
			.reservationId(reservationId)
			.reservationSpecId(reservationSpecId)
			.acceptDateTime(RentalDateTime.now())
			.build();
	}
}
