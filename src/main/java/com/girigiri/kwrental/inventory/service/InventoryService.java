package com.girigiri.kwrental.inventory.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.equipment.service.EquipmentService;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.dto.request.AddInventoryRequest;
import com.girigiri.kwrental.inventory.dto.request.UpdateInventoryRequest;
import com.girigiri.kwrental.inventory.dto.response.InventoriesResponse;
import com.girigiri.kwrental.inventory.dto.response.InventoryResponse;
import com.girigiri.kwrental.inventory.exception.InventoryInvalidAccessException;
import com.girigiri.kwrental.inventory.exception.InventoryNotFoundException;
import com.girigiri.kwrental.inventory.repository.InventoryRepository;
import com.girigiri.kwrental.reservation.domain.RentalAmount;
import com.girigiri.kwrental.reservation.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.service.AmountValidator;

@Service
public class InventoryService {

    private final EquipmentService equipmentService;
    private final AmountValidator amountValidator;
    private final InventoryRepository inventoryRepository;

    public InventoryService(final EquipmentService equipmentService, final AmountValidator amountValidator,
                            final InventoryRepository inventoryRepository) {
        this.equipmentService = equipmentService;
        this.amountValidator = amountValidator;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public Long save(final Long memberId, final AddInventoryRequest addInventoryRequest) {
        final RentalPeriod rentalPeriod = new RentalPeriod(addInventoryRequest.getRentalStartDate(), addInventoryRequest.getRentalEndDate());
        final Long equipmentId = addInventoryRequest.getEquipmentId();
        final Optional<Inventory> foundInventory = inventoryRepository.findByPeriodAndEquipmentIdAndMemberId(rentalPeriod, equipmentId, memberId);
        if (foundInventory.isEmpty()) {
            final Equipment equipment = equipmentService.validateRentalDays(equipmentId,
                rentalPeriod.getRentalDayCount());
            amountValidator.validateAmount(equipmentId, addInventoryRequest.getAmount(), rentalPeriod);
            final Inventory inventory = inventoryRepository.save(mapToInventory(memberId, equipment, addInventoryRequest));
            return inventory.getId();
        }
        final Inventory inventory = foundInventory.get();
        final int updatedAmount = inventory.getRentalAmount().getAmount() + addInventoryRequest.getAmount();
        amountValidator.validateAmount(equipmentId, updatedAmount, inventory.getRentalPeriod());
        inventoryRepository.updateAmount(inventory.getId(), RentalAmount.ofPositive(updatedAmount));
        return inventory.getId();
    }

    private Inventory mapToInventory(final Long memberId, final Equipment equipment, final AddInventoryRequest addInventoryRequest) {
        final RentalPeriod rentalPeriod = new RentalPeriod(addInventoryRequest.getRentalStartDate(), addInventoryRequest.getRentalEndDate());
        return Inventory.builder()
                .rentable(equipment)
                .rentalPeriod(rentalPeriod)
                .rentalAmount(RentalAmount.ofPositive(addInventoryRequest.getAmount()))
                .memberId(memberId)
                .build();
    }

    @Transactional(readOnly = true)
    public InventoriesResponse getInventories(final Long memberId) {
        final List<Inventory> inventories = inventoryRepository.findAllWithEquipment(memberId);
        return InventoriesResponse.from(inventories);
    }

    @Transactional
    public void deleteAll(final Long memberId) {
        inventoryRepository.deleteAll(memberId);
    }

    @Transactional
    public void deleteById(final Long memberId, final Long id) {
        final Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(InventoryNotFoundException::new);
        validateInventoryMemberId(memberId, inventory);
        inventoryRepository.deleteById(id);
    }

    private void validateInventoryMemberId(final Long memberId, final Inventory inventory) {
        if (!inventory.hasMemberId(memberId)) {
            throw new InventoryInvalidAccessException();
        }
    }

    @Transactional
    public InventoryResponse update(final Long memberId, final Long id, final UpdateInventoryRequest request) {
        final Inventory inventory = inventoryRepository.findWithEquipmentById(id)
                .orElseThrow(InventoryNotFoundException::new);
        validateInventoryMemberId(memberId, inventory);
        amountValidator.validateAmount(inventory.getRentable().getId(), request.getAmount(),
                new RentalPeriod(request.getRentalStartDate(), request.getRentalEndDate()));
        inventory.setRentalAmount(RentalAmount.ofPositive(request.getAmount()));
        inventory.setRentalPeriod(new RentalPeriod(request.getRentalStartDate(), request.getRentalEndDate()));
        return InventoryResponse.from(inventory);
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public List<Inventory> getInventoriesWithEquipment(final Long memberId) {
        final List<Inventory> inventories = inventoryRepository.findAllWithEquipment(memberId);
        if (inventories.isEmpty()) {
            throw new InventoryNotFoundException();
        }
        return inventories;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteByEquipmentId(Long equipmentId) {
        inventoryRepository.deleteByEquipmentId(equipmentId);
    }
}
