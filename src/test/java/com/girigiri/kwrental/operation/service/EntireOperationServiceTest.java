package com.girigiri.kwrental.operation.service;

import static com.girigiri.kwrental.testsupport.DeepReflectionEqMatcher.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.operation.domain.EntireOperation;
import com.girigiri.kwrental.operation.repository.EntireOperationRepository;
import com.girigiri.kwrental.testsupport.fixture.EntireOperationFixture;

@ExtendWith(MockitoExtension.class)
class EntireOperationServiceTest {

	@Mock
	private EntireOperationRepository entireOperationRepository;
	@InjectMocks
	private EntireOperationService entireOperationService;

	@Test
	@DisplayName("전체 운영이 저장된 적이 없으면 저장한다.")
	void putEntireOperation_save() {
		// given
		when(entireOperationRepository.exists()).thenReturn(false);
		final EntireOperation entireOperation = EntireOperationFixture.create(true);
		when(entireOperationRepository.save(deepRefEq(entireOperation))).thenReturn(entireOperation);

		// when
		entireOperationService.putEntireOperation(true);

		// then
		verify(entireOperationRepository).save(any(EntireOperation.class));
	}

	@Test
	@DisplayName("전체 운영이 저장된 적이 있으면 운영 여부를 업데이트한다.")
	void putEntireOperation_update() {
		// given
		when(entireOperationRepository.exists()).thenReturn(true);
		when(entireOperationRepository.updateEntireOperation(true)).thenReturn(1L);

		// when
		entireOperationService.putEntireOperation(true);

		// then
		verify(entireOperationRepository).updateEntireOperation(true);
	}
}