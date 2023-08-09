package com.girigiri.kwrental.item.dto.response;

import com.girigiri.kwrental.asset.equipment.domain.Category;

import lombok.Builder;

@Builder
public record ItemHistory(Category category, String modelName, String propertyNumber, Integer normalRentalCount,
                          Integer abnormalRentalCount) {

}
