package com.girigiri.kwrental.rental.domain;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.girigiri.kwrental.rental.domain.entity.RentalSpec;
import com.girigiri.kwrental.rental.exception.RentalSpecNotFoundException;
import com.girigiri.kwrental.rental.exception.RentedStatusForReturnException;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;

public class Rental {

	private final Map<Long, RentalSpec> rentalSpecMap;
	private final ReservationFromRental reservationFromRental;

	private Rental(final Map<Long, RentalSpec> rentalSpecMap,
		final ReservationFromRental reservationFromRental) {
		this.rentalSpecMap = rentalSpecMap;
		this.reservationFromRental = reservationFromRental;
	}

	public static Rental of(final List<? extends RentalSpec> rentalSpecs, final Reservation reservation) {
		final Map<Long, RentalSpec> rentalSpecMap = rentalSpecs.stream()
			.collect(toMap(RentalSpec::getId, Function.identity()));
		final ReservationFromRental reservationFromRental = ReservationFromRental.from(reservation);
		return new Rental(rentalSpecMap, reservationFromRental);
	}

	public void restore(final Map<Long, RentalSpecStatus> returnRequest, final PostEachModify... postEachModifies) {
		modify(returnRequest, this::restoreRentalSpec, postEachModifies);
	}

	private void restoreRentalSpec(final RentalSpec rentalSpec, final RentalSpecStatus status) {
		final LocalDateTime returnDateTime = LocalDateTime.now();
		setStatusForRestore(rentalSpec, status);
		rentalSpec.setReturnDateTimeIfAnyReturned(returnDateTime);
	}

	private void setStatusForRestore(final RentalSpec rentalSpec, final RentalSpecStatus status) {
		if (status == RentalSpecStatus.RENTED)
			throw new RentedStatusForReturnException();
		final boolean nowIsLegalForReturn = reservationFromRental.nowIsLegalForReturn(
			rentalSpec.getReservationSpecId());
		if (status == RentalSpecStatus.RETURNED && !nowIsLegalForReturn) {
			rentalSpec.setStatus(RentalSpecStatus.OVERDUE_RETURNED);
			return;
		}
		rentalSpec.setStatus(status);
	}

	public void normalRestoreAll(final PostEachModify... postEachModifies) {
		final Map<Long, RentalSpecStatus> normalRestoreRequest = rentalSpecMap.keySet().stream()
			.collect(toMap(Function.identity(), it -> RentalSpecStatus.RETURNED));
		modify(normalRestoreRequest, this::setStatusNormalReturned, postEachModifies);
	}

	private void setStatusNormalReturned(final RentalSpec rentalSpec, final RentalSpecStatus rentalSpecStatus) {
		rentalSpec.setStatus(RentalSpecStatus.RETURNED);
		rentalSpec.setReturnDateTimeIfAnyReturned(LocalDateTime.now());
	}

	public void update(final Map<Long, RentalSpecStatus> updateRequest, final PostEachModify... postEachModifies) {
		modify(updateRequest, this::setStatusForUpdate, postEachModifies);
	}

	private void setStatusForUpdate(final RentalSpec rentalSpec, final RentalSpecStatus status) {
		if (status == RentalSpecStatus.RENTED)
			throw new RentedStatusForReturnException();
		rentalSpec.setStatus(status);
	}

	private void modify(final Map<Long, RentalSpecStatus> request,
		final BiConsumer<RentalSpec, RentalSpecStatus> modifyAction, final PostEachModify... postEachModifies) {
		for (final Map.Entry<Long, RentalSpecStatus> entry : request.entrySet()) {
			final RentalSpec rentalSpec = getRentalSpec(entry.getKey());
			final RentalSpecStatus status = entry.getValue();
			modifyAction.accept(rentalSpec, status);
			executeAll(postEachModifies, rentalSpec);
		}
		setReservationStatusAfterModification();
	}

	private RentalSpec getRentalSpec(final Long id) {
		final RentalSpec rentalSpec = rentalSpecMap.get(id);
		if (rentalSpec == null)
			throw new RentalSpecNotFoundException();
		return rentalSpec;
	}

	private void executeAll(final PostEachModify[] postEachModifies, final RentalSpec rentalSpec) {
		final Reservation reservation = reservationFromRental.getReservation();
		for (PostEachModify executor : postEachModifies) {
			executor.execute(rentalSpec, reservation);
		}
	}

	private void setReservationStatusAfterModification() {
		final Map<Long, List<RentalSpecStatus>> rentalStatusPerReservationSpecId = groupRentalStatusByReservationSpecId();
		reservationFromRental.setStatusAfterReturn(rentalStatusPerReservationSpecId);
	}

	private Map<Long, List<RentalSpecStatus>> groupRentalStatusByReservationSpecId() {
		return rentalSpecMap.values().stream()
			.collect(
				groupingBy(RentalSpec::getReservationSpecId, mapping(RentalSpec::getStatus, toList())));
	}
}
