package com.girigiri.kwrental.penalty.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.asset.repository.AssetRepository;
import com.girigiri.kwrental.config.JpaConfig;
import com.girigiri.kwrental.inventory.domain.RentalDateTime;
import com.girigiri.kwrental.penalty.domain.Penalty;
import com.girigiri.kwrental.penalty.domain.PenaltyPeriod;
import com.girigiri.kwrental.penalty.domain.PenaltyReason;
import com.girigiri.kwrental.penalty.dto.response.PenaltyHistoryResponse;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltiesResponse;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltyResponse;
import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.domain.LabRoomRentalSpec;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.EquipmentRentalSpecFixture;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;
import com.girigiri.kwrental.testsupport.fixture.LabRoomRentalSpecFixture;
import com.girigiri.kwrental.testsupport.fixture.PenaltyFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@DataJpaTest
@Import(JpaConfig.class)
class PenaltyRepositoryTest {

	@Autowired
	private PenaltyRepository penaltyRepository;
	@Autowired
	private AssetRepository assetRepository;
	@Autowired
	private ReservationRepository reservationRepository;
	@Autowired
	private RentalSpecRepository rentalSpecRepository;

	@Test
	@DisplayName("특정 회원의 현재 진행 중인 페널티를 조회한다.")
	void findOngoingPenalty() {
		// given
		final LocalDate now = LocalDate.now();
		final PenaltyPeriod penaltyPeriod1 = new PenaltyPeriod(now, now.plusDays(3));
		final Penalty penalty1 = penaltyRepository.save(
			PenaltyFixture.builder(PenaltyReason.BROKEN).memberId(1L).period(penaltyPeriod1).build());
		final PenaltyPeriod penaltyPeriod2 = new PenaltyPeriod(now.minusDays(2), now.minusDays(1));
		final Penalty penalty2 = penaltyRepository.save(
			PenaltyFixture.builder(PenaltyReason.BROKEN).memberId(1L).period(penaltyPeriod2).build());
		final PenaltyPeriod penaltyPeriod3 = new PenaltyPeriod(now.minusDays(2), now);
		final Penalty penalty3 = penaltyRepository.save(
			PenaltyFixture.builder(PenaltyReason.BROKEN).memberId(1L).period(penaltyPeriod3).build());
		final Penalty penalty4 = penaltyRepository.save(
			PenaltyFixture.builder(PenaltyReason.BROKEN).memberId(2L).period(penaltyPeriod1).build());

		// when
		final List<Penalty> actual = penaltyRepository.findByOngoingPenalties(1L);

		// then
		assertThat(actual).containsExactlyInAnyOrder(penalty1, penalty3);
	}

	@Test
	@DisplayName("특정 회원의 페널티 이력을 조회한다.")
	void findUserPenaltiesResponseByMemberId() {
		// given
		final Rentable equipment = assetRepository.save(EquipmentFixture.create());
		final Rentable labRoom = assetRepository.save(LabRoomFixture.create());

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.create(equipment);
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.create(List.of(reservationSpec1)));
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.create(labRoom);
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.create(List.of(reservationSpec2)));

		final EquipmentRentalSpec equipmentRentalSpec = EquipmentRentalSpecFixture.builder()
			.reservationId(reservation1.getId())
			.reservationSpecId(reservationSpec1.getId())
			.returnDateTime(RentalDateTime.now())
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
			.rentalSpecId(equipmentRentalSpec.getId())
			.reservationSpecId(reservationSpec1.getId())
			.period(PenaltyPeriod.fromPenaltyCount(0))
			.build());
		final Penalty penalty2 = penaltyRepository.save(PenaltyFixture.builder(PenaltyReason.LOST)
			.memberId(0L)
			.reservationId(reservation2.getId())
			.rentalSpecId(labRoomRentalSpec.getId())
			.reservationSpecId(reservationSpec2.getId())
			.period(PenaltyPeriod.fromPenaltyCount(1))
			.build());

		// when
		final UserPenaltiesResponse actual = penaltyRepository.findUserPenaltiesResponseByMemberId(memberId);

		// then
		assertThat(actual.getPenalties()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(
				new UserPenaltyResponse(penalty1.getId(), equipmentRentalSpec.getAcceptDateTime().toLocalDate(),
					equipmentRentalSpec.getReturnDateTime().toLocalDate(), penalty1.getStatusMessage(),
					equipment.getName(), penalty1.getReason())
			);
	}

	@Test
	@DisplayName("페널티 히스토리를 조회한다.")
	void findPenaltyHistoryPageResponse() {
		// given
		final Rentable equipment = assetRepository.save(EquipmentFixture.create());
		final Rentable labRoom = assetRepository.save(LabRoomFixture.create());

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
		final Page<PenaltyHistoryResponse> actual = penaltyRepository.findPenaltyHistoryPageResponse(
			PageRequest.of(0, 1));

		// then
		assertThat(actual.getContent()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(
				new PenaltyHistoryResponse(penalty1.getId(), reservation1.getName(), penalty1.getPeriod(),
					equipment.getName(), penalty1.getReason()));
		assertThat(actual.getTotalElements()).isEqualTo(2);
	}
}
