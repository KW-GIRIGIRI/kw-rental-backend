package com.girigiri.kwrental.equipment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.girigiri.kwrental.acceptance.TestFixtures;
import com.girigiri.kwrental.equipment.EquipmentRepository;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.dto.EquipmentDetailResponse;
import com.girigiri.kwrental.equipment.dto.EquipmentResponse;
import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.support.ResetDatabaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

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
        final EquipmentDetailResponse response = equipmentService.findById(equipment.getId());

        // then
        assertThat(response).usingRecursiveComparison().ignoringFields("id")
                .isEqualTo(TestFixtures.createEquipmentDetailResponse());
    }

    @Test
    @DisplayName("등록되지 않은 기자재를 조회하려면 예외가 발생한다.")
    void findById_exception_notFound() {
        // given, when, then
        assertThatThrownBy(() -> equipmentService.findById(1L))
                .isExactlyInstanceOf(EquipmentNotFoundException.class);
    }

    @Test
    @DisplayName("등록된 기자재들을 페이지로 조회할 수 있다.")
    void findEquipmentsBy() {
        // given
        equipmentRepository.save(TestFixtures.createEquipment());
        equipmentRepository.save(TestFixtures.createEquipment());
        equipmentRepository.save(TestFixtures.createEquipment());

        // when
        final Slice<EquipmentResponse> expect = equipmentService.findEquipmentsBy(
                PageRequest.of(1, 1, Sort.by("id").descending()));

        // then
        assertAll(
                () -> assertThat(expect.hasNext()).isTrue(),
                () -> assertThat(expect.hasPrevious()).isTrue(),
                () -> assertThat(expect.getContent()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                        .containsExactly(TestFixtures.createEquipmentResponse())
        );
    }
}
