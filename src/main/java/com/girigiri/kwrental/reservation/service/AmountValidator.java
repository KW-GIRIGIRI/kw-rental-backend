package com.girigiri.kwrental.reservation.service;

import com.girigiri.kwrental.reservation.domain.RentalPeriod;

public interface AmountValidator {

    void validateAmount(Long assetId, Integer amount, final RentalPeriod rentalPeriod);
}
