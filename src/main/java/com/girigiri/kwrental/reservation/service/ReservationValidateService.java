package com.girigiri.kwrental.reservation.service;

import java.util.HashMap;
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

	 Map<Long, Set<String>> validatePropertyNumbersCountAndGroupByEquipmentId(final Long reservationId,
		 final Map<Long, Set<String>> propertyNumbersByReservationSpecId) {
		 final Reservation reservation = reservationRepository.findByIdWithSpecs(reservationId)
			 .orElseThrow(ReservationNotFoundException::new);
		 validateReservationSpecIdContainsAll(reservation, propertyNumbersByReservationSpecId.keySet());
		 Map<Long, Set<String>> collectedByEquipmentId = new HashMap<>();
		 for (ReservationSpec reservationSpec : reservation.getReservationSpecs()) {
			 reservationSpec.validateAmount(propertyNumbersByReservationSpecId.get(reservationSpec.getId()).size());
			 collectedByEquipmentId.put(reservationSpec.getRentable().getId(),
				 propertyNumbersByReservationSpecId.get(reservationSpec.getId()));
		 }
		return collectedByEquipmentId;
	}

	private void validateReservationSpecIdContainsAll(final Reservation reservation,
		final Set<Long> reservationSpecIdsFromInput) {
		final Set<Long> reservationSpecIdsFromReservation = reservation.getReservationSpecs().stream()
			.filter(ReservationSpec::isReserved)
			.map(ReservationSpec::getId)
			.collect(Collectors.toSet());
		if (reservationSpecIdsFromInput.containsAll(reservationSpecIdsFromReservation) &&
			reservationSpecIdsFromReservation.containsAll(reservationSpecIdsFromInput))
			return;
		throw new ReservationSpecException("신청한 대여 상세와 입력된 대여 상세가 일치하지 않습니다.");
	}
}
