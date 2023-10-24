package com.girigiri.kwrental.penalty.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.girigiri.kwrental.penalty.domain.PenaltyPeriod;
import com.girigiri.kwrental.penalty.domain.PenaltyReason;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;

public record UserPenaltiesResponse(List<UserPenaltyResponse> penalties) {
	public record UserPenaltyResponse(
		Long id, LocalDate acceptDate, LocalDate returnDate, String status, String assetName, PenaltyReason reason) {
		public UserPenaltyResponse(final Long id, final RentalDateTime acceptDate, final RentalDateTime returnDate,
			final PenaltyPeriod period,
			final String assetName, final PenaltyReason reason) {
			this(id, acceptDate.toLocalDate(), returnDate.toLocalDate(), period.getStatus().getMessage(), assetName,
				reason);
		}
	}
}
