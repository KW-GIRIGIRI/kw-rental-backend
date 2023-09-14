package com.girigiri.kwrental.asset.labroom.service.event;

import java.time.LocalDate;

import org.springframework.context.ApplicationEvent;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;

import lombok.Getter;

@Getter
public class LabRoomDailyUnavailableEvent extends ApplicationEvent {
	private final String labRoomName;
	private final Long labRoomId;
	private final LocalDate date;

	public LabRoomDailyUnavailableEvent(final Object source, final LabRoom labRoom, final LocalDate date) {
		super(source);
		this.labRoomId = labRoom.getId();
		this.labRoomName = labRoom.getName();
		this.date = date;
	}
}
