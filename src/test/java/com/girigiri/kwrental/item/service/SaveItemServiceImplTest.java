package com.girigiri.kwrental.item.service;

import com.girigiri.kwrental.equipment.dto.request.AddItemRequest;
import com.girigiri.kwrental.item.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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