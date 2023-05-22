package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.config.JpaConfig;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpec.ReservationSpecBuilder;
import com.girigiri.kwrental.reservation.domain.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.repository.dto.ReservedAmount;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.MemberFixture;
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
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Import(JpaConfig.class)
class ReservationSpecRepositoryCustomImplTest {

    public static final LocalDate NOW = LocalDate.now();

    @Autowired
    private ReservationSpecRepository reservationSpecRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("특정 기간과 겹치는 대여 사항을 조회한다.")
    void findOverlappedByPeriod() {
        // given
        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        final RentalPeriod notOverlappedLeft = new RentalPeriod(NOW, NOW.plusDays(1));
        final RentalPeriod overlappedLeft = new RentalPeriod(NOW, NOW.plusDays(5));
        final RentalPeriod overlappedMid = new RentalPeriod(NOW.plusDays(2), NOW.plusDays(3));
        final RentalPeriod overlappedRight = new RentalPeriod(NOW.plusDays(3), NOW.plusDays(8));
        final RentalPeriod notOverlappedRight = new RentalPeriod(NOW.plusDays(6), NOW.plusDays(8));
        final RentalPeriod overlappedBoth = new RentalPeriod(NOW, NOW.plusDays(10));


        List.of(notOverlappedLeft, overlappedLeft, overlappedMid, overlappedRight, notOverlappedRight, overlappedBoth)
                .forEach(it -> reservationSpecRepository.save(ReservationSpecFixture.builder(equipment).period(it).build()));

        // when
        final RentalPeriod period = new RentalPeriod(NOW.plusDays(1), NOW.plusDays(4));
        final List<ReservationSpec> expect = reservationSpecRepository.findOverlappedByPeriod(equipment.getId(), period);

        // then
        assertThat(expect).usingRecursiveFieldByFieldElementComparator()
                .extracting(ReservationSpec::getPeriod)
                .containsExactlyInAnyOrder(overlappedLeft, overlappedMid, overlappedRight, overlappedBoth);
    }

    @Test
    @DisplayName("기자재들의 특정 날짜에 대여 예약된 갯수를 구한다.")
    void findRentalAmountsByEquipmentIds() {
        // given
        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().name("모델이름1").totalQuantity(4).build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().name("모델이름2").totalQuantity(4).build());
        final Equipment equipment3 = equipmentRepository.save(EquipmentFixture.builder().name("모델이름3").totalQuantity(4).build());

        final ReservationSpecBuilder rentalSpec1Builder = ReservationSpecFixture.builder(equipment1);
        final ReservationSpec reservationSpec1 = rentalSpec1Builder.amount(RentalAmount.ofPositive(2)).period(new RentalPeriod(NOW, NOW.plusDays(2))).build();
        final ReservationSpec reservationSpec2 = rentalSpec1Builder.amount(RentalAmount.ofPositive(1)).period(new RentalPeriod(NOW, NOW.plusDays(1))).build();
        final ReservationSpec reservationSpec3 = rentalSpec1Builder.amount(RentalAmount.ofPositive(1)).period(new RentalPeriod(NOW.plusDays(1), NOW.plusDays(2))).build();
        reservationSpecRepository.save(reservationSpec1);
        reservationSpecRepository.save(reservationSpec2);
        reservationSpecRepository.save(reservationSpec3);

        // when
        final List<ReservedAmount> expect = reservationSpecRepository.findRentalAmountsByEquipmentIds(List.of(equipment1.getId(), equipment2.getId(), equipment3.getId()), NOW);

