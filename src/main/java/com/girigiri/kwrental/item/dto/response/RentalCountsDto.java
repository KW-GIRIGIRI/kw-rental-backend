package com.girigiri.kwrental.item.dto.response;

import lombok.Getter;

@Getter
public class RentalCountsDto {

    private final String propertyNumber;
    private final Integer normalRentalCount;
    private final Integer abnormalRentalCount;

    public RentalCountsDto(final String propertyNumber, final Integer normalRentalCount, final Integer abnormalRentalCount) {
        this.propertyNumber = propertyNumber;
        this.normalRentalCount = normalRentalCount;
        this.abnormalRentalCount = abnormalRentalCount;
    }
}
