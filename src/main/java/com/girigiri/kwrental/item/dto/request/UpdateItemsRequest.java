package com.girigiri.kwrental.item.dto.request;

import jakarta.validation.Valid;

import java.util.List;

public record UpdateItemsRequest(
        @Valid List<UpdateItemRequest> items
) {
}
