package com.girigiri.kwrental.asset.equipment.service;

import java.util.List;

import com.girigiri.kwrental.asset.equipment.dto.request.AddItemRequest;

public interface SaveItemService {

	void saveItems(final Long equipmentId, List<AddItemRequest> itemRequests);
}
