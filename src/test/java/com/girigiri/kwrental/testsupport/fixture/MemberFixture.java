package com.girigiri.kwrental.testsupport.fixture;

import com.girigiri.kwrental.member.domain.Member;
import com.girigiri.kwrental.member.domain.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MemberFixture {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static Member create() {
        return builder().build();
    }

    public static Member.MemberBuilder builder() {
        return Member.builder()
                .memberNumber("2016317016")
                .name("양동주")
                .email("yangdongjue@naver.com")
                .phoneNumber("01073015510")
                .birthDate("970309")
                .role(Role.USER)
                .password(passwordEncoder.encode("12345678"));
    }

    public static Member.MemberBuilder builder(final String password) {
        return builder().password(passwordEncoder.encode(password));
    }
}
