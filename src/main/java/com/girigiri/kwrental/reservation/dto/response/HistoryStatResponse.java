package com.girigiri.kwrental.reservation.dto.response;

import lombok.Getter;

@Getter
public class HistoryStatResponse {
	private String labRoomName;
	private Integer reservationCount;
	private Integer userCount;
	private Integer abnormalReturnCount;

	private HistoryStatResponse() {
	}

	public HistoryStatResponse(String labRoomName, Integer reservationCount, Integer userCount,
		Integer abnormalReturnCount) {
		this.labRoomName = labRoomName;
		this.reservationCount = reservationCount;
		this.userCount = userCount;
		this.abnormalReturnCount = abnormalReturnCount;
	}
}
