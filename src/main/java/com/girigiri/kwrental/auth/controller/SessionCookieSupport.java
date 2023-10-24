package com.girigiri.kwrental.auth.controller;

import org.springframework.boot.web.server.Cookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class SessionCookieSupport {

    protected static final String COOKIE_NAME = "JSESSIONID";
    private static final int REMOVAL_MAX_AGE = 0;


    public ResponseCookie createLogoutCookie() {
        return createTokenCookieBuilder("")
                .maxAge(REMOVAL_MAX_AGE)
                .build();
    }

    private ResponseCookie.ResponseCookieBuilder createTokenCookieBuilder(final String value) {
        return ResponseCookie.from(COOKIE_NAME, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite(Cookie.SameSite.NONE.attributeValue());
    }
}
