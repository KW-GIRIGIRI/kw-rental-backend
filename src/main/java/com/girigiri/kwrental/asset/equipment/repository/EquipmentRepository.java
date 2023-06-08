package com.girigiri.kwrental.asset.equipment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.repository.AssetRepositoryCustom;

public interface EquipmentRepository extends Repository<Equipment, Long>, EquipmentRepositoryCustom,
    AssetRepositoryCustom {
    @Transactional
    @Modifying
    @Query("update Equipment e set e.totalQuantity = ?1, e.rentableQuantity = ?2 where e.id = ?3")
    int updateTotalQuantityAndRentableQuantityById(Integer totalQuantity, Integer rentableQuantity, Long id);

    Equipment save(Equipment equipment);

    Optional<Equipment> findById(Long id);
}
