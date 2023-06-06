package com.girigiri.kwrental.item.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.girigiri.kwrental.asset.equipment.domain.Category;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.response.EquipmentItemDto;

public interface ItemQueryDslRepositoryCustom {

    int updateRentalAvailable(Long id, boolean rentalAvailable);

    int updatePropertyNumber(Long id, String propertyNumber);

    int countAvailable(Long equipmentId);

    List<Item> findByEquipmentIds(Set<Long> equipmentIds);

    long deleteByPropertyNumbers(List<String> propertyNumbers);

    Page<EquipmentItemDto> findEquipmentItem(Pageable pageable, Category category);
}
