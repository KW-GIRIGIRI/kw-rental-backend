package com.girigiri.kwrental.reservation.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

import com.girigiri.kwrental.testsupport.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.girigiri.kwrental.asset.domain.RentableAsset;
import com.girigiri.kwrental.asset.repository.AssetRepository;
import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.common.config.JpaConfig;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec.ReservationSpecBuilder;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.domain.entity.ReservedAmount;
import com.girigiri.kwrental.reservation.dto.response.HistoryStatResponse;
import com.girigiri.kwrental.reservation.dto.response.LabRoomReservationsWithMemberNumberResponse.LabRoomReservationWithMemberNumberResponse;
import com.girigiri.kwrental.reservation.dto.response.LabRoomReservationsWithMemberNumberResponse.LabRoomReservationWithMemberNumberResponse.LabRoomReservationSpecWithMemberNumberResponse;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;
import com.girigiri.kwrental.testsupport.fixture.MemberFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RepositoryTest
class ReservationSpecRepositoryTest {

	public static final LocalDate NOW = LocalDate.now();

	@Autowired
	private ReservationSpecRepository reservationSpecRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private AssetRepository assetRepository;

	@Autowired
	private MemberRepository memberRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@DisplayName("특정 기간과 겹치는 대여 사항을 조회한다.")
	void findOverlappedByPeriod() {
		// given
		final RentableAsset equipment = assetRepository.save(EquipmentFixture.create());
		final RentalPeriod notOverlappedLeft = new RentalPeriod(NOW, NOW.plusDays(1));
		final RentalPeriod overlappedLeft = new RentalPeriod(NOW, NOW.plusDays(2));
		final RentalPeriod overlappedMid = new RentalPeriod(NOW.plusDays(2), NOW.plusDays(3));
		final RentalPeriod overlappedRight = new RentalPeriod(NOW.plusDays(3), NOW.plusDays(8));
		final RentalPeriod notOverlappedRight = new RentalPeriod(NOW.plusDays(6), NOW.plusDays(8));
		final RentalPeriod overlappedBoth = new RentalPeriod(NOW, NOW.plusDays(10));

		List<RentalPeriod> periods = List.of(notOverlappedLeft, overlappedLeft, overlappedMid,
			overlappedRight, notOverlappedRight, overlappedBoth);

		periods.forEach(
			it -> reservationSpecRepository.save(ReservationSpecFixture.builder(equipment).period(it).build()));
		periods.forEach(
			it -> reservationSpecRepository.save(
				ReservationSpecFixture.builder(equipment).period(it).status(ReservationSpecStatus.CANCELED).build()));

		// when
		final RentalPeriod period = new RentalPeriod(NOW.plusDays(1), NOW.plusDays(4));
		final List<ReservationSpec> expect = reservationSpecRepository.findOverlappedReservedOrRentedByPeriod(
			equipment.getId(), period);

		// then
		assertThat(expect).usingRecursiveFieldByFieldElementComparator()
			.extracting(ReservationSpec::getPeriod)
			.containsExactlyInAnyOrder(overlappedLeft, overlappedMid, overlappedRight, overlappedBoth);
	}

