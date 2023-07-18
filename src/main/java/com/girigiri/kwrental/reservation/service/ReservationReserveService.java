package com.girigiri.kwrental.reservation.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.exception.ReservationException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationReserveService {

	private final ReservationRepository reservationRepository;
	private final PenaltyService penaltyService;
	private final AmountValidator amountValidator;

	void reserve(final Long memberId, final List<Reservation> reservations, final ReserveValidator reserveValidator) {
		validatePenalty(memberId);
		for (final Reservation reservation : reservations) {
			reserveValidator.validate(reservation);
			validateAvailableCount(reservation);
		}
		reservationRepository.saveAll(reservations);
	}

	private void validatePenalty(final Long memberId) {
		if (penaltyService.hasOngoingPenalty(memberId)) {
			throw new ReservationException("페널티에 적용되는 기간에는 대여 예약을 할 수 없습니다.");
		}
	}

	private void validateAvailableCount(final Reservation reservation) {
		reservation.getReservationSpecs()
			.forEach(spec -> amountValidator.validateAmount(spec.getRentable().getId(),
				spec.getAmount().getAmount(), spec.getPeriod()));
	}
}
