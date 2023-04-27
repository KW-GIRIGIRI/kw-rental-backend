package com.girigiri.kwrental.member.service;

import com.girigiri.kwrental.member.domain.Member;
import com.girigiri.kwrental.member.domain.Role;
import com.girigiri.kwrental.member.dto.request.LoginRequest;
import com.girigiri.kwrental.member.dto.request.RegisterMemberRequest;
import com.girigiri.kwrental.member.exception.MemberNotFoundException;
import com.girigiri.kwrental.member.exception.PasswordNotMatchesException;
import com.girigiri.kwrental.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(final MemberRepository memberRepository, final PasswordEncoder passwordEncoder) {
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

    public Long login(final LoginRequest loginRequest) {
        final Member member = memberRepository.findByMemberNumber(loginRequest.getMemberNumber())
                .orElseThrow(MemberNotFoundException::new);
        final boolean matches = passwordEncoder.matches(loginRequest.getPassword(), member.getPassword());
        if (!matches) throw new PasswordNotMatchesException();
        return member.getId();
    }
}
