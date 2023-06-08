package com.girigiri.kwrental.asset.labroom.dto.response;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class RemainReservationCountPerDateResponse {
	private LocalDate date;
	private Integer remainReservationCount;

	private RemainReservationCountPerDateResponse() {
	}

	public RemainReservationCountPerDateResponse(LocalDate date, Integer remainReservationCount) {
		this.date = date;
		this.remainReservationCount = remainReservationCount;
	}
}
