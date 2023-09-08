package com.girigiri.kwrental.reservation.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.service.AssetDeleteEvent;
import com.girigiri.kwrental.reservation.service.cancel.ReservationCancelTrigger;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {

	private final ReservationCancelTrigger reservationCancelTrigger;

	@EventListener
	@Transactional(propagation = Propagation.MANDATORY)
	public void handleAssetDelete(final AssetDeleteEvent event) {
		reservationCancelTrigger.triggerByAssetDelete(event.getAssetId());
	}
}
