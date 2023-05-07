package com.girigiri.kwrental.testsupport.fixture;

import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.domain.Item.ItemBuilder;

public class ItemFixture {

    private static final long EQUIPMENT_ID = 0L;
    private static final String PROPERTY_NUMBER = "123456789";

    public static ItemBuilder builder() {
        return Item.builder()
                .equipmentId(EQUIPMENT_ID)
                .propertyNumber(PROPERTY_NUMBER)
                .available(true);
    }

    public static Item create() {
        return builder().build();
    }
}
