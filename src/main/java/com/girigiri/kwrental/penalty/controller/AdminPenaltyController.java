package com.girigiri.kwrental.penalty.controller;

import com.girigiri.kwrental.penalty.dto.request.UpdatePeriodRequest;
import com.girigiri.kwrental.penalty.dto.response.PenaltyHistoryPageResponse;
import com.girigiri.kwrental.penalty.dto.response.PenaltyHistoryResponse;
import com.girigiri.kwrental.penalty.service.PenaltyServiceImpl;
import com.girigiri.kwrental.util.EndPointUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/penalties")
public class AdminPenaltyController {

    private final PenaltyServiceImpl penaltyService;

    public AdminPenaltyController(final PenaltyServiceImpl penaltyService) {
        this.penaltyService = penaltyService;
    }

    @GetMapping("/histories")
    public final PenaltyHistoryPageResponse getPenaltyHistoryPage(@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        final Page<PenaltyHistoryResponse> penaltyHistoryPage = penaltyService.getPenaltyHistoryPage(pageable);
        final List<String> allPageEndPoints = EndPointUtils.createAllPageEndPoints(penaltyHistoryPage);
        return new PenaltyHistoryPageResponse(allPageEndPoints, pageable.getPageNumber(), penaltyHistoryPage.getContent());
    }

    @PatchMapping("/{id}")
    public final ResponseEntity<?> updatePeriod(@PathVariable final Long id, @Validated @RequestBody UpdatePeriodRequest updatePeriodRequest) {
        penaltyService.updatePeriod(id, updatePeriodRequest.getStatus());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public final ResponseEntity<?> delete(@PathVariable final Long id) {
        penaltyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
