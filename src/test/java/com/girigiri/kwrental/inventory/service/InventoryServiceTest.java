package com.girigiri.kwrental.inventory.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.equipment.service.EquipmentService;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.inventory.dto.request.AddInventoryRequest;
import com.girigiri.kwrental.inventory.dto.request.UpdateInventoryRequest;
import com.girigiri.kwrental.inventory.dto.response.InventoryResponse;
import com.girigiri.kwrental.inventory.exception.InventoryInvalidAccessException;
import com.girigiri.kwrental.inventory.exception.InventoryNotFoundException;
import com.girigiri.kwrental.inventory.repository.InventoryRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.InventoryFixture;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private AmountValidator amountValidator;

    @Mock
    private EquipmentService equipmentService;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    @DisplayName("담은 기자재를 저장한다")
    void saveInventory() {
        // given
        given(inventoryRepository.findByPeriodAndEquipmentIdAndMemberId(any(), any(), any()))
                .willReturn(Optional.empty());
        doNothing().when(amountValidator).validateAmount(any(), any(), any());
        final Equipment equipment = EquipmentFixture.builder().id(1L).build();
        given(equipmentService.validateRentalDays(any(), any())).willReturn(equipment);
        given(inventoryRepository.save(any())).willReturn(InventoryFixture.builder(equipment).id(1L).build());
        final AddInventoryRequest addInventoryRequest = AddInventoryRequest.builder()
                .equipmentId(1L)
                .amount(1)
                .rentalStartDate(LocalDate.now().plusDays(1))
                .rentalEndDate(LocalDate.now().plusDays(2))
                .build();

        // when
        Long id = inventoryService.save(1L, addInventoryRequest);

        // then
        assertThat(id).isNotNull();
    }

    @Test
    @DisplayName("담은 기자재를 저장하려고 할 때 기간과 기자재 ID가 동일한 담은 기자재가 존재하면 갯수를 추가해서 저장")
    void saveInventory_update() {
        // given
        final Equipment equipment = EquipmentFixture.builder().id(1L).build();
        final LocalDate now = LocalDate.now();
        final Inventory inventory = InventoryFixture.builder(equipment).id(1L).memberId(1L)
                .rentalPeriod(new RentalPeriod(now.plusDays(1), now.plusDays(2))).build();
        given(inventoryRepository.findByPeriodAndEquipmentIdAndMemberId(any(), any(), any()))
                .willReturn(Optional.of(inventory));
        final int updateAmount = inventory.getRentalAmount().getAmount() + 1;
        doNothing().when(amountValidator).validateAmount(
                eq(1L), eq(updateAmount), eq(inventory.getRentalPeriod()));
        doNothing().when(inventoryRepository).updateAmount(inventory.getId(), RentalAmount.ofPositive(updateAmount));
        final AddInventoryRequest addInventoryRequest = AddInventoryRequest.builder()
                .equipmentId(1L)
                .amount(1)
                .rentalStartDate(now.plusDays(1))
                .rentalEndDate(now.plusDays(2))
                .build();

        // when
        Long id = inventoryService.save(1L, addInventoryRequest);

        // then
        assertThat(id).isNotNull();
    }

    @Test
    @DisplayName("담은 기자재를 모두 삭제한다.")
    void deleteAll() {
        // given
        given(inventoryRepository.deleteAll(1L)).willReturn(1);

        // when
        assertThatCode(() -> inventoryService.deleteAll(1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("특정 담은 기자재를 삭제할 때 존재하지 않으면 예외")
    void delete() {
        // given
        final Inventory inventory = InventoryFixture.create(EquipmentFixture.create(), 1L);
        given(inventoryRepository.findById(any())).willReturn(Optional.of(inventory));
        doNothing().when(inventoryRepository).deleteById(any());

        // when, then
        assertThatCode(() -> inventoryService.deleteById(1L, 1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("특정 담은 기자재를 삭제할 때 유저 정보가 맞지 않으면 예외")
    void delete_invalidAccess() {
        // given
        final Inventory inventory = InventoryFixture.create(EquipmentFixture.create(), 1L);
        given(inventoryRepository.findById(any())).willReturn(Optional.of(inventory));

        // when, then
        assertThatThrownBy(() -> inventoryService.deleteById(2L, 1L))
                .isExactlyInstanceOf(InventoryInvalidAccessException.class);
    }

    @Test
    @DisplayName("특정 기자재 수정 시 없으면 예외")
    void update_notFound() {
        // given
        given(inventoryRepository.findWithEquipmentById(any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> inventoryService.update(1L, 1L, null))
                .isExactlyInstanceOf(InventoryNotFoundException.class);

    }

    @Test
    @DisplayName("특정 담은 기자재 수정")
    void update() {
        // given
        final Inventory inventory = InventoryFixture.create(EquipmentFixture.create(), 1L);
        final int amount = 2;
        final LocalDate rentalStartDate = LocalDate.now().plusDays(2);
        final LocalDate rentalEndDate = LocalDate.now().plusDays(3);
        final UpdateInventoryRequest updateInventoryRequest = UpdateInventoryRequest.builder()
                .amount(amount)
                .rentalStartDate(rentalStartDate)
                .rentalEndDate(rentalEndDate)
                .build();
        given(inventoryRepository.findWithEquipmentById(any())).willReturn(Optional.of(inventory));
        doNothing().when(amountValidator).validateAmount(any(), any(), any());

        // when
        final InventoryResponse response = inventoryService.update(1L, 1L, updateInventoryRequest);

        // then
        assertAll(
                () -> assertThat(response.getAmount()).isEqualTo(amount),
                () -> assertThat(response.getRentalStartDate()).isEqualTo(rentalStartDate),
                () -> assertThat(response.getRentalEndDate()).isEqualTo(rentalEndDate)

        );
    }

    @Test
    @DisplayName("특정 담은 기자재 수정할 때 사용자 정보가 맞지 않으면 예외 발생")
    void update_invalidAccess() {
        // given
        final Inventory inventory = InventoryFixture.create(EquipmentFixture.create(), 1L);
        final int amount = 2;
        final LocalDate rentalStartDate = LocalDate.now().plusDays(2);
        final LocalDate rentalEndDate = LocalDate.now().plusDays(3);
        final UpdateInventoryRequest updateInventoryRequest = UpdateInventoryRequest.builder()
                .amount(amount)
                .rentalStartDate(rentalStartDate)
                .rentalEndDate(rentalEndDate)
                .build();
        given(inventoryRepository.findWithEquipmentById(any())).willReturn(Optional.of(inventory));

        // when, then
        assertThatThrownBy(() -> inventoryService.update(2L, 1L, updateInventoryRequest))
                .isExactlyInstanceOf(InventoryInvalidAccessException.class);
    }

    @Test
    @DisplayName("담은 기자재 전원 조회 시 없을 경우 예외")
    void getInventories_notFound() {
        // given
        given(inventoryRepository.findAllWithEquipment(1L)).willReturn(Collections.emptyList());

        // when, then
        assertThatThrownBy(() -> inventoryService.getInventoriesWithEquipment(1L))
                .isExactlyInstanceOf(InventoryNotFoundException.class);
    }
}