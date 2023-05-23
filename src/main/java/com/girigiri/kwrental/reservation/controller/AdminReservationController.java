package com.girigiri.kwrental.reservation.controller;

import com.girigiri.kwrental.reservation.dto.request.CancelReservationSpecRequest;
import com.girigiri.kwrental.reservation.dto.request.ReturnLabRoomRequest;
import com.girigiri.kwrental.reservation.dto.response.LabRoomReservationsWithMemberNumberResponse;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

    @PatchMapping("/specs/{reservationSpecId}")
    public ResponseEntity<?> cancelReservationSpec(@PathVariable Long reservationSpecId,
                                                   @Validated @RequestBody final CancelReservationSpecRequest body) {
        final Long cancelReservationSpecId = reservationService.cancelReservationSpec(reservationSpecId, body.getAmount());
        return ResponseEntity.noContent()
                .location(URI.create("/api/reservations/specs/" + cancelReservationSpecId)).build();
    }

    @GetMapping(value = "/labRooms", params = "startDate")
    public LabRoomReservationsWithMemberNumberResponse getLabRoomReservationsForAccept(final LocalDate startDate) {
        return new LabRoomReservationsWithMemberNumberResponse(reservationService.getLabRoomReservationForAccept(startDate));
    }

    @GetMapping(value = "/labRooms", params = "endDate")
    public LabRoomReservationsWithMemberNumberResponse getLabRoomReservationsForReturn(final LocalDate endDate) {
        return new LabRoomReservationsWithMemberNumberResponse(reservationService.getLabRoomReservationForReturn(endDate));
    }

    @PatchMapping("/labRooms/return")
    public ResponseEntity<?> returnLabRoom(@Validated @RequestBody ReturnLabRoomRequest returnLabRoomRequest) {
        reservationService.returnLabRoom(returnLabRoomRequest);
        return ResponseEntity.noContent().build();
    }

}
