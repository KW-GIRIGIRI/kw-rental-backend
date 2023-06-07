package com.girigiri.kwrental.asset.equipment.service;

import com.girigiri.kwrental.asset.service.AssetDeleteEvent;

public class EquipmentDeleteEvent extends AssetDeleteEvent {

	private final Long equipmentId;

	public EquipmentDeleteEvent(final Object source, final Long equipmentId) {
		super(source);
		this.equipmentId = equipmentId;
	}

	@Override
	public Long getAssetId() {
		return this.equipmentId;
	}
}
