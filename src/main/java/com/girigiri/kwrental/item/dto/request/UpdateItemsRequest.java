package com.girigiri.kwrental.item.dto.request;

import java.util.List;

public record UpdateItemsRequest(
        List<UpdateItemRequest> items
) {
}
