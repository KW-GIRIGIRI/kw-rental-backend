package com.girigiri.kwrental.equipment.dto.request;

import com.girigiri.kwrental.equipment.domain.Category;

public record EquipmentSearchCondition(String keyword, Category category) {
}
