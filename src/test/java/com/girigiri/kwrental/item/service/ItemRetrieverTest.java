package com.girigiri.kwrental.item.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.item.exception.ItemNotFoundException;
import com.girigiri.kwrental.item.repository.ItemRepository;

@ExtendWith(MockitoExtension.class)
class ItemRetrieverTest {

	@Mock
	private ItemRepository itemRepository;
	@InjectMocks
	private ItemRetriever itemRetriever;

	@Test
	@DisplayName("존재하지 않는 id로 조회시 예외 발생")
	void getById() {
		// given
		given(itemRepository.findById(any())).willReturn(Optional.empty());

		// when, then
		Assertions.assertThatCode(() -> itemRetriever.getById(1L))
			.isExactlyInstanceOf(ItemNotFoundException.class);
	}

	@Test
	@DisplayName("존재하지 않는 id로 조회시 예외 발생")
	void getByPropertyNumber() {
		// given
		given(itemRepository.findByPropertyNumber(any())).willReturn(Optional.empty());

		// when, then
		Assertions.assertThatCode(() -> itemRetriever.getByPropertyNumber("12345678"))
			.isExactlyInstanceOf(ItemNotFoundException.class);
	}
}