package com.girigiri.kwrental.item.repository;

import com.girigiri.kwrental.item.domain.Item;

import java.util.List;
import java.util.Set;

public interface ItemQueryDslRepositoryCustom {

    int updateRentalAvailable(Long id, boolean rentalAvailable);

    int updatePropertyNumber(Long id, String propertyNumber);

    int countAvailable(Long equipmentId);

    List<Item> findByEquipmentIds(Set<Long> equipmentIds);
}
