package com.girigiri.kwrental.reservation.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.service.AssetDeleteEvent;

@Component
public class ReservationEventListener {

	private final ReservationService reservationService;

	public ReservationEventListener(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	@EventListener
	@Transactional(propagation = Propagation.MANDATORY)
	public void handleAssetDelete(final AssetDeleteEvent event) {
		reservationService.cancelByAssetId(event.getAssetId());
	}
}
