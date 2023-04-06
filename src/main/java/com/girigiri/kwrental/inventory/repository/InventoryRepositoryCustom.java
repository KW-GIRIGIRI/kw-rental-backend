package com.girigiri.kwrental.inventory.repository;

import com.girigiri.kwrental.inventory.domain.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepositoryCustom {
    List<Inventory> findAllWithEquipment();

    int deleteAll();

    Optional<Inventory> findWithEquipmentById(Long id);
}
