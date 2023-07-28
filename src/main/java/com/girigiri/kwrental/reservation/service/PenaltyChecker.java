package com.girigiri.kwrental.reservation.service;

public interface PenaltyChecker {
	boolean hasOngoingPenalty(Long memberId);
}
