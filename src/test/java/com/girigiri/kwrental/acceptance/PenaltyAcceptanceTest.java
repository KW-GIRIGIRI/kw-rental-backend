package com.girigiri.kwrental.acceptance;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.asset.repository.AssetRepository;
import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.penalty.domain.Penalty;
import com.girigiri.kwrental.penalty.domain.PenaltyPeriod;
import com.girigiri.kwrental.penalty.domain.PenaltyReason;
import com.girigiri.kwrental.penalty.dto.response.*;
import com.girigiri.kwrental.penalty.repository.PenaltyRepository;
import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.domain.LabRoomRentalSpec;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.testsupport.fixture.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

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

        final Rentable equipment = assetRepository.save(EquipmentFixture.create());
        final Rentable labRoom = assetRepository.save(LabRoomFixture.create());
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.create(equipment);
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.create(List.of(reservationSpec1)));
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.create(labRoom);
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.create(List.of(reservationSpec2)));

        final EquipmentRentalSpec equipmentRentalSpec = EquipmentRentalSpecFixture.builder().reservationId(reservation1.getId()).reservationSpecId(reservationSpec1.getId()).build();
        final LabRoomRentalSpec labRoomRentalSpec = LabRoomRentalSpecFixture.builder().reservationId(reservation2.getId()).reservationSpecId(reservationSpec2.getId()).build();
        rentalSpecRepository.saveAll(List.of(equipmentRentalSpec, labRoomRentalSpec));

        final Long memberId = member.getId();
        final Penalty penalty1 = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.BROKEN).memberId(memberId).reservationId(reservation1.getId()).rentalSpecId(reservationSpec1.getId())
                .reservationSpecId(reservationSpec1.getId()).period(PenaltyPeriod.fromPenaltyCount(0)).build());
        final Penalty penalty2 = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.LOST).memberId(memberId).reservationId(reservation2.getId()).rentalSpecId(reservationSpec2.getId())
                .reservationSpecId(reservationSpec2.getId()).period(PenaltyPeriod.fromPenaltyCount(1)).build());

        // when
        final UserPenaltiesResponse response = RestAssured.given(requestSpec)
                .sessionId(sessionId)
                .filter(document("getPenaltyByMember"))
                .when().log().all().get("/api/penalties")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(UserPenaltiesResponse.class);

        // then
        assertThat(response.getPenalties()).usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                        new UserPenaltyResponse(penalty1.getId(), penalty1.getPeriod(), equipment.getName(), penalty1.getReason()),
                        new UserPenaltyResponse(penalty2.getId(), penalty2.getPeriod(), labRoom.getName(), penalty2.getReason())
                );
    }

    @Test
    @DisplayName("회원의 페널티 상태를 조회한다.")
    void getUserPenaltyStatus() {
        // given
        final String password = "12345678";
        final Member member = memberRepository.save(MemberFixture.create(password));
        final String sessionId = getSessionId(member.getMemberNumber(), password);

        final Rentable equipment = assetRepository.save(EquipmentFixture.create());
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.create(equipment);
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.create(List.of(reservationSpec1)));

        final EquipmentRentalSpec equipmentRentalSpec = EquipmentRentalSpecFixture.builder().reservationId(reservation1.getId()).reservationSpecId(reservationSpec1.getId()).build();
        rentalSpecRepository.saveAll(List.of(equipmentRentalSpec));

        final Long memberId = member.getId();
        final Penalty penalty1 = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.BROKEN).memberId(memberId).reservationId(reservation1.getId()).rentalSpecId(reservationSpec1.getId())
                .reservationSpecId(reservationSpec1.getId()).period(PenaltyPeriod.fromPenaltyCount(0)).build());

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
                .isEqualTo(new UserPenaltyStatusResponse(false, penalty1.getPeriod().getEndDate()));
    }

    @Test
    @DisplayName("패널티 히스토리를 조회한다.")
    void getPenaltyHistoryPage() {
        // given
        final Rentable equipment = assetRepository.save(EquipmentFixture.create());
        final Rentable labRoom = assetRepository.save(LabRoomFixture.create());

        final ReservationSpec reservationSpec1 = ReservationSpecFixture.create(equipment);
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.create(List.of(reservationSpec1)));
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.create(labRoom);
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.create(List.of(reservationSpec2)));

        final EquipmentRentalSpec equipmentRentalSpec = EquipmentRentalSpecFixture.builder().reservationId(reservation1.getId()).reservationSpecId(reservationSpec1.getId()).build();
        final LabRoomRentalSpec labRoomRentalSpec = LabRoomRentalSpecFixture.builder().reservationId(reservation2.getId()).reservationSpecId(reservationSpec2.getId()).build();
        rentalSpecRepository.saveAll(List.of(equipmentRentalSpec, labRoomRentalSpec));

        final Long memberId = 1L;
        final Penalty penalty1 = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.BROKEN).memberId(memberId).reservationId(reservation1.getId()).rentalSpecId(reservationSpec1.getId())
                .reservationSpecId(reservationSpec1.getId()).period(PenaltyPeriod.fromPenaltyCount(0)).build());
        final Penalty penalty2 = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.LOST).memberId(0L).reservationId(reservation2.getId()).rentalSpecId(reservationSpec2.getId())
                .reservationSpecId(reservationSpec2.getId()).period(PenaltyPeriod.fromPenaltyCount(1)).build());

        // when
        final PenaltyHistoryPageResponse response = RestAssured.given(requestSpec)
                .filter(document("getPenaltyHistoryPage"))
                .when().log().all().get("/api/admin/penalties/histories?size={size}&page={page}", 1, 0)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(PenaltyHistoryPageResponse.class);

        // then
        assertThat(response.getPenalties()).usingRecursiveFieldByFieldElementComparator()
                .containsExactly(new PenaltyHistoryResponse(penalty2.getId(), reservation2.getName(), penalty2.getPeriod(), labRoom.getName(), penalty2.getReason()));
        assertThat(response.getEndPoints()).hasSize(2);
    }
}
