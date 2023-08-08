package com.girigiri.kwrental.asset.equipment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.asset.equipment.repository.EquipmentRepository;

@ExtendWith(MockitoExtension.class)
class EquipmentRetrieverTest {
	@Mock
	private EquipmentRepository equipmentRepository;
	@InjectMocks
	private EquipmentRetriever equipmentRetriever;

	@Test
	@DisplayName("등록되지 않은 기자재를 조회하려면 예외가 발생한다.")
	void getEquipment_exception_notFound() {
		// given
		given(equipmentRepository.findById(1L)).willThrow(EquipmentNotFoundException.class);

		// when, then
		assertThatThrownBy(() -> equipmentRetriever.getEquipment(1L))
			.isExactlyInstanceOf(EquipmentNotFoundException.class);
	}
}