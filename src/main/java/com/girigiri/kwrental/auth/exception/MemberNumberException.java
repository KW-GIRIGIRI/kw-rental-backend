package com.girigiri.kwrental.auth.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class MemberNumberException extends BadRequestException {
	public MemberNumberException(final String message) {
		super(message);
	}
}
