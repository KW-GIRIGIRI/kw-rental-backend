package com.girigiri.kwrental.auth.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Builder;

@Builder
public record ResetPasswordRequest(String memberNumber, @Email String email) {

}
