package com.girigiri.kwrental.reservation.service.reserve.creator;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.dto.request.AddEquipmentReservationRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EquipmentReservationCreator {
	private final ReservationSpecMapper reservationSpecMapper;

	public List<Reservation> create(final Long memberId,
		final AddEquipmentReservationRequest addReservationRequest) {
		final List<ReservationSpec> specs = reservationSpecMapper.map(memberId);
		return mapToReservations(memberId, addReservationRequest, specs);
	}

	private List<Reservation> mapToReservations(final Long memberId,
		final AddEquipmentReservationRequest addReservationRequest,
		final List<ReservationSpec> reservationSpecs) {
		final Map<RentalPeriod, List<ReservationSpec>> collectByPeriod = reservationSpecs.stream()
			.collect(groupingBy(ReservationSpec::getPeriod));
		return collectByPeriod.keySet()
			.stream()
			.map(key -> mapToReservation(memberId, addReservationRequest, collectByPeriod.get(key)))
			.toList();
	}

	private Reservation mapToReservation(final Long memberId,
		final AddEquipmentReservationRequest addReservationRequest,
		final List<ReservationSpec> samePeriodSpec) {
		return Reservation.builder()
			.reservationSpecs(samePeriodSpec)
			.email(addReservationRequest.renterEmail())
			.name(addReservationRequest.renterName())
			.purpose(addReservationRequest.rentalPurpose())
			.phoneNumber(addReservationRequest.renterPhoneNumber())
			.memberId(memberId)
			.build();
	}
}
