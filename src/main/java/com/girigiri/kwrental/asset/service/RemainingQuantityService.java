package com.girigiri.kwrental.asset.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RemainingQuantityService {

    Map<Long, Integer> getRemainingQuantityByEquipmentIdAndDate(List<Long> rentableIds, LocalDate date);

    Map<LocalDate, Integer> getReservedAmountBetween(Long rentableId, LocalDate from, LocalDate to);
}
