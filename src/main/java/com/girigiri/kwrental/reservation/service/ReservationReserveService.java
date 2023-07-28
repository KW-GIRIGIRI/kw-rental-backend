package com.girigiri.kwrental.reservation.service;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.girigiri.kwrental.reservation.domain.LabRoomReservation;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.exception.ReservationException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.service.remainquantity.RemainQuantityValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationReserveService {

	private final ReservationRepository reservationRepository;
	private final PenaltyChecker penaltyChecker;
	private final RemainQuantityValidator remainQuantityValidator;

	void reserve(final Long memberId, final List<Reservation> reservations, final ReserveValidator reserveValidator) {
		validatePenalty(memberId);
		for (final Reservation reservation : reservations) {
			reserveValidator.validate(reservation);
			validateAvailableCount(reservation);
		}
		reservationRepository.saveAll(reservations);
	}

	private void validatePenalty(final Long memberId) {
		if (penaltyChecker.hasOngoingPenalty(memberId)) {
			throw new ReservationException("페널티에 적용되는 기간에는 대여 예약을 할 수 없습니다.");
		}
	}

	private void validateAvailableCount(final Reservation reservation) {
		reservation.getReservationSpecs()
			.forEach(spec -> remainQuantityValidator.validateAmount(spec.getRentable().getId(),
				spec.getAmount().getAmount(), spec.getPeriod()));
	}

	Map<Long, Long> findLabRoomReservationIdsBySpecIds(final List<Long> specIds) {
		return reservationRepository.findByReservationSpecIds(specIds)
			.stream()
			.map(LabRoomReservation::new)
			.collect(toMap(LabRoomReservation::getReservationSpecId, LabRoomReservation::getId));
	}
}
