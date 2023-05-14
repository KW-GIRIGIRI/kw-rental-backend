package com.girigiri.kwrental.item.service;

import com.girigiri.kwrental.item.dto.response.RentalCountsDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public interface RentedItemService {

    Set<String> getRentedPropertyNumbers(Long equipmentId, LocalDateTime date);

    Map<String, RentalCountsDto> getRentalCountsByPropertyNumbersBetweenDate(Set<String> propertyNumbers, LocalDate from, LocalDate to);
}
