package com.girigiri.kwrental.equipment.dto.request;

import jakarta.validation.Valid;
import java.util.List;

public record AddEquipmentWithItemsRequest(
        @Valid AddEquipmentRequest equipment,
        List<AddItemRequest> items
) {
}
