package com.girigiri.kwrental.reservation.service.reserve.creator;

import java.util.List;

import org.springframework.stereotype.Component;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.asset.labroom.service.LabRoomRetriever;
import com.girigiri.kwrental.asset.labroom.service.LabRoomValidator;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.dto.request.AddLabRoomReservationRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LabRoomReservationCreator {
	private final LabRoomRetriever labRoomRetriever;
	private final LabRoomValidator labRoomValidator;

	public Reservation create(final Long memberId, final AddLabRoomReservationRequest addLabRoomReservationRequest) {
		final LabRoom labRoom = labRoomRetriever.getLabRoomByName(addLabRoomReservationRequest.labRoomName());
		final RentalPeriod period = new RentalPeriod(addLabRoomReservationRequest.startDate(),
			addLabRoomReservationRequest.endDate());
		validateLabRoomForReserve(labRoom, period);
		final RentalAmount amount = RentalAmount.ofPositive(addLabRoomReservationRequest.renterCount());
		final ReservationSpec spec = mapToReservationSpec(labRoom, period, amount);
		return mapToReservation(memberId, addLabRoomReservationRequest, spec);
	}

	private void validateLabRoomForReserve(final LabRoom labRoom, RentalPeriod period) {
		labRoomValidator.validateDays(labRoom, period.getRentalDays());
		labRoomValidator.validateAvailable(labRoom);
	}

	private ReservationSpec mapToReservationSpec(final LabRoom labRoom, final RentalPeriod period,
		final RentalAmount amount) {
		return ReservationSpec.builder()
			.period(period)
			.amount(amount)
			.asset(labRoom)
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
