package com.girigiri.kwrental.reservation.service.creator;

import java.util.List;

import org.springframework.stereotype.Service;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.asset.labroom.exception.LabRoomNotAvailableException;
import com.girigiri.kwrental.asset.labroom.service.LabRoomService;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.dto.request.AddLabRoomReservationRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LabRoomReservationCreator {
	private final LabRoomService labRoomService;

	public Reservation create(final Long memberId, final AddLabRoomReservationRequest addLabRoomReservationRequest) {
		final LabRoom labRoom = labRoomService.getLabRoomByName(addLabRoomReservationRequest.labRoomName());
		final RentalPeriod period = new RentalPeriod(addLabRoomReservationRequest.startDate(),
			addLabRoomReservationRequest.endDate());
		validateLabRoomForReserve(labRoom, period);
		final RentalAmount amount = RentalAmount.ofPositive(addLabRoomReservationRequest.renterCount());
		final ReservationSpec spec = mapToReservationSpec(labRoom, period, amount);
		return mapToReservation(memberId, addLabRoomReservationRequest, spec);
	}

	private void validateLabRoomForReserve(final LabRoom labRoom, RentalPeriod period) {
		labRoomService.validateDays(labRoom, period.getRentalDays());
		if (!labRoom.isAvailable())
			throw new LabRoomNotAvailableException();
	}

	private ReservationSpec mapToReservationSpec(final Rentable rentable, final RentalPeriod period,
		final RentalAmount amount) {
		return ReservationSpec.builder()
			.period(period)
			.amount(amount)
			.rentable(rentable)
			.build();
	}

	private Reservation mapToReservation(final Long memberId,
		final AddLabRoomReservationRequest addLabRoomReservationRequest, final ReservationSpec spec) {
		return Reservation.builder()
			.reservationSpecs(List.of(spec))
			.memberId(memberId)
			.email(addLabRoomReservationRequest.renterEmail())
			.name(addLabRoomReservationRequest.renterName())
			.purpose(addLabRoomReservationRequest.rentalPurpose())
			.phoneNumber(addLabRoomReservationRequest.renterPhoneNumber())
			.build();
	}
}
