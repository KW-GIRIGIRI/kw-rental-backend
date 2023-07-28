package com.girigiri.kwrental.reservation.service.reserve.template;

import java.util.List;

import org.springframework.stereotype.Component;

import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.exception.ReservationException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.service.remainquantity.RemainQuantityValidator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReserveTemplate {

	private final ReservationRepository reservationRepository;
	private final PenaltyChecker penaltyChecker;
	private final RemainQuantityValidator remainQuantityValidator;

	public void reserve(final Long memberId, final List<Reservation> reservations,
		final ReserveValidator... reserveValidators) {
		validatePenalty(memberId);
		for (final Reservation reservation : reservations) {
			validateValidators(reservation, reserveValidators);
			validateAvailableCount(reservation);
		}
		reservationRepository.saveAll(reservations);
	}

	private void validatePenalty(final Long memberId) {
		if (penaltyChecker.hasOngoingPenalty(memberId)) {
			throw new ReservationException("페널티에 적용되는 기간에는 대여 예약을 할 수 없습니다.");
		}
	}

	private void validateValidators(final Reservation reservation, final ReserveValidator[] reserveValidators) {
		for (ReserveValidator reserveValidator : reserveValidators) {
			reserveValidator.validate(reservation);
		}
	}

	private void validateAvailableCount(final Reservation reservation) {
		reservation.getReservationSpecs()
			.forEach(spec -> remainQuantityValidator.validateAmount(spec.getRentable().getId(),
				spec.getAmount().getAmount(), spec.getPeriod()));
	}
}
