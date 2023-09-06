package com.girigiri.kwrental.acceptance;

import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

class ScheduleAcceptanceTest extends AcceptanceTest {

	@Test
	@DisplayName("랩실 운영 일정을 설정한다.")
	void putSchedules() {
		// given
		final String requestBody = """
			{
			    "isRunning": true,
			    "schedules": [
			        {
			            "dayOfWeek": "MONDAY",
			            "startAt": "00:00",
			            "endAt": "23:59"
			        }
			    ]
			}""";

		// when, then
		RestAssured.given(requestSpec)
			.filter(document("putSchedules"))
			.body(requestBody).contentType(ContentType.JSON)
			.when().log().all().put("/api/admin/schedules")
			.then().log().all().statusCode(HttpStatus.NO_CONTENT.value());
	}
}
