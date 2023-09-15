package com.girigiri.kwrental.reservation.service.cancel;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.common.mail.MailEvent;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.service.cancel.event.CancelByAdminEvent;
import com.girigiri.kwrental.reservation.service.cancel.event.CancelByAssetDeleteEvent;
import com.girigiri.kwrental.reservation.service.cancel.event.CancelByAssetUnavailableEvent;
import com.girigiri.kwrental.reservation.service.cancel.event.CancelByPenaltyEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class ReservationCancelTrigger {
	private final ReservationCanceler reservationCanceler;
	private final ApplicationEventPublisher eventPublisher;

	public void triggerByPenalty(final Long memberId) {
		reservationCanceler.cancelByMemberId(memberId,
			specs -> publishEvent(specs, email -> new CancelByPenaltyEvent(email, this)));
	}

	public void triggerByAssetDelete(final Long assetId) {
		reservationCanceler.cancelByAssetId(assetId,
			specs -> publishEvent(specs, email -> new CancelByAssetDeleteEvent(email, this)));
	}

	public Long triggerByAdminCancelReservationSpec(final Long reservationSpecId, final Integer amount) {
		return reservationCanceler.cancelReservationSpec(reservationSpecId, amount,
			spec -> publishEvent(List.of(spec), email -> new CancelByAdminEvent(email, this)));
	}

	public void triggerByLabRoomUnavailable(final Long labRoomId, final String labRoomName) {
		reservationCanceler.cancelByAssetId(labRoomId,
			specs -> publishEvent(specs, email -> new CancelByAssetUnavailableEvent(labRoomName, email, this)));
	}

	public void triggerByLabRoomDailyUnavailable(final Long labRoomId, final String labRoomName, final LocalDate date) {
		reservationCanceler.cancelByAssetIdAndDate(labRoomId, date,
			specs -> publishEvent(specs, email -> new CancelByAssetUnavailableEvent(labRoomName, email, this)));
	}

	private void publishEvent(final List<ReservationSpec> specs,
		final Function<String, MailEvent> eventFunction) {
		final List<MailEvent> events = specs.stream()
			.map(ReservationSpec::getReservation)
			.map(Reservation::getEmail)
			.distinct()
			.map(eventFunction)
			.toList();
		events.forEach(eventPublisher::publishEvent);
	}
}
