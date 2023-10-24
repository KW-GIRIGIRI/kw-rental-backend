package com.girigiri.kwrental.reservation.domain.entity;

public class ReservedAmount {

    private final Long equipmentId;
    private final int rentableAmount;
    private final int reservedAmount;

    public ReservedAmount(final Long equipmentId, final int rentableAmount, final int reservedAmount) {
        this.equipmentId = equipmentId;
        this.rentableAmount = rentableAmount;
        this.reservedAmount = reservedAmount;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public int getRemainingAmount() {
        return rentableAmount - reservedAmount;
    }
}
