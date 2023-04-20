package com.girigiri.kwrental.reservation.repository;

import com.girigiri.kwrental.config.JpaConfig;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.ReservedAmount;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.RentalSpecFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Import(JpaConfig.class)
class RentalSpecRepositoryCustomImplTest {

    public static final LocalDate NOW = LocalDate.now();
    @Autowired
    private RentalSpecRepository rentalSpecRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

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
                .forEach(it -> rentalSpecRepository.save(RentalSpecFixture.builder(equipment).period(it).build()));

        // when
        final RentalPeriod period = new RentalPeriod(NOW.plusDays(1), NOW.plusDays(4));
        final List<RentalSpec> expect = rentalSpecRepository.findOverlappedByPeriod(equipment.getId(), period);

        // then
        assertThat(expect).usingRecursiveFieldByFieldElementComparator()
                .extracting(RentalSpec::getPeriod)
                .containsExactlyInAnyOrder(overlappedLeft, overlappedMid, overlappedRight, overlappedBoth);
    }

    @Test
    @DisplayName("기자재들의 특정 날짜에 대여 예약된 갯수를 구한다.")
    void findRentalAmountsByEquipmentIds() {
        // given
        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().modelName("모델이름1").totalQuantity(4).build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().modelName("모델이름2").totalQuantity(4).build());

        final RentalSpec.RentalSpecBuilder rentalSpec1Builder = RentalSpecFixture.builder(equipment1);
        final RentalSpec rentalSpec1 = rentalSpec1Builder.amount(new RentalAmount(2)).period(new RentalPeriod(NOW, NOW.plusDays(2))).build();
        final RentalSpec rentalSpec2 = rentalSpec1Builder.amount(new RentalAmount(1)).period(new RentalPeriod(NOW, NOW.plusDays(1))).build();
        final RentalSpec rentalSpec3 = rentalSpec1Builder.amount(new RentalAmount(1)).period(new RentalPeriod(NOW.plusDays(1), NOW.plusDays(2))).build();
        rentalSpecRepository.save(rentalSpec1);
        rentalSpecRepository.save(rentalSpec2);
        rentalSpecRepository.save(rentalSpec3);

        // when
        final List<ReservedAmount> expect = rentalSpecRepository.findRentalAmountsByEquipmentIds(List.of(equipment1.getId(), equipment2.getId()), NOW);

        // then
        final ReservedAmount actual = new ReservedAmount(equipment1.getId(), 4, 3);
        assertAll(
                () -> assertThat(expect).hasSize(1),
                () -> assertThat(expect.get(0)).usingRecursiveComparison().isEqualTo(actual)
        );
    }
}