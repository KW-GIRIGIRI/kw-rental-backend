package com.girigiri.kwrental.rental.service.rent.creator;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.girigiri.kwrental.rental.domain.entity.AbstractRentalSpec;
import com.girigiri.kwrental.rental.domain.entity.LabRoomRentalSpec;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;
import com.girigiri.kwrental.reservation.dto.request.CreateLabRoomRentalRequest;
import com.girigiri.kwrental.reservation.service.ReservationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LabRoomRentalSpecCreator implements RentalSpecCreator<CreateLabRoomRentalRequest> {
	private final ReservationService reservationService;

	@Override
	public List<AbstractRentalSpec> create(final CreateLabRoomRentalRequest rentalSpecRequest) {
		final Map<Long, Long> reservationIdByReservationSpecId = reservationService.getLabRoomReservationIdsByReservationSpecIds(
			rentalSpecRequest.reservationSpecIds());
		return mapToRentalSpecs(reservationIdByReservationSpecId);
	}

	private List<AbstractRentalSpec> mapToRentalSpecs(final Map<Long, Long> reservationIdByReservationSpecId) {
		return reservationIdByReservationSpecId.keySet()
			.stream()
			.map(reservationSpecId -> mapToRentalSpec(reservationIdByReservationSpecId.get(reservationSpecId),
				reservationSpecId))
			.toList();
	}

	private AbstractRentalSpec mapToRentalSpec(final Long reservationId, final Long reservationSpecId) {
		return LabRoomRentalSpec.builder()
			.reservationId(reservationId)
			.reservationSpecId(reservationSpecId)
			.acceptDateTime(RentalDateTime.now())
			.build();
	}
}
