package com.girigiri.kwrental.reservation.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.service.EquipmentDeleteEvent;

@SpringBootTest
class ReservationEventListenerTest {

	@Autowired
	private ApplicationEventPublisher publisher;

	@MockBean
	private ReservationService reservationService;

	@InjectMocks
	private ReservationEventListener reservationEventListener;

	@Test
	@Transactional
	@DisplayName("자산 삭제 시 해당 자산의 대여를 취소한다.")
	void handleAssetDelete() {
		// given
		EquipmentDeleteEvent equipmentDeleteEvent = new EquipmentDeleteEvent(this, 1L);

		// when
		publisher.publishEvent(equipmentDeleteEvent);

		// then
		verify(reservationService).cancelByAssetId(any());
	}
}