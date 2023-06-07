package com.girigiri.kwrental.item.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import com.girigiri.kwrental.item.dto.response.RentalCountsDto;

public interface RentedItemService {

    Set<String> getRentedPropertyNumbers(Long equipmentId, LocalDateTime date);

    Map<String, RentalCountsDto> getRentalCountsByPropertyNumbersBetweenDate(Set<String> propertyNumbers,
        LocalDate from, LocalDate to);

    void updatePropertyNumber(final String propertyNumberBefore, final String updatedPropetyNumber);
}
