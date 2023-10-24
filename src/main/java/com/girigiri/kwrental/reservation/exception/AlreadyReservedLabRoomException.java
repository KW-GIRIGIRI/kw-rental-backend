package com.girigiri.kwrental.reservation.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class AlreadyReservedLabRoomException extends BadRequestException {
	public AlreadyReservedLabRoomException() {
		super("랩실은 하루에 한 번만 대여할 수 있습니다.");
	}
}
