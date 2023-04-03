package com.girigiri.kwrental.item.service;

import com.girigiri.kwrental.equipment.dto.request.AddItemRequest;
import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.equipment.service.ItemService;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.request.ItemPropertyNumberRequest;
import com.girigiri.kwrental.item.dto.request.ItemRentalAvailableRequest;
import com.girigiri.kwrental.item.dto.response.ItemResponse;
import com.girigiri.kwrental.item.dto.response.ItemsResponse;
import com.girigiri.kwrental.item.exception.ItemNotFoundException;
import com.girigiri.kwrental.item.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final EquipmentRepository equipmentRepository;

    public ItemServiceImpl(final ItemRepository itemRepository, final EquipmentRepository equipmentRepository) {
        this.itemRepository = itemRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public void saveItems(final Long equipmentId, final List<AddItemRequest> itemRequests) {
        final List<Item> items = itemRequests.stream()
                .map(it -> mapToItem(equipmentId, it))
                .toList();
        itemRepository.saveAll(items);
    }

    private Item mapToItem(final Long equipmentId, final AddItemRequest addItemRequest) {
        return Item.builder()
                .equipmentId(equipmentId)
                .propertyNumber(addItemRequest.propertyNumber())
                .rentalAvailable(true)
                .build();
    }

    @Transactional(readOnly = true)
    public ItemsResponse getItems(final Long equipmentId) {
        equipmentRepository.findById(equipmentId)
                .orElseThrow(EquipmentNotFoundException::new);
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
}
