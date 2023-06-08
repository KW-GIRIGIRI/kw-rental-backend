package com.girigiri.kwrental.inventory.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.service.EquipmentDeleteEvent;

@Component
public class InventoryEventListener {
	private final InventoryService inventoryService;

	public InventoryEventListener(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@EventListener
	@Transactional(propagation = Propagation.MANDATORY)
	public void handleEquipmentDelete(final EquipmentDeleteEvent event) {
		inventoryService.deleteByEquipmentId(event.getAssetId());
	}
}
