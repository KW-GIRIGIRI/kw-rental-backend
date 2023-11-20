package com.girigiri.kwrental.common.exception;

import com.girigiri.kwrental.common.mail.MailEvent;

public class InternalServerErrorEvent extends MailEvent {
    public InternalServerErrorEvent(final String body, final String email, final Object source) {
        super("프로젝트에서 예상하지 못한 문제가 발생했습니다!", body, email, source);
    }
}