        // then
        final ReservedAmount reservedAmount1 = new ReservedAmount(equipment1.getId(), 4, 3);
        final ReservedAmount reservedAmount2 = new ReservedAmount(equipment2.getId(), 4, 0);
        final ReservedAmount reservedAmount3 = new ReservedAmount(equipment3.getId(), 4, 0);
        assertAll(
                () -> assertThat(expect).hasSize(3),
                () -> assertThat(expect.get(0)).usingRecursiveComparison().isEqualTo(reservedAmount1),
                () -> assertThat(expect.get(1)).usingRecursiveComparison().isEqualTo(reservedAmount2),
                () -> assertThat(expect.get(2)).usingRecursiveComparison().isEqualTo(reservedAmount3)
        );
    }

    @Test
    @DisplayName("특정 기간에 대여 수령하는 대여 상세를 조회한다.")
    void findByStartDateBetween() {
        // given
        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().name("model1").build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().name("model2").build());
        final YearMonth now = YearMonth.now();
        final ReservationSpec reservationSpec1 = reservationSpecRepository.save(ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(now.atDay(1), now.atEndOfMonth())).build());
        final ReservationSpec reservationSpec2 = reservationSpecRepository.save(ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(now.atEndOfMonth(), now.atEndOfMonth().plusDays(1))).build());
        final ReservationSpec reservationSpec3 = reservationSpecRepository.save(ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(now.atEndOfMonth().plusDays(1), now.atEndOfMonth().plusDays(2))).build());

        // when
        final List<ReservationSpec> expect = reservationSpecRepository.findByStartDateBetween(equipment1.getId(), now.atDay(1), now.atEndOfMonth());

        // then
        assertThat(expect).containsExactlyInAnyOrder(reservationSpec1);
    }

    @Test
    @DisplayName("대여 예약 상세의 갯수와 상태를 변경한다.")
    void adjustAmountAndStatus() {
        // given
        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().name("model1").build());
        final ReservationSpec reservationSpec = reservationSpecRepository.save(ReservationSpecFixture.builder(equipment1)
                .amount(RentalAmount.ofPositive(1)).status(ReservationSpecStatus.RESERVED).build());

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
    @DisplayName("")
    void findEquipmentReservationForAccept() {
        // given
        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().name("test1").build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().name("test2").build());
        final Equipment equipment3 = equipmentRepository.save(EquipmentFixture.builder().name("test3").build());
        final Member member = memberRepository.save(MemberFixture.create());

        final ReservationSpec reservationSpec1 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(NOW, NOW.plusDays(1))).status(ReservationSpecStatus.RESERVED).build();
        final ReservationSpec reservationSpec2 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(NOW, NOW.plusDays(1))).status(ReservationSpecStatus.RENTED).build();
        final ReservationSpec reservationSpec3 = ReservationSpecFixture.builder(equipment3).period(new RentalPeriod(NOW, NOW.plusDays(1))).status(ReservationSpecStatus.CANCELED).build();
        final Reservation reservation1 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec1, reservationSpec2, reservationSpec3)).memberId(member.getId()).build());

        final ReservationSpec reservationSpec4 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(NOW.plusDays(1), NOW.plusDays(2))).build();
        final ReservationSpec reservationSpec5 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(NOW.plusDays(1), NOW.plusDays(2))).build();
        final Reservation reservation2 = reservationRepository.save(ReservationFixture.create(List.of(reservationSpec4, reservationSpec5)));

        final ReservationSpec reservationSpec6 = ReservationSpecFixture.builder(equipment1).period(new RentalPeriod(NOW, NOW.plusDays(1))).status(ReservationSpecStatus.RESERVED).build();
        final ReservationSpec reservationSpec7 = ReservationSpecFixture.builder(equipment2).period(new RentalPeriod(NOW, NOW.plusDays(1))).status(ReservationSpecStatus.RENTED).build();
        final Reservation reservation3 = reservationRepository.save(ReservationFixture.builder(List.of(reservationSpec6, reservationSpec7)).memberId(member.getId()).build());

        // when
        final Set<EquipmentReservationWithMemberNumber> actual = reservationSpecRepository.findEquipmentReservationWhenAccept(ReservationSpecRepositoryCustomImplTest.NOW);

        // then
        assertThat(actual).usingRecursiveFieldByFieldElementComparatorIgnoringFields()
                .containsExactlyInAnyOrder(new EquipmentReservationWithMemberNumber(reservation1.getName(), member.getMemberNumber(), reservation1.getAcceptDateTime(), List.of(reservationSpec1, reservationSpec2))
                        , new EquipmentReservationWithMemberNumber(reservation3.getName(), member.getMemberNumber(), reservation3.getAcceptDateTime(), List.of(reservationSpec6, reservationSpec7)));
    }
}