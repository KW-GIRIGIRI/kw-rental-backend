package com.girigiri.kwrental.acceptance;

import static com.girigiri.kwrental.rental.dto.response.LabRoomRentalsDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.asset.repository.AssetRepository;
import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.penalty.domain.Penalty;
import com.girigiri.kwrental.penalty.domain.PenaltyPeriod;
import com.girigiri.kwrental.penalty.domain.PenaltyReason;
import com.girigiri.kwrental.penalty.repository.PenaltyRepository;
import com.girigiri.kwrental.rental.domain.AbstractRentalSpec;
import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.domain.LabRoomRentalSpec;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.rental.dto.request.CreateEquipmentRentalRequest;
import com.girigiri.kwrental.rental.dto.request.RentalSpecsRequest;
import com.girigiri.kwrental.rental.dto.request.ReturnRentalRequest;
import com.girigiri.kwrental.rental.dto.request.ReturnRentalSpecRequest;
import com.girigiri.kwrental.rental.dto.request.UpdateLabRoomRentalSpecStatusRequest;
import com.girigiri.kwrental.rental.dto.request.UpdateLabRoomRentalSpecStatusesRequest;
import com.girigiri.kwrental.rental.dto.response.EquipmentRentalSpecResponse;
import com.girigiri.kwrental.rental.dto.response.EquipmentRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.EquipmentRentalsDto;
import com.girigiri.kwrental.rental.dto.response.EquipmentRentalsDto.EquipmentRentalDto;
import com.girigiri.kwrental.rental.dto.response.EquipmentRentalsDto.EquipmentRentalDto.EquipmentRentalSpecDto;
import com.girigiri.kwrental.rental.dto.response.LabRoomRentalsDto;
import com.girigiri.kwrental.rental.dto.response.LabRoomReservationPageResponse;
import com.girigiri.kwrental.rental.dto.response.LabRoomReservationResponse;
import com.girigiri.kwrental.rental.dto.response.LabRoomReservationsResponse;
import com.girigiri.kwrental.rental.dto.response.ReservationsWithRentalSpecsByEndDateResponse;
import com.girigiri.kwrental.rental.dto.response.overduereservations.OverdueReservationResponse;
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.EquipmentReservationWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs.EquipmentReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.dto.request.CreateLabRoomRentalRequest;
import com.girigiri.kwrental.reservation.dto.request.ReturnLabRoomRequest;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.EquipmentRentalSpecFixture;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;
import com.girigiri.kwrental.testsupport.fixture.LabRoomRentalSpecFixture;
import com.girigiri.kwrental.testsupport.fixture.MemberFixture;
import com.girigiri.kwrental.testsupport.fixture.PenaltyFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

class RentalAcceptanceTest extends AcceptanceTest {

	@Autowired
	private AssetRepository assetRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private RentalSpecRepository rentalSpecRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PenaltyRepository penaltyRepository;

