package com.girigiri.kwrental.equipment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.girigiri.kwrental.acceptance.TestFixtures;
import com.girigiri.kwrental.equipment.Equipment;
import com.girigiri.kwrental.equipment.EquipmentRepository;
import com.girigiri.kwrental.equipment.dto.EquipmentResponse;
import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.support.ResetDatabaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EquipmentServiceTest extends ResetDatabaseTest {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EquipmentService equipmentService;

    @Test
    @DisplayName("등록된 기자재를 조회할 수 있다.")
    void findById() {
        // given
        final Equipment equipment = equipmentRepository.save(TestFixtures.createEquipment());

        // when
        final EquipmentResponse response = equipmentService.findById(equipment.getId());

        // then
        assertThat(response).usingRecursiveComparison().ignoringFields("id")
                .isEqualTo(TestFixtures.createEquipmentResponse());
    }

    @Test
    @DisplayName("등록되지 않은 기자재를 조회하려면 예외가 발생한다.")
    void findById_exception_notFound() {
        // given, when, then
        assertThatThrownBy(() -> equipmentService.findById(1L))
                .isExactlyInstanceOf(EquipmentNotFoundException.class);
    }
}
