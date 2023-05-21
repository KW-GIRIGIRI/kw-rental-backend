package com.girigiri.kwrental.acceptance;

import com.girigiri.kwrental.equipment.dto.response.RemainQuantitiesPerDateResponse;
import com.girigiri.kwrental.equipment.dto.response.RemainQuantityPerDateResponse;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.labroom.domain.LabRoom;
import com.girigiri.kwrental.labroom.repository.LabRoomRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class LabRoomAcceptanceTest extends AcceptanceTest {

    @Autowired
    private LabRoomRepository labRoomRepository;

    @Autowired
    private ReservationSpecRepository reservationSpecRepository;

    @Test
    @DisplayName("특정 기자재의 날짜별 남은 갯수를 조회한다.")
    void getRemainQuantitiesBetween() {
        // given
        final LabRoom labRoom = LabRoomFixture.builder().name("hanool").totalQuantity(10).build();
        labRoomRepository.save(labRoom);
        LocalDate monday = LocalDate.of(2023, 5, 15);
        reservationSpecRepository.save(ReservationSpecFixture.builder(labRoom).amount(RentalAmount.ofPositive(5)).period(new RentalPeriod(monday, monday.plusDays(1))).build());
        reservationSpecRepository.save(ReservationSpecFixture.builder(labRoom).amount(RentalAmount.ofPositive(4)).period(new RentalPeriod(monday.plusDays(1), monday.plusDays(2))).build());
        reservationSpecRepository.save(ReservationSpecFixture.builder(labRoom).amount(RentalAmount.ofPositive(3)).period(new RentalPeriod(monday.plusDays(2), monday.plusDays(3))).build());

        // when
        final RemainQuantitiesPerDateResponse response = RestAssured.given(requestSpec)
                .filter(document("admin_getLabRoomRemainQuantities"))
                .when().log().all().get("/api/admin/labRooms/{name}/remainQuantities?from={from}&to={to}", labRoom.getName(), monday.toString(), monday.plusDays(2).toString())
                .then().log().all().statusCode(HttpStatus.OK.value())
                .extract().as(RemainQuantitiesPerDateResponse.class);

        // then
        assertThat(response.getRemainQuantities()).usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                        new RemainQuantityPerDateResponse(monday, 5),
                        new RemainQuantityPerDateResponse(monday.plusDays(1), 6),
                        new RemainQuantityPerDateResponse(monday.plusDays(2), 7)
                );
    }
}
