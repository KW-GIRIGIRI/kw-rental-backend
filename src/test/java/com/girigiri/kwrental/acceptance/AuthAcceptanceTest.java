package com.girigiri.kwrental.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.domain.Role;
import com.girigiri.kwrental.auth.domain.SessionMember;
import com.girigiri.kwrental.auth.dto.request.LoginRequest;
import com.girigiri.kwrental.auth.dto.request.PasswordCheckRequest;
import com.girigiri.kwrental.auth.dto.request.RegisterMemberRequest;
import com.girigiri.kwrental.auth.dto.request.ResetPasswordRequest;
import com.girigiri.kwrental.auth.dto.request.UpdateAdminRequest;
import com.girigiri.kwrental.auth.dto.request.UpdateUserRequest;
import com.girigiri.kwrental.auth.dto.response.MemberResponse;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.testsupport.fixture.MemberFixture;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

class AuthAcceptanceTest extends AcceptanceTest {

	@Autowired
	private MemberRepository memberRepository;

	@Test
	@DisplayName("회원가입한다.")
	void register() {
		// given
		final RegisterMemberRequest request = RegisterMemberRequest.builder()
			.name("양동주")
			.birthDate("970309")
			.memberNumber("2016317016")
			.email("djwhy5510@naver.com")
			.password("12345678")
			.phoneNumber("01073015510")
			.build();

		// when, then
		RestAssured.given(requestSpec)
			.filter(document("registerMember"))
			.body(request)
			.contentType(ContentType.JSON)
			.when().log().all().post("/api/members")
			.then().log().all()
			.statusCode(HttpStatus.CREATED.value())
			.header(HttpHeaders.LOCATION, containsString("/api/members/"));
	}

	@Test
	@DisplayName("로그인한다.")
	void login() {
		// given
		final Member member = memberRepository.save(MemberFixture.builder("12345678").build());
		final LoginRequest request = LoginRequest.builder()
			.memberNumber(member.getMemberNumber())
			.password("12345678")
			.build();

		// when
		RestAssured.given(requestSpec)
			.filter(document("login"))
			.body(request)
			.contentType(ContentType.JSON)
			.when().log().all().post("/api/members/login")
			.then().log().all()
			.statusCode(HttpStatus.OK.value())
			.header(HttpHeaders.SET_COOKIE, containsString("JSESSIONID="));
	}

	@Test
	@DisplayName("현재 로그인한 회원정보 조회한다.")
	void getMember() {
		// given
		final String password = "12345678";
		final Member member = memberRepository.save(MemberFixture.builder(password).build());
		final String sessionId = getSessionId(member.getMemberNumber(), password);

		// when
		final MemberResponse memberResponse = RestAssured.given(requestSpec)
			.filter(document("getMember"))
			.sessionId(sessionId)
			.when().log().all().get("/api/members")
			.then().log().all()
			.statusCode(HttpStatus.OK.value())
			.extract().as(MemberResponse.class);

		// then
		assertThat(memberResponse).usingRecursiveComparison().isEqualTo(MemberResponse.from(member));
	}

	@Test
	@DisplayName("현재 로그인한 회원의 학번정보를 조회한다.")
	void getMemberBySession() {
		// given
		final String password = "12345678";
		final Member member = memberRepository.save(MemberFixture.builder(password).build());
		final String sessionId = getSessionId(member.getMemberNumber(), password);

		// when
		final SessionMember response = RestAssured.given(requestSpec)
			.filter(document("getMemberNumber"))
			.sessionId(sessionId)
			.when().log().all().get("/api/members/memberNumber")
			.then().log().all()
			.statusCode(HttpStatus.OK.value())
			.extract().as(SessionMember.class);

		// then
		assertThat(response).usingRecursiveComparison().isEqualTo(SessionMember.from(member));
	}

