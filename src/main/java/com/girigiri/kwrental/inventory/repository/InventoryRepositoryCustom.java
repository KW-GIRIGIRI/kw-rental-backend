package com.girigiri.kwrental.inventory.repository;

import java.util.List;
import java.util.Optional;

import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;

public interface InventoryRepositoryCustom {
    List<Inventory> findAllWithEquipment(final Long memberId);

    int deleteAll(Long memberId);

    Optional<Inventory> findWithEquipmentById(Long id);

    Optional<Inventory> findByPeriodAndEquipmentIdAndMemberId(RentalPeriod rentalPeriod, Long equipmentId,
        Long memberId);

    void updateAmount(Long id, RentalAmount amount);

    void deleteByEquipmentId(Long assetId);
}
