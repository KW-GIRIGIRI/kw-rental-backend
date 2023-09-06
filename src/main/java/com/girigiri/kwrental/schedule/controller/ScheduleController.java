package com.girigiri.kwrental.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.girigiri.kwrental.schedule.dto.request.PutSchedulesRequest;
import com.girigiri.kwrental.schedule.service.ScheduleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/schedules")
public class ScheduleController {
	private final ScheduleService scheduleService;

	@PutMapping
	public ResponseEntity<?> putSchedules(@RequestBody final PutSchedulesRequest putSchedulesRequest) {
		scheduleService.putSchedules(putSchedulesRequest);
		return ResponseEntity.noContent().build();
	}
}
