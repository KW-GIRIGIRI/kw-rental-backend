package com.girigiri.kwrental.equipment.controller;

import com.girigiri.kwrental.equipment.dto.EquipmentResponse;
import com.girigiri.kwrental.equipment.service.EquipmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/equipments")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(final EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping("/{id}")
    public EquipmentResponse getEquipment(@PathVariable final Long id) {
      return equipmentService.findById(id);
    }
}