	@Test
	@DisplayName("대여를 수령한다.")
	void createRental() {
		// given
		final Rentable equipment = assetRepository.save(EquipmentFixture.create());
		final Item item = itemRepository.save(ItemFixture.builder().assetId(equipment.getId()).build());
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment)
			.period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1)))
			.build();
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.create(List.of(reservationSpec1)));

		CreateEquipmentRentalRequest request = new CreateEquipmentRentalRequest(
			reservation1.getId(),
			List.of(
				new RentalSpecsRequest(reservation1.getReservationSpecs().get(0).getId(),
					List.of(item.getPropertyNumber()))
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

		// then
		final Reservation actualReservation = reservationRepository.findByIdWithSpecs(reservation1.getId())
			.orElseThrow();
		final ReservationSpec actualSpec = actualReservation.getReservationSpecs().get(0);
		assertThat(actualReservation.getAcceptDateTime()).isNotNull();
		assertThat(actualSpec.getStatus()).isEqualTo(ReservationSpecStatus.RENTED);
	}

	@Test
	@DisplayName("특절 날짜에 수령일인 대여 예약을 조회한다.")
	void getReservationsByStartDate() {
		// given
		final Rentable equipment1 = assetRepository.save(EquipmentFixture.builder().name("test1").build());
		final Rentable equipment2 = assetRepository.save(EquipmentFixture.builder().name("test2").build());
		final Member member = memberRepository.save(MemberFixture.create());

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1)
			.period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1)))
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1)))
			.build();
		final RentalDateTime acceptDateTime = RentalDateTime.now();
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2))
				.memberId(member.getId())
				.acceptDateTime(acceptDateTime)
				.build());
		final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder()
			.propertyNumber("11111111")
			.reservationSpecId(reservationSpec1.getId())
			.build();
		final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder()
			.propertyNumber("33333333")
			.reservationSpecId(reservationSpec2.getId())
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

		final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment1)
			.period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(2)))
			.build();
		final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(2)))
			.build();
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec3, reservationSpec4)).memberId(member.getId()).build());

		final ReservationSpec reservationSpec5 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)))
			.build();
		final ReservationSpec reservationSpec6 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)))
			.build();
		final Reservation reservation3 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec5, reservationSpec6)).memberId(member.getId()).build());

		// when
		final EquipmentReservationsWithRentalSpecsResponse response = RestAssured.given(requestSpec)
			.filter(document("admin_getReservationsWithRentalSpecsByStartDate"))
			.when().log().all().get("/api/admin/rentals?startDate={startDate}", LocalDate.now().toString())
			.then().log().all().statusCode(HttpStatus.OK.value())
			.extract().as(EquipmentReservationsWithRentalSpecsResponse.class);

		// then
		assertThat(response.getReservations()).usingRecursiveFieldByFieldElementComparator()
			.containsExactlyInAnyOrder(EquipmentReservationWithRentalSpecsResponse.of(
					new EquipmentReservationWithMemberNumber(reservation1.getId(), reservation1.getName(),
						member.getMemberNumber(), reservation1.getAcceptDateTime(),
						List.of(reservationSpec1, reservationSpec2)), List.of(rentalSpec1, rentalSpec2)),
				EquipmentReservationWithRentalSpecsResponse.of(
					new EquipmentReservationWithMemberNumber(reservation2.getId(), reservation2.getName(),
						member.getMemberNumber(), reservation2.getAcceptDateTime(),
						List.of(reservationSpec3, reservationSpec4)), Collections.emptyList()));
	}

	@Test
	@DisplayName("특정 날짜에 반납일인 대여 예약과 연체된 대여 예약을 조회한다.")
	void getReservationWithRentalSpecsByEndDate() {
		// given
		final Rentable equipment1 = assetRepository.save(EquipmentFixture.builder().name("test1").build());
		final Rentable equipment2 = assetRepository.save(EquipmentFixture.builder().name("test2").build());
		final Member member = memberRepository.save(MemberFixture.create());

		final RentalDateTime acceptDateTime = RentalDateTime.now();
		final LocalDate now = LocalDate.now();
		final LocalDate yesterday = now.minusDays(1);

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1)
			.period(new RentalPeriod(yesterday, now))
			.status(ReservationSpecStatus.RETURNED)
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(yesterday, now))
			.status(ReservationSpecStatus.RENTED)
			.build();
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2))
				.acceptDateTime(acceptDateTime)
				.memberId(member.getId())
				.build());
		final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder()
			.propertyNumber("11111111")
			.reservationSpecId(reservationSpec1.getId())
			.build();
		final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder()
			.propertyNumber("22222222")
			.reservationSpecId(reservationSpec2.getId())
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

		final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment1)
			.period(new RentalPeriod(yesterday.minusDays(1), yesterday))
			.status(ReservationSpecStatus.OVERDUE_RENTED)
			.build();
		final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(yesterday.minusDays(1), yesterday))
			.status(ReservationSpecStatus.ABNORMAL_RETURNED)
			.build();
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec3, reservationSpec4))
				.memberId(member.getId())
				.acceptDateTime(acceptDateTime)
				.build());
		final EquipmentRentalSpec rentalSpec3 = EquipmentRentalSpecFixture.builder()
			.propertyNumber("33333333")
			.reservationSpecId(reservationSpec3.getId())
			.build();
		final EquipmentRentalSpec rentalSpec4 = EquipmentRentalSpecFixture.builder()
			.propertyNumber("44444444")
			.reservationSpecId(reservationSpec4.getId())
			.returnDateTime(RentalDateTime.now())
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec3, rentalSpec4));

		// when
		final ReservationsWithRentalSpecsByEndDateResponse response = RestAssured.given(requestSpec)
			.filter(document("admin_getReservationsWithRentalSpecsByEndDate"))
			.when().log().all().get("/api/admin/rentals?endDate={endDate}", LocalDate.now().toString())
			.then().log().all().statusCode(HttpStatus.OK.value())
			.extract().as(ReservationsWithRentalSpecsByEndDateResponse.class);

		// then
		assertAll(
			() -> assertThat(
				response.getOverdueReservations().getReservations()).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrder(OverdueReservationResponse.of(
					new EquipmentReservationWithMemberNumber(reservation2.getId(), reservation2.getName(),
						member.getMemberNumber(), reservation2.getAcceptDateTime(), List.of(reservationSpec3)),
					List.of(rentalSpec3))),
			() -> assertThat(
				response.getReservationsByEndDate().getReservations()).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrder(EquipmentReservationWithRentalSpecsResponse.of(
					new EquipmentReservationWithMemberNumber(reservation1.getId(), reservation1.getName(),
						member.getMemberNumber(), reservation1.getAcceptDateTime(), List.of(reservationSpec2)),
					List.of(rentalSpec1, rentalSpec2)))
		);
	}

	@Test
	@DisplayName("기자재를 반납한다.")
	void returnRentals() {
		// given
		final Rentable equipment1 = assetRepository.save(EquipmentFixture.builder().name("test1").build());
		final Rentable equipment2 = assetRepository.save(EquipmentFixture.builder().name("test2").build());

		final RentalDateTime acceptDateTime = RentalDateTime.now();
		final LocalDate now = LocalDate.now();
		final LocalDate yesterday = now.minusDays(1);
		itemRepository.save(
			ItemFixture.builder().propertyNumber("22222222").assetId(equipment2.getId()).available(true).build());
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1)
			.period(new RentalPeriod(yesterday, now))
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(yesterday, now))
			.build();
		final Reservation reservation = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2))
				.acceptDateTime(acceptDateTime)
				.build());
		final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder()
			.propertyNumber("11111111")
			.reservationSpecId(reservationSpec1.getId())
			.reservationId(reservation.getId())
			.build();
		final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder()
			.propertyNumber("22222222")
			.reservationSpecId(reservationSpec2.getId())
			.reservationId(reservation.getId())
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

		final ReturnRentalSpecRequest returnRentalSpecRequest1 = ReturnRentalSpecRequest.builder()
			.id(rentalSpec1.getId())
			.status(RentalSpecStatus.RETURNED)
			.build();
		final ReturnRentalSpecRequest returnRentalSpecRequest2 = ReturnRentalSpecRequest.builder()
			.id(rentalSpec2.getId())
			.status(RentalSpecStatus.LOST)
			.build();
		final ReturnRentalRequest returnRentalRequest = ReturnRentalRequest.builder()
			.reservationId(reservation.getId())
			.rentalSpecs(List.of(returnRentalSpecRequest1, returnRentalSpecRequest2))
			.build();

		// when
		RestAssured.given(requestSpec)
			.filter(document("admin_returnRentals"))
			.body(returnRentalRequest)
			.contentType(ContentType.JSON)
			.when().log().all().patch("/api/admin/rentals/returns")
			.then().log().all().statusCode(HttpStatus.NO_CONTENT.value());

		// then
		final List<Penalty> actualPenalties = penaltyRepository.findByOngoingPenalties(reservation.getMemberId());
		final Penalty expect = Penalty.builder().reservationId(reservation.getId())
			.reservationSpecId(reservationSpec2.getId())
			.rentalSpecId(rentalSpec2.getId())
			.memberId(reservation.getMemberId())
			.reason(PenaltyReason.LOST)
			.period(PenaltyPeriod.fromPenaltyCount(0))
			.build();
		assertThat(actualPenalties).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
			.containsExactly(expect);
	}

	@Test
	@DisplayName("사용자의 기자재 대여 이력을 조회한다.")
	void getRentals() {
		// given
		final String password = "12345678";
		final Member member = memberRepository.save(MemberFixture.create(password));
		final String sessionId = getSessionId(member.getMemberNumber(), password);

		final Rentable equipment1 = assetRepository.save(EquipmentFixture.builder().name("test1").build());
		final Rentable equipment2 = assetRepository.save(EquipmentFixture.builder().name("test2").build());

		final LocalDate now = LocalDate.now();
		final LocalDate yesterday = now.minusDays(1);

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1)
			.period(new RentalPeriod(yesterday, now))
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(yesterday, now))
			.build();
		final Reservation reservation = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).memberId(member.getId()).build());
		final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder()
			.propertyNumber("11111111")
			.reservationSpecId(reservationSpec1.getId())
			.reservationId(reservation.getId())
			.status(RentalSpecStatus.RETURNED)
			.build();
		final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder()
			.propertyNumber("22222222")
			.reservationSpecId(reservationSpec2.getId())
			.reservationId(reservation.getId())
			.status(RentalSpecStatus.RETURNED)
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

		// when
		final EquipmentRentalsDto response = RestAssured.given(requestSpec)
			.filter(document("getRentals"))
			.sessionId(sessionId)
			.when().log().all().get("/api/rentals?from={from}&to={}", yesterday.toString(), now.toString())
			.then().log().all().statusCode(HttpStatus.OK.value())
			.extract().as(EquipmentRentalsDto.class);

		// then
		assertThat(response.rentals()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(new EquipmentRentalDto(reservation.getStartDate(), reservation.getEndDate(),
				Set.of(new EquipmentRentalSpecDto(rentalSpec1.getId(), equipment1.getName(), rentalSpec1.getStatus()),
					new EquipmentRentalSpecDto(rentalSpec2.getId(), equipment2.getName(), rentalSpec2.getStatus())))
			);
	}

	@Test
	@DisplayName("특정 자산번호의 반납된 사용이력을 조회한다.")
	void getReturnsByPropertyNumber() {
		// given
		final Rentable equipment = assetRepository.save(EquipmentFixture.create());

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.create(equipment);
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1)).terminated(true).build());
		final RentalDateTime now = RentalDateTime.now();
		final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder()
			.reservationId(reservation1.getId())
			.propertyNumber("11111111")
			.acceptDateTime(now)
			.returnDateTime(now.calculateDay(1))
			.status(RentalSpecStatus.RETURNED)
			.build();

		final ReservationSpec reservationSpec2 = ReservationSpecFixture.create(equipment);
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec2)).terminated(true).build());
		final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder()
			.reservationId(reservation1.getId())
			.propertyNumber("11111111")
			.acceptDateTime(now.calculateDay(2))
			.returnDateTime(now.calculateDay(3))
			.status(RentalSpecStatus.LOST)
			.build();

		rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

		// when
		final EquipmentRentalSpecsResponse response = RestAssured.given(requestSpec)
			.filter(document("admin_getReturnedRentalSpecsByPropertyNumber"))
			.when()
			.log()
			.all()
			.get("/api/admin/rentals/returns?propertyNumber={propertyNumber}", rentalSpec1.getPropertyNumber())
			.then()
			.log()
			.all()
			.statusCode(HttpStatus.OK.value())
			.extract()
			.as(EquipmentRentalSpecsResponse.class);

		// then
		assertThat(response.getRentalSpecs()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(
				new EquipmentRentalSpecResponse("불량 반납", rentalSpec2.getAcceptDateTime().toLocalDate(),
					rentalSpec2.getReturnDateTime().toLocalDate(), reservation2.getName(),
					rentalSpec2.getStatus().name())
				, new EquipmentRentalSpecResponse("정상 반납", rentalSpec1.getAcceptDateTime().toLocalDate(),
					rentalSpec1.getReturnDateTime().toLocalDate(), reservation1.getName(),
					rentalSpec1.getStatus().name())
			);
	}

	@Test
	@DisplayName("특정 자산번호의 반납된 사용이력을 조회한다.")
	void getReturnsByPropertyNumberInclusive() {
		// given
		final Rentable equipment = assetRepository.save(EquipmentFixture.create());

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.create(equipment);
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1)).terminated(true).build());
		final RentalDateTime now = RentalDateTime.now();
		final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder()
			.reservationId(reservation1.getId())
			.propertyNumber("11111111")
			.acceptDateTime(now)
			.returnDateTime(now.calculateDay(1))
			.status(RentalSpecStatus.RETURNED)
			.build();

		final ReservationSpec reservationSpec2 = ReservationSpecFixture.create(equipment);
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec2)).terminated(true).build());
		final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder()
			.reservationId(reservation2.getId())
			.propertyNumber("11111111")
			.acceptDateTime(now.calculateDay(2))
			.returnDateTime(now.calculateDay(3))
			.status(RentalSpecStatus.LOST)
			.build();

		rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

		// when
		final EquipmentRentalSpecsResponse response = RestAssured.given(requestSpec)
			.filter(document("getReturnsByPropertyNumberInclusive"))
			.when()
			.log()
			.all()
			.get("/api/admin/rentals/returns?propertyNumber={propertyNumber}&startDate={startDate}&endDate={endDate}",
				rentalSpec1.getPropertyNumber(), now.toLocalDate().toString(),
				now.calculateDay(3).toLocalDate().toString())
			.then()
			.log()
			.all()
			.statusCode(HttpStatus.OK.value())
			.extract()
			.as(EquipmentRentalSpecsResponse.class);

		// then
		assertThat(response.getRentalSpecs()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(
				new EquipmentRentalSpecResponse("불량 반납", rentalSpec2.getAcceptDateTime().toLocalDate(),
					rentalSpec2.getReturnDateTime().toLocalDate(), reservation2.getName(),
					rentalSpec2.getStatus().name())
				, new EquipmentRentalSpecResponse("정상 반납", rentalSpec1.getAcceptDateTime().toLocalDate(),
					rentalSpec1.getReturnDateTime().toLocalDate(), reservation1.getName(),
					rentalSpec1.getStatus().name())
			);
	}

	@Test
	@DisplayName("랩실 대여 예약을 사용 처리한다.")
	void rentLabRoom() {
		// given
		final Rentable labRoom1 = assetRepository.save(LabRoomFixture.builder().name("hanul").build());
		final Member member = memberRepository.save(MemberFixture.create());

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(labRoom1)
			.period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1)))
			.build();
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1)).memberId(member.getId()).build());

		final CreateLabRoomRentalRequest requestBody = CreateLabRoomRentalRequest.builder()
			.reservationSpecIds(List.of(reservationSpec1.getId()))
			.name(labRoom1.getName())
			.build();

		// when
		RestAssured.given(requestSpec)
			.filter(document("admin_rentLabRoom"))
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when().log().all().post("/api/admin/rentals/labRooms")
			.then().log().all().statusCode(HttpStatus.NO_CONTENT.value());

		// then
		final Reservation actual = reservationRepository.findByIdWithSpecs(reservation1.getId())
			.orElseThrow();
		assertAll(
			() -> assertThat(actual.getReservationSpecs().get(0).getStatus()).isEqualTo(ReservationSpecStatus.RENTED),
			() -> assertThat(actual.getAcceptDateTime()).isNotNull()
		);
	}

	@Test
	@DisplayName("랩실 대여 예약을 퇴실 처리한다.")
	void returnLabRoom() {
		// given
		final Rentable labRoom1 = assetRepository.save(LabRoomFixture.builder().name("hanul").build());
		final Member member = memberRepository.save(MemberFixture.create());

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(labRoom1)
			.period(new RentalPeriod(LocalDate.now().minusDays(1), LocalDate.now()))
			.status(ReservationSpecStatus.RENTED)
			.build();
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1)).memberId(member.getId()).build());
		final LabRoomRentalSpec rentalSpec = LabRoomRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec1.getId())
			.reservationId(reservation1.getId())
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec));

		final ReturnLabRoomRequest requestBody = ReturnLabRoomRequest.builder()
			.reservationSpecIds(List.of(reservationSpec1.getId()))
			.name(labRoom1.getName())
			.build();

		// when
		RestAssured.given(requestSpec)
			.filter(document("admin_returnLabRoom"))
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when().log().all().patch("/api/admin/rentals/labRooms/returns")
			.then().log().all().statusCode(HttpStatus.NO_CONTENT.value());

		// then
		final Reservation actualReservation = reservationRepository.findByIdWithSpecs(reservation1.getId())
			.orElseThrow();
		final AbstractRentalSpec actualRentalSpec = rentalSpecRepository.findById(rentalSpec.getId()).orElseThrow();
		assertAll(
			() -> assertThat(actualReservation.getReservationSpecs().get(0).getStatus()).isEqualTo(
				ReservationSpecStatus.RETURNED),
			() -> assertThat(actualReservation.isTerminated()).isTrue(),
			() -> assertThat(actualRentalSpec.getStatus()).isEqualTo(RentalSpecStatus.RETURNED),
			() -> assertThat(actualRentalSpec.getReturnDateTime()).isNotNull()
		);
	}

	@Test
	@DisplayName("특정 날짜에 완료된 특정 랩실 대여 예약을 조회한다.")
	void getLabRoomReservations() {
		// given
		final Rentable labRoom1 = assetRepository.save(LabRoomFixture.builder().name("hanul").build());
		final Member member = memberRepository.save(MemberFixture.create());

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(labRoom1)
			.period(new RentalPeriod(LocalDate.now().minusDays(1), LocalDate.now()))
			.status(ReservationSpecStatus.RETURNED)
			.build();
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1)).memberId(member.getId()).build());
		final LabRoomRentalSpec rentalSpec = LabRoomRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec1.getId())
			.reservationId(reservation1.getId())
			.status(RentalSpecStatus.RETURNED)
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec));

		// when
		final LabRoomReservationsResponse response = RestAssured.given(requestSpec)
			.filter(document("admin_getLabRoomReservations"))
			.when()
			.log()
			.all()
			.get("/api/admin/rentals/labRooms/{labRoomName}?date={date}", labRoom1.getName(),
				LocalDate.now().minusDays(1).toString())
			.then()
			.log()
			.all()
			.statusCode(HttpStatus.OK.value())
			.extract()
			.as(LabRoomReservationsResponse.class);

		// then
		assertThat(response.getReservations()).usingRecursiveFieldByFieldElementComparator()
			.containsExactlyInAnyOrder(new LabRoomReservationResponse(reservation1.getId(), reservationSpec1.getId(),
				reservationSpec1.getStartDate(), reservationSpec1.getEndDate(), reservation1.getName(),
				rentalSpec.getStatus()));
	}

	@Test
	@DisplayName("사용자의 랩실 대여 이력을 조회한다.")
	void getLabRoomRentals() {
		// given
		final String password = "12345678";
		final Member member = memberRepository.save(MemberFixture.create(password));
		final String sessionId = getSessionId(member.getMemberNumber(), password);

		final Rentable labRoom1 = assetRepository.save(LabRoomFixture.builder().name("hanul").build());
		final Rentable labRoom2 = assetRepository.save(LabRoomFixture.builder().name("saebit").build());

		final LocalDate now = LocalDate.now();
		final LocalDate yesterday = now.minusDays(1);

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(labRoom1)
			.period(new RentalPeriod(yesterday, now))
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(labRoom2)
			.period(new RentalPeriod(yesterday, now))
			.build();
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1)).memberId(member.getId()).build());
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec2)).memberId(member.getId()).build());
		final LabRoomRentalSpec rentalSpec1 = LabRoomRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec1.getId())
			.reservationId(reservation1.getId())
			.status(RentalSpecStatus.RETURNED)
			.build();
		final LabRoomRentalSpec rentalSpec2 = LabRoomRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec2.getId())
			.reservationId(reservation1.getId())
			.status(RentalSpecStatus.RETURNED)
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

		// when
		final LabRoomRentalsDto response = RestAssured.given(requestSpec)
			.filter(document("getLabRoomRentals"))
			.sessionId(sessionId)
			.when().log().all().get("/api/rentals/labRooms?from={from}&to={}", yesterday.toString(), now.toString())
			.then().log().all().statusCode(HttpStatus.OK.value())
			.extract().as(LabRoomRentalsDto.class);

		// then
		assertThat(response.rentals()).usingRecursiveFieldByFieldElementComparator()
			.containsExactlyInAnyOrder(
				new LabRoomRentalDto(reservation1.getStartDate(), reservation1.getEndDate(), labRoom1.getName(),
					reservationSpec1.getAmount().getAmount(), rentalSpec1.getStatus()),
				new LabRoomRentalDto(reservation2.getStartDate(), reservation2.getEndDate(), labRoom2.getName(),
					reservationSpec2.getAmount().getAmount(), rentalSpec2.getStatus())
			);
	}

	@Test
	@DisplayName("랩실 대여 상세의 상태를 변경한다.")
	void updateLabRoomRentalSpecStatuses() {
		// given
		final String password = "12345678";
		final Member member = memberRepository.save(MemberFixture.create(password));
		final String sessionId = getSessionId(member.getMemberNumber(), password);

		final Rentable labRoom1 = assetRepository.save(LabRoomFixture.builder().name("hanul").build());
		final Rentable labRoom2 = assetRepository.save(LabRoomFixture.builder().name("saebit").build());

		final LocalDate now = LocalDate.now();
		final LocalDate yesterday = now.minusDays(1);

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(labRoom1)
			.period(new RentalPeriod(yesterday, now))
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(labRoom2)
			.period(new RentalPeriod(yesterday, now))
			.build();
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1)).memberId(member.getId()).build());
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec2)).memberId(member.getId()).build());
		final LabRoomRentalSpec rentalSpec1 = LabRoomRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec1.getId())
			.reservationId(reservation1.getId())
			.status(RentalSpecStatus.RETURNED)
			.build();
		final LabRoomRentalSpec rentalSpec2 = LabRoomRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec2.getId())
			.reservationId(reservation1.getId())
			.status(RentalSpecStatus.BROKEN)
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

		final Penalty penalty = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.BROKEN)
			.reservationId(reservation2.getId())
			.reservationSpecId(reservationSpec2.getId())
			.rentalSpecId(rentalSpec2.getId())
			.memberId(member.getId())
			.build());

		final UpdateLabRoomRentalSpecStatusesRequest requestBody = new UpdateLabRoomRentalSpecStatusesRequest(List.of(
			new UpdateLabRoomRentalSpecStatusRequest(reservation1.getId(), RentalSpecStatus.LOST),
			new UpdateLabRoomRentalSpecStatusRequest(reservation2.getId(), RentalSpecStatus.RETURNED)
		));
		// when
		RestAssured.given(requestSpec)
			.filter(document("updateLabRoomRentalSpecStatuses"))
			.sessionId(sessionId)
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when().log().all().patch("/api/admin/rentals/labRooms/status")
			.then().log().all().statusCode(HttpStatus.NO_CONTENT.value());

		// then
		final Optional<Penalty> existsActual = penaltyRepository.findByRentalSpecId(rentalSpec1.getId());
		final Optional<Penalty> notExistsActual = penaltyRepository.findById(penalty.getId());
		assertThat(existsActual).isPresent();
		assertThat(notExistsActual).isEmpty();
	}

	@Test
	@DisplayName("랩실 대여 히스토리를 조회한다.")
	void getLabRoomHistory() {
		// given
		final Rentable labRoom1 = assetRepository.save(LabRoomFixture.builder().name("hanul").build());

		final LocalDate now = LocalDate.now();
		final LocalDate yesterday = now.minusDays(1);

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(labRoom1)
			.period(new RentalPeriod(yesterday, now))
			.status(ReservationSpecStatus.RETURNED)
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(labRoom1)
			.period(new RentalPeriod(yesterday, now))
			.status(ReservationSpecStatus.ABNORMAL_RETURNED)
			.build();

		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1)).name("양동주").build());
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec2)).build());

		final LabRoomRentalSpec rentalSpec1 = LabRoomRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec1.getId())
			.reservationId(reservation1.getId())
			.status(RentalSpecStatus.RETURNED)
			.build();
		final LabRoomRentalSpec rentalSpec2 = LabRoomRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec2.getId())
			.reservationId(reservation2.getId())
			.status(RentalSpecStatus.LOST)
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

		//when
		LabRoomReservationPageResponse response = RestAssured.given(requestSpec)
			.filter(document("getLabRoomHistory"))
			.when().log().all()
			.get(
				"/api/admin/rentals/labRooms/{labRoomName}/history?startDate={startDate}&endDate={endDate}&size={size}",
				labRoom1.getName(), yesterday.toString(), now.toString(), 1)
			.then().log().all()
			.extract().as(LabRoomReservationPageResponse.class);

		// then
		assertThat(response.getPage()).isZero();
		assertThat(response.getLabRoomReservations()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(
				new LabRoomReservationResponse(reservation2.getId(), reservationSpec2.getId(),
					reservationSpec2.getStartDate(), reservationSpec2.getEndDate(),
					reservation2.getName(), rentalSpec2.getStatus())
			);
		assertThat(response.getEndPoints()).hasSize(2);
	}
}
