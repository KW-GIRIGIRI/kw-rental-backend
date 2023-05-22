package com.girigiri.kwrental.asset.domain;

public interface Rentable {
    boolean canRentFor(Integer rentalDays);

    Long getId();

    String getName();

    Integer getTotalQuantity();

    Integer getMaxRentalDays();

    <T extends Rentable> T as(Class<T> clazz);
}
