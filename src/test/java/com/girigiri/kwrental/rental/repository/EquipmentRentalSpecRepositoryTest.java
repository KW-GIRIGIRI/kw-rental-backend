package com.girigiri.kwrental.rental.repository;

import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.config.JpaConfig;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.inventory.domain.RentalDateTime;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.rental.dto.response.RentalSpecWithName;
import com.girigiri.kwrental.rental.repository.dto.RentalDto;
import com.girigiri.kwrental.rental.repository.dto.RentalSpecDto;
import com.girigiri.kwrental.rental.repository.dto.RentalSpecStatuesPerPropertyNumber;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import com.girigiri.kwrental.testsupport.fixture.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Import(JpaConfig.class)
class EquipmentRentalSpecRepositoryTest {

    @Autowired
    private RentalSpecRepository rentalSpecRepository;
    @Autowired
    private ReservationSpecRepository reservationSpecRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("대여 상세를 모두 저장한다.")
    void saveAll() {
        // given
        final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder().acceptDateTime(null).propertyNumber("12345678").build();
        final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder().acceptDateTime(null).propertyNumber("87654321").build();

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
        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().name("modelName1").build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().name("modelName2").build());
        final ReservationSpec reservationSpec1 = reservationSpecRepository.save(ReservationSpecFixture.builder(equipment1).build());
        final ReservationSpec reservationSpec2 = reservationSpecRepository.save(ReservationSpecFixture.builder(equipment2).build());

        final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder().acceptDateTime(acceptTime).propertyNumber("11111111").reservationSpecId(reservationSpec1.getId()).build();
        final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder().acceptDateTime(acceptTime).returnDateTime(returnTime).propertyNumber("22222222").reservationSpecId(reservationSpec1.getId()).build();
        final EquipmentRentalSpec rentalSpec3 = EquipmentRentalSpecFixture.builder().acceptDateTime(acceptTime).propertyNumber("33333333").reservationSpecId(reservationSpec2.getId()).build();

        rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2, rentalSpec3));

        // when
        final Set<EquipmentRentalSpec> rentedRentalSpecs = rentalSpecRepository.findRentedRentalSpecs(equipment1.getId(), LocalDateTime.now());

        // then
        assertThat(rentedRentalSpecs).containsExactlyInAnyOrder(rentalSpec1);
    }

    @Test
    @DisplayName("여러 ID로 조회한다.")
    void findByIds() {
        // given
        final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder().propertyNumber("11111111").reservationSpecId(1L).reservationId(1L).build();
        final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder().propertyNumber("22222222").reservationSpecId(2L).reservationId(1L).build();
        final EquipmentRentalSpec rentalSpec3 = EquipmentRentalSpecFixture.builder().propertyNumber("33333333").reservationSpecId(3L).reservationId(1L).build();
        rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2, rentalSpec3));

        // when
        final List<EquipmentRentalSpec> expect = rentalSpecRepository.findByReservationId(1L);

        // then
        assertThat(expect).containsExactlyInAnyOrder(rentalSpec1, rentalSpec2, rentalSpec3);
    }

    @Test
    @DisplayName("특정 기간에 해당하는 대여를 조히한다.")
    void findRentalDtosBetweenDate() {
        // given
        final Member member = memberRepository.save(MemberFixture.create());
        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().name("model1").build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().name("model2").build());

        final LocalDate now = LocalDate.now();
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(now, now.plusDays(1))).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(now, now.plusDays(1))).build();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).memberId(member.getId()).build());

        final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(now.plusDays(1), now.plusDays(2))).build();
        final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(now.plusDays(1), now.plusDays(2))).build();
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec3, reservationSpec4)).memberId(member.getId()).build());

        final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder().propertyNumber("11111111").reservationSpecId(reservationSpec1.getId()).reservationId(reservation1.getId()).build();
        final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder().propertyNumber("22222222").reservationSpecId(reservationSpec2.getId()).reservationId(reservation1.getId()).build();
        final EquipmentRentalSpec rentalSpec3 = EquipmentRentalSpecFixture.builder().propertyNumber("33333333").reservationSpecId(reservationSpec3.getId()).reservationId(reservation2.getId()).build();
        final EquipmentRentalSpec rentalSpec4 = EquipmentRentalSpecFixture.builder().propertyNumber("44444444").reservationSpecId(reservationSpec4.getId()).reservationId(reservation2.getId()).build();
        rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2, rentalSpec3, rentalSpec4));

        // when
        final List<RentalDto> rentalDtos = rentalSpecRepository.findRentalDtosBetweenDate(member.getId(), now, now.plusDays(2));

        // then
        assertThat(rentalDtos).usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                        new RentalDto(reservationSpec1.getStartDate(), reservationSpec1.getEndDate(),
                                Set.of(new RentalSpecDto(rentalSpec1.getId(), equipment1.getName(), rentalSpec1.getStatus()), new RentalSpecDto(rentalSpec2.getId(), equipment2.getName(), rentalSpec2.getStatus()))),
                        new RentalDto(reservationSpec3.getStartDate(), reservationSpec3.getEndDate(),
                                Set.of(new RentalSpecDto(rentalSpec3.getId(), equipment1.getName(), rentalSpec3.getStatus()), new RentalSpecDto(rentalSpec4.getId(), equipment2.getName(), rentalSpec4.getStatus())))
                );
    }

    @Test
    @DisplayName("특정 자산번호의 특정 기간동안 대여 상태들을 조회한다.")
    void findRentalCountsByPropertyNumbersBetweenDate() {
        // given
        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().name("model1").build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().name("model2").build());

        final LocalDate now = LocalDate.now();
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(now, now.plusDays(1))).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(now, now.plusDays(1))).build();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).build());

        final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(now.plusDays(1), now.plusDays(2))).build();
        final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(now.plusDays(1), now.plusDays(2))).build();
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec3, reservationSpec4)).build());

        final String propertyNumber1 = "11111111";
        final String propertyNumber2 = "22222222";
        final EquipmentRentalSpec rentalSpec1 = EquipmentRentalSpecFixture.builder().propertyNumber(propertyNumber1).reservationSpecId(reservationSpec1.getId()).reservationId(reservation1.getId())
                .status(RentalSpecStatus.RETURNED).build();
        final EquipmentRentalSpec rentalSpec2 = EquipmentRentalSpecFixture.builder().propertyNumber(propertyNumber2).reservationSpecId(reservationSpec2.getId()).reservationId(reservation1.getId())
                .status(RentalSpecStatus.LOST).build();
        final EquipmentRentalSpec rentalSpec3 = EquipmentRentalSpecFixture.builder().propertyNumber(propertyNumber1).reservationSpecId(reservationSpec3.getId()).reservationId(reservation2.getId())
                .status(RentalSpecStatus.BROKEN).build();
        final EquipmentRentalSpec rentalSpec4 = EquipmentRentalSpecFixture.builder().propertyNumber(propertyNumber2).reservationSpecId(reservationSpec4.getId()).reservationId(reservation2.getId())
                .status(RentalSpecStatus.LOST).build();
        rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2, rentalSpec3, rentalSpec4));

        // when
        final List<RentalSpecStatuesPerPropertyNumber> rentalCountsDtos = rentalSpecRepository.findStatusesByPropertyNumbersBetweenDate(
                Set.of(rentalSpec1.getPropertyNumber(), rentalSpec2.getPropertyNumber()), now, now.plusDays(2));

        // then
        assertThat(rentalCountsDtos).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(
                        new RentalSpecStatuesPerPropertyNumber(rentalSpec1.getPropertyNumber(), List.of(RentalSpecStatus.RETURNED, RentalSpecStatus.BROKEN)),
                        new RentalSpecStatuesPerPropertyNumber(rentalSpec2.getPropertyNumber(), List.of(RentalSpecStatus.LOST, RentalSpecStatus.LOST)));
    }

    @Test
    @DisplayName("자산번호에 해당하는 대여 상세를 대여자의 이름과 함께 조회한다.")
    void findRentalSpecsWithNameByPropertyNumber() {
        // given
        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        final ReservationSpec reservationSpec = ReservationSpecFixture.create(equipment);
        final Reservation reservation = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec)).terminated(true).build());
        final EquipmentRentalSpec rentalSpec = EquipmentRentalSpecFixture.builder().reservationId(reservation.getId()).build();
        rentalSpecRepository.saveAll(List.of(rentalSpec));

        // when
        final List<RentalSpecWithName> result = rentalSpecRepository.findTerminatedWithNameByPropertyNumber(rentalSpec.getPropertyNumber());

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0)).usingRecursiveComparison()
                        .isEqualTo(new RentalSpecWithName(
                                reservation.getName(), rentalSpec.getAcceptDateTime(), rentalSpec.getReturnDateTime(), rentalSpec.getStatus()))
        );
    }
}