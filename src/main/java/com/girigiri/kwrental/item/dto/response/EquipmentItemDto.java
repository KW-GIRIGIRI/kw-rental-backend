package com.girigiri.kwrental.item.dto.response;

import com.girigiri.kwrental.asset.equipment.domain.Category;

import lombok.Getter;

@Getter
public class EquipmentItemDto {

    private final String modelName;
    private final Category category;
    private final String propertyNumber;

    public EquipmentItemDto(final String modelName, final Category category, final String propertyNumber) {
        this.modelName = modelName;
        this.category = category;
        this.propertyNumber = propertyNumber;
    }
}
