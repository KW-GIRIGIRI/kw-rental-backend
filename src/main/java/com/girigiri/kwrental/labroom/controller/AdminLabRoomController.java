package com.girigiri.kwrental.labroom.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.girigiri.kwrental.asset.dto.response.RemainQuantitiesPerDateResponse;
import com.girigiri.kwrental.labroom.dto.request.LabRoomNoticeRequest;
import com.girigiri.kwrental.labroom.dto.response.LabRoomNoticeResponse;
import com.girigiri.kwrental.labroom.dto.response.RemainReservationCountsPerDateResponse;
import com.girigiri.kwrental.labroom.service.LabRoomService;

@RestController
@RequestMapping("/api/admin/labRooms")
public class AdminLabRoomController {

	private final LabRoomService labRoomService;

	public AdminLabRoomController(final LabRoomService labRoomService) {
		this.labRoomService = labRoomService;
	}

	@GetMapping("/{name}/remainQuantities")
	public RemainQuantitiesPerDateResponse getRemainQuantities(@PathVariable final String name, final LocalDate from,
		final LocalDate to) {
		return labRoomService.getRemainQuantityByLabRoomName(name, from, to);
	}

	@GetMapping("/{name}/remainReservationCounts")
	public RemainReservationCountsPerDateResponse getRemainReservationCounts(@PathVariable final String name,
		final LocalDate from, final LocalDate to) {
		return labRoomService.getRemainReservationCountByLabRoomName(name, from, to);
	}

	@PutMapping("/{name}/notice")
	public ResponseEntity<?> setNotice(@PathVariable final String name,
		@RequestBody final LabRoomNoticeRequest labRoomNoticeRequest) {
		labRoomService.setNotice(name, labRoomNoticeRequest);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{name}/notice")
	public LabRoomNoticeResponse getNotice(@PathVariable final String name) {
		return labRoomService.getNotice(name);
	}
}
