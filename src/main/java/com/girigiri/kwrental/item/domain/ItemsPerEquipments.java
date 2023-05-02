package com.girigiri.kwrental.item.domain;

import com.girigiri.kwrental.item.exception.ItemNotAvailableException;
import com.girigiri.kwrental.item.exception.ItemNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

public class ItemsPerEquipments {

    private final Map<Long, Set<Item>> items;

    private ItemsPerEquipments(final Map<Long, Set<Item>> items) {
        this.items = items;
    }

    public static ItemsPerEquipments from(final List<Item> items) {
        if (items.isEmpty()) throw new ItemNotFoundException();
        final Map<Long, Set<Item>> itemGroup = items.stream()
                .collect(groupingBy(Item::getEquipmentId, toSet()));
        return new ItemsPerEquipments(itemGroup);
    }

    public void validatePropertyNumbersAvailable(final Long equipmentId, final Set<String> propertyNumbers) {
        final Map<String, Item> itemsPerPropertyNumber = getItemsByEquipmentIdPerPropertyNumber(equipmentId);
        for (String propertyNumber : propertyNumbers) {
            final Item item = itemsPerPropertyNumber.get(propertyNumber);
            if (item == null) throw new ItemNotFoundException();
            if (!item.isAvailable()) throw new ItemNotAvailableException();
        }
    }

    private Map<String, Item> getItemsByEquipmentIdPerPropertyNumber(final Long equipmentId) {
        final Set<Item> itemsByEquipmentId = items.get(equipmentId);
        if (itemsByEquipmentId == null) throw new ItemNotFoundException();
        return itemsByEquipmentId.stream()
                .collect(toMap(Item::getPropertyNumber, Function.identity()));
    }
}
