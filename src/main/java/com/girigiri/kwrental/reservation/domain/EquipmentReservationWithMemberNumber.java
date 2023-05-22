package com.girigiri.kwrental.reservation.domain;

import com.girigiri.kwrental.inventory.domain.RentalDateTime;
import lombok.Getter;

import java.util.List;

@Getter
public class EquipmentReservationWithMemberNumber {
    private final String renterName;
    private final String memberNumber;
    private final RentalDateTime acceptDateTime;

    private final List<ReservationSpec> reservationSpecs;

    public EquipmentReservationWithMemberNumber(final String renterName, final String memberNumber,
                                                final RentalDateTime acceptDateTime,
                                                final List<ReservationSpec> reservationSpecs) {
        this.renterName = renterName;
        this.memberNumber = memberNumber;
        this.acceptDateTime = acceptDateTime;
        this.reservationSpecs = reservationSpecs;
    }
}
