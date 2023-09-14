package com.girigiri.kwrental.reservation.service.cancel.event;

import com.girigiri.kwrental.common.mail.MailEvent;

public class CancelByAssetDeleteEvent extends MailEvent {
	public CancelByAssetDeleteEvent(final String email, final Object source) {
		super("대여 예약이 취소됐습니다.", "예약했던 기자재가 삭제되어 대여 예약이 취소되었습니다.", email, source);
	}
}
