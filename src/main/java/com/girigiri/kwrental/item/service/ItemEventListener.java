package com.girigiri.kwrental.item.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.service.EquipmentDeleteEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class ItemEventListener {

	private final ItemService itemService;

	@EventListener
	public void handleEquipmentDelete(final EquipmentDeleteEvent event) {
		itemService.deleteByAssetId(event.getAssetId());
	}
}
