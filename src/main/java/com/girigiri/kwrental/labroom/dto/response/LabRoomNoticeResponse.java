package com.girigiri.kwrental.labroom.dto.response;

import lombok.Getter;

@Getter
public class LabRoomNoticeResponse {

	private String notice;

	private LabRoomNoticeResponse() {
	}

	public LabRoomNoticeResponse(String notice) {
		this.notice = notice;
	}
}
