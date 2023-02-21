package com.girigiri.kwrental.equipment.dto;

import com.girigiri.kwrental.equipment.Equipment;
import lombok.Builder;

@Builder
public record EquipmentResponse(
        Long id,
        String category,
        String maker,
        String modelName,
        RentalQuantityResponse rentalQuantity,
        String imgUrl
) {
    public static EquipmentResponse from(final Equipment equipment) {
        return EquipmentResponse.builder()
                .id(equipment.getId())
                .category(equipment.getCategory().name())
                .maker(equipment.getMaker())
                .modelName(equipment.getModelName())
                .rentalQuantity(RentalQuantityResponse.from(equipment.getRentalQuantity()))
                .imgUrl(equipment.getImgUrl())
                .build();
    }
}
