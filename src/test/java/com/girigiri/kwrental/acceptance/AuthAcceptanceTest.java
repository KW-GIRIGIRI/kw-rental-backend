package com.girigiri.kwrental.acceptance;

import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.dto.request.LoginRequest;
import com.girigiri.kwrental.auth.dto.request.RegisterMemberRequest;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.testsupport.fixture.MemberFixture;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

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
}
