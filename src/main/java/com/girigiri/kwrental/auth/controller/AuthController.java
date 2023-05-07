package com.girigiri.kwrental.auth.controller;

import com.girigiri.kwrental.auth.domain.SessionMember;
import com.girigiri.kwrental.auth.dto.request.LoginRequest;
import com.girigiri.kwrental.auth.dto.request.RegisterMemberRequest;
import com.girigiri.kwrental.auth.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/members")
public class AuthController {

    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody @Validated final RegisterMemberRequest registerMemberRequest) {
        final Long memberId = authService.register(registerMemberRequest);
        return ResponseEntity
                .created(URI.create("/api/members/" + memberId)).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated final LoginRequest loginRequest, HttpSession session) {
        final SessionMember sessionMember = authService.login(loginRequest);
        session.setAttribute("member", sessionMember);
        return ResponseEntity.ok().build();
    }
}
