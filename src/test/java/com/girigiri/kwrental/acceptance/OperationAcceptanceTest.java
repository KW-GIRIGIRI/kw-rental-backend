package com.girigiri.kwrental.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import java.time.DayOfWeek;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.girigiri.kwrental.operation.dto.response.EntireOperationResponse;
import com.girigiri.kwrental.operation.dto.response.SchedulesResponse;
import com.girigiri.kwrental.operation.repository.EntireOperationRepository;
import com.girigiri.kwrental.operation.repository.ScheduleRepository;
import com.girigiri.kwrental.testsupport.fixture.EntireOperationFixture;
import com.girigiri.kwrental.testsupport.fixture.ScheduleFixture;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

class OperationAcceptanceTest extends AcceptanceTest {

	@Autowired
	private ScheduleRepository scheduleRepository;
	@Autowired
	private EntireOperationRepository entireOperationRepository;

	@Test
	@DisplayName("랩실 운영 일정을 설정한다.")
	void putSchedules() {
		// given
		final String requestBody = """
			{
			    "schedules": [
			       "MONDAY" 
			    ]
			}""";

		// when, then
		RestAssured.given(requestSpec)
			.filter(document("putSchedules"))
			.body(requestBody).contentType(ContentType.JSON)
			.when().log().all().put("/api/admin/operations/schedules")
			.then().log().all().statusCode(HttpStatus.NO_CONTENT.value());
	}

	@Test
	@DisplayName("랩실 운영 요일을 조회한다.")
	void getSchedules() {
		// given
		scheduleRepository.saveAll(
			List.of(ScheduleFixture.create(DayOfWeek.MONDAY), ScheduleFixture.create(DayOfWeek.FRIDAY)));

		// when
		final SchedulesResponse response = RestAssured.given(requestSpec)
			.filter(document("getSchedules"))
			.when().log().all().get("/api/admin/operations/schedules")
			.then().log().all().statusCode(HttpStatus.OK.value())
			.extract().as(SchedulesResponse.class);

		// then
		assertThat(response.schedules()).containsExactlyInAnyOrder(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
	}

	@Test
	@DisplayName("랩실 전체 운영을 조회한다.")
	void getEntireOperation() {
		// given
		entireOperationRepository.save(EntireOperationFixture.create(true));

		// when
		final EntireOperationResponse response = RestAssured.given(requestSpec)
			.filter(document("getEntireOperation"))
			.when().log().all().get("/api/admin/operations")
			.then().log().all().statusCode(HttpStatus.OK.value())
			.extract().as(EntireOperationResponse.class);

		// then
		assertThat(response.isRunning()).isTrue();
	}

	@Test
	@DisplayName("랩실 전체 운영을 설정한다.")
	void putEntireOperation() {
		// given
		final String requestBody = """
			{
			    "isRunning": true
			}""";

		// when, then
		RestAssured.given(requestSpec)
			.filter(document("putEntireOperation"))
			.body(requestBody).contentType(ContentType.JSON)
			.when().log().all().put("/api/admin/operations")
			.then().log().all().statusCode(HttpStatus.NO_CONTENT.value());
	}
}
