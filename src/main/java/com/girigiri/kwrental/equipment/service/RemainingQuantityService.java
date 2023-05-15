package com.girigiri.kwrental.equipment.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RemainingQuantityService {

    Map<Long, Integer> getRemainingQuantityByEquipmentIdAndDate(List<Long> equipmentIds, LocalDate date);

    Map<LocalDate, Integer> getReservedAmountBetween(Long equipmentId, LocalDate from, LocalDate to);
}
