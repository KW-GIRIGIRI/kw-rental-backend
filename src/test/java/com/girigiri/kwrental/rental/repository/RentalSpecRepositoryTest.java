package com.girigiri.kwrental.rental.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.asset.domain.RentableAsset;
import com.girigiri.kwrental.asset.repository.AssetRepository;
import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.config.JpaConfig;
import com.girigiri.kwrental.rental.domain.AbstractRentalSpec;
import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.domain.LabRoomRentalSpec;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.rental.dto.response.EquipmentRentalsDto.EquipmentRentalDto;
import com.girigiri.kwrental.rental.dto.response.EquipmentRentalsDto.EquipmentRentalDto.EquipmentRentalSpecDto;
import com.girigiri.kwrental.rental.dto.response.LabRoomRentalsDto.LabRoomRentalDto;
import com.girigiri.kwrental.rental.dto.response.LabRoomReservationResponse;
import com.girigiri.kwrental.rental.dto.response.RentalSpecStatuesPerPropertyNumber;
import com.girigiri.kwrental.rental.dto.response.RentalSpecWithName;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.EquipmentRentalSpecFixture;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;
import com.girigiri.kwrental.testsupport.fixture.LabRoomRentalSpecFixture;
import com.girigiri.kwrental.testsupport.fixture.MemberFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@Import(JpaConfig.class)
class RentalSpecRepositoryTest {

	@Autowired
	private RentalSpecRepository rentalSpecRepository;
	@Autowired
	private ReservationSpecRepository reservationSpecRepository;
	@Autowired
	private AssetRepository assetRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private ReservationRepository reservationRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@DisplayName("대여 상세를 모두 저장한다.")
	void saveAll() {
		// given
		final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder()
			.acceptDateTime(null)
			.propertyNumber("12345678")
			.build();
		final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder()
			.acceptDateTime(null)
			.propertyNumber("87654321")
			.build();

		// when
		rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

		// then
		assertAll(
			() -> assertThat(rentalSpec1.getId()).isNotNull(),
			() -> assertThat(rentalSpec2.getId()).isNotNull(),
			() -> assertThat(rentalSpec1.getAcceptDateTime()).isNotNull(),
			() -> assertThat(rentalSpec2.getAcceptDateTime()).isNotNull()
		);
	}

