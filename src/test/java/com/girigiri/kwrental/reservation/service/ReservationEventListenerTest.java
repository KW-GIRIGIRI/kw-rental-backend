package com.girigiri.kwrental.reservation.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.girigiri.kwrental.asset.equipment.service.EquipmentDeleteEvent;

@ExtendWith(MockitoExtension.class)
class ReservationEventListenerTest {

	@Mock
	private ReservationService reservationService;

	@InjectMocks
	private ReservationEventListener reservationEventListener;

	@Test
	@DisplayName("자산 삭제됨에 따라 ")
	void handleAssetDelete() {
		// given
		doNothing().when(reservationService).cancelByAssetId(anyLong());
		final EquipmentDeleteEvent event = new EquipmentDeleteEvent(this, 1L);

		// when, then
		assertThatCode(() -> reservationEventListener.handleAssetDelete(event))
			.doesNotThrowAnyException();
	}
}