package com.girigiri.kwrental.reservation.repository.dto;

import com.querydsl.core.annotations.QueryProjection;

public class ReservedAmount {

    private Long equipmentId;
    private int totalAmount;
    private int reservedAmount;

    protected ReservedAmount() {
    }

    @QueryProjection
    public ReservedAmount(final Long equipmentId, final int totalAmount, final int reservedAmount) {
        this.equipmentId = equipmentId;
        this.totalAmount = totalAmount;
        this.reservedAmount = reservedAmount;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public int getRemainingAmount() {
        return totalAmount - reservedAmount;
    }
}
