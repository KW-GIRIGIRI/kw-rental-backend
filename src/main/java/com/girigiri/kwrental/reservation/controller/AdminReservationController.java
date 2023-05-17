package com.girigiri.kwrental.reservation.controller;

import com.girigiri.kwrental.reservation.dto.request.CancelReservationSpecRequest;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

    @PatchMapping("/specs/{reservationSpecId}")
    public ResponseEntity<?> cancelReservationSpec(@PathVariable Long reservationSpecId,
                                                   @Validated @RequestBody final CancelReservationSpecRequest body) {
        final Long cancelReservationSpecId = reservationService.cancelReservationSpec(reservationSpecId, body.getAmount());
        return ResponseEntity.noContent()
                .location(URI.create("/api/reservations/specs/" + cancelReservationSpecId)).build();
    }
}
