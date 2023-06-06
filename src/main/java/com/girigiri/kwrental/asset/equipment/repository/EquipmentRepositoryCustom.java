package com.girigiri.kwrental.asset.equipment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.girigiri.kwrental.asset.equipment.domain.Category;
import com.girigiri.kwrental.asset.equipment.domain.Equipment;

public interface EquipmentRepositoryCustom {

    Page<Equipment> findEquipmentBy(Pageable pageable, String keyword, Category category);
}
