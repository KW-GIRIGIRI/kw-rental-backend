package com.girigiri.kwrental.mail;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public abstract class MailEvent extends ApplicationEvent {
	private final String subject;
	private final String body;
	private final String email;

	protected MailEvent(final String subject, final String body, final String email, final Object source) {
		super(source);
		this.subject = subject;
		this.body = body;
		this.email = email;
	}
}
