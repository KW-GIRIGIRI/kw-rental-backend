package com.girigiri.kwrental.auth.dto.request;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;

@Getter
public class PasswordCheckRequest {
	@Length(min = 8)
	private String password;

	private PasswordCheckRequest() {
	}

	public PasswordCheckRequest(String password) {
		this.password = password;
	}
}