	@Test
	@DisplayName("주어진 두 날짜값에 겹치는 대여 예약 상세를 조회한다.")
	void findOverlappedReservedOrRentedInclusive() {
		// given
		final RentableAsset equipment = assetRepository.save(EquipmentFixture.create());
		final RentalPeriod notOverlappedLeft = new RentalPeriod(NOW, NOW.plusDays(1));
		final RentalPeriod overlappedLeft = new RentalPeriod(NOW, NOW.plusDays(2));
		final RentalPeriod overlappedMid = new RentalPeriod(NOW.plusDays(2), NOW.plusDays(3));
		final RentalPeriod overlappedRight = new RentalPeriod(NOW.plusDays(4), NOW.plusDays(8));
		final RentalPeriod notOverlappedRight = new RentalPeriod(NOW.plusDays(6), NOW.plusDays(8));
		final RentalPeriod overlappedBoth = new RentalPeriod(NOW.plusDays(1), NOW.plusDays(4));

		List<RentalPeriod> periods = List.of(notOverlappedLeft, overlappedLeft, overlappedMid,
			overlappedRight, notOverlappedRight, overlappedBoth);

		periods.forEach(
			it -> reservationSpecRepository.save(ReservationSpecFixture.builder(equipment).period(it).build()));
		periods.forEach(
			it -> reservationSpecRepository.save(
				ReservationSpecFixture.builder(equipment).period(it).status(ReservationSpecStatus.CANCELED).build()));

		// when
		final List<ReservationSpec> expect = reservationSpecRepository.findOverlappedReservedOrRentedInclusive(
			equipment.getId(), NOW.plusDays(1), NOW.plusDays(4));

		// then
		assertThat(expect).usingRecursiveFieldByFieldElementComparator()
			.extracting(ReservationSpec::getPeriod)
			.containsExactlyInAnyOrder(overlappedLeft, overlappedMid, overlappedRight, overlappedBoth);
	}

	@Test
	@DisplayName("기자재들의 특정 날짜에 대여 예약된 갯수를 구한다.")
	void findRentalAmountsByEquipmentIds() {
		// given
		final RentableAsset equipment1 = assetRepository.save(
			EquipmentFixture.builder().name("모델이름1").totalQuantity(4).rentableQuantity(3).build());
		final RentableAsset equipment2 = assetRepository.save(
			EquipmentFixture.builder().name("모델이름2").totalQuantity(4).rentableQuantity(2).build());
		final RentableAsset equipment3 = assetRepository.save(
			EquipmentFixture.builder().name("모델이름3").totalQuantity(4).rentableQuantity(2).build());

		final ReservationSpecBuilder rentalSpec1Builder = ReservationSpecFixture.builder(equipment1);
		final ReservationSpec reservationSpec1 = rentalSpec1Builder.amount(RentalAmount.ofPositive(2))
			.period(new RentalPeriod(NOW, NOW.plusDays(2)))
			.build();
		final ReservationSpec reservationSpec2 = rentalSpec1Builder.amount(RentalAmount.ofPositive(1))
			.period(new RentalPeriod(NOW, NOW.plusDays(1)))
			.build();
		final ReservationSpec reservationSpec3 = rentalSpec1Builder.amount(RentalAmount.ofPositive(1))
			.period(new RentalPeriod(NOW.plusDays(1), NOW.plusDays(2)))
			.build();
		reservationSpecRepository.save(reservationSpec1);
		reservationSpecRepository.save(reservationSpec2);
		reservationSpecRepository.save(reservationSpec3);

		// when
		final List<ReservedAmount> expect = reservationSpecRepository.findRentalAmountsByAssetIds(
			List.of(equipment1.getId(), equipment2.getId(), equipment3.getId()), NOW);

		// then
		final ReservedAmount reservedAmount1 = new ReservedAmount(equipment1.getId(), 3, 3);
		final ReservedAmount reservedAmount2 = new ReservedAmount(equipment2.getId(), 2, 0);
		final ReservedAmount reservedAmount3 = new ReservedAmount(equipment3.getId(), 2, 0);
		assertAll(
			() -> assertThat(expect).hasSize(3),
			() -> assertThat(expect.get(0)).usingRecursiveComparison().isEqualTo(reservedAmount1),
			() -> assertThat(expect.get(1)).usingRecursiveComparison().isEqualTo(reservedAmount2),
			() -> assertThat(expect.get(2)).usingRecursiveComparison().isEqualTo(reservedAmount3)
		);
	}

