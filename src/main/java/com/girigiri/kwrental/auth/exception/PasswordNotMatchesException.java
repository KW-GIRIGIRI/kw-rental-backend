package com.girigiri.kwrental.auth.exception;

import com.girigiri.kwrental.common.exception.BadRequestException;

public class PasswordNotMatchesException extends BadRequestException {
    public PasswordNotMatchesException() {
        super("비밀번호가 일치하지 않습니다.");
    }
}
