package com.girigiri.kwrental.item.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.dto.request.AddItemRequest;
import com.girigiri.kwrental.asset.equipment.service.SaveItemService;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.repository.ItemRepository;

@Service
public class SaveItemServiceImpl implements SaveItemService {

    private final ItemRepository itemRepository;

    public SaveItemServiceImpl(final ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void saveItems(final Long equipmentId, final List<AddItemRequest> itemRequests) {
        final List<Item> items = itemRequests.stream()
                .map(it -> mapToItem(equipmentId, it))
                .toList();
        itemRepository.saveAll(items);
    }

    private Item mapToItem(final Long equipmentId, final AddItemRequest addItemRequest) {
        return Item.builder()
                .assetId(equipmentId)
                .propertyNumber(addItemRequest.propertyNumber())
                .available(true)
                .build();
    }
}
