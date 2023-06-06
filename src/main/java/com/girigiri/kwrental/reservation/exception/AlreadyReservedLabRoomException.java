package com.girigiri.kwrental.reservation.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class AlreadyReservedLabRoomException extends BadRequestException {
	public AlreadyReservedLabRoomException() {
		super("이미 해당 회원이 해당 랩실을 해당 기간에 대여를 등록한 적이 있습니다.");
	}
}
