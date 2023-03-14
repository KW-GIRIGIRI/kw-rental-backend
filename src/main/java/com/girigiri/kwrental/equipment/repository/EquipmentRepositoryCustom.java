package com.girigiri.kwrental.equipment.repository;

import com.girigiri.kwrental.equipment.domain.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EquipmentRepositoryCustom {

    Page<Equipment> findEquipmentBy(Pageable pageable, String keyword);
}
