package com.girigiri.kwrental.inventory.repository;

import com.girigiri.kwrental.inventory.domain.Inventory;

import java.util.List;

public interface InventoryRepositoryCustom {
    List<Inventory> findAllWithEquipment();

    int deleteAll();
}
