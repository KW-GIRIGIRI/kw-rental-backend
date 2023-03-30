package com.girigiri.kwrental.equipment.service;

import com.girigiri.kwrental.equipment.dto.request.AddItemRequest;
import java.util.List;

public interface ItemService {

    void saveItems(final Long equipmentId, List<AddItemRequest> itemRequests);
}
