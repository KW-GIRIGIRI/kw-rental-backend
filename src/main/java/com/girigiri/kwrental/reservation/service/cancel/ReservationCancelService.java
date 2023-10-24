package com.girigiri.kwrental.reservation.service.cancel;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationCancelService {
	private final ReservationCancelTrigger reservationCancelTrigger;

	public Long cancelReservationSpecByAdmin(final Long reservationSpecId, final Integer amount) {
		return reservationCancelTrigger.triggerByAdminCancelReservationSpec(reservationSpecId, amount);
	}
}
