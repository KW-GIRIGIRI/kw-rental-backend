package com.girigiri.kwrental.asset.equipment.service;

import java.util.Collections;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.domain.Category;
import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.equipment.dto.request.AddEquipmentRequest;
import com.girigiri.kwrental.asset.equipment.dto.request.AddEquipmentWithItemsRequest;
import com.girigiri.kwrental.asset.equipment.dto.request.AddItemRequest;
import com.girigiri.kwrental.asset.equipment.dto.request.UpdateEquipmentRequest;
import com.girigiri.kwrental.asset.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.item.service.ItemsUpdateUseCase;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EquipmentService {

	private final ItemSaverPerEquipment itemSaverPerEquipment;
	private final EquipmentRetriever equipmentRetriever;
	private final EquipmentRepository equipmentRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final EquipmentValidator equipmentValidator;
	private final ItemsUpdateUseCase itemsUpdateUseCase;

	public Long saveEquipment(final AddEquipmentWithItemsRequest addEquipmentWithItemsRequest) {
		final AddEquipmentRequest addEquipmentRequest = addEquipmentWithItemsRequest.equipment();
		equipmentValidator.validateNotExistsByName(addEquipmentRequest.modelName());
		final Equipment equipment = equipmentRepository.save(mapToEquipment(addEquipmentRequest));
		final List<ToBeSavedItem> toBeSavedItems = mapToToBeSavedItems(equipment.getId(), addEquipmentWithItemsRequest.items());
		itemSaverPerEquipment.execute(toBeSavedItems);
		return equipment.getId();
	}

	private Equipment mapToEquipment(final AddEquipmentRequest addEquipmentRequest) {
		return Equipment.builder()
			.name(addEquipmentRequest.modelName())
			.maker(addEquipmentRequest.maker())
			.imgUrl(addEquipmentRequest.imgUrl())
			.purpose(addEquipmentRequest.purpose())
			.category(Category.from(addEquipmentRequest.category()))
			.description(addEquipmentRequest.description())
			.components(addEquipmentRequest.components())
			.rentalPlace(addEquipmentRequest.rentalPlace())
			// .totalQuantity(addEquipmentRequest.totalQuantity())
			// .rentableQuantity(addEquipmentRequest.totalQuantity())
			.totalQuantity(0).rentableQuantity(0)
			.maxRentalDays(addEquipmentRequest.maxRentalDays())
			.build();
	}

	private List<ToBeSavedItem> mapToToBeSavedItems(final Long equipmentId, final List<AddItemRequest> requests) {
		if (requests == null || requests.isEmpty()) return Collections.emptyList();
		return requests.stream()
			.map(it -> new ToBeSavedItem(it.propertyNumber(), equipmentId))
			.toList();
	}

	public void deleteEquipment(final Long id) {
		Equipment equipment = equipmentRetriever.getEquipment(id);
		equipment.delete();
		eventPublisher.publishEvent(new EquipmentDeleteEvent(this, id));
	}

	public Long update(final Long id, final UpdateEquipmentRequest updateEquipmentRequest) {
		Equipment equipment = equipmentRetriever.getEquipment(id);

		updateEquipment(updateEquipmentRequest, equipment);

		itemsUpdateUseCase.saveOrUpdate(id, updateEquipmentRequest.items());
		return id;
	}

	private void updateEquipment(final UpdateEquipmentRequest updateEquipmentRequest, final Equipment equipment) {
		equipment.setName(updateEquipmentRequest.modelName());
		equipment.setComponents(updateEquipmentRequest.components());
		equipment.setCategory(Category.from(updateEquipmentRequest.category()));
		equipment.setMaker(updateEquipmentRequest.maker());
		equipment.setDescription(updateEquipmentRequest.description());
		equipment.setPurpose(updateEquipmentRequest.purpose());
		equipment.setImgUrl(updateEquipmentRequest.imgUrl());
		equipment.setRentalPlace(updateEquipmentRequest.rentalPlace());
		equipment.setMaxRentalDays(updateEquipmentRequest.maxRentalDays());
		// equipment.setTotalQuantity(updateEquipmentRequest.totalQuantity());
	}
}
