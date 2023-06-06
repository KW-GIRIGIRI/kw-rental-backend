package com.girigiri.kwrental.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailService {
	private static final String CONTENT_PREFIX = "\n"
		+ " _   __ _    _         ___  ___ _____ ______  _____   ___           _       ___  ______ \n"
		+ "| | / /| |  | |        |  \\/  ||  ___||  _  \\|_   _| / _ \\         | |     / _ \\ | ___ \\\n"
		+ "| |/ / | |  | | ______ | .  . || |__  | | | |  | |  / /_\\ \\ ______ | |    / /_\\ \\| |_/ /\n"
		+ "|    \\ | |/\\| ||______|| |\\/| ||  __| | | | |  | |  |  _  ||______|| |    |  _  || ___ \\\n"
		+ "| |\\  \\\\  /\\  /        | |  | || |___ | |/ /  _| |_ | | | |        | |____| | | || |_/ /\n"
		+ "\\_| \\_/ \\/  \\/         \\_|  |_/\\____/ |___/   \\___/ \\_| |_/        \\_____/\\_| |_/\\____/ \n"
		+ "                                                                                        \n"
		+ "                                                                                        \n";

	private final JavaMailSender javaMailSender;

	public EmailService(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void sendRenewPassword(final String email, final String rawPassword) {
		final SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject("[광운대학교 미디어영상학부 랩실] 임시비밀번호 알려드립니다.");
		message.setText(CONTENT_PREFIX + String.format("임시 비밀번호 : %s", rawPassword));
		message.setTo(email);
		javaMailSender.send(message);
	}
}
