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
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.labroom.domain.LabRoom;
import com.girigiri.kwrental.labroom.dto.response.RemainReservationCountPerDateResponse;
import com.girigiri.kwrental.labroom.dto.response.RemainReservationCountsPerDateResponse;
import com.girigiri.kwrental.labroom.repository.LabRoomRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

import io.restassured.RestAssured;

public class LabRoomAcceptanceTest extends AcceptanceTest {

	@Autowired
	private LabRoomRepository labRoomRepository;

	@Autowired
	private ReservationSpecRepository reservationSpecRepository;

	@Test
	@DisplayName("특정 랩실의 날짜별 남은 갯수를 조회한다.")
	void getRemainQuantitiesBetween() {
		// given
		final LabRoom labRoom = LabRoomFixture.builder().name("hanul").totalQuantity(16).build();
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
}
