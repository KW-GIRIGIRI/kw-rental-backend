package com.girigiri.kwrental.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.girigiri.kwrental.asset.domain.RentableAsset;
import com.girigiri.kwrental.asset.repository.AssetRepository;
import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.penalty.domain.Penalty;
import com.girigiri.kwrental.penalty.domain.PenaltyPeriod;
import com.girigiri.kwrental.penalty.domain.PenaltyReason;
import com.girigiri.kwrental.penalty.domain.PenaltyStatus;
import com.girigiri.kwrental.penalty.dto.request.UpdatePeriodRequest;
import com.girigiri.kwrental.penalty.dto.response.PenaltyHistoryPageResponse;
import com.girigiri.kwrental.penalty.dto.response.PenaltyHistoryPageResponse.PenaltyHistoryResponse;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltiesResponse;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltiesResponse.UserPenaltyResponse;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltyStatusResponse;
import com.girigiri.kwrental.penalty.repository.PenaltyRepository;
import com.girigiri.kwrental.rental.domain.entity.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.domain.entity.LabRoomRentalSpec;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.EquipmentRentalSpecFixture;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;
import com.girigiri.kwrental.testsupport.fixture.LabRoomRentalSpecFixture;
import com.girigiri.kwrental.testsupport.fixture.MemberFixture;
import com.girigiri.kwrental.testsupport.fixture.PenaltyFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

class PenaltyAcceptanceTest extends AcceptanceTest {

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private AssetRepository assetRepository;
	@Autowired
	private ReservationRepository reservationRepository;
	@Autowired
	private RentalSpecRepository rentalSpecRepository;
	@Autowired
	private PenaltyRepository penaltyRepository;

