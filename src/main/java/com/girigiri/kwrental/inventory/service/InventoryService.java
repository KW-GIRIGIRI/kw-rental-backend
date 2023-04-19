package com.girigiri.kwrental.inventory.service;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.service.EquipmentService;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.inventory.dto.request.AddInventoryRequest;
import com.girigiri.kwrental.inventory.dto.request.UpdateInventoryRequest;
import com.girigiri.kwrental.inventory.dto.response.InventoriesResponse;
import com.girigiri.kwrental.inventory.dto.response.InventoryResponse;
import com.girigiri.kwrental.inventory.exception.InventoryNotFoundException;
import com.girigiri.kwrental.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public Long save(final AddInventoryRequest addInventoryRequest) {
        final RentalPeriod rentalPeriod = new RentalPeriod(addInventoryRequest.getRentalStartDate(), addInventoryRequest.getRentalEndDate());
        final Long equipmentId = addInventoryRequest.getEquipmentId();
        final Equipment equipment = equipmentService.validateRentalDays(equipmentId, rentalPeriod.getRentalDays());
        amountValidator.validateAmount(equipmentId, addInventoryRequest.getAmount(), rentalPeriod);
        final Inventory inventory = inventoryRepository.save(mapToInventory(equipment, addInventoryRequest));
        return inventory.getId();
    }

    private Inventory mapToInventory(final Equipment equipment, final AddInventoryRequest addInventoryRequest) {
        final RentalPeriod rentalPeriod = new RentalPeriod(addInventoryRequest.getRentalStartDate(), addInventoryRequest.getRentalEndDate());
        return Inventory.builder()
                .equipment(equipment)
                .rentalPeriod(rentalPeriod)
                .rentalAmount(new RentalAmount(addInventoryRequest.getAmount()))
                .build();
    }

    // TODO: 2023/04/06 회원이 담은 기자재를 조회해야 된다.
    @Transactional(readOnly = true)
    public InventoriesResponse getInventories() {
        final List<Inventory> inventories = inventoryRepository.findAllWithEquipment();
        return InventoriesResponse.from(inventories);
    }

    @Transactional
    public void deleteAll() {
        inventoryRepository.deleteAll();
    }

    @Transactional
    public void deleteById(final Long id) {
        inventoryRepository.findById(id)
                .orElseThrow(InventoryNotFoundException::new);
        inventoryRepository.deleteById(id);
    }

    @Transactional
    public InventoryResponse update(final Long id, final UpdateInventoryRequest request) {
        final Inventory inventory = inventoryRepository.findWithEquipmentById(id)
                .orElseThrow(InventoryNotFoundException::new);
        amountValidator.validateAmount(inventory.getEquipment().getId(), request.getAmount(),
                new RentalPeriod(request.getRentalStartDate(), request.getRentalEndDate()));
        inventory.setRentalAmount(new RentalAmount(request.getAmount()));
        inventory.setRentalPeriod(new RentalPeriod(request.getRentalStartDate(), request.getRentalEndDate()));
        return InventoryResponse.from(inventory);
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public List<Inventory> getInventoriesWithEquipment() {
        final List<Inventory> inventories = inventoryRepository.findAllWithEquipment();
        if (inventories.isEmpty()) {
            throw new InventoryNotFoundException();
        }
        return inventories;
    }
}
