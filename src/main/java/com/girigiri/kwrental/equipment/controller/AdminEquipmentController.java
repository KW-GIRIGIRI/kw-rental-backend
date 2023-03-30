package com.girigiri.kwrental.equipment.controller;

import com.girigiri.kwrental.equipment.dto.request.EquipmentSearchCondition;
import com.girigiri.kwrental.equipment.dto.response.EquipmentPageResponse;
import com.girigiri.kwrental.equipment.dto.response.SimpleEquipmentResponse;
import com.girigiri.kwrental.equipment.service.EquipmentService;
import com.girigiri.kwrental.util.EndPointUtils;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/equipments")
public class AdminEquipmentController {

    private final EquipmentService equipmentService;

    public AdminEquipmentController(final EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public EquipmentPageResponse getEquipments(@Validated EquipmentSearchCondition searchCondition,
                                               @PageableDefault(sort = {"id"}, direction = Direction.DESC)
                                               Pageable pageable) {
        final Page<SimpleEquipmentResponse> page = equipmentService.findEquipments(pageable, searchCondition);

        final List<String> allPageEndPoints = EndPointUtils.createAllPageEndPoints(page);

        page.getContent();
        return EquipmentPageResponse.builder()
                .endPoints(allPageEndPoints)
                .page(pageable.getPageNumber())
                .items(page.getContent())
                .build();
    }
}
