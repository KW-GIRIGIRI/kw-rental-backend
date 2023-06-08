package com.girigiri.kwrental.asset.equipment.dto.request;

import java.util.List;

import jakarta.validation.Valid;

public record AddEquipmentWithItemsRequest(
	@Valid AddEquipmentRequest equipment,
	List<AddItemRequest> items
) {
}
