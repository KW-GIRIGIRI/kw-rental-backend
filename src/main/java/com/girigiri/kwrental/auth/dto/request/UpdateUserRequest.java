package com.girigiri.kwrental.auth.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class UpdateUserRequest {
	@Length(min = 8)
	private String password;
	@Email
	private String email;
	@NotEmpty
	private String phoneNumber;

	private UpdateUserRequest() {
	}

	public UpdateUserRequest(String password, String email, String phoneNumber) {
		this.password = password;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}
}
