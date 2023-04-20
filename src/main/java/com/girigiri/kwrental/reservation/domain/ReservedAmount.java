package com.girigiri.kwrental.reservation.domain;

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
}
