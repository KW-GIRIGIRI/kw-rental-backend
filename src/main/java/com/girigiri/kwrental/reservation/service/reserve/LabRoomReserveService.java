package com.girigiri.kwrental.reservation.service.reserve;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.dto.request.AddLabRoomReservationRequest;
import com.girigiri.kwrental.reservation.service.ReservationValidator;
import com.girigiri.kwrental.reservation.service.reserve.creator.LabRoomReservationCreator;
import com.girigiri.kwrental.reservation.service.reserve.template.ReserveTemplate;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LabRoomReserveService {

	private final LabRoomReservationCreator labRoomReservationCreator;
	private final ReserveTemplate reserveTemplate;
	private final ReservationValidator reservationValidator;

	public Long reserveLabRoom(final Long memberId, final AddLabRoomReservationRequest addLabRoomReservationRequest) {
		final Reservation reservation = labRoomReservationCreator.create(memberId, addLabRoomReservationRequest);
		reserveTemplate.reserve(memberId, List.of(reservation),
			reservationValidator::validateAlreadyReservedSamePeriod);
		return reservation.getId();
	}
}
