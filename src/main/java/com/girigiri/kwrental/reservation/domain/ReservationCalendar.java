package com.girigiri.kwrental.reservation.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

@Getter
public class ReservationCalendar {

    private final Map<Integer, List<String>> calendar;

    private ReservationCalendar(final Map<Integer, List<String>> calendar) {
        this.calendar = calendar;
    }

    public static ReservationCalendar from(final LocalDate start, final LocalDate end) {
        return new ReservationCalendar(initCalendar(start, end));
    }

    private static Map<Integer, List<String>> initCalendar(final LocalDate startOfMonth, final LocalDate endOfMonth) {
        Map<Integer, List<String>> calendar = new HashMap<>();
        for (int i = startOfMonth.getDayOfMonth(); i <= endOfMonth.getDayOfMonth(); i++) {
            calendar.put(i, new ArrayList<>());
        }
        return calendar;
    }

    public void addAll(final List<ReservationSpec> reservationSpecs) {
        for (ReservationSpec reservationSpec : reservationSpecs) {
            add(reservationSpec);
        }
    }

    private void add(final ReservationSpec reservationSpec) {
        final int dayOfMonth = reservationSpec.getStartDate().getDayOfMonth();
        for (int i = 0; i < reservationSpec.getAmount().getAmount(); i++) {
            calendar.get(dayOfMonth).add(reservationSpec.getReservation().getName());
        }
    }
}
