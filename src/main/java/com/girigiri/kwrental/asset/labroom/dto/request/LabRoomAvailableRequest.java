package com.girigiri.kwrental.asset.labroom.dto.request;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class LabRoomAvailableRequest {

	private boolean entirePeriod;
	private LocalDate date;
	private boolean available;

	private LabRoomAvailableRequest() {
	}

	public LabRoomAvailableRequest(boolean entirePeriod, LocalDate date, boolean available) {
		this.entirePeriod = entirePeriod;
		this.date = date;
		this.available = available;
	}
}
