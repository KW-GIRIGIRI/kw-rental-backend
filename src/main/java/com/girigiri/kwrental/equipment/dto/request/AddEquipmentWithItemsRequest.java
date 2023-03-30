package com.girigiri.kwrental.equipment.dto.request;

import java.util.List;

public record AddEquipmentWithItemsRequest(
        AddEquipmentRequest equipment,
        List<AddItemRequest> items
) {
}
