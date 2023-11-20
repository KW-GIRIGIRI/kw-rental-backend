package com.girigiri.kwrental.item.service.propertynumberupdate;

import java.util.Objects;

import com.girigiri.kwrental.item.exception.ItemException;

public record ToBeUpdatedItem(Long id, Long assetId, String asIsPropertyNumber, String toBePropertyNumber) {
	public ToBeUpdatedItem {
		Objects.requireNonNull(id);
		Objects.requireNonNull(assetId);
		validatePropertyNumberNotEmpty(asIsPropertyNumber);
		validatePropertyNumberNotEmpty(toBePropertyNumber);
		validatePropertyNumbersNotSame(asIsPropertyNumber, toBePropertyNumber);
	}

	private void validatePropertyNumberNotEmpty(final String propertyNumber) {
		if (propertyNumber == null || propertyNumber.isBlank()) {
			throw new ItemException("자산 번호가 null이거나 빈 공백이면 안됩니다.");
		}
	}

	private void validatePropertyNumbersNotSame(final String asIsPropertyNumber, final String toBePropertyNumber) {
		if (asIsPropertyNumber.equals(toBePropertyNumber)) {
			throw new ItemException("기존의 자산번호와 같은 번호로 변경할 수 없습니다.");
		}
	}
}
