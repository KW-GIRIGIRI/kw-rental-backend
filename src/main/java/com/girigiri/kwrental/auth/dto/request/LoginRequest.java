package com.girigiri.kwrental.auth.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginRequest {

    private String memberNumber;
    private String password;

    private LoginRequest() {
    }

    @Builder
    private LoginRequest(final String memberNumber, final String password) {
        this.memberNumber = memberNumber;
        this.password = password;
    }
}
