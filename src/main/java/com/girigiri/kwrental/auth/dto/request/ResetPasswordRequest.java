package com.girigiri.kwrental.auth.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class ResetPasswordRequest {
	private String memberNumber;
	@Email
	private String email;

	private ResetPasswordRequest() {
	}

	public ResetPasswordRequest(String memberNumber, String email) {
		this.memberNumber = memberNumber;
		this.email = email;
	}
}
