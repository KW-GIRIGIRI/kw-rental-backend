package com.girigiri.kwrental.auth.service;

import com.girigiri.kwrental.common.mail.MailEvent;

public class RenewPasswordAlertEvent extends MailEvent {
	protected RenewPasswordAlertEvent(final String renewPassword, final String email, final Object source) {
		super("임시비밀번호 알려드립니다.", String.format("임시 비밀번호 : %s", renewPassword), email, source);
	}
}
