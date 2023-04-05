package com.girigiri.kwrental.inventory.service;

import com.girigiri.kwrental.equipment.service.EquipmentService;
import com.girigiri.kwrental.inventory.dto.request.AddInventoryRequest;
import com.girigiri.kwrental.inventory.repository.InventoryRepository;
import com.girigiri.kwrental.testsupport.fixture.InventoryFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
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
        doNothing().when(amountValidator).validateAmount(any(), any());
        doNothing().when(equipmentService).validateRentalDays(any(), any());
        given(inventoryRepository.save(any())).willReturn(InventoryFixture.builder().id(1L).build());
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
}