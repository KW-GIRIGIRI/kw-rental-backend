package com.girigiri.kwrental.inventory.service;

import com.girigiri.kwrental.inventory.domain.RentalPeriod;

public interface AmountValidator {

    void validateAmount(Long assetId, Integer amount, final RentalPeriod rentalPeriod);
}
