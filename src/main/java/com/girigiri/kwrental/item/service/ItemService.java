package com.girigiri.kwrental.item.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.domain.Category;
import com.girigiri.kwrental.asset.equipment.service.EquipmentService;
import com.girigiri.kwrental.item.domain.EquipmentItems;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.domain.ItemsPerEquipments;
import com.girigiri.kwrental.item.dto.request.SaveOrUpdateItemsRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.item.dto.response.EquipmentItemDto;
import com.girigiri.kwrental.item.dto.response.ItemHistory;
import com.girigiri.kwrental.item.dto.response.ItemHistory.ItemHistoryBuilder;
import com.girigiri.kwrental.item.dto.response.ItemResponse;
import com.girigiri.kwrental.item.dto.response.ItemsResponse;
import com.girigiri.kwrental.item.dto.response.RentalCountsDto;
import com.girigiri.kwrental.item.exception.ItemNotFoundException;
import com.girigiri.kwrental.item.exception.NotEnoughAvailableItemException;
import com.girigiri.kwrental.item.repository.ItemRepository;

@Service
public class ItemService {

	private final ItemRepository itemRepository;
	private final EquipmentService equipmentService;
	private final RentedItemService rentedItemService;

	public ItemService(final ItemRepository itemRepository, final EquipmentService equipmentService,
		final RentedItemService rentedItemService) {
		this.itemRepository = itemRepository;
		this.equipmentService = equipmentService;
		this.rentedItemService = rentedItemService;
	}

	@Transactional(readOnly = true)
	public ItemsResponse getItems(final Long equipmentId) {
		equipmentService.validateExistsById(equipmentId);
		final List<Item> items = itemRepository.findByAssetId(equipmentId);
		return ItemsResponse.of(items);
	}

	@Transactional(readOnly = true)
	public ItemResponse getItem(final Long id) {
		final Item item = itemRepository.findById(id)
			.orElseThrow(ItemNotFoundException::new);
		return ItemResponse.from(item);
	}

	@Transactional
	public int updateRentalAvailable(final Long id, final boolean rentalAvailable) {
		final Item item = itemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
		return updateRentalAvailableAndAdjustEquipment(rentalAvailable, item);
	}

	private int updateRentalAvailableAndAdjustEquipment(final boolean rentalAvailable, final Item item) {
		int operand = getOperandOfRentableQuantity(item, rentalAvailable);
		equipmentService.adjustRentableQuantity(item.getAssetId(), operand);
		return itemRepository.updateRentalAvailable(item.getId(), rentalAvailable);
	}

	private int getOperandOfRentableQuantity(final Item item, final boolean rentalAvailable) {
		final boolean itemAvailable = item.isAvailable();
		if (itemAvailable && rentalAvailable)
			return 0;
		if (itemAvailable)
			return -1;
		if (rentalAvailable)
			return 1;
		return 0;
	}

	@Transactional
	public int updatePropertyNumber(final Long id, final String propertyNumber) {
		Item item = itemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
		rentedItemService.updatePropertyNumber(item.getPropertyNumber(), propertyNumber);
		return itemRepository.updatePropertyNumber(id, propertyNumber);
	}

	@Transactional
	public void delete(final Long id) {
		Item item = itemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
		rentedItemService.validateNotRentedByPropertyNumber(item.getPropertyNumber());
		int deletedCount = itemRepository.deleteById(id);
		equipmentService.adjustWhenItemDeleted(deletedCount, getOperandOfRentableQuantity(item, false),
			item.getAssetId());
		item.setAvailable(false);
	}

	@Transactional
	public ItemsResponse saveOrUpdate(final Long equipmentId, final SaveOrUpdateItemsRequest saveOrUpdateItemsRequest) {
		Map<Boolean, List<UpdateItemRequest>> itemRequestsGroup = groupByIdNull(saveOrUpdateItemsRequest);
		List<UpdateItemRequest> saveItemRequests = itemRequestsGroup.get(true);
		save(equipmentId, saveItemRequests);
		final EquipmentItems equipmentItems = getEquipmentItems(equipmentId);
		List<UpdateItemRequest> updateItemRequests = itemRequestsGroup.get(false);
		update(equipmentItems, updateItemRequests);
		deleteNotRequested(equipmentItems, saveOrUpdateItemsRequest.items(), equipmentId);
		return ItemsResponse.of(equipmentItems.getItems());
	}

