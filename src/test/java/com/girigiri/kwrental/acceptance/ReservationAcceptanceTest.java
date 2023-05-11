package com.girigiri.kwrental.acceptance;

import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.inventory.repository.InventoryRepository;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.dto.request.AddReservationRequest;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.dto.response.UnterminatedReservationResponse;
import com.girigiri.kwrental.reservation.dto.response.UnterminatedReservationsResponse;
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

    @Autowired
    private MemberRepository memberRepository;


    @Test
    @DisplayName("대여 예약를 등록한다.")
    void reserve() {
        // given
        final String password = "12345678";
        final Member member = memberRepository.save(MemberFixture.create(password));
        final String sessionId = getSessionId(member.getMemberNumber(), password);

        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        final Item item = itemRepository.save(ItemFixture.builder().equipmentId(equipment.getId()).build());
        final Inventory inventory = inventoryRepository.save(InventoryFixture.create(equipment, member.getId()));

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
                .sessionId(sessionId)
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
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment).period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))).build();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.create(List.of(reservationSpec1)));
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.create(List.of(reservationSpec2)));

        // when
        final ReservationsByEquipmentPerYearMonthResponse response = RestAssured.given(requestSpec)
                .filter(document("admin_getReservationByEquipment"))
                .when().log().all().get("/api/admin/reservations?equipmentId={id}&yearMonth={yearMonth}", equipment.getId(), YearMonth.now().toString())
                .then().log().all().statusCode(HttpStatus.OK.value())
                .extract().as(ReservationsByEquipmentPerYearMonthResponse.class);

        // then
        assertThat(response.getReservations().get(LocalDate.now().getDayOfMonth()))
                .usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(reservation1.getName());
    }

    @Test
    @DisplayName("특정 유저의 완료되지 않은 대여 예약 건을 조회한다.")
    void getUnterminatedReservations() {
        // given
        final String password = "12345678";
        final Member member = memberRepository.save(MemberFixture.create(password));
        final String sessionId = getSessionId(member.getMemberNumber(), password);

        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().modelName("modelName1").build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().modelName("modelName2").build());
        final Item item1 = itemRepository.save(ItemFixture.builder().equipmentId(equipment1.getId()).propertyNumber("11111111").build());
        final Item item2 = itemRepository.save(ItemFixture.builder().equipmentId(equipment2.getId()).propertyNumber("22222222").build());
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))).build();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1)).memberId(member.getId()).build());
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec2)).memberId(member.getId()).build());

        // when
        final UnterminatedReservationsResponse response = RestAssured.given(requestSpec)
                .filter(document("getUnterminatedReservations"))
                .sessionId(sessionId)
                .when().log().all().get("/api/reservations?terminated=false")
                .then().log().all().statusCode(HttpStatus.OK.value())
                .extract().as(UnterminatedReservationsResponse.class);

        // then
        assertThat(response.getReservations()).usingRecursiveFieldByFieldElementComparator()
                .containsExactly(UnterminatedReservationResponse.from(reservation1), UnterminatedReservationResponse.from(reservation2));
    }
}