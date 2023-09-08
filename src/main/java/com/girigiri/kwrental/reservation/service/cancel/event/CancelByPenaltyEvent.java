package com.girigiri.kwrental.reservation.service.cancel.event;

import com.girigiri.kwrental.common.mail.MailEvent;

public class CancelByPenaltyEvent extends MailEvent {
	public CancelByPenaltyEvent(final String email, final Object source) {
		super("[광운대학교 미디어커뮤니케이션 랩실] 대여 예약이 취소됐습니다.", "해당 사용자에게 페널티가 부여되서 대여 예약이 모두 취소되었습니다.", email, source);
	}
}
