package com.girigiri.kwrental.reservation.service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.exception.ReservationNotFoundException;
import com.girigiri.kwrental.reservation.exception.ReservationSpecException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationValidateService {
	private final ReservationRepository reservationRepository;

	void validateReservationSpecIdContainsAll(final Long reservationId,
		final Set<Long> reservationSpecIdsFromInput) {
		final Reservation reservation = getReservationWithSpecs(reservationId);
		final Set<Long> reservationSpecIdsFromReservation = reservation.getReservationSpecs().stream()
			.filter(ReservationSpec::isReserved)
			.map(ReservationSpec::getId)
			.collect(Collectors.toSet());
		if (reservationSpecIdsFromInput.containsAll(reservationSpecIdsFromReservation) &&
			reservationSpecIdsFromReservation.containsAll(reservationSpecIdsFromInput))
			return;
		throw new ReservationSpecException("신청한 대여 상세와 입력된 대여 상세가 일치하지 않습니다.");
	}

	void validateReservationSpecAmount(final Long reservationId,
		final Map<Long, Set<String>> propertyNumbersByReservationSpecId) {
		final Reservation reservation = getReservationWithSpecs(reservationId);
		for (ReservationSpec reservationSpec : reservation.getReservationSpecs()) {
			reservationSpec.validateAmount(propertyNumbersByReservationSpecId.get(reservationSpec.getId()).size());
		}
	}

	private Reservation getReservationWithSpecs(final Long reservationId) {
		return reservationRepository.findByIdWithSpecs(reservationId)
			.orElseThrow(ReservationNotFoundException::new);
	}
}
