package com.girigiri.kwrental.reservation.service.cancel.event;

import com.girigiri.kwrental.common.mail.MailEvent;

public class CancelByAssetUnavailableEvent extends MailEvent {
	public CancelByAssetUnavailableEvent(final String assetName, final String email, final Object source) {
		super("대여 예약이 취소됐습니다.", String.format("예약했던 자산(%s)이 사용할 수 없게 되어 대여 예약이 취소되었습니다.", assetName), email, source);
	}
}
