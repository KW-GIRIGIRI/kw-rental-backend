package com.girigiri.kwrental.auth.exception;

import com.girigiri.kwrental.common.exception.NotFoundException;

public class MemberNotFoundException extends NotFoundException {
    public MemberNotFoundException() {
        super("회원 정보를 찾지 못했습니다.");
    }
}
