package com.girigiri.kwrental.asset.equipment.dto.response;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;

import lombok.Builder;

@Builder
public record SimpleEquipmentResponse(
        Long id,
        String category,
        String maker,
        String modelName,
        Integer totalQuantity,
        String imgUrl
) {
    public static SimpleEquipmentResponse from(final Equipment equipment) {
        return SimpleEquipmentResponse.builder()
                .id(equipment.getId())
                .category(equipment.getCategory().name())
                .imgUrl(equipment.getImgUrl())
                .modelName(equipment.getName())
                .maker(equipment.getMaker())
                .totalQuantity(equipment.getTotalQuantity())
                .build();
    }
}
