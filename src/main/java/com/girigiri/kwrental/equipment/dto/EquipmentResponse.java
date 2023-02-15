package com.girigiri.kwrental.equipment.dto;

import com.girigiri.kwrental.equipment.Equipment;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record EquipmentResponse(long id,
                                String category,
                                String maker,
                                String modelName,
                                int totalQuantity,
                                int remainingQuantity,
                                String imgUrl,
                                LocalTime availableTimeFrom,
                                LocalTime availableTimeTo,
                                String description) {

    public static EquipmentResponse from(final Equipment equipment) {
        return EquipmentResponse.builder()
                .id(equipment.getId())
                .category(equipment.getCategory().name())
                .maker(equipment.getMaker())
                .modelName(equipment.getModelName())
                .totalQuantity(equipment.getTotalQuantity())
                .remainingQuantity(equipment.getRemainingQuantity())
                .imgUrl(equipment.getImgUrl())
                .availableTimeFrom(equipment.getAvailableTimeFrom())
                .availableTimeTo(equipment.getAvailableTimeTo())
                .description(equipment.getDescription())
                .build();
    }
}
