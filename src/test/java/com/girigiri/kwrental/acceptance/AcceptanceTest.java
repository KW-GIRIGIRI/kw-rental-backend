package com.girigiri.kwrental.acceptance;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import java.time.DayOfWeek;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;

import com.amazonaws.services.s3.AmazonS3;
import com.girigiri.kwrental.auth.dto.request.LoginRequest;
import com.girigiri.kwrental.auth.service.KwangwoonMemberService;
import com.girigiri.kwrental.common.mail.EmailEventListener;
import com.girigiri.kwrental.operation.repository.EntireOperationRepository;
import com.girigiri.kwrental.operation.repository.ScheduleRepository;
import com.girigiri.kwrental.testsupport.databasecleanup.CleanBeforeEach;
import com.girigiri.kwrental.testsupport.fixture.EntireOperationFixture;
import com.girigiri.kwrental.testsupport.fixture.ScheduleFixture;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(RestDocumentationExtension.class)
@CleanBeforeEach
abstract class AcceptanceTest {

    @MockBean
    protected AmazonS3 amazonS3;
    @MockBean
    protected EmailEventListener emailEventListener;
    @MockBean
    protected KwangwoonMemberService kwangwoonMemberService;
    protected RequestSpecification requestSpec;
    @LocalServerPort
    private int port;

    @Autowired
    private EntireOperationRepository entireOperationRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        RestAssured.requestSpecification = this.requestSpec;
        this.requestSpec = new RequestSpecBuilder()
            .addFilter(documentationConfiguration(restDocumentation).operationPreprocessors()
                .withRequestDefaults(prettyPrint())
                .withResponseDefaults(prettyPrint()))
            .build();
        entireOperationRepository.save(EntireOperationFixture.create(true));
        scheduleRepository.saveAll(
            List.of(ScheduleFixture.create(DayOfWeek.MONDAY), ScheduleFixture.create(DayOfWeek.TUESDAY),
                ScheduleFixture.create(DayOfWeek.WEDNESDAY), ScheduleFixture.create(DayOfWeek.THURSDAY)));
    }

    protected String getSessionId(final String memberNumber, final String password) {
        final LoginRequest request = LoginRequest.builder()
                .memberNumber(memberNumber)
                .password(password)
                .build();
        return RestAssured.given(requestSpec)
                .body(request)
                .contentType(ContentType.JSON)
                .when().post("/api/members/login")
                .then()
                .extract().cookie("JSESSIONID");
    }
}
