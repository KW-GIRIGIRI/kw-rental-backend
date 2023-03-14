package com.girigiri.kwrental.equipment.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.girigiri.kwrental.TestFixtures;
import com.girigiri.kwrental.config.JpaConfig;
import com.girigiri.kwrental.equipment.domain.Equipment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest
@Import(JpaConfig.class)
class EquipmentRepositoryTest {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    @DisplayName("기자재를 모델 이름으로 검색해서 목록 조회한다.")
    void findEquipmentBy() {
        // given
        final Equipment equipment1 = TestFixtures.getEquipmentBuilder().modelName("key").build();
        final Equipment equipment2 = TestFixtures.getEquipmentBuilder().modelName("akey").build();
        final Equipment equipment3 = TestFixtures.getEquipmentBuilder().modelName("keyb").build();
        final Equipment equipment4 = TestFixtures.getEquipmentBuilder().modelName("akeyb").build();
        final Equipment equipment5 = TestFixtures.getEquipmentBuilder().modelName("notForSearch").build();
        equipmentRepository.save(equipment1);
        equipmentRepository.save(equipment2);
        equipmentRepository.save(equipment3);
        equipmentRepository.save(equipment4);
        equipmentRepository.save(equipment5);

        final Page<Equipment> equipmentsPage = equipmentRepository.findEquipmentBy(
                PageRequest.of(0, 2, Sort.by("id").descending()), "key");

        assertAll(
                () -> assertThat(equipmentsPage.getTotalPages()).isEqualTo(2),
                () -> assertThat(equipmentsPage.getTotalElements()).isEqualTo(4),
                () -> assertThat(equipmentsPage.getContent()).containsExactly(equipment4, equipment3)
        );
    }
}
