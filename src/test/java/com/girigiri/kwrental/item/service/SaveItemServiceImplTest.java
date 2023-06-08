package com.girigiri.kwrental.item.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.equipment.dto.request.AddItemRequest;
import com.girigiri.kwrental.item.repository.ItemRepository;

@ExtendWith(MockitoExtension.class)
class SaveItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private SaveItemServiceImpl saveItemService;

    @Test
    @DisplayName("품목 Bulk Update")
    void saveItems() {
        // given
        given(itemRepository.saveAll(any()))
                .willReturn(1);
        final AddItemRequest addItemRequest = new AddItemRequest("12345678");

        // when
        saveItemService.saveItems(1L, List.of(addItemRequest));

        // then
        verify(itemRepository).saveAll(any());
    }
}