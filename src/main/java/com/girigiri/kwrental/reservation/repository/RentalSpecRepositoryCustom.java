package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.dto.ReservedAmount;

import java.time.LocalDate;
import java.util.List;

public interface RentalSpecRepositoryCustom {
    List<ReservationSpec> findOverlappedByPeriod(Long equipmentId, RentalPeriod rentalPeriod);

    List<ReservedAmount> findRentalAmountsByEquipmentIds(List<Long> equipmentIds, LocalDate date);

    List<ReservationSpec> findByStartDateBetween(Long equipmentId, LocalDate start, LocalDate end);
}
