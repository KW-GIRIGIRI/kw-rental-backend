package com.girigiri.kwrental.equipment.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record EquipmentsPageResponse(
        List<String> endPoints,
        Integer page,
        List<EquipmentResponse> items
) {
}
