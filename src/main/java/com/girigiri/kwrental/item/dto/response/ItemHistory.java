package com.girigiri.kwrental.item.dto.response;

import com.girigiri.kwrental.equipment.domain.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemHistory {

    private final Category category;
    private final String modelName;
    private final String propertyNumber;
    private final Integer normalRentalCount;
    private final Integer abnormalRentalCount;

    public ItemHistory(final Category category, final String modelName, final String propertyNumber, final Integer normalRentalCount, final Integer abnormalRentalCount) {
        this.category = category;
        this.modelName = modelName;
        this.propertyNumber = propertyNumber;
        this.normalRentalCount = normalRentalCount;
        this.abnormalRentalCount = abnormalRentalCount;
    }
}
