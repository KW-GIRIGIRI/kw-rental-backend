package com.girigiri.kwrental.item.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.girigiri.kwrental.item.exception.PropertyNumberNotUniqueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.exception.NotEnoughAvailableItemException;
import com.girigiri.kwrental.item.repository.ItemRepository;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;

@ExtendWith(MockitoExtension.class)
class ItemValidatorTest {

    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemValidator itemValidator;

    @Test
    @DisplayName("운영 중인 품목 갯수를 검증한다.")
    void validateAvailableCount() {
        // given
        given(itemRepository.countAvailable(any())).willReturn(10);

        // when
        assertThatThrownBy(() -> itemValidator.validateAvailableCount(1L, 11))
                .isExactlyInstanceOf(NotEnoughAvailableItemException.class);
    }

    @Test
    @DisplayName("기자재 ID들과 그에 해당하는 자산번호를 검증한다.")
    void validatePropertyNumbers() {
        // given
        final Item item = ItemFixture.builder().assetId(1L).propertyNumber("11111111").build();
        given(itemRepository.findByEquipmentIds(any()))
                .willReturn(List.of(item));

        // when, then
        assertThatCode(
                () -> itemValidator.validatePropertyNumbers(Map.of(item.getAssetId(), Set.of(item.getPropertyNumber()))))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("입력받은 자산번호가 기존의 품목의 자산번호으로 존재하면 예외가 발생한다.")
    void validateItemsNotExistsByPropertyNumbers_exists() {
        // given
        final Item item = ItemFixture.builder().assetId(1L).propertyNumber("11111111").build();
        given(itemRepository.findByPropertyNumbers(any()))
                .willReturn(List.of(item));

        // when, then
        assertThatThrownBy(() -> itemValidator.validateItemsNotExistsByPropertyNumbers(List.of(item.getPropertyNumber())))
				.isExactlyInstanceOf(PropertyNumberNotUniqueException.class);
    }

    @Test
    @DisplayName("입력받은 자산번호가 기존의 품목의 자산번호으로 존재하면 예외가 발생한다.")
    void validateItemsNotExistsByPropertyNumbers() {
        // given
        given(itemRepository.findByPropertyNumbers(any()))
                .willReturn(Collections.emptyList());

        // when, then
        assertThatCode(() -> itemValidator.validateItemsNotExistsByPropertyNumbers(List.of("123123123")))
                .doesNotThrowAnyException();
    }
}