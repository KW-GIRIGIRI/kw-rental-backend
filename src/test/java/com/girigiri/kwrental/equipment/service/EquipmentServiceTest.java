package com.girigiri.kwrental.equipment.service;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.dto.request.AddEquipmentRequest;
import com.girigiri.kwrental.equipment.dto.request.AddEquipmentWithItemsRequest;
import com.girigiri.kwrental.equipment.dto.request.AddItemRequest;
import com.girigiri.kwrental.equipment.dto.request.EquipmentSearchCondition;
import com.girigiri.kwrental.equipment.dto.response.EquipmentDetailResponse;
import com.girigiri.kwrental.equipment.dto.response.SimpleEquipmentResponse;
import com.girigiri.kwrental.equipment.dto.response.SimpleEquipmentWithRentalQuantityResponse;
import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.equipment.exception.InvalidCategoryException;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EquipmentService equipmentService;

    @Test
    @DisplayName("등록된 기자재를 조회할 수 있다.")
    void findById() {
        // given
        final Equipment equipment = EquipmentFixture.create();
        final long id = 1L;
        given(equipmentRepository.findById(id)).willReturn(Optional.of(equipment));

        // when
        final EquipmentDetailResponse response = equipmentService.findById(id);

        // then
        assertThat(response).usingRecursiveComparison().ignoringFields("id")
                .isEqualTo(EquipmentDetailResponse.from(equipment));
    }

    @Test
    @DisplayName("등록되지 않은 기자재를 조회하려면 예외가 발생한다.")
    void findById_exception_notFound() {
        // given
        given(equipmentRepository.findById(1L)).willThrow(EquipmentNotFoundException.class);

        // when, then
        assertThatThrownBy(() -> equipmentService.findById(1L))
                .isExactlyInstanceOf(EquipmentNotFoundException.class);
    }

    // TODO: 2023/03/29  대여 가능 횟수 로직이 포함되지 않았다.
    @Test
    @DisplayName("등록된 기자재들을 대여 가능 횟수를 포함해서 페이지로 조건 없이 조회할 수 있다.")
    void findEquipmentsWithRentalQuantityBy() {
        // given
        final PageRequest pageable = PageRequest.of(1, 1, Sort.by("id").descending());

        final Equipment equipment = EquipmentFixture.builder().id(2L).build();
        given(equipmentRepository.findEquipmentBy(any(), any(), any()))
                .willReturn(new PageImpl<>(List.of(equipment), pageable, 3));

        // when
        final Page<SimpleEquipmentWithRentalQuantityResponse> expect = equipmentService.findEquipmentsWithRentalQuantityBy(
                pageable, new EquipmentSearchCondition(null, null));

        // then
        assertAll(
                () -> assertThat(expect.hasNext()).isTrue(),
                () -> assertThat(expect.hasPrevious()).isTrue(),
                () -> assertThat(expect.getContent()).usingRecursiveFieldByFieldElementComparator()
                        .containsExactly(SimpleEquipmentWithRentalQuantityResponse.from(equipment))
        );
    }

    @Test
    @DisplayName("등록된 기자재들을 페이지로 조건 없이 조회할 수 있다.")
    void findEquipmentsBy() {
        // given
        final PageRequest pageable = PageRequest.of(1, 1, Sort.by("id").descending());

        final Equipment equipment = EquipmentFixture.builder().id(2L).build();
        given(equipmentRepository.findEquipmentBy(any(), any(), any()))
                .willReturn(new PageImpl<>(List.of(equipment), pageable, 3));

        // when
        final Page<SimpleEquipmentResponse> expect = equipmentService.findEquipments(
                pageable, new EquipmentSearchCondition(null, null));

        // then
        assertAll(
                () -> assertThat(expect.hasNext()).isTrue(),
                () -> assertThat(expect.hasPrevious()).isTrue(),
                () -> assertThat(expect.getContent()).usingRecursiveFieldByFieldElementComparator()
                        .containsExactly(SimpleEquipmentResponse.from(equipment))
        );
    }

    @Test
    @DisplayName("기자재 저장 API")
    void saveEquipment() {
        // given
        AddEquipmentRequest addEquipmentRequest = new AddEquipmentRequest("rentalPlace", "modelName", "CAMERA",
                "maker", "imgUrl", "component", "purpose", "description", 1, 2);
        final AddItemRequest addItemRequest = new AddItemRequest("propertyNumber");
        final AddEquipmentWithItemsRequest request = new AddEquipmentWithItemsRequest(addEquipmentRequest,
                List.of(addItemRequest));
        final Equipment equipment = EquipmentFixture.builder().id(1L).build();
        given(equipmentRepository.save(any())).willReturn(equipment);

        // when
        final Long id = equipmentService.saveEquipment(request);

        // then
        assertThat(id).isOne();
        verify(equipmentRepository).save(any());
        verify(itemService).saveItems(any(), any());
    }

    @Test
    @DisplayName("기자재 저장에서 잘못된 카테고리 예외 처리")
    void saveEquipment_invalidCategory() {
        // given
        AddEquipmentRequest addEquipmentRequest = new AddEquipmentRequest(
                "rentalPlace", "modelName", "invalidCategory",
                "maker", "imgUrl", "component",
                "purpose", "description", 1, 1);
        final AddItemRequest addItemRequest = new AddItemRequest("propertyNumber");
        final AddEquipmentWithItemsRequest request = new AddEquipmentWithItemsRequest(addEquipmentRequest,
                List.of(addItemRequest));

        // when, then
        assertThatThrownBy(() -> equipmentService.saveEquipment(request))
                .isExactlyInstanceOf(InvalidCategoryException.class);
    }

    @Test
    @DisplayName("기자재 삭제")
    void deleteEquipment() {
        // given
        given(equipmentRepository.findById(1L)).willReturn(Optional.of(EquipmentFixture.create()));

        // when
        equipmentService.deleteEquipment(1L);

        // then
        verify(equipmentRepository).findById(1L);
        verify(equipmentRepository).deleteById(1L);
        verify(eventPublisher).publishEvent(any(EquipmentDeleteEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 기자재 삭제 예외 처리")
    void deleteEquipment_notFound() {
        // given
        given(equipmentRepository.findById(1L)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> equipmentService.deleteEquipment(1L))
                .isExactlyInstanceOf(EquipmentNotFoundException.class);
    }
}