	private void save(final Long equipmentId, final List<UpdateItemRequest> saveItemRequests) {
		if (saveItemRequests == null)
			return;
		List<Item> itemsToSave = saveItemRequests.stream().map(it -> mapToItem(equipmentId, it)).toList();
		itemRepository.saveAll(itemsToSave);
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
			equipmentItems.updatePropertyNumberById(item.getId(), request.propertyNumber());
		}
	}

	private void deleteNotRequested(final EquipmentItems equipmentItems,
		final List<UpdateItemRequest> updateItemRequests, Long equipmentId) {
		if (updateItemRequests == null)
			return;
		final List<String> notRequestedPropertyNumbers = getNotRequestedPropertyNumbers(equipmentItems,
			updateItemRequests);
		List<Item> itemToRemove = equipmentItems.getItemsByPropertyNumbers(notRequestedPropertyNumbers);
		int deletedCount = itemRepository.deleteByPropertyNumbers(notRequestedPropertyNumbers);
		int operandSumOfRentableQuantity = itemToRemove.stream()
			.mapToInt(item -> getOperandOfRentableQuantity(item, false))
			.sum();
		itemRepository.updateRentalAvailable(itemToRemove.stream().map(Item::getId).toList(), false);
		equipmentService.adjustWhenItemDeleted(deletedCount, operandSumOfRentableQuantity, equipmentId);
		equipmentItems.deleteByPropertyNumbers(notRequestedPropertyNumbers);
	}

	private List<String> getNotRequestedPropertyNumbers(EquipmentItems equipmentItems,
		List<UpdateItemRequest> updateItemRequests) {
		final List<String> propertyNumbers = equipmentItems.getPropertyNumbers();
		final Set<String> requestedIds = updateItemRequests.stream()
			.map(UpdateItemRequest::propertyNumber)
			.collect(Collectors.toSet());
		return propertyNumbers.stream().filter(id -> !requestedIds.contains(id)).toList();
	}

	private Map<Boolean, List<UpdateItemRequest>> groupByIdNull(SaveOrUpdateItemsRequest updateItemsRequest) {
		return updateItemsRequest.items().stream().collect(Collectors.groupingBy(it -> it.id() == null));
	}

	private Item mapToItem(final Long equipmentId, final UpdateItemRequest updateItemRequest) {
		return Item.builder().propertyNumber(updateItemRequest.propertyNumber()).assetId(equipmentId).build();
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public void validateAvailableCount(final Long equipmentId, final int amount) {
		final int availableCount = itemRepository.countAvailable(equipmentId);
		if (availableCount < amount) {
			throw new NotEnoughAvailableItemException();
		}
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public void validatePropertyNumbers(final Map<Long, Set<String>> propertyNumbersPerEquipmentId) {
		final Set<Long> equipmentIds = propertyNumbersPerEquipmentId.keySet();
		List<Item> itemsByEquipmentIds = itemRepository.findByEquipmentIds(equipmentIds);
		ItemsPerEquipments items = ItemsPerEquipments.from(itemsByEquipmentIds);
		for (Long equipmentId : propertyNumbersPerEquipmentId.keySet()) {
			items.validatePropertyNumbersAvailable(equipmentId, propertyNumbersPerEquipmentId.get(equipmentId));
		}
	}

	@Transactional(readOnly = true)
	public ItemsResponse getRentalAvailableItems(final Long equipmentId) {
		equipmentService.validateExistsById(equipmentId);
		final Set<String> rentedPropertyNumbers = rentedItemService.getRentedPropertyNumbers(equipmentId,
			LocalDateTime.now());
		final List<Item> items = itemRepository.findByAssetId(equipmentId);
		final List<Item> rentalAvailableItems = items.stream()
			.filter(it -> canRentalAvailable(rentedPropertyNumbers, it))
			.toList();
		return ItemsResponse.of(rentalAvailableItems);
	}

	private boolean canRentalAvailable(final Set<String> rentedPropertyNumbers, final Item it) {
		return !rentedPropertyNumbers.contains(it.getPropertyNumber()) && it.isAvailable();
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void setAvailable(final String propertyNumber, final boolean available) {
		Item item = itemRepository.findByPropertyNumber(propertyNumber).orElseThrow(ItemNotFoundException::new);
		updateRentalAvailableAndAdjustEquipment(available, item);
	}

	@Transactional(readOnly = true)
	public Page<ItemHistory> getItemHistories(final Pageable pageable, final Category category, final LocalDate from,
		final LocalDate to) {
		final Page<EquipmentItemDto> itemDtosPage = itemRepository.findEquipmentItem(pageable, category);
		final Set<String> propertyNumbers = itemDtosPage.getContent()
			.stream()
			.map(EquipmentItemDto::getPropertyNumber)
			.collect(Collectors.toSet());
		final Map<String, RentalCountsDto> rentalCountsByPropertyNumbers = rentedItemService.getRentalCountsByPropertyNumbersBetweenDate(
			propertyNumbers, from, to);
		return itemDtosPage.map(it -> mapToHistory(it, rentalCountsByPropertyNumbers.get(it.getPropertyNumber())));
	}

	private ItemHistory mapToHistory(final EquipmentItemDto equipmentItemDto, final RentalCountsDto rentalCountsDto) {
		final ItemHistoryBuilder builder = ItemHistory.builder()
			.modelName(equipmentItemDto.getModelName())
			.category(equipmentItemDto.getCategory())
			.propertyNumber(equipmentItemDto.getPropertyNumber());

		if (rentalCountsDto == null) {
			return builder.normalRentalCount(0).abnormalRentalCount(0).build();
		}
		return builder.normalRentalCount(rentalCountsDto.getNormalRentalCount())
			.abnormalRentalCount(rentalCountsDto.getAbnormalRentalCount())
			.build();
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void deleteByAssetId(Long assetId) {
		rentedItemService.validateNotRentedByAssetId(assetId);
		int updatedNotAvailableCount = setAvailableByAssetId(assetId, false);
		int deletedCount = itemRepository.deleteByAssetId(assetId);
		equipmentService.adjustWhenItemDeleted(deletedCount, -updatedNotAvailableCount, assetId);
	}

	private int setAvailableByAssetId(Long assetId, boolean available) {
		List<Long> ids = itemRepository.findByAssetId(assetId)
			.stream()
			.filter(item -> item.isAvailable() == available)
			.map(Item::getId)
			.toList();
		return itemRepository.updateRentalAvailable(ids, available);
	}
}
