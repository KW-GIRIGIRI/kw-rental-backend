package com.girigiri.kwrental.auth.service;

import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.domain.Role;
import com.girigiri.kwrental.auth.domain.SessionMember;
import com.girigiri.kwrental.auth.dto.request.LoginRequest;
import com.girigiri.kwrental.auth.dto.request.RegisterMemberRequest;
import com.girigiri.kwrental.auth.dto.response.MemberResponse;
import com.girigiri.kwrental.auth.exception.MemberNotFoundException;
import com.girigiri.kwrental.auth.exception.PasswordNotMatchesException;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(final MemberRepository memberRepository, final PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Long register(final RegisterMemberRequest registerMemberRequest) {
        final Member member = Member.builder()
                .name(registerMemberRequest.getName())
                .birthDate(registerMemberRequest.getBirthDate())
                .email(registerMemberRequest.getEmail())
                .password(passwordEncoder.encode(registerMemberRequest.getPassword()))
                .memberNumber(registerMemberRequest.getMemberNumber())
                .phoneNumber(registerMemberRequest.getPhoneNumber())
                .role(Role.USER)
                .build();
        memberRepository.save(member);
        return member.getId();
    }

    public SessionMember login(final LoginRequest loginRequest) {
        final Member member = memberRepository.findByMemberNumber(loginRequest.getMemberNumber())
                .orElseThrow(MemberNotFoundException::new);
        final boolean matches = passwordEncoder.matches(loginRequest.getPassword(), member.getPassword());
        if (!matches) throw new PasswordNotMatchesException();
        return SessionMember.from(member);
    }

    public MemberResponse getMember(final Long id) {
        final Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
        return MemberResponse.from(member);
    }
}
