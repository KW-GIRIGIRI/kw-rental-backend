package com.girigiri.kwrental.penalty.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.girigiri.kwrental.penalty.domain.PenaltyPeriod;
import com.girigiri.kwrental.penalty.domain.PenaltyReason;

public record PenaltyHistoryPageResponse(
	List<String> endPoints,
	Integer page,
	List<PenaltyHistoryResponse> penalties) {

	public record PenaltyHistoryResponse(
		Long id, String renterName, String status, LocalDate startDate, LocalDate endDate, String assetName,
		PenaltyReason reason) {

		public PenaltyHistoryResponse(final Long id, final String renterName, final PenaltyPeriod period,
			final String assetName, final PenaltyReason reason) {
			this(id, renterName, period.getStatus().getMessage(), period.getStartDate(), period.getEndDate(), assetName,
				reason);
		}
	}
}
