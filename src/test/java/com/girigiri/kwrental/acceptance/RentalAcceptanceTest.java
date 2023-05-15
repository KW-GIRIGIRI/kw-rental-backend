package com.girigiri.kwrental.acceptance;

import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.inventory.domain.RentalDateTime;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.rental.dto.request.CreateRentalRequest;
import com.girigiri.kwrental.rental.dto.request.RentalSpecsRequest;
import com.girigiri.kwrental.rental.dto.request.ReturnRentalRequest;
import com.girigiri.kwrental.rental.dto.request.ReturnRentalSpecRequest;
import com.girigiri.kwrental.rental.dto.response.RentalSpecByItemResponse;
import com.girigiri.kwrental.rental.dto.response.RentalSpecsByItemResponse;
import com.girigiri.kwrental.rental.dto.response.RentalsDto;
import com.girigiri.kwrental.rental.dto.response.ReservationsWithRentalSpecsByEndDateResponse;
import com.girigiri.kwrental.rental.dto.response.overduereservations.OverdueReservationResponse;
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.ReservationWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.ReservationsWithRentalSpecsAndMemberNumberResponse;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.rental.repository.dto.RentalDto;
import com.girigiri.kwrental.rental.repository.dto.RentalSpecDto;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.dto.ReservationWithMemberNumber;
import com.girigiri.kwrental.testsupport.fixture.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private MemberRepository memberRepository;

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
        final Member member = memberRepository.save(MemberFixture.create());

        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();
        final RentalDateTime acceptDateTime = RentalDateTime.now();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).memberId(member.getId()).acceptDateTime(acceptDateTime).build());
        final RentalSpec rentalSpec1 = RentalSpecFixture.builder().propertyNumber("11111111").reservationSpecId(reservationSpec1.getId()).build();
        final RentalSpec rentalSpec2 = RentalSpecFixture.builder().propertyNumber("33333333").reservationSpecId(reservationSpec2.getId()).build();
        rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

        final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(2))).build();
        final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(2))).build();
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec3, reservationSpec4)).memberId(member.getId()).build());

        final ReservationSpec reservationSpec5 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))).build();
        final ReservationSpec reservationSpec6 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))).build();
        final Reservation reservation3 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec5, reservationSpec6)).memberId(member.getId()).build());


        // when
        final ReservationsWithRentalSpecsAndMemberNumberResponse response = RestAssured.given(requestSpec)
                .filter(document("admin_getReservationsWithRentalSpecsByStartDate"))
                .when().log().all().get("/api/admin/rentals?startDate={startDate}", LocalDate.now().toString())
                .then().log().all().statusCode(HttpStatus.OK.value())
                .extract().as(ReservationsWithRentalSpecsAndMemberNumberResponse.class);

        // then
        assertThat(response.getReservations()).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(ReservationWithRentalSpecsResponse.of(new ReservationWithMemberNumber(reservation1, member.getMemberNumber()), List.of(rentalSpec1, rentalSpec2)),
                        ReservationWithRentalSpecsResponse.of(new ReservationWithMemberNumber(reservation2, member.getMemberNumber()), Collections.emptyList()));
    }

    @Test
    @DisplayName("특정 날짜에 반납일인 대여 예약과 연체된 대여 예약을 조회한다.")
    void getReservationWithRentalSpecsByEndDate() {
        // given
        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().modelName("test1").build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().modelName("test2").build());
        final Member member = memberRepository.save(MemberFixture.create());

        final RentalDateTime acceptDateTime = RentalDateTime.now();
        final LocalDate now = LocalDate.now();
        final LocalDate yesterday = now.minusDays(1);

        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(yesterday, now)).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(yesterday, now)).build();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).acceptDateTime(acceptDateTime).memberId(member.getId()).build());
        final RentalSpec rentalSpec1 = RentalSpecFixture.builder().propertyNumber("11111111").reservationSpecId(reservationSpec1.getId()).build();
        final RentalSpec rentalSpec2 = RentalSpecFixture.builder().propertyNumber("22222222").reservationSpecId(reservationSpec2.getId()).build();
        rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

        final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(yesterday.minusDays(1), yesterday)).build();
        final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(yesterday.minusDays(1), yesterday)).build();
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec3, reservationSpec4)).memberId(member.getId()).acceptDateTime(acceptDateTime).build());
        final RentalSpec rentalSpec3 = RentalSpecFixture.builder().propertyNumber("33333333").reservationSpecId(reservationSpec3.getId()).build();
        final RentalSpec rentalSpec4 = RentalSpecFixture.builder().propertyNumber("44444444").reservationSpecId(reservationSpec4.getId()).returnDateTime(RentalDateTime.now()).build();
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
                        .containsExactlyInAnyOrder(OverdueReservationResponse.of(new ReservationWithMemberNumber(reservation2, member.getMemberNumber()), List.of(rentalSpec3))),
                () -> assertThat(response.getReservationsByEndDate().getReservations()).usingRecursiveFieldByFieldElementComparator()
                        .containsExactlyInAnyOrder(ReservationWithRentalSpecsResponse.of(new ReservationWithMemberNumber(reservation1, member.getMemberNumber()), List.of(rentalSpec1, rentalSpec2)))
        );
    }

    @Test
    @DisplayName("반납한다.")
    void returnRentals() {
        // given
        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().modelName("test1").build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().modelName("test2").build());

        final RentalDateTime acceptDateTime = RentalDateTime.now();
        final LocalDate now = LocalDate.now();
        final LocalDate yesterday = now.minusDays(1);

        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(yesterday, now)).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(yesterday, now)).build();
        final Reservation reservation = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).acceptDateTime(acceptDateTime).build());
        final RentalSpec rentalSpec1 = RentalSpecFixture.builder().propertyNumber("11111111").reservationSpecId(reservationSpec1.getId()).reservationId(reservation.getId()).build();
        final RentalSpec rentalSpec2 = RentalSpecFixture.builder().propertyNumber("22222222").reservationSpecId(reservationSpec2.getId()).reservationId(reservation.getId()).build();
        rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

        final ReturnRentalSpecRequest returnRentalSpecRequest1 = ReturnRentalSpecRequest.builder()
                .id(rentalSpec1.getId())
                .status(RentalSpecStatus.RETURNED)
                .build();
        final ReturnRentalSpecRequest returnRentalSpecRequest2 = ReturnRentalSpecRequest.builder()
                .id(rentalSpec2.getId())
                .status(RentalSpecStatus.RETURNED)
                .build();
        final ReturnRentalRequest returnRentalRequest = ReturnRentalRequest.builder()
                .reservationId(reservation.getId())
                .rentalSpecs(List.of(returnRentalSpecRequest1, returnRentalSpecRequest2))
                .build();

        // when, then
        RestAssured.given(requestSpec)
                .filter(document("admin_returnRentals"))
                .body(returnRentalRequest)
                .contentType(ContentType.JSON)
                .when().log().all().patch("/api/admin/rentals/returns")
                .then().log().all().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("사용자의 대여 이력을 조회한다.")
    void getRentals() {
        // given
        final String password = "12345678";
        final Member member = memberRepository.save(MemberFixture.create(password));
        final String sessionId = getSessionId(member.getMemberNumber(), password);

        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().modelName("test1").build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().modelName("test2").build());

        final LocalDate now = LocalDate.now();
        final LocalDate yesterday = now.minusDays(1);

        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(yesterday, now)).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(yesterday, now)).build();
        final Reservation reservation = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).memberId(member.getId()).build());
        final RentalSpec rentalSpec1 = RentalSpecFixture.builder().propertyNumber("11111111").reservationSpecId(reservationSpec1.getId()).reservationId(reservation.getId()).status(RentalSpecStatus.RETURNED).build();
        final RentalSpec rentalSpec2 = RentalSpecFixture.builder().propertyNumber("22222222").reservationSpecId(reservationSpec2.getId()).reservationId(reservation.getId()).status(RentalSpecStatus.RETURNED).build();
        rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

        // when
        final RentalsDto response = RestAssured.given(requestSpec)
                .filter(document("getRentals"))
                .sessionId(sessionId)
                .when().log().all().get("/api/rentals?from={from}&to={}", yesterday.toString(), now.toString())
                .then().log().all().statusCode(HttpStatus.OK.value())
                .extract().as(RentalsDto.class);

        // then
        assertThat(response.getRentals()).usingRecursiveFieldByFieldElementComparator()
                .containsExactly(new RentalDto(reservation.getStartDate(), reservation.getEndDate(),
                        Set.of(new RentalSpecDto(equipment1.getModelName(), rentalSpec1.getStatus()), new RentalSpecDto(equipment2.getModelName(), rentalSpec2.getStatus())))
                );
    }

    @Test
    @DisplayName("특정 자산번호의 반납된 사용이력을 조회한다.")
    void getReturnsByPropertyNumber() {
        // given
        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());

        final ReservationSpec reservationSpec1 = ReservationSpecFixture.create(equipment);
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1)).terminated(true).build());
        final RentalDateTime now = RentalDateTime.now();
        final RentalSpec rentalSpec1 = RentalSpecFixture.builder().reservationId(reservation1.getId()).propertyNumber("11111111").acceptDateTime(now).returnDateTime(now.calculateDay(1)).status(RentalSpecStatus.RETURNED).build();

        final ReservationSpec reservationSpec2 = ReservationSpecFixture.create(equipment);
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec2)).terminated(true).build());
        final RentalSpec rentalSpec2 = RentalSpecFixture.builder().reservationId(reservation1.getId()).propertyNumber("11111111").acceptDateTime(now.calculateDay(2)).returnDateTime(now.calculateDay(3)).status(RentalSpecStatus.LOST).build();

        rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

        // when
        final RentalSpecsByItemResponse response = RestAssured.given(requestSpec)
                .filter(document("admin_getReturnedRentalSpecsByPropertyNumber"))
                .when().log().all().get("/api/admin/rentals/returns?propertyNumber={propertyNumber}", rentalSpec1.getPropertyNumber())
                .then().log().all().statusCode(HttpStatus.OK.value())
                .extract().as(RentalSpecsByItemResponse.class);

        // then
        assertThat(response.getRentalSpecs()).usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                        new RentalSpecByItemResponse("불량 반납", rentalSpec2.getAcceptDateTime().toLocalDate(), rentalSpec2.getReturnDateTime().toLocalDate(), reservation2.getName(), rentalSpec2.getStatus().name())
                        , new RentalSpecByItemResponse("정상 반납", rentalSpec1.getAcceptDateTime().toLocalDate(), rentalSpec1.getReturnDateTime().toLocalDate(), reservation1.getName(), rentalSpec1.getStatus().name())
                );
    }
}
