package com.girigiri.kwrental.labroom.controller;

import com.girigiri.kwrental.equipment.dto.response.RemainQuantitiesPerDateResponse;
import com.girigiri.kwrental.labroom.service.LabRoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/labRooms")
public class AdminLabRoomController {

    private final LabRoomService labRoomService;

    public AdminLabRoomController(final LabRoomService labRoomService) {
        this.labRoomService = labRoomService;
    }

    @GetMapping("/{name}/remainQuantities")
    public RemainQuantitiesPerDateResponse getRemainQuantities(@PathVariable final String name, final LocalDate from, final LocalDate to) {
        return labRoomService.getRemainQuantityByName(name, from, to);
    }
}
