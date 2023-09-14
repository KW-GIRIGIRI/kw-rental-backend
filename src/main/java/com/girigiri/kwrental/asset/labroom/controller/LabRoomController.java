package com.girigiri.kwrental.asset.labroom.controller;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.girigiri.kwrental.asset.labroom.dto.response.LabRoomAvailableResponse;
import com.girigiri.kwrental.asset.labroom.service.LabRoomAvailableService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/labRooms")
public class LabRoomController {

	private final LabRoomAvailableService labRoomAvailableService;


	@GetMapping("/{name}/available")
	public LabRoomAvailableResponse getLabRoomAvailableByDate(@PathVariable final String name, final LocalDate date) {
		if (date == null) {
			return labRoomAvailableService.getAvailable(name);
		}
		return labRoomAvailableService.getAvailableByDate(name, date);
	}
}
