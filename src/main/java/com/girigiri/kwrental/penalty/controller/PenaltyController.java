package com.girigiri.kwrental.penalty.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.girigiri.kwrental.auth.argumentresolver.Login;
import com.girigiri.kwrental.auth.domain.SessionMember;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltiesResponse;
import com.girigiri.kwrental.penalty.dto.response.UserPenaltyStatusResponse;
import com.girigiri.kwrental.penalty.service.PenaltyServiceImpl;

@RestController
@RequestMapping("/api/penalties")
public class PenaltyController {

    private final PenaltyServiceImpl penaltyService;

    public PenaltyController(final PenaltyServiceImpl penaltyService) {
        this.penaltyService = penaltyService;
    }

    @GetMapping
    public UserPenaltiesResponse getUserPenaltiesResponse(@Login final SessionMember sessionMember) {
        return penaltyService.getPenalties(sessionMember.getId());
    }

    @GetMapping("/status")
    public UserPenaltyStatusResponse getUserPenaltyStatus(@Login final SessionMember sessionMember) {
        return penaltyService.getPenaltyStatus(sessionMember.getId());
    }
}
