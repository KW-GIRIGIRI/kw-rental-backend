package com.girigiri.kwrental.reservation.domain;

import com.girigiri.kwrental.inventory.domain.RentalDateTime;
import lombok.Getter;

import java.util.List;

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

    public static EquipmentReservationWithMemberNumber of(final Reservation reservation, final List<ReservationSpec> specs, final String memberNumber) {
        return new EquipmentReservationWithMemberNumber(reservation.getId(), reservation.getName(), memberNumber, reservation.getAcceptDateTime(), specs);
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
