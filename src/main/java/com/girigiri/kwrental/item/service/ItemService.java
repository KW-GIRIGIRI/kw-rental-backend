package com.girigiri.kwrental.item.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.item.domain.EquipmentItems;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.request.SaveOrUpdateItemsRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.item.dto.response.ItemsResponse;
import com.girigiri.kwrental.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

	private final ItemRetriever itemRetriever;
	private final ItemAvailableSetter itemAvailableSetter;
	private final ItemRepository itemRepository;
	private final ItemSaverImpl itemSaver;
	private final ItemDeleter itemDeleter;
	private final RentedItemService rentedItemService;

	public void updateAvailable(final Long id, final boolean rentalAvailable) {
		final Item item = itemRetriever.getById(id);
		itemAvailableSetter.updateAvailable(rentalAvailable, item);
	}

	public void updatePropertyNumber(final Long id, final String propertyNumber) {
		Item item = itemRetriever.getById(id);
		rentedItemService.updatePropertyNumber(item.getPropertyNumber(), propertyNumber);
		item.setPropertyNumber(propertyNumber);
	}

	public void delete(final Long id) {
		Item item = itemRetriever.getById(id);
		itemDeleter.delete(item);
	}

	public ItemsResponse saveOrUpdate(final Long equipmentId, final SaveOrUpdateItemsRequest saveOrUpdateItemsRequest) {
		Map<Boolean, List<UpdateItemRequest>> itemRequestsGroup = groupByIdNull(saveOrUpdateItemsRequest);
		List<UpdateItemRequest> saveItemRequests = itemRequestsGroup.get(true);
		itemSaver.saveItemsWhenUpdate(equipmentId, saveItemRequests);
		final EquipmentItems equipmentItems = getEquipmentItems(equipmentId);
		List<UpdateItemRequest> updateItemRequests = itemRequestsGroup.get(false);
		update(equipmentItems, updateItemRequests);
		deleteNotRequested(equipmentItems, saveOrUpdateItemsRequest.items());
		return ItemsResponse.of(equipmentItems.getItems());
	}

	private Map<Boolean, List<UpdateItemRequest>> groupByIdNull(SaveOrUpdateItemsRequest updateItemsRequest) {
		return updateItemsRequest.items().stream().collect(Collectors.groupingBy(it -> it.id() == null));
	}

	private EquipmentItems getEquipmentItems(final Long equipmentId) {
		List<Item> items = itemRepository.findByAssetId(equipmentId);
		return EquipmentItems.from(items);
	}

	private void update(EquipmentItems equipmentItems, List<UpdateItemRequest> updateItemRequests) {
		if (updateItemRequests == null)
			return;
		for (UpdateItemRequest request : updateItemRequests) {
			final Item item = equipmentItems.getItem(request.id());
			updatePropertyNumber(item.getId(), request.propertyNumber());
		}
	}

	private void deleteNotRequested(final EquipmentItems equipmentItems,
		final List<UpdateItemRequest> updateItemRequests) {
		if (updateItemRequests == null)
			return;
		final List<String> notRequestedPropertyNumbers = getNotRequestedPropertyNumbers(equipmentItems,
			updateItemRequests);
		List<Item> itemsToDelete = equipmentItems.getItemsByPropertyNumbers(notRequestedPropertyNumbers);
		itemDeleter.batchDelete(itemsToDelete);
		equipmentItems.deleteByPropertyNumbers(notRequestedPropertyNumbers);
	}

	private List<String> getNotRequestedPropertyNumbers(EquipmentItems equipmentItems,
		List<UpdateItemRequest> updateItemRequests) {
		final List<String> propertyNumbers = equipmentItems.getPropertyNumbers();
		final Set<String> requestedPropertyNumbers = updateItemRequests.stream()
			.map(UpdateItemRequest::propertyNumber)
			.collect(Collectors.toSet());
		return propertyNumbers.stream().filter(id -> !requestedPropertyNumbers.contains(id)).toList();
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void setAvailable(final String propertyNumber, final boolean available) {
		Item item = itemRetriever.getByPropertyNumber(propertyNumber);
		itemAvailableSetter.updateAvailable(available, item);
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void deleteByAssetId(Long assetId) {
		final List<Item> items = itemRepository.findByAssetId(assetId);
		itemDeleter.batchDelete(items);
	}
}
