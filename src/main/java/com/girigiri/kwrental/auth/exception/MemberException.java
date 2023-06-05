package com.girigiri.kwrental.auth.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class MemberException extends BadRequestException {
	public MemberException(String message) {
		super(message);
	}
}
