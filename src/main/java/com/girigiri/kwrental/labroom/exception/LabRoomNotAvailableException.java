package com.girigiri.kwrental.labroom.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class LabRoomNotAvailableException extends BadRequestException {
	public LabRoomNotAvailableException() {
		super("랩실을 사용할 수 없습니다.");
	}
}
