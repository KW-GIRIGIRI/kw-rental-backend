package com.girigiri.kwrental.auth.exception;

import com.girigiri.kwrental.common.exception.InternalServerErrorException;

public class SessionMemberInconsistencyException extends InternalServerErrorException {
    public SessionMemberInconsistencyException() {
        super("세션에 담긴 정보가 실제 회원 정보와 일치하지 않습니다.");
    }
}
