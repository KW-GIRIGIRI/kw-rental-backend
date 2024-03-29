package com.girigiri.kwrental.rental.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.girigiri.kwrental.rental.dto.request.CreateEquipmentRentalRequest;
import com.girigiri.kwrental.rental.dto.request.RestoreEquipmentRentalRequest;
import com.girigiri.kwrental.rental.dto.request.UpdateLabRoomRentalSpecStatusesRequest;
import com.girigiri.kwrental.rental.dto.response.EquipmentRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.LabRoomReservationPageResponse;
import com.girigiri.kwrental.rental.dto.response.LabRoomReservationResponse;
import com.girigiri.kwrental.rental.dto.response.LabRoomReservationsResponse;
import com.girigiri.kwrental.rental.dto.response.equipmentreservationbyenddate.EquipmentReservationsWithRentalSpecsResponse;
import com.girigiri.kwrental.rental.dto.response.equipmentreservationbyenddate.ReservationsWithRentalSpecsByEndDateResponse;
import com.girigiri.kwrental.rental.service.RentalViewService;
import com.girigiri.kwrental.rental.service.rent.RentalRentService;
import com.girigiri.kwrental.rental.service.restore.EquipmentRentalRestoreService;
import com.girigiri.kwrental.rental.service.restore.LabRoomRentalRestoreService;
import com.girigiri.kwrental.reservation.dto.request.CreateLabRoomRentalRequest;
import com.girigiri.kwrental.reservation.dto.request.RestoreLabRoomRentalRequest;
import com.girigiri.kwrental.util.EndPointUtils;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/rentals")
public class AdminRentalController {

	private final RentalViewService rentalViewService;
	private final RentalRentService rentalRentService;
	private final EquipmentRentalRestoreService equipmentRentalRestoreService;
	private final LabRoomRentalRestoreService labRoomRentalRestoreService;
	private final EndPointUtils endPointUtils;

	@GetMapping(params = "startDate")
	public EquipmentReservationsWithRentalSpecsResponse getReservationsWithRentalSpecsByStartDate(
		final LocalDate startDate) {
		return rentalViewService.getReservationsWithRentalSpecsByStartDate(startDate);
	}

	@GetMapping(params = "endDate")
	public ReservationsWithRentalSpecsByEndDateResponse getReservationWithRentalSpecsByEndDate(
		final LocalDate endDate) {
		return rentalViewService.getReservationsWithRentalSpecsByEndDate(endDate);
	}

	@PostMapping
	public ResponseEntity<?> rentEquipment(
		@RequestBody final CreateEquipmentRentalRequest createEquipmentRentalRequest) {
		rentalRentService.rentEquipment(createEquipmentRentalRequest);
		return ResponseEntity
			.created(URI.create("/api/rentals?reservationId=" + createEquipmentRentalRequest.reservationId()))
			.build();
	}

	@PatchMapping("/returns")
	public ResponseEntity<?> returnEquipment(
		@RequestBody final RestoreEquipmentRentalRequest restoreEquipmentRentalRequest) {
		equipmentRentalRestoreService.restore(restoreEquipmentRentalRequest);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(value = "/returns", params = "propertyNumber")
	public EquipmentRentalSpecsResponse getReturnsByPropertyNumber(final String propertyNumber) {
		return rentalViewService.getReturnedRentalSpecs(propertyNumber);
	}

	@GetMapping(value = "/returns", params = {"startDate", "endDate", "propertyNumber"})
	public EquipmentRentalSpecsResponse getReturnsByPropertyNumberInclusive(final String propertyNumber,
		final LocalDate startDate, final LocalDate endDate) {
		return rentalViewService.getReturnedRentalSpecsInclusive(propertyNumber, startDate, endDate);
	}

	@PostMapping("/labRooms")
	public ResponseEntity<?> rentLabRoom(
		@Validated @RequestBody CreateLabRoomRentalRequest createLabRoomRentalRequest) {
		rentalRentService.rentLabRoom(createLabRoomRentalRequest);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/labRooms/returns")
	public ResponseEntity<?> returnLabRoom(
		@Validated @RequestBody RestoreLabRoomRentalRequest restoreLabRoomRentalRequest) {
		labRoomRentalRestoreService.normalRestoreAll(restoreLabRoomRentalRequest);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(value = "/labRooms/{labRoomName}", params = "date")
	public LabRoomReservationsResponse getLabRoomReservations(@PathVariable String labRoomName, final LocalDate date) {
		return rentalViewService.getReturnedLabRoomReservation(labRoomName, date);
	}

	@GetMapping(value = "/labRooms/{labRoomName}/history", params = {"startDate", "endDate"})
	public LabRoomReservationPageResponse getLabRoomHistory(@PathVariable String labRoomName,
		final LocalDate startDate, final LocalDate endDate,
		@PageableDefault(sort = "id", direction = Sort.Direction.DESC) final Pageable pageable) {
		Page<LabRoomReservationResponse> page = rentalViewService.getLabRoomHistory(labRoomName, startDate, endDate,
			pageable);
		List<String> allPageEndPoints = endPointUtils.createAllPageEndPoints(page);
		return new LabRoomReservationPageResponse(page.getContent(), page.getNumber(), allPageEndPoints);
	}

	@PatchMapping("/labRooms/status")
	public ResponseEntity<?> updateLabRoomRentalSpecStatuses(
		@RequestBody @Validated UpdateLabRoomRentalSpecStatusesRequest updateLabRoomRentalSpecStatusesRequest) {
		labRoomRentalRestoreService.updateRentals(updateLabRoomRentalSpecStatusesRequest);
		return ResponseEntity.noContent().build();
	}
}
