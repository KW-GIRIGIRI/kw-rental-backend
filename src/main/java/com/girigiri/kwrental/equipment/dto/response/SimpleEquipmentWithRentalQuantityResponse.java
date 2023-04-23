package com.girigiri.kwrental.equipment.dto.response;

import com.girigiri.kwrental.equipment.domain.Equipment;
import lombok.Builder;

@Builder
public record SimpleEquipmentWithRentalQuantityResponse(
        Long id,
        String category,
        String maker,
        String modelName,
        RentalQuantityResponse rentalQuantity,
        String imgUrl
) {
    public static SimpleEquipmentWithRentalQuantityResponse from(final Equipment equipment, final int remainingQuantity) {
        return SimpleEquipmentWithRentalQuantityResponse.builder()
                .id(equipment.getId())
                .category(equipment.getCategory().name())
                .maker(equipment.getMaker())
                .modelName(equipment.getModelName())
                .rentalQuantity(new RentalQuantityResponse(equipment.getTotalQuantity(), remainingQuantity))
                .imgUrl(equipment.getImgUrl())
                .build();
    }
}
