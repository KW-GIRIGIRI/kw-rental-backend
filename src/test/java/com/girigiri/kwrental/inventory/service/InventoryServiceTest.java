package com.girigiri.kwrental.inventory.service;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.service.EquipmentService;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.dto.request.AddInventoryRequest;
import com.girigiri.kwrental.inventory.dto.request.UpdateInventoryRequest;
import com.girigiri.kwrental.inventory.dto.response.InventoryResponse;
import com.girigiri.kwrental.inventory.exception.InventoryNotFound;
import com.girigiri.kwrental.inventory.repository.InventoryRepository;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.InventoryFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

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
        Long id = inventoryService.save(addInventoryRequest);

        // then
        assertThat(id).isNotNull();
    }

    @Test
    @DisplayName("담은 기자재를 모두 삭제한다.")
    void deleteAll() {
        // given
        given(inventoryRepository.deleteAll()).willReturn(1);

        // when
        assertThatCode(() -> inventoryService.deleteAll())
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("특정 담은 기자재를 삭제할 때 존재하지 않으면 예외")
    void delete() {
        // given
        final Inventory inventory = InventoryFixture.create(EquipmentFixture.create());
        given(inventoryRepository.findById(any())).willReturn(Optional.of(inventory));
        doNothing().when(inventoryRepository).deleteById(any());

        // when, then
        assertThatCode(() -> inventoryService.deleteById(1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("특정 기자재 수정 시 없으면 예외")
    void update_notFound() {
        // given
        given(inventoryRepository.findWithEquipmentById(any())).willThrow(InventoryNotFound.class);

        // when, then
        assertThatThrownBy(() -> inventoryService.update(1L, null))
                .isExactlyInstanceOf(InventoryNotFound.class);

    }

    @Test
    @DisplayName("특정 기자재 수정")
    void update() {
        // given
        final Inventory inventory = InventoryFixture.create(EquipmentFixture.create());
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
        final InventoryResponse response = inventoryService.update(1L, updateInventoryRequest);

        // then
        assertAll(
                () -> assertThat(response.getAmount()).isEqualTo(amount),
                () -> assertThat(response.getRentalStartDate()).isEqualTo(rentalStartDate),
                () -> assertThat(response.getRentalEndDate()).isEqualTo(rentalEndDate)

        );
    }
}