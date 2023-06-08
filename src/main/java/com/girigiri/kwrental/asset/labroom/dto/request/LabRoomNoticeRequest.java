package com.girigiri.kwrental.asset.labroom.dto.request;

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
