package com.girigiri.kwrental.reservation.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.service.AssetDeleteEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {

	private final ReservationService reservationService;

	@EventListener
	@Transactional(propagation = Propagation.MANDATORY)
	public void handleAssetDelete(final AssetDeleteEvent event) {
		reservationService.cancelByAssetId(event.getAssetId());
	}
}
