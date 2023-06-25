package com.girigiri.kwrental.reservation.domain;

import java.util.List;

import com.girigiri.kwrental.inventory.domain.RentalDateTime;

import lombok.Getter;

@Getter
public class EquipmentReservationWithMemberNumber {
    private final Long id;
    private final String renterName;
    private final String memberNumber;
    private final RentalDateTime acceptDateTime;

    private final List<ReservationSpec> reservationSpecs;

    public EquipmentReservationWithMemberNumber(final Long id, final String renterName, final String memberNumber,
                                                final RentalDateTime acceptDateTime,
                                                final List<ReservationSpec> reservationSpecs) {
        this.id = id;
        this.renterName = renterName;
        this.memberNumber = memberNumber;
        this.acceptDateTime = acceptDateTime;
        this.reservationSpecs = reservationSpecs;
    }

    public List<Long> getReservationSpecIds() {
        return this.reservationSpecs.stream()
                .map(ReservationSpec::getId)
                .toList();
    }

    public boolean isAccepted() {
        return this.acceptDateTime != null;
    }
}
