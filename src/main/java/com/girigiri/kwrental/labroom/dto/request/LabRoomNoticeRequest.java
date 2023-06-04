package com.girigiri.kwrental.labroom.dto.request;

import lombok.Getter;

@Getter
public class LabRoomNoticeRequest {

	private String notice;

	private LabRoomNoticeRequest() {
	}

	public LabRoomNoticeRequest(String notice) {
		this.notice = notice;
	}
}
