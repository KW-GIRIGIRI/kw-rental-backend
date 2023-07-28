package com.girigiri.kwrental.rental.exception;

import com.girigiri.kwrental.common.exception.DomainException;

public class ReservationIdNotSingleValueWhenEquipmentAcceptException extends DomainException {
	public ReservationIdNotSingleValueWhenEquipmentAcceptException() {
		super("수령 처리되어야 할 기자재 대여가 여러개면 안됩니다.");
	}
}
