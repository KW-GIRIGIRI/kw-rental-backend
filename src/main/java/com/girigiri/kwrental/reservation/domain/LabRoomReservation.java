package com.girigiri.kwrental.reservation.domain;

import java.time.LocalDate;
import java.util.List;

import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.exception.LabRoomReservationException;

import lombok.Getter;

@Getter
public class LabRoomReservation {

	private final Reservation reservation;

	public LabRoomReservation(final Reservation reservation) {
		final List<ReservationSpec> specs = reservation.getReservationSpecs();
		if (specs.size() != 1)
			throw new LabRoomReservationException("랩실 대여 예약 상세는 하나여야 합니다.");
		this.reservation = reservation;
	}

	public void validateCanRentNow() {
		final List<ReservationSpec> specs = reservation.getReservationSpecs();
		if (!specs.stream().allMatch(ReservationSpec::isReserved))
			throw new LabRoomReservationException("랩실 대여를 하려는 예약 상세는 예약 상태여야 합니다.");
		if (!specs.stream().allMatch(spec -> spec.containsDate(LocalDate.now())))
			throw new LabRoomReservationException("대여 수령 날짜가 대여 신청 기간에 없습니다.");
	}

	public void validateLabRoomName(final String labRoomName) {
		final boolean onlyRentFor = this.reservation.isOnlyRentFor(labRoomName);
		if (!onlyRentFor)
			throw new LabRoomReservationException("랩실 대여 예약의 랩실 이름이 불일치 합니다.");
	}

	public boolean has(final RentalPeriod period) {
		ReservationSpec spec = getReservationSpec();
		return spec.hasPeriod(period);
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

	public Long getLabRoomId() {
		return getReservationSpec().getRentable().getId();
	}

	public RentalPeriod getPeriod() {
		return getReservationSpec().getPeriod();
	}
}
