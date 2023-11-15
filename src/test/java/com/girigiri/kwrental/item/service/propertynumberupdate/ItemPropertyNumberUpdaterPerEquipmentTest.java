package com.girigiri.kwrental.item.service.propertynumberupdate;

import com.girigiri.kwrental.item.exception.ItemsNotSameEquipmentException;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.item.service.RentedItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ItemPropertyNumberUpdaterPerEquipmentTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private RentedItemService rentedItemService;

    @InjectMocks
    private ItemPropertyNumberUpdaterPerEquipment itemPropertyNumberUpdaterPerEquipment;

    @Test
    @DisplayName("특정 자산의 품목들의 자산번호를 수정한다.")
    void execute() {
        // given
        final ToBeUpdatedItem toBeUpdatedItem1 = new ToBeUpdatedItem(1L, 2L, "12345678", "87654321");
        final ToBeUpdatedItem toBeUpdatedItem2 = new ToBeUpdatedItem(2L, 2L, "87654321", "12345678");
        final List<ToBeUpdatedItem> toBeUpdatedItems = List.of(toBeUpdatedItem1, toBeUpdatedItem2);
        given(itemRepository.updatePropertyNumbers(toBeUpdatedItems)).willReturn(2);
        given(rentedItemService.updatePropertyNumbers(toBeUpdatedItems)).willReturn(2);

        // when
        int actual = itemPropertyNumberUpdaterPerEquipment.execute(toBeUpdatedItems);

        // then
        assertThat(actual).isEqualTo(2);
    }

    @Test
    @DisplayName("자산번호를 수정하려는 품목들이 같은 기자재가 아니면 예외가 발생")
    void execute_notSameEquipment() {
        // given
        final ToBeUpdatedItem toBeUpdatedItem1 = new ToBeUpdatedItem(1L, 2L, "12345678", "87654321");
        final ToBeUpdatedItem toBeUpdatedItem2 = new ToBeUpdatedItem(2L, 3L, "87654321", "12345678");
        final List<ToBeUpdatedItem> toBeUpdatedItems = List.of(toBeUpdatedItem1, toBeUpdatedItem2);

        // when, then
        assertThatThrownBy(() -> itemPropertyNumberUpdaterPerEquipment.execute(toBeUpdatedItems))
                .isExactlyInstanceOf(ItemsNotSameEquipmentException.class);
    }
}