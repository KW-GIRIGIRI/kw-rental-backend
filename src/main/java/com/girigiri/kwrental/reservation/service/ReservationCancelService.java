package com.girigiri.kwrental.reservation.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.exception.ReservationNotFoundException;
import com.girigiri.kwrental.reservation.exception.ReservationSpecException;
import com.girigiri.kwrental.reservation.exception.ReservationSpecNotFoundException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationCancelService {

	private final ReservationRepository reservationRepository;
	private final ReservationSpecRepository reservationSpecRepository;

	@Transactional(propagation = Propagation.MANDATORY)
	public void cancelReserved(final Long memberId) {
		Set<Reservation> reservations = reservationRepository.findNotTerminatedReservationsByMemberId(memberId);
		final List<ReservationSpec> specs = reservations.stream()
			.filter(reservation -> !reservation.isAccepted())
			.map(Reservation::getReservationSpecs)
			.flatMap(Collection::stream)
			.filter(ReservationSpec::isReserved)
			.toList();
		specs.forEach(spec -> cancelAndAdjust(spec, spec.getAmount().getAmount()));
	}

	public Long cancelReservationSpec(final Long reservationSpecId, final Integer amount) {
		final ReservationSpec reservationSpec = getReservationSpec(reservationSpecId);
		cancelAndAdjust(reservationSpec, amount);
		return reservationSpec.getId();
	}

	private ReservationSpec getReservationSpec(final Long reservationSpecId) {
		return reservationSpecRepository.findById(reservationSpecId)
			.orElseThrow(ReservationSpecNotFoundException::new);
	}

	private void cancelAndAdjust(final ReservationSpec reservationSpec, final Integer amount) {
		reservationSpec.cancelAmount(amount);
		reservationSpecRepository.adjustAmountAndStatus(reservationSpec);
		final Long reservationId = reservationSpec.getReservation().getId();
		updateAndAdjustTerminated(reservationId);
	}

	private void updateAndAdjustTerminated(final Long reservationId) {
		final Reservation reservation = reservationRepository.findByIdWithSpecs(reservationId)
			.orElseThrow(ReservationNotFoundException::new);
		reservation.updateIfTerminated();
		if (reservation.isTerminated())
			reservationRepository.adjustTerminated(reservation);
	}

	void cancelByAssetId(Long assetId) {
		List<ReservationSpec> reservedOrRentedSpecs = reservationSpecRepository.findReservedOrRentedByAssetId(
			assetId);
		validateAllReserved(reservedOrRentedSpecs);
		reservedOrRentedSpecs
			.forEach(spec -> cancelReservationSpec(spec.getId(), spec.getAmount().getAmount()));
	}

	private void validateAllReserved(List<ReservationSpec> reservedOrRentedSpecs) {
		boolean anyRented = reservedOrRentedSpecs.stream()
			.anyMatch(ReservationSpec::isRented);
		if (anyRented)
			throw new ReservationSpecException("대여 중인 대여 예약 상세는 취소할 수 없습니다.");
	}
}
