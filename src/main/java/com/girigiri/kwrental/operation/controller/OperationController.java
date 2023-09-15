package com.girigiri.kwrental.operation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.girigiri.kwrental.operation.dto.request.PutEntireOperationRequest;
import com.girigiri.kwrental.operation.dto.request.PutSchedulesRequest;
import com.girigiri.kwrental.operation.dto.response.EntireOperationResponse;
import com.girigiri.kwrental.operation.dto.response.SchedulesResponse;
import com.girigiri.kwrental.operation.service.EntireOperationService;
import com.girigiri.kwrental.operation.service.ScheduleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/operations")
public class OperationController {
	private final ScheduleService scheduleService;
	private final EntireOperationService entireOperationService;

	@GetMapping("/schedules")
	public SchedulesResponse getSchedules() {
		return scheduleService.getSchedules();
	}

	@PutMapping("/schedules")
	public ResponseEntity<?> putSchedules(@RequestBody final PutSchedulesRequest putSchedulesRequest) {
		scheduleService.putSchedules(putSchedulesRequest);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public EntireOperationResponse getOperation() {
		return entireOperationService.getEntireOperation();
	}

	@PutMapping
	public ResponseEntity<?> putEntireOperation(
		@RequestBody final PutEntireOperationRequest putEntireOperationRequest) {
		entireOperationService.putEntireOperation(putEntireOperationRequest.isRunning());
		return ResponseEntity.noContent().build();
	}

}
