package com.girigiri.kwrental.reservation.controller;

import com.girigiri.kwrental.auth.domain.SessionMember;
import com.girigiri.kwrental.auth.interceptor.UserMember;
import com.girigiri.kwrental.common.exception.BadRequestException;
import com.girigiri.kwrental.reservation.dto.request.AddLabRoomReservationRequest;
import com.girigiri.kwrental.reservation.dto.request.AddReservationRequest;
import com.girigiri.kwrental.reservation.dto.response.UnterminatedEquipmentReservationsResponse;
import com.girigiri.kwrental.reservation.dto.response.UnterminatedLabRoomReservationsResponse;
import com.girigiri.kwrental.reservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    private ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<?> reserve(@UserMember final SessionMember sessionMember, @RequestBody final AddReservationRequest addReservationRequest) {
        final Long id = reservationService.reserve(sessionMember.getId(), addReservationRequest);
        return ResponseEntity.created(URI.create("/api/reservations/" + id)).build();
    }

    @PostMapping("/labRooms")
    public ResponseEntity<?> reserveLabRoom(@UserMember final SessionMember sessionMember,
                                            @RequestBody final AddLabRoomReservationRequest addLabRoomReservationRequest) {
        final Long id = reservationService.reserve(sessionMember.getId(), addLabRoomReservationRequest);
        return ResponseEntity.created(URI.create("/api/reservations/" + id)).build();
    }

    @GetMapping(params = "terminated")
    public UnterminatedEquipmentReservationsResponse findUnterminatedEquipmentReservations(@UserMember final SessionMember sessionMember, final Boolean terminated) {
        if (!terminated) return reservationService.getUnterminatedEquipmentReservations(sessionMember.getId());
        throw new BadRequestException("terminated가 true인 경우는 제공하지 않습니다.");
    }

    @GetMapping(path = "/labRooms", params = "terminated")
    public UnterminatedLabRoomReservationsResponse findUnterminatedLabRoomReservations(@UserMember final SessionMember sessionMember, final Boolean terminated) {
        if (!terminated) return reservationService.getUnterminatedLabRoomReservations(sessionMember.getId());
        throw new BadRequestException("terminated가 true인 경우는 제공하지 않습니다.");
    }
}
