package com.girigiri.kwrental.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.girigiri.kwrental.auth.dto.request.KwangwoonMemberRetrieveRequest;
import com.girigiri.kwrental.auth.dto.request.MemberNumberResponse;
import com.girigiri.kwrental.auth.service.MemberNumberRetrieveService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final MemberNumberRetrieveService memberNumberRetrieveService;

	@GetMapping(path = "/memberNumbers", params = {"name", "birthday"})
	public MemberNumberResponse getMemberNumberFromKLAS(
		final KwangwoonMemberRetrieveRequest kwangwoonMemberRetrieveRequest) {
		return memberNumberRetrieveService.retrieveMediaMemberNumber(kwangwoonMemberRetrieveRequest);
	}
}
