package com.girigiri.kwrental.labroom.dto.response;

import java.util.List;

import lombok.Getter;

@Getter
public class RemainReservationCountsPerDateResponse {
	private Long id;
	private List<RemainReservationCountPerDateResponse> remainReservationCounts;

	private RemainReservationCountsPerDateResponse() {
	}

	public RemainReservationCountsPerDateResponse(Long id,
		List<RemainReservationCountPerDateResponse> remainReservationCounts) {
		this.id = id;
		this.remainReservationCounts = remainReservationCounts;
	}
}
