package com.girigiri.kwrental.item.dto.response;

import com.girigiri.kwrental.item.domain.Item;
import java.util.List;

public record ItemsResponse(
        List<ItemResponse> items
) {
    public static ItemsResponse of(final List<Item> items) {
        final List<ItemResponse> itemResponses = items.stream()
                .map(ItemResponse::from)
                .toList();
        return new ItemsResponse(itemResponses);
    }
}
