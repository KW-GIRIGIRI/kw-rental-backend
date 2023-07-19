package com.girigiri.kwrental.reservation.controller;

import java.net.URI;
import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.girigiri.kwrental.reservation.dto.request.CancelReservationSpecRequest;
import com.girigiri.kwrental.reservation.dto.response.HistoryStatResponse;
import com.girigiri.kwrental.reservation.dto.response.LabRoomReservationsWithMemberNumberResponse;
import com.girigiri.kwrental.reservation.dto.response.ReservationPurposeResponse;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.service.ReservationService;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/reservations")
public class AdminReservationController {

	private final ReservationService reservationService;

	@GetMapping
	public ReservationsByEquipmentPerYearMonthResponse getReservationsByEquipmentPerYearMonth(final Long equipmentId,
		final YearMonth yearMonth) {
		return reservationService.getReservationsByEquipmentsPerYearMonth(equipmentId, yearMonth);
	}

	@PatchMapping("/specs/{reservationSpecId}")
	public ResponseEntity<?> cancelReservationSpec(@PathVariable Long reservationSpecId,
		@Validated @RequestBody final CancelReservationSpecRequest body) {
		final Long cancelReservationSpecId = reservationService.cancelReservationSpec(reservationSpecId, body.amount());
		return ResponseEntity.noContent()
			.location(URI.create("/api/reservations/specs/" + cancelReservationSpecId)).build();
	}

	@GetMapping(value = "/labRooms", params = "startDate")
	public LabRoomReservationsWithMemberNumberResponse getLabRoomReservationsForAccept(final LocalDate startDate) {
		return new LabRoomReservationsWithMemberNumberResponse(
			reservationService.getLabRoomReservationForAccept(startDate));
	}

	@GetMapping(value = "/labRooms", params = "endDate")
	public LabRoomReservationsWithMemberNumberResponse getLabRoomReservationsForReturn(final LocalDate endDate) {
		return new LabRoomReservationsWithMemberNumberResponse(
			reservationService.getLabRoomReservationForReturn(endDate));
	}

	@GetMapping(value = "/histories/stat", params = {"name", "startDate", "endDate"})
	public HistoryStatResponse getHistoryStat(final @NotEmpty String name,
		@NotNull final LocalDate startDate,
		@NotNull final LocalDate endDate) {
		return reservationService.getHistoryStat(name, startDate, endDate);
	}

	@GetMapping("/{id}/purpose")
	public ReservationPurposeResponse getPurpose(@PathVariable final Long id) {
		return reservationService.getPurpose(id);
	}
}
