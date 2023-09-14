package com.girigiri.kwrental.common.mail;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public abstract class MailEvent extends ApplicationEvent {
	protected static final String SUBJECT_PREFIX = "[광운대학교 미디어커뮤니케이션 랩실]";

	private final String subject;
	private final String body;
	private final String email;

	protected MailEvent(final String subject, final String body, final String email, final Object source) {
		super(source);
		this.subject = SUBJECT_PREFIX + subject;
		this.body = body;
		this.email = email;
	}
}
