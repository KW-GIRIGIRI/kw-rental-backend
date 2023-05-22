package com.girigiri.kwrental.acceptance;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.asset.repository.AssetRepository;
import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalDateTime;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.inventory.repository.InventoryRepository;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.dto.request.AddLabRoomReservationRequest;
import com.girigiri.kwrental.reservation.dto.request.AddReservationRequest;
import com.girigiri.kwrental.reservation.dto.request.CancelReservationSpecRequest;
import com.girigiri.kwrental.reservation.dto.response.*;
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
    private AssetRepository assetRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;


    @Test
    @DisplayName("기자재 대여 예약를 등록한다.")
    void reserveEquipment() {
        // given
        final String password = "12345678";
        final Member member = memberRepository.save(MemberFixture.create(password));
        final String sessionId = getSessionId(member.getMemberNumber(), password);

        final Rentable asset = assetRepository.save(EquipmentFixture.create());
        final Item item = itemRepository.save(ItemFixture.builder().assetId(asset.getId()).build());
        final Inventory inventory = inventoryRepository.save(InventoryFixture.create(asset, member.getId()));

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
    @DisplayName("랩실 대여 예약를 등록한다.")
    void reserveLabRoom() {
        // given
        final String password = "12345678";
        final Member member = memberRepository.save(MemberFixture.create(password));
        final String sessionId = getSessionId(member.getMemberNumber(), password);

        final Rentable asset = assetRepository.save(LabRoomFixture.builder().name("hanul").totalQuantity(16).build());

        final AddLabRoomReservationRequest request = AddLabRoomReservationRequest.builder()
                .renterName("대여자")
                .renterEmail("djwhy5510@naver.com")
                .renterPhoneNumber("010-7301-5510")
                .rentalPurpose("필요하니까")
                .labRoomName(asset.getName())
                .startDate(LocalDate.of(2023, 5, 15))
                .endDate(LocalDate.of(2023, 5, 16))
                .renterCount(10)
                .build();

        // when
        RestAssured.given(requestSpec)
                .filter(document("addLabRoomReservations"))
                .body(request).contentType(ContentType.JSON)
                .sessionId(sessionId)
                .when().log().all().post("/api/reservations/labRooms")
                .then().log().all().statusCode(HttpStatus.CREATED.value())
                .header(HttpHeaders.LOCATION, containsString("/api/reservations/"));
    }

    @Test
    @DisplayName("특정 기자재에 대여 예약된 이력을 조회한다.")
    void getReservationsByEquipment() {
        // given
        final Rentable asset = assetRepository.save(EquipmentFixture.create());
        final Item item = itemRepository.save(ItemFixture.builder().assetId(asset.getId()).build());
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(asset).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(asset).period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))).build();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.create(List.of(reservationSpec1)));
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.create(List.of(reservationSpec2)));

        // when
        final ReservationsByEquipmentPerYearMonthResponse response = RestAssured.given(requestSpec)
                .filter(document("admin_getReservationByEquipment"))
                .when().log().all().get("/api/admin/reservations?equipmentId={id}&yearMonth={yearMonth}", asset.getId(), YearMonth.now().toString())
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

        final Rentable asset1 = assetRepository.save(EquipmentFixture.builder().name("name1").build());
        final Rentable asset2 = assetRepository.save(EquipmentFixture.builder().name("name2").build());
        final Item item1 = itemRepository.save(ItemFixture.builder().assetId(asset1.getId()).propertyNumber("11111111").build());
        final Item item2 = itemRepository.save(ItemFixture.builder().assetId(asset2.getId()).propertyNumber("22222222").build());
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(asset1).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(asset2).period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))).build();
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

    @Test
    @DisplayName("대여 예약 상세를 취소한다.")
    void cancelReservationSpec() {
        // given
        final Long memberId = 1L;
        final Rentable asset = assetRepository.save(EquipmentFixture.builder().name("name1").build());
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(asset).amount(RentalAmount.ofPositive(2)).build();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1)).memberId(memberId).build());

        final CancelReservationSpecRequest requestBody = new CancelReservationSpecRequest(2);

        // when, then
        RestAssured.given(requestSpec)
                .filter(document("admin_cancelReservationSpec"))
                .contentType(ContentType.JSON).body(requestBody)
                .when().log().all().patch("/api/admin/reservations/specs/{id}", reservationSpec1.getId())
                .then().log().all().statusCode(HttpStatus.NO_CONTENT.value())
                .header(HttpHeaders.LOCATION, containsString("/api/reservations/specs/" + reservationSpec1.getId()));
    }

    @Test
    @DisplayName("특절 날짜에 수령일인 랩실 대여 예약을 조회한다.")
    void getReservationsByStartDate() {
        // given
        final Rentable labRoom1 = assetRepository.save(LabRoomFixture.builder().name("test1").build());
        final Rentable labRoom2 = assetRepository.save(LabRoomFixture.builder().name("test2").build());
        final Member member = memberRepository.save(MemberFixture.create());

        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(labRoom1).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).status(ReservationSpecStatus.RENTED).build();
        final RentalDateTime acceptDateTime = RentalDateTime.now();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1)).memberId(member.getId()).acceptDateTime(acceptDateTime).build());

        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(labRoom1).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).status(ReservationSpecStatus.RENTED).build();
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec2)).memberId(member.getId()).acceptDateTime(acceptDateTime).build());

        final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(labRoom2).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();
        final Reservation reservation3 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec3)).memberId(member.getId()).build());

        // when
        final LabRoomReservationsWithMemberNumberResponse response = RestAssured.given(requestSpec)
                .filter(document("admin_getLabRoomReservationsWhenAccept"))
                .when().log().all().get("/api/admin/reservations/labRooms?startDate={startDate}", LocalDate.now().toString())
                .then().log().all().statusCode(HttpStatus.OK.value())
                .extract().as(LabRoomReservationsWithMemberNumberResponse.class);

        // then
        assertThat(response.getReservations()).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(
                        new LabRoomReservationWithMemberNumberResponse(labRoom1.getName(), reservation1.getAcceptDateTime(), List.of(
                                new LabRoomReservationSpecWithMemberNumberResponse(reservationSpec1.getId(), reservation1.getName(), member.getMemberNumber(), reservationSpec1.getAmount().getAmount(), reservation1.getPhoneNumber()),
                                new LabRoomReservationSpecWithMemberNumberResponse(reservationSpec2.getId(), reservation2.getName(), member.getMemberNumber(), reservationSpec2.getAmount().getAmount(), reservation2.getPhoneNumber())
                        )),
                        new LabRoomReservationWithMemberNumberResponse(labRoom2.getName(), reservation3.getAcceptDateTime(), List.of(
                                new LabRoomReservationSpecWithMemberNumberResponse(reservationSpec3.getId(), reservation3.getName(), member.getMemberNumber(), reservationSpec3.getAmount().getAmount(), reservation3.getPhoneNumber())
                        ))
                );
    }

}