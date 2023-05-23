package com.girigiri.kwrental.asset.domain;

import com.girigiri.kwrental.common.SuperEntity;

public interface Rentable extends SuperEntity {
    boolean canRentFor(Integer rentalDays);

    Long getId();

    String getName();

    Integer getTotalQuantity();

    Integer getMaxRentalDays();
}
