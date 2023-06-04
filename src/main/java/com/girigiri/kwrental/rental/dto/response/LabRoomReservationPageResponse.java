package com.girigiri.kwrental.rental.dto.response;

import java.util.List;

import lombok.Getter;

@Getter
public class LabRoomReservationPageResponse {
	private List<LabRoomReservationResponse> labRoomReservations;
	private Integer page;
	private List<String> endPoints;

	private LabRoomReservationPageResponse() {
	}

	public LabRoomReservationPageResponse(List<LabRoomReservationResponse> labRoomReservations, Integer page,
		List<String> endPoints) {
		this.labRoomReservations = labRoomReservations;
		this.page = page;
		this.endPoints = endPoints;
	}
}
