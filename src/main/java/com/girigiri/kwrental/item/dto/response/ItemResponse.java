package com.girigiri.kwrental.item.dto.response;

import com.girigiri.kwrental.item.domain.Item;

public record ItemResponse(
        Long id,
        String propertyNumber,
        boolean rentalAvailable,
        Long equipmentId
) {
    public static ItemResponse from(final Item item) {
        return new ItemResponse(item.getId(), item.getPropertyNumber(), item.isAvailable(),
                item.getEquipmentId());
    }
}
