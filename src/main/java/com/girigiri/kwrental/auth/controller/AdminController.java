package com.girigiri.kwrental.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.girigiri.kwrental.auth.argumentresolver.Login;
import com.girigiri.kwrental.auth.domain.Role;
import com.girigiri.kwrental.auth.domain.SessionMember;
import com.girigiri.kwrental.auth.dto.request.UpdateAdminRequest;
import com.girigiri.kwrental.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

	private final AuthService authService;

	@PatchMapping
	public ResponseEntity<?> update(@Login(Role.ADMIN) final SessionMember sessionMember,
		@RequestBody final UpdateAdminRequest updateAdminRequest) {
		authService.updateAdmin(sessionMember.getId(), updateAdminRequest);
		return ResponseEntity.noContent().build();
	}
}
