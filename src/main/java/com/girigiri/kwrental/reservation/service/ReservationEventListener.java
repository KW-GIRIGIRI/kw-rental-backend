package com.girigiri.kwrental.reservation.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.girigiri.kwrental.asset.service.AssetDeleteEvent;
import com.girigiri.kwrental.reservation.service.cancel.ReservationCancelTrigger;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {

	private final ReservationCancelTrigger reservationCancelTrigger;

	@Transactional(propagation = Propagation.MANDATORY)
	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void handleAssetDelete(final AssetDeleteEvent event) {
		reservationCancelTrigger.triggerByAssetDelete(event.getAssetId());
	}
}
