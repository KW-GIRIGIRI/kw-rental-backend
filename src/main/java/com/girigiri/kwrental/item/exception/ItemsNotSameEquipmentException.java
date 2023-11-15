package com.girigiri.kwrental.item.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class ItemsNotSameEquipmentException extends BadRequestException {
	public ItemsNotSameEquipmentException() {
		super("저장되어야 할 품목들의 기자재 정보가 서로 다릅니다.");
	}
}
