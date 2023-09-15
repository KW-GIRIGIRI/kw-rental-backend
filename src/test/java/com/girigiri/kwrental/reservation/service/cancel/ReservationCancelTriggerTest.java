package com.girigiri.kwrental.reservation.service.cancel;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.service.cancel.event.CancelByAdminEvent;
import com.girigiri.kwrental.reservation.service.cancel.event.CancelByAssetDeleteEvent;
import com.girigiri.kwrental.reservation.service.cancel.event.CancelByAssetUnavailableEvent;
import com.girigiri.kwrental.reservation.service.cancel.event.CancelByPenaltyEvent;
import com.girigiri.kwrental.testsupport.fixture.ReservationFixture;
import com.girigiri.kwrental.testsupport.fixture.ReservationSpecFixture;

@ExtendWith(MockitoExtension.class)
class ReservationCancelTriggerTest {

	@Mock
	private ReservationCanceler reservationCanceler;
	@Mock
	private ApplicationEventPublisher applicationEventPublisher;
	@Captor
	private ArgumentCaptor<CancelAlerter> alerterArgumentCaptor;
	@InjectMocks
	private ReservationCancelTrigger reservationCancelTrigger;

	@Test
	@DisplayName("페널티에 의해 대여 예약이 취소된다.")
	void triggerByPenalty() {
		// given
		doNothing().when(reservationCanceler).cancelByMemberId(anyLong(), any());
		doNothing().when(applicationEventPublisher).publishEvent(any(CancelByPenaltyEvent.class));

		// when
		assertThatCode(() -> reservationCancelTrigger.triggerByPenalty(1L))
			.doesNotThrowAnyException();

		// then
		verify(reservationCanceler).cancelByMemberId(any(), alerterArgumentCaptor.capture());
		final ReservationSpec spec = ReservationSpecFixture.create(null);
		ReservationFixture.create(List.of(spec));
		alerterArgumentCaptor.getValue().alert(List.of(spec));
	}

	@Test
	@DisplayName("자산 삭제에 의해 대여 예약이 취소된다.")
	void triggerByAssetDelete() {
		// given
		doNothing().when(reservationCanceler).cancelByAssetId(anyLong(), any(CancelAlerter.class));
		doNothing().when(applicationEventPublisher).publishEvent(any(CancelByAssetDeleteEvent.class));

		// when
		assertThatCode(() -> reservationCancelTrigger.triggerByAssetDelete(1L))
			.doesNotThrowAnyException();

		// then
		verify(reservationCanceler).cancelByAssetId(any(), alerterArgumentCaptor.capture());
		final ReservationSpec spec = ReservationSpecFixture.create(null);
		ReservationFixture.create(List.of(spec));
		alerterArgumentCaptor.getValue().alert(List.of(spec));
	}

	@Test
	@DisplayName("관리자가 대여 예약 상세를 취소한다")
	void triggerByAdminCancelReservationSpec() {
		// given
		given(reservationCanceler.cancelReservationSpec(anyLong(), anyInt(), any(CancelAlerter.class)))
			.willReturn(1L);
		doNothing().when(applicationEventPublisher).publishEvent(any(CancelByAdminEvent.class));

		// when
		assertThatCode(() -> reservationCancelTrigger.triggerByAdminCancelReservationSpec(1L, 1))
			.doesNotThrowAnyException();

		// then
		verify(reservationCanceler).cancelReservationSpec(any(), any(), alerterArgumentCaptor.capture());
		final ReservationSpec spec = ReservationSpecFixture.create(null);
		ReservationFixture.create(List.of(spec));
		alerterArgumentCaptor.getValue().alert(spec);
	}

	@Test
	@DisplayName("랩실 공간이 사용할 수 없게 되는 경우 관련 대여 예약 상세를 취소한다.")
	void triggerByLabRoomUnavailable() {
		// given
		doNothing().when(reservationCanceler).cancelByAssetId(any(), any(CancelAlerter.class));
		doNothing().when(applicationEventPublisher).publishEvent(any(CancelByAssetUnavailableEvent.class));

		// when
		assertThatCode(() -> reservationCancelTrigger.triggerByLabRoomUnavailable(1L, "hanul"))
			.doesNotThrowAnyException();

		// then
		verify(reservationCanceler).cancelByAssetId(any(), alerterArgumentCaptor.capture());
		final ReservationSpec spec = ReservationSpecFixture.create(null);
		ReservationFixture.create(List.of(spec));
		alerterArgumentCaptor.getValue().alert(List.of(spec));
	}

	@Test
	@DisplayName("랩실 공간이 특정 일자에 사용할 수 없게 되는 경우 관련 대여 예약 상세를 취소한다.")
	void triggerByLabRoomDailyUnavailable() {
		// given
		doNothing().when(reservationCanceler).cancelByAssetIdAndDate(any(), any(), any(CancelAlerter.class));
		doNothing().when(applicationEventPublisher).publishEvent(any(CancelByAssetUnavailableEvent.class));

		// when
		assertThatCode(() -> reservationCancelTrigger.triggerByLabRoomDailyUnavailable(1L, "hanul", LocalDate.now()))
			.doesNotThrowAnyException();

		// then
		verify(reservationCanceler).cancelByAssetIdAndDate(any(), eq(LocalDate.now()), alerterArgumentCaptor.capture());
		final ReservationSpec spec = ReservationSpecFixture.create(null);
		ReservationFixture.create(List.of(spec));
		alerterArgumentCaptor.getValue().alert(List.of(spec));
	}
}