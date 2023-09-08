package com.girigiri.kwrental.reservation.service.cancel;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.exception.ReservationSpecException;
import com.girigiri.kwrental.reservation.exception.ReservationSpecNotFoundException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
class ReservationCanceler {

	private final ReservationRepository reservationRepository;
	private final ReservationSpecRepository reservationSpecRepository;

	void cancelByMemberId(final Long memberId,
		final CancelAlerter<List<ReservationSpec>> alertExecutor) {
		Set<Reservation> reservations = reservationRepository.findNotTerminatedReservationsByMemberId(memberId);
		final List<ReservationSpec> reservedSpecs = filterReservedSpecs(reservations);
		reservedSpecs.forEach(spec -> cancelAndAdjust(spec, spec.getAmount().getAmount()));
		alertExecutor.alert(reservedSpecs);
	}

	private List<ReservationSpec> filterReservedSpecs(final Set<Reservation> reservations) {
		return reservations.stream()
			.filter(reservation -> !reservation.isAccepted())
			.map(Reservation::getReservationSpecs)
			.flatMap(Collection::stream)
			.filter(ReservationSpec::isReserved)
			.toList();
	}

	Long cancelReservationSpec(final Long reservationSpecId, final Integer amount,
		final CancelAlerter<ReservationSpec> alertExecutor) {
		final ReservationSpec reservationSpec = getSpecWithReservation(reservationSpecId);
		cancelAndAdjust(reservationSpec, amount);
		alertExecutor.alert(reservationSpec);
		return reservationSpec.getId();
	}

	private ReservationSpec getSpecWithReservation(final Long reservationSpecId) {
		return reservationSpecRepository.findById(reservationSpecId)
			.orElseThrow(ReservationSpecNotFoundException::new);
	}

	private void cancelAndAdjust(final ReservationSpec reservationSpec, final Integer amount) {
		reservationSpec.cancelAmount(amount);
		reservationSpecRepository.adjustAmountAndStatus(reservationSpec);
		updateIsTerminated(reservationSpec.getReservation());
	}

	private void updateIsTerminated(final Reservation reservation) {
		reservation.updateIfTerminated();
		if (reservation.isTerminated())
			reservationRepository.adjustTerminated(reservation);
	}

	void cancelByAssetId(final Long assetId,
		final CancelAlerter<List<ReservationSpec>> alarmExecutor) {
		final List<ReservationSpec> reservedSpecs = getReservedSpecsByAssetId(assetId);
		reservedSpecs.forEach(spec -> cancelAndAdjust(spec, spec.getAmount().getAmount()));
		alarmExecutor.alert(reservedSpecs);
	}

	private List<ReservationSpec> getReservedSpecsByAssetId(final Long assetId) {
		List<ReservationSpec> reservedOrRentedSpecs = reservationSpecRepository.findReservedOrRentedByAssetId(
			assetId);
		validateAllReserved(reservedOrRentedSpecs);
		return reservedOrRentedSpecs;
	}

	private void validateAllReserved(final List<ReservationSpec> reservedOrRentedSpecs) {
		boolean anyRented = reservedOrRentedSpecs.stream()
			.anyMatch(ReservationSpec::isRented);
		if (anyRented)
			throw new ReservationSpecException("대여 중인 대여 예약 상세는 취소할 수 없습니다.");
	}
}
