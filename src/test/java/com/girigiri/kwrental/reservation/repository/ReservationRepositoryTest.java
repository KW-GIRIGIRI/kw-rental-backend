package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.config.JpaConfig;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.repository.dto.ReservationWithMemberNumber;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.MemberFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private MemberRepository memberRepository;


    @Test
    @DisplayName("대여 수령일로 대여 예약을 조회")
    void findReservationsWithSpecsByStartDate() {
        // given
        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().modelName("test1").build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().modelName("test2").build());
        final Member member = memberRepository.save(MemberFixture.create());

        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(LocalDate.now(), LocalDate.now().plusDays(1))).build();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).memberId(member.getId()).build());

        final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))).build();
        final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))).build();
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.create(List.of(reservationSpec3, reservationSpec4)));

        // when
        final Set<ReservationWithMemberNumber> expect = reservationRepository.findReservationsWithSpecsByStartDate(LocalDate.now());

        // then
        assertThat(expect).usingRecursiveFieldByFieldElementComparator().containsExactly(new ReservationWithMemberNumber(reservation1, member.getMemberNumber()));
    }

    @Test
    @DisplayName("반납이 지연된 대여 예약을 회웑 정보와 함께 조회")
    void findOverdueReservationWithSpecs() {
        // given
        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().modelName("test1").build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().modelName("test2").build());

        final LocalDate now = LocalDate.now();
        final LocalDate start = now.minusDays(1);
        final Member member = memberRepository.save(MemberFixture.create());
        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(start, now.minusDays(1))).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(start, now.minusDays(1))).build();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).memberId(member.getId()).build());
        final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(start, now)).build();
        final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(start, now)).build();
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec3, reservationSpec4)).terminated(true).build());

        // when
        final Set<ReservationWithMemberNumber> expect = reservationRepository.findOverdueReservationWithSpecs(now);

        // then
        assertThat(expect).usingRecursiveFieldByFieldElementComparator().containsExactly(new ReservationWithMemberNumber(reservation1, member.getMemberNumber()));
    }

    @Test
    @DisplayName("반납일로 대여 예약을 조회")
    void findReservationsWithSpecsByEndDate() {
        // given
        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().modelName("test1").build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().modelName("test2").build());

        final LocalDate now = LocalDate.now();
        final LocalDate start = now.minusDays(1);
        final Member member = memberRepository.save(MemberFixture.create());

        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(start, now)).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(start, now)).build();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2)).memberId(member.getId()).build());

        final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(start, now.plusDays(2))).build();
        final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(start, now.plusDays(2))).build();
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.create(List.of(reservationSpec3, reservationSpec4)));

        // when
        final Set<ReservationWithMemberNumber> expect = reservationRepository.findReservationsWithSpecsByEndDate(now);

        // then
        assertThat(expect).usingRecursiveFieldByFieldElementComparator().containsExactly(new ReservationWithMemberNumber(reservation1, member.getMemberNumber()));
    }
}