	@Test
	@DisplayName("특정 기간에 대여 수령하는 취소되지 않은 대여 상세를 조회한다.")
	void findNotCanceldByStartDateBetween() {
		// given
		final RentableAsset equipment1 = assetRepository.save(EquipmentFixture.builder().name("model1").build());
		final RentableAsset equipment2 = assetRepository.save(EquipmentFixture.builder().name("model2").build());
		final YearMonth now = YearMonth.now();
		final ReservationSpec reservationSpec1 = reservationSpecRepository.save(
			ReservationSpecFixture.builder(equipment1)
				.period(new RentalPeriod(now.atDay(1), now.atEndOfMonth()))
				.build());
		final ReservationSpec reservationSpec2 = reservationSpecRepository.save(
			ReservationSpecFixture.builder(equipment2)
				.period(new RentalPeriod(now.atEndOfMonth(), now.atEndOfMonth().plusDays(1)))
				.build());
		final ReservationSpec reservationSpec3 = reservationSpecRepository.save(
			ReservationSpecFixture.builder(equipment1)
				.period(new RentalPeriod(now.atEndOfMonth().plusDays(1), now.atEndOfMonth().plusDays(2)))
				.build());
		final ReservationSpec reservationSpec4 = reservationSpecRepository.save(
			ReservationSpecFixture.builder(equipment1)
				.period(new RentalPeriod(now.atDay(1), now.atEndOfMonth()))
				.status(ReservationSpecStatus.CANCELED)
				.build());

		// when
		final List<ReservationSpec> expect = reservationSpecRepository.findNotCanceldByStartDateBetween(
			equipment1.getId(),
			now.atDay(1), now.atEndOfMonth());

		// then
		assertThat(expect).containsExactlyInAnyOrder(reservationSpec1);
	}

	@Test
	@DisplayName("대여 예약 상세의 갯수와 상태를 변경한다.")
	void adjustAmountAndStatus() {
		// given
		final RentableAsset equipment1 = assetRepository.save(EquipmentFixture.builder().name("model1").build());
		final ReservationSpec reservationSpec = ReservationSpecFixture.builder(equipment1)
			.amount(RentalAmount.ofPositive(1)).status(ReservationSpecStatus.RESERVED).build();
		final Reservation reservation = reservationRepository.save(ReservationFixture.create(List.of(reservationSpec)));

		// when
		reservationSpec.cancelAmount(1);
		reservationSpecRepository.adjustAmountAndStatus(reservationSpec);
		entityManager.detach(reservationSpec);
		final ReservationSpec actual = reservationSpecRepository.findById(reservationSpec.getId())
			.orElseThrow();

		// then
		assertThat(actual).usingRecursiveComparison()
			.isEqualTo(reservationSpec);
	}

