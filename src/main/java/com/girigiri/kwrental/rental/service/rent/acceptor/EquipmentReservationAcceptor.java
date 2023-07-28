package com.girigiri.kwrental.rental.service.rent.acceptor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.girigiri.kwrental.rental.domain.entity.AbstractRentalSpec;
import com.girigiri.kwrental.rental.exception.ReservationIdNotSingleValueWhenEquipmentAcceptException;
import com.girigiri.kwrental.reservation.service.ReservationAcceptor;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EquipmentReservationAcceptor implements
	com.girigiri.kwrental.rental.service.rent.acceptor.ReservationAcceptor {
	private final ReservationAcceptor reservationAcceptor;

	@Override
	public void acceptReservationsBy(final List<AbstractRentalSpec> rentalSpecs) {
		final Long reservationId = getReservationId(rentalSpecs);
		final List<Long> reservationSpecIds = getReservationIds(rentalSpecs);
		reservationAcceptor.accept(reservationId, reservationSpecIds);
	}

	private Long getReservationId(final List<AbstractRentalSpec> rentalSpecs) {
		final Set<Long> reservationIds = rentalSpecs.stream()
			.map(AbstractRentalSpec::getReservationId)
			.collect(Collectors.toSet());
		validateReservationIdIsSingleValue(reservationIds);
		return reservationIds.iterator().next();
	}

	private void validateReservationIdIsSingleValue(final Set<Long> reservationIds) {
		if (reservationIds.size() != 1) {
			throw new ReservationIdNotSingleValueWhenEquipmentAcceptException();
		}
	}

	private List<Long> getReservationIds(final List<AbstractRentalSpec> rentalSpecs) {
		return rentalSpecs.stream()
			.map(AbstractRentalSpec::getReservationSpecId)
			.toList();
	}
}
