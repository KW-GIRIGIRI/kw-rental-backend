package com.girigiri.kwrental.asset.labroom.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.girigiri.kwrental.asset.dto.response.RemainQuantitiesPerDateResponse;
import com.girigiri.kwrental.asset.labroom.dto.request.LabRoomAvailableRequest;
import com.girigiri.kwrental.asset.labroom.dto.request.LabRoomNoticeRequest;
import com.girigiri.kwrental.asset.labroom.dto.response.LabRoomNoticeResponse;
import com.girigiri.kwrental.asset.labroom.dto.response.RemainReservationCountsPerDateResponse;
import com.girigiri.kwrental.asset.labroom.service.LabRoomRemainQuantityService;
import com.girigiri.kwrental.asset.labroom.service.LabRoomService;
import com.girigiri.kwrental.common.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/labRooms")
public class AdminLabRoomController {

	private final LabRoomService labRoomService;
	private final LabRoomRemainQuantityService labRoomRemainQuantityService;


	@GetMapping("/{name}/remainQuantities")
	public RemainQuantitiesPerDateResponse getRemainQuantities(@PathVariable final String name, final LocalDate from,
		final LocalDate to) {
		return labRoomRemainQuantityService.getRemainQuantityByLabRoomName(name, from, to);
	}

	@GetMapping("/{name}/remainReservationCounts")
	public RemainReservationCountsPerDateResponse getRemainReservationCounts(@PathVariable final String name,
		final LocalDate from, final LocalDate to) {
		return labRoomRemainQuantityService.getRemainReservationCountByLabRoomName(name, from, to);
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

	@PatchMapping("/{name}/available")
	public ResponseEntity<?> setAvailable(@PathVariable final String name,
		@RequestBody final LabRoomAvailableRequest labRoomAvailableRequest) {
		if (labRoomAvailableRequest.isEntirePeriod() && labRoomAvailableRequest.getDate() == null) {
			labRoomService.setAvailableForEntirePeriod(name, labRoomAvailableRequest.isAvailable());
			return ResponseEntity.noContent().build();
		}
		if (!labRoomAvailableRequest.isEntirePeriod() && labRoomAvailableRequest.getDate() != null) {
			labRoomService.setAvailable(name, labRoomAvailableRequest.getDate(), labRoomAvailableRequest.isAvailable());
			return ResponseEntity.noContent().build();
		}
		throw new BadRequestException("랩실 운영 상태 변경 요청 데이터의 조건이 잘못됐습니다.");
	}
}
