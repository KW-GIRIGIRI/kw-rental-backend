package com.girigiri.kwrental.auth.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.domain.Role;
import com.girigiri.kwrental.auth.domain.SessionMember;
import com.girigiri.kwrental.auth.dto.request.LoginRequest;
import com.girigiri.kwrental.auth.dto.request.RegisterMemberRequest;
import com.girigiri.kwrental.auth.dto.request.ResetPasswordRequest;
import com.girigiri.kwrental.auth.dto.request.UpdateAdminRequest;
import com.girigiri.kwrental.auth.dto.request.UpdateUserRequest;
import com.girigiri.kwrental.auth.dto.response.MemberResponse;
import com.girigiri.kwrental.auth.exception.EmailNotMatchesException;
import com.girigiri.kwrental.auth.exception.ForbiddenException;
import com.girigiri.kwrental.auth.exception.MemberException;
import com.girigiri.kwrental.auth.exception.MemberNotFoundException;
import com.girigiri.kwrental.auth.exception.PasswordNotMatchesException;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.mail.EmailService;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthService(final MemberRepository memberRepository, final PasswordEncoder passwordEncoder,
        EmailService emailService) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public Long register(final RegisterMemberRequest registerMemberRequest) {
        if (memberRepository.findByMemberNumber(registerMemberRequest.getMemberNumber()).isPresent()) {
            throw new MemberException("이미 존재하는 회원입니다.");
        }
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

    @Transactional(readOnly = true)
    public SessionMember login(final LoginRequest loginRequest) {
        final Member member = getMemberByMemberNumber(loginRequest.getMemberNumber());
        final boolean matches = passwordEncoder.matches(loginRequest.getPassword(), member.getPassword());
        if (!matches)
            throw new PasswordNotMatchesException();
        return SessionMember.from(member);
    }

    private Member getMemberByMemberNumber(final String memberNumber) {
        return memberRepository.findByMemberNumber(memberNumber)
            .orElseThrow(MemberNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberResponse(final Long id) {
        final Member member = getMember(id);
        return MemberResponse.from(member);
    }

    private Member getMember(final Long id) {
        return memberRepository.findById(id)
            .orElseThrow(MemberNotFoundException::new);
    }

    @Transactional
    public void updateMember(final Long id, final UpdateUserRequest updateUserRequest) {
        final Member member = getMember(id);
        final String encodedPassword = passwordEncoder.encode(updateUserRequest.getPassword());
        member.updatePassword(encodedPassword);
        member.updateEmail(updateUserRequest.getEmail());
        member.updatePhoneNumber(updateUserRequest.getPhoneNumber());
    }

    @Transactional
    public void updateAdmin(final Long id, final UpdateAdminRequest updateAdminRequest) {
        final Member admin = getMember(id);
        if (!admin.isAdmin()) {
            throw new ForbiddenException("관리자 정보 수정은 관리자만 가능합니다.");
        }
        final String encodedPassword = passwordEncoder.encode(updateAdminRequest.getPassword());
        admin.updatePassword(encodedPassword);
    }

    @Transactional(readOnly = true)
    public void checkPassword(final Long id, final String password) {
        final String encodedPassword = getMember(id).getPassword();
        boolean matches = passwordEncoder.matches(password, encodedPassword);
        if (!matches) {
            throw new PasswordNotMatchesException();
        }
    }

    @Transactional
    public void resetPassword(final ResetPasswordRequest resetPasswordRequest) {
        final Member member = getMemberByMemberNumber(resetPasswordRequest.getMemberNumber());
        if (!member.hasSameEmail(resetPasswordRequest.getEmail())) {
            throw new EmailNotMatchesException();
        }
        final String randomPassword = getRandomPassword();
        final String encodedPassword = passwordEncoder.encode(randomPassword);
        member.updatePassword(encodedPassword);
        emailService.sendRenewPassword(member.getEmail(), randomPassword);
    }

    private String getRandomPassword() {
        String randomUUID = UUID.randomUUID().toString().replaceAll("-", "");
        return randomUUID.substring(0, 8);
    }
}
