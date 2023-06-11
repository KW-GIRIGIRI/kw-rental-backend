package com.girigiri.kwrental.reservation.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class AlreadyReservedLabRoomException extends BadRequestException {
	public AlreadyReservedLabRoomException() {
		super("해당 기간에 대여한 이력이 있습니다. 랩실은 기간 내 1번만 대여할 수 있습니다.");
	}
}
