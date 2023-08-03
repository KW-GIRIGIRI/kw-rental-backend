package com.girigiri.kwrental.asset.domain;

import com.girigiri.kwrental.common.SuperEntity;

public interface Rentable extends SuperEntity {
    boolean canRentDaysFor(Integer rentalDays);

    void validateAmountForRent(int amount);

    Integer getRemainQuantity(int amount);
}
