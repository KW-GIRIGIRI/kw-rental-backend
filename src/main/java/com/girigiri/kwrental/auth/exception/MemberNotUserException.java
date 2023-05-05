package com.girigiri.kwrental.auth.exception;

public class MemberNotUserException extends ForbiddenException {

    public MemberNotUserException() {
        super("회원이 사용자 권한이 아닙니다.");
    }
}
