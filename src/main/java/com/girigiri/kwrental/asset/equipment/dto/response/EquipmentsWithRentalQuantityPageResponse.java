package com.girigiri.kwrental.asset.equipment.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record EquipmentsWithRentalQuantityPageResponse(
        List<String> endPoints,
        Integer page,
        List<SimpleEquipmentWithRentalQuantityResponse> items
) {
}
