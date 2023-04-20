package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.ReservedAmount;

import java.time.LocalDate;
import java.util.List;

public interface RentalSpecRepositoryCustom {
    List<RentalSpec> findOverlappedByPeriod(Long equipmentId, RentalPeriod rentalPeriod);

    List<ReservedAmount> findRentalAmountsByEquipmentIds(List<Long> equipmentIds, LocalDate date);
}
