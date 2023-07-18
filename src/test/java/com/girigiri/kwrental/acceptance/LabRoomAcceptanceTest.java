package com.girigiri.kwrental.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.girigiri.kwrental.asset.dto.response.RemainQuantitiesPerDateResponse;
import com.girigiri.kwrental.asset.dto.response.RemainQuantityPerDateResponse;
import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.asset.labroom.domain.LabRoomDailyBan;
import com.girigiri.kwrental.asset.labroom.dto.request.LabRoomAvailableRequest;
import com.girigiri.kwrental.asset.labroom.dto.request.LabRoomNoticeRequest;
import com.girigiri.kwrental.asset.labroom.dto.response.LabRoomAvailableResponse;
import com.girigiri.kwrental.asset.labroom.dto.response.LabRoomNoticeResponse;
import com.girigiri.kwrental.asset.labroom.dto.response.RemainReservationCountPerDateResponse;
import com.girigiri.kwrental.asset.labroom.dto.response.RemainReservationCountsPerDateResponse;
import com.girigiri.kwrental.asset.labroom.repository.LabRoomDailyBanRepository;
import com.girigiri.kwrental.asset.labroom.repository.LabRoomRepository;
import com.girigiri.kwrental.reservation.domain.RentalAmount;
import com.girigiri.kwrental.reservation.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class LabRoomAcceptanceTest extends AcceptanceTest {

	@Autowired
	private LabRoomRepository labRoomRepository;

	@Autowired
	private ReservationSpecRepository reservationSpecRepository;

	@Autowired
	private LabRoomDailyBanRepository labRoomDailyBanRepository;

	@Test
	@DisplayName("특정 랩실의 날짜별 남은 갯수를 조회한다.")
	void getRemainQuantitiesBetween() {
		// given
		final LabRoom labRoom = LabRoomFixture.builder().name("hanul").totalQuantity(16).rentableQuantity(16).build();
		labRoomRepository.save(labRoom);
		LocalDate monday = LocalDate.of(2023, 5, 15);
		reservationSpecRepository.save(ReservationSpecFixture.builder(labRoom)
			.amount(RentalAmount.ofPositive(5))
			.period(new RentalPeriod(monday, monday.plusDays(1)))
			.build());
		reservationSpecRepository.save(ReservationSpecFixture.builder(labRoom)
			.amount(RentalAmount.ofPositive(4))
			.period(new RentalPeriod(monday.plusDays(1), monday.plusDays(2)))
			.build());
		reservationSpecRepository.save(ReservationSpecFixture.builder(labRoom)
			.amount(RentalAmount.ofPositive(3))
			.period(new RentalPeriod(monday.plusDays(2), monday.plusDays(3)))
			.build());

		// when
		final RemainQuantitiesPerDateResponse response = RestAssured.given(requestSpec)
			.filter(document("admin_getLabRoomRemainQuantities"))
			.when()
			.log()
			.all()
			.get("/api/admin/labRooms/{name}/remainQuantities?from={from}&to={to}", labRoom.getName(),
				monday.toString(), monday.plusDays(2).toString())
			.then()
			.log()
			.all()
			.statusCode(HttpStatus.OK.value())
			.extract()
			.as(RemainQuantitiesPerDateResponse.class);

		// then
		assertThat(response.getRemainQuantities()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(
				new RemainQuantityPerDateResponse(monday, 11),
				new RemainQuantityPerDateResponse(monday.plusDays(1), 12),
				new RemainQuantityPerDateResponse(monday.plusDays(2), 13)
			);
	}

	@Test
	@DisplayName("특정 랩실의 날짜별 남은 대여 갯수를 조회한다.")
	void getRemainReservationCounts() {
		// given
		final LabRoom labRoom = LabRoomFixture.builder().name("hanul").reservationCountPerDay(1).build();
		labRoomRepository.save(labRoom);
		LocalDate monday = LocalDate.of(2023, 5, 15);
		reservationSpecRepository.save(ReservationSpecFixture.builder(labRoom)
			.period(new RentalPeriod(monday, monday.plusDays(1)))
			.build());
		reservationSpecRepository.save(ReservationSpecFixture.builder(labRoom)
			.period(new RentalPeriod(monday.plusDays(2), monday.plusDays(3)))
			.build());

		// when
		final RemainReservationCountsPerDateResponse response = RestAssured.given(requestSpec)
			.filter(document("getRemainReservationCounts"))
			.when().log().all()
			.get("/api/admin/labRooms/{name}/remainReservationCounts?from={from}&to={to}", labRoom.getName(),
				monday.toString(), monday.plusDays(3).toString())
			.then().log().all()
			.statusCode(HttpStatus.OK.value())
			.extract().as(RemainReservationCountsPerDateResponse.class);

		// then
		assertThat(response.getId()).isEqualTo(labRoom.getId());
		assertThat(response.getRemainReservationCounts()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(
				new RemainReservationCountPerDateResponse(monday, 0),
				new RemainReservationCountPerDateResponse(monday.plusDays(1), 1),
				new RemainReservationCountPerDateResponse(monday.plusDays(2), 0),
				new RemainReservationCountPerDateResponse(monday.plusDays(3), 1)
			);
	}

	@Test
	@DisplayName("랩실 공지사항을 업데이트 한다.")
	void updateNotice() {
		// given
		LabRoom hanul = labRoomRepository.save(LabRoomFixture.builder().name("hanul").build());

		LabRoomNoticeRequest requestBody = new LabRoomNoticeRequest("이러쿵 저러쿵하니까 잘 이용해주세요!!!!!!");

		// when
		RestAssured.given(requestSpec)
			.filter(document("setNotice"))
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when().log().all()
			.put("/api/admin/labRooms/{name}/notice", hanul.getName())
			.then().log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());

		// then
		LabRoom actual = labRoomRepository.findLabRoomByName(hanul.getName())
			.orElseThrow();
		assertThat(actual.getNotice()).isEqualTo(requestBody.getNotice());
	}

	@Test
	@DisplayName("랩실 공지사항을 조회한다.")
	void getNotice() {
		// given
		LabRoom hanul = labRoomRepository.save(LabRoomFixture.builder().name("hanul").notice("notice").build());

		// when
		LabRoomNoticeResponse response = RestAssured.given(requestSpec)
			.filter(document("getNotice"))
			.when().log().all()
			.get("/api/admin/labRooms/{name}/notice", hanul.getName())
			.then().log().all()
			.statusCode(HttpStatus.OK.value())
			.extract().as(LabRoomNoticeResponse.class);

		// then
		assertThat(response.getNotice()).isEqualTo(hanul.getNotice());
	}

	@Test
	@DisplayName("특정 랩실을 사용불가 처리한다.")
	void setAvailableEntirePeriod() {
		// given
		LabRoom hanul = labRoomRepository.save(LabRoomFixture.builder().name("hanul").notice("notice").build());

		// when
		LabRoomAvailableRequest requestBody = new LabRoomAvailableRequest(true, null, false);
		RestAssured.given(requestSpec)
			.filter(document("setAvailableEntirePeriod"))
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when().log().all()
			.patch("/api/admin/labRooms/{name}/available", hanul.getName())
			.then().log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());

		// then
		LabRoom actual = labRoomRepository.findLabRoomByName(hanul.getName()).orElseThrow();
		assertThat(actual.isAvailable()).isFalse();
	}

	@Test
	@DisplayName("특정 랩실의 특정 일자를 사용 불가 처리한다.")
	void setAvailable() {
		// given
		LabRoom hanul = labRoomRepository.save(LabRoomFixture.builder().name("hanul").notice("notice").build());

		// when
		LocalDate now = LocalDate.now();
		LabRoomAvailableRequest requestBody = new LabRoomAvailableRequest(false, now, false);
		RestAssured.given(requestSpec)
			.filter(document("setAvailable"))
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when().log().all()
			.patch("/api/admin/labRooms/{name}/available", hanul.getName())
			.then().log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());

		// then
		LabRoomDailyBan actual = labRoomDailyBanRepository.findByLabRoomIdAndBanDate(hanul.getId(),
			now).orElseThrow();
		assertThat(actual.getLabRoomId()).isEqualTo(hanul.getId());
		assertThat(actual.getBanDate()).isEqualTo(now);
	}

	@Test
	@DisplayName("랩실 전체 사용 불가 인지 확인한다.")
	void getAvailable() {
		// given
		final LabRoom hanul = labRoomRepository.save(LabRoomFixture.builder().name("hanul").isAvailable(true).build());

		// when
		final LabRoomAvailableResponse response = RestAssured.given(requestSpec)
			.filter(document("getAvailable"))
			.given().log().all()
			.get("/api/labRooms/{name}/available", hanul.getName())
			.then().log().all()
			.statusCode(HttpStatus.OK.value())
			.extract().as(LabRoomAvailableResponse.class);

		// then
		assertThat(response).usingRecursiveComparison()
			.isEqualTo(new LabRoomAvailableResponse(hanul.getId(), true, null));

	}

	@Test
	@DisplayName("특정 날짜에 랩실 사용 불가 인지 확인한다.")
	void getAvailableByDate() {
		// given
		final LabRoom hanul = labRoomRepository.save(LabRoomFixture.builder().name("hanul").isAvailable(true).build());
		labRoomDailyBanRepository.save(
			LabRoomDailyBan.builder().labRoomId(hanul.getId()).banDate(LocalDate.now()).build());

		// when
		final LabRoomAvailableResponse response = RestAssured.given(requestSpec)
			.filter(document("getAvailableByDate"))
			.given().log().all()
			.get("/api/labRooms/{name}/available?date={date}", hanul.getName(), LocalDate.now().toString())
			.then().log().all()
			.statusCode(HttpStatus.OK.value())
			.extract().as(LabRoomAvailableResponse.class);

		// then
		assertThat(response).usingRecursiveComparison()
			.isEqualTo(new LabRoomAvailableResponse(hanul.getId(), false, LocalDate.now()));

	}
}
