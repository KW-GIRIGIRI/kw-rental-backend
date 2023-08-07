package com.girigiri.kwrental.rental.service.rent.acceptor;

import java.util.List;

import com.girigiri.kwrental.rental.domain.entity.RentalSpec;

public interface ReservationAcceptor {
	void acceptReservationsBy(List<RentalSpec> rentalSpecs);
}
