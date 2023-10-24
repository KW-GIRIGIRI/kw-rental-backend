package com.girigiri.kwrental.asset.labroom.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class LabRoomAvailableDateFailureException extends BadRequestException {
	public LabRoomAvailableDateFailureException() {
		super("랩실 대여가 전체 기간 동안 불가능해서 특정 일짜에 대여를 활성화 할 수 없습니다.");
	}
}
