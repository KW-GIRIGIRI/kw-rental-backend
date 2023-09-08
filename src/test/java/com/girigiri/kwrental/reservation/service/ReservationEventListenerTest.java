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
import com.girigiri.kwrental.reservation.service.cancel.ReservationCancelTrigger;

@ExtendWith(MockitoExtension.class)
class ReservationEventListenerTest {

	@Mock
	private ReservationCancelTrigger reservationCancelTrigger;

	@InjectMocks
	private ReservationEventListener reservationEventListener;

	@Test
	@DisplayName("자산 삭제됨에 따라 ")
	void handleAssetDelete() {
		// given
		doNothing().when(reservationCancelTrigger).triggerByAssetDelete(anyLong());
		final EquipmentDeleteEvent event = new EquipmentDeleteEvent(this, 1L);

		// when, then
		assertThatCode(() -> reservationEventListener.handleAssetDelete(event))
			.doesNotThrowAnyException();
	}
}