package com.girigiri.kwrental.rental.service.rent.acceptor;

import java.util.List;

import org.springframework.stereotype.Service;

import com.girigiri.kwrental.rental.domain.AbstractRentalSpec;
import com.girigiri.kwrental.reservation.service.ReservationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LabRoomReservationAcceptor implements ReservationAcceptor {
	private final ReservationService reservationService;

	@Override
	public void acceptReservationsBy(final List<AbstractRentalSpec> rentalSpecs) {
		rentalSpecs.forEach(rentalSpec -> reservationService.acceptReservation(rentalSpec.getReservationId(),
			List.of(rentalSpec.getReservationSpecId())));
	}
}
