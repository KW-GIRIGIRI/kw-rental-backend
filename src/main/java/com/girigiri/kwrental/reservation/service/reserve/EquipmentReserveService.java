package com.girigiri.kwrental.reservation.service.reserve;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.dto.request.AddEquipmentReservationRequest;
import com.girigiri.kwrental.reservation.service.reserve.creator.EquipmentReservationCreator;
import com.girigiri.kwrental.reservation.service.reserve.template.ReserveTemplate;
import com.girigiri.kwrental.reservation.service.reserve.template.ReserveValidator;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EquipmentReserveService {

	private final EquipmentReservationCreator equipmentReservationCreator;
	private final ReserveTemplate reserveTemplate;

	public void reserveEquipment(final Long memberId, final AddEquipmentReservationRequest addReservationRequest) {
		final List<Reservation> reservations = equipmentReservationCreator.create(memberId, addReservationRequest);
		reserveTemplate.reserve(memberId, reservations, ReserveValidator.noExtraValidation());
	}
}
