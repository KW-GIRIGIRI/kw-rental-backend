package com.girigiri.kwrental.acceptance;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.inventory.repository.InventoryRepository;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.reservation.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.dto.request.AddReservationRequest;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.testsupport.fixture.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

class ReservationAcceptanceTest extends AcceptanceTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("대여 예약를 등록한다.")
    void reserve() {
        // given
        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        final Item item = itemRepository.save(ItemFixture.builder().equipmentId(equipment.getId()).build());
        final Inventory inventory = inventoryRepository.save(InventoryFixture.create(equipment));

        final AddReservationRequest request = AddReservationRequest.builder()
                .renterName("대여자")
                .renterEmail("djwhy5510@naver.com")
                .renterPhoneNumber("010-7301-5510")
                .rentalPurpose("필요하니까")
                .build();

        // when
        RestAssured.given(requestSpec)
                .filter(document("addReservations"))
                .body(request).contentType(ContentType.JSON)
                .when().log().all().post("/api/reservations")
                .then().log().all().statusCode(HttpStatus.CREATED.value())
                .header(HttpHeaders.LOCATION, containsString("/api/reservations/"));
    }

    @Test
    @DisplayName("특정 기자재에 대여 예약된 이력을 조회한다.")
    void getReservationsByEquipment() {
        // given
        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        final Item item = itemRepository.save(ItemFixture.builder().equipmentId(equipment.getId()).build());
        final RentalSpec rentalSpec1 = RentalSpecFixture.builder(equipment).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();
        final RentalSpec rentalSpec2 = RentalSpecFixture.builder(equipment).period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))).build();
        final Reservation reservation = reservationRepository.save(ReservationFixture.create(List.of(rentalSpec1, rentalSpec2)));

        // when
        final ReservationsByEquipmentPerYearMonthResponse response = RestAssured.given(requestSpec)
                .filter(document("admin_getReservationByEquipment"))
                .when().log().all().get("/api/admin/reservations?equipmentId={id}&yearMonth={yearMonth}", equipment.getId(), YearMonth.now().toString())
                .then().log().all().statusCode(HttpStatus.OK.value())
                .extract().as(ReservationsByEquipmentPerYearMonthResponse.class);

        // then
        assertAll(
                () -> assertThat(response.getReservations().get(LocalDate.now().getDayOfMonth()))
                        .usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(reservation.getName())
        );
    }
}