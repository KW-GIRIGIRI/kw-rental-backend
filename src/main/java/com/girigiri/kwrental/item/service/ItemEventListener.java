package com.girigiri.kwrental.item.service;

import com.girigiri.kwrental.equipment.service.EquipmentDeleteEvent;
import com.girigiri.kwrental.item.repository.ItemRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ItemEventListener {

    private final ItemRepository itemRepository;

    public ItemEventListener(final ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @EventListener
    public void handleEquipmentDelete(final EquipmentDeleteEvent event) {
        itemRepository.deleteByEquipmentId(event.getEquipmentId());
    }
}
