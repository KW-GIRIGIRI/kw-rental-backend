package com.girigiri.kwrental.equipment.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.girigiri.kwrental.asset.dto.response.RemainQuantitiesPerDateResponse;
import com.girigiri.kwrental.asset.dto.response.RemainQuantityPerDateResponse;
import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.asset.service.RemainingQuantityService;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.dto.request.AddEquipmentRequest;
import com.girigiri.kwrental.equipment.dto.request.AddEquipmentWithItemsRequest;
import com.girigiri.kwrental.equipment.dto.request.AddItemRequest;
import com.girigiri.kwrental.equipment.dto.request.EquipmentSearchCondition;
import com.girigiri.kwrental.equipment.dto.request.UpdateEquipmentRequest;
import com.girigiri.kwrental.equipment.dto.response.EquipmentDetailResponse;
import com.girigiri.kwrental.equipment.dto.response.SimpleEquipmentResponse;
import com.girigiri.kwrental.equipment.dto.response.SimpleEquipmentWithRentalQuantityResponse;
import com.girigiri.kwrental.equipment.exception.EquipmentException;
import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.equipment.exception.InvalidCategoryException;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private SaveItemService saveItemService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private RemainingQuantityService remainingQuantityService;

    @Mock
    private AssetService assetService;

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

    @Test
    @DisplayName("등록된 기자재들을 대여 가능 횟수를 포함해서 페이지로 조건 없이 조회할 수 있다.")
    void findEquipmentsWithRentalQuantityBy() {
        // given
        final PageRequest pageable = PageRequest.of(1, 1, Sort.by("id").descending());

        final Equipment equipment = EquipmentFixture.builder().id(2L).build();
        given(equipmentRepository.findEquipmentBy(any(), any(), any()))
                .willReturn(new PageImpl<>(List.of(equipment), pageable, 3));
        given(remainingQuantityService.getRemainingQuantityByEquipmentIdAndDate(any(), any()))
                .willReturn(Map.of(equipment.getId(), equipment.getTotalQuantity()));

        // when
        final Page<SimpleEquipmentWithRentalQuantityResponse> expect = equipmentService.findEquipmentsWithRentalQuantityBy(
                pageable, new EquipmentSearchCondition(null, null, null));

        // then
        assertAll(
                () -> assertThat(expect.hasNext()).isTrue(),
                () -> assertThat(expect.hasPrevious()).isTrue(),
                () -> assertThat(expect.getContent()).usingRecursiveFieldByFieldElementComparator()
                        .containsExactly(SimpleEquipmentWithRentalQuantityResponse.from(equipment, equipment.getTotalQuantity()))
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
                pageable, new EquipmentSearchCondition(null, null, null));

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
        verify(saveItemService).saveItems(any(), any());
    }

    @Test
    @DisplayName("기자재 저장에서 잘못된 카테고리 예외")
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
    @DisplayName("존재하지 않는 기자재 삭제 예외")
    void deleteEquipment_notFound() {
        // given
        given(equipmentRepository.findById(1L)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> equipmentService.deleteEquipment(1L))
                .isExactlyInstanceOf(EquipmentNotFoundException.class);
    }

    @Test
    @DisplayName("기자재 수정 ")
    void updateEquipment() {
        // given
        Equipment equipment = EquipmentFixture.create();
        given(equipmentRepository.findById(any())).willReturn(Optional.of(equipment));
        UpdateEquipmentRequest updateEquipmentRequest = new UpdateEquipmentRequest(
                "updatedDays", "updatedName",
                "ETC", "updatedMaker", "updatedImgUrl",
                "updatedComponent", "updatedPurpose", "updatedDescription", 2, 2);

        // when
        EquipmentDetailResponse expect = equipmentService.update(1L, updateEquipmentRequest);

        // then
        assertThat(expect).usingRecursiveComparison()
                .isEqualTo(EquipmentDetailResponse.from(equipment));
    }

    @Test
    @DisplayName("존재하지 않은 기자재 수정 예외")
    void updateEquipment_notFound() {
        // given
        given(equipmentRepository.findById(any())).willReturn(Optional.empty());
        UpdateEquipmentRequest updateEquipmentRequest = new UpdateEquipmentRequest(
                "rentalDays", "modelName",
                "CAMERA", "maker", "imgUrl",
                "component", "purpose", "description", 1, 2);

        // when, then
        assertThatThrownBy(() -> equipmentService.update(1L, updateEquipmentRequest))
                .isExactlyInstanceOf(EquipmentNotFoundException.class);
    }

    @Test
    @DisplayName("가능한 대여일 수 인지 검증할 때 존재하지 않은 기자재 예외")
    void validateRentalDays_notFound() {
        // given
        given(equipmentRepository.findById(any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> equipmentService.validateRentalDays(1L, 1))
                .isExactlyInstanceOf(EquipmentNotFoundException.class);
    }

    @Test
    @DisplayName("최대 대여일보다 긴 기간은 예외가 발생")
    void validateRentalDays_invalid() {
        // given
        final Equipment equipment = EquipmentFixture.builder().maxRentalDays(1).build();
        given(equipmentRepository.findById(any())).willReturn(Optional.of(equipment));

        // when, then
        assertThatThrownBy(() -> equipmentService.validateRentalDays(1L, 2))
                .isExactlyInstanceOf(EquipmentException.class);
    }

    @Test
    @DisplayName("최대 대여일보다 짧거나 같은 기간은 검증을 통과")
    void validateRentalDays() {
        // given
        final Equipment equipment = EquipmentFixture.builder().maxRentalDays(1).build();
        given(equipmentRepository.findById(any())).willReturn(Optional.of(equipment));

        // when, then
        assertThatCode(() -> equipmentService.validateRentalDays(1L, 1))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("특정 기자재의 날짜별 대여 가능 갯수 조회")
    void getRemainQuantitiesPerDate() {
        // given
        final Equipment equipment = EquipmentFixture.builder().totalQuantity(10).build();
        final LocalDate now = LocalDate.now();
        given(equipmentRepository.findById(any())).willReturn(Optional.of(equipment));
        final Map<LocalDate, Integer> reservedAmounts = Map.of(now, 10, now.plusDays(1), 5);
	    given(remainingQuantityService.getReservedAmountInclusive(any(), any(), any()))
                .willReturn(reservedAmounts);
        given(assetService.getReservableCountPerDate(reservedAmounts, equipment))
                .willReturn(new RemainQuantitiesPerDateResponse(List.of(new RemainQuantityPerDateResponse(now, 0), new RemainQuantityPerDateResponse(now.plusDays(1), 5))));

        // when
        final RemainQuantitiesPerDateResponse remainQuantitiesPerDate = equipmentService.getRemainQuantitiesPerDate(1L, now, now.plusDays(1));

        // then
        assertThat(remainQuantitiesPerDate.getRemainQuantities()).usingRecursiveFieldByFieldElementComparator()
                .containsExactly(new RemainQuantityPerDateResponse(now, 0), new RemainQuantityPerDateResponse(now.plusDays(1), 5));
    }
}
