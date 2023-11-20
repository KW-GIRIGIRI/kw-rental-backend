package com.girigiri.kwrental.item.repository.jpa;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.girigiri.kwrental.asset.equipment.domain.Category;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.response.EquipmentItemDto;
import com.girigiri.kwrental.item.service.propertynumberupdate.ToBeUpdatedItem;

public interface ItemQueryDslRepositoryCustom {

    int countAvailable(Long equipmentId);

    List<Item> findByEquipmentIds(Set<Long> equipmentIds);

    int deleteByPropertyNumbers(List<String> propertyNumbers);

    Page<EquipmentItemDto> findEquipmentItem(Pageable pageable, Category category);

    int updateAvailable(List<Long> ids, boolean available);

    int deleteByIdIn(Collection<Long> ids);

    int deleteById(Long id);

    List<Item> findByAssetId(Long assetId);

    List<Item> findByPropertyNumbers(List<String> propertyNumbers);

    int updatePropertyNumbers(List<ToBeUpdatedItem> toBeUpdatedItems);

    List<Item> findByIds(Collection<Long> ids);
}
