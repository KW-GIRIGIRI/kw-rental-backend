package com.girigiri.kwrental.item.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.girigiri.kwrental.equipment.dto.request.AddItemRequest;
import com.girigiri.kwrental.item.repository.ItemRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    @DisplayName("품목 Bulk Update")
    void saveItems() {
        // given
        BDDMockito.given(itemRepository.saveAll(any()))
                .willReturn(1);
        final AddItemRequest addItemRequest = new AddItemRequest("12345678");

        // when
        itemService.saveItems(1L, List.of(addItemRequest));

        // then
        verify(itemRepository).saveAll(any());
    }
}
