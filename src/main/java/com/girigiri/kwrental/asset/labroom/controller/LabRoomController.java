package com.girigiri.kwrental.asset.labroom.controller;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.girigiri.kwrental.asset.labroom.dto.response.LabRoomAvailableResponse;
import com.girigiri.kwrental.asset.labroom.service.LabRoomService;

@RestController
@RequestMapping("/api/labRooms")
public class LabRoomController {

	private final LabRoomService labRoomService;

	public LabRoomController(final LabRoomService labRoomService) {
		this.labRoomService = labRoomService;
	}

	@GetMapping("/{name}/available")
	public LabRoomAvailableResponse getLabRoomAvailableByDate(@PathVariable final String name, final LocalDate date) {
		if (date == null) {
			return labRoomService.getAvailable(name);
		}
		return labRoomService.getAvailableByDate(name, date);
	}
}