	@Test
	@DisplayName("특정 기자재의 특정 날짜에 대여 중인 대여 상세를 조회한다.")
	void findRentedRentalSpecs() {
		// given
		final RentalDateTime acceptTime = RentalDateTime.now();
		final RentalDateTime returnTime = RentalDateTime.now();
		final Rentable equipment1 = assetRepository.save(EquipmentFixture.builder().name("modelName1").build());
		final Rentable equipment2 = assetRepository.save(EquipmentFixture.builder().name("modelName2").build());
		final ReservationSpec reservationSpec1 = reservationSpecRepository.save(
			ReservationSpecFixture.builder(equipment1).build());
		final ReservationSpec reservationSpec2 = reservationSpecRepository.save(
			ReservationSpecFixture.builder(equipment2).build());

		final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder()
			.acceptDateTime(acceptTime)
			.propertyNumber("11111111")
			.reservationSpecId(reservationSpec1.getId())
			.build();
		final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder()
			.acceptDateTime(acceptTime)
			.returnDateTime(returnTime)
			.propertyNumber("22222222")
			.reservationSpecId(reservationSpec1.getId())
			.build();
		final EquipmentRentalSpec rentalSpec3 = EquipmentRentalSpecFixture.builder()
			.acceptDateTime(acceptTime)
			.propertyNumber("33333333")
			.reservationSpecId(reservationSpec2.getId())
			.build();

		rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2, rentalSpec3));

		// when
		final Set<EquipmentRentalSpec> rentedRentalSpecs = rentalSpecRepository.findRentedRentalSpecsByAssetId(
			equipment1.getId(), LocalDateTime.now());

		// then
		assertThat(rentedRentalSpecs).containsExactlyInAnyOrder(rentalSpec1);
	}

	@Test
	@DisplayName("여러 ID로 조회한다.")
	void findByIds() {
		// given
		final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder()
			.propertyNumber("11111111")
			.reservationSpecId(1L)
			.reservationId(1L)
			.build();
		final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder()
			.propertyNumber("22222222")
			.reservationSpecId(2L)
			.reservationId(1L)
			.build();
		final EquipmentRentalSpec rentalSpec3 = EquipmentRentalSpecFixture.builder()
			.propertyNumber("33333333")
			.reservationSpecId(3L)
			.reservationId(1L)
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2, rentalSpec3));

		// when
		final List<EquipmentRentalSpec> expect = rentalSpecRepository.findByReservationId(1L);

		// then
		assertThat(expect).containsExactlyInAnyOrder(rentalSpec1, rentalSpec2, rentalSpec3);
	}

	@Test
	@DisplayName("특정 기간에 해당하는 기자재 대여를 조히한다.")
	void findEquipmentRentalDtosBetweenDate() {
		// given
		final Member member1 = memberRepository.save(MemberFixture.create());
		final Member member2 = memberRepository.save(MemberFixture.create());
		final Rentable equipment1 = assetRepository.save(EquipmentFixture.builder().name("model1").build());
		final Rentable equipment2 = assetRepository.save(EquipmentFixture.builder().name("model2").build());

		final LocalDate now = LocalDate.now();
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1)
			.period(new RentalPeriod(now, now.plusDays(1)))
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(now, now.plusDays(1)))
			.build();
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).memberId(member1.getId()).build());

		final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment1)
			.period(new RentalPeriod(now.plusDays(1), now.plusDays(2)))
			.build();
		final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(now.plusDays(1), now.plusDays(2)))
			.build();
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec3, reservationSpec4)).memberId(member2.getId()).build());

		final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder()
			.propertyNumber("11111111")
			.reservationSpecId(reservationSpec1.getId())
			.reservationId(reservation1.getId())
			.build();
		final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder()
			.propertyNumber("22222222")
			.reservationSpecId(reservationSpec2.getId())
			.reservationId(reservation1.getId())
			.build();
		final EquipmentRentalSpec rentalSpec3 = EquipmentRentalSpecFixture.builder()
			.propertyNumber("33333333")
			.reservationSpecId(reservationSpec3.getId())
			.reservationId(reservation2.getId())
			.build();
		final EquipmentRentalSpec rentalSpec4 = EquipmentRentalSpecFixture.builder()
			.propertyNumber("44444444")
			.reservationSpecId(reservationSpec4.getId())
			.reservationId(reservation2.getId())
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2, rentalSpec3, rentalSpec4));

		// when
		final List<EquipmentRentalDto> rentalDtos = rentalSpecRepository.findEquipmentRentalDtosBetweenDate(
			member1.getId(), now, now.plusDays(2));

		// then
		assertThat(rentalDtos).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(
				new EquipmentRentalDto(reservationSpec1.getStartDate(), reservationSpec1.getEndDate(),
					Set.of(
						new EquipmentRentalSpecDto(rentalSpec1.getId(), equipment1.getName(), rentalSpec1.getStatus()),
						new EquipmentRentalSpecDto(rentalSpec2.getId(), equipment2.getName(), rentalSpec2.getStatus())))
			);
	}

	@Test
	@DisplayName("특정 자산번호의 특정 기간동안 대여 상태들을 조회한다.")
	void findRentalCountsByPropertyNumbersBetweenDate() {
		// given
		final Rentable equipment1 = assetRepository.save(EquipmentFixture.builder().name("model1").build());
		final Rentable equipment2 = assetRepository.save(EquipmentFixture.builder().name("model2").build());

		final LocalDate now = LocalDate.now();
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1)
			.period(new RentalPeriod(now, now.plusDays(1)))
			.build();
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(now, now.plusDays(1)))
			.build();
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).build());

		final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment1)
			.period(new RentalPeriod(now.plusDays(1), now.plusDays(2)))
			.build();
		final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment2)
			.period(new RentalPeriod(now.plusDays(1), now.plusDays(2)))
			.build();
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec3, reservationSpec4)).build());

		final String propertyNumber1 = "11111111";
		final String propertyNumber2 = "22222222";
		final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder()
			.propertyNumber(propertyNumber1)
			.reservationSpecId(reservationSpec1.getId())
			.reservationId(reservation1.getId())
			.status(RentalSpecStatus.RETURNED)
			.build();
		final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder()
			.propertyNumber(propertyNumber2)
			.reservationSpecId(reservationSpec2.getId())
			.reservationId(reservation1.getId())
			.status(RentalSpecStatus.LOST)
			.build();
		final EquipmentRentalSpec rentalSpec3 = EquipmentRentalSpecFixture.builder()
			.propertyNumber(propertyNumber1)
			.reservationSpecId(reservationSpec3.getId())
			.reservationId(reservation2.getId())
			.status(RentalSpecStatus.BROKEN)
			.build();
		final EquipmentRentalSpec rentalSpec4 = EquipmentRentalSpecFixture.builder()
			.propertyNumber(propertyNumber2)
			.reservationSpecId(reservationSpec4.getId())
			.reservationId(reservation2.getId())
			.status(RentalSpecStatus.LOST)
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2, rentalSpec3, rentalSpec4));

		// when
		final List<RentalSpecStatuesPerPropertyNumber> rentalCountsDtos = rentalSpecRepository.findStatusesByPropertyNumbersBetweenDate(
			Set.of(rentalSpec1.getPropertyNumber(), rentalSpec2.getPropertyNumber()), now, now.plusDays(2));

		// then
		assertThat(rentalCountsDtos).usingRecursiveFieldByFieldElementComparator()
			.containsExactlyInAnyOrder(
				new RentalSpecStatuesPerPropertyNumber(rentalSpec1.getPropertyNumber(),
					List.of(RentalSpecStatus.RETURNED, RentalSpecStatus.BROKEN)),
				new RentalSpecStatuesPerPropertyNumber(rentalSpec2.getPropertyNumber(),
					List.of(RentalSpecStatus.LOST, RentalSpecStatus.LOST)));
	}

	@Test
	@DisplayName("자산번호에 해당하는 대여 상세를 대여자의 이름과 함께 조회한다.")
	void findTerminatedRentalSpecsWithNameByPropertyNumber() {
		// given
		final Rentable equipment = assetRepository.save(EquipmentFixture.create());
		final ReservationSpec reservationSpec = ReservationSpecFixture.create(equipment);
		final Reservation reservation = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec)).terminated(true).build());
		final EquipmentRentalSpec rentalSpec = EquipmentRentalSpecFixture.builder()
			.reservationId(reservation.getId())
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec));

		// when
		final List<RentalSpecWithName> result = rentalSpecRepository.findTerminatedWithNameByPropertyNumber(
			rentalSpec.getPropertyNumber());

		// then
		assertAll(
			() -> assertThat(result).hasSize(1),
			() -> assertThat(result.
				get(0)).usingRecursiveComparison()
				.isEqualTo(new RentalSpecWithName(
					reservation.getName(), rentalSpec.getAcceptDateTime(), rentalSpec.getReturnDateTime(),
					rentalSpec.getStatus()))
		);
	}

	@Test
	@DisplayName("대여 예약에 해당하는 대여 상세를 정상 반납으로 업데이트한다.")
	void updateNormalReturnedByReservationIds() {
		// given
		final Rentable equipment = assetRepository.save(EquipmentFixture.create());
		final ReservationSpec reservationSpec = ReservationSpecFixture.create(equipment);
		final Reservation reservation = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec)).terminated(true).build());
		final LabRoomRentalSpec rentalSpec = LabRoomRentalSpecFixture.builder()
			.reservationId(reservation.getId())
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec));
		entityManager.clear();

		// when
		rentalSpecRepository.updateNormalReturnedByReservationIds(List.of(reservation.getId()), RentalDateTime.now());

		//then
		AbstractRentalSpec actual = rentalSpecRepository.findById(rentalSpec.getId()).orElseThrow();
		assertThat(actual.getStatus()).isEqualTo(RentalSpecStatus.RETURNED);
		assertThat(actual.getReturnDateTime()).isNotNull();
	}

	@Test
	@DisplayName("특정 랩실 이름과 특정 날짜로 랩실 대여 예약을 대여 상세와 함께 조회한다.")
	void getLabRoomReservationWithRentalSpec() {
		// given
		final Rentable labRoom = assetRepository.save(LabRoomFixture.create());
		final ReservationSpec reservationSpec = ReservationSpecFixture.builder(labRoom)
			.status(ReservationSpecStatus.RETURNED)
			.build();
		final Reservation reservation = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec)).build());
		final LabRoomRentalSpec rentalSpec = LabRoomRentalSpecFixture.builder()
			.reservationId(reservation.getId())
			.reservationSpecId(reservationSpec.getId())
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec));

		// when
		final List<LabRoomReservationResponse> actual = rentalSpecRepository.getReturnedLabRoomReservationResponse(
			labRoom.getName(), reservationSpec.getPeriod().getRentalStartDate());

		// then
		assertThat(actual).usingRecursiveFieldByFieldElementComparator()
			.containsExactlyInAnyOrder(new LabRoomReservationResponse(reservation.getId(), reservationSpec.getId(),
				reservationSpec.getPeriod().getRentalStartDate(), reservationSpec.getPeriod().getRentalEndDate(),
				reservation.getName(), rentalSpec.getStatus()));
	}

	@Test
	@DisplayName("특정 기간에 해당하는 랩실 대여를 조히한다.")
	void findLabRoomRentalDtosBetweenDate() {
		// given
		final Member member1 = memberRepository.save(MemberFixture.create());
		final Member member2 = memberRepository.save(MemberFixture.create());
		final Rentable labRoom1 = assetRepository.save(LabRoomFixture.builder().name("hanul").build());
		final Rentable labRoom2 = assetRepository.save(LabRoomFixture.builder().name("saebit").build());

		final LocalDate now = LocalDate.now();
		final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(labRoom1)
			.period(new RentalPeriod(now, now.plusDays(1)))
			.build();
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec1)).memberId(member1.getId()).build());
		final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(labRoom2)
			.period(new RentalPeriod(now.plusDays(1), now.plusDays(2)))
			.build();
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec2)).memberId(member1.getId()).build());

		final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(labRoom1)
			.period(new RentalPeriod(now, now.plusDays(2)))
			.build();
		final Reservation reservation3 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec3)).memberId(member2.getId()).build());
		final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(labRoom2)
			.period(new RentalPeriod(now.plusDays(1), now.plusDays(2)))
			.build();
		final Reservation reservation4 = reservationRepository.save(
			ReservationFixture.builder(List.of(reservationSpec4)).memberId(member2.getId()).build());

		final LabRoomRentalSpec rentalSpec1 = LabRoomRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec1.getId())
			.reservationId(reservation1.getId())
			.build();
		final LabRoomRentalSpec rentalSpec2 = LabRoomRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec2.getId())
			.reservationId(reservation2.getId())
			.build();
		final LabRoomRentalSpec rentalSpec3 = LabRoomRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec3.getId())
			.reservationId(reservation3.getId())
			.build();
		final LabRoomRentalSpec rentalSpec4 = LabRoomRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec4.getId())
			.reservationId(reservation4.getId())
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2, rentalSpec3, rentalSpec4));

		// when
		final List<LabRoomRentalDto> rentalDtos = rentalSpecRepository.findLabRoomRentalDtosBetweenDate(member1.getId(),
			now, now.plusDays(2));

		// then
		assertThat(rentalDtos).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(
				new LabRoomRentalDto(reservation1.getStartDate(), reservation1.getEndDate(), labRoom1.getName(),
					reservationSpec1.getAmount().getAmount(), rentalSpec1.getStatus()),
				new LabRoomRentalDto(reservation2.getStartDate(), reservation2.getEndDate(), labRoom2.getName(),
					reservationSpec2.getAmount().getAmount(), rentalSpec2.getStatus())
			);
	}

	@Test
	@DisplayName("자산번호를 업데이트 한다.")
	void updatePropertyNumber() {
		// given
		final EquipmentRentalSpec spec = EquipmentRentalSpecFixture.builder().propertyNumber("11111111").build();
		rentalSpecRepository.saveAll(List.of(spec));

		// when
		final String updatedPropertyNumber = "22222222";
		rentalSpecRepository.updatePropertyNumber(spec.getPropertyNumber(), updatedPropertyNumber);

		// then
		entityManager.refresh(spec);
		assertThat(spec.getPropertyNumber()).isEqualTo(updatedPropertyNumber);
	}

	@Test
	@DisplayName("특정 자산에 해당하는 대여 상세를 조회한다.")
	void findRentedRentalSpecsByAssetId() {
		// given
		RentableAsset asset1 = assetRepository.save(EquipmentFixture.builder().name("name1").build());
		RentableAsset asset2 = assetRepository.save(EquipmentFixture.builder().name("name2").build());
		ReservationSpec reservationSpec1 = reservationSpecRepository.save(ReservationSpecFixture.create(asset1));
		ReservationSpec reservationSpec2 = reservationSpecRepository.save(ReservationSpecFixture.create(asset2));

		final EquipmentRentalSpec spec1 = EquipmentRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec1.getId())
			.status(RentalSpecStatus.RENTED)
			.propertyNumber("11111111")
			.build();
		final EquipmentRentalSpec spec2 = EquipmentRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec1.getId())
			.status(RentalSpecStatus.RETURNED)
			.propertyNumber("22222222")
			.build();
		final EquipmentRentalSpec spec3 = EquipmentRentalSpecFixture.builder()
			.reservationSpecId(reservationSpec2.getId())
			.status(RentalSpecStatus.RENTED)
			.propertyNumber("33333333")
			.build();
		rentalSpecRepository.saveAll(List.of(spec1, spec2, spec3));

		// when
		List<AbstractRentalSpec> actual = rentalSpecRepository.findRentedRentalSpecsByAssetId(
			asset1.getId());

		// then
		assertThat(actual).containsExactly(spec1);
	}

	@Test
	@DisplayName("특정 기간에 수령과 반납을 특정 자산번호로 된 대여 상세를 이름과 함께 조회")
	void getReturnedEquipmentRentalSpecsWithNameInclusive() {
		// given
		final Rentable equipment = assetRepository.save(EquipmentFixture.create());
		final RentalDateTime now = RentalDateTime.now();
		final Reservation reservation1 = reservationRepository.save(
			ReservationFixture.builder(List.of(ReservationSpecFixture.create(equipment))).terminated(true).build());
		final Reservation reservation2 = reservationRepository.save(
			ReservationFixture.builder(List.of(ReservationSpecFixture.create(equipment))).terminated(false).build());
		final String propertyNumber = "11111111";
		final RentalDateTime returnDate = now.calculateDay(1);
		final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder()
			.reservationId(reservation1.getId())
			.acceptDateTime(returnDate)
			.returnDateTime(now)
			.propertyNumber(propertyNumber)
			.status(RentalSpecStatus.LOST)
			.build();
		final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder()
			.reservationId(reservation2.getId())
			.acceptDateTime(returnDate)
			.returnDateTime(now)
			.propertyNumber(propertyNumber)
			.build();
		rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2));

		// when
		List<RentalSpecWithName> actual = rentalSpecRepository.findTerminatedWithNameByPropertyAndInclusive(
			propertyNumber, now, returnDate);

		// then
		assertThat(actual).usingRecursiveFieldByFieldElementComparator().containsExactly(
			new RentalSpecWithName(reservation1.getName(), rentalSpec1.getAcceptDateTime(),
				rentalSpec1.getReturnDateTime(), rentalSpec1.getStatus()));
	}

	@Test
	@DisplayName("랩실의 이름에 해당하는 대여 상세를 조회할 수 있다.")
	void findNowRentedRentalSpecsByName() {
		// given
		final RentableAsset labRoom = assetRepository.save(LabRoomFixture.create());
		final LocalDate now = LocalDate.now();
		final ReservationSpec nowRent = ReservationSpecFixture.builder(labRoom)
			.period(new RentalPeriod(now, now.plusDays(3))).build();
		final ReservationSpec rentedBefore = ReservationSpecFixture.builder(labRoom)
			.period(new RentalPeriod(now.minusDays(4), now.minusDays(3))).build();
		final ReservationSpec notRentedYet = ReservationSpecFixture.builder(labRoom)
			.period(new RentalPeriod(now.plusDays(1), now.plusDays(3))).build();

		reservationSpecRepository.save(nowRent);
		reservationSpecRepository.save(rentedBefore);
		reservationSpecRepository.save(notRentedYet);
		final LabRoomRentalSpec nowRentalSpec = LabRoomRentalSpecFixture.builder()
			.reservationSpecId(nowRent.getId())
			.build();
		final LabRoomRentalSpec beforeRentalSpec = LabRoomRentalSpecFixture.builder()
			.reservationSpecId(rentedBefore.getId())
			.build();
		final LabRoomRentalSpec afterRentalSpec = LabRoomRentalSpecFixture.builder()
			.reservationSpecId(notRentedYet.getId())
			.build();

		rentalSpecRepository.saveAll(List.of(nowRentalSpec, beforeRentalSpec, afterRentalSpec));

		// when
		final List<AbstractRentalSpec> actual = rentalSpecRepository.findNowRentedRentalSpecsByName(
			labRoom.getName());

		// then
		assertThat(actual).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(nowRentalSpec);
	}
}