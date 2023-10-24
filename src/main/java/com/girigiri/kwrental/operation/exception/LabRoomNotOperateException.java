package com.girigiri.kwrental.operation.exception;

import com.girigiri.kwrental.common.exception.DomainException;

public class LabRoomNotOperateException extends DomainException {
	public LabRoomNotOperateException() {
		super("랩실이 요청한 일자에 운영하지 않거나 현재 운영하지 않습니다.");
	}
}
