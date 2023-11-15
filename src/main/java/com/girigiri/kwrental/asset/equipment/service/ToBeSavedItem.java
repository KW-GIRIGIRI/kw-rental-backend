package com.girigiri.kwrental.asset.equipment.service;

import com.girigiri.kwrental.item.domain.Item;

public record ToBeSavedItem(String propertyNumber, Long assetId) {

	public Item mapToEntity() {
		return Item.builder()
			.propertyNumber(this.propertyNumber)
			.assetId(this.assetId)
			.available(true)
			.build();
	}
}
