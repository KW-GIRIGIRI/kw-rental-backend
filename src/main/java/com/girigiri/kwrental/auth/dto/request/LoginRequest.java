package com.girigiri.kwrental.auth.dto.request;

import lombok.Builder;

@Builder
public record LoginRequest(String memberNumber, String password) {
}
