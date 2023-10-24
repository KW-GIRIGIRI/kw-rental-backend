package com.girigiri.kwrental.penalty.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.girigiri.kwrental.penalty.dto.request.UpdatePeriodRequest;
import com.girigiri.kwrental.penalty.dto.response.PenaltyHistoryPageResponse;
import com.girigiri.kwrental.penalty.dto.response.PenaltyHistoryPageResponse.PenaltyHistoryResponse;
import com.girigiri.kwrental.penalty.service.PenaltyServiceImpl;
import com.girigiri.kwrental.util.EndPointUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/penalties")
public class AdminPenaltyController {

	private final PenaltyServiceImpl penaltyService;
	private final EndPointUtils endPointUtils;


	@GetMapping("/histories")
	public final PenaltyHistoryPageResponse getPenaltyHistoryPage(
		@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
		final Page<PenaltyHistoryResponse> penaltyHistoryPage = penaltyService.getPenaltyHistoryPage(pageable);
		final List<String> allPageEndPoints = endPointUtils.createAllPageEndPoints(penaltyHistoryPage);
		return new PenaltyHistoryPageResponse(allPageEndPoints, pageable.getPageNumber(),
			penaltyHistoryPage.getContent());
	}

	@PatchMapping("/{id}")
	public final ResponseEntity<?> updatePeriod(@PathVariable final Long id,
		@Validated @RequestBody UpdatePeriodRequest updatePeriodRequest) {
		penaltyService.updatePeriod(id, updatePeriodRequest.status());
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public final ResponseEntity<?> delete(@PathVariable final Long id) {
		penaltyService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
