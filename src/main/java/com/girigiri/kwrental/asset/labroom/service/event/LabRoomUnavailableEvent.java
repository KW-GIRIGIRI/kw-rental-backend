package com.girigiri.kwrental.asset.labroom.service.event;

import org.springframework.context.ApplicationEvent;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;

import lombok.Getter;

@Getter
public class LabRoomUnavailableEvent extends ApplicationEvent {

	private final String labRoomName;
	private final Long labRoomId;

	public LabRoomUnavailableEvent(final Object source, final LabRoom labRoom) {
		super(source);
		this.labRoomName = labRoom.getName();
		this.labRoomId = labRoom.getId();
	}
}
