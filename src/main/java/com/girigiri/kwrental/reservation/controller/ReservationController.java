package com.girigiri.kwrental.reservation.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.girigiri.kwrental.auth.argumentresolver.Login;
import com.girigiri.kwrental.auth.domain.SessionMember;
import com.girigiri.kwrental.common.exception.BadRequestException;
import com.girigiri.kwrental.reservation.dto.request.AddEquipmentReservationRequest;
import com.girigiri.kwrental.reservation.dto.request.AddLabRoomReservationRequest;
import com.girigiri.kwrental.reservation.dto.response.RelatedReservationsInfoResponse;
import com.girigiri.kwrental.reservation.dto.response.UnterminatedEquipmentReservationsResponse;
import com.girigiri.kwrental.reservation.dto.response.UnterminatedLabRoomReservationsResponse;
import com.girigiri.kwrental.reservation.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

	private final ReservationService reservationService;

	@PostMapping
	public ResponseEntity<?> reserve(@Login final SessionMember sessionMember,
		@RequestBody final AddEquipmentReservationRequest addReservationRequest) {
		reservationService.reserveEquipment(sessionMember.getId(), addReservationRequest);
		return ResponseEntity.created(URI.create("/api/reservations")).build();
	}

	@PostMapping("/labRooms")
	public ResponseEntity<?> reserveLabRoom(@Login final SessionMember sessionMember,
		@RequestBody final AddLabRoomReservationRequest addLabRoomReservationRequest) {
		final Long id = reservationService.reserveLabRoom(sessionMember.getId(), addLabRoomReservationRequest);
		return ResponseEntity.created(URI.create("/api/reservations/" + id)).build();
	}

	@GetMapping(value = "/{id}", params = "related")
	public RelatedReservationsInfoResponse getRelatedReservationsInfo(@PathVariable final Long id,
		final boolean related) {
		if (related) {
			return reservationService.getRelatedReservationsInfo(id);
		}
		throw new BadRequestException("related가 false인 경우는 제공하지 않습니다.");
	}

	@GetMapping(params = "terminated")
	public UnterminatedEquipmentReservationsResponse findUnterminatedEquipmentReservations(
		@Login final SessionMember sessionMember, final Boolean terminated) {
		if (!terminated)
			return reservationService.getUnterminatedEquipmentReservations(sessionMember.getId());
		throw new BadRequestException("terminated가 true인 경우는 제공하지 않습니다.");
	}

	@GetMapping(path = "/labRooms", params = "terminated")
	public UnterminatedLabRoomReservationsResponse findUnterminatedLabRoomReservations(
		@Login final SessionMember sessionMember, final Boolean terminated) {
		if (!terminated)
			return reservationService.getUnterminatedLabRoomReservations(sessionMember.getId());
		throw new BadRequestException("terminated가 true인 경우는 제공하지 않습니다.");
	}
}
