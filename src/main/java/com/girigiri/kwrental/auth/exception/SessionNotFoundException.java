package com.girigiri.kwrental.auth.exception;

public class SessionNotFoundException extends UnauthorizedException {

    public SessionNotFoundException() {
        super("세션 정보가 없습니다.");
    }
}
