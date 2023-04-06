package com.girigiri.kwrental.inventory.service;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.service.EquipmentService;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.inventory.dto.request.AddInventoryRequest;
import com.girigiri.kwrental.inventory.dto.response.InventoriesResponse;
import com.girigiri.kwrental.inventory.exception.InventoryNotFound;
import com.girigiri.kwrental.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;

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
                .amount(addInventoryRequest.getAmount())
                .build();
    }

    // TODO: 2023/04/06 회원이 담은 기자재를 조회해야 된다.
    public InventoriesResponse getInventories() {
        final List<Inventory> inventories = inventoryRepository.findAllWithEquipment();
        return InventoriesResponse.from(inventories);
    }

    public void deleteAll() {
        inventoryRepository.deleteAll();
    }

    public void deleteById(final Long id) {
        inventoryRepository.findById(id)
                .orElseThrow(InventoryNotFound::new);
        inventoryRepository.deleteById(id);
    }
}
