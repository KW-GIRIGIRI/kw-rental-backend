package com.girigiri.kwrental.equipment.dto.request;

public record AddEquipmentRequest(
        String rentalPlace,
        String modelName,
        String category,
        String maker,
        String imgUrl,
        String components,
        String purpose,
        String description,
        Integer maxRentalDays
) {
}
