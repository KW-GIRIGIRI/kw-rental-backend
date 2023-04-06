package com.girigiri.kwrental.inventory.repository;

import com.girigiri.kwrental.inventory.domain.Inventory;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface InventoryRepository extends Repository<Inventory, Long>, InventoryRepositoryCustom {

    Inventory save(Inventory inventory);

    Optional<Inventory> findById(Long id);

    void deleteById(Long id);
}
