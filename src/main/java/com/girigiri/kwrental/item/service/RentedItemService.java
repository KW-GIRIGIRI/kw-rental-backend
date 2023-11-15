package com.girigiri.kwrental.item.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.item.dto.response.RentalCountsDto;
import com.girigiri.kwrental.item.service.propertynumberupdate.ToBeUpdatedItem;

public interface RentedItemService {

    Set<String> getRentedPropertyNumbers(Long equipmentId, LocalDateTime date);

    Map<String, RentalCountsDto> getRentalCountsByPropertyNumbersBetweenDate(Set<String> propertyNumbers,
        LocalDate from, LocalDate to);

	@Transactional(propagation = Propagation.MANDATORY)
	int updatePropertyNumbers(List<ToBeUpdatedItem> toBeUpdatedItems);

	void validateNotRentedByAssetId(Long assetId);

    void validateNotRentedByPropertyNumbers(Collection<String> propertyNumbers);
}
