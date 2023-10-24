package com.girigiri.kwrental.auth.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record RegisterMemberRequest(
	@Length(min = 2)
	String name,
	@Length(min = 6, max = 6)
	String birthDate,
	@NotEmpty
	String memberNumber,
	@Length(min = 8)
	String password,
	@Email
	String email,
	@NotEmpty
	String phoneNumber) {
}
