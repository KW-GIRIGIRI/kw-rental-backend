package com.girigiri.kwrental.item.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record UpdateItemRequest(Long id, @NotEmpty String propertyNumber) {
}
