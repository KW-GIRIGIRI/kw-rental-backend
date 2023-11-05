package com.girigiri.kwrental.inventory.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.girigiri.kwrental.inventory.domain.Inventory;

public interface InventoryRepository extends Repository<Inventory, Long>, InventoryRepositoryCustom {

    Inventory save(Inventory inventory);

    Optional<Inventory> findById(Long id);

    void deleteById(Long id);
}
