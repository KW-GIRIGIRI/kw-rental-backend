package com.girigiri.kwrental.reservation.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.girigiri.kwrental.reservation.domain.LabRoomReservation;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.exception.AlreadyReservedLabRoomException;
import com.girigiri.kwrental.reservation.exception.ReservationNotFoundException;
import com.girigiri.kwrental.reservation.exception.ReservationSpecException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationValidateService {
	private final ReservationRepository reservationRepository;
	private final ReservationSpecRepository reservationSpecRepository;

	void validateReservationSpecIdContainsAll(final Long reservationId,
		final Set<Long> reservationSpecIdsFromInput) {
		final Reservation reservation = getReservationWithSpecs(reservationId);
		final Set<Long> reservedSpecIds = getReservedSpecIds(reservation);
		if (reservationSpecIdsFromInput.containsAll(reservedSpecIds) &&
			reservedSpecIds.containsAll(reservationSpecIdsFromInput))
			return;
		throw new ReservationSpecException("신청한 대여 상세와 입력된 대여 상세가 일치하지 않습니다.");
	}

	private Reservation getReservationWithSpecs(final Long reservationId) {
		return reservationRepository.findByIdWithSpecs(reservationId)
			.orElseThrow(ReservationNotFoundException::new);
	}

	private Set<Long> getReservedSpecIds(final Reservation reservation) {
		return reservation.getReservationSpecs().stream()
			.filter(ReservationSpec::isReserved)
			.map(ReservationSpec::getId)
			.collect(Collectors.toSet());
	}

	void validateAmountIsSame(final Map<Long, Integer> amountByReservationSpecId) {
		final List<ReservationSpec> specs = reservationSpecRepository.findByIdIn(amountByReservationSpecId.keySet());
		for (ReservationSpec spec : specs) {
			final Integer amount = amountByReservationSpecId.get(spec.getId());
			spec.validateAmountIsSame(amount);
		}
	}

	void validateAlreadyReservedSamePeriod(final Reservation reservation) {
		final Long memberId = reservation.getMemberId();
		final RentalPeriod period = reservation.getRentalPeriod();
		boolean alreadyReserved = reservationRepository.findNotTerminatedLabRoomReservationsByMemberId(memberId)
			.stream()
			.map(LabRoomReservation::new)
			.anyMatch(it -> it.has(period));
		if (alreadyReserved) {
			throw new AlreadyReservedLabRoomException();
		}
	}
}
