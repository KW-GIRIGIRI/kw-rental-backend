package com.girigiri.kwrental.auth.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class MemberNumberRetrieveException extends BadRequestException {
	public MemberNumberRetrieveException(final String message) {
		super(message);
	}
}
