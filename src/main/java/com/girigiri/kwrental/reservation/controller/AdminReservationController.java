package com.girigiri.kwrental.reservation.controller;

import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByStartDateResponse;
import com.girigiri.kwrental.reservation.service.ReservationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ReservationsByEquipmentPerYearMonthResponse getReservationsByEquipmentPerYearMonth(final Long equipmentId, final YearMonth yearMonth) {
        return reservationService.getReservationsByEquipmentsPerYearMonth(equipmentId, yearMonth);
    }

    @GetMapping(params = "startDate")
    public ReservationsByStartDateResponse getReservationsByStartDate(final LocalDate startDate) {
        return reservationService.getReservationsByStartDate(startDate);
    }
}
