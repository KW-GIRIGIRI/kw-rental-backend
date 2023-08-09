package com.girigiri.kwrental.auth.dto.request;

import org.hibernate.validator.constraints.Length;

import lombok.Builder;

@Builder
public record PasswordCheckRequest(@Length(min = 8) String password) {
}
