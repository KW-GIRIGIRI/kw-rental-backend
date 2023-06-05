package com.girigiri.kwrental.auth.controller;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.girigiri.kwrental.auth.argumentresolver.Login;
import com.girigiri.kwrental.auth.domain.Role;
import com.girigiri.kwrental.auth.domain.SessionMember;
import com.girigiri.kwrental.auth.dto.request.LoginRequest;
import com.girigiri.kwrental.auth.dto.request.PasswordCheckRequest;
import com.girigiri.kwrental.auth.dto.request.RegisterMemberRequest;
import com.girigiri.kwrental.auth.dto.request.UpdateUserRequest;
import com.girigiri.kwrental.auth.dto.response.MemberResponse;
import com.girigiri.kwrental.auth.exception.SessionNotFoundException;
import com.girigiri.kwrental.auth.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/members")
public class AuthController {

	private final AuthService authService;
	private final SessionCookieSupport sessionCookieSupport;

	public AuthController(final AuthService authService, final SessionCookieSupport sessionCookieSupport) {
		this.authService = authService;
		this.sessionCookieSupport = sessionCookieSupport;
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

	@PostMapping("/logout")
	public ResponseEntity<?> logout(
		final HttpSession session,
		@CookieValue(name = SessionCookieSupport.COOKIE_NAME, required = false) Cookie cookie) {
		if (session == null || cookie == null) {
			throw new SessionNotFoundException();
		}
		session.invalidate();
		return ResponseEntity.noContent()
			.header(HttpHeaders.SET_COOKIE, sessionCookieSupport.createLogoutCookie().toString())
			.build();
	}

	@GetMapping
	public MemberResponse getMember(@Login final SessionMember sessionMember) {
		return authService.getMemberResponse(sessionMember.getId());
	}

	@GetMapping("/memberNumber")
	public SessionMember getMemberNumber(final HttpServletRequest request) {
		final HttpSession session = request.getSession(false);
		if (session == null)
			throw new SessionNotFoundException();
		return (SessionMember)session.getAttribute("member");
	}

	@PatchMapping
	public ResponseEntity<?> updateMember(@Login final SessionMember sessionMember,
		@RequestBody final UpdateUserRequest updateUserRequest) {
		authService.updateMember(sessionMember.getId(), updateUserRequest);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/password")
	public ResponseEntity<?> checkPassword(@Login({Role.ADMIN, Role.USER}) final SessionMember sessionMember,
		@RequestBody final PasswordCheckRequest passwordCheckRequest) {
		authService.checkPassword(sessionMember.getId(), passwordCheckRequest.getPassword());
		return ResponseEntity.noContent().build();
	}
}
