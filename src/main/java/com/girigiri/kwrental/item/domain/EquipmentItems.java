package com.girigiri.kwrental.item.domain;

import com.girigiri.kwrental.item.exception.EquipmentItemsException;
import com.girigiri.kwrental.item.exception.ItemNotFoundException;

import java.util.*;

public class EquipmentItems {

    private final Map<Long, Item> items;

    private EquipmentItems(Map<Long, Item> items) {
        this.items = items;
    }

    public static EquipmentItems from(List<Item> inputItems) {
        Map<Long, Item> items = new HashMap<>();
        Set<String> propertyNumberSet = new HashSet<>();
        for (Item item : inputItems) {
            validateId(items, item);
            validatePropertyNumber(propertyNumberSet, item);
            items.put(item.getId(), item);
            propertyNumberSet.add(item.getPropertyNumber());
        }
        return new EquipmentItems(items);
    }

    private static void validatePropertyNumber(Set<String> propertyNumberSet, Item item) {
        if (item.getPropertyNumber() == null) {
            throw new EquipmentItemsException("기자재의 품목 중 자산 번호가 null인 품목이 존재합니다.");
        }
        if (propertyNumberSet.contains(item.getPropertyNumber())) {
            throw new EquipmentItemsException("기자재의 품목 중 자산 번호가 중복된 품목이 존재합니다.");
        }
    }

    private static void validateId(Map<Long, Item> items, Item item) {
        if (item.getId() == null) {
            throw new EquipmentItemsException("기자재의 품목 중 ID가 null인 품목이 존재합니다.");
        }
        if (items.containsKey(item.getId())) {
            throw new EquipmentItemsException("기자재의 품목 중 ID가 중복된 품목이 존재합니다.");
        }
    }

    public void updatePropertyNumberById(Long id, String propertyNumber) {
        Item item = items.get(id);
        if (item == null) {
            throw new ItemNotFoundException();
        }
        item.updatePropertyNumber(propertyNumber);
    }

    public List<Item> getItems() {
        return new ArrayList<>(items.values());
    }
}