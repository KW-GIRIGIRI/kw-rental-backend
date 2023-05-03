package com.girigiri.kwrental.acceptance;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.rental.dto.request.CreateRentalRequest;
import com.girigiri.kwrental.rental.dto.request.RentalSpecsRequest;
import com.girigiri.kwrental.rental.dto.response.ReservationsWithRentalSpecsByEndDateResponse;
import com.girigiri.kwrental.rental.dto.response.overduereservations.OverdueReservationResponse;
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.ReservationWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.ReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.testsupport.fixture.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

class RentalAcceptanceTest extends AcceptanceTest {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RentalSpecRepository rentalSpecRepository;

    @Test
    @DisplayName("대여를 생성한다.")
    void createRental() {
        // given
        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        final Item item = itemRepository.save(ItemFixture.builder().equipmentId(equipment.getId()).build());
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.create(List.of(reservationSpec1)));

        CreateRentalRequest request = new CreateRentalRequest(
                reservation1.getId(),
                List.of(
                        new RentalSpecsRequest(reservation1.getReservationSpecs().get(0).getId(), List.of(item.getPropertyNumber()))
                )
        );

        // when
        RestAssured.given(requestSpec)
                .filter(document("admin_createRental"))
                .when().log().all()
                .body(request)
                .contentType(ContentType.JSON)
                .post("/api/admin/rentals")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .header(HttpHeaders.LOCATION, containsString("/api/rentals?reservationId="));
    }

    @Test
    @DisplayName("특절 날짜에 수령일인 대여 예약을 조회한다.")
    void getReservationsByStartDate() {
        // given
        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().modelName("test1").build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().modelName("test2").build());

        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();
        final LocalDateTime acceptDateTime = LocalDateTime.now();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).acceptDateTime(acceptDateTime).build());
        final RentalSpec rentalSpec1 = RentalSpecFixture.builder().propertyNumber("11111111").reservationSpecId(reservationSpec1.getId()).build();
        final RentalSpec rentalSpec2 = RentalSpecFixture.builder().propertyNumber("33333333").reservationSpecId(reservationSpec2.getId()).build();
        rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

        final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(2))).build();
        final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(2))).build();
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.create(List.of(reservationSpec3, reservationSpec4)));

        final ReservationSpec reservationSpec5 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))).build();
        final ReservationSpec reservationSpec6 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))).build();
        final Reservation reservation3 = reservationRepository.save(ReservationFixture.create(List.of(reservationSpec5, reservationSpec6)));


        // when
        final ReservationsWithRentalSpecsResponse response = RestAssured.given(requestSpec)
                .filter(document("admin_getReservationsWithRentalSpecsByStartDate"))
                .when().log().all().get("/api/admin/rentals?startDate={startDate}", LocalDate.now().toString())
                .then().log().all().statusCode(HttpStatus.OK.value())
                .extract().as(ReservationsWithRentalSpecsResponse.class);

        // then
        assertThat(response.getReservations()).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(ReservationWithRentalSpecsResponse.of(reservation1, List.of(rentalSpec1, rentalSpec2)),
                        ReservationWithRentalSpecsResponse.of(reservation2, Collections.emptyList()));
    }

    @Test
    @DisplayName("특정 날짜에 반납일인 대여 예약과 연체된 대여 예약을 조회한다.")
    void getReservationWithRentalSpecsByEndDate() {
        // given
        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().modelName("test1").build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().modelName("test2").build());

        final LocalDateTime acceptDateTime = LocalDateTime.now();
        final LocalDate now = LocalDate.now();
        final LocalDate yesterday = now.minusDays(1);

        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(yesterday, now)).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(yesterday, now)).build();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).acceptDateTime(acceptDateTime).build());
        final RentalSpec rentalSpec1 = RentalSpecFixture.builder().propertyNumber("11111111").reservationSpecId(reservationSpec1.getId()).build();
        final RentalSpec rentalSpec2 = RentalSpecFixture.builder().propertyNumber("22222222").reservationSpecId(reservationSpec2.getId()).build();
        rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

        final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(yesterday.minusDays(1), yesterday)).build();
        final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(yesterday.minusDays(1), yesterday)).build();
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec3, reservationSpec4)).acceptDateTime(acceptDateTime).build());
        final RentalSpec rentalSpec3 = RentalSpecFixture.builder().propertyNumber("33333333").reservationSpecId(reservationSpec3.getId()).build();
        final RentalSpec rentalSpec4 = RentalSpecFixture.builder().propertyNumber("44444444").reservationSpecId(reservationSpec4.getId()).returnDateTime(LocalDateTime.now()).build();
        rentalSpecRepository.saveAll(List.of(rentalSpec3, rentalSpec4));

        // when
        final ReservationsWithRentalSpecsByEndDateResponse response = RestAssured.given(requestSpec)
                .filter(document("admin_getReservationsWithRentalSpecsByEndDate"))
                .when().log().all().get("/api/admin/rentals?endDate={endDate}", LocalDate.now().toString())
                .then().log().all().statusCode(HttpStatus.OK.value())
                .extract().as(ReservationsWithRentalSpecsByEndDateResponse.class);

        // then
        assertAll(

                () -> assertThat(response.getOverdueReservations().getReservations()).usingRecursiveFieldByFieldElementComparator()
                        .containsExactlyInAnyOrder(OverdueReservationResponse.of(reservation2, List.of(rentalSpec3))),
                () -> assertThat(response.getReservationsByEndDate().getReservations()).usingRecursiveFieldByFieldElementComparator()
                        .containsExactlyInAnyOrder(ReservationWithRentalSpecsResponse.of(reservation1, List.of(rentalSpec1, rentalSpec2)))
        );
    }
}
