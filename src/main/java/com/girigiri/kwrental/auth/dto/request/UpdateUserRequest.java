package com.girigiri.kwrental.auth.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record UpdateUserRequest(@Length(min = 8) String password, @Email String email, @NotEmpty String phoneNumber) {

}
