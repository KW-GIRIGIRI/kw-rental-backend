package com.girigiri.kwrental.reservation.dto.response;

import lombok.Builder;

@Builder
public record HistoryStatResponse(
	String labRoomName,
	Integer reservationCount,
	Integer userCount,
	Integer abnormalReturnCount
) {
}
