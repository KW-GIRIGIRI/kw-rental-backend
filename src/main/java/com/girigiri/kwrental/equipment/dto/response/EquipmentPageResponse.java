package com.girigiri.kwrental.equipment.dto.response;

import java.util.List;

public record EquipmentPageResponse(List<String> endPoints, Integer page, List<SimpleEquipmentResponse> items) {
}
