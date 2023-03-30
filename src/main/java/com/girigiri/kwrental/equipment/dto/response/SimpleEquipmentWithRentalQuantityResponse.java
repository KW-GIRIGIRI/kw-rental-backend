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
    // TODO: 2023/03/30 남은 대여 갯수 로직 수정해야 함
    public static SimpleEquipmentWithRentalQuantityResponse from(final Equipment equipment) {
        return SimpleEquipmentWithRentalQuantityResponse.builder()
                .id(equipment.getId())
                .category(equipment.getCategory().name())
                .maker(equipment.getMaker())
                .modelName(equipment.getModelName())
                .rentalQuantity(new RentalQuantityResponse(2, 1))
                .imgUrl(equipment.getImgUrl())
                .build();
    }
}
