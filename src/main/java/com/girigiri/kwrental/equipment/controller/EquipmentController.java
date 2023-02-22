package com.girigiri.kwrental.equipment.controller;

import com.girigiri.kwrental.equipment.dto.EquipmentDetailResponse;
import com.girigiri.kwrental.equipment.dto.EquipmentResponse;
import com.girigiri.kwrental.equipment.dto.EquipmentsPageResponse;
import com.girigiri.kwrental.equipment.service.EquipmentService;
import com.girigiri.kwrental.util.LinkUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

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
    public EquipmentsPageResponse getEquipmentsPage(
            @PageableDefault(sort = {"id"}, direction = Direction.DESC) Pageable pageable) {

        final UriComponentsBuilder builder = MvcUriComponentsBuilder.fromMethodName(
                EquipmentController.class, "getEquipmentsPage", pageable);

        final Slice<EquipmentResponse> equipments = equipmentService.findEquipmentsBy(pageable);

        String nextLink = LinkUtils.createIfNextExists(equipments, builder);
        String previousLink = LinkUtils.createIfPreviousExists(equipments, builder);
        return EquipmentsPageResponse.builder()
                .nextLink(nextLink).previousLink(previousLink)
                .page(pageable.getPageNumber())
                .items(equipments.getContent())
                .build();
    }
}
