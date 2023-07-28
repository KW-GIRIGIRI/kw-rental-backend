package com.girigiri.kwrental.reservation.service.reserve.template;

public interface PenaltyChecker {
	boolean hasOngoingPenalty(Long memberId);
}
