package com.girigiri.kwrental.auth.dto.request;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;

@Getter
public class UpdateAdminRequest {
	@Length(min = 8)
	private String password;

	private UpdateAdminRequest() {
	}

	public UpdateAdminRequest(String password) {
		this.password = password;
	}
}
