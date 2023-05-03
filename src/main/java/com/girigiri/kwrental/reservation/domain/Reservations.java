package com.girigiri.kwrental.reservation.domain;

import java.util.List;

public class Reservations {

    private final List<Reservation> reservations;

    public Reservations(final List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<Long> getAcceptedReservationSpecIds() {
        return reservations.stream()
                .filter(Reservation::isAccepted)
                .map(Reservation::getReservationSpecs)
                .flatMap(List::stream)
                .map(ReservationSpec::getId)
                .toList();
    }
}