	@Test
	@DisplayName("특정 사용자의 페널티 이력을 조회한다.")
	void penaltiesByMember() {
		// given
		final String password = "12345678";
		final Member member = memberRepository.save(MemberFixture.create(password));
		final String sessionId = getSessionId(member.getMemberNumber(), password);

		final RentableAsset equipment = assetRepository.save(EquipmentFixture.create());
		final RentableAsset labRoom = assetRepository.save(LabRoomFixture.create());
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.create(equipment);
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.create(List.of(reservationSpec1)));
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.create(labRoom);
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.create(List.of(reservationSpec2)));

		final EquipmentRentalSpec equipmentRentalSpec = EquipmentRentalSpecFixture.builder()
			.reservationId(reservation1.getId())
			.reservationSpecId(reservationSpec1.getId())
			.acceptDateTime(RentalDateTime.now().calculateDay(-1))
			.returnDateTime(RentalDateTime.now())
			.build();
		final LabRoomRentalSpec labRoomRentalSpec = LabRoomRentalSpecFixture.builder()
			.reservationId(reservation2.getId())
			.reservationSpecId(reservationSpec2.getId())
			.acceptDateTime(RentalDateTime.now().calculateDay(-2))
			.returnDateTime(RentalDateTime.now())
			.build();
		rentalSpecRepository.saveAll(List.of(equipmentRentalSpec, labRoomRentalSpec));

		final Long memberId = member.getId();
		final Penalty penalty1 = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.BROKEN)
			.memberId(memberId)
			.reservationId(reservation1.getId())
			.rentalSpecId(equipmentRentalSpec.getId())
			.reservationSpecId(reservationSpec1.getId())
			.period(PenaltyPeriod.fromPenaltyCount(0))
			.build());
		final Penalty penalty2 = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.LOST)
			.memberId(memberId)
			.reservationId(reservation2.getId())
			.rentalSpecId(labRoomRentalSpec.getId())
			.reservationSpecId(reservationSpec2.getId())
			.period(PenaltyPeriod.fromPenaltyCount(1))
			.build());

		// when
		final UserPenaltiesResponse response = RestAssured.given(requestSpec)
			.sessionId(sessionId)
			.filter(document("getPenaltyByMember"))
			.when().log().all().get("/api/penalties")
			.then().log().all()
			.statusCode(HttpStatus.OK.value())
			.extract().as(UserPenaltiesResponse.class);

		// then
		assertThat(response.penalties()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(
				new UserPenaltyResponse(penalty1.getId(), RentalDateTime.now().calculateDay(-1).toLocalDate(),
					RentalDateTime.now().toLocalDate(), penalty1.getStatusMessage(), equipment.getName(),
					penalty1.getReason()),
				new UserPenaltyResponse(penalty2.getId(), RentalDateTime.now().calculateDay(-2).toLocalDate(),
					RentalDateTime.now().toLocalDate(), penalty2.getStatusMessage(), labRoom.getName(),
					penalty2.getReason())
			);
	}

	@Test
	@DisplayName("회원의 페널티 상태를 조회한다.")
	void getUserPenaltyStatus() {
		// given
		final String password = "12345678";
		final Member member = memberRepository.save(MemberFixture.create(password));
		final String sessionId = getSessionId(member.getMemberNumber(), password);

		final RentableAsset equipment = assetRepository.save(EquipmentFixture.create());
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.create(equipment);
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.create(List.of(reservationSpec1)));

		final EquipmentRentalSpec equipmentRentalSpec = EquipmentRentalSpecFixture.builder()
			.reservationId(reservation1.getId())
			.reservationSpecId(reservationSpec1.getId())
			.build();
		rentalSpecRepository.saveAll(List.of(equipmentRentalSpec));

		final Long memberId = member.getId();
		final Penalty penalty1 = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.BROKEN)
			.memberId(memberId)
			.reservationId(reservation1.getId())
			.rentalSpecId(reservationSpec1.getId())
			.reservationSpecId(reservationSpec1.getId())
			.period(PenaltyPeriod.fromPenaltyCount(0))
			.build());

		// when
		final UserPenaltyStatusResponse response = RestAssured.given(requestSpec)
			.sessionId(sessionId)
			.filter(document("getUserPenaltyStatus"))
			.when().log().all().get("/api/penalties/status")
			.then().log().all()
			.statusCode(HttpStatus.OK.value())
			.extract().as(UserPenaltyStatusResponse.class);

		// then
		assertThat(response).usingRecursiveComparison()
			.isEqualTo(new UserPenaltyStatusResponse(false, penalty1.getPeriod().getStatus(),
				penalty1.getPeriod().getEndDate()));
	}

	@Test
	@DisplayName("패널티 히스토리를 조회한다.")
	void getPenaltyHistoryPage() {
		// given
		final RentableAsset equipment = assetRepository.save(EquipmentFixture.create());
		final RentableAsset labRoom = assetRepository.save(LabRoomFixture.create());

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.create(equipment);
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.create(List.of(reservationSpec1)));
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.create(labRoom);
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.create(List.of(reservationSpec2)));

		final EquipmentRentalSpec equipmentRentalSpec = EquipmentRentalSpecFixture.builder()
			.reservationId(reservation1.getId())
			.reservationSpecId(reservationSpec1.getId())
			.build();
		final LabRoomRentalSpec labRoomRentalSpec = LabRoomRentalSpecFixture.builder()
			.reservationId(reservation2.getId())
			.reservationSpecId(reservationSpec2.getId())
			.build();
		rentalSpecRepository.saveAll(List.of(equipmentRentalSpec, labRoomRentalSpec));

		final Long memberId = 1L;
		final Penalty penalty1 = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.BROKEN)
			.memberId(memberId)
			.reservationId(reservation1.getId())
			.rentalSpecId(reservationSpec1.getId())
			.reservationSpecId(reservationSpec1.getId())
			.period(PenaltyPeriod.fromPenaltyCount(0))
			.build());
		final Penalty penalty2 = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.LOST)
			.memberId(0L)
			.reservationId(reservation2.getId())
			.rentalSpecId(reservationSpec2.getId())
			.reservationSpecId(reservationSpec2.getId())
			.period(PenaltyPeriod.fromPenaltyCount(1))
			.build());

		// when
		final PenaltyHistoryPageResponse response = RestAssured.given(requestSpec)
			.filter(document("getPenaltyHistoryPage"))
			.when().log().all().get("/api/admin/penalties/histories?size={size}&page={page}", 1, 0)
			.then().log().all()
			.statusCode(HttpStatus.OK.value())
			.extract().as(PenaltyHistoryPageResponse.class);

		// then
		assertThat(response.penalties()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(new PenaltyHistoryResponse(penalty2.getId(), reservation2.getName(), penalty2.getPeriod(),
				labRoom.getName(), penalty2.getReason()));
		assertThat(response.endPoints()).hasSize(2);
	}

	@Test
	@DisplayName("패널티의 기간을 수정한다.")
	void updatePenaltyPeriod() {
		// given
		final RentableAsset equipment = assetRepository.save(EquipmentFixture.create());

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.create(equipment);
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.create(List.of(reservationSpec1)));

		final EquipmentRentalSpec equipmentRentalSpec = EquipmentRentalSpecFixture.builder()
			.reservationId(reservation1.getId())
			.reservationSpecId(reservationSpec1.getId())
			.build();
		rentalSpecRepository.saveAll(List.of(equipmentRentalSpec));

		final Long memberId = 1L;
		final Penalty penalty1 = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.BROKEN)
			.memberId(memberId)
			.reservationId(reservation1.getId())
			.rentalSpecId(reservationSpec1.getId())
			.reservationSpecId(reservationSpec1.getId())
			.period(PenaltyPeriod.fromPenaltyCount(0))
			.build());

		final UpdatePeriodRequest requestBody = new UpdatePeriodRequest(PenaltyStatus.ONE_YEAR);

		// when
		RestAssured.given(requestSpec)
			.filter(document("updatePenaltyPeriod"))
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when().log().all().patch("/api/admin/penalties/{id}", penalty1.getId())
			.then().log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());

		// then
		final Penalty actual = penaltyRepository.findById(penalty1.getId()).orElseThrow();
		assertThat(actual.getPeriod().getStatus()).isEqualTo(PenaltyStatus.ONE_YEAR);
	}

	@Test
	@DisplayName("패널티를 삭제한다..")
	void deletePenalty() {
		// given
		final RentableAsset equipment = assetRepository.save(EquipmentFixture.create());

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.create(equipment);
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.create(List.of(reservationSpec1)));

		final EquipmentRentalSpec equipmentRentalSpec = EquipmentRentalSpecFixture.builder()
			.reservationId(reservation1.getId())
			.reservationSpecId(reservationSpec1.getId())
			.build();
		rentalSpecRepository.saveAll(List.of(equipmentRentalSpec));

		final Long memberId = 1L;
		final Penalty penalty1 = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.BROKEN)
			.memberId(memberId)
			.reservationId(reservation1.getId())
			.rentalSpecId(reservationSpec1.getId())
			.reservationSpecId(reservationSpec1.getId())
			.period(PenaltyPeriod.fromPenaltyCount(0))
			.build());

		// when
		RestAssured.given(requestSpec)
			.filter(document("deletePenalty"))
			.when().log().all().delete("/api/admin/penalties/{id}", penalty1.getId())
			.then().log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());

		// then
		final Optional<Penalty> actual = penaltyRepository.findById(penalty1.getId());
		assertThat(actual).isEmpty();
	}
}
