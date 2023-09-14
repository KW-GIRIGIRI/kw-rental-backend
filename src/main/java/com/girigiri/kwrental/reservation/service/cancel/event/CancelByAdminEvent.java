package com.girigiri.kwrental.reservation.service.cancel.event;

import com.girigiri.kwrental.common.mail.MailEvent;

public class CancelByAdminEvent extends MailEvent {
	public CancelByAdminEvent(final String email, final Object source) {
		super("대여 예약이 취소됐습니다.", "관리자에 의해 대여 예약이 취소되었습니다.", email, source);
	}
}
