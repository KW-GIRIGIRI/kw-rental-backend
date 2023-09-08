package com.girigiri.kwrental.common.mail;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class MailAsyncConfig {

	public static final String MAIL_THREAD_POOL = "mailThreadPoolTaskExecutor";

	@Bean(name = MAIL_THREAD_POOL)
	public Executor mailThreadPoolTaskExecutor() {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(3);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("MailExecutor-");
		return executor;
	}
}
