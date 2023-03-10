package com.girigiri.kwrental.equipment.dto;

import com.girigiri.kwrental.equipment.domain.Equipment;
import lombok.Builder;

@Builder
public record EquipmentDetailResponse(long id, String rentalPlace, String modelName,
                                      String category, String maker, String imgUrl,
                                      String components, String purpose, String description,
                                      RentalQuantityResponse rentalQuantity) {

    public static EquipmentDetailResponse from(final Equipment equipment) {
        return EquipmentDetailResponse.builder()
                .id(equipment.getId())
                .category(equipment.getCategory().name())
                .maker(equipment.getMaker())
                .modelName(equipment.getModelName())
                .imgUrl(equipment.getImgUrl())
                .description(equipment.getDescription())
                .components(equipment.getComponents())
                .rentalPlace(equipment.getRentalPlace())
                .purpose(equipment.getPurpose())
                .rentalQuantity(RentalQuantityResponse.from(equipment.getRentalQuantity()))
                .build();
    }
}
