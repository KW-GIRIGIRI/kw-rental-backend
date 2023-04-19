package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.RentalSpec;

import java.util.List;

public interface RentalSpecRepositoryCustom {
    List<RentalSpec> findOverlappedByPeriod(Long equipmentId, RentalPeriod rentalPeriod);
}
