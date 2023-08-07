package com.girigiri.kwrental.rental.service.rent.acceptor;

import java.util.List;

import org.springframework.stereotype.Component;

import com.girigiri.kwrental.rental.domain.entity.RentalSpec;
import com.girigiri.kwrental.reservation.service.ReservationAcceptor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LabRoomReservationAcceptor implements
	com.girigiri.kwrental.rental.service.rent.acceptor.ReservationAcceptor {
	private final ReservationAcceptor reservationAcceptor;

	@Override
	public void acceptReservationsBy(final List<RentalSpec> rentalSpecs) {
		rentalSpecs.forEach(rentalSpec -> reservationAcceptor.accept(rentalSpec.getReservationId(),
			List.of(rentalSpec.getReservationSpecId())));
	}
}
