package com.girigiri.kwrental.asset.equipment.repository;

import static com.girigiri.kwrental.asset.equipment.domain.Category.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.common.config.JpaConfig;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;

@DataJpaTest
@Import(JpaConfig.class)
class EquipmentRepositoryTest {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    @DisplayName("기자재를 모델 이름과 카테고리로 검색해서 목록 조회한다.")
    void findEquipmentBy() {
        // given
        final Equipment equipment1 = EquipmentFixture.builder().name("key").category(CAMERA).build();
        final Equipment equipment2 = EquipmentFixture.builder().name("akey").category(CAMERA).build();
        final Equipment equipment3 = EquipmentFixture.builder().name("keyb").category(CAMERA).build();
        final Equipment equipment4 = EquipmentFixture.builder().name("akeyb").category(ETC).build();
        final Equipment equipment5 = EquipmentFixture.builder().name("notForSearch").category(CAMERA).build();
        equipmentRepository.save(equipment1);
        equipmentRepository.save(equipment2);
        equipmentRepository.save(equipment3);
        equipmentRepository.save(equipment4);
        equipmentRepository.save(equipment5);

        final Page<Equipment> equipmentsPage = equipmentRepository.findEquipmentBy(
                PageRequest.of(0, 2, Sort.by("id").descending()), "key", CAMERA);

        assertAll(
                () -> assertThat(equipmentsPage.getTotalPages()).isEqualTo(2),
                () -> assertThat(equipmentsPage.getTotalElements()).isEqualTo(3),
                () -> assertThat(equipmentsPage.getContent()).containsExactly(equipment3, equipment2)
        );
    }
}
