package com.girigiri.kwrental.reservation.domain;

import java.time.LocalDate;
import java.util.List;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.exception.IllegalRentDateException;
import com.girigiri.kwrental.reservation.exception.LabRoomReservationNotRentedWhenReturnException;
import com.girigiri.kwrental.reservation.exception.LabRoomReservationNotReservedWhenAcceptException;
import com.girigiri.kwrental.reservation.exception.LabRoomReservationSpecNotOneException;

import lombok.Getter;

@Getter
public class LabRoomReservation {

	private final Reservation reservation;

	public LabRoomReservation(final Reservation reservation) {
		final List<ReservationSpec> specs = reservation.getReservationSpecs();
		if (specs.size() != 1)
			throw new LabRoomReservationSpecNotOneException();
		this.reservation = reservation;
	}

	public void validateWhenRent() {
		final List<ReservationSpec> specs = reservation.getReservationSpecs();
		if (!specs.stream().allMatch(ReservationSpec::isReserved))
			throw new LabRoomReservationNotReservedWhenAcceptException();
		if (!specs.stream().allMatch(spec -> spec.containsDate(LocalDate.now())))
			throw new IllegalRentDateException();
	}

	public void validateWhenReturn() {
		final List<ReservationSpec> specs = reservation.getReservationSpecs();
		if (!specs.stream().allMatch(ReservationSpec::isRented))
			throw new LabRoomReservationNotRentedWhenReturnException();
	}

	public Long getReservationSpecId() {
		return getReservationSpec().getId();
	}

	public ReservationSpec getReservationSpec() {
		return reservation.getReservationSpecs().iterator().next();
	}

	public Long getId() {
		return reservation.getId();
	}

	public void normalReturnAll() {
		final List<ReservationSpec> specs = reservation.getReservationSpecs();
		specs.forEach(spec -> spec.setStatus(ReservationSpecStatus.RETURNED));
		reservation.updateIfTerminated();
	}

	public Long getLabRoomId() {
		return getReservationSpec().getRentable().getId();
	}

	public RentalPeriod getPeriod() {
		return getReservationSpec().getPeriod();
	}

	public boolean has(Rentable rentable, RentalPeriod period) {
		ReservationSpec spec = getReservationSpec();
		return spec.getRentable().getId().equals(rentable.getId())
			&& spec.hasPeriod(period);
	}
}
