package com.girigiri.kwrental.inventory.service;

import com.girigiri.kwrental.equipment.service.EquipmentService;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.inventory.dto.request.AddInventoryRequest;
import com.girigiri.kwrental.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;

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

    public Long save(final AddInventoryRequest addInventoryRequest) {
        final RentalPeriod rentalPeriod = new RentalPeriod(addInventoryRequest.getRentalStartDate(), addInventoryRequest.getRentalEndDate());
        final Long equipmentId = addInventoryRequest.getEquipmentId();
        equipmentService.validateRentalDays(equipmentId, rentalPeriod.getRentalDays());
        amountValidator.validateAmount(equipmentId, addInventoryRequest.getAmount(), rentalPeriod);
        final Inventory mapedInventory = mapToInventory(addInventoryRequest);
        final Inventory inventory = inventoryRepository.save(mapedInventory);
        return inventory.getId();
    }

    private Inventory mapToInventory(final AddInventoryRequest addInventoryRequest) {
        final RentalPeriod rentalPeriod = new RentalPeriod(addInventoryRequest.getRentalStartDate(), addInventoryRequest.getRentalEndDate());
        return Inventory.builder()
                .equipmentId(addInventoryRequest.getEquipmentId())
                .rentalPeriod(rentalPeriod)
                .amount(addInventoryRequest.getAmount())
                .build();
    }
}