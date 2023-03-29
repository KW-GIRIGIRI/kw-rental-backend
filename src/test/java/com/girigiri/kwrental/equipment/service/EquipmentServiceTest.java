package com.girigiri.kwrental.equipment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.dto.request.EquipmentSearchCondition;
import com.girigiri.kwrental.equipment.dto.response.EquipmentDetailResponse;
import com.girigiri.kwrental.equipment.dto.response.SimpleEquipmentWithRentalQuantityResponse;
import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.testsupport.CleanBeforeEach;
import com.girigiri.kwrental.testsupport.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SpringBootTest
@CleanBeforeEach
class EquipmentServiceTest {

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
                .isEqualTo(EquipmentDetailResponse.from(equipment));
    }

    @Test
    @DisplayName("등록되지 않은 기자재를 조회하려면 예외가 발생한다.")
    void findById_exception_notFound() {
        // given, when, then
        assertThatThrownBy(() -> equipmentService.findById(1L))
                .isExactlyInstanceOf(EquipmentNotFoundException.class);
    }

    @Test
    @DisplayName("등록된 기자재들을 페이지로 조건 없이 조회할 수 있다.")
    void findEquipmentsBy() {
        // given
        equipmentRepository.save(TestFixtures.createEquipment());
        final Equipment equipment = equipmentRepository.save(TestFixtures.createEquipment());
        equipmentRepository.save(TestFixtures.createEquipment());

        // when
        final Page<SimpleEquipmentWithRentalQuantityResponse> expect = equipmentService.findEquipmentsWIthRentalQuantityBy(
                PageRequest.of(1, 1, Sort.by("id").descending()), new EquipmentSearchCondition(null, null));

        // then
        assertAll(
                () -> assertThat(expect.hasNext()).isTrue(),
                () -> assertThat(expect.hasPrevious()).isTrue(),
                () -> assertThat(expect.getContent()).usingRecursiveFieldByFieldElementComparator()
                        .containsExactly(SimpleEquipmentWithRentalQuantityResponse.from(equipment))
        );
    }
}
