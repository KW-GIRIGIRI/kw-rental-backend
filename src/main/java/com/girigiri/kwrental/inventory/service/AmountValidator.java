package com.girigiri.kwrental.inventory.service;

import com.girigiri.kwrental.inventory.domain.RentalPeriod;

// TODO: 2023/04/05 구현체를 만들어야 한다.
public interface AmountValidator {

    void validateAmount(Long equipmentId, Integer amount, final RentalPeriod rentalPeriod);
}
