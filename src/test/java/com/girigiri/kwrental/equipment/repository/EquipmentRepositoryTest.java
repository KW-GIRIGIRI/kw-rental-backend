package com.girigiri.kwrental.equipment.repository;

import static com.girigiri.kwrental.equipment.domain.Category.CAMERA;
import static com.girigiri.kwrental.equipment.domain.Category.ETC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.girigiri.kwrental.config.JpaConfig;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest
@Import(JpaConfig.class)
class EquipmentRepositoryTest {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    @DisplayName("기자재를 모델 이름과 카테고리로 검색해서 목록 조회한다.")
    void findEquipmentBy() {
        // given
        final Equipment equipment1 = EquipmentFixture.builder().modelName("key").category(CAMERA).build();
        final Equipment equipment2 = EquipmentFixture.builder().modelName("akey").category(CAMERA).build();
        final Equipment equipment3 = EquipmentFixture.builder().modelName("keyb").category(CAMERA).build();
        final Equipment equipment4 = EquipmentFixture.builder().modelName("akeyb").category(ETC).build();
        final Equipment equipment5 = EquipmentFixture.builder().modelName("notForSearch").category(CAMERA).build();
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

    @Test
    @DisplayName("중복된 모델이름으로 기자재를 등록하려면 예외가 발생한다.")
    void save_duplicatedModelName() {
        // given
        final Equipment equipment = EquipmentFixture.create();
        equipmentRepository.save(equipment);
        final Equipment duplicatedModelNameEquipment = EquipmentFixture.create();

        // when, then
        assertThatThrownBy(() -> equipmentRepository.save(duplicatedModelNameEquipment))
                .isExactlyInstanceOf(DataIntegrityViolationException.class);
    }
}
