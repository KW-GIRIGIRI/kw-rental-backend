package com.girigiri.kwrental.inventory.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.InventoryFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@ExtendWith(MockitoExtension.class)
class InventoryReservationSpecMapperTest {
	@Mock
	private InventoryService inventoryService;
	@InjectMocks
	private InventoryReservationSpecMapper inventoryReservationSpecMapper;

	@Test
	@DisplayName("회원의 장바구니를 기반으로 대여 예약 상세를 생성한다.")
	void map() {
		// given
		final Long memberId = 1L;
		final Equipment equipment = EquipmentFixture.builder().id(1L).build();
		final Inventory inventory = InventoryFixture.create(equipment, memberId);
		given(inventoryService.getInventoriesWithEquipment(memberId)).willReturn(List.of(inventory));

		final ReservationSpec expect = ReservationSpecFixture.builder(equipment)
			.period(inventory.getRentalPeriod())
			.amount(inventory.getRentalAmount())
			.build();

		// when
		final List<ReservationSpec> actual = inventoryReservationSpecMapper.map(memberId);

		// then
		assertThat(actual).usingRecursiveFieldByFieldElementComparator().containsExactly(expect);
	}
}