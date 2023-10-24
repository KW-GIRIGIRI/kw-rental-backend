package com.girigiri.kwrental.inventory.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.equipment.service.EquipmentValidator;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.dto.request.AddInventoryRequest;
import com.girigiri.kwrental.inventory.dto.request.UpdateInventoryRequest;
import com.girigiri.kwrental.inventory.dto.response.InventoriesResponse;
import com.girigiri.kwrental.inventory.dto.response.InventoriesResponse.InventoryResponse;
import com.girigiri.kwrental.inventory.exception.InventoryInvalidAccessException;
import com.girigiri.kwrental.inventory.exception.InventoryNotFoundException;
import com.girigiri.kwrental.inventory.repository.InventoryRepository;
import com.girigiri.kwrental.operation.exception.LabRoomNotOperateException;
import com.girigiri.kwrental.operation.service.OperationChecker;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.service.remainquantity.RemainQuantityValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

	private final EquipmentValidator equipmentValidator;
	private final RemainQuantityValidator remainQuantityValidator;
	private final InventoryRepository inventoryRepository;
	private final OperationChecker operationChecker;

	@Transactional
	public Long save(final Long memberId, final AddInventoryRequest addInventoryRequest) {
		final Optional<Inventory> alreadyExistsInventory = findAlreadyExistsInventory(memberId, addInventoryRequest);
		if (alreadyExistsInventory.isEmpty()) {
			return createAndSave(memberId, addInventoryRequest).getId();
		}
		final Inventory inventory = alreadyExistsInventory.get();
		updateAmount(addInventoryRequest, inventory);
		return inventory.getId();
	}

	private Optional<Inventory> findAlreadyExistsInventory(final Long memberId,
		final AddInventoryRequest addInventoryRequest) {
		final RentalPeriod rentalPeriod = new RentalPeriod(addInventoryRequest.rentalStartDate(),
			addInventoryRequest.rentalEndDate());
		final Long equipmentId = addInventoryRequest.equipmentId();
		return inventoryRepository.findByPeriodAndEquipmentIdAndMemberId(rentalPeriod, equipmentId, memberId);
	}

	private Inventory createAndSave(final Long memberId, final AddInventoryRequest addInventoryRequest) {
		validateOperate(addInventoryRequest);
		final RentalPeriod rentalPeriod = new RentalPeriod(addInventoryRequest.rentalStartDate(),
			addInventoryRequest.rentalEndDate());
		final Long equipmentId = addInventoryRequest.equipmentId();
		final int rentalDatesCount = operationChecker.getOperateDates(rentalPeriod.getRentalStartDate(),
			rentalPeriod.getRentalEndDate()).size() - 1;
		final Equipment equipment = equipmentValidator.validateRentalDays(equipmentId, rentalDatesCount);
		remainQuantityValidator.validateAmount(equipmentId, addInventoryRequest.amount(), rentalPeriod);
		return inventoryRepository.save(mapToInventory(memberId, equipment, addInventoryRequest));
	}

	private void validateOperate(final AddInventoryRequest addInventoryRequest) {
		final boolean canOperate = operationChecker.canOperate(addInventoryRequest.rentalStartDate(),
			addInventoryRequest.rentalEndDate());
		if (!canOperate)
			throw new LabRoomNotOperateException();
	}

	private Inventory mapToInventory(final Long memberId, final Equipment equipment,
		final AddInventoryRequest addInventoryRequest) {
		final RentalPeriod rentalPeriod = new RentalPeriod(addInventoryRequest.rentalStartDate(),
			addInventoryRequest.rentalEndDate());
		return Inventory.builder()
			.asset(equipment)
			.rentalPeriod(rentalPeriod)
			.rentalAmount(RentalAmount.ofPositive(addInventoryRequest.amount()))
			.memberId(memberId)
			.build();
	}

	private void updateAmount(final AddInventoryRequest addInventoryRequest, final Inventory inventory) {
		final int updatedAmount = inventory.getRentalAmount().getAmount() + addInventoryRequest.amount();
		remainQuantityValidator.validateAmount(addInventoryRequest.equipmentId(), updatedAmount,
			inventory.getRentalPeriod());
		inventoryRepository.updateAmount(inventory.getId(), RentalAmount.ofPositive(updatedAmount));
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
		remainQuantityValidator.validateAmount(inventory.getAsset().getId(), request.amount(),
			new RentalPeriod(request.rentalStartDate(), request.rentalEndDate()));
		inventory.setRentalAmount(RentalAmount.ofPositive(request.amount()));
		inventory.setRentalPeriod(new RentalPeriod(request.rentalStartDate(), request.rentalEndDate()));
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
