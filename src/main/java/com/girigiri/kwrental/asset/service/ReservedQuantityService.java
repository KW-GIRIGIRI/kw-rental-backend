package com.girigiri.kwrental.asset.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReservedQuantityService {

	Map<Long, Integer> getRemainingQuantityByAssetIdAndDate(List<Long> rentableIds, LocalDate date);

	Map<LocalDate, Integer> getReservedAmountInclusive(Long rentableId, LocalDate from, LocalDate to);

	Map<LocalDate, Integer> getReservationCountInclusive(
		Long rentableId, LocalDate from, LocalDate to);
}