	@Test
	@DisplayName("로그아웃 한다.")
	void logout() {
		// given
		final String password = "12345678";
		final Member member = memberRepository.save(MemberFixture.builder(password).build());
		final String sessionId = getSessionId(member.getMemberNumber(), password);

		// when
		RestAssured.given(requestSpec)
			.filter(document("logout"))
			.sessionId(sessionId)
			.when().log().all().post("/api/members/logout")
			.then().log().all()
			.statusCode(HttpStatus.NO_CONTENT.value())
			.header(HttpHeaders.SET_COOKIE, containsString("JSESSIONID="));
	}

	@Test
	@DisplayName("비밀번호를 확인한다.")
	void checkPassword() {
		// given
		final String password = "12345678";
		final Member member = memberRepository.save(MemberFixture.builder(password).build());
		final String sessionId = getSessionId(member.getMemberNumber(), password);

		final PasswordCheckRequest requestBody = new PasswordCheckRequest(password);

		// when, then
		RestAssured.given(requestSpec)
			.filter(document("checkPassword"))
			.sessionId(sessionId)
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when().log().all().post("/api/members/password")
			.then().log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@Test
	@DisplayName("사용자의 정보를 수정한다.")
	void updateUser() {
		// given
		final String password = "12345678";
		final Member member = memberRepository.save(MemberFixture.builder(password).build());
		final String sessionId = getSessionId(member.getMemberNumber(), password);

		String updatedPassword = "87654321";
		final UpdateUserRequest requestBody = new UpdateUserRequest("87654321", "hello@naver.com", "010-7301-5510");

		// when
		RestAssured.given(requestSpec)
			.filter(document("updateUser"))
			.sessionId(sessionId)
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when().log().all().patch("/api/members")
			.then().log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());

		// then
		final Member actual = memberRepository.findById(member.getId()).orElseThrow();
		assertThat(actual.getEmail()).isEqualTo(requestBody.getEmail());
		assertThat(actual.getPhoneNumber()).isEqualTo(requestBody.getPhoneNumber());
		boolean matches = new BCryptPasswordEncoder().matches(updatedPassword, actual.getPassword());
		assertThat(matches).isTrue();
	}

	@Test
	@DisplayName("관리자의 정보를 수정한다.")
	void updateAdmin() {
		// given
		final String password = "12345678";
		final Member member = memberRepository.save(MemberFixture.builder(password).role(Role.ADMIN).build());
		final String sessionId = getSessionId(member.getMemberNumber(), password);

		String updatedPassword = "87654321";
		final UpdateAdminRequest requestBody = new UpdateAdminRequest(updatedPassword);

		// when
		RestAssured.given(requestSpec)
			.filter(document("updateAdmin"))
			.sessionId(sessionId)
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when().log().all().patch("/api/admin")
			.then().log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());

		// then
		final Member actual = memberRepository.findById(member.getId()).orElseThrow();
		boolean matches = new BCryptPasswordEncoder().matches(updatedPassword, actual.getPassword());
		assertThat(matches).isTrue();
	}

	@Test
	@DisplayName("비밀번호 초기화를 실행한다.")
	void resetPassword() {
		// given
		final String password = "12345678";
		final Member member = memberRepository.save(MemberFixture.builder(password).role(Role.USER).build());
		final String sessionId = getSessionId(member.getMemberNumber(), password);

		final ResetPasswordRequest requestBody = new ResetPasswordRequest(member.getMemberNumber(),
			"djwhy5510@naver.com");
		doNothing().when(emailService).sendRenewPassword(eq(requestBody.getEmail()), anyString());

		// when
		RestAssured.given(requestSpec)
			.filter(document("resetPassword"))
			.sessionId(sessionId)
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when().log().all().patch("/api/members/password")
			.then().log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());

		// then
		final Member actual = memberRepository.findById(member.getId()).orElseThrow();
		boolean matches = new BCryptPasswordEncoder().matches(password, actual.getPassword());
		assertThat(matches).isFalse();
	}
}