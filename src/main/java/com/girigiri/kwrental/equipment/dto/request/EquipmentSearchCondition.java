package com.girigiri.kwrental.equipment.dto.request;

import com.girigiri.kwrental.equipment.controller.validator.TrimmedSize;
import com.girigiri.kwrental.equipment.domain.Category;

import java.time.LocalDate;

public record EquipmentSearchCondition(
        @TrimmedSize(message = "검색어는 양쪽 공백 제외 두글자 이상이어야 합니다.", min = 2) String keyword,
        Category category,
        LocalDate date) {
}
