package com.girigiri.kwrental.item.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.service.EquipmentDeleteEvent;

@Component
public class ItemEventListener {

	private final ItemService itemService;

	public ItemEventListener(ItemService itemService) {
		this.itemService = itemService;
	}

	@EventListener
	@Transactional(propagation = Propagation.MANDATORY)
	public void handleEquipmentDelete(final EquipmentDeleteEvent event) {
		itemService.deleteByAssetId(event.getAssetId());
	}
}
