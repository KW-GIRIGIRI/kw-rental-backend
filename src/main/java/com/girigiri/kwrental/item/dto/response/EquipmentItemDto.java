package com.girigiri.kwrental.item.dto.response;

import com.girigiri.kwrental.asset.equipment.domain.Category;

public record EquipmentItemDto(String modelName, Category category, String propertyNumber) {

}
