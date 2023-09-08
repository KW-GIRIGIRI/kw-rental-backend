package com.girigiri.kwrental.common.mail;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailEventListener {
	private final JavaMailSender javaMailSender;

	@EventListener
	@Async(MailAsyncConfig.MAIL_THREAD_POOL)
	public void handleMailEvent(final MailEvent mailEvent) {
		final SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setSubject(mailEvent.getSubject());
		mailMessage.setText(mailEvent.getBody());
		mailMessage.setTo(mailEvent.getEmail());
		javaMailSender.send(mailMessage);
	}
}
