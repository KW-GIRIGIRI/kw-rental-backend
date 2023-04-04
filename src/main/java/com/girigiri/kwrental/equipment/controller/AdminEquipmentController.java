package com.girigiri.kwrental.equipment.controller;

import com.girigiri.kwrental.common.MultiPartFileHandler;
import com.girigiri.kwrental.equipment.dto.request.AddEquipmentWithItemsRequest;
import com.girigiri.kwrental.equipment.dto.request.EquipmentSearchCondition;
import com.girigiri.kwrental.equipment.dto.request.UpdateEquipmentRequest;
import com.girigiri.kwrental.equipment.dto.response.EquipmentDetailResponse;
import com.girigiri.kwrental.equipment.dto.response.EquipmentPageResponse;
import com.girigiri.kwrental.equipment.dto.response.SimpleEquipmentResponse;
import com.girigiri.kwrental.equipment.exception.EquipmentException;
import com.girigiri.kwrental.equipment.service.EquipmentService;
import com.girigiri.kwrental.util.EndPointUtils;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/admin/equipments")
public class AdminEquipmentController {

    private final EquipmentService equipmentService;

    private final MultiPartFileHandler multiPartFileHandler;

    public AdminEquipmentController(final EquipmentService equipmentService, MultiPartFileHandler multiPartFileHandler) {
        this.equipmentService = equipmentService;
        this.multiPartFileHandler = multiPartFileHandler;
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
    public ResponseEntity<?> deleteEquipment(@PathVariable @Positive Long id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/images")
    public ResponseEntity<?> uploadImage(@RequestPart("file") final MultipartFile multipartFile) throws IOException, URISyntaxException {
        URL url = multiPartFileHandler.upload(multipartFile);
        return ResponseEntity.noContent()
                .location(url.toURI())
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable final Long id, @Validated @RequestBody final UpdateEquipmentRequest updateEquipmentRequest) {
        EquipmentDetailResponse updatedResponse = equipmentService.update(id, updateEquipmentRequest);
        return ResponseEntity.noContent()
                .location(URI.create("/api/equipments/" + updatedResponse.id()))
                .build();
    }
}
