package com.girigiri.kwrental.asset.equipment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;

@ExtendWith(MockitoExtension.class)
class EquipmentAdjusterTest {

	@Mock
	private EquipmentRetriever equipmentRetriever;
	@InjectMocks
	private EquipmentAdjuster equipmentAdjuster;

	@Test
	@DisplayName("품목 제거 시 기자재 갯수를 조정한다.")
	void adjustWhenItemDeleted() {
		// given
		final Equipment equipment = EquipmentFixture.builder().totalQuantity(10).rentableQuantity(10).build();
		given(equipmentRetriever.getEquipment(any())).willReturn(equipment);

		// when
		equipmentAdjuster.adjustWhenItemDeleted(2, -2, 1L);

		// then
		assertThat(equipment.getTotalQuantity()).isEqualTo(8);
		assertThat(equipment.getRentableQuantity()).isEqualTo(8);
	}

	@Test
	@DisplayName("품목 저장 시 기자재 갯수를 조정한다.")
	void adjustWhenItemSaved() {
		// given
		final Equipment equipment = EquipmentFixture.builder().totalQuantity(10).rentableQuantity(10).build();
		given(equipmentRetriever.getEquipment(any())).willReturn(equipment);

		// when
		equipmentAdjuster.adjustWhenItemSaved(1, 1L);

		// then
		assertThat(equipment.getTotalQuantity()).isEqualTo(11);
		assertThat(equipment.getRentableQuantity()).isEqualTo(11);
	}

	@Test
	@DisplayName("대여 가능 갯수를 조정한다.")
	void adjustRentableQuantity() {
		// given
		final Equipment equipment = EquipmentFixture.builder().totalQuantity(10).rentableQuantity(10).build();
		given(equipmentRetriever.getEquipment(any())).willReturn(equipment);

		// when
		equipmentAdjuster.adjustRentableQuantity(1L, -1);

		// then
		assertThat(equipment.getRentableQuantity()).isEqualTo(9);
	}
}