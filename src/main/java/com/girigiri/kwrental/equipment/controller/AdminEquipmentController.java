package com.girigiri.kwrental.equipment.controller;

import com.girigiri.kwrental.equipment.dto.request.AddEquipmentWithItemsRequest;
import com.girigiri.kwrental.equipment.dto.request.EquipmentSearchCondition;
import com.girigiri.kwrental.equipment.dto.response.EquipmentPageResponse;
import com.girigiri.kwrental.equipment.dto.response.SimpleEquipmentResponse;
import com.girigiri.kwrental.equipment.exception.EquipmentException;
import com.girigiri.kwrental.equipment.service.EquipmentService;
import com.girigiri.kwrental.util.EndPointUtils;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

        return EquipmentPageResponse.builder()
                .endPoints(allPageEndPoints)
                .page(pageable.getPageNumber())
                .items(page.getContent())
                .build();
    }

    @PostMapping
    public ResponseEntity<?> saveEquipment(
            @RequestBody @Validated final AddEquipmentWithItemsRequest addEquipmentWithItemsRequest) {
        validateTotalQuantity(addEquipmentWithItemsRequest);
        final Long equipmentId = equipmentService.saveEquipment(addEquipmentWithItemsRequest);
        return ResponseEntity.created(URI.create("/api/equipments/" + equipmentId)).build();
    }

    private void validateTotalQuantity(final AddEquipmentWithItemsRequest addEquipmentWithItemsRequest) {
        if (addEquipmentWithItemsRequest.items().size() != addEquipmentWithItemsRequest.equipment()
                .getTotalQuantity()) {
            throw new EquipmentException("품목 갯수와 기자재의 총 갯수가 맞지 않습니다.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }
}
