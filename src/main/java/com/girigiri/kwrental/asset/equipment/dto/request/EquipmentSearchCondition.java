package com.girigiri.kwrental.asset.equipment.dto.request;

import java.time.LocalDate;

import com.girigiri.kwrental.asset.equipment.controller.validator.TrimmedSize;
import com.girigiri.kwrental.asset.equipment.domain.Category;

public record EquipmentSearchCondition(
	@TrimmedSize(message = "검색어는 양쪽 공백 제외 두글자 이상이어야 합니다.", min = 2) String keyword,
	Category category,
	LocalDate date) {
}
