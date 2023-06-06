package com.girigiri.kwrental.asset.equipment.service;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class EquipmentDeleteEvent extends ApplicationEvent {

	private final Long equipmentId;

	public EquipmentDeleteEvent(final Object source, final Long equipmentId) {
		super(source);
		this.equipmentId = equipmentId;
	}
}
