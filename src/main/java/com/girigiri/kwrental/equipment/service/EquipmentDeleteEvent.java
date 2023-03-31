package com.girigiri.kwrental.equipment.service;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EquipmentDeleteEvent extends ApplicationEvent {

    private final Long equipmentId;

    public EquipmentDeleteEvent(final Object source, final Long equipmentId) {
        super(source);
        this.equipmentId = equipmentId;
    }
}
