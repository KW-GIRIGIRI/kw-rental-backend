package com.girigiri.kwrental.labroom.dto.response;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class LabRoomAvailableResponse {
	private Long id;
	private boolean available;
	private LocalDate date;

	private LabRoomAvailableResponse() {
	}

	public LabRoomAvailableResponse(Long id, boolean available, LocalDate date) {
		this.id = id;
		this.available = available;
		this.date = date;
	}
}
