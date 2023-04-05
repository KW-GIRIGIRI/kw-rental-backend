package com.girigiri.kwrental.inventory.repository;

import com.girigiri.kwrental.inventory.domain.Inventory;
import org.springframework.data.repository.Repository;

public interface InventoryRepository extends Repository<Inventory, Long> {

    Inventory save(Inventory inventory);
}