	@Test
	@DisplayName("특정 날짜가 수령일인 기자재 대여 예약 상세를 회원 번호화 함께 조회한다.")
	void findEquipmentReservationForAccept() {
		// given
		final RentableAsset equipment1 = assetRepository.save(EquipmentFixture.builder().name("test1").build());
		final RentableAsset equipment2 = assetRepository.save(EquipmentFixture.builder().name("test2").build());
		final RentableAsset equipment3 = assetRepository.save(EquipmentFixture.builder().name("test3").build());
		final Member member = memberRepository.save(MemberFixture.create());

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1)
			.period(new RentalPeriod(NOW, NOW.plusDays(1)))
			.status(ReservationSpecStatus.RESERVED)
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(NOW, NOW.plusDays(1)))
			.status(ReservationSpecStatus.RENTED)
			.build();
		final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment3)
			.period(new RentalPeriod(NOW, NOW.plusDays(1)))
			.status(ReservationSpecStatus.CANCELED)
			.build();
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2, reservationSpec3))
				.memberId(member.getId())
				.build());

		final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(NOW.plusDays(1), NOW.plusDays(2)))
			.build();
		final ReservationSpec reservationSpec5 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(NOW.plusDays(1), NOW.plusDays(2)))
			.build();
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.create(List.of(reservationSpec4, reservationSpec5)));

		final ReservationSpec reservationSpec6 = ReservationSpecFixture.builder(equipment1)
			.period(new RentalPeriod(NOW, NOW.plusDays(1)))
			.status(ReservationSpecStatus.RESERVED)
			.build();
		final ReservationSpec reservationSpec7 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(NOW, NOW.plusDays(1)))
			.status(ReservationSpecStatus.RENTED)
			.build();
		final Reservation reservation3 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec6, reservationSpec7)).memberId(member.getId()).build());

		// when
		final Set<EquipmentReservationWithMemberNumber> actual = reservationSpecRepository.findEquipmentReservationWhenAccept(
			ReservationSpecRepositoryTest.NOW);

		// then
		assertThat(actual).usingRecursiveFieldByFieldElementComparatorIgnoringFields()
			.containsExactlyInAnyOrder(
				new EquipmentReservationWithMemberNumber(reservation1.getId(), reservation1.getName(),
					member.getMemberNumber(), reservation1.getAcceptDateTime(),
					List.of(reservationSpec1, reservationSpec2))
				, new EquipmentReservationWithMemberNumber(reservation3.getId(), reservation3.getName(),
					member.getMemberNumber(), reservation3.getAcceptDateTime(),
					List.of(reservationSpec6, reservationSpec7)));
	}

	@Test
	@DisplayName("기자재 반납이 지연된 대여 예약 상세를 회웑 정보와 함께 조회")
	void findOverdueEquipmentReservationWhenReturn() {
		// given
		final RentableAsset equipment1 = assetRepository.save(EquipmentFixture.builder().name("test1").build());
		final RentableAsset equipment2 = assetRepository.save(EquipmentFixture.builder().name("test2").build());

		final LocalDate now = LocalDate.now();
		final LocalDate start = now.minusDays(2);
		final Member member = memberRepository.save(MemberFixture.create());
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1)
			.period(new RentalPeriod(start, now.minusDays(1)))
			.status(ReservationSpecStatus.RETURNED)
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(start, now.minusDays(1)))
			.status(ReservationSpecStatus.OVERDUE_RENTED)
			.build();
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).memberId(member.getId()).build());
		final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(start, now))
			.status(ReservationSpecStatus.RETURNED)
			.build();
		final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(start, now))
			.status(ReservationSpecStatus.OVERDUE_RENTED)
			.build();
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec3, reservationSpec4)).terminated(true).build());

		// when
		final Set<EquipmentReservationWithMemberNumber> expect = reservationSpecRepository.findOverdueEquipmentReservationWhenReturn(
			now);

		// then
		assertThat(expect).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(
				new EquipmentReservationWithMemberNumber(reservation1.getId(), reservation1.getName(),
					member.getMemberNumber(), reservation1.getAcceptDateTime(), List.of(reservationSpec2)));
	}

	@Test
	@DisplayName("기자재 반납이 예정된 예약 상세를 회웑 정보와 함께 조회")
	void findEquipmentReservationWhenReturn() {
		// given
		final RentableAsset equipment1 = assetRepository.save(EquipmentFixture.builder().name("test1").build());
		final RentableAsset equipment2 = assetRepository.save(EquipmentFixture.builder().name("test2").build());

		final LocalDate now = LocalDate.now();
		final LocalDate start = now.minusDays(1);
		final Member member = memberRepository.save(MemberFixture.create());

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1)
			.period(new RentalPeriod(start, now))
			.status(ReservationSpecStatus.RENTED)
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(start, now))
			.status(ReservationSpecStatus.CANCELED)
			.build();
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).memberId(member.getId()).build());

		final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(start, now.plusDays(2)))
			.build();
		final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(start, now.plusDays(2)))
			.build();
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.create(List.of(reservationSpec3, reservationSpec4)));

		final ReservationSpec reservationSpec5 = ReservationSpecFixture.builder(equipment1)
			.period(new RentalPeriod(start, now))
			.status(ReservationSpecStatus.CANCELED)
			.build();
		final ReservationSpec reservationSpec6 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(start, now))
			.status(ReservationSpecStatus.CANCELED)
			.build();
		final Reservation reservation3 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec5, reservationSpec6))
				.memberId(member.getId())
				.terminated(true)
				.build());

		// when
		final Set<EquipmentReservationWithMemberNumber> expect = reservationSpecRepository.findEquipmentReservationWhenReturn(
			now);

		// then
		assertThat(expect).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(
				new EquipmentReservationWithMemberNumber(reservation1.getId(), reservation1.getName(),
					member.getMemberNumber(), reservation1.getAcceptDateTime(), List.of(reservationSpec1)));
	}

	@Test
	@DisplayName("특정 날짜가 수령일인 랩실 대여 예약 상세를 회원 번호화 함께 조회한다.")
	void findLabRoomReservationsWhenAccept() {
		// given
		final RentableAsset labRoom1 = assetRepository.save(LabRoomFixture.builder().name("test1").build());
		final RentableAsset labRoom2 = assetRepository.save(LabRoomFixture.builder().name("test2").build());
		final Member member = memberRepository.save(MemberFixture.create());

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(labRoom1)
			.period(new RentalPeriod(NOW, NOW.plusDays(1)))
			.build();
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1)).memberId(member.getId()).build());

		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(labRoom2)
			.period(new RentalPeriod(NOW, NOW.plusDays(2)))
			.build();
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec2)).memberId(member.getId()).build());

		final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(labRoom1)
			.period(new RentalPeriod(NOW, NOW.plusDays(1)))
			.build();
		final Reservation reservation3 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec3)).memberId(member.getId()).build());

		// when
		final Set<LabRoomReservationWithMemberNumberResponse> actual = reservationSpecRepository.findLabRoomReservationsWhenAccept(
			NOW);

		// then
		assertThat(actual).usingRecursiveFieldByFieldElementComparatorIgnoringFields()
			.containsExactlyInAnyOrder(
				new LabRoomReservationWithMemberNumberResponse(labRoom1.getName(), (RentalDateTime)null,
					List.of(createLabRoomReservationSpecWithMemberNumberResponse(member.getMemberNumber(), reservation1,
							reservationSpec1)
						, createLabRoomReservationSpecWithMemberNumberResponse(member.getMemberNumber(), reservation3,
							reservationSpec3))),
				new LabRoomReservationWithMemberNumberResponse(labRoom2.getName(), (RentalDateTime)null,
					List.of(createLabRoomReservationSpecWithMemberNumberResponse(member.getMemberNumber(), reservation2,
						reservationSpec2))
				));
	}

	@Test
	@DisplayName("특정 날짜가 반납일인 랩실 대여 예약 상세를 회원 번호화 함께 조회한다.")
	void findLabRoomReservationWhenReturn() {
		// given
		final RentableAsset labRoom1 = assetRepository.save(LabRoomFixture.builder().name("test1").build());
		final RentableAsset labRoom2 = assetRepository.save(LabRoomFixture.builder().name("test2").build());
		final Member member = memberRepository.save(MemberFixture.create());

		final RentalPeriod period = new RentalPeriod(NOW.minusDays(1), NOW);

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(labRoom1)
			.period(period)
			.status(ReservationSpecStatus.RENTED)
			.build();
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1)).memberId(member.getId()).build());

		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(labRoom2)
			.period(period)
			.status(ReservationSpecStatus.RENTED)
			.build();
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec2)).memberId(member.getId()).build());

		final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(labRoom1)
			.period(new RentalPeriod(NOW, NOW.plusDays(2)))
			.status(ReservationSpecStatus.RENTED)
			.build();
		final Reservation reservation3 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec3)).memberId(member.getId()).build());

		// when
		final Set<LabRoomReservationWithMemberNumberResponse> actual = reservationSpecRepository.findLabRoomReservationWhenReturn(
			NOW);

		// then
		assertThat(actual).usingRecursiveFieldByFieldElementComparatorIgnoringFields()
			.containsExactlyInAnyOrder(
				new LabRoomReservationWithMemberNumberResponse(labRoom1.getName(), (RentalDateTime)null,
					List.of(createLabRoomReservationSpecWithMemberNumberResponse(member.getMemberNumber(), reservation1,
						reservationSpec1))),
				new LabRoomReservationWithMemberNumberResponse(labRoom2.getName(), (RentalDateTime)null,
					List.of(createLabRoomReservationSpecWithMemberNumberResponse(member.getMemberNumber(), reservation2,
						reservationSpec2))
				));
	}

	@Test
	@DisplayName("대여 예약 상세 id로 대여 예약을 조회한다.")
	void findByReservationSpecIds() {
		// given
		final RentableAsset labRoom1 = assetRepository.save(LabRoomFixture.builder().name("test1").build());
		final RentableAsset labRoom2 = assetRepository.save(LabRoomFixture.builder().name("test2").build());
		final RentableAsset labRoom3 = assetRepository.save(LabRoomFixture.builder().name("test3").build());
		final Member member = memberRepository.save(MemberFixture.create());

		final ReservationSpec reservationSpec1 = ReservationSpecFixture.create(labRoom1);
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.create(labRoom2);
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).memberId(member.getId()).build());
		final ReservationSpec reservationSpec3 = ReservationSpecFixture.create(labRoom3);
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec3)).memberId(member.getId()).build());

		// when
		entityManager.flush();
		entityManager.clear();
		final List<Reservation> reservations = reservationRepository.findByReservationSpecIds(
			List.of(reservationSpec1.getId(), reservationSpec3.getId()));

		// then
		assertThat(reservations).usingRecursiveFieldByFieldElementComparator()
			.containsExactlyInAnyOrder(reservation1, reservation2);
	}

	@Test
	@DisplayName("id에 해당하는 상세들을 특정 상태로 업데이트")
	void updateStatusByIds() {
		// given
		final RentableAsset labRoom1 = assetRepository.save(LabRoomFixture.builder().name("test1").build());
		final ReservationSpec reservationSpec1 = reservationSpecRepository.save(
			ReservationSpecFixture.create(labRoom1));

		// when
		reservationSpecRepository.updateStatusByIds(List.of(reservationSpec1.getId()), ReservationSpecStatus.CANCELED);
		entityManager.refresh(reservationSpec1);

		// then
		assertThat(reservationSpec1.getStatus()).isEqualTo(ReservationSpecStatus.CANCELED);
	}

	@Test
	@DisplayName("대여 히스토리 통계를 조회한다.")
	void findHistoryStat() {
		// given
		LocalDate start = LocalDate.now().minusDays(10);
		LocalDate end = LocalDate.now();
		final RentableAsset labRoom1 = assetRepository.save(LabRoomFixture.builder().name("test1").build());
		final RentableAsset labRoom2 = assetRepository.save(LabRoomFixture.builder().name("test2").build());
		final ReservationSpec reservationSpec1 = reservationSpecRepository.save(
			ReservationSpecFixture.builder(labRoom1)
				.period(new RentalPeriod(start, end))
				.status(ReservationSpecStatus.RETURNED)
				.build());
		final ReservationSpec reservationSpec2 = reservationSpecRepository.save(
			ReservationSpecFixture.builder(labRoom1)
				.period(new RentalPeriod(start.minusDays(1), end))
				.status(ReservationSpecStatus.RETURNED)
				.build());
		final ReservationSpec reservationSpec3 = reservationSpecRepository.save(
			ReservationSpecFixture.builder(labRoom1)
				.period(new RentalPeriod(start, end.plusDays(1)))
				.status(ReservationSpecStatus.RETURNED)
				.build());
		final ReservationSpec reservationSpec4 = reservationSpecRepository.save(
			ReservationSpecFixture.builder(labRoom1)
				.period(new RentalPeriod(start.minusDays(1), end.plusDays(1)))
				.status(ReservationSpecStatus.RETURNED)
				.build());
		final ReservationSpec reservationSpec5 = reservationSpecRepository.save(
			ReservationSpecFixture.builder(labRoom2)
				.period(new RentalPeriod(start, end))
				.status(ReservationSpecStatus.RETURNED)
				.build());
		final ReservationSpec reservationSpec6 = reservationSpecRepository.save(
			ReservationSpecFixture.builder(labRoom1)
				.period(new RentalPeriod(start, end))
				.status(ReservationSpecStatus.ABNORMAL_RETURNED)
				.build());
		final ReservationSpec reservationSpec7 = reservationSpecRepository.save(
			ReservationSpecFixture.builder(labRoom2)
				.period(new RentalPeriod(start, end))
				.status(ReservationSpecStatus.ABNORMAL_RETURNED)
				.build());

		// when
		HistoryStatResponse actual = reservationSpecRepository.findHistoryStat(
			labRoom1.getName(), start, end);

		// then
		assertThat(actual).usingRecursiveComparison()
			.isEqualTo(
				new HistoryStatResponse(labRoom1.getName(), 2,
					reservationSpec1.getAmount().getAmount() + reservationSpec6.getAmount().getAmount(), 1));
	}

	@Test
	@DisplayName("특정 자산의 예약 중이거나 대여된 대여 예약 상세를 조회한다.")
	void findReservedOrRentedByAssetId() {
		// given
		final RentableAsset equipment1 = assetRepository.save(EquipmentFixture.builder().name("test1").build());
		final RentableAsset equipment2 = assetRepository.save(EquipmentFixture.builder().name("test2").build());
		final ReservationSpec spec1 = ReservationSpecFixture.create(equipment1);
		reservationRepository.save(ReservationFixture.create(List.of(spec1)));
		final ReservationSpec spec2 = ReservationSpecFixture.create(equipment2);
		reservationRepository.save(ReservationFixture.create(List.of(spec2)));

		// when
		final List<ReservationSpec> actual = reservationSpecRepository.findReservedOrRentedByAssetId(
			equipment1.getId());

		// then
		assertThat(actual).usingRecursiveFieldByFieldElementComparator()
			.containsExactlyInAnyOrder(spec1);
	}

	@Test
	@DisplayName("특정 자산의 예약 중이거나 대여 중이고 특정 날짜가 대여 기간에 포함된 대여 예약 상세를 조회한다.")
	void findReservedOrRentedByAssetIdAndDate() {
		// given
		final LocalDate now = LocalDate.now();
		final RentableAsset equipment1 = assetRepository.save(EquipmentFixture.builder().name("test1").build());
		final RentableAsset equipment2 = assetRepository.save(EquipmentFixture.builder().name("test2").build());
		final ReservationSpec spec1 = ReservationSpecFixture.builder(equipment1)
			.period(new RentalPeriod(now, now.plusDays(1))).build();
		reservationRepository.save(ReservationFixture.create(List.of(spec1)));
		final ReservationSpec spec2 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(now.plusDays(1), now.plusDays(2))).build();
		reservationRepository.save(ReservationFixture.create(List.of(spec2)));

		// when
		final List<ReservationSpec> actual = reservationSpecRepository.findReservedOrRentedByAssetIdAndDate(
			equipment1.getId(), now);

		// then
		assertThat(actual).usingRecursiveFieldByFieldElementComparator()
			.containsExactlyInAnyOrder(spec1);
	}

	private LabRoomReservationSpecWithMemberNumberResponse createLabRoomReservationSpecWithMemberNumberResponse(
		final String memberNumber, final Reservation reservation2, final ReservationSpec reservationSpec2) {
		return new LabRoomReservationSpecWithMemberNumberResponse(reservationSpec2.getId(),
			reservationSpec2.getReservation().getId(), reservation2.getName(), memberNumber,
			reservationSpec2.getAmount().getAmount(),
			reservation2.getPhoneNumber());
	}
}