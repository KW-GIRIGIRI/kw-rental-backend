package com.girigiri.kwrental.member.controller;

import com.girigiri.kwrental.member.dto.request.RegisterMemberRequest;
import com.girigiri.kwrental.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody @Validated final RegisterMemberRequest registerMemberRequest) {
        final Long memberId = memberService.register(registerMemberRequest);
        return ResponseEntity
                .created(URI.create("/api/members/" + memberId)).build();
    }
}
