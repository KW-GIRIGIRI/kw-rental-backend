package com.girigiri.kwrental.equipment.controller;

import com.girigiri.kwrental.equipment.dto.request.EquipmentSearchCondition;
import com.girigiri.kwrental.equipment.dto.response.EquipmentDetailResponse;
import com.girigiri.kwrental.equipment.dto.response.EquipmentsWithRentalQuantityPageResponse;
import com.girigiri.kwrental.equipment.dto.response.SimpleEquipmentWithRentalQuantityResponse;
import com.girigiri.kwrental.equipment.service.EquipmentService;
import com.girigiri.kwrental.util.EndPointUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/equipments")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(final EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping("/{id}")
    public EquipmentDetailResponse getEquipment(@PathVariable final Long id) {
        return equipmentService.findById(id);
    }

    @GetMapping
    public EquipmentsWithRentalQuantityPageResponse getEquipmentsPage(
            @Validated EquipmentSearchCondition searchCondition,
            @PageableDefault(sort = {"id"}, direction = Direction.DESC) Pageable pageable) {
        final Page<SimpleEquipmentWithRentalQuantityResponse> page = equipmentService.findEquipmentsWithRentalQuantityBy(
                pageable,
                searchCondition);

        final List<String> allPageEndPoints = EndPointUtils.createAllPageEndPoints(page);

        return EquipmentsWithRentalQuantityPageResponse.builder()
                .endPoints(allPageEndPoints)
                .page(pageable.getPageNumber())
                .items(page.getContent())
                .build();
    }
}
