package com.girigiri.kwrental.inventory.dto.response;

import com.girigiri.kwrental.inventory.domain.Inventory;
import lombok.Getter;

import java.util.List;

@Getter

public class InventoriesResponse {

    private List<InventoryResponse> inventories;

    private InventoriesResponse() {
    }

    private InventoriesResponse(final List<InventoryResponse> inventories) {
        this.inventories = inventories;
    }

    public static InventoriesResponse from(final List<Inventory> inventories) {
        final List<InventoryResponse> inventoryResponses = inventories.stream()
                .map(InventoryResponse::from)
                .toList();
        return new InventoriesResponse(inventoryResponses);
    }
}
