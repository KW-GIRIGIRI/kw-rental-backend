package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.asset.repository.AssetRepository;
import com.girigiri.kwrental.config.JpaConfig;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpecStatus;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.LabRoomFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    private AssetRepository assetRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("특정 회원의 완료되지 않은 기자재 대여를 조회한다.")
    void findNotTerminatedReservationsByMemberId() {
        // given
        final Rentable equipment1 = assetRepository.save(EquipmentFixture.builder().name("test1").build());
        final Rentable labRoom = assetRepository.save(LabRoomFixture.builder().name("test2").build());

        final LocalDate now = LocalDate.now();
        final LocalDate start = now.minusDays(1);

        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(start, now)).build();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1)).memberId(1L).build());
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(labRoom).period(new RentalPeriod(start, now)).build();
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec2)).memberId(1L).build());

        final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(start, now.plusDays(2))).status(ReservationSpecStatus.RETURNED).build();
        final Reservation reservation3 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec3)).terminated(true).memberId(1L).build());
        final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(labRoom).period(new RentalPeriod(start, now.plusDays(2))).status(ReservationSpecStatus.RETURNED).build();
        final Reservation reservation4 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec4)).terminated(true).memberId(1L).build());

        final ReservationSpec reservationSpec5 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(start, now.plusDays(2))).status(ReservationSpecStatus.RETURNED).build();
        final Reservation reservation5 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec5)).memberId(2L).build());
        final ReservationSpec reservationSpec6 = ReservationSpecFixture.builder(labRoom).period(new RentalPeriod(start, now.plusDays(2))).status(ReservationSpecStatus.RETURNED).build();
        final Reservation reservation6 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec6)).memberId(2L).build());

        // when
        entityManager.clear();
        final Set<Reservation> actual = reservationRepository.findNotTerminatedEquipmentReservationsByMemberId(1L);

        // then
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(reservation1);
    }

    @Test
    @DisplayName("특정 회원의 완료되지 않은 기자재 대여를 조회한다.")
    void findNotTerminatedLabRoomReservationsByMemberId() {
        // given
        final Rentable equipment1 = assetRepository.save(EquipmentFixture.builder().name("test1").build());
        final Rentable labRoom = assetRepository.save(LabRoomFixture.builder().name("test2").build());

        final LocalDate now = LocalDate.now();
        final LocalDate start = now.minusDays(1);

        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(start, now)).build();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1)).memberId(1L).build());
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(labRoom).period(new RentalPeriod(start, now)).build();
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec2)).memberId(1L).build());

        final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(start, now.plusDays(2))).status(ReservationSpecStatus.RETURNED).build();
        final Reservation reservation3 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec3)).terminated(true).memberId(1L).build());
        final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(labRoom).period(new RentalPeriod(start, now.plusDays(2))).status(ReservationSpecStatus.RETURNED).build();
        final Reservation reservation4 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec4)).terminated(true).memberId(1L).build());

        final ReservationSpec reservationSpec5 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(start, now.plusDays(2))).status(ReservationSpecStatus.RETURNED).build();
        final Reservation reservation5 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec5)).memberId(2L).build());
        final ReservationSpec reservationSpec6 = ReservationSpecFixture.builder(labRoom).period(new RentalPeriod(start, now.plusDays(2))).status(ReservationSpecStatus.RETURNED).build();
        final Reservation reservation6 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec6)).memberId(2L).build());

        // when
        entityManager.clear();
        final Set<Reservation> actual = reservationRepository.findNotTerminatedLabRoomReservationsByMemberId(1L);

        // then
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(reservation2);
    }


    @Test
    @DisplayName("대여 예약의 종결 여부를 업데이트 한다.")
    void adjustTerminated() {
        // given
        final Rentable equipment = assetRepository.save(EquipmentFixture.create());
        final ReservationSpec reservationSpec = ReservationSpecFixture.builder(equipment).status(ReservationSpecStatus.CANCELED).build();
        final Reservation reservation = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec)).terminated(false).build());
        reservation.updateIfTerminated();

        // when
        reservationRepository.adjustTerminated(reservation);
        entityManager.detach(reservation);
        final Reservation actual = reservationRepository.findById(reservation.getId()).orElseThrow();

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(reservation);
    }
}