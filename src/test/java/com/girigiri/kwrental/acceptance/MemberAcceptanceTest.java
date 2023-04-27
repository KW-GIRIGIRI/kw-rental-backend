package com.girigiri.kwrental.acceptance;

import com.girigiri.kwrental.member.dto.request.RegisterMemberRequest;
import com.girigiri.kwrental.member.repository.MemberRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

class MemberAcceptanceTest extends AcceptanceTest {

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
}
