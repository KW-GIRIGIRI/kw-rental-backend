package com.girigiri.kwrental.auth.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class EmailNotMatchesException extends BadRequestException {
	public EmailNotMatchesException() {
		super("입력된 이메일이 회원의 이메일과 일치하지 않습니다!");
	}
}
