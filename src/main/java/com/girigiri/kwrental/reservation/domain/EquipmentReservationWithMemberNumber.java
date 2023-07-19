package com.girigiri.kwrental.reservation.domain;

import java.util.List;

import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EquipmentReservationWithMemberNumber {
    private final Long id;
    private final String renterName;
    private final String memberNumber;
    private final RentalDateTime acceptDateTime;

    private final List<ReservationSpec> reservationSpecs;

    public List<Long> getReservationSpecIds() {
        return this.reservationSpecs.stream()
                .map(ReservationSpec::getId)
                .toList();
    }

    public boolean isAccepted() {
        return this.acceptDateTime != null;
    }
}
