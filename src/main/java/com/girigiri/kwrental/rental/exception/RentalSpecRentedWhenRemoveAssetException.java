package com.girigiri.kwrental.rental.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class RentalSpecRentedWhenRemoveAssetException extends BadRequestException {
	public RentalSpecRentedWhenRemoveAssetException() {
		super("특정 기자재의 품목이 이미 대여중이라 삭제할 수 없습니다.");
	}
}
