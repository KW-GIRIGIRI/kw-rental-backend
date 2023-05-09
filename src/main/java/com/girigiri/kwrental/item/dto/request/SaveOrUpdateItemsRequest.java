package com.girigiri.kwrental.item.dto.request;

import jakarta.validation.Valid;

import java.util.List;

public record SaveOrUpdateItemsRequest(
        @Valid List<UpdateItemRequest> items
) {
}
