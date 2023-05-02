package com.girigiri.kwrental.rental.repository;

import com.girigiri.kwrental.config.JpaConfig;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.RentalSpecFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Import(JpaConfig.class)
class RentalSpecRepositoryTest {

    @Autowired
    private RentalSpecRepository rentalSpecRepository;
    @Autowired
    private ReservationSpecRepository reservationSpecRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    @DisplayName("대여 상세를 모두 저장한다.")
    void saveAll() {
        // given
        final RentalSpec rentalSpec1 = RentalSpecFixture.builder().acceptDateTime(null).propertyNumber("12345678").build();
        final RentalSpec rentalSpec2 = RentalSpecFixture.builder().acceptDateTime(null).propertyNumber("87654321").build();

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
        final LocalDateTime acceptTime = LocalDateTime.now();
        final LocalDateTime returnTime = LocalDateTime.now();
        final Equipment equipment1 = equipmentRepository.save(EquipmentFixture.builder().modelName("modelName1").build());
        final Equipment equipment2 = equipmentRepository.save(EquipmentFixture.builder().modelName("modelName2").build());
        final ReservationSpec reservationSpec1 = reservationSpecRepository.save(ReservationSpecFixture.builder(equipment1).build());
        final ReservationSpec reservationSpec2 = reservationSpecRepository.save(ReservationSpecFixture.builder(equipment2).build());

        final RentalSpec rentalSpec1 = RentalSpecFixture.builder().acceptDateTime(acceptTime).propertyNumber("11111111").reservationSpecId(reservationSpec1.getId()).build();
        final RentalSpec rentalSpec2 = RentalSpecFixture.builder().acceptDateTime(acceptTime).returnDateTime(returnTime).propertyNumber("22222222").reservationSpecId(reservationSpec1.getId()).build();
        final RentalSpec rentalSpec3 = RentalSpecFixture.builder().acceptDateTime(acceptTime).propertyNumber("33333333").reservationSpecId(reservationSpec2.getId()).build();

        rentalSpecRepository.saveAll(List.of(rentalSpec1, rentalSpec2, rentalSpec3));

        // when
        final Set<RentalSpec> rentedRentalSpecs = rentalSpecRepository.findRentedRentalSpecs(equipment1.getId(), LocalDateTime.now());

        // then
        assertThat(rentedRentalSpecs).containsExactlyInAnyOrder(rentalSpec1);
    }
}