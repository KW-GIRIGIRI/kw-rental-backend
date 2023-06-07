package com.girigiri.kwrental.asset.domain;

import com.girigiri.kwrental.common.SuperEntity;

public interface Rentable extends SuperEntity {
    boolean canRentDaysFor(Integer rentalDays);

    Long getId();

    String getName();

    Integer getTotalQuantity();

    Integer getRentableQuantity();

    Integer getMaxRentalDays();

    void validateAmountForRent(int amount);
}
