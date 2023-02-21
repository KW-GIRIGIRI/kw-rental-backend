package com.girigiri.kwrental.equipment.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record EquipmentsPageResponse(
        String nextLink,
        String previousLink,
        Integer page,
        List<EquipmentResponse> items
) {
}
