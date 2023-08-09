package com.girigiri.kwrental.item.dto.request;

import java.util.List;

import jakarta.validation.Valid;

public record SaveOrUpdateItemsRequest(@Valid List<UpdateItemRequest> items) {
}
