package com.girigiri.kwrental.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.dto.request.RegisterMemberRequest;
import com.girigiri.kwrental.auth.dto.request.ResetPasswordRequest;
import com.girigiri.kwrental.auth.exception.EmailNotMatchesException;
import com.girigiri.kwrental.auth.exception.MemberException;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.mail.EmailService;
import com.girigiri.kwrental.testsupport.fixture.MemberFixture;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private EmailService emailService;

	@InjectMocks
	private AuthService authService;

	@Test
	@DisplayName("중복된 회원은 등록할 수 없다.")
	void register_duplicatedMember() {
		// given
		given(memberRepository.findByMemberNumber(anyString()))
			.willReturn(Optional.of(MemberFixture.create()));
		final RegisterMemberRequest request = RegisterMemberRequest.builder()
			.memberNumber("2016317016")
			.name("양동주")
			.birthDate("970309")
			.email("djwhy5510@naver.com")
			.password("12345678")
			.build();

		// when, then
		assertThatThrownBy(() -> authService.register(request))
			.isExactlyInstanceOf(MemberException.class);
	}

	@Test
	@DisplayName("회원의 비밀번호를 재설정한다.")
	void resetPassword() {
		// given
		final Member member = MemberFixture.create();
		given(memberRepository.findByMemberNumber(any())).willReturn(Optional.of(member));
		given(passwordEncoder.encode(any())).willReturn("password");
		doNothing().when(emailService).sendRenewPassword(anyString(), anyString());
		final ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest("2016317016", member.getEmail());

		// when
		assertThatCode(() -> authService.resetPassword(resetPasswordRequest))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("회원의 비밀번호를 재설정한다.")
	void resetPassword_emailNotMatches() {
		// given
		final Member member = MemberFixture.builder().email("email@email.com").build();
		given(memberRepository.findByMemberNumber(any())).willReturn(Optional.of(member));
		final ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest("2016317016", "notMache@email.com");

		// when
		assertThatThrownBy(() -> authService.resetPassword(resetPasswordRequest))
			.isExactlyInstanceOf(EmailNotMatchesException.class);
	}
}