package com.girigiri.kwrental.item.service;

import com.girigiri.kwrental.equipment.service.EquipmentService;
import com.girigiri.kwrental.item.domain.EquipmentItems;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.domain.ItemsPerEquipments;
import com.girigiri.kwrental.item.dto.request.ItemPropertyNumberRequest;
import com.girigiri.kwrental.item.dto.request.ItemRentalAvailableRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemsRequest;
import com.girigiri.kwrental.item.dto.response.ItemResponse;
import com.girigiri.kwrental.item.dto.response.ItemsResponse;
import com.girigiri.kwrental.item.exception.ItemNotFoundException;
import com.girigiri.kwrental.item.exception.NotEnoughAvailableItemException;
import com.girigiri.kwrental.item.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final EquipmentService equipmentService;
    private final RentedItemService rentedItemService;

    public ItemService(final ItemRepository itemRepository, final EquipmentService equipmentService, final RentedItemService rentedItemService) {
        this.itemRepository = itemRepository;
        this.equipmentService = equipmentService;
        this.rentedItemService = rentedItemService;
    }

    @Transactional(readOnly = true)
    public ItemsResponse getItems(final Long equipmentId) {
        equipmentService.validateExistsById(equipmentId);
        final List<Item> items = itemRepository.findByEquipmentId(equipmentId);
        return ItemsResponse.of(items);
    }

    @Transactional(readOnly = true)
    public ItemResponse getItem(final Long id) {
        final Item item = itemRepository.findById(id)
                .orElseThrow(ItemNotFoundException::new);
        return ItemResponse.from(item);
    }

    @Transactional
    public int updateRentalAvailable(final Long id, final ItemRentalAvailableRequest rentalAvailableRequest) {
        itemRepository.findById(id)
                .orElseThrow(ItemNotFoundException::new);
        return itemRepository.updateRentalAvailable(id, rentalAvailableRequest.rentalAvailable());
    }

    @Transactional
    public int updatePropertyNumber(final Long id, final ItemPropertyNumberRequest propertyNumberRequest) {
        itemRepository.findById(id)
                .orElseThrow(ItemNotFoundException::new);
        return itemRepository.updatePropertyNumber(id, propertyNumberRequest.propertyNumber());
    }

    @Transactional
    public void delete(final Long id) {
        itemRepository.findById(id)
                .orElseThrow(ItemNotFoundException::new);
        itemRepository.deleteById(id);
    }

    @Transactional
    public ItemsResponse saveOrUpdate(final Long equipmentId, final UpdateItemsRequest updateItemsRequest) {
        Map<Boolean, List<UpdateItemRequest>> updateItemRequestsGroup = groupByIdNull(updateItemsRequest);
        List<UpdateItemRequest> saveItemRequests = updateItemRequestsGroup.get(true);
        save(equipmentId, saveItemRequests);

        List<UpdateItemRequest> updateItemRequests = updateItemRequestsGroup.get(false);
        List<Item> updatedItems = update(equipmentId, updateItemRequests);
        return ItemsResponse.of(updatedItems);
    }

    private List<Item> update(Long equipmentId, List<UpdateItemRequest> updateItemRequests) {
        List<Item> items = itemRepository.findByEquipmentId(equipmentId);
        EquipmentItems equipmentItems = EquipmentItems.from(items);
        for (UpdateItemRequest request : updateItemRequests) {
            equipmentItems.updatePropertyNumberById(request.id(), request.propertyNumber());
        }
        return equipmentItems.getItems();
    }

    private Map<Boolean, List<UpdateItemRequest>> groupByIdNull(UpdateItemsRequest updateItemsRequest) {
        return updateItemsRequest.items()
                .stream()
                .collect(Collectors.groupingBy(it -> it.id() == null));
    }

    private void save(final Long equipmentId, final List<UpdateItemRequest> saveItemRequests) {
        List<Item> itemsToSave = saveItemRequests
                .stream()
                .map(it -> mapToItem(equipmentId, it))
                .toList();
        itemRepository.saveAll(itemsToSave);
    }

    private Item mapToItem(final Long equipmentId, final UpdateItemRequest updateItemRequest) {
        return Item.builder()
                .propertyNumber(updateItemRequest.propertyNumber())
                .equipmentId(equipmentId)
                .build();
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
        final Set<String> rentedPropertyNumbers = rentedItemService.getRentedPropertyNumbers(equipmentId, LocalDateTime.now());
        final List<Item> items = itemRepository.findByEquipmentId(equipmentId);
        final List<Item> rentalAvailableItems = items.stream()
                .filter(it -> canRentalAvailable(rentedPropertyNumbers, it))
                .toList();
        return ItemsResponse.of(rentalAvailableItems);
    }

    private static boolean canRentalAvailable(final Set<String> rentedPropertyNumbers, final Item it) {
        return !rentedPropertyNumbers.contains(it.getPropertyNumber()) && it.isAvailable();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void setAvailable(final String propertyNumber, final boolean available) {
        Item item = itemRepository.findByPropertyNumber(propertyNumber)
                .orElseThrow(ItemNotFoundException::new);
        item.setAvailable(available);
    }
}
