package com.girigiri.kwrental.rental.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class RentalSpecRentedWhenRemoveItemException extends BadRequestException {
	public RentalSpecRentedWhenRemoveItemException() {
		super("품목을 삭제할 때 해당 품목을 대여중이면 안됩니다.");
	}
}
